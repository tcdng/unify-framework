/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.FileUploadButton;
import com.tcdng.unify.web.ui.widget.writer.AbstractAutoRefreshMultiControlWriter;

/**
 * File upload button writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(FileUploadButton.class)
@Component("fileuploadbutton-writer")
public class FileUploadButtonWriter extends AbstractAutoRefreshMultiControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		FileUploadButton fileUploadButton = (FileUploadButton) widget;
		writer.writeStructureAndContent(fileUploadButton.getButtonCtrl());
		writer.writeStructureAndContent(fileUploadButton.getFileCtrl());
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		// Append rigging
		FileUploadButton fileUploadButton = (FileUploadButton) widget;
		writer.beginFunction("ux.rigFileUploadButton");
		writer.writeParam("pId", fileUploadButton.getId());
		writer.writeCommandURLParam("pCmdURL");
		writer.writeParam("pContId", fileUploadButton.getContainerId());
		writer.writeParam("pFileId", fileUploadButton.getFileCtrl().getId());
		writer.writeParam("pBtnId", fileUploadButton.getButtonCtrl().getId());
		writer.writeParam("pEditable", fileUploadButton.isContainerEditable());
		writer.writeParam("pIndex", fileUploadButton.getValueIndex());
		writer.writeParam("pRef", fileUploadButton.getRefs());
		writer.endFunction();
	}

}
