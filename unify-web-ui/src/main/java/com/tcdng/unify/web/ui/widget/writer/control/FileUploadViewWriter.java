/*
 * Copyright 2018-2022 The Code Department.
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
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.FileUpload;
import com.tcdng.unify.web.ui.widget.control.FileUploadView;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * File upload view writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(FileUploadView.class)
@Component("fileuploadview-writer")
public class FileUploadViewWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        FileUploadView fileUploadView = (FileUploadView) widget;
        writer.write("<div");
        writeTagAttributes(writer, fileUploadView);
        writer.write("><div style=\"display:table;border-collapse:collapse;width:100%;\">");
        boolean isContainerDisabled = fileUploadView.isContainerDisabled();
        boolean isContainerEditable = fileUploadView.isContainerEditable();
        FileUpload fileCtrl = fileUploadView.getFileCtrl();
        Control attachCtrl = fileUploadView.getAttachCtrl();
        Control viewCtrl = fileUploadView.getViewCtrl();
        Control removeCtrl = fileUploadView.getRemoveCtrl();
        Object uploadId = fileUploadView.getUploadId();
        writer.write("<div style=\"display:table;width:100%;\">");
        writer.write("<div style=\"display:table-row;\">");
        writer.write("<div style=\"display:table-cell;\">");
        writer.write("<div class=\"faaction\">");
        writer.writeStructureAndContent(fileCtrl);

        attachCtrl.setDisabled(isContainerDisabled);
        attachCtrl.setEditable(isContainerEditable);
        writer.writeStructureAndContent(attachCtrl);

        if (uploadId == null) {
            viewCtrl.setDisabled(true);
            viewCtrl.setEditable(false);
            removeCtrl.setDisabled(true);
            removeCtrl.setEditable(false);
        } else {
            viewCtrl.setDisabled(isContainerDisabled);
            viewCtrl.setEditable(isContainerEditable);
            removeCtrl.setDisabled(isContainerDisabled);
            removeCtrl.setEditable(isContainerEditable);
        }

        writer.writeStructureAndContent(viewCtrl);
        writer.writeStructureAndContent(removeCtrl);
        writer.write("</div></div></div></div>");
        writer.write("</div></div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);

        // Append rigging
        FileUploadView fileAttachment = (FileUploadView) widget;
        writer.beginFunction("ux.rigFileUploadView");
        writer.writeParam("pId", fileAttachment.getId());
        writer.writeCommandURLParam("pCmdURL");
        String viewPath = fileAttachment.getViewPath();
        if (viewPath != null) {
            writer.writeContextURLParam("pViewURL", viewPath);
        }

        writer.writeParam("pContId", fileAttachment.getContainerId());
        writer.writeParam("pFileId", fileAttachment.getFileCtrl().getBaseId());
        writer.writeParam("pAttchId", fileAttachment.getAttachCtrl().getBaseId());
        writer.writeParam("pViewId", fileAttachment.getViewCtrl().getBaseId());
        writer.writeParam("pRemId", fileAttachment.getRemoveCtrl().getBaseId());
        writer.writeParam("pEditable", fileAttachment.isContainerEditable());
        writer.writeParam("pRef", DataUtils.toArray(String.class, writer.getPostCommandRefs()));
        writer.endFunction();
    }
}
