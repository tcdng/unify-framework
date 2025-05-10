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
package com.tcdng.unify.web.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.TopicEventType;
import com.tcdng.unify.core.data.Parameters;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ClientResponse;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.TargetPath;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.data.TopicEvent;
import com.tcdng.unify.web.ui.constant.PageRequestParameterConstants;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.data.Hint;
import com.tcdng.unify.web.ui.widget.data.Hints;
import com.tcdng.unify.web.ui.widget.data.MessageIcon;
import com.tcdng.unify.web.ui.widget.data.ValidationInfo;

/**
 * Default implementation of application page request context utility.
 * 
 * @author The Code Department
 * @since 4.1
 */
@SuppressWarnings("unchecked")
@Component(WebUIApplicationComponents.APPLICATION_PAGEREQUESTCONTEXTUTIL)
public class PageRequestContextUtilImpl extends AbstractUnifyComponent implements PageRequestContextUtil {

    private static final String COMMAND = "COMMAND";

    public static final String COMMAND_TAG = "COMMAND_TAG";

    private static final String COMMAND_RESULT_MAPPING = "COMMAND_RESULT_MAPPING";

    private static final String COMMAND_POSTRESPONSE_PATH = "COMMAND_POSTRESPONSE_PATH";

    private static final String TRIGGER_WIDGETID = "TRIGGER_WIDGETID";

    private static final String PAGENAME_ALIASES = "PAGENAME_ALIASES";

    private static final String REFRESH_PANEL_LONGNAMES = "REFRESH_PANEL_LONGNAMES";

    private static final String REFRESH_PANELS = "REFRESH_PANELS";

    private static final String SWITCHED_PANELS = "SWITCHED_PANELS";

    private static final String IGNORE_PANEL_SWITCHED = "IGNORE_PANEL_SWITCHED";

    private static final String REQUEST_DOCUMENT = "REQUEST_DOCUMENT";

    private static final String REQUEST_PAGE = "REQUEST_PAGE";

    private static final String CONTENT_PAGE = "CONTENT_PAGE";

    private static final String REQUEST_POPUP_NAME = "REQUEST_POPUP_NAME";

    private static final String REQUEST_POPUP_PANEL = "REQUEST_POPUP_PANEL";

    private static final String REQUEST_NONCE = "REQUEST_NONCE";
   
    private static final String REQUEST_PATHPARTS = "REQUEST_PATHPARTS";

    private static final String RESPONSE_PATHPARTS = "RESPONSE_PATHPARTS";

    private static final String CLOSED_PAGEPATHS = "CLOSED_PAGEPATHS";
    
    private static final String DYNAMICPANEL_PAGENAME = "DYNAMICPANEL_PAGENAME";

    private static final String USER_HINT_LIST = "USER_HINT_LIST";

    private static final String VALIDATION_INFO_LIST = "VALIDATION_INFO_LIST";

    private static final String ON_SAVE_LIST = "ON_SAVE_LIST";
    
    private static final String FOCUS_ON_WIDGET = "FOCUS_ON_WIDGET";

    private static final String CONSIDER_DEFAULT_FOCUS = "CONSIDER_DEFAULT_FOCUS";

    private static final String DEFAULT_FOCUS_ON_WIDGET = "PAGEREQUEST.DEFAULT_FOCUS_ON_WIDGET";

    private static final String CONTENT_SCROLL_RESET = "CONTENT_SCROLL_RESET";

    private static final String LOW_LATENCY_REQUEST = "LOW_LATENCY_REQUEST";

    private static final String DEBOUNCE_WIDGET = "DEBOUNCE_WIDGET";

    private static final String NO_PUSH_WIDGET_ID_LIST = "NO_PUSH_WIDGET_ID_LIST";

    private static final String CLIENT_TOPIC = "CLIENT_TOPIC";

    private static final String CLIENT_TOPIC_EVENTS = "CLIENT_TOPIC_EVENTS";

    private static final String CLIENT_RESPONSE = "CLIENT_RESPONSE";

