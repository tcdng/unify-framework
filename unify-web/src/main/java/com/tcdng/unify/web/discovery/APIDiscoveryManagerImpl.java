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
package com.tcdng.unify.web.discovery;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.RemoteCallController;
import com.tcdng.unify.web.RemoteCallParams;
import com.tcdng.unify.web.RemoteCallResult;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.discovery.gem.data.APIDiscoveryRemoteCallInfo;

/**
 * Default API discovery manager implementation.
 * 
 * @author Lateef
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_APIDISCOVERYMANAGER)
public class APIDiscoveryManagerImpl extends AbstractUnifyComponent implements APIDiscoveryManager {

    private Map<String, RemoteCallInfo> remoteCallInfos;

    public APIDiscoveryManagerImpl() {
        this.remoteCallInfos = new HashMap<String, RemoteCallInfo>();
    }

    @Override
    public APIDiscoveryRemoteCallInfo getRemoteCallInfo(String code) throws UnifyException {
        RemoteCallInfo rci = this.remoteCallInfos.get(code);
        if (rci == null) {
            throw new UnifyException(UnifyWebErrorConstants.APIDISCOVERY_REMOTECALL_CODE_UNKNOWN, code);
        }

        return new APIDiscoveryRemoteCallInfo(rci.getCode(), rci.getDescription(),
                this.getSessionContext().getUriBase() + this.getSessionContext().getContextPath() + rci.getPath(),
                rci.isRestricted());
    }

    @Override
    protected void onInitialize() throws UnifyException {
        // TODO No API discovery if disabled at container level

        // Do full API detection
        this.logDebug("Initializing API detection...");
        for (UnifyComponentConfig ucc : this.getComponentConfigs(RemoteCallController.class)) {
            String name = ucc.getName();
            Method[] methods = ucc.getType().getMethods();

            this.logDebug("Detecting API methods for [{0}]...", name);
            for (Method method : methods) {
                com.tcdng.unify.web.annotation.GatewayAction goa = method
                        .getAnnotation(com.tcdng.unify.web.annotation.GatewayAction.class);
                if (goa != null && goa.discoverable()) {
                    if (RemoteCallResult.class.isAssignableFrom(method.getReturnType())
                            && method.getParameterTypes().length == 1
                            && RemoteCallParams.class.isAssignableFrom(method.getParameterTypes()[0])) {
                        RemoteCallInfo existRci = this.remoteCallInfos.get(goa.name());
                        if (existRci != null) {
                            throw new UnifyException(UnifyWebErrorConstants.APIDISCOVERY_REMOTECALL_CODE_EXISTS,
                                    goa.name(), name, existRci.getComponentName());
                        }

                        String path = name + '/' + method.getName();
                        this.logDebug("... method [{0}] detected.", path);
                        this.remoteCallInfos.put(goa.name(), new RemoteCallInfo(name, goa.name(),
                                this.resolveApplicationMessage(goa.description()), path, goa.restricted()));
                    } else {
                        throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_INVALID_REMOTECALL_HANDLER_SIGNATURE,
                                name, method.getName());
                    }
                }
            }
        }
        this.logDebug("API detection completed...");
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private class RemoteCallInfo {

        private String componentName;

        private String code;

        private String description;

        private String path;

        private boolean restricted;

        public RemoteCallInfo(String componentName, String code, String description, String path, boolean restricted) {
            this.componentName = componentName;
            this.code = code;
            this.description = description;
            this.path = path;
            this.restricted = restricted;
        }

        public String getComponentName() {
            return componentName;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public String getPath() {
            return path;
        }

        public boolean isRestricted() {
            return restricted;
        }
    }
}
