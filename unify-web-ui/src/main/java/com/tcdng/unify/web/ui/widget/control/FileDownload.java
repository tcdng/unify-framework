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
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.DownloadFile;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;

/**
 * File download widget.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-filedownload")
@UplAttributes({ @UplAttribute(name = "fileSrc", type = String.class),
        @UplAttribute(name = "fileBinding", type = String.class),
        @UplAttribute(name = "handler", type = String.class),
        @UplAttribute(name = "imageSrc", type = String.class, defaultVal = "$t{images/download.png}"),
        @UplAttribute(name = "caption", type = String.class, defaultVal = "$m{button.download}") })
public class FileDownload extends Button {

    @Action
    public void download() throws UnifyException {
        DownloadFile downloadFile = null;
        String fileSrc = getUplAttribute(String.class, "fileSrc");
        if (StringUtils.isNotBlank(fileSrc)) {
            byte[] data = IOUtils.readFileResourceInputStream(fileSrc);
            String fileName = fileSrc;
            int index = fileName.lastIndexOf('/') + 1;
            if (index > 0) {
                fileName = fileName.substring(index);
            }

            downloadFile = new DownloadFile(MimeType.APPLICATION_OCTETSTREAM, fileName, data);
        } else {
            String fileBinding = getUplAttribute(String.class, "fileBinding");
            if (StringUtils.isNotBlank(fileBinding)) {
                downloadFile = (DownloadFile) getValue(fileBinding);
            } else {
                String handler = getUplAttribute(String.class, "handler");
                if (StringUtils.isNotBlank(handler)) {
                    FileDownloadHandler fileDownloadHandler = (FileDownloadHandler) getComponent(handler);
                    String id = getRequestTarget(String.class);
                    downloadFile = fileDownloadHandler.handleFileDownload(id);
                }
            }
        }

        if (downloadFile != null) {
            setRequestAttribute(UnifyWebRequestAttributeConstants.DOWNLOAD_FILE, downloadFile);
            setCommandResultMapping(ResultMappingConstants.DOWNLOAD_FILE);
        }
    }

}
