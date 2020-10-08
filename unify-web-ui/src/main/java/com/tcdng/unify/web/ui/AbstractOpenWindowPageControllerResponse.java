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
package com.tcdng.unify.web.ui;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.data.WebStringWriter;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Abstract open window page controller response.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public abstract class AbstractOpenWindowPageControllerResponse extends AbstractJsonPageControllerResponse {

    public AbstractOpenWindowPageControllerResponse() {
        super("openWindowHdl", false);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        WindowResourceInfo windowResourceInfo = prepareWindowResource();
        setSessionAttribute(windowResourceInfo.getResourceName(), windowResourceInfo.getResourceObject());

        writer.write(",\"openWindow\":");
        writer.useSecondary(128);
        writer.writeContextResourceURL(windowResourceInfo.resourcePath, windowResourceInfo.getContentType(),
                windowResourceInfo.getResourceName(), null, windowResourceInfo.isDownload(), false);
        WebStringWriter urlLsw = writer.discardSecondary();

        writer.writeJsonQuote(urlLsw);
        writer.write(",\"attachment\":").write(windowResourceInfo.isDownload());
    }

    protected abstract WindowResourceInfo prepareWindowResource() throws UnifyException;

    protected class WindowResourceInfo {

        private Object resourceObject;

        private String resourcePath;

        private String resourceName;

        private String contentType;

        private boolean download;

        public WindowResourceInfo(Object resourceObject, String resourcePath, String resourceName, String contentType,
                boolean download) {
            this.resourceObject = resourceObject;
            this.resourcePath = resourcePath;
            this.resourceName = resourceName;
            this.contentType = contentType;
            this.download = download;
        }

        public Object getResourceObject() {
            return resourceObject;
        }

        public String getResourcePath() {
            return resourcePath;
        }

        public String getResourceName() {
            return resourceName;
        }

        public String getContentType() {
            return contentType;
        }

        public boolean isDownload() {
            return download;
        }

    }
}
