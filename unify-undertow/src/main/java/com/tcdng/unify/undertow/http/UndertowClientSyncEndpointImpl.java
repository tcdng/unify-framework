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
package com.tcdng.unify.undertow.http;

import java.util.UUID;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.tcdng.unify.web.AbstractClientSyncEndpoint;
import com.tcdng.unify.web.AbstractClientSyncSession;
import com.tcdng.unify.web.constant.ClientSyncNameConstants;

/**
 * Undertow client synchronization end-point implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@ServerEndpoint(ClientSyncNameConstants.SYNC_CONTEXT)
public class UndertowClientSyncEndpointImpl extends AbstractClientSyncEndpoint {

	private final String sessionId;
	
	public UndertowClientSyncEndpointImpl() {
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

	}
}
