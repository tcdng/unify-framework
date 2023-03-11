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

import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Abstract base data source component that with typical configurable data
 * source properties.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDataSource extends AbstractUnifyComponent implements DataSource {

	@Configurable
	private DataSourceDialect dialect;

	@Configurable("false")
	private boolean allObjectsInLowercase;

	@Configurable("false")
	private boolean readOnly;

	@Configurable("false")
	private boolean initDelayed;

	@Configurable(ApplicationComponents.APPLICATION_DATASOURCE_ENTITYLIST_PROVIDER)
	private DataSourceEntityListProvider entityListProvider;

	public void setDialect(DataSourceDialect dialect) throws UnifyException {
		this.dialect = dialect;
		if (dialect != null) {
			dialect.setDataSourceName(getEntityMatchingName());
			dialect.setAllObjectsInLowerCase(allObjectsInLowercase);
		}
	}

	public final void setAllObjectsInLowercase(boolean allObjectsInLowercase) {
		this.allObjectsInLowercase = allObjectsInLowercase;
	}

	public final void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public final void setInitDelayed(boolean initDelayed) {
		this.initDelayed = initDelayed;
	}

	public final void setEntityListProvider(DataSourceEntityListProvider entityListProvider) {
		this.entityListProvider = entityListProvider;
	}

	@Override
	public final boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public boolean isInitDelayed() throws UnifyException {
		return initDelayed;
	}

	@Override
	public List<Class<?>> getTableEntityTypes() throws UnifyException {
		return entityListProvider.getTableEntityTypes(getEntityMatchingName());
	}

	@Override
	public List<Class<? extends Entity>> getViewEntityTypes() throws UnifyException {
		return entityListProvider.getViewEntityTypes(getEntityMatchingName());
	}

	@Override
	public DataSourceDialect getDialect() throws UnifyException {
		return dialect;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		if (dialect != null) {
			dialect.setDataSourceName(getEntityMatchingName());
			dialect.setAllObjectsInLowerCase(allObjectsInLowercase);
		}
	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private String getEntityMatchingName() {
		String name = getPreferredName();
		if (StringUtils.isBlank(name)) {
			name = getName();
		}

		return name;
	}
}
