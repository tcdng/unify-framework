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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A component interface for managing data sources.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface DataSourceManager extends UnifyComponent {

	/**
	 * Initializes a data source.
	 * 
	 * @param datasource the datasource name
	 * @param options    the manager options to apply to this operation
	 * @throws UnifyException if data source does not allow management. If an error
	 *                        occurs.
	 */
	void initDataSource(String datasource, DataSourceManagerOptions options) throws UnifyException;

	/**
	 * Manages a data source, making sure that entity models match datasource
	 * schema.
	 * 
	 * @param datasource the datasource name
	 * @param options    the manager options to apply to this operation
	 * @throws UnifyException if data source does not allow management. If an error
	 *                        occurs.
	 */
	void manageDataSource(String datasource, DataSourceManagerOptions options) throws UnifyException;

	/**
	 * Run delayed datasource initialization.
	 * 
	 * @param options the manager options to apply to this operation
	 * @throws UnifyException If an error occurs.
	 */
	void initDelayedDataSource() throws UnifyException;
}
