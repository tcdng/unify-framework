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
package com.tcdng.unify.core.database;

import java.util.Date;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Update;

/**
 * Entity policy.
 * 
 * @author The Code Department
 * @since 4.1
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
     * Entity pre-update method. Called before update of record.
     * 
     * @param update
     *            the update object
     * @param now
     *            the now time stamp
     * @throws UnifyException
     *             if an error occurs
     */
    void preUpdate(Update update, Date now) throws UnifyException;

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
     * Called before usage of query object.
     * 
     * @param query
     *            the query object which may be altered
     * @throws UnifyException
     *             if an error occurs
     */
    void preQuery(Query<? extends Entity> query) throws UnifyException;
    
    /**
     * Entity post-create method. Called after creation of record.
     * 
     * @param record
     *            the created record
     * @param now
     *            the now time stamp
     * @throws UnifyException
     *             if an error occurs
     */
    void postCreate(Entity record, Date now) throws UnifyException;

    /**
     * Entity post-update method. Called after update of record.
     * 
     * @param record
     *            the updated record
     * @param now
     *            the now time stamp
     * @throws UnifyException
     *             if an error occurs
     */
    void postUpdate(Entity record, Date now) throws UnifyException;

    /**
     * Entity post-update method. Called after update of record.
     * 
     * @param update
     *            the update object
     * @param now
     *            the now time stamp
     * @throws UnifyException
     *             if an error occurs
     */
    void postUpdate(Update update, Date now) throws UnifyException;

    /**
     * Entity post-delete method. Called after delete of record.
     * 
     * @param record
     *            the deleted record
     * @param now
     *            the now time stamp
     * @throws UnifyException
     *             if an error occurs
     */
    void postDelete(Entity record, Date now) throws UnifyException;

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
