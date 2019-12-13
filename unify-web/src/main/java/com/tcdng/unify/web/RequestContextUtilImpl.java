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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.logging.EventLogger;
import com.tcdng.unify.core.logging.EventType;
import com.tcdng.unify.core.logging.FieldAudit;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.data.Hint;
import com.tcdng.unify.web.ui.data.MessageIcon;
import com.tcdng.unify.web.ui.data.ValidationInfo;

/**
 * Default implementation of application request context utility.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@SuppressWarnings("unchecked")
@Component(WebApplicationComponents.APPLICATION_REQUESTCONTEXTUTIL)
public class RequestContextUtilImpl extends AbstractUnifyComponent implements RequestContextUtil {

    private static final String COMMAND = "COMMAND";

    private static final String COMMAND_RESULT_MAPPING = "COMMAND_RESULT_MAPPING";

    private static final String COMMAND_POSTRESPONSE_PATH = "COMMAND_POSTRESPONSE_PATH";

    private static final String PAGENAME_ALIASES = "PAGENAME_ALIASES";

    private static final String REFRESH_PANEL_LONGNAMES = "REFRESH_PANEL_LONGNAMES";

    private static final String SWITCHED_PANELS = "SWITCHED_PANELS";

    private static final String REQUEST_DOCUMENT = "REQUEST_DOCUMENT";

    private static final String REQUEST_PAGE = "REQUEST_PAGE";

    private static final String REQUEST_POPUP_NAME = "REQUEST_POPUP_NAME";

    private static final String RESPONSE_PATHPARTS = "RESPONSE_PATHPARTS";

    private static final String CLOSED_PAGEPATHS = "CLOSED_PAGEPATHS";
    
    private static final String DYNAMICPANEL_PAGENAME = "DYNAMICPANEL_PAGENAME";

    private static final String USER_HINT_LIST = "USER_HINT_LIST";

    private static final String VALIDATION_INFO_LIST = "VALIDATION_INFO_LIST";

    private static final String ON_SAVE_LIST = "ON_SAVE_LIST";

    private static final String FOCUS_ON_WIDGET = "FOCUS_ON_WIDGET";

    @Configurable(ApplicationComponents.APPLICATION_EVENTSLOGGER)
    private EventLogger eventLogger;

    @Override
    public void setRequestPage(Page page) throws UnifyException {
        setRequestAttribute(REQUEST_PAGE, page);
    }

    @Override
    public Page getRequestPage() throws UnifyException {
        return (Page) getRequestAttribute(REQUEST_PAGE);
    }

    @Override
    public void setRequestPopupName(String longName) throws UnifyException {
        setRequestAttribute(REQUEST_POPUP_NAME, longName);
    }

    @Override
    public String getRequestPopupName() throws UnifyException {
        return (String) getRequestAttribute(REQUEST_POPUP_NAME);
    }

    @Override
    public void setRequestDocument(Document document) throws UnifyException {
        setRequestAttribute(REQUEST_DOCUMENT, document);
    }

    @Override
    public Document getRequestDocument() throws UnifyException {
        return (Document) getRequestAttribute(REQUEST_DOCUMENT);
    }

    @Override
    public void setRequestCommand(RequestCommand requestCommand) throws UnifyException {
        setRequestAttribute(COMMAND, requestCommand);
    }

    @Override
    public RequestCommand getRequestCommand() throws UnifyException {
        return (RequestCommand) getRequestAttribute(COMMAND);
    }

    @Override
    public void setCommandResultMapping(String resultMapping) throws UnifyException {
        setRequestAttribute(COMMAND_RESULT_MAPPING, resultMapping);
    }

    @Override
    public String getCommandResultMapping() throws UnifyException {
        return (String) getRequestAttribute(COMMAND_RESULT_MAPPING);
    }

    @Override
    public void setCommandResponsePath(String path) throws UnifyException {
        setRequestAttribute(COMMAND_POSTRESPONSE_PATH, path);
    }

    @Override
    public String getCommandResponsePath() throws UnifyException {
        return (String) getRequestAttribute(COMMAND_POSTRESPONSE_PATH);
    }

    @Override
    public void extractRequestParameters(ClientRequest request) throws UnifyException {
        setRequestAttribute(RequestParameterConstants.TARGET_VALUE,
                request.getParameter(RequestParameterConstants.TARGET_VALUE));
        setRequestAttribute(RequestParameterConstants.CONFIRM_MSG,
                request.getParameter(RequestParameterConstants.CONFIRM_MSG));
        setRequestAttribute(RequestParameterConstants.CONFIRM_MSGICON,
                request.getParameter(RequestParameterConstants.CONFIRM_MSGICON));
        setRequestAttribute(RequestParameterConstants.CONFIRM_PARAM,
                request.getParameter(RequestParameterConstants.CONFIRM_PARAM));
        setRequestAttribute(RequestParameterConstants.REMOTE_VIEWER,
                request.getParameter(RequestParameterConstants.REMOTE_VIEWER));
    }

    @Override
    public <T> T getRequestTargetValue(Class<T> targetClazz) throws UnifyException {
        return DataUtils.convert(targetClazz, getRequestAttribute(RequestParameterConstants.TARGET_VALUE), null);
    }

    @Override
    public String getRequestConfirmMessage() throws UnifyException {
        return DataUtils.convert(String.class, getRequestAttribute(RequestParameterConstants.CONFIRM_MSG), null);
    }

    @Override
    public MessageIcon getRequestConfirmMessageIcon() throws UnifyException {
        return MessageIcon.getIconByInt(DataUtils.convert(int.class, getRequestAttribute(RequestParameterConstants.CONFIRM_MSGICON), null));
    }

    @Override
    public String getRequestConfirmParam() throws UnifyException {
        return DataUtils.convert(String.class, getRequestAttribute(RequestParameterConstants.CONFIRM_PARAM), null);
    }

    @Override
    public String getRemoteViewer() throws UnifyException {
        return DataUtils.convert(String.class, getRequestAttribute(RequestParameterConstants.REMOTE_VIEWER), null);
    }

    @Override
    public boolean isRemoteViewer() throws UnifyException {
        return StringUtils.isNotBlank(getRemoteViewer());
    }

    @Override
    public void setResponsePathParts(PathParts respPathParts) throws UnifyException {
        setRequestAttribute(RESPONSE_PATHPARTS, respPathParts);
    }
    
    @Override
    public PathParts getResponsePathParts() throws UnifyException {
        return (PathParts) getRequestAttribute(RESPONSE_PATHPARTS);
    }

    @Override
    public void setClosedPagePaths(List<String> pathIdList) throws UnifyException {
        setRequestAttribute(CLOSED_PAGEPATHS, pathIdList);
    }

    @Override
    public List<String> getClosedPagePaths() throws UnifyException {
        List<String> pathIdList= (List<String>) getRequestAttribute(CLOSED_PAGEPATHS);
        if (pathIdList != null) {
            return pathIdList;
        }
        
        return Collections.emptyList();
    }

    @Override
    public void setDynamicPanelPageName(String pageName, String parentPageName) throws UnifyException {
        List<DynamicPanelNames> dynamicPanelNameList =
                (List<DynamicPanelNames>) getRequestAttribute(DYNAMICPANEL_PAGENAME);
        if (dynamicPanelNameList == null) {
            dynamicPanelNameList = new ArrayList<DynamicPanelNames>();
            setRequestAttribute(DYNAMICPANEL_PAGENAME, dynamicPanelNameList);
        }

        dynamicPanelNameList.add(new DynamicPanelNames(pageName, parentPageName));
    }

    @Override
    public String getDynamicPanelPageName() throws UnifyException {
        List<DynamicPanelNames> dynamicPanelNameList =
                (List<DynamicPanelNames>) getRequestAttribute(DYNAMICPANEL_PAGENAME);
        if (DataUtils.isNotBlank(dynamicPanelNameList)) {
            return ((List<DynamicPanelNames>) getRequestAttribute(DYNAMICPANEL_PAGENAME)).get(0).getPageName();
        }

        return null;
    }

    @Override
    public String getDynamicPanelParentPageName() throws UnifyException {
        List<DynamicPanelNames> dynamicPanelNameList =
                (List<DynamicPanelNames>) getRequestAttribute(DYNAMICPANEL_PAGENAME);
        if (DataUtils.isNotBlank(dynamicPanelNameList)) {
            return ((List<DynamicPanelNames>) getRequestAttribute(DYNAMICPANEL_PAGENAME)).get(0).getParentPageName();
        }

        return null;
    }

    @Override
    public void clearDynamicPanelPageName() throws UnifyException {
        List<DynamicPanelNames> dynamicPanelNameList =
                (List<DynamicPanelNames>) getRequestAttribute(DYNAMICPANEL_PAGENAME);
        if (DataUtils.isNotBlank(dynamicPanelNameList)) {
            dynamicPanelNameList.remove(0);
        }
    }

    @Override
    public void setResponseRefreshPanels(String[] longNames) throws UnifyException {
        setRequestAttribute(REFRESH_PANEL_LONGNAMES, longNames);
    }

    @Override
    public String[] getResponseRefreshPanels() throws UnifyException {
        return (String[]) getRequestAttribute(REFRESH_PANEL_LONGNAMES);
    }

    @Override
    public void setPanelSwitchStateFlag(Panel panel) throws UnifyException {
        Set<Panel> switchedPanels = (Set<Panel>) getRequestAttribute(SWITCHED_PANELS);
        if (switchedPanels == null) {
            switchedPanels = new HashSet<Panel>();
            setRequestAttribute(SWITCHED_PANELS, switchedPanels);
        }
        switchedPanels.add(panel);
    }

    @Override
    public boolean isPanelSwitched(Panel panel) throws UnifyException {
        Set<Panel> switchedPanels = (Set<Panel>) getRequestAttribute(SWITCHED_PANELS);
        return switchedPanels != null && switchedPanels.contains(panel);
    }

    @Override
    public void addPageAlias(String id, String... aliases) throws UnifyException {
        Map<String, Set<String>> aliasMap = (Map<String, Set<String>>) getRequestAttribute(PAGENAME_ALIASES);
        if (aliasMap == null) {
            aliasMap = new HashMap<String, Set<String>>();
            setRequestAttribute(PAGENAME_ALIASES, aliasMap);
        }

        Set<String> set = aliasMap.get(id);
        if (set == null) {
            set = new HashSet<String>();
            aliasMap.put(id, set);
        }

        for (String alias : aliases) {
            set.add(alias);
        }
    }

    @Override
    public Set<String> getRequestPageNameAliases(String pageName) throws UnifyException {
        Map<String, Set<String>> childAliasMap = (Map<String, Set<String>>) getRequestAttribute(PAGENAME_ALIASES);
        if (childAliasMap != null && childAliasMap.containsKey(pageName)) {
            return childAliasMap.get(pageName);
        }
        return (Set<String>) Collections.EMPTY_SET;
    }

    @Override
    public Map<String, Set<String>> getRequestPageNameAliases() throws UnifyException {
        return (Map<String, Set<String>>) getRequestAttribute(PAGENAME_ALIASES);
    }

    @Override
    public void addRequestValidationInfo(String pageName, ValidationInfo validationInfo) throws UnifyException {
        Map<String, ValidationInfo> validationInfos =
                (Map<String, ValidationInfo>) this.getRequestAttribute(VALIDATION_INFO_LIST);
        if (validationInfos == null) {
            validationInfos = new LinkedHashMap<String, ValidationInfo>();
            setRequestAttribute(VALIDATION_INFO_LIST, validationInfos);
        }

        ValidationInfo oldValidationInfo = validationInfos.get(pageName);
        if (oldValidationInfo != null) {
            if (oldValidationInfo.isPass() && !validationInfo.isPass()) {
                validationInfos.put(pageName, validationInfo);
            }
        } else {
            validationInfos.put(pageName, validationInfo);
        }
    }

    @Override
    public Collection<ValidationInfo> getRequestValidationInfoList() throws UnifyException {
        Map<String, ValidationInfo> map = (Map<String, ValidationInfo>) getRequestAttribute(VALIDATION_INFO_LIST);
        if (map != null) {
            return map.values();
        }

        return Collections.emptyList();
    }

    @Override
    public void addOnSaveContentWidget(String widgetId) throws UnifyException {
        List<String> widgetList = (List<String>) getRequestAttribute(ON_SAVE_LIST);
        if (widgetList == null) {
            widgetList = new ArrayList<String>();
            setRequestAttribute(ON_SAVE_LIST, widgetList);
        }

        widgetList.add(widgetId);
    }

    @Override
    public List<String> getOnSaveContentWidgets() throws UnifyException {
        List<String> widgetList = (List<String>) getRequestAttribute(ON_SAVE_LIST);
        if (widgetList != null) {
            return widgetList;
        }

        return Collections.emptyList();
    }

    @Override
    public void hintUser(String message, Object... params) throws UnifyException {
        hintUser(Hint.MODE.INFO, message, params);
    }

    @Override
    public void hintUser(Hint.MODE mode, String message, Object... params) throws UnifyException {
        List<Hint> hintList = (List<Hint>) getRequestAttribute(USER_HINT_LIST);
        if (hintList == null) {
            hintList = new ArrayList<Hint>();
            setRequestAttribute(USER_HINT_LIST, hintList);
        }

        hintList.add(new Hint(mode, resolveSessionMessage(message, params)));
    }

    @Override
    public List<Hint> getUserHints() throws UnifyException {
        return (List<Hint>) getRequestAttribute(USER_HINT_LIST);
    }

    @Override
    public void logUserEvent(String eventCode, String... details) throws UnifyException {
        eventLogger.logUserEvent(eventCode, details);
    }

    @Override
    public void logUserEvent(String eventCode, List<String> details) throws UnifyException {
        eventLogger.logUserEvent(eventCode, details);
    }

    @Override
    public void logUserEvent(EventType eventType, Class<? extends Entity> entityClass) throws UnifyException {
        eventLogger.logUserEvent(eventType, entityClass);
    }

    @Override
    public void logUserEvent(EventType eventType, Entity record, boolean isNewRecord) throws UnifyException {
        eventLogger.logUserEvent(eventType, record, isNewRecord);
    }

    @Override
    public <T extends Entity> void logUserEvent(EventType eventType, T oldRecord, T newRecord) throws UnifyException {
        eventLogger.logUserEvent(eventType, oldRecord, newRecord);
    }

    @Override
    public void logUserEvent(EventType eventType, Class<? extends Entity> entityClass, Object recordId,
            List<FieldAudit> fieldAuditList) throws UnifyException {
        eventLogger.logUserEvent(eventType, entityClass, recordId, fieldAuditList);
    }

    @Override
    public boolean setFocusOnWidgetId(String id) throws UnifyException {
        if(!isRequestAttribute(FOCUS_ON_WIDGET)) {
            setRequestAttribute(FOCUS_ON_WIDGET, id);
            return true;
        }

        return false;
    }

    @Override
    public boolean isFocusOnWidget() throws UnifyException {
        return isRequestAttribute(FOCUS_ON_WIDGET);
    }

    @Override
    public String getFocusOnWidgetId() throws UnifyException {
        return (String) getRequestAttribute(FOCUS_ON_WIDGET);
    }

    @Override
    public void clearRequestContext() throws UnifyException {
        setRequestAttribute(RequestParameterConstants.TARGET_VALUE, null);
        setRequestAttribute(RequestParameterConstants.CONFIRM_MSG, null);
        setRequestAttribute(RequestParameterConstants.CONFIRM_MSGICON, null);
        setRequestAttribute(RequestParameterConstants.CONFIRM_PARAM, null);
        setRequestAttribute(COMMAND, null);
        setRequestAttribute(COMMAND_POSTRESPONSE_PATH, null);
        setRequestAttribute(PAGENAME_ALIASES, null);
        setRequestAttribute(REFRESH_PANEL_LONGNAMES, null);
        setRequestAttribute(REQUEST_DOCUMENT, null);
        setRequestAttribute(REQUEST_PAGE, null);
        setRequestAttribute(RESPONSE_PATHPARTS, null);
        setRequestAttribute(USER_HINT_LIST, null);
        setRequestAttribute(VALIDATION_INFO_LIST, null);

    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private class DynamicPanelNames {

        String pageName;

        String parentPageName;

        public DynamicPanelNames(String pageName, String parentPageName) {
            this.pageName = pageName;
            this.parentPageName = parentPageName;
        }

        public String getPageName() {
            return pageName;
        }

        public String getParentPageName() {
            return parentPageName;
        }

    }
}
