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
package com.tcdng.unify.tomcat.http;

import javax.servlet.ServletContextEvent;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.tomcat.websocket.server.Constants;
import org.apache.tomcat.websocket.server.WsContextListener;

import com.tcdng.unify.web.constant.ClientSyncNameConstants;

/**
 * Tomcat client synchronization WS context listener.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TomcatClientSyncWsContextListener extends WsContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);

		ServerContainer sc = (ServerContainer) sce.getServletContext()
				.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
		try {
			ServerEndpointConfig endPointConfig = ServerEndpointConfig.Builder
					.create(TomcatClientSyncEndpointImpl.class, ClientSyncNameConstants.SYNC_CONTEXT).build();
			sc.addEndpoint(endPointConfig);
		} catch (DeploymentException de) {
			de.printStackTrace();
		}
	}

}