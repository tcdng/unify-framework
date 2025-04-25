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
 * Controller utilities object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ControllerUtil extends UnifyComponent {

    /**
     * Returns a plain controller information.
     * 
     * @param controllerName
     *            the controller name
     * @return the plain controller information
     * @throws UnifyException
     *             if bean with name does not exist. If controller with name is not
     *             a page-controller. if an error occurs
     */
    PlainControllerInfo getPlainControllerInfo(String controllerName) throws UnifyException;

    /**
     * Returns a remote call controller information.
     * 
     * @param controllerName
     *            the controller name
     * @return the remote call controller information
     * @throws UnifyException
     *             if bean with name does not exist. If controller with name is not
     *             a page-controller. if an error occurs
     */
    RemoteCallControllerInfo getRemoteCallControllerInfo(String controllerName) throws UnifyException;

}
