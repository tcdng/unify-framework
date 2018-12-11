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

package com.tcdng.unify.core.notification;

import com.tcdng.unify.core.constant.NetworkSecurityType;

/**
 * Email server configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class EmailServerConfig extends NotificationServerConfig {

	public EmailServerConfig(String hostAddress, Integer hostPort, NetworkSecurityType securityType, String username,
			String password) {
		super(hostAddress, hostPort, securityType, username, password);
	}

	public EmailServerConfig(String hostAddress, Integer hostPort, NetworkSecurityType securityType,
			String authentication) {
		super(hostAddress, hostPort, securityType, authentication);
	}

	public EmailServerConfig(String hostAddress, Integer hostPort) {
		super(hostAddress, hostPort);
	}

}
