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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * File upload button.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-fileuploadbutton")
@UplAttributes({
		@UplAttribute(name = "type", type = FileAttachmentType.class, defaultVal = "wildcard"),
		@UplAttribute(name = "caption", type = String.class, defaultVal = "$m{button.upload}"),
		@UplAttribute(name = "handler", type = String.class) })
public class FileUploadButton extends AbstractMultiControl {

	private Control fileControl;

	private Control buttonControl;

	private UploadedFile[] uploadedFile;

	@Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		super.populate(transferBlock);
		if (uploadedFile != null && uploadedFile.length > 0) {
			Object id = null; // TODO
			UploadedFile _uploadedFile = uploadedFile[0];
			String handler = getUplAttribute(String.class, "handler");
			if (!StringUtils.isBlank(handler)) {
				FileUploadButtonHandler _handler = getComponent(FileUploadButtonHandler.class, handler);
				_handler.save(id, getType(), _uploadedFile.getFilename(), _uploadedFile.getData());
			}
		}

		uploadedFile = null;
	}

	public Control getFileCtrl() {
		return fileControl;
	}

	public Control getButtonCtrl() {
		return buttonControl;
	}

	public UploadedFile[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public FileAttachmentType getType() throws UnifyException {
		return getUplAttribute(FileAttachmentType.class, "type");
	}

	@Override
	public boolean isRefreshesContainer() {
		return true;
	}

	@Override
	protected void doOnPageConstruct() throws UnifyException {
		FileAttachmentType type = getType();
		fileControl = (Control) addInternalChildWidget(
				"!ui-fileupload accept:$s{" + type.name() + "} binding:uploadedFile selectOnly:true hidden:true");
		StringBuilder sb = new StringBuilder();
		sb.append("!ui-button symbol:$s{file} alwaysValueIndex:true");
		appendUplAttribute(sb, "caption");
		appendUplAttribute(sb, "styleClass");
		buttonControl = (Control) addInternalChildWidget(sb.toString());
	}
}
