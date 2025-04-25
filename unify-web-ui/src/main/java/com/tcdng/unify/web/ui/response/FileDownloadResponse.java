/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.DownloadFile;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * File download response.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("filedownloadresponse")
public class FileDownloadResponse extends AbstractJsonPageControllerResponse {

    public FileDownloadResponse() {
        super("downloadHdl", false);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        DownloadFile downloadFile = (DownloadFile) getRequestAttribute(UnifyWebRequestAttributeConstants.DOWNLOAD_FILE);
        setSessionAttribute(downloadFile.getFilename(), downloadFile.getData());
        writer.write(",\"downloadPath\":\"");
        writer.writeContextResourceURL("/resource/scope", downloadFile.getMimeType().template(),
                downloadFile.getFilename(), null, true, true); // Download is true. Clear on read is
                                                               // true
        writer.write("\"");
    }
}
