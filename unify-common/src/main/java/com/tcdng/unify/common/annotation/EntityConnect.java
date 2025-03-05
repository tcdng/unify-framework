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
package com.tcdng.unify.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tcdng.unify.common.constants.ConnectEntityBaseType;

/**
 * Annotation for specifying entity connection.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityConnect {
    
	ConnectEntityBaseType base() default ConnectEntityBaseType.BASE_ENTITY;
	
	String application();
	
	String entity();

	String datasource() default AnnotationConstants.NONE;

	String id() default AnnotationConstants.NONE;

	String versionNo() default AnnotationConstants.NONE;

	String description() default AnnotationConstants.NONE;

	String actionPolicy() default AnnotationConstants.NONE;
}
