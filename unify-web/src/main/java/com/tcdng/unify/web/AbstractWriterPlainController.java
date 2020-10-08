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

package com.tcdng.unify.web;

import java.io.Writer;

import com.tcdng.unify.core.UnifyException;

/**
 * Abstract writer plain controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractWriterPlainController extends AbstractPlainController {

    private String contentType;

    public AbstractWriterPlainController(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void doProcess(ClientRequest request, ClientResponse response) throws UnifyException {
        response.setContentType(contentType);
        doExecute(response.getWriter(), request);
    }

    protected abstract void doExecute(Writer writer, ClientRequest request) throws UnifyException;
}
