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
 * Component for managing runtime java classes. Compiled and saved classes are
 * maintained in groups. Each group has a single class loader which is
 * invalidated whenever the version of any of its members changes.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface RuntimeJavaClassManager extends UnifyComponent {

    /**
     * Compiles a Java class source from input stream and load resulting class to
     * JVM. Uses a one-time class loader which implies that classes with the same
     * name can be compiled and loaded.
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
     * Compiles a Java class source from reader and load resulting class to JVM.
     * Uses a one-time class loader which implies that classes with the same name
     * can be compiled and loaded.
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
     * Compiles a Java class source from string and load resulting class to JVM.
     * Uses a one-time class loader which implies that classes with the same name
     * can be compiled and loaded.
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
     * Compiles a Java class source from file and load resulting class to JVM. Uses
     * a one-time class loader which implies that classes with the same name can be
     * compiled and loaded.
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

    /**
     * Compiles a java class source and saves a java class under a specified group.
     * Performs no operation if supplied source is of a lower or equal version to
     * previously saved compilation.
     * 
     * @param groupName
     *            the group name
     * @param inputStreamJavaClassSource
     *            the source object
     * @return true if supplied source is a newer version and compiled and saved
     *         successfully otherwise false
     * @throws UnifyException
     *             if compilation fails. if an error occurs
     */
    boolean compileAndSaveJavaClass(String groupName, InputStreamJavaClassSource inputStreamJavaClassSource)
            throws UnifyException;

    /**
     * Compiles a java class source and saves a java class under a specified group.
     * Performs no operation if supplied source is of a lower or equal version to
     * previously saved compilation.
     * 
     * @param groupName
     *            the group name
     * @param readerJavaClassSource
     *            the reader object
     * @return true if supplied source is a newer version and compiled and saved
     *         successfully otherwise false
     * @throws UnifyException
     *             if compilation fails. if an error occurs
     */
    boolean compileAndSaveJavaClass(String groupName, ReaderJavaClassSource readerJavaClassSource)
            throws UnifyException;

    /**
     * Compiles a java class source and saves a java class under a specified group.
     * Performs no operation if supplied source is of a lower or equal version to
     * previously saved compilation.
     * 
     * @param groupName
     *            the group name
     * @param stringJavaClassSource
     *            the source object
     * @return true if supplied source is a newer version and compiled and saved
     *         successfully otherwise false
     * @throws UnifyException
     *             if compilation fails. if an error occurs
     */
    boolean compileAndSaveJavaClass(String groupName, StringJavaClassSource stringJavaClassSource)
            throws UnifyException;

    /**
     * Compiles a java class source and saves a java class under a specified group.
     * Performs no operation if supplied source is of a lower or equal version to
     * previously saved compilation.
     * 
     * @param groupName
     *            the group name
     * @param fileJavaClassSource
     *            the source object
     * @return true if supplied source is a newer version and compiled and saved
     *         successfully otherwise false
     * @throws UnifyException
     *             if compilation fails. if an error occurs
     */
    boolean compileAndSaveJavaClass(String groupName, FileJavaClassSource fileJavaClassSource) throws UnifyException;

    /**
     * Gets a java class that belongs to a group. Class object if fetched from the
     * group's class loader. Provided a group class loader has not been invalidated
     * by a successful save, this method will always return the same class object
     * for the same group name and class name.
     * 
     * @param groupName
     *            the group name
     * @param className
     *            the class name
     * @return the java class object
     * @throws UnifyException
     *             if class is not found in group. if an error occurs
     */
    Class<?> getSavedJavaClass(String groupName, String className) throws UnifyException;

    /**
     * Gets the version of a saved java class belonging to s group.
     * 
     * @param groupName
     *            the group name
     * @param className
     *            the class name
     * @return the saved class version if found otherwise zero
     * @throws UnifyException
     *             if an error occurs
     */
    long getSavedJavaClassVersion(String groupName, String className) throws UnifyException;
}
