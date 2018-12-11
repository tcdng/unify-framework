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
package com.tcdng.unify.core.net;

/**
 * A network interface configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class NetworkInterfaceConfig {

	private NetworkInterfaceConfigType type;

	private String configName;

	private String communicator;

	private String host;

	private int port;

	private int maxThreads;

	public NetworkInterfaceConfig(NetworkInterfaceConfigType type, String configName, String communicator, String host,
			int port, int maxThreads) {
		this.type = type;
		this.configName = configName;
		this.communicator = communicator;
		this.host = host;
		this.port = port;
		this.maxThreads = maxThreads;
	}

	public NetworkInterfaceConfigType getType() {
		return type;
	}

	public String getConfigName() {
		return configName;
	}

	public String getCommunicator() {
		return communicator;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getMaxThreads() {
		return maxThreads;
	}
}
