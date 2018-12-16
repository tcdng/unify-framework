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
package com.tcdng.unify.core.list;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Listable;

/**
 * Used for managing the retrieval of lists from list commands.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ListManager extends UnifyComponent {

    /**
     * Returns a list of listables from specified list command for a locale.
     * 
     * @param locale
     *            the locale
     * @param listName
     *            the list command name
     * @param params
     *            optional parameters
     * @throws UnifyException
     *             if list is unknown. If an error occurs
     */
    List<? extends Listable> getList(Locale locale, String listName, Object... params) throws UnifyException;

    /**
     * Returns a map of key to description values from specified list command for a
     * locale.
     * 
     * @param locale
     *            the request locale
     * @param listName
     *            the list command name
     * @param params
     *            optional parameters
     * @throws UnifyException
     *             if list is unknown. If an error occurs
     */
    Map<String, String> getListMap(Locale locale, String listName, Object... params) throws UnifyException;

    /**
     * Returns the description of a list item of a list.
     * 
     * @param locale
     *            the request locale
     * @param listKey
     *            the list key of the item to fetch description for
     * @param listName
     *            the name of the list command
     * @param params
     *            optional request parameters
     * @throws UnifyException
     *             if list is unknown. If an error occurs
     */
    String getListKeyDescription(Locale locale, String listKey, String listName, Object... params)
            throws UnifyException;
}
