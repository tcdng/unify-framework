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
package com.tcdng.unify.core.database;

import java.util.Map;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Restriction;

/**
 * A data source dialect component.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface DataSourceDialect extends UnifyComponent {

    /**
     * Sets the name of the data source utilizing this dialect object.
     * 
     * @param dataSourceName the data source name
     */
    void setDataSourceName(String dataSourceName);
    
    /**
     * Gets the name of the data source utilizing this dialect object.
     */
    String getDataSourceName();
    
	/**
	 * Checks if unify views are supported.
	 * 
	 * @return true if support unify views.
	 * @throws UnifyException if an error occurs
	 */
	boolean isSupportUnifyViews() throws UnifyException;

	/**
	 * Set support unify views flag.
	 * 
	 * @param supportUnifyViews the flag to set
	 * @throws UnifyException if an error occurs
	 */
	void setSupportUnifyViews(boolean supportUnifyViews) throws UnifyException;
    
    /**
     * Checks if object are all renamed to lower case in dialect.
     * 
     * @return true if all lower case objects
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isAllObjectsInLowerCase() throws UnifyException;

    /**
     * Set all objects in lower case flag.
     * 
     * @param objectsInLowerCase
     *            the flag to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setAllObjectsInLowerCase(boolean objectsInLowerCase) throws UnifyException;

    /**
     * Translates specified restriction to data source dialect.
     * 
     * @param restriction
     *            the restriction to translate
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
    String translateNativeSqlParam(Object value) throws UnifyException;

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
