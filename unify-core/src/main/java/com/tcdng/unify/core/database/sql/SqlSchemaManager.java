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

package com.tcdng.unify.core.database.sql;

import java.util.List;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * SQL schema manager.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface SqlSchemaManager extends UnifyComponent {

	/**
	 * Register SQL entity classes.
	 * 
	 * @param sqlDataSource   the SQL datasource
	 * @param entityClassList the entity class list
	 * @throws UnifyException if an error occurs
	 */
	void registerSqlEntityClasses(SqlDataSource sqlDataSource, List<Class<? extends Entity>> entityClassList) throws UnifyException;

	/**
	 * Update schema for datasource based on supplied classes.
	 * 
	 * @param sqlDataSource          the SQL datasource
	 * @param schemaChangedClassList the schema changed list
	 * @throws UnifyException if an error occurs
	 */
	void updateSchema(SqlDataSource sqlDataSource, List<Class<?>> schemaChangedClassList) throws UnifyException;

	/**
	 * Manages table schema for list of entity classes.
	 * 
	 * @param sqlDataSource the SQL data source
	 * @param options       the manager options
	 * @param entityClasses the entity class list
	 * @throws UnifyException if an error occurs
	 */
	void manageTableSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options, List<Class<?>> entityClasses)
			throws UnifyException;

	/**
	 * Manages view schema for list of entity classes.
	 * 
	 * @param sqlDataSource the SQL data source
	 * @param options       the manager options
	 * @param entityClasses the entity class list
	 * @throws UnifyException if an error occurs
	 */
	void manageViewSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options,
			List<Class<? extends Entity>> entityClasses) throws UnifyException;

	/**
	 * Drops view schema for list of entity classes.
	 * 
	 * @param sqlDataSource the SQL data source
	 * @param options       the manager options
	 * @param entityClasses the entity class list
	 * @throws UnifyException if an error occurs
	 */
	void dropViewSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options,
			List<Class<? extends Entity>> entityClasses) throws UnifyException;

	/**
	 * Builds a parent dependency list form list of entities.
	 * 
	 * @param sqlDataSource the data source
	 * @param entityClasses the entity class list
	 * @return entity classes in a parent dependency list
	 * @throws UnifyException if an error occurs
	 */
	List<Class<?>> buildParentDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityClasses) throws UnifyException;

	/**
	 * Builds a child dependency list form list of entities.
	 * 
	 * @param sqlDataSource the data source
	 * @param entityClasses the entity class list
	 * @return entity classes in a child dependency list
	 * @throws UnifyException if an error occurs
	 */
	List<Class<?>> buildChildDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityClasses) throws UnifyException;
}
