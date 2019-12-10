/*
 * Copyright 2018-2019 The Code Department.
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
 * Manages controllers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ControllerManager extends UnifyComponent {

    /**
     * Returns a page controller information.
     * 
     * @param controllerName
     *            the controller name
     * @return the page controller information
     * @throws UnifyException
     *             if bean with name does not exist. If controller with name is not
     *             a page-controller. if an error occurs
     */
    PageControllerInfo getPageControllerInfo(String controllerName) throws UnifyException;

    /**
     * Updates page controller info with stand alone panel information
     * 
     * @param controllerName
     *            the controller name
     * @param standalonePanelName
     *            the stand alone panel
     * @throws UnifyException
     *             if an error occurs
     */
    void updatePageControllerInfo(String controllerName, String standalonePanelName) throws UnifyException;

    /**
     * Returns a remote-call controller information.
     * 
     * @param controllerName
     *            the controller name
     * @return the remote-call controller information
     * @throws UnifyException
     *             if bean with name does not exist. If controller with name is not
     *             a resource-controller. if an error occurs
     */
    RemoteCallControllerInfo getRemoteCallControllerInfo(String controllerName) throws UnifyException;

    /**
     * Returns a resource controller information.
     * 
     * @param controllerName
     *            the controller name
     * @return the resource controller information
     * @throws UnifyException
     *             if bean with name does not exist. If controller with name is not
     *             a resource-controller. if an error occurs
     */
    ResourceControllerInfo getResourceControllerInfo(String controllerName) throws UnifyException;

    /**
     * Returns an instance of a controller component by name.
     * 
     * @param controllerName
     *            the controller name
     * @param isLoadPage indicates page loading
     * @throws UnifyException
     *             if an error occurs
     */
    Controller getController(PathParts pathParts, boolean isLoadPage) throws UnifyException;

    /**
     * Executes a client request using appropriate controller.
     * 
     * @param request
     *            the client request
     * @param response
     *            the client response
     * @throws UnifyException
     *             if an error occurs
     */
    void executeController(ClientRequest request, ClientResponse response) throws UnifyException;

    /**
     * Executes a controller action.
     * 
     * @param fullActionPath
     *            the full action path
     * @return the response mapping string
     * @throws UnifyException
     *             if an error occurs
     */
    String executeController(String fullActionPath) throws UnifyException;

    /**
     * Populates the property of a controller page bean.
     * 
     * @param controllerName
     *            the controller name
     * @param propertyName
     *            the property to set
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    void populateControllerPageBean(String controllerName, String propertyName, Object value) throws UnifyException;
}
