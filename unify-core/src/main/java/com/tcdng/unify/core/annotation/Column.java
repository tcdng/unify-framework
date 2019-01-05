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
package com.tcdng.unify.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tcdng.unify.core.constant.AnnotationConstants;
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;

/**
 * Annotation for indicating that a field is a table column. Also provides a
 * means for specifying attributes of the field.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /** The field {@link ColumnType}. */
    ColumnType type() default ColumnType.AUTO;

    /**
     * The field column name. If not set, the system generates a column name using
     * the field name.
     */
    String name() default AnnotationConstants.NONE;

    /** The name of the field transformer. */
    String transformer() default AnnotationConstants.NONE;

    /** The length of the field. Applied for character columns. */
    int length() default -1;

    /** The precision of the field. Applied to numeric columns. */
    int precision() default -1;

    /** The scale of the field. Applied to numeric columns */
    int scale() default -1;

    /** Indicates the field is nullable. */
    boolean nullable() default false;

    /** Specifies the column default value */
    String defaultVal() default AnnotationConstants.NONE;
    
    /** Column position */
    int position() default DefaultColumnPositionConstants.COLUMN_POSITION;
}
