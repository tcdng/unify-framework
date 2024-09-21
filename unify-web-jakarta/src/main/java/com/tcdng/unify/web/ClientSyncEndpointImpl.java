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
package com.tcdng.unify.web;

import com.tcdng.unify.web.constant.ClientSyncNameConstants;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Client synchronization implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@ServerEndpoint(ClientSyncNameConstants.ENDPOINT_NAME)
public class ClientSyncEndpointImpl extends AbstractClientSyncEndpoint {

	@OnOpen
	public void onOpen(Session session) {
		handleOpenSession(new ClientSyncSessionImpl(session));
	}

	@OnMessage
	public void onMessage(String txt, Session session) {
		handleTextMessage(session.getId(), txt);
	}

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		handleCloseSession(session.getId(), reason.getReasonPhrase());
	}

	@OnError
	public void onError(Session session, Throwable t) {

	}

	private static class ClientSyncSessionImpl extends AbstractClientSyncSession {

		private final Session session;

		public ClientSyncSessionImpl(Session session) {
			this.session = session;
		}

		@Override
		public String getId() {
			return session.getId();
		}

	}
}
