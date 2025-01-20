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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.TableExt;
import com.tcdng.unify.core.annotation.View;
import com.tcdng.unify.core.util.AnnotationUtils;

@Component(ApplicationComponents.APPLICATION_DATASOURCE_ENTITYLIST_PROVIDER)
public class DataSourceEntityListProviderImpl extends AbstractDataSourceEntityListProvider {

	@Override
	public DataSourceEntityContext getDataSourceEntityContext(List<String> datasources) throws UnifyException {
		Map<String, List<Class<?>>> tableEnitiesByDataSource = new HashMap<String, List<Class<?>>>();
		Map<String, List<Class<? extends Entity>>> viewEnitiesByDataSource = new HashMap<String, List<Class<? extends Entity>>>();
		final List<String> _datasources = new ArrayList<String>(datasources);
		if (_datasources.remove(ApplicationComponents.APPLICATION_DATASOURCE)) {
			_datasources.add(ApplicationComponents.APPLICATION_DATASOURCE);
		}

		Set<Class<?>> usedTables = new HashSet<Class<?>>();
		Set<Class<? extends Entity>> usedViews = new HashSet<Class<? extends Entity>>();
		for (String datasource : _datasources) {
			List<Class<?>> tableEntities = new ArrayList<Class<?>>();
			// Enumeration constants
			for (Class<? extends EnumConst> enumConstClass : getAnnotatedClasses(EnumConst.class, StaticList.class)) {
				StaticList sa = enumConstClass.getAnnotation(StaticList.class);
				if (AnnotationUtils.isStaticListDataSource(sa, datasource)) {
					if (!usedTables.contains(enumConstClass)) {
						tableEntities.add(enumConstClass);
						usedTables.add(enumConstClass);
					}
				}
			}

			// Entities
			for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, Table.class)) {
				Table ta = entityClass.getAnnotation(Table.class);
				if (AnnotationUtils.isTableDataSource(ta, datasource)) {
					if (!usedTables.contains(entityClass)) {
						tableEntities.add(entityClass);
						usedTables.add(entityClass);
					}
				}
			}

			// Extensions
			for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, TableExt.class)) {
				Class<?> extendedEntityClass = entityClass.getSuperclass();
				if (extendedEntityClass != null) {
					Table ta = extendedEntityClass.getAnnotation(Table.class);
					if (AnnotationUtils.isTableDataSource(ta, datasource)) {
						if (!usedTables.contains(entityClass)) {
							final int index = tableEntities.indexOf(extendedEntityClass);
							tableEntities.add(index + 1, entityClass);
							usedTables.add(entityClass);
						}
					}
				}
			}

			// Views
			List<Class<? extends Entity>> viewEntities = new ArrayList<Class<? extends Entity>>();
			for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, View.class)) {
				View va = entityClass.getAnnotation(View.class);
				if (AnnotationUtils.isViewDataSource(va, datasource)) {
					if (!usedViews.contains(entityClass)) {
						viewEntities.add(entityClass);
						usedViews.add(entityClass);
					}
				}
			}
			
			tableEnitiesByDataSource.put(datasource, tableEntities);
			viewEnitiesByDataSource.put(datasource, viewEntities);
		}

		return new DataSourceEntityContext(_datasources, tableEnitiesByDataSource, viewEnitiesByDataSource);
	}

}
