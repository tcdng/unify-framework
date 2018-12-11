/*
 * Copyright 2018 The Code Department
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
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.constant.EnumConst;

/**
 * Abstract base data source component that with typical configurable data
 * source properties.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDataSource extends AbstractUnifyComponent implements DataSource {

	@Configurable
	private DataSourceDialect dialect;

	private List<Class<?>> assembledEntityTypeList;

	public AbstractDataSource() {
		assembledEntityTypeList = new ArrayList<Class<?>>();
	}

	@Override
	public List<Class<?>> getEntityTypes() throws UnifyException {
		return assembledEntityTypeList;
	}

	@Override
	public DataSourceDialect getDialect() throws UnifyException {
		return dialect;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		logInfo("Assembling entity type information for {0}...", getName());

		String name = getName();
		// Enumeration constants
		for (Class<? extends EnumConst> enumConstClass : getAnnotatedClasses(EnumConst.class, StaticList.class)) {
			StaticList sa = enumConstClass.getAnnotation(StaticList.class);
			if (sa.datasource().equals(name)) {
				assembledEntityTypeList.add(enumConstClass);
			}
		}

		// Records
		for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, Table.class)) {
			Table ta = entityClass.getAnnotation(Table.class);
			if (ta.datasource().equals(name)) {
				assembledEntityTypeList.add(entityClass);
			}
		}

		assembledEntityTypeList = Collections.unmodifiableList(assembledEntityTypeList);
		logInfo("Assembly of entity type information for {0} completed.", getName());
	}

	protected void setDialect(DataSourceDialect dialect) {
		this.dialect = dialect;
	}
}
