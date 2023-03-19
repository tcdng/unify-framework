/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.data.FileAttachmentInfo;

/**
 * A file upload view input control.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-fileuploadview")
@UplAttributes({ @UplAttribute(name = "type", type = FileAttachmentType.class, defaultVal = "wildcard"),
		@UplAttribute(name = "filenameBinding", type = String.class),
		@UplAttribute(name = "handler", type = String.class, mandatory = false),
		@UplAttribute(name = "category", type = String.class),
		@UplAttribute(name = "parentCategory", type = String.class),
		@UplAttribute(name = "parentFieldName", type = String.class),
		@UplAttribute(name = "viewPath", type = String.class) })
public class FileUploadView extends AbstractMultiControl {

	private FileUpload fileCtrl;

	private Control attachCtrl;

	private Control viewCtrl;

	private Control removeCtrl;

	private FileUploadViewHandler handler;

	public String getAttachmentFileName() throws UnifyException {
		if (handler != null) {
			Object uploadId = getUploadId();
			if (uploadId != null) {
				String category = getUplAttribute(String.class, "category");
				FileAttachmentType type = getUplAttribute(FileAttachmentType.class, "type");
				FileAttachmentInfo fileAttachmentInfo = handler.peek(uploadId, category, type);
				return fileAttachmentInfo.getFilename();
			}
		}

		return null;
	}

	@Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		if (transferBlock != null) {
			DataTransferBlock nextBlock = transferBlock.getChildBlock();
			Object value = nextBlock.getValue();
			UploadedFile uploadedFile = ((UploadedFile[]) value)[0];
			if (handler == null) {
				setFilename(uploadedFile.getFilename());
				setValue(uploadedFile.getData());
			} else {
				String category = getUplAttribute(String.class, "category");
				FileAttachmentType type = getUplAttribute(FileAttachmentType.class, "type");
				Object uploadId = handler.save(getUploadId(), category, type, uploadedFile.getFilename(),
						uploadedFile.getData());
				setUploadId(uploadId);
			}
		}
	}

	@Action
	public void view() throws UnifyException {
		if (handler == null) {
			byte[] data = getValue(byte[].class);
			if (data != null) {
				final FileAttachmentType type = getType();
				FileAttachmentInfo fileAttachmentInfo = new FileAttachmentInfo(type);
				String filename = getFilename();
				fileAttachmentInfo.setFilename(!StringUtils.isBlank(filename) ? filename : type.appendDefaultExtension("file"));
				fileAttachmentInfo.setAttachment(data);
				setRequestAttribute(UnifyWebRequestAttributeConstants.FILEATTACHMENTS_INFO, fileAttachmentInfo);
				setCommandResultMapping(ResultMappingConstants.SHOW_ATTACHMENT);
			}
		} else {
			// Setup view
			Object uploadId = getUploadId();
			if (uploadId != null) {
				String category = getUplAttribute(String.class, "category");
				FileAttachmentType type = getUplAttribute(FileAttachmentType.class, "type");
				FileAttachmentInfo fileAttachmentInfo = handler.retrive(uploadId, category, type);
				setRequestAttribute(UnifyWebRequestAttributeConstants.FILEATTACHMENTS_INFO, fileAttachmentInfo);
				setCommandResultMapping(ResultMappingConstants.SHOW_ATTACHMENT);
			}
		}
	}
	
	@Action
	public void detach() throws UnifyException {
		if (handler == null) {
			setFilename(null);
			setValue(null);
		} else {
			// Detach
			Object uploadId = getUploadId();
			Object parentId = null;
			Object valueObject = getValueStore().getValueObject();
			if (valueObject instanceof Entity) {
				parentId = ((Entity) valueObject).getId();
			}

			if (uploadId != null) {
				handler.delete(uploadId, getUplAttribute(String.class, "category"), parentId,
						getUplAttribute(String.class, "parentCategory"),
						getUplAttribute(String.class, "parentFieldName"));
				setUploadId(null);
			}
		}
	}

	@Override
	public void addPageAliases() throws UnifyException {
		addPageAlias(fileCtrl);
	}

	public String getViewPath() throws UnifyException {
		return getUplAttribute(String.class, "viewPath");
	}

	public FileAttachmentType getType() throws UnifyException {
		return getUplAttribute(FileAttachmentType.class, "type");
	}

	public String getFilenameBinding() throws UnifyException {
		return getUplAttribute(String.class, "filenameBinding");
	}

	public Object getUploadId() throws UnifyException {
		return getValue();
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
	public boolean isRefreshesContainer() {
		return true;
	}

	@Override
	protected void doOnPageConstruct() throws UnifyException {
		FileAttachmentType _type = getType();
		fileCtrl = (FileUpload) addInternalChildWidget(
				"!ui-fileupload accept:" + _type + " selectOnly:true hidden:true");
		attachCtrl = (Control) addInternalChildWidget(
				"!ui-button styleClass:$e{fabutton} caption:$m{button.attach} hint:$m{button.attach} debounce:false");
		viewCtrl = (Control) addInternalChildWidget(
				"!ui-button styleClass:$e{fabutton} caption:$m{button.view} hint:$m{button.view} debounce:false");
		removeCtrl = (Control) addInternalChildWidget(
				"!ui-button styleClass:$e{fabutton-alert} caption:$m{button.remove} hint:$m{button.remove} debounce:false");
		String _handler = getUplAttribute(String.class, "handler");
		handler = !StringUtils.isBlank(_handler) ? (FileUploadViewHandler) getComponent(_handler) : null;
	}

	protected void setUploadId(Object uploadId) throws UnifyException {
		setValue(uploadId);
	}

	protected void setFilename(String filename) throws UnifyException {
		String filenameBinding = getFilenameBinding();
		if (!StringUtils.isBlank(filenameBinding)) {
			setValue(filenameBinding, filename);
		}
	}

	protected String getFilename() throws UnifyException {
		String filenameBinding = getFilenameBinding();
		if (!StringUtils.isBlank(filenameBinding)) {
			return getValue(String.class, filenameBinding);
		}

		return null;
	}
}
