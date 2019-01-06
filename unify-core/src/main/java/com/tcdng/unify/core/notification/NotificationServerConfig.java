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

package com.tcdng.unify.core.notification;

import com.tcdng.unify.core.constant.NetworkSecurityType;

/**
 * Notification server configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class NotificationServerConfig {

    private String hostAddress;

    private Integer hostPort;

    private NetworkSecurityType securityType;

    private String authentication;

    private String username;

    private String password;

    public NotificationServerConfig(String hostAddress, Integer hostPort, NetworkSecurityType securityType,
            String username, String password) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.securityType = securityType;
        this.username = username;
        this.password = password;
    }

    public NotificationServerConfig(String hostAddress, Integer hostPort, NetworkSecurityType securityType,
            String authentication) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.securityType = securityType;
        this.authentication = authentication;
    }

    public NotificationServerConfig(String hostAddress, Integer hostPort) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public Integer getHostPort() {
        return hostPort;
    }

    public NetworkSecurityType getSecurityType() {
        return securityType;
    }

    public String getAuthentication() {
        return authentication;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
