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
package com.tcdng.unify.web.ui.controller;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.web.DownloadPathLogger;
import com.tcdng.unify.web.constant.RealPathConstants;

/**
 * Resource controller for fetching file downloading resources from container
 * download path.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("/resource/downloadpath")
public class DownloadPathResourceController extends RealPathResourceController {

    @Configurable
    private DownloadPathLogger downloadPathLogger;
    
    public DownloadPathResourceController() {
        super(RealPathConstants.DOWNLOAD_FOLDER);
    }

    public void setDownloadPathLogger(DownloadPathLogger downloadPathLogger) {
        this.downloadPathLogger = downloadPathLogger;
    }

    @Override
    public void prepareExecution() throws UnifyException {
        super.prepareExecution();
        if (downloadPathLogger != null) {
            downloadPathLogger.logDownloadAttempt(getResourceName());
        }
    }
}
