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
package com.tcdng.unify.core.database;

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Data source entity list provider.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface DataSourceEntityListProvider extends UnifyComponent {

	/**
	 * Gets datasource entity context.
	 * 
	 * @param datasources the datasources
	 * @return the entity context
	 * @throws UnifyException if an error occurs
	 */
	DataSourceEntityContext getDataSourceEntityContext(List<String> datasources) throws UnifyException;

	/**
	 * Gets entity aliases by data source.
	 * 
	 * @param datasourceName the data source name
	 * @return
	 * @throws UnifyException
	 */
	List<String> getEntityAliasesByDataSource(String datasourceName) throws UnifyException;

	/**
	 * Gets data source by entity alias.
	 * 
	 * @param entityAlias the entity alias
	 * @return the data source name if entity alias is found otherwise null
	 * @throws UnifyException if an error occurs
	 */
	String getDataSourceByEntityAlias(String entityAlias) throws UnifyException;
}
