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
package com.tcdng.unify.web.ui.control;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.web.DataTransferBlock;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.AbstractValueListMultiControl;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.data.FileAttachmentInfo;
import com.tcdng.unify.web.ui.data.FileAttachmentsInfo;

/**
 * A file attachment input control used for managing multiple file attachments.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-fileattachment")
@UplAttributes({ @UplAttribute(name = "handler", type = String.class),
        @UplAttribute(name = "viewPath", type = String.class) })
public class FileAttachment extends AbstractValueListMultiControl<ValueStore, FileAttachmentInfo> {

    private FileUpload fileCtrl;

    private Control attachCtrl;

    private Control viewCtrl;

    private Control removeCtrl;

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();

        fileCtrl = (FileUpload) addInternalChildWidget(
                "!ui-fileupload acceptBinding:typeName selectOnly:true hidden:true");
        attachCtrl = (Control) addInternalChildWidget(
                "!ui-button styleClass:$e{fabutton} caption:$m{button.attach} hint:$m{button.attach} debounce:false");
        viewCtrl = (Control) addInternalChildWidget(
                "!ui-button styleClass:$e{fabutton} caption:$m{button.view} hint:$m{button.view} debounce:false");
        removeCtrl = (Control) addInternalChildWidget(
                "!ui-button styleClass:$e{fabutton-alert} caption:$m{button.remove} hint:$m{button.remove} debounce:false");
    }

    @Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        if (transferBlock != null) {
            DataTransferBlock nextBlock = transferBlock.getChildBlock();
            Object value = nextBlock.getValue();
            // Attach
            FileAttachmentsInfo fileAttachmentsInfo = getAttachmentsInfo();
            fileAttachmentsInfo.setSelectedIndex(nextBlock.getItemIndex());
            FileAttachmentInfo fileAttachmentInfo =
                    (FileAttachmentInfo) fileAttachmentsInfo.getSelectedAttachmentInfo();
            UploadedFile uploadedFile = ((UploadedFile[]) value)[0];
            fileAttachmentInfo.setFilename(uploadedFile.getFilename());
            fileAttachmentInfo.setAttachment(uploadedFile.getData());
            fileAttachmentsInfo.attach();

            String handler = getUplAttribute(String.class, "handler");
            if (handler != null) {
                FileAttachmentHandler fileAttachmentHandler = (FileAttachmentHandler) getComponent(handler);
                fileAttachmentHandler.handleAttach(fileAttachmentsInfo.getParentId(), fileAttachmentInfo);
                fileAttachmentInfo.setAttachment(null);
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

        String handler = getUplAttribute(String.class, "handler");
        if (handler != null) {
            FileAttachmentHandler fileAttachmentHandler = (FileAttachmentHandler) getComponent(handler);
            fileAttachmentHandler.handleDetach(fileAttachmentsInfo.getParentId(), fileAttachmentInfo);
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
    protected List<FileAttachmentInfo> getItemList() throws UnifyException {
        FileAttachmentsInfo fileAttachmentsInfo = getAttachmentsInfo();
        if (fileAttachmentsInfo != null) {
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

}