    private static final String CLIENT_REQUEST = "CLIENT_REQUEST";
    
    @Override
    public void setRequestPage(Page page) throws UnifyException {
        setRequestAttribute(REQUEST_PAGE, page);
    }

    @Override
    public Page getRequestPage() throws UnifyException {
        return (Page) getRequestAttribute(REQUEST_PAGE);
    }

    @Override
	public void setContentPage(Page page) throws UnifyException {
        setRequestAttribute(CONTENT_PAGE, page);
	}

	@Override
	public Page getContentPage() throws UnifyException {
        return (Page) getRequestAttribute(CONTENT_PAGE);
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
    public void setRequestPopupPanel(Panel panel) throws UnifyException {
        setRequestAttribute(REQUEST_POPUP_PANEL, panel);
    }

    @Override
    public Panel getRequestPopupPanel() throws UnifyException {
        return (Panel) getRequestAttribute(REQUEST_POPUP_PANEL);
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
    public void setRequestCommandTag(String cmdTag) throws UnifyException {
        setRequestAttribute(COMMAND_TAG, cmdTag);
    }

    @Override
    public String getRequestCommandTag() throws UnifyException {
        return (String) getRequestAttribute(COMMAND_TAG);
    }

    @Override
    public void setTriggerWidgetId(String widgetId) throws UnifyException {
        setRequestAttribute(TRIGGER_WIDGETID, widgetId);
    }

    @Override
    public String getTriggerWidgetId() throws UnifyException {
        return (String) getRequestAttribute(TRIGGER_WIDGETID);
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
	public boolean isWithCommandResultMapping() throws UnifyException {
		return isRequestAttribute(COMMAND_RESULT_MAPPING);
	}

	@Override
    public void setCommandResponsePath(TargetPath targetPath) throws UnifyException {
        setRequestAttribute(COMMAND_POSTRESPONSE_PATH, targetPath);
    }

    @Override
    public TargetPath getCommandResponsePath() throws UnifyException {
        return (TargetPath) getRequestAttribute(COMMAND_POSTRESPONSE_PATH);
    }

    @Override
    public int getRequestTriggerDataIndex() throws UnifyException {
        String widgetId = (String) getRequestAttribute(TRIGGER_WIDGETID);
        if (widgetId != null) {
            int dindex = widgetId.lastIndexOf('d');
            if (dindex > 0) {
                try {
                    return Integer.parseInt(widgetId.substring(dindex + 1));
                } catch (Exception e) {
                }
            }
        }

        return -1;
    }

    @Override
    public <T> T getRequestTargetValue(Class<T> targetClazz) throws UnifyException {
        return DataUtils.convert(targetClazz, getRequestAttribute(PageRequestParameterConstants.TARGET_VALUE));
    }

    @Override
    public String getRequestConfirmMessage() throws UnifyException {
        return DataUtils.convert(String.class, getRequestAttribute(PageRequestParameterConstants.CONFIRM_MSG));
    }

    @Override
    public MessageIcon getRequestConfirmMessageIcon() throws UnifyException {
        return MessageIcon.getIconByInt(DataUtils.convert(int.class, getRequestAttribute(PageRequestParameterConstants.CONFIRM_MSGICON)));
    }

    @Override
    public String getRequestConfirmParam() throws UnifyException {
        return DataUtils.convert(String.class, getRequestAttribute(PageRequestParameterConstants.CONFIRM_PARAM));
    }

    @Override
    public String getRemoteViewer() throws UnifyException {
        return DataUtils.convert(String.class, getRequestAttribute(RequestParameterConstants.REMOTE_VIEWER));
    }

    @Override
    public String getNonce() throws UnifyException {
        String nonce = (String) getRequestAttribute(REQUEST_NONCE);
        if (nonce == null) {
            String base = Long.toString(new Date().getTime());
            nonce = Base64.encodeBase64String(base.getBytes());
            setRequestAttribute(REQUEST_NONCE, nonce);
        }
        
        return nonce;
    }

    @Override
    public boolean isWithNonce() throws UnifyException {
        return getRequestAttribute(REQUEST_NONCE) != null;
    }

    @Override
    public boolean isRemoteViewer() throws UnifyException {
        return StringUtils.isNotBlank(getRemoteViewer());
    }

    @Override
    public void setRequestPathParts(ControllerPathParts reqPathParts) throws UnifyException {
        setRequestAttribute(REQUEST_PATHPARTS, reqPathParts);
    }
    
    @Override
    public ControllerPathParts getRequestPathParts() throws UnifyException {
        return (ControllerPathParts) getRequestAttribute(REQUEST_PATHPARTS);
    }

    @Override
    public void setResponsePathParts(ControllerPathParts respPathParts) throws UnifyException {
        setRequestAttribute(RESPONSE_PATHPARTS, respPathParts);
    }
    
    @Override
    public ControllerPathParts getResponsePathParts() throws UnifyException {
        return (ControllerPathParts) getRequestAttribute(RESPONSE_PATHPARTS);
    }

    @Override
    public void setClosedPagePaths(List<String> pathIdList) throws UnifyException {
        setRequestAttribute(CLOSED_PAGEPATHS, pathIdList);
    }

    @Override
	public void setSystemErrorRecoveryPath(String path) throws UnifyException {
		setSessionAttribute(UnifyWebRequestAttributeConstants.SYSTEM_ERROR_RECOVERY_PATH, path);
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

        dynamicPanelNameList.add(0, new DynamicPanelNames(pageName, parentPageName));
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
    public void setResponseRefreshPanels(String... longNames) throws UnifyException {
    	List<String> list = new ArrayList<String>(Arrays.asList(longNames));
        setRequestAttribute(REFRESH_PANEL_LONGNAMES, list);
    }

    @Override
    public List<String> getResponseRefreshPanels() throws UnifyException {
        return (List<String>) getRequestAttribute(REFRESH_PANEL_LONGNAMES);
    }

    @Override
	public boolean removeResponseRefreshPanel(String longName) throws UnifyException {
    	List<String> list = getResponseRefreshPanels();
		return list != null && list.remove(longName);
	}

	@Override
    public boolean setResponseRefreshPanels(Panel... panels) throws UnifyException {
        for (Panel panel : panels) {
            if (panel == null) {
                return false;
            }
        }
        
        setRequestAttribute(REFRESH_PANELS, panels);
        return true;
    }

    @Override
    public Panel[] getResponseRefreshWidgetPanels() throws UnifyException {
        return (Panel[]) getRequestAttribute(REFRESH_PANELS);
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
	public void setIgnorePanelSwitched(boolean ignorePanelSwitched) throws UnifyException {
		setRequestAttribute(IGNORE_PANEL_SWITCHED, ignorePanelSwitched);
	}

	@Override
    public boolean isPanelSwitched(Panel panel) throws UnifyException {
		if (getRequestAttribute(boolean.class, IGNORE_PANEL_SWITCHED)) {
			return false;
		}
		
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
    public void clearOnSaveContentWidgets() throws UnifyException {
        removeRequestAttribute(ON_SAVE_LIST);
    }

    @Override
    public void hintUser(String message, Object... params) throws UnifyException {
        hintUser(Hint.MODE.INFO, message, params);
    }

    @Override
    public void hintUser(Hint.MODE mode, String message, Object... params) throws UnifyException {
        Hints hints = (Hints) getRequestAttribute(USER_HINT_LIST);
        if (hints == null) {
            hints = new Hints();
            setRequestAttribute(USER_HINT_LIST, hints);
        }

        hints.add(mode, resolveSessionMessage(message, params));
    }

    @Override
    public Hints getUserHints() throws UnifyException {
        return (Hints) getRequestAttribute(USER_HINT_LIST);
    }

    @Override
    public void clearHintUser() throws UnifyException {
        removeRequestAttribute(USER_HINT_LIST); 
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
    public void considerDefaultFocusOnWidget() throws UnifyException {
        setRequestAttribute(CONSIDER_DEFAULT_FOCUS, Boolean.TRUE);
    }

    @Override
    public void setDefaultFocusOnWidgetId(String id) throws UnifyException {
        setSessionAttribute(DEFAULT_FOCUS_ON_WIDGET, id);        
    }

    @Override
    public boolean isFocusOnWidgetOrDefault() throws UnifyException {
        return isFocusOnWidget()
                || (isRequestAttribute(CONSIDER_DEFAULT_FOCUS) && isSessionAttribute(DEFAULT_FOCUS_ON_WIDGET));
    }

    @Override
    public String getFocusOnWidgetIdOrDefault() throws UnifyException {
        if (isFocusOnWidget()) {
            return getFocusOnWidgetId();
        }
        
        return (String) getSessionAttribute(DEFAULT_FOCUS_ON_WIDGET);
    }

    @Override
    public void clearFocusOnWidget() throws UnifyException {
        removeRequestAttribute(FOCUS_ON_WIDGET);       
    }

	@Override
	public boolean isWithClientTopic() throws UnifyException {
		return isRequestAttribute(CLIENT_TOPIC);
	}

	@Override
	public String getClientTopic() throws UnifyException {
        return getRequestAttribute(String.class, CLIENT_TOPIC);
	}

	@Override
	public void setClientTopic(String topic) throws UnifyException {
		setRequestAttribute(CLIENT_TOPIC, topic);
	}

	@Override
	public void setClientResponse(ClientResponse response) throws UnifyException {
		setRequestAttribute(CLIENT_RESPONSE, response);
	}

	@Override
	public ClientResponse getClientResponse() throws UnifyException {
        return getRequestAttribute(ClientResponse.class, CLIENT_RESPONSE);
	}

	@Override
	public void setClientRequest(ClientRequest request) throws UnifyException {
		setRequestAttribute(CLIENT_REQUEST, request);
	}

	@Override
	public ClientRequest getClientRequest() throws UnifyException {
        return getRequestAttribute(ClientRequest.class, CLIENT_REQUEST);
	}

	@Override
	public void addClientTopicEvent(TopicEventType eventType, String topic, String title) throws UnifyException {
		addClientTopicEvent(eventType, topic + ":" + title);
	}

	@Override
	public void addClientTopicEvent(TopicEventType eventType, String topic) throws UnifyException {
		List<TopicEvent> events = getRequestAttribute(List.class, CLIENT_TOPIC_EVENTS);
		if (events == null) {
			synchronized(this) {
				events = getRequestAttribute(List.class, CLIENT_TOPIC_EVENTS);
				if (events == null) {
					events = new ArrayList<TopicEvent>();
					setRequestAttribute(CLIENT_TOPIC_EVENTS, events);
				}
			}
		}
		
		events.add(new TopicEvent(eventType, topic));
	}

	@Override
	public List<TopicEvent> getClientTopicEvents() throws UnifyException {
		return getRequestAttribute(List.class, CLIENT_TOPIC_EVENTS);
	}

	@Override
	public boolean isWithClientTopicEvent() throws UnifyException {
		return isRequestAttribute(CLIENT_TOPIC_EVENTS);
	}

	@Override
    public boolean isNoPushWidgets() throws UnifyException {
        return getRequestAttribute(NO_PUSH_WIDGET_ID_LIST) != null;
    }

    @Override
    public void addNoPushWidgetId(String id) throws UnifyException {
        List<String> list = (List<String>) getRequestAttribute(NO_PUSH_WIDGET_ID_LIST);
        if (list == null) {
            list =  new ArrayList<String>();
            setRequestAttribute(NO_PUSH_WIDGET_ID_LIST, list);
        }
        
        list.add(id);
    }

    @Override
    public List<String> getNoPushWidgetIds() throws UnifyException {
        return (List<String>) getRequestAttribute(NO_PUSH_WIDGET_ID_LIST);
    }

    @Override
    public void addListItem(String listName, String item) throws UnifyException {
        List<String> list = (List<String>) getRequestAttribute(listName);
        if (list == null) {
            list = new ArrayList<String>();
            setRequestAttribute(listName, list);
        }
        
        list.add(item);
    }

    @Override
	public void addListItem(String listName, List<String> items) throws UnifyException {
        List<String> list = (List<String>) getRequestAttribute(listName);
        if (list == null) {
            list = new ArrayList<String>();
            setRequestAttribute(listName, list);
        }
        
        list.addAll(items);
	}

	@Override
    public void registerWidgetDebounce(String widgetId) throws UnifyException {
        Collection<String> widgetIds = (Collection<String>) getRequestAttribute(DEBOUNCE_WIDGET);
        if (widgetIds == null) {
            widgetIds = new ArrayList<String>();
            setRequestAttribute(DEBOUNCE_WIDGET, widgetIds);
        }
        
        widgetIds.add(widgetId);
    }

    @Override
    public Collection<String> getAndClearRegisteredDebounceWidgetIds() throws UnifyException {
        return (Collection<String>) removeRequestAttribute(DEBOUNCE_WIDGET);
    }

    @Override
    public boolean isRegisteredDebounceWidgets() throws UnifyException {
        return getRequestAttribute(DEBOUNCE_WIDGET) != null;
    }

    @Override
    public void setContentScrollReset() throws UnifyException {
        setRequestAttribute(CONTENT_SCROLL_RESET, Boolean.TRUE);
    }

    @Override
    public boolean isContentScrollReset() throws UnifyException {
        return getRequestAttribute(boolean.class, CONTENT_SCROLL_RESET);
    }

    @Override
	public void setLowLatencyRequest() throws UnifyException {
        setRequestAttribute(LOW_LATENCY_REQUEST, Boolean.TRUE);
	}

	@Override
	public boolean isLowLatencyRequest() throws UnifyException {
        return getRequestAttribute(boolean.class, LOW_LATENCY_REQUEST);
	}

	@Override
    public void clearRequestContext() throws UnifyException {
        setRequestAttribute(PageRequestParameterConstants.TARGET_VALUE, null);
        setRequestAttribute(PageRequestParameterConstants.WINDOW_NAME, null);
        setRequestAttribute(PageRequestParameterConstants.CONFIRM_MSG, null);
        setRequestAttribute(PageRequestParameterConstants.CONFIRM_MSGICON, null);
        setRequestAttribute(PageRequestParameterConstants.CONFIRM_PARAM, null);
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
    public void extractRequestParameters(ClientRequest request) throws UnifyException {
    	Parameters parameters = request.getParameters();
        setRequestAttribute(PageRequestParameterConstants.TARGET_VALUE,
        		parameters.getParam(PageRequestParameterConstants.TARGET_VALUE));
        setRequestAttribute(PageRequestParameterConstants.WINDOW_NAME,
        		parameters.getParam(PageRequestParameterConstants.WINDOW_NAME));
        setRequestAttribute(PageRequestParameterConstants.CONFIRM_MSG,
        		parameters.getParam(PageRequestParameterConstants.CONFIRM_MSG));
        setRequestAttribute(PageRequestParameterConstants.CONFIRM_MSGICON,
        		parameters.getParam(PageRequestParameterConstants.CONFIRM_MSGICON));
        setRequestAttribute(PageRequestParameterConstants.CONFIRM_PARAM,
        		parameters.getParam(PageRequestParameterConstants.CONFIRM_PARAM));
        setRequestAttribute(RequestParameterConstants.REMOTE_VIEWER,
        		parameters.getParam(RequestParameterConstants.REMOTE_VIEWER));
        setRequestAttribute(RequestParameterConstants.REMOTE_SESSION_ID,
        		parameters.getParam(RequestParameterConstants.REMOTE_SESSION_ID));
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
