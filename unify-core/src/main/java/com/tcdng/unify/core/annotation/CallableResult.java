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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tcdng.unify.core.constant.AnnotationConstants;

/**
 * Annotation that indicates a callable result.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallableResult {

    /**
     * Comma-separated list of result fields. Field names are the names of fields of
     * callable type that are annotated with {@link ResultField}. Order of field
     * names in the list must match the order columns in the procedure result
     * select. If this value is set, indexing is used in reading results otherwise
     * field column names are used.
     */
    String fields() default AnnotationConstants.NONE;
}
