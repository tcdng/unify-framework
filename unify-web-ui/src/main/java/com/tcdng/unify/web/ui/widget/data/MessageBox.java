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
package com.tcdng.unify.web.ui.widget.data;

/**
 * Message box data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class MessageBox {

	private MessageIcon messageIcon;

	private MessageMode messageMode;

	private MessageBoxCaptions captions;

	private String message;

	private String resultPath;

	public MessageBox(String caption, String message) {
		this(new MessageBoxCaptions(caption), message, null);
	}

	public MessageBox(MessageBoxCaptions captions, String message) {
		this(captions, message, null);
	}

	public MessageBox(String caption, String message, String resultPath) {
		this(MessageIcon.INFO, MessageMode.OK, new MessageBoxCaptions(caption), message, resultPath);
	}

	public MessageBox(MessageBoxCaptions captions, String message, String resultPath) {
		this(MessageIcon.INFO, MessageMode.OK, captions, message, resultPath);
	}

	public MessageBox(MessageIcon messageIcon, MessageMode messageMode, String caption, String message) {
		this(messageIcon, messageMode, new MessageBoxCaptions(caption), message, null);
	}

	public MessageBox(MessageIcon messageIcon, MessageMode messageMode, MessageBoxCaptions captions, String message) {
		this(messageIcon, messageMode, captions, message, null);
	}

	public MessageBox(MessageIcon messageIcon, MessageMode messageMode, String caption, String message,
			String resultPath) {
		this(messageIcon, messageMode, new MessageBoxCaptions(caption), message, resultPath);
	}

	public MessageBox(MessageIcon messageIcon, MessageMode messageMode, MessageBoxCaptions captions, String message,
			String resultPath) {
		this.captions = captions;
		this.messageIcon = messageIcon;
		this.messageMode = messageMode;
		this.message = message;
		this.resultPath = resultPath;
	}

	public MessageIcon getMessageIcon() {
		return messageIcon;
	}

	public int getIconIndex() {
		return messageIcon.getIconIndex();
	}

	public MessageMode getMessageMode() {
		return messageMode;
	}

	public String getCaption() {
		return captions.getMainCaption();
	}

	public String getMessage() {
		return message;
	}

	public String getResultPath() {
		return resultPath;
	}

	public String getOkCode() {
		return MessageResult.OK.code();
	}

	public String getCancelCode() {
		return MessageResult.CANCEL.code();
	}

	public String getYesCode() {
		return MessageResult.YES.code();
	}

	public String getNoCode() {
		return MessageResult.NO.code();
	}

	public String getRetryCode() {
		return MessageResult.RETRY.code();
	}

	public String getOkCaption() {
		return captions.getOkCaption();
	}

	public String getYesCaption() {
		return captions.getYesCaption();
	}

	public String getNoCaption() {
		return captions.getNoCaption();
	}

	public String getRetryCaption() {
		return captions.getRetryCaption();
	}

	public String getCancelCaption() {
		return captions.getCancelCaption();
	}

	public String getOkStyleClass() {
		return captions.getOkStyleClass();
	}

	public String getYesStyleClass() {
		return captions.getYesStyleClass();
	}

	public String getNoStyleClass() {
		return captions.getNoStyleClass();
	}

	public String getRetryStyleClass() {
		return captions.getRetryStyleClass();
	}

	public String getCancelStyleClass() {
		return captions.getCancelStyleClass();
	}

}
