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
import java.util.List;

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
    public List<Class<?>> getTableEntityTypes(String dataSourceName) throws UnifyException {
        List<Class<?>> entityList = new ArrayList<Class<?>>();
        // Enumeration constants
        for (Class<? extends EnumConst> enumConstClass : getAnnotatedClasses(EnumConst.class, StaticList.class)) {
            StaticList sa = enumConstClass.getAnnotation(StaticList.class);
            if (AnnotationUtils.isStaticListDataSource(sa, dataSourceName)) {
                entityList.add(enumConstClass);
            }
        }

        // Entities
        for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, Table.class)) {
            Table ta = entityClass.getAnnotation(Table.class);
            if (AnnotationUtils.isTableDataSource(ta, dataSourceName)) {
                entityList.add(entityClass);
            }
        }

        // Extensions
        for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, TableExt.class)) {
            Class<?> extendedEntityClass = entityClass.getSuperclass();
            if (extendedEntityClass != null) {
                Table ta = extendedEntityClass.getAnnotation(Table.class);
                if (AnnotationUtils.isTableDataSource(ta, dataSourceName)) {
                    int index = entityList.indexOf(extendedEntityClass);
                    entityList.add(index + 1, entityClass);
                }
            }
        }
        return entityList;
    }

    @Override
    public List<Class<? extends Entity>> getViewEntityTypes(String dataSourceName) throws UnifyException {
        List<Class<? extends Entity>> entityList = new ArrayList<Class<? extends Entity>>();
        // Entities
        for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, View.class)) {
            View va = entityClass.getAnnotation(View.class);
            if (AnnotationUtils.isViewDataSource(va, dataSourceName)) {
                entityList.add(entityClass);
            }
        }

        return entityList;
    }

}
