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

package com.tcdng.unify.web;

import com.tcdng.unify.web.util.ClientSyncUtils;

/**
 * Convenient abstract base class for client synchronization end-points.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractClientSyncEndpoint implements ClientSyncEndpoint {

	/**
	 * Handles open session.
	 * 
	 * @param session the synchronization session
	 */
	protected void handleOpenSession(ClientSyncSession session) {
		ClientSyncUtils.getClientSyncManager().openClientSession(session);
	}

	/**
	 * Handles text message.
	 * 
	 * @param clientSessionId the client session ID
	 * @param txt the text message to handle
	 */
	protected void handleTextMessage(String clientSessionId, String txt) {
		ClientSyncUtils.getClientSyncManager().processClientMessage(clientSessionId, txt);
	}

	/**
	 * Handles close session.
	 * 
	 * @param clientSessionId the client session ID
	 * @param reason the closure reason
	 */
	protected void handleCloseSession(String clientSessionId, String reason) {
		ClientSyncUtils.getClientSyncManager().closeClientSession(clientSessionId, reason);
	}

}
