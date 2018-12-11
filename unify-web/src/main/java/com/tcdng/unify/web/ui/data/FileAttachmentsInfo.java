/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.web.ui.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.constant.FileAttachmentType;

/**
 * File attachments information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FileAttachmentsInfo {

	private Object parentId;

	private List<FileAttachmentInfo> attachmentInfoList;

	private FileAttachmentType defaultType;

	private String handlerName;

	private int selectedIndex;

	private int maxAutoItems;

	private boolean adhoc;

	/**
	 * Creates new instance of file attachments information in ad-hoc mode.
	 * 
	 * @param defaultType
	 *            the default file attachment type
	 * @param maxAutoItems
	 *            the maximum number of attachments
	 */
	public FileAttachmentsInfo(FileAttachmentType defaultType, int maxAutoItems) {
		this(null, defaultType, maxAutoItems);
	}

	/**
	 * Creates new instance of file attachments information in ad-hoc mode.
	 * 
	 * @param parentId
	 *            the ID of the entity that attachments are attached to. This is
	 *            optional
	 * @param defaultType
	 *            the default file attachment type
	 * @param maxAutoItems
	 *            the maximum number of attachments
	 */
	public FileAttachmentsInfo(Object parentId, FileAttachmentType defaultType, int maxAutoItems) {
		this.parentId = parentId;
		this.defaultType = defaultType;
		this.maxAutoItems = maxAutoItems;
		this.attachmentInfoList = new ArrayList<FileAttachmentInfo>();
		this.adhoc = true;
		this.attach();
	}

	/**
	 * Creates new instance of file attachments information in fixed mode.
	 * 
	 * @param fileAttachmentInfo
	 *            array of file attachment information.
	 */
	public FileAttachmentsInfo(FileAttachmentInfo... fileAttachmentInfo) {
		this(null, Arrays.asList(fileAttachmentInfo));
	}

	/**
	 * Creates new instance of file attachments information in fixed mode.
	 * 
	 * @param fileAttachmentInfoList
	 *            list of file attachment information. Defines fixed list.
	 */
	public FileAttachmentsInfo(List<FileAttachmentInfo> fileAttachmentInfoList) {
		this(null, fileAttachmentInfoList);
	}

	/**
	 * Creates new instance of file attachments information in fixed mode.
	 * 
	 * @param parentId
	 *            the ID of the entity that attachments are attached to. This is
	 *            optional
	 * @param fileAttachmentInfo
	 *            array of file attachment information.
	 */
	public FileAttachmentsInfo(Object parentId, FileAttachmentInfo... fileAttachmentInfo) {
		this(parentId, Arrays.asList(fileAttachmentInfo));
	}

	/**
	 * Creates new instance of file attachments information in fixed mode.
	 * 
	 * @param parentId
	 *            the ID of the entity that attachments are attached to. This is
	 *            optional
	 * @param fileAttachmentInfoList
	 *            list of file attachment information. Defines fixed list.
	 */
	public FileAttachmentsInfo(Object parentId, List<FileAttachmentInfo> fileAttachmentInfoList) {
		this.parentId = parentId;
		this.maxAutoItems = 0;
		this.defaultType = null;
		this.attachmentInfoList = Collections.unmodifiableList(fileAttachmentInfoList);
		this.adhoc = false;
	}

	public void attach() {
		if (this.adhoc) {
			if (!this.attachmentInfoList.isEmpty()) {
				if (this.attachmentInfoList.get(this.attachmentInfoList.size() - 1).isEmpty()) {
					return;
				}
			}

			if (this.maxAutoItems > 0 && this.maxAutoItems <= this.attachmentInfoList.size()) {
				return;
			}

			this.attachmentInfoList.add(new FileAttachmentInfo(defaultType));
		}
	}

	public FileAttachmentInfo detach() {
		FileAttachmentInfo result = null;
		if (this.adhoc) {
			result = this.attachmentInfoList.remove(this.selectedIndex);
			this.attach();
		} else {
			result = this.attachmentInfoList.get(this.selectedIndex);
			result.setFilename(null);
			result.setAttachment(null);
		}

		return result;
	}

	public List<FileAttachmentInfo> getAttachmentInfoList() {
		return attachmentInfoList;
	}

	public FileAttachmentInfo getSelectedAttachmentInfo() {
		return this.attachmentInfoList.get(this.selectedIndex);
	}

	public FileAttachmentInfo getAttachmentInfo(int index) {
		return this.attachmentInfoList.get(index);
	}

	public Object getParentId() {
		return parentId;
	}

	public FileAttachmentType getDefaultType() {
		return defaultType;
	}

	public int size() {
		return this.attachmentInfoList.size();
	}

	public int getMaxAutoItems() {
		return maxAutoItems;
	}

	public boolean isAdhoc() {
		return adhoc;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

}
