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
package com.tcdng.unify.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used for mapping a result name to a set of page-controller responses.
 * Responses are defined using standard UPL.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultMapping {
    /** The result mapping name */
    String name();

    /** List of page bean responses */
    String[] response() default {};
}
