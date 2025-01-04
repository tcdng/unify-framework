/*
 * Copyright 2018-2024 The Code Department.
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

import com.tcdng.unify.common.annotation.AnnotationConstants;

/**
 * Annotation for declaring a parameter.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    /** The parameter name that unique in a parameter set. */
    String value() default AnnotationConstants.NONE;

    /** The parameter name that unique in a parameter set. */
    String name() default AnnotationConstants.NONE;

    /** The parameter description. Can be a message key. */
    String description() default AnnotationConstants.NONE;

    /** The parameter editor. Used for presentation purposes. */
    String editor() default AnnotationConstants.NONE;

    /** The parameter data type. */
    Class<?> type() default String.class;

    /** Indicates the parameter is mandatory */
    boolean mandatory() default false;

    /** The parameter order index */
    int order() default 0;
}
