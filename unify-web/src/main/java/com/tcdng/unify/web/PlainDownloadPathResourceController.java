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

package com.tcdng.unify.web;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.web.constant.RealPathConstants;

/**
 * Plain download path resource controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("/plain/resource/downloadpath")
public class PlainDownloadPathResourceController extends PlainRealPathResourceController {

    @Configurable
    private DownloadPathLogger downloadPathLogger;

    public PlainDownloadPathResourceController() {
        super(RealPathConstants.DOWNLOAD_FOLDER);
    }

    @Override
    public void prepareExecution(ClientRequest request) throws UnifyException {
        super.prepareExecution(request);
        if (downloadPathLogger != null) {
            downloadPathLogger.logDownloadAttempt(getResourceName());
        }
    }

}
