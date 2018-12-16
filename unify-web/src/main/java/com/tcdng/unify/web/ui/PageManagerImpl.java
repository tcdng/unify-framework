/*
 * Copyright 2018 The Code Department
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.web.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.LocaleFactoryMaps;
import com.tcdng.unify.core.system.SequenceNumberBusinessModule;
import com.tcdng.unify.core.upl.UplCompiler;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplDocumentAttributes;
import com.tcdng.unify.core.upl.UplElementAttributes;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.UnifyWebPropertyConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.ui.panel.StandalonePanel;

/**
 * Default implementation of page manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_PAGEMANAGER)
public class PageManagerImpl extends AbstractUnifyComponent implements PageManager {

    private static final String PAGENAME_PREFIX = "p";

    private static final String REMOTE_WEBVIEWER_PAGENAME_PREFIX = "rp";

    @Configurable(ApplicationComponents.APPLICATION_UPLCOMPILER)
    private UplCompiler uplCompiler;

    @Configurable(ApplicationComponents.APPLICATION_SEQUENCENUMBERBUSINESSMODULE)
    private SequenceNumberBusinessModule sequenceNumberBusinessModule;

    private PageNameMap pageNameMap;

    private LocaleFactoryMaps<String, StandalonePanelInfo> standalonePanelInfoByNameMap;

    // Page name property bindings by standalone panel
    private FactoryMap<String, Map<String, BindingInfo>> pageNamePropertyBindings;

    // Expanded component references by component page name
    private Map<String, List<String>> expandedReferences;

    // Expanded value references by component page name
    private Map<String, List<String>> valueReferences;

    private List<String> documentStyleSheets;

    private List<String> documentScripts;

    private String pageNamePrefix;

    public PageManagerImpl() {
        expandedReferences = new HashMap<String, List<String>>();
        valueReferences = new HashMap<String, List<String>>();
        documentStyleSheets = Collections.emptyList();
        documentScripts = Collections.emptyList();
        pageNamePrefix = PAGENAME_PREFIX;

        pageNameMap = new PageNameMap();

        standalonePanelInfoByNameMap = new LocaleFactoryMaps<String, StandalonePanelInfo>() {

            @SuppressWarnings("unchecked")
            @Override
            protected StandalonePanelInfo createObject(Locale locale, String name, Object... params) throws Exception {
                UplDocumentAttributes uplDocumentAttributes = null;
                if (params.length > 0) {
                    uplDocumentAttributes = (UplDocumentAttributes) params[0];
                } else {
                    uplDocumentAttributes = uplCompiler.compileComponentDocuments(locale, name);
                }

                expandReferences(uplDocumentAttributes, uplDocumentAttributes);

                // Sort reusable and non-reusable elements and process
                // reusable ones.
                Set<String> nonreusableComponentLongNames = new HashSet<String>();
                Map<String, PageValidation> reusablePageValidations = new LinkedHashMap<String, PageValidation>();
                Map<String, PageAction> reusablePageActions = new LinkedHashMap<String, PageAction>();
                for (String longName : uplDocumentAttributes.getLongNames()) {
                    UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName(longName);
                    Class<? extends UplComponent> type = (Class<? extends UplComponent>) getComponentType(
                            uea.getComponentName());

                    // Only page action and page validation components are
                    // reusable
                    boolean isPageAction = false;
                    if ((isPageAction = PageAction.class.isAssignableFrom(type))
                            || PageValidation.class.isAssignableFrom(type)) {
                        UplComponent uplComponent = getUplComponent(locale, uea.getKey());
                        String pageName = getPageName(longName);

                        if (isPageAction) {
                            PageAction pageAction = (PageAction) uplComponent;
                            pageAction.setId(pageName);
                            expandReferences(uplDocumentAttributes, uea);

                            if (pageAction.isUplAttribute("valueComponentList")) {
                                if (!valueReferences.containsKey(pageName)) {
                                    UplElementReferences uer = uea.getAttributeValue(UplElementReferences.class,
                                            "valueComponentList");
                                    if (uer != null) {
                                        valueReferences.put(pageName, Collections.unmodifiableList(
                                                StringUtils.removeDuplicates(getPageNames(uer.getLongNames()))));
                                    }
                                }
                            }

                            reusablePageActions.put(longName, pageAction);
                        } else {
                            PageValidation pageValidation = (PageValidation) uplComponent;
                            pageValidation.setId(pageName);
                            expandReferences(uplDocumentAttributes, uea);
                            reusablePageValidations.put(longName, pageValidation);
                        }
                    } else {
                        nonreusableComponentLongNames.add(longName);
                    }
                }

                // Validate page action validations
                for (Map.Entry<String, PageAction> entry : reusablePageActions.entrySet()) {
                    PageAction pageAction = entry.getValue();
                    if (pageAction.isUplAttribute("validations")) {
                        UplElementReferences uer = pageAction.getUplAttribute(UplElementReferences.class,
                                "validations");
                        if (uer != null) {
                            for (String validationLongName : uer.getLongNames()) {
                                if (reusablePageValidations.get(validationLongName) == null) {
                                    throw new UnifyException(
                                            UnifyWebErrorConstants.PAGEACTION_REFERS_UNKNOWN_PAGEVALIDATION,
                                            entry.getKey(), validationLongName);
                                }
                            }
                        }
                    }
                }

                // Process event handlers and create widget info for non
                // reusable components
                Map<String, WidgetNameInfo> widgetNameInfos = new HashMap<String, WidgetNameInfo>();
                for (String longName : nonreusableComponentLongNames) {
                    UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName(longName);
                    // Widget info
                    widgetNameInfos.put(longName, createWidgetInfo(uea, nonreusableComponentLongNames));

                    // Go through event handler attributes
                    for (String attribute : uea.getAttributeNames()) {
                        Object value = uea.getAttributeValue(Object.class, attribute);
                        if (value instanceof EventHandler[]) {
                            EventHandler[] eventHandlers = (EventHandler[]) value;
                            for (EventHandler eventHandler : eventHandlers) {
                                UplElementReferences uer = eventHandler.getUplAttribute(UplElementReferences.class,
                                        "action");
                                if (uer != null) {
                                    List<PageAction> pageActionList = new ArrayList<PageAction>();
                                    for (String actionLongName : uer.getLongNames()) {
                                        PageAction pageAction = reusablePageActions.get(actionLongName);
                                        if (pageAction == null) {
                                            throw new UnifyException(
                                                    UnifyWebErrorConstants.EVENTHANDLER_REFERENCING_UNKNOWN_ACTION,
                                                    longName, actionLongName);
                                        }

                                        pageActionList.add(pageAction);
                                    }
                                    eventHandler.setPageAction(
                                            pageActionList.toArray(new PageAction[pageActionList.size()]));
                                }
                            }
                        }
                    }
                }

                widgetNameInfos.put(uplDocumentAttributes.getLongName(),
                        createWidgetInfo(uplDocumentAttributes, nonreusableComponentLongNames));
                StandalonePanelInfo standalonePanelInfo = new StandalonePanelInfo(
                        Collections.unmodifiableMap(widgetNameInfos),
                        Collections.unmodifiableMap(reusablePageValidations),
                        Collections.unmodifiableMap(reusablePageActions));
                return standalonePanelInfo;
            }
        };

        pageNamePropertyBindings = new FactoryMap<String, Map<String, BindingInfo>>() {

            @Override
            protected Map<String, BindingInfo> create(String name, Object... params) throws Exception {
                Map<String, BindingInfo> propertyBindingMap = new HashMap<String, BindingInfo>();
                StandalonePanel sp = createStandalonePanel(Locale.getDefault(), name);
                for (String longName : sp.getWidgetLongNames()) {
                    Widget widget = sp.getWidgetByLongName(longName);
                    String property = widget.getBinding();
                    if (property != null) {
                        String id = widget.getId();
                        boolean masked = widget.isMasked();

                        // Compute long property
                        StringBuilder longSb = new StringBuilder();
                        longSb.append(property);
                        Widget container = null;
                        while ((container = widget.getContainer()) != null) {
                            String containerProperty = container.getBinding();
                            if (StringUtils.isBlank(containerProperty)) {
                                break;
                            }

                            longSb.insert(0, '.');
                            longSb.insert(0, containerProperty);
                            widget = container;
                        }

                        String shortProperty = property;
                        int index = property.lastIndexOf('.');
                        if (index >= 0) {
                            shortProperty = property.substring(index + 1);
                        }
                        propertyBindingMap.put(id, new BindingInfo(property, shortProperty, longSb.toString(), masked));
                    }
                }
                return propertyBindingMap;
            }
        };

    }

    @Override
    public List<String> getDocumentStyleSheets() throws UnifyException {
        return documentStyleSheets;
    }

    @Override
    public List<String> getDocumentsScripts() throws UnifyException {
        return documentScripts;
    }

    @Override
    public Page createPage(Locale locale, String name) throws UnifyException {
        return (Page) createStandalonePanel(locale, name);
    }

    @Override
    public boolean invalidateStaleDocument(String name) throws UnifyException {
        if (uplCompiler.invalidateStaleDocument(name)) {
            Set<StandalonePanelInfo> infoList = standalonePanelInfoByNameMap.removeSubkeys(name);
            for (StandalonePanelInfo standalonePanelInfo : infoList) {
                standalonePanelInfo.invalidate();
            }

            pageNamePropertyBindings.remove(name);
            return true;
        }
        return false;
    }

    @Override
    public StandalonePanel createStandalonePanel(Locale locale, String name) throws UnifyException {
        UplDocumentAttributes uplDocumentAttributes = uplCompiler.compileComponentDocuments(locale, name);
        String spLongName = uplDocumentAttributes.getLongName();
        String id = getPageName(spLongName);

        StandalonePanelContext ctx = new StandalonePanelContext(
                standalonePanelInfoByNameMap.get(locale, name, uplDocumentAttributes));
        StandalonePanelInfo standalonePanelInfo = ctx.getStandalonePanelInfo();
        StandalonePanel standalonePanel = (StandalonePanel) getUplComponent(locale, uplDocumentAttributes.getKey());
        standalonePanel.setId(id);
        standalonePanel.setStandalonePanelInfo(standalonePanelInfo);

        // Create non-reusable components
        for (String longName : standalonePanelInfo.getWidgetLongNames()) {
            if (!longName.equals(spLongName)) {
                UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName(longName);
                // All non reusable components must be widgets
                Widget widget = (Widget) getUplComponent(locale, uea.getKey());
                widget.setId(getPageName(longName));
                ctx.addWidget(widget);
            }
        }

        // Wire widgets
        for (String longName : ctx.getWidgetLongNames()) {
            wireWidget(ctx, ctx.getWidget(longName));
        }

        wireWidget(ctx, standalonePanel);

        // Set container for non-referenced components
        for (String longName : ctx.getWidgetLongNames()) {
            Widget widget = ctx.getWidget(longName);
            if (widget.getContainer() == null) {
                Container parentContainer = (Container) ctx.getWidget(widget.getParentLongName());
                if (parentContainer != null) {
                    widget.setContainer(parentContainer);
                } else {
                    widget.setContainer(standalonePanel);
                }
                ctx.addRepositoryWidget(widget);
            }
        }

        // Perform page initialization
        for (Widget widget : ctx.getWidgets()) {
            widget.onPageInitialize();
        }
        standalonePanel.onPageInitialize();

        return standalonePanel;
    }

    @Override
    public Map<String, BindingInfo> getStandalonePanelPropertyBindings(String name) throws UnifyException {
        standalonePanelInfoByNameMap.get(Locale.getDefault(), name);
        return pageNamePropertyBindings.get(name);
    }

    @Override
    public String getPageName(String longName) throws UnifyException {
        return pageNameMap.get(longName);
    }

    @Override
    public List<String> getPageNames(Collection<String> longNames) throws UnifyException {
        List<String> pageNameList = new ArrayList<String>();
        if (longNames != null) {
            for (String longName : longNames) {
                pageNameList.add(pageNameMap.get(longName));
            }
        }
        return pageNameList;
    }

    @Override
    public String getLongName(String pageName) throws UnifyException {
        return pageNameMap.getLongName(pageName);
    }

    @Override
    public List<String> getLongNames(Collection<String> pageNames) throws UnifyException {
        List<String> longNames = new ArrayList<String>();
        for (String pageName : pageNames) {
            longNames.add(pageNameMap.getLongName(pageName));
        }
        return longNames;
    }

    @Override
    public List<String> getExpandedReferences(String pageName) throws UnifyException {
        return expandedReferences.get(pageName);
    }

    @Override
    public List<String> getValueReferences(String pageName) throws UnifyException {
        return valueReferences.get(pageName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitialize() throws UnifyException {
        if (getContainerSetting(boolean.class, UnifyWebPropertyConstants.APPLICATION_REMOTE_VIEWING_ENABLED, false)) {
            pageNamePrefix = REMOTE_WEBVIEWER_PAGENAME_PREFIX;
        }

        List<String> styleSheets = DataUtils.convert(ArrayList.class, String.class,
                getContainerSetting(Object.class, UnifyWebPropertyConstants.APPLICATION_DOCUMENT_STYLESHEET), null);
        if (styleSheets != null) {
            List<String> actStyleSheets = new ArrayList<String>();
            for (String styleSheet : styleSheets) {
                actStyleSheets.add(styleSheet);
            }

            documentStyleSheets = Collections.unmodifiableList(actStyleSheets);
        }

        List<String> scripts = DataUtils.convert(ArrayList.class, String.class,
                getContainerSetting(Object.class, UnifyWebPropertyConstants.APPLICATION_DOCUMENT_SCRIPT), null);
        if (scripts != null) {
            documentScripts = Collections.unmodifiableList(scripts);
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private void wireWidget(StandalonePanelContext ctx, Widget widget) throws UnifyException {
        if (widget instanceof Container) {
            setContainerWidgetRepository(ctx, (Container) widget);
        } else if (widget instanceof MultiControl) {
            MultiControl multiControl = (MultiControl) widget;
            for (String longName : multiControl.getShallowReferencedLongNames("components")) {
                Widget cWidget = ctx.getWidget(longName);
                if (cWidget instanceof Container) {
                    setContainerWidgetRepository(ctx, (Container) cWidget);
                }
                multiControl.addChildControl((Control) cWidget);
            }
        }
    }

    private void setContainerWidgetRepository(StandalonePanelContext ctx, Container container) throws UnifyException {
        if (!container.hasWidgetRepository()) {
            container.setWidgetRepository(ctx.getWidgetRepository());
            for (String longName : container.getShallowReferencedLongNames("components")) {
                Widget refWidget = ctx.getWidget(longName);
                refWidget.setContainer(container);
                ctx.addRepositoryWidget(refWidget);
            }
        }
    }

    private WidgetNameInfo createWidgetInfo(UplElementAttributes uea, Set<String> widgetLongNames)
            throws UnifyException {
        // Widget info
        Set<String> shallowNames = new HashSet<String>();
        for (String shallowName : uea.getShallowReferencedLongNames()) {
            if (widgetLongNames.contains(shallowName)) {
                shallowNames.add(shallowName);
            }
        }

        if (uea.isAttribute("section")) {
            Section[] sections = uea.getAttributeValue(Section[].class, "section");
            for (Section section : sections) {
                UplElementReferences uplRef = section.getUplAttribute(UplElementReferences.class, "components");
                for (String shallowName : uplRef.getLongNames()) {
                    if (widgetLongNames.contains(shallowName)) {
                        shallowNames.add(shallowName);
                    }
                }
            }
        }

        Set<String> deepNames = new HashSet<String>();
        for (String deepName : uea.getDeepReferencedLongNames()) {
            if (widgetLongNames.contains(deepName)) {
                deepNames.add(deepName);
            }
        }
        return new WidgetNameInfo(Collections.unmodifiableList(new ArrayList<String>(shallowNames)),
                Collections.unmodifiableSet(deepNames));
    }

    private void expandReferences(UplDocumentAttributes uplDocumentAttributes, UplElementAttributes uea)
            throws UnifyException {
        String pageName = getPageName(uea.getLongName());
        if (!expandedReferences.containsKey(pageName)) {
            List<String> expandedList = new ArrayList<String>();
            expandReferences(expandedList, uplDocumentAttributes, uea);
            expandedList = StringUtils.removeDuplicates(expandedList);
            expandedList.remove(uea.getLongName());
            expandedReferences.put(pageName, Collections.unmodifiableList(getPageNames(expandedList)));
        }
    }

    @SuppressWarnings("unchecked")
    private void expandReferences(List<String> expandedList, UplDocumentAttributes uplDocumentAttributes,
            UplElementAttributes uea) throws UnifyException {
        for (String longName : uea.getShallowReferencedLongNames("components")) {
            expandReferences(expandedList, uplDocumentAttributes,
                    uplDocumentAttributes.getChildElementByLongName(longName));
        }

        if (uea.isAttribute("section")) {
            Section[] sections = uea.getAttributeValue(Section[].class, "section");
            for (Section section : sections) {
                UplElementReferences uplRef = section.getUplAttribute(UplElementReferences.class, "components");
                for (String longName : uplRef.getLongNames()) {
                    expandReferences(expandedList, uplDocumentAttributes,
                            uplDocumentAttributes.getChildElementByLongName(longName));
                }
            }
        }

        Set<UplElementAttributes> childElements = uea.getChildElements();
        for (UplElementAttributes ueaInner : childElements) {
            expandReferences(expandedList, uplDocumentAttributes, ueaInner);
        }

        if (childElements.isEmpty() && !expandedList.contains(uea.getLongName())) {
            Class<? extends UplComponent> type = (Class<? extends UplComponent>) getComponentType(
                    uea.getComponentName());
            if (!PageAction.class.isAssignableFrom(type) && !PageValidation.class.isAssignableFrom(type)) {
                expandedList.add(uea.getLongName());
            }
        }
    }

    private class StandalonePanelContext {

        private StandalonePanelInfo standalonePanelInfo;

        private Map<String, Widget> componentsByLongNameMap;

        private WidgetRepository widgetRepository;

        public StandalonePanelContext(StandalonePanelInfo standalonePanelInfo) {
            this.standalonePanelInfo = standalonePanelInfo;
            componentsByLongNameMap = new HashMap<String, Widget>();
            widgetRepository = new WidgetRepository(standalonePanelInfo.getWidgetInfos());
        }

        public void addWidget(Widget widget) throws UnifyException {
            componentsByLongNameMap.put(widget.getLongName(), widget);
        }

        public Widget getWidget(String longName) {
            return componentsByLongNameMap.get(longName);
        }

        public void addRepositoryWidget(Widget widget) throws UnifyException {
            widgetRepository.putWidget(widget);
        }

        public Collection<String> getWidgetLongNames() {
            return componentsByLongNameMap.keySet();
        }

        public Collection<Widget> getWidgets() {
            return componentsByLongNameMap.values();
        }

        public StandalonePanelInfo getStandalonePanelInfo() {
            return standalonePanelInfo;
        }

        public WidgetRepository getWidgetRepository() {
            return widgetRepository;
        }

    }

    private class PageNameMap extends FactoryMap<String, String> {

        private Map<String, String> longNameByPageNameMap;

        public PageNameMap() {
            longNameByPageNameMap = new HashMap<String, String>();
        }

        public String getLongName(String pageName) throws UnifyException {
            String longName = longNameByPageNameMap.get(pageName);
            if (longName == null) {
                throw new UnifyException(UnifyCoreErrorConstants.UNKNOWN_PAGE_NAME, pageName);
            }
            return longName;
        }

        @Override
        public void clear() {
            super.clear();
            longNameByPageNameMap.clear();
        }

        @Override
        protected String create(String longName, Object... params) throws Exception {
            String pageName = pageNamePrefix + sequenceNumberBusinessModule.getUniqueStringId(longName);
            longNameByPageNameMap.put(pageName, longName);
            return pageName;
        }
    }
}
