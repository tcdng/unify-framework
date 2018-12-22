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
package com.tcdng.unify.core.business;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.DatabaseTransactionManager;

/**
 * Interface that must be implemented by any class that is to be considered a
 * business service component by the framework. Business service components are
 * treated specially by the framework for transaction and synchronization
 * management.
 * 
 * @author Lateef Ojulari
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
}
