/*
 * Copyright 2014 The Code Department
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
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;

/**
 * Annotation for indicating that a field is a version number.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Version {
	/**
	 * The field column name. If not set, the system generates a column name using
	 * the field name.
	 */
	String name() default AnnotationConstants.NONE;

	/** Column position */
	int position() default DefaultColumnPositionConstants.VERSION_POSITION;
}
