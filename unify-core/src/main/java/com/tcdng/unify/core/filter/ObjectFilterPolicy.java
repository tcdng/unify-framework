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
package com.tcdng.unify.core.filter;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.data.ValueStore;

/**
 * Object filter policy.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ObjectFilterPolicy {

    /**
     * Perform value store field matching.
     * 
     * @param valueStore
     *            the value store object
     * @param restriction
     *            restriction the restriction field
     * @return true restriction matches the value store field
     * @throws UnifyException
     *             if an error occurs
     */
    boolean match(ValueStore valueStore, Restriction restriction) throws UnifyException;

    /**
     * Perform bean field matching.
     * 
     * @param bean
     *            the bean object
     * @param restriction
     *            restriction the restriction field
     * @return true restriction matches bean field
     * @throws UnifyException
     *             if an error occurs
     */
    boolean match(Object bean, Restriction restriction) throws UnifyException;
}
