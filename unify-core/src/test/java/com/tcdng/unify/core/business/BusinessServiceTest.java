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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.Setting;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceConfig;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceManager;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDatabase;
import com.tcdng.unify.core.database.sql.NameSqlDataSourceSchemaImpl;
import com.tcdng.unify.core.database.sql.SqlDatabase;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Business service tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class BusinessServiceTest extends AbstractUnifyComponentTest {

    public BusinessServiceTest() {
        super(true);
    }

    @Test
    public void testInvokeBusinessServiceMethod() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        assertEquals(25, mockService.add(17, 8));
        assertEquals(45, mockService.multiply(3, 15));
        assertEquals("Hello World!", mockService.hello());
    }

    @Test
    public void testBusinessServiceCreateRecord() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        Long accountId = mockService.createAccount(new Account("001", "Sim"));
        assertNotNull(accountId);
        Account account = mockService.findAccount(accountId);
        assertNotNull(account);
        assertEquals("001", account.getAccountNo());
        assertEquals("Sim", account.getAccountName());
    }

    @Test
    public void testTransactionAcrossBusinessServices() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        Long loanAccountId = mockService.createLoanAccount("501", "William Whipper Snapper", 67000.00);
        assertNotNull(loanAccountId);
        LoanAccount loanAccount = mockService.findLoanAccount(loanAccountId);
        assertNotNull(loanAccount);
        assertEquals("501", loanAccount.getAccountNo());
        assertEquals("William Whipper Snapper", loanAccount.getAccountName());
        assertEquals(67000.00, loanAccount.getPrincipalAmount(), 0);
    }

    @Test
    public void testRollbackWithinMethod() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        boolean isException = false;
        try {
            mockService.createLoanAccountWithError("501", "William Whipper Snapper", 20.00);
        } catch (Exception e) {
            isException = true;
        }
        assertTrue(isException);
        List<Account> accountList = mockService.find((AccountQuery) new AccountQuery().ignoreEmptyCriteria(true));
        assertTrue(accountList.isEmpty());
    }

    @Test
    public void testRollbackAcrossBusinessServices() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        boolean isException = false;
        try {
            mockService.createLoanAccount("501", "William Whipper Snapper", null);
        } catch (Exception e) {
            isException = true;
        }
        assertTrue(isException);
        List<Account> accountList = mockService.find((AccountQuery) new AccountQuery().ignoreEmptyCriteria(true));
        assertTrue(accountList.isEmpty());
    }

    @Test
    public void testInvokeBusinessServiceMethodWithSubClassing() throws Exception {
        SubMockService mockService = (SubMockService) getComponent("submockservice");
        assertEquals(25, mockService.add(17, 8));
        assertEquals(45, mockService.multiply(3, 15));
        assertEquals("Hello World!", mockService.hello());
    }

    @Test
    public void testBusinessServiceCreateRecordWithSubClassing() throws Exception {
        SubMockService mockService = (SubMockService) getComponent("submockservice");
        Long accountId = mockService.createAccount(new Account("001", "Sim"));
        assertNotNull(accountId);
        Account account = mockService.findAccount(accountId);
        assertNotNull(account);
        assertEquals("001", account.getAccountNo());
        assertEquals("Sim", account.getAccountName());
    }

    @Test
    public void testTransactionAcrossBusinessServicesWithSubClassing() throws Exception {
        SubMockService mockService = (SubMockService) getComponent("submockservice");
        Long loanAccountId = mockService.createLoanAccount("501", "William Whipper Snapper", 67000.00);
        assertNotNull(loanAccountId);
        LoanAccount loanAccount = mockService.findLoanAccount(loanAccountId);
        assertNotNull(loanAccount);
        assertEquals("501", loanAccount.getAccountNo());
        assertEquals("William Whipper Snapper", loanAccount.getAccountName());
        assertEquals(67000.00, loanAccount.getPrincipalAmount(), 0);
    }

    @Test
    public void testRollbackAcrossBusinessServicesWithSubClassing() throws Exception {
        SubMockService mockService = (SubMockService) getComponent("submockservice");
        boolean isException = false;
        try {
            mockService.createLoanAccount("501", "William Whipper Snapper", null);
        } catch (Exception e) {
            isException = true;
        }
        assertTrue(isException);
        List<Account> accountList = mockService.find((AccountQuery) new AccountQuery().ignoreEmptyCriteria(true));
        assertTrue(accountList.isEmpty());
    }

    @Test
    public void testBusinessServicesPluginExtension() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        Long loanAccountId = mockService.createLoanAccount("501", "William Whipper Snapper", 200000.00);

        LoanDisbursementService loanDisbursementService =
                (LoanDisbursementService) getComponent("loandisbursementservice");
        List<LoanDisbursement> disbursementList =
                loanDisbursementService.find(new LoanDisbursementQuery().loanAccountId(loanAccountId));
        assertNotNull(disbursementList);
        assertFalse(disbursementList.isEmpty());
        LoanDisbursement loanDisbursement = disbursementList.get(0);
        assertEquals(20, loanDisbursement.getDisbursementCount());
        assertEquals(Double.valueOf(200000.00), loanDisbursement.getDisbursementAmount());
    }

    @Test
    public void testTransactionAcrossDynamicDatabase() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        Long accountId =
                mockService.createAccountWithCreditCheck(new Account("001", "Sim"), BigDecimal.valueOf(102.63));
        assertNotNull(accountId);

        // Validate account record in application data source
        Account account = mockService.findAccount(accountId);
        assertNotNull(account);
        assertEquals("001", account.getAccountNo());
        assertEquals("Sim", account.getAccountName());

        // Validate credit check record in third-party dynamic data source
        DatabaseTransactionManager dbTransactionManager =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", MockService.CREDITCHECK_DATASOURCECONFIG));
        assertNotNull(db);
        dbTransactionManager.beginTransaction();
        try {
            CreditCheck creditCheck = db.find(CreditCheck.class, "Sim");
            assertNotNull(creditCheck);
            assertEquals("Sim", creditCheck.getAccountName());
            assertEquals("001", creditCheck.getAccountNo());
            assertEquals(BigDecimal.valueOf(102.63), creditCheck.getLoanAmount());
        } finally {
            dbTransactionManager.endTransaction();
        }
    }

    @Test
    public void testTransactionRollbackAcrossDynamicDatabase() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        mockService.createAccountWithCreditCheckRollbackAfter(new Account("001", "Sim"), BigDecimal.valueOf(102.63));

        // Validate rollback
        DatabaseTransactionManager dbTransactionManager =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        SqlDatabase appDb = (SqlDatabase) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        DynamicSqlDatabase dynDb =
                (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                        new Setting("dataSourceConfigName", MockService.CREDITCHECK_DATASOURCECONFIG));
        dbTransactionManager.beginTransaction();
        try {
            assertEquals(0, appDb.countAll(new AccountQuery().ignoreEmptyCriteria(true)));
            assertEquals(0, dynDb.countAll(new CreditCheckQuery().ignoreEmptyCriteria(true)));
        } finally {
            dbTransactionManager.endTransaction();
        }
    }

    @Test
    public void testTransactionExceptionRollbackDuringAcrossDynamicDatabase() throws Exception {
        try {
            MockService mockService = (MockService) getComponent("mockservice");
            mockService.createAccountWithCreditCheck(new Account("001", "Sim"), null);
        } catch (Exception e) {
        }

        // Validate rollback
        DatabaseTransactionManager dbTransactionManager =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        SqlDatabase appDb = (SqlDatabase) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        DynamicSqlDatabase dynDb =
                (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                        new Setting("dataSourceConfigName", MockService.CREDITCHECK_DATASOURCECONFIG));
        dbTransactionManager.beginTransaction();
        try {
            assertEquals(0, appDb.countAll(new AccountQuery().ignoreEmptyCriteria(true)));
            assertEquals(0, dynDb.countAll(new CreditCheckQuery().ignoreEmptyCriteria(true)));
        } finally {
            dbTransactionManager.endTransaction();
        }
    }

    @Test
    public void testTransactionExceptionRollbackAfterAcrossDynamicDatabase() throws Exception {
        try {
            MockService mockService = (MockService) getComponent("mockservice");
            mockService.createAccountWithCreditCheckExceptionAfter(new Account("001", "Sim"),
                    BigDecimal.valueOf(102.63));
        } catch (Exception e) {
        }

        // Validate rollback
        DatabaseTransactionManager dbTransactionManager =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        SqlDatabase appDb = (SqlDatabase) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        DynamicSqlDatabase dynDb =
                (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                        new Setting("dataSourceConfigName", MockService.CREDITCHECK_DATASOURCECONFIG));
        dbTransactionManager.beginTransaction();
        try {
            assertEquals(0, appDb.countAll(new AccountQuery().ignoreEmptyCriteria(true)));
            assertEquals(0, dynDb.countAll(new CreditCheckQuery().ignoreEmptyCriteria(true)));
        } finally {
            dbTransactionManager.endTransaction();
        }
    }

    @Test
    public void testCrossTransactionBoundarySessionManagement() throws Exception {
        MockService mockService = (MockService) getComponent("mockservice");
        String id = mockService.createBooking(new Booking("1002252443", BigDecimal.valueOf(10.52)));
        assertEquals("1002252443", id);
    }

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        super.doAddSettingsAndDependencies();
        addDependency("thirdparty-datasource", NameSqlDataSourceSchemaImpl.class, new Setting("appSchema", "PUBLIC"));
    }

    @Override
    protected void onSetup() throws Exception {
        // Configure and create dynamic data source
        DynamicSqlDataSourceManager dynamicSqlDataSourceManager = (DynamicSqlDataSourceManager) getComponent(
                ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
        dynamicSqlDataSourceManager.configure(new DynamicSqlDataSourceConfig(MockService.CREDITCHECK_DATASOURCECONFIG,
                "hsqldb-dialect", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:dyntest", null, null, null, 2, true));
        Connection connection = dynamicSqlDataSourceManager.getConnection(MockService.CREDITCHECK_DATASOURCECONFIG);
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE PUBLIC.CREDIT_CHECK (" + "ACCOUNT_NM VARCHAR(48) NOT NULL PRIMARY KEY,"
                    + "ACCOUNT_NO VARCHAR(16) NOT NULL," + "LOAN_AMOUNT DECIMAL(14,2) NOT NULL);");
            connection.commit();
        } finally {
            SqlUtils.close(stmt);
            dynamicSqlDataSourceManager.restoreConnection(MockService.CREDITCHECK_DATASOURCECONFIG, connection);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        // Clear application data soure records
        deleteAll(LoanDisbursement.class, LoanAccount.class, Account.class, Booking.class);

        // Unconfigure and dynamic data source
        DynamicSqlDataSourceManager dynamicSqlDataSourceManager = (DynamicSqlDataSourceManager) getComponent(
                ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
        if (dynamicSqlDataSourceManager.isConfigured(MockService.CREDITCHECK_DATASOURCECONFIG)) {
            dynamicSqlDataSourceManager.terminateAll();
        }

    }
}
