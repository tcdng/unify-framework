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
package com.tcdng.unify.core.list;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Listable;

/**
 * Search provider component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SearchProvider extends ListCommand<SearchProviderParams> {

    /**
     * Performs a search using supplied filter.
     * 
     * @param filter
     *            the search filter
     * @return search result
     * @throws UnifyException
     *             if an error occurs
     */
    List<? extends Listable> search(String filter) throws UnifyException;

    /**
     * Returns the search provider key property.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getKeyProperty() throws UnifyException;

    /**
     * Returns the search provider description property.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getDescProperty() throws UnifyException;
}
