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

import java.util.Date;

/**
 * Client synchronization session.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ClientSyncSession {

	/**
	 * Sends event to this session's remote client.
	 * 
	 * @param event the event message
	 */
	void sendEventToRemote(ServerEventMsg event);

	/**
	 * Sets the session idle timeout.
	 * 
	 * @param expirationInMilliSeconds expiration in milliseconds
	 */
	void setIdleTimeoutInMilliSec(long expirationInMilliSeconds);
	
	/**
	 * Gets session unique ID.
	 * 
	 * @return the unique ID
	 */
	String getId();

	/**
	 * Sets the session client ID.
	 * 
	 * @param clientId the client ID to set.
	 */
	void setClientId(String clientId);
	
	/**
	 * Gets session client ID.
	 * 
	 * @return the client ID
	 */
	String getClientId();

	/**
	 * Invalidates session.
	 */
	void invalidate();
	
	/**
	 * Checks if session is invalidated.
	 * 
	 * @return true if invalidated otherwise false
	 */
	boolean isInvalidated();
	
	/**
	 * Triggers heart beat on client call.
	 */
	void heartBeat();
	
	/**
	 * Returns the last time client called server.
	 * 
	 * @return the timestamp
	 */
	Date lastClientCallOn();
}
