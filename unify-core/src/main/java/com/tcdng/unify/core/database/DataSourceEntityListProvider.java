/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.View;

/**
 * Data source entity list provider.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface DataSourceEntityListProvider extends UnifyComponent {

    /**
     * Returns a list of entity types annotated with {@link Table} and enumerations
     * annotated with {@link StaticList} that are maintained in a data source.
     * Entity types in list are expected to be ordered based on dependency with
     * parents coming before dependents.
     * 
     * @param datasourceName the datasource name
     * @throws UnifyException
     *             if an error occurs
     */
    List<Class<?>> getTableEntityTypes(String datasourceName) throws UnifyException;

    /**
     * Returns a list of entity types annotated with {@link View} that are
     * maintained in a data source.
     * 
     * @param datasourceName the datasource name
     * @throws UnifyException
     *             if an error occurs
     */
    List<Class<? extends Entity>> getViewEntityTypes(String datasourceName) throws UnifyException;
}
