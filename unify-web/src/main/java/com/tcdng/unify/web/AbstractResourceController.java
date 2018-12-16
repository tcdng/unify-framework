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
package com.tcdng.unify.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.web.annotation.RequestParameter;

/**
 * Abstract base class for resource request controllers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractResourceController extends AbstractUserInterfaceController implements ResourceController {

    @RequestParameter
    private String resourceName;

    @RequestParameter
    private String contentType;

    @RequestParameter
    private boolean attachment;

    @RequestParameter
    private String morsic;

    private Map<String, String> metaDataMap;

    public AbstractResourceController(boolean secured) {
        super(secured, false);
    }

    @Override
    public ControllerType getType() {
        return ControllerType.RESOURCE_CONTROLLER;
    }

    @Override
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setAttachment(boolean attachment) {
        this.attachment = attachment;
    }

    public String getMorsic() {
        return morsic;
    }

    public void setMorsic(String morsic) {
        this.morsic = morsic;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getMetaDataKeys() {
        if (metaDataMap != null) {
            return metaDataMap.keySet();
        }
        return (Set<String>) Collections.EMPTY_SET;
    }

    @Override
    public String getMetaData(String name) {
        if (metaDataMap != null) {
            return metaDataMap.get(name);
        }
        return null;
    }

    protected void setMetaData(String name, String value) {
        if (metaDataMap == null) {
            metaDataMap = new HashMap<String, String>();
        }

        metaDataMap.put(name, value);
    }

    protected void setContentLength(long contentLength) {
        setMetaData("Content-Length", String.valueOf(contentLength));
    }

    protected void setContentDisposition(String fileName) {
        String disposition = "inline;filename=";
        if (isAttachment()) {
            disposition = "attachment;filename=";
        }

        disposition = disposition + "\"" + fileName + "\"";
        setMetaData("Content-Disposition", disposition);
    }

    protected String getResourceName() {
        return resourceName;
    }

    protected boolean isAttachment() {
        return attachment;
    }
}
