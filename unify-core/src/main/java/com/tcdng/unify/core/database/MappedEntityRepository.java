/*
 * Copyright 2018-2023 The Code Department.
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

}
