/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ClientResponse;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.annotation.RequestParameter;
import com.tcdng.unify.web.constant.ReadOnly;
import com.tcdng.unify.web.constant.ResetOnWrite;
import com.tcdng.unify.web.constant.Secured;

/**
 * Abstract base class for page resource controllers.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Singleton(false)
public abstract class AbstractPageResourceController extends AbstractUIController implements PageResourceController {

    @RequestParameter
    private String resourceName;

    @RequestParameter
    private String contentType;

    @RequestParameter
    private boolean attachment;

    @RequestParameter
    private String morsic;

    private Map<String, String> metaDataMap;

    public AbstractPageResourceController(Secured secured) {
        super(secured, ReadOnly.FALSE, ResetOnWrite.FALSE);
    }

    @Override
    public void ensureContextResources(ControllerPathParts controllerPathParts) throws UnifyException {
        
    }

    @Override
	public boolean isRefererRequired() {
		return true;
	}

	@Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        if (!isReadOnly()) {
            DataUtils.setNestedBeanProperty(this, transferBlock.getLongProperty(), transferBlock.getValue(), null);
        }
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

    @Override
    public void reset() throws UnifyException {

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

    @Override
    protected DataTransferParam getDataTransferParam() throws UnifyException {
        return new DataTransferParam(getUIControllerUtil().getResourceControllerInfo(getName()));
    }

    @Override
    protected void doProcess(ClientRequest request, ClientResponse response,
            PageController<?> docPageController, ControllerPathParts docPathParts) throws UnifyException {
        if (!isReadOnly()) {
            DataTransfer dataTransfer = prepareDataTransfer(request);
            populate(dataTransfer);
        }

        prepareExecution();

        for (String key : getMetaDataKeys()) {
            response.setMetaData(key, getMetaData(key));
        }

        if (getContentType() != null) {
            response.setContentType(getContentType());
        }

        String contentType = execute(response.getOutputStream());
        if (!StringUtils.isBlank(contentType)) {
            response.setContentType(contentType);
        }
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
