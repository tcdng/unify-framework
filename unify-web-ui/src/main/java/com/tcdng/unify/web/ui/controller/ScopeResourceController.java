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
package com.tcdng.unify.web.ui.controller;

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.Scope;
import com.tcdng.unify.core.resource.ResourceGenerator;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.annotation.RequestParameter;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.ui.AbstractPageResourceController;

/**
 * Resource controller for fetching resources from application or session scope.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("/resource/scope")
public class ScopeResourceController extends AbstractPageResourceController {

    @RequestParameter
    private String scope;

    @RequestParameter
    private boolean clearOnRead;

    private boolean isApplicationScope;

    private Object resource;

    public ScopeResourceController() {
        super(Secured.FALSE);
    }

    @Override
    public void prepareExecution() throws UnifyException {
        setContentDisposition(getResourceName());
        if (isApplicationScope = Scope.APPLICATION.equals(Scope.fromCode(scope))) {
            resource = getApplicationAttribute(getResourceName());
        } else {
            resource = getSessionContext().getAttribute(getResourceName());
        }

        if (resource != null && resource instanceof byte[]) {
            setContentLength(((byte[]) resource).length);
        }
    }

    @Override
    public String execute(OutputStream outputStream) throws UnifyException {
        InputStream inputStream = null;
        if (resource != null) {
            if (resource instanceof byte[]) {
                IOUtils.writeAll(outputStream, (byte[]) resource);
            } else if (resource instanceof InputStream) {
                inputStream = (InputStream) resource;
                IOUtils.writeAll(outputStream, inputStream);
                clearOnRead = true;
            } else if (resource instanceof ResourceGenerator) {
                ((ResourceGenerator<?>) resource).generate(outputStream);
                clearOnRead = true;
            }

            if (clearOnRead) {
                if (isApplicationScope) {
                    removeApplicationAttribute(getResourceName());
                } else {
                    getSessionContext().removeAttribute(getResourceName());
                }
            }
        }
        
        return null;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isClearOnRead() {
        return clearOnRead;
    }

    public void setClearOnRead(boolean clearOnRead) {
        this.clearOnRead = clearOnRead;
    }
}
