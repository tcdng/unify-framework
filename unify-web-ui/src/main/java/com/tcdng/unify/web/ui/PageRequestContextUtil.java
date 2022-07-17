/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.TargetPath;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.data.Hint;
import com.tcdng.unify.web.ui.widget.data.MessageIcon;
import com.tcdng.unify.web.ui.widget.data.ValidationInfo;

/**
 * Page request context utility object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface PageRequestContextUtil extends UnifyComponent {

    /**
     * Extracts request parameters for current request context
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void extractRequestParameters(ClientRequest request) throws UnifyException;

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
     * Sets current request context's popup panel.
     * 
     * @param panel
     *            the popup panel to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequestPopupPanel(Panel panel) throws UnifyException;

    /**
     * Returns current request context's popup panel.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getRequestPopupPanel() throws UnifyException;

    /**
     * Sets the document object for current request.
     * 
     * @param document
     *            the document to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequestDocument(Document document) throws UnifyException;

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
     * Sets current request context's request command tag.
     * 
     * @param cmdTag
     *            the command tag to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequestCommandTag(String cmdTag) throws UnifyException;

    /**
     * Returns request command tag in current request context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getRequestCommandTag() throws UnifyException;

    /**
     * Sets current request context's trigger widget ID.
     * 
     * @param widgetId
     *                 the trigger widget ID to set
     * @throws UnifyException
     *                        if an error occurs
     */
    void setTriggerWidgetId(String widgetId) throws UnifyException;

    /**
     * Returns request trigger widget ID in current request context.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getTriggerWidgetId() throws UnifyException;

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
     * @param targetPath
     *            the path to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setCommandResponsePath(TargetPath targetPath) throws UnifyException;

    /**
     * Returns current request context's command response path.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    TargetPath getCommandResponsePath() throws UnifyException;

    /**
     * Returns request trigger data index from current request context.
     * 
     * @return the return data index otherwise -1
     * @throws UnifyException
     *                        if an error occurs
     */
    int getRequestTriggerDataIndex() throws UnifyException;

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
     * Returns the message icon of confirmation message from current request
     * context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    MessageIcon getRequestConfirmMessageIcon() throws UnifyException;

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
     * Gets the request nonce.
     * 
     * @return the nonce
     * @throws UnifyException
     *                        if an error occurs
     */
    String getNonce() throws UnifyException;
    
    /**
     * Returns true if a nonce has been generated for this request.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isWithNonce() throws UnifyException;
    
    /**
     * Returns true if request is from a remote viewer.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isRemoteViewer() throws UnifyException;

    /**
     * Sets the response path parts information for current request context.
     * 
     * @param respPathParts
     *            the response path parts to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setResponsePathParts(ControllerPathParts respPathParts) throws UnifyException;

    /**
     * Returns the response path parts information for current request context
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    ControllerPathParts getResponsePathParts() throws UnifyException;

    /**
     * Sets the paths of pages closed in this request.
     * 
     * @param pathIdList
     *            the closed pages path list
     * @throws UnifyException
     *             if an error occurs
     */
    void setClosedPagePaths(List<String> pathIdList) throws UnifyException;

    /**
     * Sets the paths of pages closed in this request.
     * 
     * @return the closed pages path list
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getClosedPagePaths() throws UnifyException;

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
    void setResponseRefreshPanels(String... longNames) throws UnifyException;

    /**
     * Returns panels to refresh for current request.
     * 
     * @return panel long names
     * @throws UnifyException
     *             if an error occurs
     */
    String[] getResponseRefreshPanels() throws UnifyException;

    /**
     * Sets the panels to refresh by response to current request.
     * 
     * @param longNames
     *                  the panels
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean setResponseRefreshPanels(Panel... panels) throws UnifyException;

    /**
     * Returns panels to refresh for current request.
     * 
     * @return panels
     * @throws UnifyException
     *                        if an error occurs
     */
    Panel[] getResponseRefreshWidgetPanels() throws UnifyException;

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
     * Clears on-save-content widget IDs
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clearOnSaveContentWidgets() throws UnifyException;

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
     * Clears all hint user in current request.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clearHintUser() throws UnifyException;

    /**
     * Sets the ID of default widget to focus on.
     * 
     * @param id
     *            the widget ID to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setDefaultFocusOnWidgetId(String id) throws UnifyException;

    /**
     * Consider default focus on widget in this request.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    void considerDefaultFocusOnWidget() throws UnifyException;
    
    /**
     * Checks if default widget focus is associated with this request.
     * 
     * @return a true value if widget focus is associated otherwise false.
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isFocusOnWidgetOrDefault() throws UnifyException;

    /**
     * Gets the ID of widget to focus on.
     * 
     * @return the widget ID otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    String getFocusOnWidgetIdOrDefault() throws UnifyException;

    /**
     * Sets the ID of widget to focus on. Accepts only widget ID supplied on first
     * call for request. All subsequent calls are ignored.
     * 
     * @param id
     *            the widget ID to set
     * @return a true value if set otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    boolean setFocusOnWidgetId(String id) throws UnifyException;

    /**
     * Checks if widget focus is associated with this request.
     * 
     * @return a true value if widget focus is associated otherwise false.
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isFocusOnWidget() throws UnifyException;

    /**
     * Gets the ID of widget to focus on.
     * 
     * @return the widget ID otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    String getFocusOnWidgetId() throws UnifyException;

    /**
     * Clears request focus on widget.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clearFocusOnWidget() throws UnifyException;

    /**
     * Checks if no-push widgets are set in this request.
     * 
     * @return a true value if no-push widgets exist in this request.
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isNoPushWidgets() throws UnifyException;

    /**
     * Adds the ID of widget to skip when pushing data to the server to current
     * request
     * 
     * @param id
     *           the widget ID
     * @throws UnifyException
     *                        if an error occurs
     */
    void addNoPushWidgetId(String id) throws UnifyException;

    /**
     * Gets the no-push widget IDs associated with this request.
     * 
     * @return the widget IDs
     * @throws UnifyException
     *                        if an error occurs
     */
    List<String> getNoPushWidgetIds() throws UnifyException;
    
    /**
     * Registers a widget for debounce in current request.
     * 
     * @param widgetId
     *            the widget ID
     * @throws UnifyException
     *             if an error occurs
     */
    void registerWidgetDebounce(String widgetId) throws UnifyException;

    /**
     * Gets and clears the widgets registered for debounce in current request.
     * 
     * @return the registered widget IDs
     * @throws UnifyException
     *             if an error occurs
     */
    Collection<String> getAndClearRegisteredDebounceWidgetIds() throws UnifyException;

    /**
     * Checks if widgets are registered for debounce in this request.
     * 
     * @return a true value if widgets are registered otherwise false.
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isRegisteredDebounceWidgets() throws UnifyException;

    /**
     * Clears all request context page data.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clearRequestContext() throws UnifyException;

    /**
     * Sets content to be reset.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void setContentScrollReset() throws UnifyException;

    /**
     * Returns true if content is to be reset.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isContentScrollReset() throws UnifyException;
}
