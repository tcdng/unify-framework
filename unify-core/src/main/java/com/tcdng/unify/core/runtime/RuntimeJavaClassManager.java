/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.core.runtime;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.ClassForNameProvider;

/**
 * Component for managing runtime java classes. Compiled and saved classes are
 * maintained in groups. Each group has a single class loader which is
 * invalidated whenever the version of any of its members changes.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface RuntimeJavaClassManager extends UnifyComponent, ClassForNameProvider {

    /**
     * Compiles a Java class source from input stream and load resulting class to
     * JVM. Uses a one-time class loader which implies that classes with the same
     * name can be compiled and loaded.
     * 
     * @param className
     *                  the class name
     * @param is
     *                  input stream object
     * @return the compiled and loaded class
     * @throws UnifyException
     *                        if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, InputStream is) throws UnifyException;

    /**
     * Compiles a Java class source from reader and load resulting class to JVM.
     * Uses a one-time class loader which implies that classes with the same name
     * can be compiled and loaded.
     * 
     * @param className
     *                  the class name
     * @param reader
     *                  reader object
     * @return the compiled and loaded class
     * @throws UnifyException
     *                        if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, Reader reader) throws UnifyException;

    /**
     * Compiles a Java class source from string and load resulting class to JVM.
     * Uses a one-time class loader which implies that classes with the same name
     * can be compiled and loaded.
     * 
     * @param className
     *                  the class name
     * @param src
     *                  string source object
     * @return the compiled and loaded class
     * @throws UnifyException
     *                        if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, String src) throws UnifyException;

    /**
     * Compiles a Java class source and load resulting class to JVM.
     * Uses a one-time class loader which implies that classes with the same name
     * can be compiled and loaded.
     * 
     * @param source
     *                  the java source object
     * @return the compiled and loaded class
     * @throws UnifyException
     *                        if an error occurs
     */
    Class<?> compileAndLoadJavaClass(JavaClassSource source) throws UnifyException;

    /**
     * Compiles a list of Java class sources and load resulting classes to JVM. Uses
     * a one-time class loader which implies that classes with the same name can be
     * compiled and loaded.
     * 
     * @param clazz
     *                   the class type
     * @param sourceList
     *                   the source list
     * @return the compiled and loaded classes
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> List<Class<? extends T>> compileAndLoadJavaClasses(Class<T> clazz, List<JavaClassSource> sourceList) throws UnifyException;

    /**
     * Compiles a Java class source from file and load resulting class to JVM. Uses
     * a one-time class loader which implies that classes with the same name can be
     * compiled and loaded.
     * 
     * @param className
     *                  the class name
     * @param file
     *                  file object
     * @return the compiled and loaded class
     * @throws UnifyException
     *                        if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, File file) throws UnifyException;

    /**
     * Resets manager.
     */
    void reset();
}
