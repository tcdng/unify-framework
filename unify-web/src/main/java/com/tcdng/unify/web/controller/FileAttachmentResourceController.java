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
package com.tcdng.unify.web.controller;

import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.AbstractResourceController;
import com.tcdng.unify.web.ui.control.FileAttachmentHandler;
import com.tcdng.unify.web.ui.data.FileAttachmentInfo;
import com.tcdng.unify.web.ui.data.FileAttachmentsInfo;

/**
 * Resource controller for file attachment.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("/resource/fileattachment")
public class FileAttachmentResourceController extends AbstractResourceController {

    public FileAttachmentResourceController() {
        super(false);
    }

    @Override
    public void prepareExecution() throws UnifyException {
        setContentDisposition(getResourceName());
    }

    @Override
    public void execute(OutputStream outputStream) throws UnifyException {
        FileAttachmentsInfo fileAttachmentsInfo = (FileAttachmentsInfo) this.removeSessionAttribute(getResourceName());
        FileAttachmentInfo fileAttachmentInfo = fileAttachmentsInfo.getSelectedAttachmentInfo();
        byte[] data = fileAttachmentInfo.getAttachment();
        if (data == null) {
            String handler = fileAttachmentsInfo.getHandlerName();
            if (handler != null) {
                FileAttachmentHandler fileAttachmentHandler = (FileAttachmentHandler) getComponent(handler);
                FileAttachmentInfo viewFileAttachmentInfo = fileAttachmentHandler
                        .handleView(fileAttachmentsInfo.getParentId(), fileAttachmentInfo);
                data = viewFileAttachmentInfo.getAttachment();
            }
        }

        IOUtils.writeAll(outputStream, data);
    }

}
