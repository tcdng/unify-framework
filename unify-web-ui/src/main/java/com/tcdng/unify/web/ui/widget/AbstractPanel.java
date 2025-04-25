/*
 * Copyright 2018-2025 The Code Department.
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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyCoreRequestAttributeConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.TopicEventType;
import com.tcdng.unify.core.data.DownloadFile;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.UnifyWebSessionAttributeConstants;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.PageRequestContextUtil;
import com.tcdng.unify.web.ui.widget.data.MessageBox;
import com.tcdng.unify.web.ui.widget.data.MessageBoxCaptions;
import com.tcdng.unify.web.ui.widget.data.MessageIcon;
import com.tcdng.unify.web.ui.widget.data.MessageMode;
import com.tcdng.unify.web.ui.widget.data.MessageResult;

/**
 * Serves as the base class for a panel.
 * 
 * @author The Code Department
 * @since 4.1
 */
@UplAttributes({
		@UplAttribute(name = "backImageSrc", type = String.class),
		@UplAttribute(name = "backImageSrcBinding", type = String.class),
		@UplAttribute(name = "backImageCover", type = boolean.class, defaultVal = "true"),
		@UplAttribute(name = "refreshPath", type = String.class),
		@UplAttribute(name = "refreshEvery", type = int.class),
		@UplAttribute(name = "refreshOnUserAct", type = boolean.class, defaultVal = "true"),
		@UplAttribute(name = "legend", type = String.class),
		@UplAttribute(name = "alwaysPush", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "hideOnNoComponents", type = boolean.class, defaultVal = "false") })
public abstract class AbstractPanel extends AbstractContainer implements Panel {

	private List<PanelEventListener> listeners;

	private boolean allowRefresh;

	public AbstractPanel() {
		allowRefresh = true;
	}

	@Override
	public void resetState() throws UnifyException {

	}

	@Override
	@Action
	public void switchState() throws UnifyException {
		PageRequestContextUtil util = getRequestContextUtil();
		util.setPanelSwitchStateFlag(this);
		if (getUplAttribute(boolean.class, "alwaysPush")) {
			util.addListItem(UnifyCoreRequestAttributeConstants.ALWAYS_PUSH_COMPOMENT_LIST, getId());
		}
	}

	@Override
	public void addPageAliases() throws UnifyException {
		for (String longName : getLayoutWidgetLongNames()) {
			Widget widget = getWidgetByLongName(longName);
			if (widget.isVisible() || widget.isHidden()) {
				getRequestContextUtil().addPageAlias(getId(), widget.getId());
				if (widget.isPanel()) {
					((Panel) widget).addPageAliases();
				}
			}
		}
	}

	@Override
	public boolean isVisible() throws UnifyException {
		if (getUplAttribute(boolean.class, "hideOnNoComponents")) {
			if (isNoReferencedComponents()) {
				return false;
			}
		}
		return super.isVisible();
	}

	@Override
	public boolean isAllowRefresh() {
		return allowRefresh;
	}

	@Override
	public boolean isSupportReadOnly() {
		return false;
	}

	@Override
	public boolean isSupportDisabled() {
		return false;
	}

