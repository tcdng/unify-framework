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

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Database helper.
 * 
 * @author The Code Department
 * @since 1.0
 */

public interface DbHelper extends UnifyComponent {
	
	/**
	 * Checks if entity with object details already exists.
	 * 
	 * @param entityClass the entity class
	 * @param inst        the bean to check
	 * @param fieldName   the field name to check
	 * @return true if exists otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean exists(Class<? extends Entity> entityClass, Object inst, String fieldName) throws UnifyException;

	/**
	 * Checks if entity with object details already exists.
	 * 
	 * @param entityClass the entity class
	 * @param inst        the bean to check
	 * @return true if exists otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean exists(Class<? extends Entity> entityClass, Object inst) throws UnifyException;

	/**
	 * Checks if entities exist based on criteria.
	 * 
	 * @param query the criteria
	 * @return true if exist otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean exists(Query<? extends Entity> query) throws UnifyException;
}
