/*
 * Copyright 2018-2020 The Code Department.
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
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.FileUpload;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * File upload writer.
 * 
 * @author Lateef Ojulari
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
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);

        FileUpload fileUpload = (FileUpload) widget;
        if (!fileUpload.isHidden()) {
            // Append rigging
            writer.write("ux.rigFileUpload({");
            writer.write("\"pId\":\"").write(fileUpload.getId()).write('"');
            writer.write(",\"pContId\":\"").write(fileUpload.getContainerId()).write('"');
            writer.write(",\"pBtnId\":\"").write(fileUpload.getButtonId()).write('"');
            writer.write(",\"pSpanId\":\"").write(fileUpload.getSpanId()).write('"');
            writer.write(",\"pUpBtnId\":\"").write(fileUpload.getUploadButtonId()).write('"');
            writer.write(",\"pDisabled\":").write(fileUpload.isContainerDisabled());
            writer.write(",\"pSelect\":").write(fileUpload.isSelectOnly());
            String uploadPath = fileUpload.getUploadURL();
            if (uploadPath != null) {
                writer.write(",\"pUploadURL\":\"").writeContextURL(uploadPath).write('"');
            }

            int maxSize = fileUpload.getMaxSize();
            if (maxSize > 0) {
                writer.write(",\"pMaxSize\":").write(maxSize);
                writer.write(",\"pMaxMsg\":\"").write(getSessionMessage("fileupload.maxsize", maxSize)).write('"');
            }
            writer.write("});");
        }
    }

}
