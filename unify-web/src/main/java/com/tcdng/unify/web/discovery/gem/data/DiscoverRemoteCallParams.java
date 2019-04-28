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
package com.tcdng.unify.web.discovery.gem.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.tcdng.unify.web.discovery.gem.APIDiscoveryRemoteCallCodeConstants;
import com.tcdng.unify.web.remotecall.RemoteCallParams;

/**
 * Discover remote call parameters.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@XmlRootElement
public class DiscoverRemoteCallParams extends RemoteCallParams {

    private String remoteCallCode;

    public DiscoverRemoteCallParams(String remoteCallCode) {
        super(APIDiscoveryRemoteCallCodeConstants.DISCOVER_REMOTE_CALL);
        this.remoteCallCode = remoteCallCode;
    }

    public DiscoverRemoteCallParams() {
        super(APIDiscoveryRemoteCallCodeConstants.DISCOVER_REMOTE_CALL);
    }

    public String getRemoteCallCode() {
        return remoteCallCode;
    }

    @XmlElement(required = true)
    public void setRemoteCallCode(String remoteCallCode) {
        this.remoteCallCode = remoteCallCode;
    }

}
