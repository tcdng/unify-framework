/*
 * Copyright 2014 The Code Department
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
 * Business module tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BusinessModuleTest extends AbstractUnifyComponentTest {

	public BusinessModuleTest() {
		super(true);
	}

	@Test
	public void testInvokeBusinessModuleMethod() throws Exception {
		MockBusinessModule mockBusinessModule = (MockBusinessModule) getComponent("mock-businessmodule");
		assertEquals(25, mockBusinessModule.add(17, 8));
		assertEquals(45, mockBusinessModule.multiply(3, 15));
		assertEquals("Hello World!", mockBusinessModule.hello());
	}

	@Test
	public void testBusinessModuleCreateRecord() throws Exception {
		MockBusinessModule mockBusinessModule = (MockBusinessModule) getComponent("mock-businessmodule");
		Long accountId = mockBusinessModule.createAccount(new Account("001", "Sim"));
		assertNotNull(accountId);
		Account account = mockBusinessModule.findAccount(accountId);
		assertNotNull(account);
		assertEquals("001", account.getAccountNo());
		assertEquals("Sim", account.getAccountName());
	}

	@Test
	public void testTransactionAcrossBusinessModules() throws Exception {
		MockBusinessModule mockBusinessModule = (MockBusinessModule) getComponent("mock-businessmodule");
		Long loanAccountId = mockBusinessModule.createLoanAccount("501", "William Whipper Snapper", 67000.00);
		assertNotNull(loanAccountId);
		LoanAccount loanAccount = mockBusinessModule.findLoanAccount(loanAccountId);
		assertNotNull(loanAccount);
		assertEquals("501", loanAccount.getAccountNo());
		assertEquals("William Whipper Snapper", loanAccount.getAccountName());
		assertEquals(67000.00, loanAccount.getPrincipalAmount(), 0);
	}

	@Test
	public void testRollbackAcrossBusinessModules() throws Exception {
		MockBusinessModule mockBusinessModule = (MockBusinessModule) getComponent("mock-businessmodule");
		boolean isException = false;
		try {
			mockBusinessModule.createLoanAccount("501", "William Whipper Snapper", null);
		} catch (Exception e) {
			isException = true;
		}
		assertTrue(isException);
		List<Account> accountList = mockBusinessModule
				.find((AccountQuery) new AccountQuery().ignoreEmptyCriteria(true));
		assertTrue(accountList.isEmpty());
	}

	@Test
	public void testInvokeBusinessModuleMethodWithSubClassing() throws Exception {
		SubMockBusinessModule mockBusinessModule = (SubMockBusinessModule) getComponent("submock-businessmodule");
		assertEquals(25, mockBusinessModule.add(17, 8));
		assertEquals(45, mockBusinessModule.multiply(3, 15));
		assertEquals("Hello World!", mockBusinessModule.hello());
	}

	@Test
	public void testBusinessModuleCreateRecordWithSubClassing() throws Exception {
		SubMockBusinessModule mockBusinessModule = (SubMockBusinessModule) getComponent("submock-businessmodule");
		Long accountId = mockBusinessModule.createAccount(new Account("001", "Sim"));
		assertNotNull(accountId);
		Account account = mockBusinessModule.findAccount(accountId);
		assertNotNull(account);
		assertEquals("001", account.getAccountNo());
		assertEquals("Sim", account.getAccountName());
	}

	@Test
	public void testTransactionAcrossBusinessModulesWithSubClassing() throws Exception {
		SubMockBusinessModule mockBusinessModule = (SubMockBusinessModule) getComponent("submock-businessmodule");
		Long loanAccountId = mockBusinessModule.createLoanAccount("501", "William Whipper Snapper", 67000.00);
		assertNotNull(loanAccountId);
		LoanAccount loanAccount = mockBusinessModule.findLoanAccount(loanAccountId);
		assertNotNull(loanAccount);
		assertEquals("501", loanAccount.getAccountNo());
		assertEquals("William Whipper Snapper", loanAccount.getAccountName());
		assertEquals(67000.00, loanAccount.getPrincipalAmount(), 0);
	}

	@Test
	public void testRollbackAcrossBusinessModulesWithSubClassing() throws Exception {
		SubMockBusinessModule mockBusinessModule = (SubMockBusinessModule) getComponent("submock-businessmodule");
		boolean isException = false;
		try {
			mockBusinessModule.createLoanAccount("501", "William Whipper Snapper", null);
		} catch (Exception e) {
			isException = true;
		}
		assertTrue(isException);
		List<Account> accountList = mockBusinessModule
				.find((AccountQuery) new AccountQuery().ignoreEmptyCriteria(true));
		assertTrue(accountList.isEmpty());
	}

	@Test
	public void testBusinessModulesPluginExtension() throws Exception {
		MockBusinessModule mockBusinessModule = (MockBusinessModule) getComponent("mock-businessmodule");
		Long loanAccountId = mockBusinessModule.createLoanAccount("501", "William Whipper Snapper", 200000.00);

		LoanDisbursementBusinessModule loanDisbursementModule = (LoanDisbursementBusinessModule) getComponent(
				"loandisbursement-businessmodule");
		List<LoanDisbursement> disbursementList = loanDisbursementModule
				.find(new LoanDisbursementQuery().loanAccountId(loanAccountId));
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
