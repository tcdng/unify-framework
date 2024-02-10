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

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient abstract plain resource controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Singleton(false)
public abstract class AbstractPlainResourceController extends AbstractPlainOutputStreamController {

    private static final String RESOURCE_PARAMETER_NAME = "resource";

    private static final String ATTACHMENT_PARAMETER_NAME = "attachment";

    private String resourceName;

    private boolean attachment;
    
    public AbstractPlainResourceController() {

    }

    public AbstractPlainResourceController(MimeType mimeType) {
        super(mimeType);
    }

    @Override
    protected void prepareExecution(ClientRequest request) throws UnifyException {
        resourceName = request.getParameter(String.class, RESOURCE_PARAMETER_NAME);
        attachment = request.getParameter(boolean.class, ATTACHMENT_PARAMETER_NAME);
        setContentDisposition();
    }

    @Override
    protected final void doExecute(OutputStream outStream, ClientRequest request) throws UnifyException {
        InputStream in = null;
        try {
            in = getInputStream();
            IOUtils.writeAll(outStream, in);
        } finally {
            IOUtils.close(in);
        }
    }

    @Override
    protected final String getResourceName() {
        return resourceName;
    }

    @Override
    protected final boolean isAttachment() {
        return attachment;
    }

    protected abstract InputStream getInputStream() throws UnifyException;
    
    private void setContentDisposition() {
        String disposition = "inline;filename=";
        if (isAttachment()) {
            disposition = "attachment;filename=";
        }

        disposition = disposition + "\"" + resourceName + "\"";
        setMetaData("Content-Disposition", disposition);
    }

}
