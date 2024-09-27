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

package com.tcdng.unify.core.database;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.TopicEventType;

/**
 * Entity change broadcaster.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface EntityChangeEventBroadcaster extends UnifyComponent {

	/**
	 * Broadcasts an entity change.
	 * 
	 * @param eventType   the event type
	 * @param entityClass the entity class
	 * @throws UnifyException if an error occurs
	 */
	void broadcastEntityChange(TopicEventType eventType, Class<? extends Entity> entityClass) throws UnifyException;

	/**
	 * Broadcasts an entity change.
	 * 
	 * @param eventType   the event type
	 * @param entityClass the entity class
	 * @param id          the specific entity ID
	 * @throws UnifyException if an error occurs
	 */
	void broadcastEntityChange(TopicEventType eventType, Class<? extends Entity> entityClass, Object id)
			throws UnifyException;

}
