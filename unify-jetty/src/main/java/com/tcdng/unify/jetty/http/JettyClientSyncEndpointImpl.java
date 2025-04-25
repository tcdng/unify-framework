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
package com.tcdng.unify.jetty.http;

import java.util.UUID;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.AbstractClientSyncEndpoint;
import com.tcdng.unify.web.AbstractClientSyncSession;
import com.tcdng.unify.web.ServerEventMsg;

/**
 * Jetty client synchronization end-point implementation.
 * 
 * @author The Code Department
 * @since 4.1
 */
@WebSocket
public class JettyClientSyncEndpointImpl extends AbstractClientSyncEndpoint {

	private final String sessionId;
	
	public JettyClientSyncEndpointImpl() {
		this.sessionId = UUID.randomUUID().toString();
	}
	
	@OnWebSocketConnect
	public void onOpen(Session session) {
		handleOpenSession(new ClientSyncSessionImpl(session));
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String txt) {
		handleTextMessage(sessionId, txt);
	}

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		handleCloseSession(sessionId, reason);
	}

	private class ClientSyncSessionImpl extends AbstractClientSyncSession {

		private final Session session;

		public ClientSyncSessionImpl(Session session) {
			this.session = session;
		}

		@Override
		public String getId() {
			return sessionId;
		}

		@Override
		public void setIdleTimeoutInMilliSec(long expirationInMilliSeconds) {
			session.setIdleTimeout(expirationInMilliSeconds);
		}

		@Override
		public void sendEventToRemote(ServerEventMsg event) {
			try {
				String msg = DataUtils.asJsonString(event, PrintFormat.NONE);
				session.getRemote().sendString(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
