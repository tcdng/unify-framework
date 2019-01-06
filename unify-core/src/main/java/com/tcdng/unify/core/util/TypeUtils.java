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
package com.tcdng.unify.core.util;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Provides utility methods for type information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TypeUtils {

    protected TypeUtils() {

    }

    public static TypeRepository buildTypeRepositoryFromClasspath() throws UnifyException {
        try {
            AnnotationDB classpathDB = new AnnotationDB();
            classpathDB.setScanFieldAnnotations(false);
            classpathDB.setScanMethodAnnotations(false);
            classpathDB.setScanParameterAnnotations(false);

            URL[] urls = ClasspathUrlFinder.findClassPaths();
            classpathDB.scanArchives(urls);
            return new TypeRepositoryImpl(classpathDB);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.ANNOTATIONUTIL_ERROR);
        }
    }

    public static class TypeRepositoryImpl implements TypeRepository {

        private AnnotationDB annotationDB;

        public TypeRepositoryImpl(AnnotationDB annotationDB) {
            this.annotationDB = annotationDB;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType,
                Class<? extends Annotation> annotationClass, String... packages) throws UnifyException {
            List<Class<? extends T>> resultList = new ArrayList<Class<? extends T>>();
            try {
                Set<String> annotatedClassNames = this.annotationDB.getAnnotationIndex().get(annotationClass.getName());

                if (annotatedClassNames != null && !annotatedClassNames.isEmpty()) {
                    if (packages.length > 0) {
                        for (String name : annotatedClassNames) {
                            for (String packageName : packages) {
                                if (name.startsWith(packageName)) {
                                    Class<?> clazz = Class.forName(name);
                                    if (classType.isAssignableFrom(clazz)) {
                                        resultList.add((Class<? extends T>) clazz);
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        for (String name : annotatedClassNames) {
                            Class<?> clazz = Class.forName(name);
                            if (classType.isAssignableFrom(clazz)) {
                                resultList.add((Class<? extends T>) clazz);
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new UnifyException(e, UnifyCoreErrorConstants.ANNOTATIONUTIL_ERROR);
            }
            return resultList;
        }
    }

}
