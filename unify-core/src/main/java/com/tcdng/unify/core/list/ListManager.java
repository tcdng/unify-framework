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
import java.util.Map;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Used for managing the retrieval of lists from list commands.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ListManager extends UnifyComponent {

    /**
     * Gets a static list enumeration type.
     * 
     * @param listName
     *                 the list name
     * @return the list enumeration type
     * @throws UnifyException
     *                        if static list with name is unknown. if an error occurs
     */
    Class<? extends EnumConst> getStaticListEnumType(String listName) throws UnifyException;
    
    /**
     * Returns a list of all static lists for a locale.
     * 
     * @param locale
     *               the locale
     * @throws UnifyException
     *                        if list is unknown. If an error occurs
     */
    List<? extends Listable> getAllStaticLists(Locale locale) throws UnifyException;

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
     * Returns a list of listables from specified list command for a locale.
     * 
     * @param locale
     *                 the locale
     * @param listName
     *                 the list command name
     * @param filter
     *                 optional filter string
     * @param limit
     *                 limits number of returned records if greater than zero
     * @param params
     *                 optional parameters
     * @throws UnifyException
     *                        if list is unknown. If an error occurs
     */
    List<? extends Listable> getSubList(Locale locale, String listName, String filter, int limit, Object... params)
            throws UnifyException;

    /**
     * Returns a list of listables from specified list command for a locale with
     * case-insensitive filtering.
     * 
     * @param locale
     *                 the locale
     * @param listName
     *                 the list command name
     * @param filter
     *                 optional filter string
     * @param limit
     *                 limits number of returned records if greater than zero
     * @param params
     *                 optional parameters
     * @throws UnifyException
     *                        if list is unknown. If an error occurs
     */
    List<? extends Listable> getCaseInsensitiveSubList(Locale locale, String listName, String filter, int limit,
            Object... params) throws UnifyException;

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
    Map<String, Listable> getListMap(Locale locale, String listName, Object... params) throws UnifyException;

    /**
     * Returns a map of key to description values from specified list command for a
     * locale.
     * 
     * @param locale
     *                 the request locale
     * @param listName
     *                 the list command name
     * @param filter
     *                 optional filter string
     * @param limit
     *                 limits number of returned records if greater than zero
     * @param params
     *                 optional parameters
     * @throws UnifyException
     *                        if list is unknown. If an error occurs
     */
    Map<String, Listable> getSubListMap(Locale locale, String listName, String filter, int limit, Object... params)
            throws UnifyException;

    /**
     * Returns the list item of a list by key.
     * 
     * @param locale
     *            the request locale
     * @param listName
     *            the name of the list command
     * @param listKey
     *            the list key of the item to fetch
     * @param params
     *            optional request parameters
     * @throws UnifyException
     *             if list is unknown. If an error occurs
     */
    Listable getListItemByKey(Locale locale, String listName, String listKey, Object... params)
            throws UnifyException;

    /**
     * Returns the list item of a list by description.
     * 
     * @param locale
     *            the request locale
     * @param listName
     *            the name of the list command
     * @param listDesc
     *            the list description of the item to fetch
     * @param params
     *            optional request parameters
     * @throws UnifyException
     *             if list is unknown. If an error occurs
     */
    Listable getListItemByDescription(Locale locale, String listName, String listDesc, Object... params)
            throws UnifyException;
}
