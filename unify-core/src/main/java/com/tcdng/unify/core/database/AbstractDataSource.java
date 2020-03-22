/*
 * Copyright 2018-2020 The Code Department.
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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.TableExt;
import com.tcdng.unify.core.annotation.View;
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

    @Override
    public List<Class<?>> getTableEntityTypes() throws UnifyException {
        List<Class<?>> entityList = new ArrayList<Class<?>>();
        String name = getName();
        // Enumeration constants
        for (Class<? extends EnumConst> enumConstClass : getAnnotatedClasses(EnumConst.class, StaticList.class)) {
            StaticList sa = enumConstClass.getAnnotation(StaticList.class);
            if (sa.datasource().equals(name)) {
                entityList.add(enumConstClass);
            }
        }

        // Entities
        for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, Table.class)) {
            Table ta = entityClass.getAnnotation(Table.class);
            if (ta.datasource().equals(name)) {
                entityList.add(entityClass);
            }
        }

        return entityList;
    }

    @Override
    public List<Class<?>> getTableExtensionEntityTypes() throws UnifyException {
        List<Class<?>> entityList = new ArrayList<Class<?>>();
        String name = getName();
        // Extensions
        for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, TableExt.class)) {
            TableExt tae = entityClass.getAnnotation(TableExt.class);
            if (tae.datasource().equals(name)) {
                entityList.add(entityClass);
            }
        }

        return entityList;
    }

    @Override
    public List<Class<? extends Entity>> getViewEntityTypes() throws UnifyException {
        List<Class<? extends Entity>> entityList = new ArrayList<Class<? extends Entity>>();
        String name = getName();
        // Entities
        for (Class<? extends Entity> entityClass : getAnnotatedClasses(Entity.class, View.class)) {
            View va = entityClass.getAnnotation(View.class);
            if (va.datasource().equals(name)) {
                entityList.add(entityClass);
            }
        }

        return entityList;
    }

    @Override
    public DataSourceDialect getDialect() throws UnifyException {
        return dialect;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected void setDialect(DataSourceDialect dialect) {
        this.dialect = dialect;
    }
}
