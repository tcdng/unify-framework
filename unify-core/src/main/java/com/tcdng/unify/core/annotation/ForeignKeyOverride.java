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
package com.tcdng.unify.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tcdng.unify.core.constant.AnnotationConstants;
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;
import com.tcdng.unify.core.database.Entity;

/**
 * Annotation for overriding foreign key definition in super class.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKeyOverride {

    /** The name of foreign key */
    String key();

    /** The foreign record type that key references. */
    Class<? extends Entity> foreignType();

    /**
     * The field column name. If not set, the system generates a column name using
     * the field name.
     */
    String name() default AnnotationConstants.NONE;

    /** Indicates the field is nullable. */
    boolean nullable() default false;

    /**
     * Indicates child records are deleted on parent delete. Ignore for child lists
     * since always true
     */
    boolean onDeleteCascade() default false;

    /** Column position */
    int position() default DefaultColumnPositionConstants.FK_POSITION;
}
