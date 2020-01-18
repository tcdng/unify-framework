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
package com.tcdng.unify.core.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Database callable tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DatabaseCallableTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private Database db;

    private static boolean isProcs;
    
    @Test
    public void testExecuteCallableNoParamsNoResultNoReturn() throws Exception {
        tm.beginTransaction();
        try {
            CallableProcA callableProcA = new CallableProcA();
            db.executeCallable(callableProcA);
            assertNull(callableProcA.getReturnValue());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testExecuteCallableWithParamsNoResultNoReturn() throws Exception {
        tm.beginTransaction();
        try {
            CallableProcB callableProcB = new CallableProcB(251, new Date(), new Date());
            db.executeCallable(callableProcB);
            assertEquals("myAccount_251", callableProcB.getAccountName());
            assertEquals(BigDecimal.valueOf(254.65), callableProcB.getBalance());
            assertNull(callableProcB.getReturnValue());
        } finally {
            tm.endTransaction();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteCallableNoParamsWithResultNoReturn() throws Exception {
        tm.beginTransaction();
        try {
            Date now = CalendarUtils.getMidnightDate(new Date());
            db.create(new CallableResultA("ABC0123456", now));
            tm.commit();
            
            CallableProcC callableProcC = new CallableProcC();
            Map<Class<?>, List<?>> resultMap = db.executeCallableWithResults(callableProcC);
            assertNotNull(resultMap);
            assertEquals(1, resultMap.size());
            
            List<CallableResultA> itemList = (List<CallableResultA>) resultMap.get(CallableResultA.class);
            assertNotNull(itemList);
            assertEquals(1, itemList.size());
            CallableResultA callableResultA = itemList.get(0);
            assertNotNull(callableResultA);
            assertEquals("ABC0123456", callableResultA.getLicenseNo());
            assertEquals(now, callableResultA.getExpiryDt());
            
            assertNull(callableProcC.getReturnValue());
        } finally {
            tm.endTransaction();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteCallableWithParamsWithResultNoReturn() throws Exception {
        tm.beginTransaction();
        try {
            CallableProcD callableProcD = new CallableProcD(313, new Date(), new Date(), "Viking_");
            Map<Class<?>, List<?>> resultMap = db.executeCallableWithResults(callableProcD);
            assertNotNull(resultMap);
            assertEquals(1, resultMap.size());
            
            List<CallableResultA> itemList = (List<CallableResultA>) resultMap.get(CallableResultA.class);
            assertNotNull(itemList);
            assertEquals(2, itemList.size());
            CallableResultA callableResultA = itemList.get(0);
            assertNotNull(callableResultA);
            assertEquals("ABC0123456", callableResultA.getLicenseNo());
            assertNotNull(callableResultA.getExpiryDt());
            
            callableResultA = itemList.get(1);
            assertNotNull(callableResultA);
            assertEquals("XYZ9876543", callableResultA.getLicenseNo());
            assertNotNull(callableResultA.getExpiryDt());

            assertEquals("Viking_313", callableProcD.getAccountName());
            assertEquals(BigDecimal.valueOf(436.92), callableProcD.getBalance());
            assertNull(callableProcD.getReturnValue());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testExecuteCallableWithParamsNoResultWithReturn() throws Exception {
        tm.beginTransaction();
        try {
            CallableProcE callableProcE = new CallableProcE(313, new Date(), new Date(), "Trip_");
            db.executeCallable(callableProcE);

            assertEquals("Trip_313", callableProcE.getReturnValue());
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        addContainerSetting(UnifyCorePropertyConstants.APPLICATION_QUERY_LIMIT, 8);
    }

    @Override
    protected void onSetup() throws Exception {
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);;
        db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        
        if (!isProcs) {
            // Create stored procedures
            DataSource dataSource = db.getDataSource();
            Connection connection = (Connection) dataSource.getConnection();
            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                // procedure_a
                stmt.executeUpdate("CREATE PROCEDURE procedure_a() "
                        + "BEGIN ATOMIC "
                        + "DECLARE blank_id INTEGER; "
                        + "SET blank_id = 25; "
                        + "END ");

                // procedure_b
                stmt.executeUpdate("CREATE PROCEDURE procedure_b(IN itemId INTEGER, IN startDt TIMESTAMP, IN endDt DATE, OUT accountName VARCHAR(20), OUT balance DECIMAL(14,2)) "
                        + "BEGIN ATOMIC "
                        + "DECLARE itemId_Str VARCHAR(20); "
                        + "SET itemId_Str = CAST(itemId AS VARCHAR(20)); "
                        + "SET accountName = CONCAT('myAccount_', itemId_Str); "
                        + "SET balance = 254.65; "
                        + "END ");

                // procedure_c
                stmt.executeUpdate("CREATE PROCEDURE procedure_c() "
                        + "READS SQL DATA DYNAMIC RESULT SETS 1 "
                        + "BEGIN ATOMIC "
                        + "DECLARE res CURSOR WITH RETURN FOR SELECT LICENSE_NO, EXPIRY_DT FROM LICENSE; "
                        + "OPEN res; "
                        + "END ");

                // procedure_d
                stmt.executeUpdate("CREATE PROCEDURE procedure_d(IN itemId INTEGER, IN startDt TIMESTAMP, IN endDt DATE, INOUT accountName VARCHAR(20), OUT balance DECIMAL(14,2)) "
                        + "MODIFIES SQL DATA DYNAMIC RESULT SETS 1 "
                        + "BEGIN ATOMIC "
                        + "DECLARE itemId_Str VARCHAR(20); "
                        + "DECLARE res CURSOR WITH RETURN FOR SELECT LICENSE_NO, EXPIRY_DT FROM LICENSE ORDER BY LICENSE_NO; "
                        + "INSERT INTO LICENSE(LICENSE_ID,LICENSE_NO, EXPIRY_DT, VERSION_NO) VALUES(10, 'ABC0123456', NOW(), 1); "
                        + "INSERT INTO LICENSE(LICENSE_ID,LICENSE_NO, EXPIRY_DT, VERSION_NO) VALUES(11, 'XYZ9876543', NOW(), 1); "
                        + "SET itemId_Str = CAST(itemId AS VARCHAR(20)); "
                        + "SET accountName = CONCAT(accountName, itemId_Str); "
                        + "SET balance = 436.92; "
                        + "OPEN res; "
                        + "END ");

                // procedure_e
                stmt.executeUpdate("CREATE FUNCTION procedure_e(IN itemId INTEGER, IN startDt TIMESTAMP, IN endDt DATE, IN accountName VARCHAR(20)) "
                        + "RETURNS VARCHAR(20) "
                        + "BEGIN ATOMIC "
                        + "DECLARE itemId_Str VARCHAR(20); "
                        + "SET itemId_Str = CAST(itemId AS VARCHAR(20)); "
                        + "RETURN CONCAT(accountName, itemId_Str); "
                        + "END ");

                connection.commit();
            } finally {
                isProcs = true;
                SqlUtils.close(stmt);
                dataSource.restoreConnection(connection);
            }            
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(CallableResultA.class);
    }
}
