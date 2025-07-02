/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.widget;

import java.text.MessageFormat;
import java.util.Collection;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.ViewDirective;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.FileAttachmentInfo;
import com.tcdng.unify.core.data.IndexedTarget;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.upl.AbstractUplComponent;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.TargetPath;
import com.tcdng.unify.web.UnifyWebSessionAttributeConstants;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.http.HttpRequestHeaders;
import com.tcdng.unify.web.http.HttpRequestParameters;
import com.tcdng.unify.web.ui.PageAttributeConstants;
import com.tcdng.unify.web.ui.PageRequestContextUtil;
import com.tcdng.unify.web.ui.UIControllerUtil;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;
import com.tcdng.unify.web.ui.constant.WidgetTempValueConstants;
import com.tcdng.unify.web.ui.util.WidgetUtils;
import com.tcdng.unify.web.ui.widget.data.Hint.MODE;
import com.tcdng.unify.web.ui.widget.data.Popup;
import com.tcdng.unify.web.ui.widget.panel.StandalonePanel;

/**
 * Abstract base class for widgets.
 * 
 * @author The Code Department
 * @since 4.1
 */
@UplAttributes({ @UplAttribute(name = "binding", type = String.class),
		@UplAttribute(name = "styleClass", type = String.class, defaultVal = "$e{}"),
		@UplAttribute(name = "styleClassBinding", type = String.class),
		@UplAttribute(name = "style", type = String.class), @UplAttribute(name = "caption", type = String.class),
		@UplAttribute(name = "captionBinding", type = String.class),
		@UplAttribute(name = "captionParamBinding", type = String.class),
		@UplAttribute(name = "columnStyle", type = String.class),
		@UplAttribute(name = "columnSelectSummary", type = boolean.class),
		@UplAttribute(name = "hint", type = String.class), @UplAttribute(name = "hintBinding", type = String.class),
		@UplAttribute(name = "readOnly", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "disabled", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "ignoreParentState", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "privilege", type = String.class),
		@UplAttribute(name = "fixedConforming", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "hidden", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "valueStoreMemory", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "behaviorAlways", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "copyEventHandlers", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "eventHandler", type = EventHandler[].class) })
public abstract class AbstractWidget extends AbstractUplComponent implements Widget {

	private static final EventHandler[] ZERO_EVENT_HANDLERS = new EventHandler[0];

	private EventHandler[] handlers;

	private String id;

	private String groupId;

	private Container container;

	private ValueStore valueStore;

	private ValueStore[] valueStoreMem;

	private String extraStyle;

	private int tabIndex;

	private boolean conforming;

	private boolean disabled;

	private boolean editable;

	private boolean visible;

	private boolean inRepeat;

	public AbstractWidget() {
		this.tabIndex = -1;
		this.conforming = true;
		this.editable = true;
		this.visible = true;
	}

	@Override
	public String getId() throws UnifyException {
		return id;
	}

	@Override
	public void setId(String id) throws UnifyException {
		this.id = id;
	}

	@Override
	public final String getBaseId() throws UnifyException {
		return id;
	}

	@Override
	public String getGroupId() throws UnifyException {
		return groupId;
	}

	@Override
	public void setGroupId(String groupId) throws UnifyException {
		this.groupId = groupId;
	}

	@Override
	public String getFacadeId() throws UnifyException {
		return this.id;
	}

	@Override
	public String getPrefixedId(String prefix) throws UnifyException {
		return prefix + getId();
	}

	@Override
	public String getNamingIndexedId(int index) throws UnifyException {
		return WidgetUtils.getNamingIndexId(getId(), index);
	}

	@Override
	public String getCaption() throws UnifyException {
		String caption = null;
		String captionBinding = getUplAttribute(String.class, "captionBinding");
		if (captionBinding != null && !captionBinding.isEmpty()) {
			caption = getStringValue(captionBinding);
		}

		caption = caption != null ? caption : getUplAttribute(String.class, "caption");
		String captionParamBinding = getUplAttribute(String.class, "captionParamBinding");
		if (captionParamBinding != null) {
			return MessageFormat.format(caption, getValue(captionParamBinding));
		}

		return caption;
	}

	@Override
	public String getBinding() throws UnifyException {
		return getUplAttribute(String.class, "binding");
	}

	@Override
	public String getColumnStyle() throws UnifyException {
		return getUplAttribute(String.class, "columnStyle");
	}

