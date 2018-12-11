/*
 * Copyright 2018 The Code Department
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

/**
 * Annotation for marking a field as a view-only property. A view-only property
 * is a read-only property that binds to a foreign property usually through a
 * view.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListOnly {

	/** The name of the foreign key field */
	String key();

	/** The property of the foreign entity the view-only field binds to */
	String property();

	/**
	 * The field column name. If not set, the system generates a column name using
	 * the field name.
	 */
	String name() default AnnotationConstants.NONE;
}
