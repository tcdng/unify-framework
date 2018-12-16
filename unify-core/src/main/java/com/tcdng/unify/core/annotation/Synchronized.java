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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for synchronizing a business module component method. All access
 * to a method marked with this annotation is synchronized. Can also be used to
 * synchronized access to multiple methods by using the same lock value. For
 * instance, we want synchronized access to two methods, we mark them like this:
 * 
 * <pre>
 * <code>
 *  {@literal @}Synchronized("same-lock")
 *  public void methodA() {
 *  ...
 *  }
 *  
 *  {@literal @}Synchronized("same-lock")
 *  public void methodB() {
 *  ...
 *  }
 * </code>
 * </pre>
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Synchronized {

    String value();
}
