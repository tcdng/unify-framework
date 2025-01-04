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

package com.tcdng.unify.core.database.dynamic.sql;

import java.util.List;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;

/**
 * Dynamic SQL entity loader.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface DynamicSqlEntityLoader extends UnifyComponent {

    /**
     * Loads dynamic entity information into SQL database.
     * 
     * @param dynamicEntityInfo
     *                          the dynamic entity information
     * @return the entity class representing the entity type
     * @throws UnifyException
     *                        if an error occurs
     */
    Class<? extends Entity> loadDynamicSqlEntity(DynamicEntityInfo dynamicEntityInfo)
            throws UnifyException;

    /**
     * Loads multiple dynamic entity information into SQL database.
     * 
     * @param dynamicEntityInfoList
     *                              the dynamic entity information list
     * @return the list of entity classes
     * @throws UnifyException
     *                        if an error occurs
     */
    List<Class<? extends Entity>> loadDynamicSqlEntities(List<DynamicEntityInfo> dynamicEntityInfoList)
            throws UnifyException;
}
