/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.annotation.ColumnType;

/**
 * SQL field schema information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface SqlFieldSchemaInfo {

    /**
     * Returns field name.
     */
    String getName();

    /**
     * Returns the field information unique marker. Markers are used to track field
     * information over a lifetime.
     */
    Long getMarker();

    /**
     * Returns the field column name.
     */
    String getColumnName();

    /**
     * Returns the field preferred column name.
     */
    String getPreferredColumnName();

    /**
     * Returns the constraint name.
     */
    String getConstraint();

    /**
     * Returns the field nullable attribute
     */
    boolean isNullable();

    /**
     * Returns the field is foster parent type
     */
    boolean isFosterParentType();

    /**
     * Returns the field is foster parent ID
     */
    boolean isFosterParentId();
    
    /**
     * Returns the field is category column
     */
    boolean isCategoryColumn();

    /**
     * Returns the field length.
     */
    int getLength();

    /**
     * Returns the field precision.
     */
    int getPrecision();

    /**
     * Returns the field scale.
     */
    int getScale();

    /**
     * Returns the field data column type.
     */
    ColumnType getColumnType();

    /**
     * Returns the field type.
     */
    Class<?> getFieldType();

    /**
     * Returns the field default value.
     */
    String getDefaultVal();

    /**
     * Returns if field has default value.
     */
    boolean isWithDefaultVal();

    /**
     * Returns true if the field is a primary key otherwise false;
     */
    boolean isPrimaryKey();

    /**
     * Returns true if field is list-only.
     */
    boolean isListOnly();

	/**
	 * Returns true if field is tenant ID.
	 */
	boolean isTenantId();

	/**
	 * Returns true if field is mapped.
	 */
	boolean isWithMapping();

	/**
	 * Return field mapping
	 */
	String getMapping();

    /**
     * Returns true if this schema info is the same with supplied info, otherwise
     * false.
     * 
     * @param sqlFieldSchemaInfo
     *            the supplied info
     */
    boolean isSameSchema(SqlFieldSchemaInfo sqlFieldSchemaInfo);

    /**
     * Returns the ignore foreign key constraint flag.
     */
    boolean isIgnoreFkConstraint();

    /**
     * Returns the foreign key entity schema info.
     */
    SqlEntitySchemaInfo getForeignEntityInfo();

    /**
     * Returns the foreign field schema information.
     */
    SqlFieldSchemaInfo getForeignFieldInfo();

    /**
     * Returns the foreign entity preferred table alias.
     */
    String getForeignEntityPreferredAlias();

    /**
     * Returns the foreign key field schema information.
     */
    SqlFieldSchemaInfo getForeignKeyFieldInfo();
}
