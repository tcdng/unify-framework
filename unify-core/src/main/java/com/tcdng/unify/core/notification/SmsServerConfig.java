/*
 * Copyright 2018-2022 The Code Department.
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

package com.tcdng.unify.core.notification;

import com.tcdng.unify.core.constant.NetworkSecurityType;
import com.tcdng.unify.core.util.StringUtils;

/**
 * SMS server configuration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class SmsServerConfig extends AbstractNotifServerConfig {

    protected SmsServerConfig(String hostAddress, Integer hostPort, NetworkSecurityType securityType, String username,
            String password) {
        super(hostAddress, hostPort, securityType, username, password);
    }

    public SmsServerConfig(String hostAddress, Integer hostPort, NetworkSecurityType securityType,
            String authentication) {
        super(hostAddress, hostPort, securityType, authentication);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String hostAddress;

        private Integer hostPort;

        private NetworkSecurityType securityType;

        private String authentication;

        private String username;

        private String password;

        private Builder() {

        }

        public Builder hostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
            return this;
        }

        public Builder hostPort(Integer hostPort) {
            this.hostPort = hostPort;
            return this;
        }

        public Builder useSecurityType(NetworkSecurityType securityType) {
            this.securityType = securityType;
            return this;
        }

        public Builder useAuthentication(String authentication) {
            this.authentication = authentication;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public SmsServerConfig build() {
            if (!StringUtils.isBlank(authentication)) {
                return new SmsServerConfig(hostAddress, hostPort, securityType, authentication);
            }

            return new SmsServerConfig(hostAddress, hostPort, securityType, username, password);
        }
    }
}
