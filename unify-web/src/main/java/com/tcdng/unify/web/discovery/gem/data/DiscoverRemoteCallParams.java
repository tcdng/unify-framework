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
package com.tcdng.unify.web.discovery.gem.data;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.tcdng.unify.web.discovery.gem.APIDiscoveryRemoteCallCodeConstants;
import com.tcdng.unify.web.remotecall.RemoteCallParams;

/**
 * Discover remote call parameters.
 * 
 * @author The Code Department
 * @since 4.1
 */
@JacksonXmlRootElement
public class DiscoverRemoteCallParams extends RemoteCallParams {

	@JacksonXmlProperty
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

    public void setRemoteCallCode(String remoteCallCode) {
        this.remoteCallCode = remoteCallCode;
    }

}
