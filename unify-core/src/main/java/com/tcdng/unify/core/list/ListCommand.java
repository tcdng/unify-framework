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
package com.tcdng.unify.core.list;

import java.util.List;
import java.util.Locale;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * List command interface used for obtaining a list of listable objects.
 * <p>
 * Typically used to populate multi-option presentation components like
 * single-select or multi-select controls.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ListCommand<T extends ListParam> extends UnifyComponent {

    /**
     * Executes list command for specified locale and parameters.
     * 
     * @param locale
     *            the locale
     * @param param
     *            the command parameter
     * @return the list of listables
     * @throws UnifyException
     *             if an error occurs
     */
    List<? extends Listable> execute(Locale locale, T param) throws UnifyException;

    /**
     * Returns the command parameter type
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Class<T> getParamType() throws UnifyException;
}
