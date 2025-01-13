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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.common.database.Entity;

/**
 * Datasource manager context
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DataSourceManagerContext {

	private final DataSourceManagerOptions options;
	
	private Map<String, List<Class<?>>> tableEnitiesByDataSource;
	
	private Map<String, List<Class<? extends Entity>>> viewEnitiesByDataSource;
	
	private Set<Class<?>> tableUtilised;
	
	private Set<Class<? extends Entity>> viewUtilised;
	
	public DataSourceManagerContext(DataSourceManagerContext ctx, DataSourceManagerOptions options) {
		this.options = options;
		this.tableEnitiesByDataSource = ctx.tableEnitiesByDataSource;
		this.viewEnitiesByDataSource = ctx.viewEnitiesByDataSource;
		this.tableUtilised = ctx.tableUtilised;
		this.viewUtilised = ctx.viewUtilised;
	}
	
	public DataSourceManagerContext(DataSourceManagerOptions options) {
		this.options = options;
		this.tableEnitiesByDataSource = new HashMap<String, List<Class<?>>>();
		this.viewEnitiesByDataSource = new HashMap<String, List<Class<? extends Entity>>>();
		this.tableUtilised = new HashSet<Class<?>>();
		this.viewUtilised = new HashSet<Class<? extends Entity>>();
	}
	
	public DataSourceManagerOptions getOptions() {
		return options;
	}

	public boolean addTableEntityClass(String datasource, Class<?> entityClass) {
		if (!tableUtilised.contains(entityClass)) {
			List<Class<?>> entities = tableEnitiesByDataSource.get(datasource);
			if (entities == null) {
				entities = new ArrayList<Class<?>>();
				tableEnitiesByDataSource.put(datasource, entities);
			}
			
			entities.add(entityClass);
			tableUtilised.add(entityClass);
			return true;
		}
		
		return false;
	}

	public boolean addViewEntityClass(String datasource, Class<? extends Entity> entityClass) {
		if (!viewUtilised.contains(entityClass)) {
			List<Class<? extends Entity>> entities = viewEnitiesByDataSource.get(datasource);
			if (entities == null) {
				entities = new ArrayList<Class<? extends Entity>>();
				viewEnitiesByDataSource.put(datasource, entities);
			}
			
			entities.add(entityClass);
			viewUtilised.add(entityClass);
			return true;
		}
		
		return false;
	}
	
	public List<Class<?>> getTableEntityClasses(String datasource) {
		List<Class<?>> classes = tableEnitiesByDataSource.get(datasource);
		return classes != null ? classes : Collections.emptyList();
	}
	
	public List<Class<? extends Entity>> getViewEntityClasses(String datasource) {
		List<Class<? extends Entity>> classes = viewEnitiesByDataSource.get(datasource);
		return classes != null ? classes : Collections.emptyList();
	}
	
	public boolean isTableUtilised(Class<?> entityClass) {
		return tableUtilised.contains(entityClass);
	}
	
	public boolean isViewUtilised(Class<? extends Entity> entityClass) {
		return viewUtilised.contains(entityClass);
	}
}
