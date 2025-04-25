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

import com.tcdng.unify.core.UnifyException;

/**
 * Value store policy.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ValueStorePolicy {

    /**
     * Executed on retrieval of value from store
     * 
     * @param valueStore
     *                   the calling value store
     * @param name
     *                   the value name
     * @param val
     *                   the retrieved
     * @return the retrieved object
     * @throws if
     *         an error occurs
     */
    Object onRetrieve(ValueStore valueStore, String name, Object val) throws UnifyException;

    /**
     * Executed on store of value from store
     * 
     * @param valueStore
     *                   the calling value store
     * @param name
     *                   the value name
     * @param val
     *                   the stored
     * @return the object to store
     * @throws if
     *         an error occurs
     */
    Object onStore(ValueStore valueStore, String name, Object val) throws UnifyException;
}
