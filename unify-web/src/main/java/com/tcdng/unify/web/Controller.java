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
package com.tcdng.unify.web;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Component interface that must be implemented by every controller class.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Controller extends UnifyComponent {

    /**
     * Processes a client request.
     * 
     * @param request
     *                 the client request to process
     * @param response
     *                 the client response object
     * @throws UnifyException
     *                        if an error occurs
     */
    void process(ClientRequest request, ClientResponse response) throws UnifyException;

    /**
     * Ensures context resources associated with this controller are created.
     * 
     * @param controllerPathParts
     *                            the controller path parts.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    void ensureContextResources(ControllerPathParts controllerPathParts) throws UnifyException;

    /**
     * Tests if controller requires secured access.
     * 
     * @return a true value means that access to this controller must have been
     *         authenticated
     */
    boolean isSecured();
    
    /**
     * Returns true if controller requires a referre.
     */
    boolean isRefererRequired();
}
