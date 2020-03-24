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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Component for managing runtime java classes.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface RuntimeJavaClassManager extends UnifyComponent {

    /**
     * Compiles a Java class source from input stream and load resulting class to
     * JVM
     * 
     * @param className
     *            the class name
     * @param is
     *            input stream object
     * @return the compiled and loaded class
     * @throws UnifyException
     *             if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, InputStream is) throws UnifyException;

    /**
     * Compiles a Java class source from reader and load resulting class to JVM
     * 
     * @param className
     *            the class name
     * @param reader
     *            reader object
     * @return the compiled and loaded class
     * @throws UnifyException
     *             if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, Reader reader) throws UnifyException;

    /**
     * Compiles a Java class source from string and load resulting class to JVM
     * 
     * @param className
     *            the class name
     * @param string
     *            string object
     * @return the compiled and loaded class
     * @throws UnifyException
     *             if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, String string) throws UnifyException;

    /**
     * Compiles a Java class source from file and load resulting class to JVM
     * 
     * @param className
     *            the class name
     * @param file
     *            file object
     * @return the compiled and loaded class
     * @throws UnifyException
     *             if an error occurs
     */
    Class<?> compileAndLoadJavaClass(String className, File file) throws UnifyException;
}
