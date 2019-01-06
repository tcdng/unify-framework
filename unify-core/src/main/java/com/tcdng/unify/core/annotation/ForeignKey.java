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

import com.tcdng.unify.core.constant.AnnotationConstants;
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;
import com.tcdng.unify.core.database.Entity;

/**
 * Annotation for indicating that a field is a foreign key. Also provides a
 * means for specifying attributes of the field.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {

    /** The foreign entity that key references. */
    Class<? extends Entity> value() default Entity.class;

    /** The foreign entity that key references. */
    Class<? extends Entity> type() default Entity.class;

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

    /**
     * Indicates annotated field should be considered a foreign key for child lists.
     */
    boolean childKey() default true;

    /**
     * Indicates the foreign key constraint be enforced if global enforcement is
     * true, Defaults to true
     */
    boolean enforce() default true;

    /** Column position */
    int position() default DefaultColumnPositionConstants.FK_POSITION;
}
