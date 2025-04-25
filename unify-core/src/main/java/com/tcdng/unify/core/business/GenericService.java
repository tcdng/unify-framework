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
package com.tcdng.unify.core.business;

import java.util.List;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.database.Query;

/**
 * Generic business service.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface GenericService extends BusinessService {

    /**
     * Creates a record in database.
     * 
     * @param record
     *            the record to persist
     * @return the record ID
     * @throws UnifyException
     *             if an error occurs
     */
    Object create(Entity record) throws UnifyException;

    /**
     * Finds a persistent record by ID.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @return the record found otherwise null
     * @throws UnifyException
     *             if record with ID is not found. If an error occurs
     */
    <T extends Entity> T find(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Lists all records with fields that match supplied query.
     * 
     * @param query
     *            the query to match
     * @return list of records found
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException;

    /**
     * Lists the value of a record's property.
     * 
     * @param valueClazz
     *            the value type
     * @param recordClazz
     *            the record type
     * @param id
     *            the record ID
     * @param property
     *            the property
     * @return the selected value
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U extends Entity> T listValue(Class<T> valueClazz, Class<U> recordClazz, Object id, String property)
            throws UnifyException;

    /**
     * Updates a record in database.
     * 
     * @param record
     *            the record to update
     * @return the number of records updated
     * @throws UnifyException
     *             if an error occurs
     */
    int update(Entity record) throws UnifyException;

    /**
     * Updates record by ID.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @param update
     *            update object
     * @return number of records updated
     * @throws UnifyException
     *             if an error occurs
     */
    int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException;

    /**
     * Updates all records with fields that match criteria using supplied update
     * information.
     * 
     * @param query
     *            the query object
     * @param update
     *            the update information object
     * @return the number of record updated
     * @throws UnifyException
     *             if an error occurs
     */
    int updateAll(Query<? extends Entity> query, Update update) throws UnifyException;

    /**
     * Deletes a persistent record by ID.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @return the record found
     * @throws UnifyException
     *             if record with ID is not found. If an error occurs
     */
    <T extends Entity> int delete(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Deletes all records with fields that match criteria.
     * 
     * @param query
     *            the query object
     * @return the number of record deleted
     * @throws UnifyException
     *             if an error occurs
     */
    int deleteAll(Query<? extends Entity> query) throws UnifyException;

    /**
     * Populates list-only properties of a record
     * 
     * @param record
     *            the record to populate
     * @throws UnifyException
     *             if an error occurs
     */
    void populateListOnly(Entity record) throws UnifyException;

    /**
     * Counts records by query.
     * 
     * @param query
     *            the search query
     * @return the number of record
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> int countAll(Query<T> query) throws UnifyException;

    /**
     * Finds constraining record that may prevent supplied record from being
     * successfully created.
     * 
     * @param record
     * @return constraining record if found otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> T findConstraint(T record) throws UnifyException;

}
