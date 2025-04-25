/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for overriding column definition in super class.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnOverride {

    String field();

    ColumnType type() default ColumnType.AUTO;

    String name() default AnnotationConstants.NONE;

    String transformer() default AnnotationConstants.NONE;

    int length() default -1;

    int precision() default -1;

    int scale() default -1;

    boolean nullable() default false;
}
