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
package com.tcdng.unify.core.cache;

import com.tcdng.unify.core.UnifyException;

/**
 * A transform cache defines extra methods for transforming cached objects
 * during a put or get operation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface TransformCache<T, U, V> extends Cache<T, U> {

    /**
     * Puts an object in cache with specified key and expiration period and returns
     * a transformed version of the cached object.
     * 
     * @param key
     *            the key to cache object with
     * @param object
     *            the object to cache
     * @param expiryPeriod
     *            the expiration period in seconds
     * @return the transformed version of the cached object
     * @throws UnifyException
     *             if an error occurs
     */
    V transformPut(T key, U object, long expiryPeriod) throws UnifyException;

    /**
     * Gets a transformed cached object with specified key. If cached object with
     * key is found, the object is transformed from U to V and returned.
     * 
     * @param key
     *            The object key
     * @return the transformed cached object
     * @throws UnifyException
     *             If an error occurs
     */
    V getTransformed(T key) throws UnifyException;
}
