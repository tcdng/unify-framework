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
import com.tcdng.unify.core.criterion.RestrictionType;

/**
 * Defines a view restriction.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewRestriction {

    /** The restriction type */
    RestrictionType type();

    /**
     * The property of left table in restriction operation.
     * Expected format is [Table Alias].[propertyName]. Example: T1.id
     */
    String leftProperty();

    /**
     * Optional property of right table in restriction operation.
     * Expected format is [Table Alias].[propertyName]. Example: T2.id
     */
    String rightProperty() default AnnotationConstants.NONE;

    /**
     * Optional operand values.
     */
    String[] values() default {};
}
