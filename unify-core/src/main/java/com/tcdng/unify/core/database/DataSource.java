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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A data source component. Entity types are defined at this level. This is
 * logical and configuration is easier.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface DataSource extends UnifyComponent {

	String ENTITYLIST_PROPERTY = "entityList";
	
	/**
	 * Checks if datasource is read-only
	 * 
	 * @return true if datasource is read-only otherwise false
     * @throws UnifyException
     *             if an error occurs
	 */
	boolean isReadOnly() throws UnifyException;
	
	/**
	 * Checks if datasource is managed
	 * 
	 * @return true if datasource is managed otherwise false
     * @throws UnifyException
     *             if an error occurs
	 */
	boolean isManaged() throws UnifyException;

	/**
	 * Checks if datasource initialization is delayed.
	 * 
	 * @return true if datasource is delayed otherwise false
     * @throws UnifyException
     *             if an error occurs
	 */
	boolean isInitDelayed() throws UnifyException;
	
    /**
     * Gets the data source preferred name.
     * 
     * @return the preferred name otherwise null
     */
    String getPreferredName();

    /**
     * Returns the data source dialect.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    DataSourceDialect getDialect() throws UnifyException;

    /**
     * Returns the number of available connections.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    int getAvailableConnections() throws UnifyException;

    /**
     * Returns a connection object from data source.
     * 
     * @return Object - the connection
     * @throws UnifyException
     *             if there is no available connection. If some other error occurs
     */
    Object getConnection() throws UnifyException;

    /**
     * Restores a connection.
     * 
     * @param connection
     *            the connection object to restore
     * @return true if connection is restored.
     * @throws UnifyException
     *             if supplied connection object did not originate from this data
     *             source. if an error occurs.
     */
    boolean restoreConnection(Object connection) throws UnifyException;
}
