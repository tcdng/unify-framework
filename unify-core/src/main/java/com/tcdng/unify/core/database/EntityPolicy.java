/*
 * Copyright 2014 The Code Department
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

import java.util.Date;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Entity policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface EntityPolicy extends UnifyComponent {
	/**
	 * Entity pre-create method. Called before creation of record.
	 * 
	 * @param record
	 *            the record to be created
	 * @param now
	 *            the now time stamp
	 * @return Object the record primary key
	 * @throws UnifyException
	 *             if an error occurs
	 */
	Object preCreate(Entity record, Date now) throws UnifyException;

	/**
	 * Entity pre-update method. Called before update of record.
	 * 
	 * @param record
	 *            the record to be updated
	 * @param now
	 *            the now time stamp
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void preUpdate(Entity record, Date now) throws UnifyException;

	/**
	 * Entity pre-delete method. Called before delete of record.
	 * 
	 * @param record
	 *            the record to be deleted
	 * @param now
	 *            the now time stamp
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void preDelete(Entity record, Date now) throws UnifyException;

	/**
	 * Called on creation of record error.
	 * 
	 * @param record
	 *            the record to be created
	 */
	void onCreateError(Entity record);

	/**
	 * Called on update of record error.
	 * 
	 * @param record
	 *            the record to be updated
	 */
	void onUpdateError(Entity record);

	/**
	 * Called on delete of record error.
	 * 
	 * @param record
	 *            the record to be deleted
	 */
	void onDeleteError(Entity record);

	/**
	 * Indicates if now should be set in alter methods
	 * 
	 * @return if an error occurs
	 */
	boolean isSetNow();
}
