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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for datasource entity list provider.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDataSourceEntityListProvider extends AbstractUnifyComponent
		implements DataSourceEntityListProvider {

	private static Map<String, String> dataSourcesByEntity;

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEntityAliasesByDataSource(String datasourceName) throws UnifyException {
		UnifyComponentConfig unifyComponentConfig = getComponentConfig(DataSource.class, datasourceName);
		return (List<String>) unifyComponentConfig.getSetting(List.class, DataSource.ENTITYLIST_PROPERTY,
				Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getDataSourceByEntityAlias(String entityAlias) throws UnifyException {
		if (dataSourcesByEntity == null) {
			synchronized (this) {
				if (dataSourcesByEntity == null) {
					dataSourcesByEntity = new HashMap<String, String>();
					List<UnifyComponentConfig> unifyComponentConfigs = getComponentConfigs(DataSource.class);
					if (!DataUtils.isBlank(unifyComponentConfigs)) {
						for (UnifyComponentConfig unifyComponentConfig : unifyComponentConfigs) {
							final String dataSourceName = unifyComponentConfig.getName();
							for (String _entityAlias : (List<String>) unifyComponentConfig.getSetting(List.class,
									DataSource.ENTITYLIST_PROPERTY, Collections.emptyList())) {
								dataSourcesByEntity.put(_entityAlias, dataSourceName);
							}
						}
					}
				}
			}
		}

		return dataSourcesByEntity.get(entityAlias);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

}
