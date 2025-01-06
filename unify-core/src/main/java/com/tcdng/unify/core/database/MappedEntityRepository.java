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

import java.util.List;
import java.util.Set;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Mapped entity repository.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface MappedEntityRepository extends UnifyComponent {

    /**
     * Creates an record in the repository.
     * 
     * @param record
     *            the record to create
     * @return the record Id
     * @throws UnifyException
     *             if an error occurs during creation
     */
    Object create(Entity record) throws UnifyException;

    /**
     * Finds record by query. Does not fetch list-only fields and children.
     * 
     * @param query
     *            the query
     * @return the list of record found
     * @throws UnifyException
     *             if an error occurs during search
     */
    <T extends Entity> List<T> findAll(Query<T> query) throws UnifyException;

    /**
     * Updates a record by ID.
     * 
     * @param record
     *            the record to modify
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int updateById(Entity record) throws UnifyException;

    /**
     * Updates a record by ID and version number.
     * 
     * @param record
     *            the record to modify
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int updateByIdVersion(Entity record) throws UnifyException;

    /**
     * Deletes record by query.
     * 
     * @param query
     *            the query
     * @return the number of record deleted.
     * @throws UnifyException
     *             if an error occurs during delete
     */
    int deleteAll(Query<? extends Entity> query) throws UnifyException;

    /**
     * Obtains a set of values of selected field for all records by query. Field
     * must be selected in query.
     * 
     * @param fieldClass
     *            the value class
     * @param fieldName
     *            the value field name
     * @param query
     *            the query
     * @return the set of values found
     * @throws UnifyException
     *             if an error occurs during search
     */
    <T, U extends Entity> Set<T> valueSet(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

}
