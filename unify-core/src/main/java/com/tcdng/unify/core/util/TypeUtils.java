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
package com.tcdng.unify.core.util;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Provides utility methods for type information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TypeUtils {

    private static TypeRepository classPathTypeRepository;

    protected TypeUtils() {

    }

    public static TypeRepository getTypeRepositoryFromClasspath(URL... baseUrls) throws UnifyException {
        if (classPathTypeRepository == null) {
            synchronized (TypeUtils.class) {
                if (classPathTypeRepository == null) {
                    try {
                        AnnotationDB classpathDB = new AnnotationDB();
                        classpathDB.setScanFieldAnnotations(false);
                        classpathDB.setScanMethodAnnotations(false);
                        classpathDB.setScanParameterAnnotations(false);

                        List<URL> finalUrls = new ArrayList<URL>();
                        if (baseUrls != null) {
                            finalUrls.addAll(Arrays.asList(baseUrls));
                        }
                        
                        URL[] urls = ClasspathUrlFinder.findClassPaths();
                        if (urls != null) {
                            finalUrls.addAll(Arrays.asList(urls));
                        }

                        classpathDB.scanArchives(DataUtils.toArray(URL.class, finalUrls));
                        classPathTypeRepository = new TypeRepositoryImpl(classpathDB);
                    } catch (Exception e) {
                        throw new UnifyException(e, UnifyCoreErrorConstants.ANNOTATIONUTIL_ERROR);
                    }
                }
            }
        }

        return classPathTypeRepository;
    }

    protected static class TypeRepositoryImpl implements TypeRepository {

        private AnnotationDB annotationDB;

        public TypeRepositoryImpl(AnnotationDB annotationDB) {
            this.annotationDB = annotationDB;
        }

        @Override
        public <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType,
                Class<? extends Annotation> annotationClass, String... packages) throws UnifyException {
            return getAnnotatedClasses(classType, annotationClass, false, packages);
        }

        @Override
        public <T> List<Class<? extends T>> getAnnotatedClassesExcluded(Class<T> classType,
                Class<? extends Annotation> annotationClass, String... excludedPackages) throws UnifyException {
            return getAnnotatedClasses(classType, annotationClass, true, excludedPackages);
        }

        @SuppressWarnings("unchecked")
        private <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType,
                Class<? extends Annotation> annotationClass, boolean exclude, String... packages)
                throws UnifyException {
            List<Class<? extends T>> resultList = new ArrayList<Class<? extends T>>();
            Set<String> annotatedClassNames = annotationDB.getAnnotationIndex().get(annotationClass.getName());

            if (annotatedClassNames != null && !annotatedClassNames.isEmpty()) {
                if (packages.length > 0) {
                    if (exclude) {
                        for (String name : annotatedClassNames) {
                            for (String packageName : packages) {
                                if (!name.startsWith(packageName)) {
                                    Class<?> clazz = ReflectUtils.classForName(name);
                                    if (classType.isAssignableFrom(clazz)) {
                                        resultList.add((Class<? extends T>) clazz);
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        for (String name : annotatedClassNames) {
                            for (String packageName : packages) {
                                if (name.startsWith(packageName)) {
                                    Class<?> clazz = ReflectUtils.classForName(name);
                                    if (classType.isAssignableFrom(clazz)) {
                                        resultList.add((Class<? extends T>) clazz);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    for (String name : annotatedClassNames) {
                        Class<?> clazz = ReflectUtils.classForName(name);
                        if (classType.isAssignableFrom(clazz)) {
                            resultList.add((Class<? extends T>) clazz);
                        }
                    }
                }
            }
            return resultList;
        }
    }

}