	@Override
	public void addEventListener(PanelEventListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<PanelEventListener>();
		}

		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeEventListener(PanelEventListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	@Override
	public String getBackImageSrc() throws UnifyException {
		return getUplAttribute(String.class, "backImageSrc", "backImageSrcBinding");
	}

	@Override
	public boolean isBackImageCover() throws UnifyException {
		return getUplAttribute(boolean.class, "backImageCover");
	}

	@Override
	public String getRefreshPath() throws UnifyException {
		return getUplAttribute(String.class, "refreshPath");
	}

	@Override
	public int getRefreshEvery() throws UnifyException {
		return getUplAttribute(int.class, "refreshEvery");
	}

	@Override
	public boolean isRefreshOnUserAct() throws UnifyException {
		return getUplAttribute(boolean.class, "refreshOnUserAct");
	}

	@Override
	public String getLegend() throws UnifyException {
		return getUplAttribute(String.class, "legend");
	}

	@Override
	public boolean isControl() {
		return false;
	}

	@Override
	public boolean isPanel() {
		return true;
	}

	/**
	 * Sets current client (browser) to listen to topic.
	 * 
	 * @param topic the topic to set
	 * @throws UnifyException if an error occurs
	 */
	protected void setClientListenToTopic(String topic) throws UnifyException {
		getRequestContextUtil().setClientTopic(topic);
	}

	/**
	 * Sets current client (browser) to listen to topic with associated title.
	 * 
	 * @param topic the topic to set
	 * @param title the associated title
	 * @throws UnifyException if an error occurs
	 */
	protected void setClientListenToTopic(String topic, String title) throws UnifyException {
		getRequestContextUtil().setClientTopic(topic + ":" + title);
	}

	/**
	 * Sets current client (browser) to listen to entity.
	 * 
	 * @param entityClass the entity class
	 * @throws UnifyException if an error occurs
	 */
	protected void setClientListenToEntity(Class<? extends Entity> entityClass) throws UnifyException {
		setClientListenToEntity(entityClass, null);
	}

	/**
	 * Sets current client (browser) to listen to entity with associated entity Id.
	 * 
	 * @param entityClass the entity class
	 * @param id          the entity ID
	 * @throws UnifyException if an error occurs
	 */
	protected void setClientListenToEntity(Class<? extends Entity> entityClass, Object id) throws UnifyException {
		getRequestContextUtil().setClientTopic(id != null ? entityClass.getName() + ":" + id : entityClass.getName());
	}

	/**
	 * Adds client topic to current request attribute.
	 * 
	 * @param eventType the event type
	 * @param topic     the topic to set
	 * @throws UnifyException if an error occurs
	 */
	protected void addClientTopicEvent(TopicEventType eventType, String topic) throws UnifyException {
		getRequestContextUtil().addClientTopicEvent(eventType, topic);
	}

	/**
	 * Adds client topic to current request attribute.
	 * 
	 * @param eventType the event type
	 * @param topic     the topic to set
	 * @param title the associated title
	 * @throws UnifyException if an error occurs
	 */
	protected void addClientTopicEvent(TopicEventType eventType, String topic, String title) throws UnifyException {
		getRequestContextUtil().addClientTopicEvent(eventType, topic, title);
	}

	/**
	 * Clears hint user in current request.
	 * 
	 * @throws UnifyException if an error occurs
	 */
	protected void clearHintUser() throws UnifyException {
		getRequestContextUtil().clearHintUser();
	}

	/**
	 * Sets this panel's refresh flag. If true panel would be automatically
	 * refreshed at a frequency based on 'refreshEvery' attribute.
	 * 
	 * @param allowRefresh the refresh flag to set
	 */
	protected void setAllowRefresh(boolean allowRefresh) {
		this.allowRefresh = allowRefresh;
	}

	/**
	 * Notifies all listeners of event.
	 * 
	 * @param eventCode the event code
	 */
	protected void notifyListeners(String eventCode) throws UnifyException {
		if (listeners != null) {
			for (PanelEventListener listener : listeners) {
				listener.notify(this, eventCode);
			}
		}
	}

	protected void setCommandIndexPage() throws UnifyException {
		setCommandResultMapping(ResultMappingConstants.INDEX);
	}

	protected void setCommandOpenPage() throws UnifyException {
		setCommandResultMapping(ResultMappingConstants.OPEN);
	}

	protected void setCommandOpenPath(String path) throws UnifyException {
		setRequestAttribute(UnifyWebRequestAttributeConstants.COMMAND_POSTRESPONSE_PATH, path);
		setCommandResultMapping(ResultMappingConstants.POST_RESPONSE);
	}

	/**
	 * Sets up a file for download in current request context and returns a file
	 * download response.
	 * 
	 * @param downloadFile the file download object
	 * @param hidePopup    the hide popup flag
	 * @throws UnifyException if an error occurs
	 */
	protected void fileDownloadResult(DownloadFile downloadFile, boolean hidePopup) throws UnifyException {
		setRequestAttribute(UnifyWebRequestAttributeConstants.DOWNLOAD_FILE, downloadFile);
		if (hidePopup) {
			setCommandResultMapping(ResultMappingConstants.DOWNLOAD_FILE_HIDE_POPUP);
		} else {
			setCommandResultMapping(ResultMappingConstants.DOWNLOAD_FILE);
		}
	}

	protected void showMessageBox(String message) throws UnifyException {
		showMessageBox(MessageIcon.INFO, MessageMode.OK, "$m{messagebox.message}", message, null);
	}

	protected void showMessageBox(MessageIcon messageIcon, MessageMode messageMode, String message)
			throws UnifyException {
		showMessageBox(messageIcon, messageMode, "$m{messagebox.message}", message, null);
	}

	protected void showMessageBox(String message, String actionPath) throws UnifyException {
		showMessageBox(MessageIcon.INFO, MessageMode.OK, "$m{messagebox.message}", message, actionPath);
	}

	protected void showMessageBox(String caption, String message, String fullActionPath) throws UnifyException {
		showMessageBox(MessageIcon.INFO, MessageMode.OK, caption, message, fullActionPath);
	}

	protected void showMessageBox(
			MessageBoxCaptions captions, String message, String fullActionPath) throws UnifyException {
		showMessageBox(MessageIcon.INFO, MessageMode.OK, captions, message, fullActionPath);
	}

	protected void showMessageBox(MessageIcon messageIcon, MessageMode messageMode, String caption, String message,
			String fullActionPath) throws UnifyException {
		caption = resolveSessionMessage(caption);
		showMessageBox(messageIcon, messageMode, new MessageBoxCaptions(caption), message, fullActionPath);
	}

	protected void showMessageBox(MessageIcon messageIcon, MessageMode messageMode, MessageBoxCaptions captions,
			String message, String fullActionPath) throws UnifyException {
		if (StringUtils.isBlank(fullActionPath)) {
			fullActionPath = ReservedPageControllerConstants.COMMONUTILITIES + "/hidePopup";
		}

		message = resolveSessionMessage(message);
		setSessionAttribute(UnifyWebSessionAttributeConstants.MESSAGEBOX,
				new MessageBox(messageIcon, messageMode, captions, message, fullActionPath));
		setCommandResultMapping("showapplicationmessage");
	}

	protected MessageResult getMessageResult() throws UnifyException {
		return getRequestTarget(MessageResult.class);
	}

	protected String getActionFullPath(String actionName) throws UnifyException {
		return resolveRequestPage().getPathId() + actionName;
	}

	protected String getCommandFullPath(String actionName) throws UnifyException {
		return resolveRequestPage().getPathId() + "/command?req_cmd=" + getId() + "->" + actionName;
	}
}
