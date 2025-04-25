/*
 * Copyright (c) 2018-2025 The Code Department.
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

import com.tcdng.unify.core.business.BusinessService;

/**
 * Client synchronization manager.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ClientSyncManager extends BusinessService {

	/**
	 * Opens a client synchronization session.
	 * 
	 * @param session the session to open
	 */
	void openClientSession(ClientSyncSession session);

	/**
	 * Processes a client message.
	 * 
	 * @param sessionId the client session ID
	 * @param msg       the client message
	 */
	void processClientMessage(String sessionId, String msg);

	/**
	 * Closes a client session.
	 * 
	 * @param sessionId the client session ID
	 * @param reason    the reason
	 */
	void closeClientSession(String sessionId, String reason);
	
}