	@Override
	public boolean getColumnSelectSummary() throws UnifyException {
		return getUplAttribute(boolean.class, "columnSelectSummary");
	}

	@Override
	public String getStyleClass() throws UnifyException {
		return getUplAttribute(String.class, "styleClass");
	}

	@Override
	public String getStyleClassBinding() throws UnifyException {
		return getUplAttribute(String.class, "styleClassBinding");
	}

	@Override
	public EventHandler[] getEventHandlers() throws UnifyException {
		if (handlers == null) {
			synchronized (this) {
				if (handlers == null) {
					handlers = getUplAttribute(EventHandler[].class, "eventHandler");
					if (handlers == null) {
						handlers = ZERO_EVENT_HANDLERS;
					}

					if (handlers.length > 0 && getUplAttribute(boolean.class, "copyEventHandlers")) {
						EventHandler[] _handlers = new EventHandler[handlers.length];
						for (int i = 0; i < handlers.length; i++) {
							_handlers[i] = handlers[i].wrap();
						}

						handlers = _handlers;
					}
				}
			}
		}

		return handlers;
	}

	@Override
	public String getStyleClassValue() throws UnifyException {
		String styleClassBinding = getUplAttribute(String.class, "styleClassBinding");
		if (styleClassBinding != null) {
			return getValue(String.class, styleClassBinding);
		}

		return null;
	}

	@Override
	public String getExtraStyle() throws UnifyException {
		return extraStyle;
	}

	@Override
	public void setExtraStyle(String extraStyle) throws UnifyException {
		this.extraStyle = extraStyle;
	}

	@Override
	public String getStyle() throws UnifyException {
		if (isHidden()) {
			return "display:none;";
		}

		String style = getUplAttribute(String.class, "style");
		return extraStyle != null ? (style != null ? style + extraStyle : extraStyle) : style;
	}

	@Override
	public String getHint() throws UnifyException {
		return getUplAttribute(String.class, "hint", "hintBinding");
	}

	@Override
	public boolean isHidden() throws UnifyException {
		return getUplAttribute(boolean.class, "hidden");
	}

	@Override
	public boolean isIgnoreParentState() throws UnifyException {
		return getUplAttribute(boolean.class, "ignoreParentState");
	}

