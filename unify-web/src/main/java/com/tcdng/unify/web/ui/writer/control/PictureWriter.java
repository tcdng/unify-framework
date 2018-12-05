/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.Picture;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Picture writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(Picture.class)
@Component("picture-writer")
public class PictureWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		Picture picture = (Picture) widget;
		writer.writeStructureAndContent(picture.getFileCtrl());
		writer.writeStructureAndContent(picture.getImageCtrl());
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		super.doWriteBehavior(writer, widget);

		// Append rigging
		Picture picture = (Picture) widget;
		writer.write("ux.rigPhotoUpload({");
		writer.write("\"pId\":\"").write(picture.getId()).write('"');
		writer.write(",\"pCmdURL\":\"");
		writer.writeCommandURL();
		writer.write('"');
		writer.write(",\"pContId\":\"").write(picture.getContainerId()).write('"');
		writer.write(",\"pFileId\":\"").write(picture.getFileCtrl().getId()).write('"');
		writer.write(",\"pImgId\":\"").write(picture.getImageCtrl().getId()).write('"');
		writer.write(",\"pEditable\":").write(picture.isContainerEditable());
		writer.write("});");
	}

}
