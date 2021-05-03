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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.web.annotation.Gateway;
import com.tcdng.unify.web.remotecall.RemoteCallParams;
import com.tcdng.unify.web.remotecall.RemoteCallResult;

/**
 * Default implementation of application controller utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_CONTROLLERUTIL)
public class ControllerUtilImpl extends AbstractUnifyComponent implements ControllerUtil {

    @Configurable
    private PathInfoRepository pathInfoRepository;

    @Configurable
    private ControllerFinder controllerFinder;


    private FactoryMap<String, RemoteCallControllerInfo> remoteCallControllerInfoMap;

    public ControllerUtilImpl() {

        remoteCallControllerInfoMap = new FactoryMap<String, RemoteCallControllerInfo>() {
            @Override
            protected RemoteCallControllerInfo create(String controllerName, Object... params) throws Exception {
                return createRemoteCallControllerInfo(controllerName);
            }
        };
    }

    public void setPathInfoRepository(PathInfoRepository pathInfoRepository) {
        this.pathInfoRepository = pathInfoRepository;
    }

    public void setControllerFinder(ControllerFinder controllerFinder) {
        this.controllerFinder = controllerFinder;
    }

    @Override
    public RemoteCallControllerInfo getRemoteCallControllerInfo(String controllerName) throws UnifyException {
        return remoteCallControllerInfoMap.get(controllerName);
    }

    @Override
    protected void onInitialize() throws UnifyException {
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private RemoteCallControllerInfo createRemoteCallControllerInfo(String controllerName) throws UnifyException {
        Class<? extends RemoteCallController> typeClass = getComponentType(RemoteCallController.class, controllerName);

        // Get gate if present
        String gateName = null;
        Gateway ga = typeClass.getAnnotation(Gateway.class);
        if (ga != null) {
            gateName = ga.value();
        }

        // Process remote call handlers
        Map<String, RemoteCallHandler> remoteCallHandlerMap = new HashMap<String, RemoteCallHandler>();
        Method[] methods = typeClass.getMethods();
        for (Method method : methods) {
            com.tcdng.unify.web.annotation.RemoteAction goa =
                    method.getAnnotation(com.tcdng.unify.web.annotation.RemoteAction.class);
            if (goa != null) {
                if (RemoteCallResult.class.isAssignableFrom(method.getReturnType())
                        && method.getParameterTypes().length == 1
                        && RemoteCallParams.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    remoteCallHandlerMap.put(controllerName + '/' + method.getName(),
                            new RemoteCallHandler(goa.name(), method, goa.restricted()));
                } else {
                    throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_INVALID_REMOTECALL_HANDLER_SIGNATURE,
                            controllerName, method.getName());
                }
            }
        }
        return new RemoteCallControllerInfo(controllerName, gateName, remoteCallHandlerMap);
    }
}
