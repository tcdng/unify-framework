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
package com.tcdng.unify.web;

import java.text.MessageFormat;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.data.DownloadFile;
import com.tcdng.unify.core.task.TaskLauncher;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskSetup;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.DataTransferWidget;
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.Page;
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
@Singleton
public abstract class AbstractPageController<T extends PageBean> extends AbstractUIController
        implements PageController<T> {

    @Configurable
    private TaskLauncher taskLauncher;

    private Class<T> pageBeanClass;

    public AbstractPageController(Class<T> pageBeanClass) {
        this(pageBeanClass, false, false, false);
    }

    public AbstractPageController(Class<T> pageBeanClass, boolean secured, boolean readOnly, boolean resetOnWrite) {
        super(secured, readOnly, resetOnWrite);
        this.pageBeanClass = pageBeanClass;
    }

    @Override
    public void reset() throws UnifyException {
        getPageBean().reset();
    }

    @Override
    public final ControllerType getType() {
        return ControllerType.PAGE_CONTROLLER;
    }

    @Override
    public final Class<T> getPageBeanClass() {
        return pageBeanClass;
    }

    @Override
    public final Page getPage() throws UnifyException {
        return getRequestContextUtil().getRequestPage();
    }

    @Override
    public final void initPage() throws UnifyException {
        onInitPage();
    }

    @Action
    public final String indexPage() throws UnifyException {
        onIndexPage();
        return ResultMappingConstants.INDEX;
    }

    @Action
    @Override
    public final String openPage() throws UnifyException {
        onOpenPage();
        if (getRequestContextUtil().isRemoteViewer()) {
            return ResultMappingConstants.REMOTE_VIEW;
        }

        return ResultMappingConstants.OPEN;
    }

    @Action
    @Override
    public final String savePage() throws UnifyException {
        onSavePage();
        return ResultMappingConstants.SAVE;
    }

    @Action
    @Override
    public final String closePage() throws UnifyException {
        onClosePage();
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
            Widget widget = getRequestContextUtil().getRequestPage().getWidgetByLongName(Widget.class,
                    requestCommand.getTargetId());
            if (widget.isRelayCommand()) {
                widget = widget.getRelayWidget();
            }

            uiCommandManager.executeCommand(widget, requestCommand.getCommand());
            String commandResultMapping = getRequestContextUtil().getCommandResultMapping();
            if (StringUtils.isNotBlank(commandResultMapping)) {
                return commandResultMapping;
            }
        }

        return ResultMappingConstants.COMMAND;
    }

    @Action
    public String confirm() throws UnifyException {
        RequestContextUtil requestContextUtil = getRequestContextUtil();
        String msg = requestContextUtil.getRequestConfirmMessage();
        String param = requestContextUtil.getRequestConfirmParam();
        if (StringUtils.isNotBlank(param)) {
            msg = MessageFormat.format(msg, param);
        }

        return showMessageBox(requestContextUtil.getRequestConfirmMessageIcon(), MessageMode.YES_NO,
                getSessionMessage("messagebox.confirmation"), msg, "/confirmResult");
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
        DataTransferWidget dataTransferWidget = (DataTransferWidget) getRequestContextUtil().getRequestPage()
                .getWidgetByLongName(transferBlock.getLongName());
        dataTransferWidget.populate(transferBlock);
    }

    /**
     * Gets the page bean from current page object.
     * 
     * @return the page binding bean
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    protected T getPageBean() throws UnifyException {
        return (T) getRequestContextUtil().getRequestPage().getPageBean();
    }

    /**
     * Sets up a file for download in current request context and returns a file
     * download response.
     * 
     * @param downloadFile
     *            the file download object
     * @return {@link ResultMappingConstants#DOWNLOAD_FILE}
     * @throws UnifyException
     *             if an error occurs
     */
    protected String fileDownloadResult(DownloadFile downloadFile) throws UnifyException {
        setRequestAttribute(UnifyWebRequestAttributeConstants.DOWNLOAD_FILE, downloadFile);
        return ResultMappingConstants.DOWNLOAD_FILE;
    }

    /**
     * Sets up a response that shows a message box with default info icon and OK
     * button. {@link MessageBox} value of the session attribute
     * {@link UnifyWebSessionAttributeConstants#MESSAGEBOX}.
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
     * Sets up a response that shows a message box. The message box is backed by the
     * {@link MessageBox} value of the session attribute
     * {@link UnifyWebSessionAttributeConstants#MESSAGEBOX}.
     * 
     * @param messageIcon
     *            the message icon of enumeration type {@link MessageIcon}
     * @param messageMode
     *            the message mode of enumeration type {@link MessageMode}
     * @param message
     *            the message to display
     * @return response to show application message box
     * @throws UnifyException
     *             if an error occurs
     */
    protected String showMessageBox(MessageIcon messageIcon, MessageMode messageMode, String message)
            throws UnifyException {
        return showMessageBox(messageIcon, messageMode, getSessionMessage("messagebox.message"), message, "/hidePopup");
    }

    /**
     * Sets up a response that shows a message box with default info icon and OK
     * button. {@link MessageBox} value of the session attribute
     * {@link UnifyWebSessionAttributeConstants#MESSAGEBOX}.
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
     * {@link UnifyWebSessionAttributeConstants#MESSAGEBOX}.
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
     * {@link UnifyWebSessionAttributeConstants#MESSAGEBOX}.
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
        if (StringUtils.isBlank(actionPath)) {
            actionPath = "/hidePopup";
        }

        message = resolveSessionMessage(message);
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
     *            optional on task failure forward to path
     * @return the show application monitor box result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    protected String launchTaskWithMonitorBox(TaskSetup taskSetup, String caption, String onSuccessPath,
            String onFailurePath) throws UnifyException {
        TaskMonitor taskMonitor = launchTask(taskSetup);
        TaskMonitorInfo taskMonitorInfo =
                new TaskMonitorInfo(taskMonitor, resolveSessionMessage(caption), onSuccessPath, onFailurePath);
        setSessionAttribute(UnifyWebSessionAttributeConstants.TASKMONITORINFO, taskMonitorInfo);
        return "showapplicationtaskmonitor";
    }

    /**
     * Fires action of a page controller in current session.
     * 
     * @param fullActionPath
     *            the target full action path name
     * @return the action result mapping
     * @throws UnifyException
     *             if an error occurs
     */
    protected String fireOtherControllerAction(String fullActionPath) throws UnifyException {
        return ((ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER))
                .executePageController(fullActionPath);
    }

    /**
     * Writes value to another page controller's property in current session.
     * 
     * @param controllerName
     *            the target controller name
     * @param propertyName
     *            the controller property to set
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeOtherControllerProperty(String controllerName, String propertyName, Object value)
            throws UnifyException {
        ((ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER))
                .populatePageBean(controllerName, propertyName, value);
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
    protected <U> U getDocumentAttribute(Class<U> clazz, String name) throws UnifyException {
        Document document = getRequestContextUtil().getRequestDocument();
        if (document != null) {
            return (U) document.getAttribute(name);
        }
        return null;
    }

    /**
     * Sets the value of attribute in current request page.
     * 
     * @param name
     *            the attribute name
     * @param value
     *            the value to set
     */
    protected void setPageAttribute(String name, Object value) throws UnifyException {
        getRequestContextUtil().getRequestPage().setAttribute(name, value);
    }

    /**
     * Clears an attribute from the current request page.
     * 
     * @param name
     *            the attribute name
     * @throws UnifyException
     *             if an error occurs
     */
    protected Object clearPageAttribute(String name) throws UnifyException {
        return getRequestContextUtil().getRequestPage().clearAttribute(name);
    }

    /**
     * Gets the value of attribute from the current request page.
     * 
     * @param name
     *            the attribute name
     * @return the attribute value if found, otherwise null.
     * @throws UnifyException
     *             if an error occurs
     */
    protected Object getPageAttribute(String name) throws UnifyException {
        return getRequestContextUtil().getRequestPage().getAttribute(name);
    }

    /**
     * Gets the value of attribute, casted to specified type, from the current
     * request page.
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
    protected <U> U getPageAttribute(Class<U> clazz, String name) throws UnifyException {
        return (U) getRequestContextUtil().getRequestPage().getAttribute(name);
    }

    /**
     * Returns true if page validation is enabled.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected boolean isPageValidationEnabled() throws UnifyException {
        return getRequestContextUtil().getRequestPage().isValidationEnabled();
    }

    /**
     * Sets the page validation enabled flag.
     * 
     * @param validationEnabled
     *            the flag to set
     * @throws UnifyException
     *             if an error occurs
     */
    protected void setPageValidationEnabled(boolean validationEnabled) throws UnifyException {
        getRequestContextUtil().getRequestPage().setValidationEnabled(validationEnabled);
    }

    protected <U> U getPageWidgetByLongName(Class<U> clazz, String longName) throws UnifyException {
        return getRequestContextUtil().getRequestPage().getWidgetByLongName(clazz, longName);
    }

    protected <U> U getPageWidgetByShortName(Class<U> clazz, String shortName) throws UnifyException {
        return getRequestContextUtil().getRequestPage().getWidgetByShortName(clazz, shortName);
    }

    protected void setPageWidgetDisabled(String shortName, boolean disabled) throws UnifyException {
        getRequestContextUtil().getRequestPage().setWidgetDisabled(shortName, disabled);
    }

    protected boolean isPageWidgetDisabled(String shortName) throws UnifyException {
        return getRequestContextUtil().getRequestPage().isWidgetDisabled(shortName);
    }

    protected void setPageWidgetVisible(String shortName, boolean visible) throws UnifyException {
        getRequestContextUtil().getRequestPage().setWidgetVisible(shortName, visible);
    }

    protected boolean isPageWidgetVisible(String shortName) throws UnifyException {
        return getRequestContextUtil().getRequestPage().isWidgetVisible(shortName);
    }

    protected void setPageWidgetEditable(String shortName, boolean editable) throws UnifyException {
        getRequestContextUtil().getRequestPage().setWidgetEditable(shortName, editable);
    }

    protected boolean isPageWidgetEditable(String shortName) throws UnifyException {
        return getRequestContextUtil().getRequestPage().isWidgetEditable(shortName);
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
    protected <U> U getRequestTarget(Class<U> clazz) throws UnifyException {
        return getRequestContextUtil().getRequestTargetValue(clazz);
    }

    /**
     * Executes on {@link #initPage()}
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void onInitPage() throws UnifyException {

    }

    /**
     * Executes on {@link #indexPage()}
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
