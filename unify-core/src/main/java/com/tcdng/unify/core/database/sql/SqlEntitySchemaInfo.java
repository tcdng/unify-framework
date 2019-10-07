/*
 * Copyright 2018-2019 The Code Department.
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
package com.tcdng.unify.core.database.sql;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * SQL entity schema information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SqlEntitySchemaInfo {

    String getTableName();

    String getPreferredTableName();

    String getSchemaTableName();

    String getTableAlias();

    String getViewName();

    String getPreferredViewName();

    String getSchemaViewName();

    Long getIndex();

    SqlFieldSchemaInfo getIdFieldInfo();

    SqlFieldSchemaInfo getVersionFieldInfo();

    List<? extends SqlFieldSchemaInfo> getFieldInfos();

    List<? extends SqlFieldSchemaInfo> getListFieldInfos();

    Set<String> getFieldNames();

    SqlFieldSchemaInfo getFieldInfo(String name) throws UnifyException;

    SqlFieldSchemaInfo getFieldInfo(Long marker) throws UnifyException;

    Map<String, Class<?>> getViewBaseTables();

    List<SqlViewRestrictionInfo> getViewRestrictionList();

    List<? extends SqlForeignKeySchemaInfo> getForeignKeyList();

    Map<String, ? extends SqlUniqueConstraintSchemaInfo> getUniqueConstraintList();

    Map<String, ? extends SqlIndexSchemaInfo> getIndexList();

    List<Map<String, Object>> getStaticValueList();

    boolean isVersioned();

    boolean isViewable();
    
    boolean isViewOnly();

    boolean isViewRestriction();

    boolean isForeignKeys();

    boolean isUniqueConstraints();

    boolean isIndexes();
}
