/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.common.annotation.AnnotationConstants;

/**
 * Used for marking, naming and optionally describing a unify component. The
 * framework configuration utilies uses this annotation to identify components
 * during a classpath or library scan.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    /**
     * Used to set the component name if no other element is supplied. Can not be
     * used in conjunction with name element.
     */
    String value() default AnnotationConstants.NONE;

    /**
     * Used to set the component name. Can not be used in conjunction with value
     * element.
     */
    String name() default AnnotationConstants.NONE;

    /**
     * Used for specifying description of a component. This element is optional.
     */
    String description() default AnnotationConstants.NONE;
}
