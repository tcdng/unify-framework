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

    /**
     * Returns the entity index.
     */
    Long getIndex();

    /**
     * Returns the entity table name.
     */
    String getTable();

    /**
     * Returns the table alias.
     */
    String getTableAlias();

    /**
     * Returns the table view.
     */
    String getView();

    /**
     * Returns true if entity is versioned.
     */
    boolean isVersioned();

    /**
     * Returns true if entity is viewable.
     */
    boolean isViewable();

    /**
     * Returns ID field information.
     */
    SqlFieldSchemaInfo getIdFieldInfo();

    /**
     * Returns version field information.
     */
    SqlFieldSchemaInfo getVersionFieldInfo();

    /**
     * Returns the entity field schema information.
     */
    List<? extends SqlFieldSchemaInfo> getFieldInfos();

    /**
     * Returns the entity list field schema information.
     */
    List<? extends SqlFieldSchemaInfo> getListFieldInfos();

    /**
     * Returns entity field names.
     */
    Set<String> getFieldNames();

    /**
     * Gets field schema information by name.
     * 
     * @param name
     *            the field name
     * @return the field schema information
     * @throws UnifyException
     *             if field with name is not found
     */
    SqlFieldSchemaInfo getFieldInfo(String name) throws UnifyException;

    /**
     * Gets field schema information by marker.
     * 
     * @param marker
     *            the field marker
     * @return the field schema information otherwise null if not found
     * @throws UnifyException
     *             if an error occurs
     */
    SqlFieldSchemaInfo getFieldInfo(Long marker) throws UnifyException;

    /**
     * Returns true if record type has foreign keys.
     */
    boolean isForeignKeys();

    /**
     * Returns foreign key information list.
     */
    List<? extends SqlForeignKeySchemaInfo> getForeignKeyList();

    /**
     * Returns true if record type has unique constraints.
     */
    boolean isUniqueConstraints();

    /**
     * Returns unique constraint information list.
     */
    Map<String, ? extends SqlUniqueConstraintSchemaInfo> getUniqueConstraintList();

    /**
     * Returns true if record type has indexes.
     */
    boolean isIndexes();

    /**
     * Returns index information list.
     */
    Map<String, ? extends SqlIndexSchemaInfo> getIndexList();

    /**
     * Returns record type static values.
     * 
     * @return the static values list otherwise null
     */
    List<Map<String, Object>> getStaticValueList();
}
