/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.business;

import java.util.Date;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.DatabaseTransactionManager;

/**
 * Interface that must be implemented by any class that is to be considered a
 * business service component by the framework. Business service components are
 * treated specially by the framework for transaction and synchronization
 * management.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface BusinessService extends UnifyComponent {

    /**
     * Gets the business service application database transaction manager.
     * 
     * @return DatabaseTransactionManager the transaction manager
     * @throws UnifyException
     *             if an error occurs
     */
    DatabaseTransactionManager tm() throws UnifyException;

    /**
     * Gets a new instance of an entity extension type.
     * 
     * @param entityClass
     *            the extended entity type
     * @return a new instance of extension type if entity is extended otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    Entity getExtendedInstance(Class<? extends Entity> entityClass) throws UnifyException;

    /**
     * Returns the today's date.
     * 
     * @return the midnight date
     * @throws UnifyException
     *             if an error occurs
     */
    Date getToday() throws UnifyException;

    /**
     * Returns the current UTC timestamp based on current session.
     * 
     * @return now
     * @throws UnifyException
     *             if an error occurs
     */
    Date getNow() throws UnifyException;
}
