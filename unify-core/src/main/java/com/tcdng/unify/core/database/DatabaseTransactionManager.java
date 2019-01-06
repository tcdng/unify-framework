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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.TransactionAttribute;

/**
 * A database transaction manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface DatabaseTransactionManager extends UnifyComponent {
    /**
     * Begins a transaction. The transaction started is of
     * {@link TransactionAttribute#REQUIRED} type.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void beginTransaction() throws UnifyException;

    /**
     * Begins a transaction using supplied transaction attribute.
     * 
     * @param txnAttribute
     *            the transaction attribute
     * @throws UnifyException
     *             if an error occurs
     */
    void beginTransaction(TransactionAttribute txnAttribute) throws UnifyException;

    /**
     * Ends current transaction. Commits current transaction if roll-back has not
     * been set.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void endTransaction() throws UnifyException;

    /**
     * Tests if transaction is open.
     * 
     * @return true is transaction is open
     */
    boolean isTransactionOpen();

    /**
     * Sets save point for transaction session.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void setSavePoint() throws UnifyException;

    /**
     * Clears current save point for transaction.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void clearSavePoint() throws UnifyException;

    /**
     * Roll back transaction to last save point.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void rollbackToSavepoint() throws UnifyException;

    /**
     * Sets current transaction to roll back.
     */
    void setRollback() throws UnifyException;

    /**
     * Commits session transactions and clears all save points.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void commit() throws UnifyException;
}
