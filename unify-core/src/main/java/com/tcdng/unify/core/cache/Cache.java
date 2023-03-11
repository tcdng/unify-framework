/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A cache of objects that can be retrieved by key. A cached object can be
 * retrieved by supplying its key. Expired cached objects are removed
 * automatically based on expiration periods specified implicitly or explicitly
 * during put operations.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Cache<T, U> extends UnifyComponent {

    /**
     * Puts an object in cache with specified name using default expiration. period.
     * 
     * @param key
     *            the object key
     * @param object
     *            The object to cache
     * @return the cached object
     * @throws UnifyException
     *             if an error occurs
     */
    U put(T key, U object) throws UnifyException;

    /**
     * Puts an object in cache with specified key and expiration period.
     * 
     * @param key
     *            The object key
     * @param object
     *            The object to cache
     * @param expirationPeriod
     *            the expiration period in milliseconds
     * @return the cached object
     * @throws UnifyException
     *             if an error occurs
     */
    U put(T key, U object, long expirationPeriod) throws UnifyException;

    /**
     * Gets a cached object with specified key.
     * 
     * @param key
     *            the object key
     * @return the cached object
     * @throws UnifyException
     *             if an error occurs
     */
    U get(T key) throws UnifyException;

    /**
     * Removes cache item with specified key.
     * 
     * @param key
     *            the item key
     * @return the removed cached object
     * @throws UnifyException
     *             if an error occurs
     */
    U remove(T key) throws UnifyException;

    /**
     * Removes all cached items.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clear() throws UnifyException;

    /**
     * Returns the number of objects in this cache.
     */
    int size();

    /**
     * Removes expired cache objects.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void removeExpiredCacheEntries() throws UnifyException;
}
