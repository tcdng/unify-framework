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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.common.database.Entity;

/**
 * Datasource entity context
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DataSourceEntityContext {

	private List<String> datasources;
	
	private Map<String, List<Class<?>>> tableEnitiesByDataSource;
	
	private Map<String, List<Class<? extends Entity>>> viewEnitiesByDataSource;

	public DataSourceEntityContext(List<String> datasources, Map<String, List<Class<?>>> tableEnitiesByDataSource,
			Map<String, List<Class<? extends Entity>>> viewEnitiesByDataSource) {
		this.datasources = datasources;
		this.tableEnitiesByDataSource = tableEnitiesByDataSource;
		this.viewEnitiesByDataSource = viewEnitiesByDataSource;
	}

	public List<String> getDatasources() {
		return datasources;
	}

	public List<Class<?>> getTableEntities(String datasource) {
		List<Class<?>> result = tableEnitiesByDataSource.get(datasource);
		return result != null ? result : Collections.emptyList();
	}

	public List<Class<? extends Entity>> getViewEntities(String datasource) {
		List<Class<? extends Entity>> result = viewEnitiesByDataSource.get(datasource);
		return result != null ? result : Collections.emptyList();
	}
}
