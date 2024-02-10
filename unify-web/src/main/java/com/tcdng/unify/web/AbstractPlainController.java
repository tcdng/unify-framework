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
import com.tcdng.unify.web.constant.Secured;

/**
 * Convenient base class for plain controllers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractPlainController extends AbstractController implements PlainController {

    public AbstractPlainController(Secured secured) {
        super(secured);
    }

    public AbstractPlainController() {
        super(Secured.FALSE);
    }

    @Override
    public final void process(ClientRequest request, ClientResponse response) throws UnifyException {
        try {
            doProcess(request, response);
        } catch (Exception e) {
            // TODO
        } finally {
            response.close();
        }
    }

    @Override
    public void ensureContextResources(ControllerPathParts controllerPathParts) throws UnifyException {
        
    }

    protected abstract void doProcess(ClientRequest request, ClientResponse response) throws UnifyException;

}
