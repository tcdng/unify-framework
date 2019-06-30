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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.logging.EventType;
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.data.Hint;
import com.tcdng.unify.web.ui.data.ValidationInfo;

/**
 * A request context utility object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface RequestContextUtil extends UnifyComponent {

    /**
     * Sets the page object for current request.
     * 
     * @param page
     *            the page to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequestPage(Page page) throws UnifyException;

    /**
     * Returns the page object for current request.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Page getRequestPage() throws UnifyException;

    /**
     * Sets current request context's popup long name.
     * 
     * @param longName
     *            the popup long name to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequestPopupName(String longName) throws UnifyException;

    /**
     * Returns current request context's popup long name.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getRequestPopupName() throws UnifyException;

    /**
     * Sets the document page controller for the current request.
     * 
     * @param pageController
     *            the document page controller to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequestDocumentController(PageController pageController) throws UnifyException;

    /**
     * Returns document page controller in current request context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    PageController getRequestDocumentController() throws UnifyException;

    /**
     * Returns document in current request context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Document getRequestDocument() throws UnifyException;

    /**
     * Sets current request context's request command.
     * 
     * @param requestCommand
     *            the command to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequestCommand(RequestCommand requestCommand) throws UnifyException;

    /**
     * Returns request command in current request context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    RequestCommand getRequestCommand() throws UnifyException;

    /**
     * Sets current request context's command result mapping.
     * 
     * @param resultMapping
     *            the result mapping to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setCommandResultMapping(String resultMapping) throws UnifyException;

    /**
     * Returns current request context's command result mapping.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getCommandResultMapping() throws UnifyException;

    /**
     * Sets current request context's command response path.
     * 
     * @param path
     *            the path to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setCommandResponsePath(String path) throws UnifyException;

    /**
     * Returns current request context's command response path.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getCommandResponsePath() throws UnifyException;

    /**
     * Extracts request parameters for current request context
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void extractRequestParameters(ClientRequest request) throws UnifyException;

    /**
     * Returns a converted value of the request target from current request context.
     * 
     * @param targetClazz
     *            the type to convert request value to
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T getRequestTargetValue(Class<T> targetClazz) throws UnifyException;

    /**
     * Returns a converted value of the confirmation message from current request
     * context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getRequestConfirmMessage() throws UnifyException;

    /**
     * Returns a converted value of the request confirm message from current request
     * context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getRequestConfirmParam() throws UnifyException;

    /**
     * Returns the request context remote viewer.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getRemoteViewer() throws UnifyException;

    /**
     * Returns true if request is from a remote viewer.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isRemoteViewer() throws UnifyException;

    /**
     * Sets the response page controller information for current request context.
     * 
     * @param info
     *            the info to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setResponsePageControllerInfo(ControllerResponseInfo info) throws UnifyException;

    /**
     * Returns the response page controller information for current request context
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    ControllerResponseInfo getResponsePageControllerInfo() throws UnifyException;

    /**
     * Sets dynamic panel page name to request context.
     * 
     * @param pageName
     *            the page name to set
     * @param parentPageName
     *            the parent page name
     * @throws UnifyException
     *             if an error occurs
     */
    void setDynamicPanelPageName(String pageName, String parentPageName) throws UnifyException;

    /**
     * Returns request context's dynamic panel page name
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getDynamicPanelPageName() throws UnifyException;

    /**
     * Returns request context's dynamic panel parent page name
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getDynamicPanelParentPageName() throws UnifyException;

    /**
     * Clears current dynamic panel page name from request context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clearDynamicPanelPageName() throws UnifyException;

    /**
     * Sets the panels to refresh by response to current request.
     * 
     * @param longNames
     *            the panels long names
     * @throws UnifyException
     *             if an error occurs
     */
    void setResponseRefreshPanels(String[] longNames) throws UnifyException;

    /**
     * Returns panels to refresh for current request.
     * 
     * @return panel long names
     * @throws UnifyException
     *             if an error occurs
     */
    String[] getResponseRefreshPanels() throws UnifyException;

    /**
     * Used to indicate panel state is switched for current request context.
     * 
     * @param panel
     *            the panel which to set flag
     * @throws UnifyException
     *             if an error occurs
     */
    void setPanelSwitchStateFlag(Panel panel) throws UnifyException;

    /**
     * Returns true is panel has been switch in current request context.
     * 
     * @param panel
     *            the panel to check
     * @throws UnifyException
     *             if an error occusr
     */
    boolean isPanelSwitched(Panel panel) throws UnifyException;

    /**
     * Adds aliases to an id in current request context.
     * 
     * @param id
     *            the id
     * @param aliases
     *            the aliases to add
     * @throws UnifyException
     *             if an erro occurs
     */
    void addPageAlias(String id, String... aliases) throws UnifyException;

    /**
     * Returns all page name aliases for specific page name in current request
     * context.
     * 
     * @param pageName
     *            the target page name
     * @throws UnifyException
     *             if an error occurs
     */
    Set<String> getRequestPageNameAliases(String pageName) throws UnifyException;

    /**
     * Returns all page name aliases in current request context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, Set<String>> getRequestPageNameAliases() throws UnifyException;

    /**
     * Adds validation result information to current request for supplied page name.
     * 
     * @param pageName
     *            the validated component page name
     * @param validationInfo
     *            the validation information to set
     * @throws UnifyException
     *             if an error occurs
     */
    void addRequestValidationInfo(String pageName, ValidationInfo validationInfo) throws UnifyException;

    /**
     * Returns all validation result information associated with current request
     * context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Collection<ValidationInfo> getRequestValidationInfoList() throws UnifyException;

    /**
     * Adds an on-save-content widget ID
     * 
     * @param widgetId
     *            the widget ID
     * @throws UnifyException
     *             if an error occurs
     */
    void addOnSaveContentWidget(String widgetId) throws UnifyException;

    /**
     * Returns all on-save-content widget IDs.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getOnSaveContentWidgets() throws UnifyException;

    /**
     * Adds a user hint message to current request in {@link Hint.MODE#INFO} mode
     * using supplied message key and optional parameters.
     * 
     * @param message
     *            the message to hint user
     * @param params
     *            the message parameters
     * @throws UnifyException
     *             if an error occurs
     */
    void hintUser(String message, Object... params) throws UnifyException;

    /**
     * Adds a user hint message to current request using supplied hint mode, message
     * key and optional parameters.
     * 
     * @param mode
     *            the hint mode
     * @param message
     *            the message to hint user
     * @param params
     *            the message parameters
     * @throws UnifyException
     *             if an error occurs
     */
    void hintUser(Hint.MODE mode, String message, Object... params) throws UnifyException;

    /**
     * Returns all user hints associated with current request.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    List<Hint> getUserHints() throws UnifyException;

    /**
     * Logs a user event using event code with optional details.
     * 
     * @param eventCode
     *            the event code
     * @param details
     *            the event details
     * @throws UnifyException
     *             if event code is unknown. if an error occurs.
     */
    void logUserEvent(String eventCode, String... details) throws UnifyException;

    /**
     * Logs a user event using event code with details.
     * 
     * @param eventCode
     *            the event code
     * @param details
     *            the event details
     * @throws UnifyException
     *             if event code is unknown. if an error occurs.
     */
    void logUserEvent(String eventCode, List<String> details) throws UnifyException;

    /**
     * Logs a user event using supplied event and record type.
     * 
     * @param eventType
     *            the event type
     * @param entityClass
     *            the record type
     * @throws UnifyException
     *             If an error occurs.
     */
    void logUserEvent(EventType eventType, Class<? extends Entity> entityClass) throws UnifyException;

    /**
     * Logs a user event with associated record.
     * 
     * @param eventType
     *            the event type
     * @param record
     *            the record object
     * @param isNewRecord
     *            indicates supplied record is new
     * @throws UnifyException
     *             if an error occurs.
     */
    void logUserEvent(EventType eventType, Entity record, boolean isNewRecord) throws UnifyException;

    /**
     * Logs a user event with associated old and new record.
     * 
     * @param eventType
     *            the event type
     * @param oldRecord
     *            the old record
     * @param newRecord
     *            the new record. Can be null
     * @throws UnifyException
     *             if audit type with supplied action is unknown. If an error
     *             occurs.
     */
    <T extends Entity> void logUserEvent(EventType eventType, T oldRecord, T newRecord) throws UnifyException;

    /**
     * Clears all request context data.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clearRequestContext() throws UnifyException;
}
