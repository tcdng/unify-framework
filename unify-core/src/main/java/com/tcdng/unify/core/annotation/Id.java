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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tcdng.unify.core.constant.AnnotationConstants;
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;

/**
 * Annotation for indicating that a field is a primary key.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    /**
     * The field column name. If not set, the underlying datasource dialect
     * generates a column name using the field name. The default implementation
     * generates one by appending the table name with a "ID" suffix. So for a table
     * with name "BOOK" the dialect would produce "BOOKID".
     */
    String name() default AnnotationConstants.NONE;

    /** The length of the field. Applies to character columns. */
    int length() default -1;

    /** Column position */
    int position() default DefaultColumnPositionConstants.ID_POSITION;
}
