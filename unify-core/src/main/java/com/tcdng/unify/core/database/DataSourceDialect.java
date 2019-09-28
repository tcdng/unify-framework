/*
 * Copyright 2018-2019 The Code Department.
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
package com.tcdng.unify.core.database;

import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Restriction;

/**
 * A data source dialect component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface DataSourceDialect extends UnifyComponent {
    /**
     * Translates specified criteria to data source dialect.
     * 
     * @param criteria
     *            the criteria to translate
     * @return the translation
     * @throws UnifyException
     *             if an error occurs
     */
    String translateCriteria(Restriction restriction) throws UnifyException;

    /**
     * Translates specified value to data source dialect.
     * 
     * @param value
     *            the value to translate
     * @return the translation
     * @throws UnifyException
     *             if an error occurs
     */
    String translateValue(Object value) throws UnifyException;

    /**
     * Generates a native query.
     * 
     * @param query
     *            the record query object
     * @return the generated native SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateNativeQuery(Query<?> query) throws UnifyException;

    /**
     * Generates a native query SQL.
     * 
     * @param query
     *            the query object
     * @return the generated native SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateNativeQuery(NativeQuery query) throws UnifyException;;

    /**
     * Gets field to native column map for specified record class.
     * 
     * @param clazz
     *            the data object type
     * @return the field to column map
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, String> getFieldToNativeColumnMap(Class<? extends Entity> clazz) throws UnifyException;
}
