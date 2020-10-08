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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * A image control with user selection option.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-picture")
public class Picture extends AbstractMultiControl {

    private Control fileControl;

    private Control imageControl;

    private UploadedFile[] uploadedFile;

    @Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        super.populate(transferBlock);
        if (uploadedFile != null && uploadedFile.length > 0) {
            setValue(uploadedFile[0].getData());
        }

        uploadedFile = null;
    }

    public Control getFileCtrl() {
        return fileControl;
    }

    public Control getImageCtrl() {
        return imageControl;
    }

    public UploadedFile[] getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile[] uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        fileControl = (Control) addInternalChildWidget(
                "!ui-fileupload accept:$s{image} binding:uploadedFile selectOnly:true hidden:true");
        StringBuilder sb = new StringBuilder();
        sb.append("!ui-image src:$t{images/camera.png}");
        appendUplAttribute(sb, "binding");
        appendUplAttribute(sb, "styleClass");
        appendUplAttribute(sb, "style");
        imageControl = (Control) addInternalChildWidget(sb.toString(), true, false);
    }
}
