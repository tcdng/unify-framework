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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.web.AbstractRemoteCallController;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.annotation.GatewayAction;
import com.tcdng.unify.web.discovery.gem.APIDiscoveryRemoteCallCodeConstants;
import com.tcdng.unify.web.discovery.gem.data.APIDiscoveryRemoteCallInfo;
import com.tcdng.unify.web.discovery.gem.data.DiscoverRemoteCallParams;
import com.tcdng.unify.web.discovery.gem.data.DiscoverRemoteCallResult;

/**
 * Remote call API discovery controller. This controller has no gate, so all
 * gateway operation restrictions are ignored.
 * 
 * @author Lateef
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_APIDISCOVERY_CONTROLLER)
public class APIDiscoveryController extends AbstractRemoteCallController {

    @Configurable(WebApplicationComponents.APPLICATION_APIDISCOVERYMANAGER)
    private APIDiscoveryManager aPIDiscoveryManager;

    @GatewayAction(name = APIDiscoveryRemoteCallCodeConstants.DISCOVER_REMOTE_CALL,
            description = "Discover Remote Call", restricted = false)
    public DiscoverRemoteCallResult discoverRemoteCall(DiscoverRemoteCallParams params) throws UnifyException {
        DiscoverRemoteCallResult result = new DiscoverRemoteCallResult();
        APIDiscoveryRemoteCallInfo remoteCallInfo = aPIDiscoveryManager.getRemoteCallInfo(params.getRemoteCallCode());
        result.setRemoteCallInfo(remoteCallInfo);
        return result;
    }
}
