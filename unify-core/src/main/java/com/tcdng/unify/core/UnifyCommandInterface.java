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
package com.tcdng.unify.core;

import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.net.NetworkInterface;
import com.tcdng.unify.core.net.NetworkInterfaceConfigType;

/**
 * Unify command interface.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("unify-commandinterface")
public class UnifyCommandInterface extends AbstractUnifyContainerInterface {

    private static final String UNIFYCOMMANDINTERFACE_NETINTERFACECONFIG = "unify-commandinterface-netconfig";

    @Configurable
    private NetworkInterface networkInterface;

    private int listeningPort;

    @Override
    public int getPort() {
        return listeningPort;
    }

    @Override
    protected void onStartServicingRequests() throws UnifyException {
        networkInterface.startLocalUnicastServer(UNIFYCOMMANDINTERFACE_NETINTERFACECONFIG);
    }

    @Override
    protected void onStopServicingRequests() throws UnifyException {
        networkInterface.stopLocalUnicastServer(UNIFYCOMMANDINTERFACE_NETINTERFACECONFIG);
    }

    @Override
    protected void onInitialize() throws UnifyException {
        listeningPort = getContainerSetting(short.class, UnifyCorePropertyConstants.APPLICATION_COMMAND_PORT,
                UnifyContainer.DEFAULT_COMMAND_PORT);
        networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER,
                UNIFYCOMMANDINTERFACE_NETINTERFACECONFIG, "unify-commandinterface-comm", "localhost", listeningPort,
                32);
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
