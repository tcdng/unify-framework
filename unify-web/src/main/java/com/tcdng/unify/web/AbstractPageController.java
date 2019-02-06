/*
 * Copyright 2018-2019 The Code Department.
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
package com.tcdng.unify.web;

import java.text.MessageFormat;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ValueStoreFactory;
import com.tcdng.unify.core.task.TaskLauncher;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskSetup;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.ui.DataTransferWidget;
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.WidgetCommandManager;
import com.tcdng.unify.web.ui.data.Hint.MODE;
import com.tcdng.unify.web.ui.data.MessageBox;
import com.tcdng.unify.web.ui.data.MessageIcon;
import com.tcdng.unify.web.ui.data.MessageMode;
import com.tcdng.unify.web.ui.data.MessageResult;
import com.tcdng.unify.web.ui.data.TaskMonitorInfo;

/**
 * Convenient base page controller implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractPageController extends AbstractUserInterfaceController implements PageController {

    @Configurable
    private TaskLauncher taskLauncher;

    private Page page;

    private static final FactoryMap<String, PageControllerPathInfo> pathInfos;

    static {
        pathInfos = new FactoryMap<String, PageControllerPathInfo>() {

            @Override
            protected PageControllerPathInfo create(String beanId, Object... params) throws Exception {
                return new PageControllerPathInfo(beanId, beanId + "/openPage", beanId + "/savePage",
                        beanId + "/closePage", false);
            }
        };
    }

    public AbstractPageController() {
        this(false, false);
    }

    public AbstractPageController(boolean secured, boolean readOnly) {
        super(secured, readOnly);
    }

    @Override
    public PageControllerPathInfo getPathInfo() throws UnifyException {
        return pathInfos.get(getSessionId());
    }

    @Override
    public String getSessionId() {
        return page.getSessionId();
    }

    @Override
    public ControllerType getType() {
        return ControllerType.PAGE_CONTROLLER;
    }

    @Override
    public Page getPage() {
        return page;
    }

    @Override
    public final void setPage(Page page) throws UnifyException {
        this.page = page;
        page.setValueStore(((ValueStoreFactory) getComponent(ApplicationComponents.APPLICATION_VALUESTOREFACTORY))
                .getValueStore(this, -1));
        onSetPage();
    }

    @Override
    public Panel getPanelByLongName(String longName) throws UnifyException {
        return page.getPanelByLongName(longName);
    }

    @Override
    public Panel getPanelByShortName(String shortName) throws UnifyException {
        return page.getPanelByShortName(shortName);
    }

    @Action
    public final String index() throws UnifyException {
        onIndexPage();
        return ResultMappingConstants.INDEX;
    }

    @Action
    public final String openPage() throws UnifyException {
        onOpenPage();
        return ResultMappingConstants.OPEN;
    }

    @Action
    public final String savePage() throws UnifyException {
        onSavePage();
        return ResultMappingConstants.SAVE;
    }

    @Action
    public final String closePage() throws UnifyException {
        onClosePage();

        // Remove controller from session, effectively terminating self
        removeSessionAttribute(page.getSessionId());
        return ResultMappingConstants.CLOSE;
    }

    @Action
    public String noResult() throws UnifyException {
        return ResultMappingConstants.NONE;
    }

    @Action
    public String hidePopup() throws UnifyException {
        return ResultMappingConstants.HIDE_POPUP;
    }

    @Action
    public String hidePopupFireConfirm() throws UnifyException {
        return ResultMappingConstants.HIDE_POPUP_FIRE_CONFIRM;
    }

    @Action
    public String command() throws UnifyException {
        RequestCommand requestCommand = getRequestContextUtil().getRequestCommand();
        if (requestCommand != null) {
            WidgetCommandManager uiCommandManager =
                    (WidgetCommandManager) getComponent(WebApplicationComponents.APPLICATION_UICOMMANDMANAGER);
            Widget widget = getPageWidgetByLongName(Widget.class, requestCommand.getTargetId());
            if (widget.isRelayCommand()) {
                widget = widget.getRelayWidget();
            }

            uiCommandManager.executeCommand(widget, requestCommand.getCommand());
            String commandResultMapping = getRequestContextUtil().getCommandResultMapping();
            if (!StringUtils.isBlank(commandResultMapping)) {
                return commandResultMapping;
            }
        }

        return ResultMappingConstants.COMMAND;
    }

    @Action
    public String confirm() throws UnifyException {
        String msg = getRequestContextUtil().getRequestConfirmMessage();
        String param = getRequestContextUtil().getRequestConfirmParam();
        if (!StringUtils.isBlank(param)) {
            msg = MessageFormat.format(msg, param);
        }

        return showMessageBox(MessageIcon.QUESTION, MessageMode.YES_NO, getSessionMessage("messagebox.confirmation"),
                msg, "/confirmResult");
    }

    @Action
    public String confirmResult() throws UnifyException {
        if (MessageResult.YES.equals(getMessageResult())) {
            return hidePopupFireConfirm();
        }

        return hidePopup();
    }

    @Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        DataTransferWidget dataTransferWidget =
                (DataTransferWidget) page.getWidgetByLongName(transferBlock.getLongName());
        dataTransferWidget.populate(transferBlock);
    }

    /**
     * Sets up a response that shows a message box with default info icon and OK
     * button. {@link MessageBox} value of the session attribute
     * {@link JacklynSessionAttributeConstants#MESSAGEBOX}.
     * 
     * @param message
     *            the message to display
     * @return response to show application message box
     * @throws UnifyException
     *             if an error occurs
     */
    protected String showMessageBox(String message) throws UnifyException {
        return showMessageBox(MessageIcon.INFO, MessageMode.OK, getSessionMessage("messagebox.message"), message,
                "/hidePopup");
    }

    /**
     * Sets up a response that shows a message box with default info icon and OK
     * button. {@link MessageBox} value of the session attribute
     * {@link JacklynSessionAttributeConstants#MESSAGEBOX}.
     * 
     * @param message
     *            the message to display
     * @param actionPath
     *            the action path
     * @return response to show application message box
     * @throws UnifyException
     *             if an error occurs
     */
    protected String showMessageBox(String message, String actionPath) throws UnifyException {
        return showMessageBox(MessageIcon.INFO, MessageMode.OK, getSessionMessage("messagebox.message"), message,
                actionPath);
    }

    /**
     * Sets up a response that shows a message box with default info icon and OK
     * button. {@link MessageBox} value of the session attribute
     * {@link JacklynSessionAttributeConstants#MESSAGEBOX}.
     * 
     * @param caption
     *            the message caption
     * @param message
     *            the message to display
     * @param actionPath
     *            the action path
     * @return response to show application message box
     * @throws UnifyException
     *             if an error occurs
     */
    protected String showMessageBox(String caption, String message, String actionPath) throws UnifyException {
        return showMessageBox(MessageIcon.INFO, MessageMode.OK, caption, message, actionPath);
    }

    /**
     * Sets up a response that shows a message box. The message box is backed by the
     * {@link MessageBox} value of the session attribute
     * {@link JacklynSessionAttributeConstants#MESSAGEBOX}.
     * 
     * @param messageIcon
     *            the message icon of enumeration type {@link MessageIcon}
     * @param messageMode
     *            the message mode of enumeration type {@link MessageMode}
     * @param caption
     *            the message caption
     * @param message
     *            the message to display
     * @param actionPath
     *            the action path
     * @return response to show application message box
     * @throws UnifyException
     *             if an error occurs
     */
    protected String showMessageBox(MessageIcon messageIcon, MessageMode messageMode, String caption, String message,
            String actionPath) throws UnifyException {
        setSessionAttribute(UnifyWebSessionAttributeConstants.MESSAGEBOX,
                new MessageBox(messageIcon, messageMode, caption, message, getName() + actionPath));
        return "showapplicationmessage";
    }

    /**
     * Launches a task and shows a monitoring box.
     * 
     * @param taskSetup
     *            the task setup
     * @param caption
     *            the task monitor box caption
     * @return the show application monitor box result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    protected String launchTaskWithMonitorBox(TaskSetup taskSetup, String caption) throws UnifyException {
        return launchTaskWithMonitorBox(taskSetup, caption, null, null);
    }

    /**
     * Launches a task and shows a monitoring box.
     * 
     * @param taskSetup
     *            the task setup
     * @param caption
     *            the task monitor box caption
     * @param onSuccessPath
     *            optional on task success forward to path
     * @param onFailurePath
     *            optional on task faile forward to path
     * @return the show application monitor box result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    protected String launchTaskWithMonitorBox(TaskSetup taskSetup, String caption, String onSuccessPath,
            String onFailurePath) throws UnifyException {
        TaskMonitor taskMonitor = launchTask(taskSetup);
        TaskMonitorInfo taskMonitorInfo = new TaskMonitorInfo(taskMonitor, resolveSessionMessage(caption), onSuccessPath,
                onFailurePath);
        setSessionAttribute(UnifyWebSessionAttributeConstants.TASKMONITORINFO, taskMonitorInfo);
        return "showapplicationtaskmonitor";
    }

    /**
     * Writes value to another page controller in current session.
     * 
     * @param controllerName
     *            the target controller
     * @param propertyName
     *            the controller property to set
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    @Deprecated
    protected void writeValueTo(String controllerName, String propertyName, Object value) throws UnifyException {
        ((ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER))
                .populateController(controllerName, propertyName, value);
    }

    /**
     * Writes value to another page controller in current session.
     * 
     * @param controllerName
     *            the target controller
     * @param propertyName
     *            the controller property to set
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    protected void populate(String controllerName, String propertyName, Object value) throws UnifyException {
        ((ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER))
                .populateController(controllerName, propertyName, value);
    }

    /**
     * Sets the disabled state of a widget in page associated with this controller.
     * 
     * @param shortName
     *            the widget short name
     * @param disabled
     *            the disabled flag to set
     * @throws UnifyException
     *             if an error occurs
     */
    protected void setDisabled(String shortName, boolean disabled) throws UnifyException {
        page.getWidgetByShortName(shortName).setDisabled(disabled);
    }

    /**
     * Returns the disabled state flag of a widget in page associated with this
     * controller.
     * 
     * @param shortName
     *            the widget short name
     * @throws UnifyException
     *             if an error occurs
     */
    protected boolean isDisabled(String shortName) throws UnifyException {
        return page.getWidgetByShortName(shortName).isDisabled();
    }

    /**
     * Sets the visible state of a widget in page associated with this controller.
     * 
     * @param shortName
     *            the widget short name
     * @param visible
     *            the disabled flag to set
     * @throws UnifyException
     *             if an error occurs
     */
    protected void setVisible(String shortName, boolean visible) throws UnifyException {
        page.getWidgetByShortName(shortName).setVisible(visible);
    }

    /**
     * Returns the visible state flag of a widget in page associated with this
     * controller.
     * 
     * @param shortName
     *            the widget short name
     * @throws UnifyException
     *             if an error occurs
     */
    protected boolean isVisible(String shortName) throws UnifyException {
        return page.getWidgetByShortName(shortName).isVisible();
    }

    /**
     * Sets the editable state of a widget in page associated with this controller.
     * 
     * @param shortName
     *            the widget short name
     * @param editable
     *            the editable flag to set
     * @throws UnifyException
     *             if an error occurs
     */
    protected void setEditable(String shortName, boolean editable) throws UnifyException {
        page.getWidgetByShortName(shortName).setEditable(editable);
    }

    /**
     * Returns the editable state flag of a widget in page associated with this
     * controller.
     * 
     * @param shortName
     *            the widget short name
     * @throws UnifyException
     *             if an error occurs
     */
    protected boolean isEditable(String shortName) throws UnifyException {
        return page.getWidgetByShortName(shortName).isEditable();
    }

    /**
     * Returns a widget from page associated with this controller by long name.
     * 
     * @param clazz
     *            the widget type
     * @param longName
     *            the component long name
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    protected <T> T getPageWidgetByLongName(Class<T> clazz, String longName) throws UnifyException {
        return (T) page.getWidgetByLongName(longName);
    }

    /**
     * Returns a widget from page associated with this controller by short name.
     * 
     * @param clazz
     *            the widget type
     * @param shortName
     *            the component short name
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    protected <T> T getPageWidgetByShortName(Class<T> clazz, String shortName) throws UnifyException {
        return (T) page.getWidgetByShortName(shortName);
    }

    /**
     * Sets the value of attribute in the document context associated with this
     * controller.
     * 
     * @param name
     *            the attribute name
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    protected void setDocumentAttribute(String name, Object value) throws UnifyException {
        Document document = getRequestContextUtil().getRequestDocument();
        if (document != null) {
            document.setAttribute(name, value);
        }
    }

    /**
     * Clears an attribute from the document context associated with this
     * controller.
     * 
     * @param name
     *            the attribute name
     * @throws UnifyException
     *             if an error occurs
     */
    protected Object clearDocumentAttribute(String name) throws UnifyException {
        Document document = getRequestContextUtil().getRequestDocument();
        if (document != null) {
            return document.clearAttribute(name);
        }
        return null;
    }

    /**
     * Gets the value of attribute from the document context associated with this
     * controller.
     * 
     * @param name
     *            the attribute name
     * @return the attribute value if found, otherwise null.
     * @throws UnifyException
     *             if an error occurs
     */
    protected Object getDocumentAttribute(String name) throws UnifyException {
        Document document = getRequestContextUtil().getRequestDocument();
        if (document != null) {
            return document.getAttribute(name);
        }
        return null;
    }

    /**
     * Gets the value of attribute, casted to specified type, from the document
     * context associated with this controller.
     * 
     * @param clazz
     *            the type to cast attribute value to
     * @param name
     *            the attribute name
     * @return the attribute value if found, otherwise null.
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    protected <T> T getDocumentAttribute(Class<T> clazz, String name) throws UnifyException {
        Document document = getRequestContextUtil().getRequestDocument();
        if (document != null) {
            return (T) document.getAttribute(name);
        }
        return null;
    }

    /**
     * Sets the value of attribute in the page context associated with this
     * controller.
     * 
     * @param name
     *            the attribute name
     * @param value
     *            the value to set
     */
    protected void setPageAttribute(String name, Object value) {
        page.setAttribute(name, value);
    }

    /**
     * Clears an attribute from the page context associated with this controller.
     * 
     * @param name
     *            the attribute name
     */
    protected Object clearPageAttribute(String name) {
        return page.clearAttribute(name);
    }

    /**
     * Gets the value of attribute from the page context associated with this
     * controller.
     * 
     * @param name
     *            the attribute name
     * @return the attribute value if found, otherwise null.
     */
    protected Object getPageAttribute(String name) {
        return page.getAttribute(name);
    }

    /**
     * Gets the value of attribute, casted to specified type, from the page context
     * associated with this controller.
     * 
     * @param clazz
     *            the type to cast attribute value to
     * @param name
     *            the attribute name
     * @return the attribute value if found, otherwise null.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getPageAttribute(Class<T> clazz, String name) {
        return (T) page.getAttribute(name);
    }

    /**
     * Returns true if page validation is enabled.
     */
    protected boolean isPageValidationEnabled() throws UnifyException {
        return page.isValidationEnabled();
    }

    /**
     * Sets the page validation enabled flag.
     * 
     * @param validationEnabled
     *            the flag to set
     */
    protected void setPageValidationEnabled(boolean validationEnabled) {
        page.setValidationEnabled(validationEnabled);
    }

    /**
     * Launches a task using supplied setup.
     * 
     * @param taskSetup
     *            the task setup
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    protected TaskMonitor launchTask(TaskSetup taskSetup) throws UnifyException {
        return taskLauncher.launchTask(taskSetup);
    }

    /**
     * Hints user in current request with supplied message in INFO mode.
     * 
     * @param messageKey
     *            the message key
     * @param params
     *            the message parameters
     * @throws UnifyException
     *             if an error occurs
     */
    protected void hintUser(String messageKey, Object... params) throws UnifyException {
        getRequestContextUtil().hintUser(MODE.INFO, messageKey, params);
    }

    /**
     * Hints user in current request with supplied message.
     * 
     * @param mode
     *            the hint mode
     * @param messageKey
     *            the message key
     * @param params
     *            the message parameters
     * @throws UnifyException
     *             if an error occurs
     */
    protected void hintUser(MODE mode, String messageKey, Object... params) throws UnifyException {
        getRequestContextUtil().hintUser(mode, messageKey, params);
    }

    /**
     * Returns the current request target object
     * 
     * @param clazz
     *            the target type
     * @throws UnifyException
     *             if an error occurs
     */
    protected <T> T getRequestTarget(Class<T> clazz) throws UnifyException {
        return getRequestContextUtil().getRequestTargetValue(clazz);
    }

    /**
     * Executes on {@link #setPage(Page)}
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void onSetPage() throws UnifyException {

    }

    /**
     * Executes on {@link #index()}
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void onIndexPage() throws UnifyException {

    }

    /**
     * Executes on {@link #openPage()}
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void onOpenPage() throws UnifyException {

    }

    /**
     * Executes on {@link #savePage()}
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void onSavePage() throws UnifyException {

    }

    /**
     * Executes on {@link #closePage()}
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void onClosePage() throws UnifyException {

    }

    /**
     * Returns message result obtained from request.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    private MessageResult getMessageResult() throws UnifyException {
        return getRequestTarget(MessageResult.class);
    }
}
