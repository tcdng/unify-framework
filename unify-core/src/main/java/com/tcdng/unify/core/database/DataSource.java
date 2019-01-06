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

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A data source component. Entity types are defined at this level. This is
 * logical and configuration is easier.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface DataSource extends UnifyComponent {

    /**
     * Returns a list of entity types maintained in this datasource. Entity types in
     * list are expected to be ordered based on dependency with parents coming
     * before dependants.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    List<Class<?>> getEntityTypes() throws UnifyException;

    /**
     * Returns the datasource dialect.
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
