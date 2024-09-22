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

import com.tcdng.unify.core.UnifyComponent;

/**
 * Page event broadcaster.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface PageEventBroadcaster extends UnifyComponent {

	/**
	 * Registers a client synchronization session.
	 * 
	 * @param session the session to register
	 */
	void registerClient(ClientSyncSession session);

	/**
	 * Unregisters a client synchronization session.
	 * 
	 * @param clientId the client ID
	 */
	void unregisterClient(String clientId);

	/**
	 * Processes a client event.
	 * 
	 * @param eventMsg
	 */
	void processClientEvent(ClientEventMsg eventMsg);
}
