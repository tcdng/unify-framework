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
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.FileUpload;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * File upload writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(FileUpload.class)
@Component("fileupload-writer")
public class FileUploadWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        FileUpload fileUpload = (FileUpload) widget;
        writer.write("<div ");
        writeTagStyleClass(writer, fileUpload);
        boolean isHidden = fileUpload.isHidden();
        if (isHidden) {
            writeTagStyle(writer, "width:0px;height:0px;overflow:hidden;");
        } else {
            writeTagStyle(writer, fileUpload);
        }
        writer.write(">");

        // Actual HTML file control
        if (!isHidden) {
            writer.write("<div style=\"width:0px;height:0px;overflow:hidden;\">");
        }
        writer.write("<input type=\"file\"");
        writeTagId(writer, fileUpload);
        String accept = fileUpload.getAccept();
        if (StringUtils.isNotBlank(accept)) {
            FileAttachmentType fileAttachmentType = FileAttachmentType.fromName(accept);
            if (fileAttachmentType != null && !FileAttachmentType.WILDCARD.equals(fileAttachmentType)) {
                writer.write(" accept=\"").write(fileAttachmentType.mimeType().template()).write('"');
            }
        }

        if (fileUpload.getUplAttribute(boolean.class, "multiple")) {
            writer.write(" multiple");
        }

        writer.write("/>");

        if (!isHidden) {
            writer.write("</div>");
        }

        // Facade
        if (!fileUpload.isHidden()) {
            writer.write("<div style=\"display:flex;width:100%;\">");

            if (!fileUpload.isSelectOnly()) {
                // Display upload button
                writer.write("<button type=\"button\" class=\"fubutton\" id=\"").write(fileUpload.getUploadButtonId());
                writer.write("\" style=\"background: url('");
                writer.writeFileImageContextURL("$t{images/upload.png}");
                writer.write("') no-repeat left 8px center/14px 14px;\">");
                writer.write("<span>");
                writer.writeWithHtmlEscape(fileUpload.getUploadCaption());
                writer.write("</span></button>");
            }

            writer.write("<button type=\"button\" class=\"fsbutton\" id=\"").write(fileUpload.getButtonId())
                    .write("\">");
            writer.writeWithHtmlEscape(fileUpload.getBrowseCaption());
            writer.write("</button>");

            writer.write("<input type=\"text\" class=\"fsspan\" id=\"").write(fileUpload.getSpanId())
                    .write("\" readonly />");

            writer.write("</div>");
        }

        writer.write("</div>");
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		FileUpload fileUpload = (FileUpload) widget;
		if (!fileUpload.isHidden()) {
			// Append rigging
			writer.beginFunction("ux.rigFileUpload");
			writer.writeParam("pId", fileUpload.getId());
			writer.writeParam("pContId", fileUpload.getContainerId());
			writer.writeParam("pBtnId", fileUpload.getButtonId());
			writer.writeParam("pSpanId", fileUpload.getSpanId());
			writer.writeParam("pUpBtnId", fileUpload.getUploadButtonId());
			writer.writeParam("pDisabled", fileUpload.isContainerDisabled());
			writer.writeParam("pSelect", fileUpload.isSelectOnly());
			String uploadPath = fileUpload.getUploadURL();
			if (uploadPath != null) {
				writer.writeContextURLParam("pUploadURL", uploadPath);
			}

			int maxSize = fileUpload.getMaxSize();
			if (maxSize > 0) {
				writer.writeParam("pMaxSize", maxSize);
				writer.writeParam("pMaxMsg", getSessionMessage("fileupload.maxsize", maxSize));
			}
			writer.writeParam("pRef", DataUtils.toArray(String.class, writer.getPostCommandRefs()));
			writer.endFunction();
		}
	}

}
