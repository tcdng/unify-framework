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
package com.tcdng.unify.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.constant.AnnotationConstants;

/**
 * Annotation for binding an entity to an RDBMS table.
 * <p>
 * This annotation can be used to specify the database names of the table's
 * attributes. The table name, associated view, primary key column, version
 * number column can be specified with the system generating a value if any of
 * these can not be resolved.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * The application data source that table belongs to. Defaults to
     * {@link ApplicationComponents#APPLICATION_DATASOURCE}
     */
    String datasource() default ApplicationComponents.APPLICATION_DATASOURCE;

    /**
     * Optional name of schema that table belongs to
     */
    String schema() default AnnotationConstants.NONE;
    
    /** The name of the table when no other annotation method is set */
    String value() default AnnotationConstants.NONE;

    /** The name of the table. */
    String name() default AnnotationConstants.NONE;

    /** The description of the table */
    String description() default AnnotationConstants.NONE;

    /**
     * The view associated with this table. A value is generated if not supplied and
     * annotated entity has view-only properties. The default implementation
     * generates one by appending the table name with a "V_" prefix. So for a table
     * with name "BOOK" the dialect would produce "V_BOOK".
     */
    String view() default AnnotationConstants.NONE;

    /**
     * The table primary key column name. If set, overrides {@link Id} name of
     * annotated primary key entity property. On a wider note, if no column name can
     * be resolved from this annotation and the {@link Id} annotation, the system
     * generates a column name. This is useful when a subclass wants to override the
     * mapping of an inherited ID field.
     */
    String idColumn() default AnnotationConstants.NONE;

    /**
     * The table primary key column name. Effective only when there is a field
     * annotated with {@link Version}. If set, overrides column name of annotated
     * version entity property. If no column name can be resolved from this
     * annotation and the version annotation, the system generates a column name.
     * This is useful when a subclass wants to override the mapping of an inherited
     * version number field.
     */
    String versionColumn() default AnnotationConstants.NONE;

    /** Table unique constraints */
    UniqueConstraint[] uniqueConstraints() default {};

    /** Table indexes */
    Index[] indexes() default {};

    /** Column overrides */
    ColumnOverride[] columnOverrides() default {};

    /** Foreign key overrides */
    ForeignKeyOverride[] foreignKeyOverrides() default {};
}
