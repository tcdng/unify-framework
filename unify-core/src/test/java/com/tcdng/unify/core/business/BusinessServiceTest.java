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
package com.tcdng.unify.core.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;

/**
 * Business service tests.
 * 
 * @author Lateef Ojulari
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

    @Override
    protected void onSetup() throws Exception {

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(LoanDisbursement.class, LoanAccount.class, Account.class);
    }
}
