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
package com.tcdng.unify.core.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.TransactionAttribute;

/**
 * Database transaction manager tests for default implementation of persistence
 * manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DatabaseTransactionManagerTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    @Test
    public void testBeginTransaction() throws Exception {
        tm.beginTransaction();
        try {
            assertTrue(tm.isTransactionOpen());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testBeginTransactionWithRequiresNew() throws Exception {
        tm.beginTransaction(TransactionAttribute.REQUIRES_NEW);
        try {
            assertTrue(tm.isTransactionOpen());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testBeginTransactionWithRequired() throws Exception {
        tm.beginTransaction(TransactionAttribute.REQUIRED);
        try {
            assertTrue(tm.isTransactionOpen());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testBeginTransactionWithSupports() throws Exception {
        tm.beginTransaction(TransactionAttribute.SUPPORTS);
        try {
            assertFalse(tm.isTransactionOpen());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testBeginTransactionWithSupportsAndOpenTransaction() throws Exception {
        tm.beginTransaction();
        try {
            assertTrue(tm.isTransactionOpen());
            try {
                tm.beginTransaction(TransactionAttribute.SUPPORTS);
                assertTrue(tm.isTransactionOpen());
            } finally {
                tm.endTransaction();
            }
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testBeginTransactionWithMandatory() throws Exception {
        tm.beginTransaction(TransactionAttribute.MANDATORY);
    }

    @Test
    public void testBeginTransactionWithMandatoryAndOpenTransaction() throws Exception {
        tm.beginTransaction();
        try {
            assertTrue(tm.isTransactionOpen());
            try {
                tm.beginTransaction(TransactionAttribute.MANDATORY);
                assertTrue(tm.isTransactionOpen());
            } finally {
                tm.endTransaction();
            }
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testBeginTransactionWithNever() throws Exception {
        tm.beginTransaction(TransactionAttribute.NEVER);
        try {
            assertFalse(tm.isTransactionOpen());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testBeginTransactionWithNeverAndOpenTransaction() throws Exception {
        tm.beginTransaction();
        try {
            assertTrue(tm.isTransactionOpen());
            tm.beginTransaction(TransactionAttribute.NEVER);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testBeginTransactionWithNotSupported() throws Exception {
        tm.beginTransaction(TransactionAttribute.NOT_SUPPORTED);
        try {
            assertFalse(tm.isTransactionOpen());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testBeginTransactionWithNotSupportedAndOpenTransaction() throws Exception {
        tm.beginTransaction();
        try {
            assertTrue(tm.isTransactionOpen());
            try {
                tm.beginTransaction(TransactionAttribute.NOT_SUPPORTED);
                assertFalse(tm.isTransactionOpen());
            } finally {
                tm.endTransaction();
            }
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testEndTransaction() throws Exception {
        tm.beginTransaction();
        try {
            assertTrue(tm.isTransactionOpen());
        } finally {
            tm.endTransaction();
        }
        assertFalse(tm.isTransactionOpen());
    }

    @Test
    public void testSetRollBack() throws Exception {
        tm.beginTransaction();
        try {
            tm.setRollback();
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void onSetup() throws Exception {
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);;
    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
