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
package com.tcdng.unify.web.util;

import com.tcdng.unify.web.ClientSyncManager;

/**
 * Client synchronization utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class ClientSyncUtils {

	private static ClientSyncManager clientSyncManager;
	
    private ClientSyncUtils() {

    }

	/**
	 * Registers a client synchronizer manager.
	 * 
	 * @param clientSyncManager the manage to register
	 */
	public static void registerClientSyncManager(ClientSyncManager clientSyncManager) {
		ClientSyncUtils.clientSyncManager = clientSyncManager;
	}

	/**
	 * Gets client synchronization manager.
	 * 
	 * @return the client synchronization manager
	 */
	public static ClientSyncManager getClientSyncManager() {
		return clientSyncManager;
	}

}
