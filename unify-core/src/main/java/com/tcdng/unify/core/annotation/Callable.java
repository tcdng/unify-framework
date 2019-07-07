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

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.constant.AnnotationConstants;

/**
 * Annotation that indicates a callable procedure.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Callable {

    /**
     * The application data source that procedure belongs to. Defaults to
     * {@link ApplicationComponents#APPLICATION_DATASOURCE}
     */
    String datasource() default ApplicationComponents.APPLICATION_DATASOURCE;

    /**
     * Optional name of schema that procedure belongs to
     */
    String schema() default AnnotationConstants.NONE;

    /**
     * The procedure name.
     */
    String procedure();

    /**
     * Comma-separated list of procedure input/output parameters. Parameter names
     * are the names of fields of callable type that are annotated with
     * {@link InOutParam}, {@link InParam} or {@link OutParam}. Order of parameter names in the list
     * must match the order of parameters of the procedure.
     */
    String params() default AnnotationConstants.NONE;

    /**
     * An array of result types returned by callable. Types must be annotated with
     * {@link CallableResult} and must have fields annotated with {@link ResultField}.
     */
    Class<?>[] results() default {};
    
    /**
     * Callable return type that indicates callable has a return value. Applies only to non-AUTO values.
     */
    CallableDataType returnType() default CallableDataType.AUTO;
}
