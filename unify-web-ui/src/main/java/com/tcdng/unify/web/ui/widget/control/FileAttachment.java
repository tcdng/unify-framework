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
package com.tcdng.unify.web.ui.widget.control;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.data.FileAttachmentInfo;
import com.tcdng.unify.core.data.FileAttachmentsInfo;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.system.SequenceNumberService;
import com.tcdng.unify.core.util.FileUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractValueListMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * A file attachment input control used for managing multiple file attachments.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-fileattachment")
@UplAttributes({ @UplAttribute(name = "handler", type = String.class),
		@UplAttribute(name = "viewPath", type = String.class) })
public class FileAttachment extends AbstractValueListMultiControl<ValueStore, FileAttachmentInfo> {

	private static final String ADHOC_FILENAME_PREFIX = "__adhoc";

	private static final String ADHOC_FILENAME_SEQUENCE = "attachments:adhocnameseq";

	@Configurable
	private SequenceNumberService sequenceNumberService;

	private FileUpload fileCtrl;

	private Control attachCtrl;

	private Control viewCtrl;

	private Control removeCtrl;

	private FileAttachmentHandler handler;

	@Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		if (transferBlock != null) {
			DataTransferBlock nextBlock = transferBlock.getChildBlock();
			Object value = nextBlock.getValue();
			// Attach
			FileAttachmentsInfo fileAttachmentsInfo = getAttachmentsInfo();
			fileAttachmentsInfo.setSelectedIndex(nextBlock.getItemIndex());
			FileAttachmentInfo fileAttachmentInfo = (FileAttachmentInfo) fileAttachmentsInfo
					.getSelectedAttachmentInfo();
			UploadedFile uploadedFile = ((UploadedFile[]) value)[0];
			final byte[] file = uploadedFile.getData();
			fileAttachmentInfo.setFilename(uploadedFile.getFilename());
			fileAttachmentInfo.setAttachment(file);

			if (fileAttachmentsInfo.isAdhoc()) {
				FileAttachmentType type = FileUtils.detectFileAttachmentType(fileAttachmentInfo);
				fileAttachmentInfo.setType(type);
				fileAttachmentInfo.setDescription(fileAttachmentInfo.getFilename());
			}

			if (isWithHandler()) {
				getFileAttachmentHandler().handleAttach(fileAttachmentsInfo.getParentId(), fileAttachmentInfo);
				fileAttachmentInfo.setAttachment(null);
			}
			
			if (!fileAttachmentsInfo.isNormalized()) {
				fileAttachmentsInfo.attach(getNextAdhocName());
			}
		}
	}

	@Action
	public void view() throws UnifyException {
		// Setup view
		FileAttachmentsInfo fileAttachmentsInfo = getAttachmentsInfo();
		fileAttachmentsInfo.setSelectedIndex(getRequestTarget(int.class));
		fileAttachmentsInfo.setHandlerName(getUplAttribute(String.class, "handler"));
		setRequestAttribute(UnifyWebRequestAttributeConstants.FILEATTACHMENTS_INFO, fileAttachmentsInfo);
		setCommandResultMapping(ResultMappingConstants.SHOW_ATTACHMENT);
	}

	@Action
	public void detach() throws UnifyException {
		// Detach
		FileAttachmentsInfo fileAttachmentsInfo = getAttachmentsInfo();
		fileAttachmentsInfo.setSelectedIndex(getRequestTarget(int.class));
		FileAttachmentInfo fileAttachmentInfo = fileAttachmentsInfo.detach();

		if (isWithHandler()) {
			getFileAttachmentHandler().handleDetach(fileAttachmentsInfo.getParentId(), fileAttachmentInfo);
		}

		if (!fileAttachmentsInfo.isNormalized()) {
			fileAttachmentsInfo.attach(getNextAdhocName());
		}

		invalidateValueList();
	}

	@Override
	public void addPageAliases() throws UnifyException {
		addPageAlias(fileCtrl);
	}

	public String getViewPath() throws UnifyException {
		return getUplAttribute(String.class, "viewPath");
	}

	public FileAttachmentsInfo getAttachmentsInfo() throws UnifyException {
		return getValue(FileAttachmentsInfo.class);
	}

	public FileAttachmentHandler getFileAttachmentHandler() throws UnifyException {
		return handler;
	}

	public boolean isWithHandler() {
		return handler != null;
	}

	public FileUpload getFileCtrl() {
		return fileCtrl;
	}

	public Control getAttachCtrl() {
		return attachCtrl;
	}

	public Control getViewCtrl() {
		return viewCtrl;
	}

	public Control getRemoveCtrl() {
		return removeCtrl;
	}

	@Override
	protected void doOnPageConstruct() throws UnifyException {
		fileCtrl = (FileUpload) addInternalChildWidget(
				"!ui-fileupload acceptBinding:typeName selectOnly:true hidden:true");
		attachCtrl = (Control) addInternalChildWidget(
				"!ui-button styleClass:$e{fabutton} caption:$m{button.attach} hint:$m{button.attach} alwaysValueIndex:true debounce:false");
		viewCtrl = (Control) addInternalChildWidget(
				"!ui-button styleClass:$e{fabutton} caption:$m{button.view} hint:$m{button.view} alwaysValueIndex:true debounce:false");
		removeCtrl = (Control) addInternalChildWidget(
				"!ui-button styleClass:$e{fabutton-alert} caption:$m{button.remove} hint:$m{button.remove} alwaysValueIndex:true debounce:false");
		String _handler = getUplAttribute(String.class, "handler");
		if (!StringUtils.isBlank(_handler)) {
			handler = (FileAttachmentHandler) getComponent(_handler);
		}
	}

	@Override
	protected List<FileAttachmentInfo> getItemList() throws UnifyException {
		FileAttachmentsInfo fileAttachmentsInfo = getAttachmentsInfo();
		if (fileAttachmentsInfo != null) {
			if (!fileAttachmentsInfo.isNormalized()) {
				fileAttachmentsInfo.attach(getNextAdhocName());
			}

			return fileAttachmentsInfo.getAttachmentInfoList();
		}

		return null;
	}

	@Override
	protected ValueStore newValue(FileAttachmentInfo item, int index) throws UnifyException {
		return createValueStore(item, index);
	}

	@Override
	protected void onCreateValueList(List<ValueStore> valueList) throws UnifyException {

	}

	private String getNextAdhocName() throws UnifyException {
		return ADHOC_FILENAME_PREFIX + sequenceNumberService.getNextSequenceNumber(ADHOC_FILENAME_SEQUENCE);
	}
}
