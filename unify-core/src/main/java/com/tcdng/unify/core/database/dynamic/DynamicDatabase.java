/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.core.database.dynamic;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.database.Entity;

/**
 * Dynamic database with changing entity structures.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface DynamicDatabase extends Database {

    /**
     * Gets database dynamic entity java class.
     * 
     * @param className
     *            the entity class name
     * @return the entity class name
     * @throws UnifyException
     *             if entity type with class name is unknown in this database. if an
     *             error occurs
     */
    Class<? extends Entity> getDynamicEntityClass(String className) throws UnifyException;

    /**
     * Creates of updates dynamic entity schema objects for database.
     * 
     * @param dynamicEntityInfoList
     *            the new dynamic entity structure list
     * @throws UnifyException
     *             if an error occurs
     */
    void createOrUpdateDynamicEntitySchemaObjects(List<DynamicEntityInfo> dynamicEntityInfoList) throws UnifyException;
}
