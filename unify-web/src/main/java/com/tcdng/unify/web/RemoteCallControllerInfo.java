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

import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * Encapsulates information about a remote-call controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class RemoteCallControllerInfo extends ControllerInfo {

    private String remoteCallGateName;

    private Map<String, RemoteCallHandler> remoteHandlerMap;

    public RemoteCallControllerInfo(String controllerName, String remoteCallGateName,
            Map<String, RemoteCallHandler> remoteHandlerMap) {
        super(controllerName);
        this.remoteCallGateName = remoteCallGateName;
        this.remoteHandlerMap = remoteHandlerMap;
    }

    public String getRemoteCallGateName() {
        return remoteCallGateName;
    }

    public boolean isRemoteCallGate() {
        return remoteCallGateName != null;
    }

    /**
     * Retrieves all remote-call handler names associated with controller.
     */
    public Set<String> getRemoteHandlerNames() {
        return remoteHandlerMap.keySet();
    }

    /**
     * Gets a remote-call handler by specified name. GatewayAction call handler
     * names are full path names composed of the remote-call controller name, a
     * forward slash and the handler method name.
     * 
     * @param name
     *            the handler name
     * @return the page action
     * @throws UnifyException
     *             if remote-call handler info with name is unknown
     */
    public RemoteCallHandler getRemoteCallHandler(String name) throws UnifyException {
        RemoteCallHandler handler = remoteHandlerMap.get(name);
        if (handler == null) {
            throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_UNKNOWN_REMOTECALL_HANDLER, getControllerName(),
                    name);
        }
        return handler;
    }

}
