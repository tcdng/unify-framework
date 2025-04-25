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
package com.tcdng.unify.core.system;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.database.Query;

/**
 * Manages application heart beats.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface HeartbeatManager extends BusinessService {

	/**
	 * Starts a heartbeat based on supplied criteria. Once started, the heartbeat
	 * manager is expected to extend the lifespan of all entites by criteria.
	 * Lifespan is extended by updating the expiry field of each entity in periodic
	 * intervals (heartbeats). Note that heartbeats will stop automatically once no
	 * entity meets criteria anymore.
	 * 
	 * @param query                  query that limits affected entities
	 * @param expiryFieldName        the expiry field name. Must be a timestamp
	 *                               field
	 * @param lifeExtensionInMinutes number of minutes to extend entity lifespan at
	 *                               every heartbeat
	 * @return the heartbeat ID
	 * @throws UnifyException if an error occurs
	 */
	String startHeartbeat(Query<? extends Entity> query, String expiryFieldName, long lifeExtensionInMinutes)
			throws UnifyException;

	/**
	 * Stops a heartbeat.
	 * 
	 * @param heartbeatId the heartbeat ID
	 * @throws UnifyException
	 */
	void stopHeartbeat(String heartbeatId) throws UnifyException;
}
