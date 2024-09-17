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
import com.tcdng.unify.core.UnifyException;

/**
 * Client synchronization manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ClientSyncManager extends UnifyComponent {

	/**
	 * Broadcasts a client synchronization message.
	 * 
	 * @param msg the message to broadcast
	 * @throws UnifyException if an error occurs
	 */
	void broadcast(ClientSyncMsg msg) throws UnifyException;

	/**
	 * Registers a client synchronization endpoint.
	 * 
	 * @param endpoint the endpoint to register
	 * @throws UnifyException if an error occurs
	 */
	void registerClientEndpoint(ClientSyncEndpoint endpoint) throws UnifyException;

	/**
	 * Unregisters a client synchronization endpoint.
	 * 
	 * @param clientId the client ID
	 * @throws UnifyException if an error occurs
	 */
	void unregisterClientEndpoint(String clientId) throws UnifyException;
}
