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
package com.tcdng.unify.tomcat.http;

import java.util.UUID;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.AbstractClientSyncEndpoint;
import com.tcdng.unify.web.AbstractClientSyncSession;
import com.tcdng.unify.web.ServerEventMsg;
import com.tcdng.unify.web.constant.ClientSyncNameConstants;

/**
 * Tomcat client synchronization end-point implementation.
 * 
 * @author The Code Department
 * @since 4.1
 */
@ServerEndpoint(ClientSyncNameConstants.SYNC_CONTEXT)
public class TomcatClientSyncEndpointImpl extends AbstractClientSyncEndpoint {

	private final String sessionId;
	
	public TomcatClientSyncEndpointImpl() {
		this.sessionId = UUID.randomUUID().toString();
	}
	
	@OnOpen
	public void onOpen(Session session) {
		handleOpenSession(new ClientSyncSessionImpl(session));
	}

	@OnMessage
	public void onMessage(String txt, Session session) {
		handleTextMessage(sessionId, txt);
	}

	@OnClose
	public void onClose(Session session) {
		handleCloseSession(sessionId, "");
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
			session.setMaxIdleTimeout(expirationInMilliSeconds);
		}

		@Override
		public void sendEventToRemote(ServerEventMsg event) {
			try {
				String msg = DataUtils.asJsonString(event, PrintFormat.NONE);
				session.getBasicRemote().sendText(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
