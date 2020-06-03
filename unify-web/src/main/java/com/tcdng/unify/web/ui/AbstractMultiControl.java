/*
 * Copyright 2018-2020 The Code Department.
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.upl.UplUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.DataTransferBlock;
import com.tcdng.unify.web.ui.panel.StandalonePanel;
import com.tcdng.unify.web.util.WidgetUtils;

/**
 * Serves as a base class for controls that contain and make use of other
 * widgets.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "components", type = UplElementReferences.class),
        @UplAttribute(name = "valueMarker", type = String.class) })
public abstract class AbstractMultiControl extends AbstractControl implements MultiControl {

    private Map<String, ChildWidgetInfo> widgetInfoMap;

    private ValueStore thisValueStore;

    private List<String> standalonePanelNames;

    private String uplValueMarker;
    
    public AbstractMultiControl() {
        widgetInfoMap = new LinkedHashMap<String, ChildWidgetInfo>();
    }

    @Override
    public final void onPageConstruct() throws UnifyException {
        super.onPageConstruct();        
        uplValueMarker = getUplAttribute(String.class, "valueMarker");   
        doOnPageConstruct();
    }
    
    @Override
    public void addChildWidget(Widget widget) throws UnifyException {
        doAddChildWidget(widget, false, false, false, true);
    }

    @Override
    public void setValueStore(ValueStore valueStore) throws UnifyException {
        super.setValueStore(valueStore);
        for (ChildWidgetInfo childWidgetInfo : widgetInfoMap.values()) {
            if (childWidgetInfo.isConforming()) {
                childWidgetInfo.getWidget().setValueStore(valueStore);
            }
        }
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        for (ChildWidgetInfo childWidgetInfo : widgetInfoMap.values()) {
            if (!childWidgetInfo.isIgnoreParentState()) {
                childWidgetInfo.getWidget().setDisabled(disabled);
            }
        }
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        for (ChildWidgetInfo childWidgetInfo : widgetInfoMap.values()) {
            if (!childWidgetInfo.isIgnoreParentState()) {
                childWidgetInfo.getWidget().setEditable(editable);
            }
        }
    }

    @Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        if (transferBlock != null) {
            DataTransferBlock childBlock = transferBlock.getChildBlock();
            DataTransferWidget dtWidget = (DataTransferWidget) getChildWidgetInfo(childBlock.getId()).getWidget();
            dtWidget.populate(childBlock);
            onInternalChildPopulated(dtWidget);
        }
    }

    @Override
    public ChildWidgetInfo getChildWidgetInfo(String childId) {
        return widgetInfoMap.get(childId);
    }

    @Override
    public Collection<ChildWidgetInfo> getChildWidgetInfos() {
        return widgetInfoMap.values();
    }

    @Override
    public int getChildWidgetCount() {
        return widgetInfoMap.size();
    }

    @Override
    public void setId(String id) throws UnifyException {
        boolean changed = !DataUtils.equals(getId(), id);
        super.setId(id);
        if (changed && !widgetInfoMap.isEmpty()) {
            Map<String, ChildWidgetInfo> map = new LinkedHashMap<String, ChildWidgetInfo>();
            for (ChildWidgetInfo childWidgetInfo : widgetInfoMap.values()) {
                Widget widget = childWidgetInfo.getWidget();
                String newChildId = WidgetUtils.renameChildId(id, widget.getId());
                widget.setId(newChildId);
                map.put(newChildId, new ChildWidgetInfo(widget, childWidgetInfo.isIgnoreParentState(),
                        childWidgetInfo.isExternal()));
            }

            widgetInfoMap = map;
        }
    }

    @Override
    public final Object getValue(String attribute) throws UnifyException {
        if (attribute != null) {
            return super.getValue(attribute);
        }

        if (getValueStore() != null) {
            return getValueStore().getValueObject();
        }

        return null;
    }

    protected String getUplValueMarker() {
        return uplValueMarker;
    }

    /**
     * Creates and adds a non-conforming external child widget that doesn't ignore
     * parent state.
     * 
     * @param descriptor
     *            descriptor used to create child widget.
     * @return the added child widget
     * @throws UnifyException
     *             if an error occurs
     */
    protected Widget addExternalChildWidget(String descriptor) throws UnifyException {
        Widget widget = (Widget) getUplComponent(getSessionLocale(), descriptor, false);
        doAddChildWidget(widget, true, false, false, true);
        return widget;
    }

    /**
     * Creates and adds a non-conforming external child standalone panel that
     * doesn't ignore parent state.
     * 
     * @param panelName
     *            the panelName
     * @param cloneId
     *            the clone ID
     * @return the added child widget
     * @throws UnifyException
     *             if an error occurs
     */
    protected Widget addExternalChildStandalonePanel(String panelName, String cloneId) throws UnifyException {
        String uniqueName = UplUtils.generateUplComponentCloneName(panelName, cloneId);
        Page page = getRequestContextUtil().getRequestPage();
        StandalonePanel standalonePanel = page.getStandalonePanel(uniqueName);
        if (standalonePanel == null) {
            standalonePanel = getPageManager().createStandalonePanel(getSessionLocale(), uniqueName);
            page.addStandalonePanel(uniqueName, standalonePanel);
            getControllerManager().updatePageControllerInfo(
                    getRequestContextUtil().getResponsePathParts().getControllerName(), uniqueName);
            if (standalonePanelNames == null) {
                standalonePanelNames = new ArrayList<String>();
            }

            standalonePanelNames.add(uniqueName);
        }

        standalonePanel.setContainer(getContainer());
        doAddChildWidget(standalonePanel, true, false, false, true);
        return standalonePanel;
    }

    @Override
    public void addPageAliases() throws UnifyException {
        super.addPageAliases();

        if (standalonePanelNames != null) {
            Page page = getRequestContextUtil().getRequestPage();
            for (String uniqueName : standalonePanelNames) {
                StandalonePanel standalonePanel = page.getStandalonePanel(uniqueName);
                if (standalonePanel != null) {
                    List<String> aliases = getPageManager().getExpandedReferences(standalonePanel.getId());
                    getRequestContextUtil().addPageAlias(getId(), aliases.toArray(new String[aliases.size()]));
                }
            }
        }
    }

    @Override
    protected final ValueStore createValueStore(Object storageObject, int dataIndex) throws UnifyException {
        if (uplValueMarker != null) {
            return super.createValueStore(storageObject, uplValueMarker, dataIndex);
        }
        
        return super.createValueStore(storageObject, dataIndex);
    }

    /**
     * Removes all external child widgets.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void removeAllExternalChildWidgets() throws UnifyException {
        for (Iterator<Map.Entry<String, ChildWidgetInfo>> it = widgetInfoMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, ChildWidgetInfo> entry = it.next();
            if (entry.getValue().isExternal()) {
                it.remove();
            }
        }

        if (standalonePanelNames != null) {
            Page page = getRequestContextUtil().getRequestPage();
            for (String uniqueName : standalonePanelNames) {
                page.removeStandalonePanel(uniqueName);
            }
        }

        standalonePanelNames = null;
    }

    /**
     * Creates and adds a non-conforming internal child widget that doesn't ignore
     * parent state.
     * 
     * @param descriptor
     *            descriptor used to create child widget.
     * @return the added child widget
     * @throws UnifyException
     *             if an error occurs
     */
    protected Widget addInternalChildWidget(String descriptor) throws UnifyException {
        return addInternalChildWidget(descriptor, false, false);
    }

    /**
     * Creates and adds an internal child widget.
     * 
     * @param descriptor
     *            descriptor used to create child widget.
     * @param conforming
     *            indicates if child is conforming
     * @param ignoreParentState
     *            set this flag to true if child widget ignore parent state.
     * @return the added child widget
     * @throws UnifyException
     *             if an error occurs
     */
    protected Widget addInternalChildWidget(String descriptor, boolean conforming, boolean ignoreParentState)
            throws UnifyException {
        Widget widget = (Widget) getUplComponent(getSessionLocale(), descriptor, false);
        doAddChildWidget(widget, true, conforming, ignoreParentState, false);
        return widget;
    }

    /**
     * Adds child widget id to request context page aliases.
     * 
     * @param widget
     *            the child widget
     * @throws UnifyException
     *             if an error occurs
     */
    protected void addPageAlias(Widget widget) throws UnifyException {
        getRequestContextUtil().addPageAlias(getId(), widget.getId());
    }

    /**
     * Adds id to request context page aliases.
     * 
     * @param id
     *            the to add
     * @throws UnifyException
     *             if an error occurs
     */
    protected void addPageAlias(String id) throws UnifyException {
        getRequestContextUtil().addPageAlias(getId(), id);
    }

    protected void onInternalChildPopulated(Widget widget) throws UnifyException {

    }

    protected abstract void doOnPageConstruct() throws UnifyException;

    private void doAddChildWidget(Widget widget, boolean pageConstruct, boolean conforming, boolean ignoreParentState,
            boolean external) throws UnifyException {
        int childIndex = widgetInfoMap.size();
        String childId = WidgetUtils.getChildId(getId(), widget.getId(), childIndex);
        widget.setId(childId);
        if (pageConstruct) {
            widget.onPageConstruct();
            widget.setContainer(getContainer());
        }

        if (!ignoreParentState) {
            widget.setEditable(isEditable());
            widget.setDisabled(isDisabled());
        }

        if (conforming) {
            widget.setValueStore(getValueStore());
        } else {
            widget.setValueStore(getThisValueStore());
        }

        widget.setConforming(conforming);
        widgetInfoMap.put(childId, new ChildWidgetInfo(widget, ignoreParentState, external));
    }

    private ValueStore getThisValueStore() throws UnifyException {
        if (thisValueStore == null) {
            thisValueStore = createValueStore(this);
        }

        return thisValueStore;
    }

    public static class ChildWidgetInfo {

        private Widget widget;

        private boolean external;

        private boolean ignoreParentState;

        public ChildWidgetInfo(Widget widget, boolean ignoreParentState, boolean external) {
            this.widget = widget;
            this.ignoreParentState = ignoreParentState;
            this.external = external;
        }

        public Widget getWidget() {
            return widget;
        }

        public boolean isIgnoreParentState() {
            return ignoreParentState;
        }

        public boolean isExternal() {
            return external;
        }

        public boolean isConforming() {
            return widget.isConforming();
        }

        public boolean isPrivilegeVisible() throws UnifyException {
            return widget.isVisible();
        }
    }
}
