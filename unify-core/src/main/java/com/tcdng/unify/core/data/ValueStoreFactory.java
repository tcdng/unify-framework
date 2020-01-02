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
package com.tcdng.unify.core.data;

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Value store factory.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ValueStoreFactory extends UnifyComponent {

    /**
     * Gets a value store instance based on a supplied source object.
     * 
     * @param sourceObject
     *            the value store source object
     * @return A null value is returned if supplied source object is null.
     * @throws UnifyException
     *             if an error occurs
     */
    ValueStore getValueStore(Object sourceObject) throws UnifyException;

    /**
     * Gets a value store instance based on a supplied source object.
     * 
     * @param sourceObject
     *            the value store source object
     * @param dataIndex
     *            the data index
     * @return A null value is returned if supplied source object is null.
     * @throws UnifyException
     *             if an error occurs
     */
    ValueStore getValueStore(Object sourceObject, int dataIndex) throws UnifyException;

    /**
     * Gets an array value store instance based on a supplied source object.
     * 
     * @param sourceObject
     *            the value store source object.
     * @param dataIndex
     *            the data index
     * @return A null value is returned if supplied source object is null.
     * @throws UnifyException
     *             if an error occurs
     */
    ValueStore getArrayValueStore(Object[] sourceObject, int dataIndex) throws UnifyException;

    /**
     * Gets an list value store instance based on a supplied source object.
     * 
     * @param clazz
     *            the list data type
     * @param sourceObject
     *            the value store source object.
     * @param dataIndex
     *            the data index
     * @return A null value is returned if supplied source object is null.
     * @throws UnifyException
     *             if an error occurs
     */
    <T> ValueStore getListValueStore(Class<T> clazz, List<T> sourceObject, int dataIndex) throws UnifyException;
}