	@Override
	public boolean isMasked() throws UnifyException {
		return false;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public void setValueStore(ValueStore valueStore) throws UnifyException {
		this.valueStore = valueStore;
		if (valueStoreMem != null && valueStore != null) {
			int dataIndex = valueStore.getDataIndex();
			if (dataIndex >= 0 && dataIndex < valueStoreMem.length) {
				valueStoreMem[dataIndex] = valueStore;
			}
		}
	}

	@Override
	public ValueStore getValueStore() {
		return valueStore;
	}

	@Override
	public String getValueMarker() {
		if (valueStore != null) {
			return valueStore.getDataMarker();
		}

		return null;
	}

	@Override
	public String getValuePrefix() {
		if (valueStore != null) {
			return valueStore.getDataPrefix();
		}

		return null;
	}

	@Override
	public int getValueIndex() {
		if (valueStore != null) {
			return valueStore.getDataIndex();
		}

		return -1;
	}

	@Override
	public boolean supportsValueStoreMemory() throws UnifyException {
		return getUplAttribute(boolean.class, "valueStoreMemory");
	}

	@Override
	public boolean initValueStoreMemory(int size) throws UnifyException {
		if (size > 0 && supportsValueStoreMemory()) {
			valueStoreMem = new ValueStore[size];
			return true;
		}

		valueStoreMem = null;
		return false;
	}

	@Override
	public boolean recallValueStore(int memoryIndex) throws UnifyException {
		if (valueStoreMem != null && memoryIndex >= 0 && memoryIndex < valueStoreMem.length) {
			valueStore = valueStoreMem[memoryIndex];
			return true;
		}

		return false;
	}

	@Override
	public void useRecallMemory(Widget srcWidget) throws UnifyException {
		if (srcWidget instanceof AbstractWidget) {
			valueStoreMem = ((AbstractWidget) srcWidget).valueStoreMem;
		}
	}

	@Override
	public String getContainerId() throws UnifyException {
		if (container != null) {
			return container.getId();
		}

		return null;
	}

	@Override
	public String getPanelId() throws UnifyException {
		Panel panel = getPanel();
		if (panel != null) {
			return panel.getId();
		}
		return null;
	}

	@Override
	public Widget getRelayWidget() throws UnifyException {
		return null;
	}

	@Override
	public int getIndexedHandlerCount() throws UnifyException {
		return 0;
	}

	@Override
	public boolean isRelayCommand() {
		return false;
	}

	@Override
	public boolean isConforming() {
		return conforming;
	}

	@Override
	public boolean isValueConforming(Container container) {
		return (container == this.container) && conforming;
	}

	@Override
	public boolean isFixedConforming() throws UnifyException {
		return getUplAttribute(boolean.class, "fixedConforming");
	}

	@Override
	public boolean isUseFacade() throws UnifyException {
		return false;
	}

	@Override
	public boolean isUseFacadeFocus() throws UnifyException {
		return isUseFacade();
	}

	@Override
	public boolean isBindEventsToFacade() throws UnifyException {
		return isUseFacade();
	}

	@Override
	public void setConforming(boolean conforming) {
		this.conforming = conforming;
	}

	@Override
	public boolean isDisabled() throws UnifyException {
		return disabled || getViewDirective().isDisabled() || getUplAttribute(boolean.class, "disabled");
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public boolean isContainerDisabled() throws UnifyException {
		if (container != null && !isIgnoreParentState()) {
			return container.isContainerDisabled() || isDisabled();
		}
		return isDisabled();
	}

	@Override
	public boolean isEditable() throws UnifyException {
		return editable && !getUplAttribute(boolean.class, "readOnly") && getViewDirective().isEditable();
	}

	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public boolean isContainerEditable() throws UnifyException {
		if (container != null && !isIgnoreParentState()) {
			return container.isContainerEditable() && isEditable();
		}
		return isEditable();
	}

	@Override
	public boolean isActive() throws UnifyException {
		return isContainerEditable() && !isContainerDisabled();
	}

	@Override
	public boolean isVisible() throws UnifyException {
		return visible && getViewDirective().isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isContainerVisible() throws UnifyException {
		if (container != null && !isIgnoreParentState()) {
			return container.isContainerVisible() && isVisible();
		}
		return isVisible();
	}

	@Override
	public void setInRepeat(boolean inRepeat) {
		this.inRepeat = inRepeat;
	}

	@Override
	public boolean isInRepeat() {
		return inRepeat;
	}

	@Override
	public boolean isBehaviorAlways() throws UnifyException {
		return getUplAttribute(boolean.class, "behaviorAlways");
	}

	@Override
	public boolean isValidatable() throws UnifyException {
		return isVisible() && !disabled && editable;
	}

	@Override
	public boolean isSupportReadOnly() {
		return true;
	}

	@Override
	public boolean isSupportDisabled() {
		return true;
	}

	@Override
	public boolean isNoPush() throws UnifyException {
		return (isSupportReadOnly() && !isContainerEditable()) || (isSupportDisabled() && isContainerDisabled());
	}

	@Override
	public void setAlternateMode(boolean alternateMode) {

	}

	@Override
	public int getTabIndex() throws UnifyException {
		return tabIndex;
	}

	@Override
	public void setTabIndex(int tabIndex) throws UnifyException {
		this.tabIndex = tabIndex;
	}

	@Override
	public void onPageConstruct() throws UnifyException {

	}

	@Override
	public Object getValue() throws UnifyException {
		if (inRepeat) {
			IndexedTarget target = getIndexedTarget();
			if (target.isValidValueIndex()) {
				return getValueStore().setDataIndex(target.getValueIndex()).getValueObjectAtDataIndex();
			}
		}

		return getValue(getUplAttribute(String.class, "binding"));
	}

	@Override
	public <T> T getValue(Class<T> valueClass) throws UnifyException {
		return convert(valueClass, getValue(), null);
	}

	@Override
	public void setValue(Object value) throws UnifyException {
		setValue(getUplAttribute(String.class, "binding"), value);
	}

	@Override
	public String getStringValue() throws UnifyException {
		return convert(String.class, getValue(), null);
	}

	@Override
	public String getStringValue(String attribute) throws UnifyException {
		return convert(String.class, getValue(attribute), null);
	}

	@Override
	public <T> T getValue(Class<T> clazz, String attribute) throws UnifyException {
		return convert(clazz, getValue(attribute), null);
	}

	@Override
	public <T, U extends Collection<T>> U getValue(Class<U> clazz, Class<T> dataClass) throws UnifyException {
		return convert(clazz, dataClass, getValue(), null);
	}

	@Override
	public boolean isSupportStretch() {
		return true;
	}

	@Override
	public boolean isLayoutCaption() throws UnifyException {
		return true;
	}

	@Override
	public void addPageAliases() throws UnifyException {

	}

	@Override
	public boolean isField() {
		return false;
	}

	@Override
	public Object getValue(String attribute) throws UnifyException {
		if (attribute != null) {
			if (valueStore != null && valueStore.isGettable(attribute)) {
				return valueStore.retrieve(attribute);
			}

			if (isSessionAttribute(attribute)) {
				return getSessionAttribute(attribute);
			}

			if (isApplicationAttribute(attribute)) {
				return getApplicationAttribute(attribute);
			}

			if (isRequestAttribute(attribute)) {
				return getRequestAttribute(attribute);
			}

			if (isPageAttribute(attribute)) {
				return getPageAttribute(attribute);
			}
		}

		return null;
	}

	@Override
	public Panel getPanel() throws UnifyException {
		Widget container = this.container;
		while (container != null) {
			if (container instanceof Panel) {
				return (Panel) container;
			}
			container = container.getContainer();
		}
		return null;
	}

	@Override
	public StandalonePanel getStandalonePanel() throws UnifyException {
		Widget container = this.container;
		while (container != null) {
			if (container instanceof StandalonePanel) {
				return (StandalonePanel) container;
			}
			container = container.getContainer();
		}
		return null;
	}

	@Override
	public void updateInternalState() throws UnifyException {

	}

	@Override
	public WriteWork getWriteWork() throws UnifyException {
		String workId = getWorkId();
		WriteWork work = (WriteWork) getRequestAttribute(workId);
		if (work == null) {
			work = new WriteWork();
			setRequestAttribute(workId, work);
		}

		return work;
	}

	@Override
	public boolean isRefreshesContainer() throws UnifyException {
		return false;
	}

	@Override
	public void setPrecision(int precision) throws UnifyException {

	}

	@Override
	public void setScale(int scale) throws UnifyException {

	}

	protected HttpRequestHeaders getHttpRequestHeaders() throws UnifyException {
		return (HttpRequestHeaders) getRequestAttribute(UnifyWebRequestAttributeConstants.HEADERS);
	}

	protected HttpRequestParameters getHttpRequestParameters() throws UnifyException {
		return (HttpRequestParameters) getRequestAttribute(UnifyWebRequestAttributeConstants.PARAMETERS);
	}

	protected String getHttpRequestHeader(String headerName) throws UnifyException {
		HttpRequestHeaders headers = getHttpRequestHeaders();
		return headers != null ? headers.getHeader(headerName) : null;
	}

	protected String getHttpRequestParameter(String paramName) throws UnifyException {
		HttpRequestParameters parameters = getHttpRequestParameters();
		return parameters != null ? parameters.getParameter(paramName) : null;
	}

	protected boolean isTempValue(String name) throws UnifyException {
		return valueStore != null ? valueStore.isTempValue(name) : false;
	}

	protected void setTempValue(String name, Object value) throws UnifyException {
		if (valueStore != null) {
			valueStore.setTempValue(name, value);
		}
	}

	protected Object removeTempValue(String name) throws UnifyException {
		if (valueStore != null) {
			return valueStore.removeTempValue(name);
		}

		return null;
	}

	protected void setPageAttribute(String name, Object value) throws UnifyException {
		Page page = resolveRequestPage();
		if (page != null) {
			page.setAttribute(name, value);
		}
	}

	protected Object clearPageAttribute(String name) throws UnifyException {
		Page page = resolveRequestPage();
		return page != null ? page.clearAttribute(name) : null;
	}

	protected boolean isPageAttribute(String name) throws UnifyException {
		Page page = resolveRequestPage();
		return page != null ? page.isAttribute(name) : false;
	}

	protected Object getPageAttribute(String name) throws UnifyException {
		Page page = resolveRequestPage();
		return page != null ? page.getAttribute(name) : null;
	}

	protected Object removePageAttribute(String name) throws UnifyException {
		Page page = resolveRequestPage();
		return page != null ? page.removeAttribute(name) : null;
	}

	protected <T> T getPageAttribute(Class<T> clazz, String name) throws UnifyException {
		Page page = resolveRequestPage();
		return DataUtils.convert(clazz, page != null ? page.getAttribute(name) : null);
	}

	protected <T> T removePageAttribute(Class<T> clazz, String name) throws UnifyException {
		Page page = resolveRequestPage();
		return DataUtils.convert(clazz, page != null ? page.removeAttribute(name) : null);
	}

	protected boolean isOtherPageClosedDetected() throws UnifyException {
		return getPageAttribute(boolean.class, PageAttributeConstants.OTHER_PAGE_CLOSED_DETECTED);
	}

	protected Page resolveRequestPage() throws UnifyException {
		PageRequestContextUtil rcUtil = getRequestContextUtil();
		Page contentPage = rcUtil.getContentPage();
		return contentPage == null ? rcUtil.getRequestPage() : contentPage;
	}

	protected boolean clearOtherPageClosedDetected() throws UnifyException {
		return removePageAttribute(boolean.class, PageAttributeConstants.OTHER_PAGE_CLOSED_DETECTED);
	}

	protected ViewDirective getViewDirective() throws UnifyException {
		if (isApplicationIgnoreViewDirective()) {
			return ViewDirective.ALLOW_VIEW_DIRECTIVE;
		}

		return getViewDirective(getUplAttribute(String.class, "privilege"));
	}

	protected int getRequestTriggerDataIndex() throws UnifyException {
		return getRequestContextUtil().getRequestTriggerDataIndex();
	}

	protected <T> T getRequestTarget(Class<T> clazz) throws UnifyException {
		return getRequestContextUtil().getRequestTargetValue(clazz);
	}

	protected IndexedTarget getIndexedTarget() throws UnifyException {
		return DataUtils.convert(IndexedTarget.class, getRequestContextUtil().getRequestTargetValue(String.class));
	}

	protected IndexedTarget getIndexedTarget(String target) throws UnifyException {
		return DataUtils.convert(IndexedTarget.class, target);
	}

	protected String getRequestCommandTag() throws UnifyException {
		return getRequestContextUtil().getRequestCommandTag();
	}

	protected void commandRefreshPanels(UplElementReferences references) throws UnifyException {
		if (references != null) {
			getRequestContextUtil()
					.setResponseRefreshPanels(DataUtils.toArray(String.class, references.getLongNames()));
			setCommandResultMapping(ResultMappingConstants.REFRESH_PANELS);
		}
	}

	protected void commandRefreshPanels(String... panelLongName) throws UnifyException {
		getRequestContextUtil().setResponseRefreshPanels(panelLongName);
		setCommandResultMapping(ResultMappingConstants.REFRESH_PANELS);
	}

	protected void commandRefreshPanelsAndHidePopup(String... panelLongName) throws UnifyException {
		getRequestContextUtil().setResponseRefreshPanels(panelLongName);
		setCommandResultMapping(ResultMappingConstants.REFRESH_HIDE_POPUP);
	}

	protected void commandRefreshPanelsAndHidePopup(Panel... panels) throws UnifyException {
		getRequestContextUtil().setResponseRefreshPanels(panels);
		setCommandResultMapping(ResultMappingConstants.REFRESH_HIDE_POPUP);
	}

	protected void commandShowPopup(String panelLongName) throws UnifyException {
		getRequestContextUtil().setRequestPopupName(panelLongName);
		setCommandResultMapping(ResultMappingConstants.SHOW_POPUP);
	}

	protected void commandShowPopup(Popup popup) throws UnifyException {
		setSessionAttribute(UnifyWebSessionAttributeConstants.POPUP, popup);
		setCommandResultMapping(popup.getResultMapping());
	}

	protected Popup getCurrentPopup() throws UnifyException {
		return getSessionAttribute(Popup.class, UnifyWebSessionAttributeConstants.POPUP);
	}

	protected Popup removeCurrentPopup() throws UnifyException {
		return (Popup) removeSessionAttribute(UnifyWebSessionAttributeConstants.POPUP);
	}

	protected void commandHidePopup() throws UnifyException {
		removeCurrentPopup();
		getRequestContextUtil().removeResponseRefreshPanel(getLongName());
		setCommandResultMapping(ResultMappingConstants.HIDE_POPUP);
	}

	protected void commandPost(String path) throws UnifyException {
		setRequestAttribute(UnifyWebRequestAttributeConstants.COMMAND_POSTRESPONSE_PATH, path);
		setCommandResultMapping(ResultMappingConstants.POST_RESPONSE);
	}

	protected void showAttachment(FileAttachmentInfo fileAttachmentInfo) throws UnifyException {
		setRequestAttribute(UnifyWebRequestAttributeConstants.FILEATTACHMENTS_INFO, fileAttachmentInfo);
		setCommandResultMapping(ResultMappingConstants.SHOW_ATTACHMENT);
	}

	protected void refreshApplicationMenu() throws UnifyException {
		setSessionAttribute(UnifyWebSessionAttributeConstants.REFRESH_MENU, Boolean.TRUE);
	}

	protected void setCommandResultMapping(String resultMappingName) throws UnifyException {
		getRequestContextUtil().setCommandResultMapping(resultMappingName);
	}

	protected void setCommandResponsePath(TargetPath targetPath) throws UnifyException {
		getRequestContextUtil().setCommandResponsePath(targetPath);
	}

	protected UIControllerUtil getUIControllerUtil() throws UnifyException {
		return (UIControllerUtil) getComponent(WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
	}

	protected PageManager getPageManager() throws UnifyException {
		return (PageManager) getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
	}

	protected PageRequestContextUtil getRequestContextUtil() throws UnifyException {
		return (PageRequestContextUtil) getComponent(WebUIApplicationComponents.APPLICATION_PAGEREQUESTCONTEXTUTIL);
	}

	protected final String getContextURL(String path) throws UnifyException {
		RequestContext requestContext = getRequestContext();
		StringBuilder sb = new StringBuilder();
		if (getRequestContextUtil().isRemoteViewer()) {
			sb.append(getSessionContext().getUriBase());
		}

		sb.append(requestContext.getContextPath());
		if (requestContext.isWithTenantPath()) {
			sb.append(requestContext.getTenantPath());
		}

		sb.append(path);
		return sb.toString();
	}

	/**
	 * Sets the value of an attribute in associated value store, if component has
	 * one.
	 * 
	 * @param attribute the attribute name
	 * @param value     the value to set
	 * @throws UnifyException if value store has no value with such attribute name
	 */
	protected void setValue(String attribute, Object value) throws UnifyException {
		if (valueStore != null && attribute != null) {
			valueStore.store(attribute, value);
		}
	}

	/**
	 * Appends a UPL attribute to a UPL string.
	 * 
	 * @param sb        string builder that represents the UPL string to append to
	 * @param attribute this component's UPL attribute to append
	 * @throws UnifyException if an error occurs
	 */
	protected void appendUplAttribute(StringBuilder sb, String attribute) throws UnifyException {
		Object value = getUplAttribute(Object.class, attribute);
		if (value != null) {
			sb.append(' ').append(attribute).append(':').append(value);
		}
	}

	protected <T> T getUplAttribute(Class<T> type, String attribute, String bindingAttribute) throws UnifyException {
		String binding = getUplAttribute(String.class, bindingAttribute);
		if (StringUtils.isNotBlank(binding)) {
			return getValue(type, binding);
		}

		return getUplAttribute(type, attribute);
	}

	/**
	 * Hints user in current request with supplied message in INFO mode.
	 * 
	 * @param messageKey the message key
	 * @param params     the message parameters
	 * @throws UnifyException if an error occurs
	 */
	protected void hintUser(String messageKey, Object... params) throws UnifyException {
		getRequestContextUtil().hintUser(MODE.INFO, messageKey, params);
	}

	/**
	 * Hints user in current request with supplied message.
	 * 
	 * @param mode       the hint mode
	 * @param messageKey the message key
	 * @param params     the message parameters
	 * @throws UnifyException if an error occurs
	 */
	protected void hintUser(MODE mode, String messageKey, Object... params) throws UnifyException {
		getRequestContextUtil().hintUser(mode, messageKey, params);
	}

	/**
	 * Checks for override tenant ID in widget value store and applies to supplied
	 * query object if found.
	 * 
	 * @param query             the query object
	 * @param tenantIdFieldName the tenant ID field name
	 * @throws UnifyException if an error occurs
	 */
	protected void applyOverrideTenantId(Query<? extends Entity> query, String tenantIdFieldName)
			throws UnifyException {
		Long overrideTenantId = getValue(Long.class, WidgetTempValueConstants.OVERRIDE_TENANT_ID);
		if (overrideTenantId != null) {
			query.addEquals(tenantIdFieldName, overrideTenantId);
		}
	}

	private String getWorkId() throws UnifyException {
		return getPrefixedId("wrk_");
	}
}
