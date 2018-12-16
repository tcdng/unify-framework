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
package com.tcdng.unify.core.util;

import java.lang.annotation.Annotation;
import java.util.List;

import com.tcdng.unify.core.UnifyException;

/**
 * Annotated class repository.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface TypeRepository {

    /**
     * Gets classes of particular type which are annotated with specific annotation
     * and optionally in some particular packages.
     * 
     * @param classType
     *            the class type
     * @param annotationClass
     *            the annotation type
     * @param packages
     *            the packages to search
     * @return a list of classes in scanner annotation context that match supplied
     *         parameters.
     * @throws UnifyException
     *             if an error occurs
     */
    <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType, Class<? extends Annotation> annotationClass,
            String... packages) throws UnifyException;
}
