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
package com.tcdng.unify.web.discovery.gem.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.tcdng.unify.web.remotecall.RemoteCallResult;

/**
 * Discover remote call result.
 * 
 * @author The Code Department
 * @since 1.0
 */
@XmlRootElement
public class DiscoverRemoteCallResult extends RemoteCallResult {

    private APIDiscoveryRemoteCallInfo remoteCallInfo;

    public APIDiscoveryRemoteCallInfo getRemoteCallInfo() {
        return remoteCallInfo;
    }

    @XmlElement(required = true)
    public void setRemoteCallInfo(APIDiscoveryRemoteCallInfo remoteCallInfo) {
        this.remoteCallInfo = remoteCallInfo;
    }
}
