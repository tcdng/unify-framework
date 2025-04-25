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

package com.tcdng.unify.core.database.dynamic.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlSchemaManager;
import com.tcdng.unify.core.runtime.JavaClassAdditionalTypeInfo;
import com.tcdng.unify.core.runtime.JavaClassSource;
import com.tcdng.unify.core.runtime.RuntimeJavaClassManager;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.DynamicEntityUtils;

/**
 * Default implementation of dynamic SQL entity loader.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLENTITYLOADER)
public class DynamicSqlEntityLoaderImpl extends AbstractUnifyComponent implements DynamicSqlEntityLoader {

	private static final String DYNAMICSQLENTITYLOADER_LOCK = "db::dynamicsqlentityloader-lock";

	@Configurable
	private RuntimeJavaClassManager runtimeJavaClassManager;

	@Configurable
	private SqlSchemaManager sqlSchemaManager;

	@Configurable(ApplicationComponents.APPLICATION_DATASOURCE)
	private SqlDataSource sqlDataSource;

	@Override
	public Class<? extends Entity> loadDynamicSqlEntity(DynamicEntityInfo dynamicEntityInfo) throws UnifyException {
		List<Class<? extends Entity>> classList = loadDynamicSqlEntities(Arrays.asList(dynamicEntityInfo));
		return classList.get(0);
	}

	@Override
	@Synchronized(DYNAMICSQLENTITYLOADER_LOCK)
	public List<Class<? extends Entity>> loadDynamicSqlEntities(List<DynamicEntityInfo> dynamicEntityInfoList)
			throws UnifyException {
		logInfo("Generating source files for [{0}] entity classes...", dynamicEntityInfoList.size());
		List<JavaClassSource> sourceList = new ArrayList<JavaClassSource>();
		for (DynamicEntityInfo dynamicEntityInfo : dynamicEntityInfoList) {
			logDebug("Generating source file for [{0}]...", dynamicEntityInfo.getClassName());
			if (!dynamicEntityInfo.isResolved()) {
				throwOperationErrorException(new IllegalArgumentException("Dynamic entity information for ["
						+ dynamicEntityInfo.getClassName() + "] entity is not finally resolved."));
			}

			JavaClassSource source = new JavaClassSource(dynamicEntityInfo.getClassName(),
					DynamicEntityUtils.generateEntityJavaClassSource(dynamicEntityInfo),
					new JavaClassAdditionalTypeInfo(dynamicEntityInfo.getListTypeArgByFieldName()));
			sourceList.add(source);
		}
		logInfo("Source files successfully generated for [{0}] entity classes...",
				dynamicEntityInfoList.size());

		List<Class<? extends Entity>> classList = runtimeJavaClassManager.compileAndLoadJavaClasses(Entity.class, sourceList);

		sqlSchemaManager.registerSqlEntityClasses(sqlDataSource, classList);
		
		// Update application datasource schema if necessary
		List<Class<?>> schemaChangedClassList = new ArrayList<Class<?>>();
		final int len = dynamicEntityInfoList.size();
		for (int i = 0; i < len; i++) {
			DynamicEntityInfo dynamicEntityInfo = dynamicEntityInfoList.get(i);
			if (dynamicEntityInfo.isManaged() && dynamicEntityInfo.isSchemaChanged()) {
				schemaChangedClassList.add(classList.get(i));
			}
		}

		if (!DataUtils.isBlank(schemaChangedClassList)) {
			sqlSchemaManager.updateSchema(sqlDataSource, schemaChangedClassList);
		}

		return classList;
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

}
