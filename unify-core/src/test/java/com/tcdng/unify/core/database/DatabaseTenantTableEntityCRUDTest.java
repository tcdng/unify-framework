/*
 * Copyright 2018-2022 The Code Department.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;

/**
 * Database tenant table entity CRUD tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DatabaseTenantTableEntityCRUDTest extends AbstractUnifyComponentTest {

	private DatabaseTransactionManager tm;

	private Database db;

	@Test
	public void testCreateTenantRecord() throws Exception {
		tm.beginTransaction();
		try {
			CompanyAccount companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			Long id = (Long) db.create(companyAccount);
			assertNotNull(id);
			assertEquals(id, companyAccount.getId());

			CompanyAccount createdCompanyAccount = db.find(CompanyAccount.class, id);
			assertEquals(Entity.PRIMARY_TENANT_ID, createdCompanyAccount.getCompanyId());
			assertEquals(createdCompanyAccount, companyAccount);
			assertFalse(createdCompanyAccount
					.equals(new CompanyAccount("0193884776", "Team Gray", BigDecimal.valueOf(340.62))));
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testCreateTenantMultipleDistinctRecord() throws Exception {
		tm.beginTransaction();
		try {
			CompanyAccount companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
			assertEquals(Entity.PRIMARY_TENANT_ID, companyAccount.getCompanyId());

			companyAccount = new CompanyAccount("0193884222", "Team Red", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
			assertEquals(Entity.PRIMARY_TENANT_ID, companyAccount.getCompanyId());

			companyAccount = new CompanyAccount("0193884444", "Team Blue", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
			assertEquals(Entity.PRIMARY_TENANT_ID, companyAccount.getCompanyId());
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test(expected = Exception.class)
	public void testCreateTenantRecordUniqueViolationBlankTenantId() throws Exception {
		tm.beginTransaction();
		try {
			CompanyAccount companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);

			// Test unique constraint by using same account number
			CompanyAccount _companyAccount = new CompanyAccount("0193884777", "Team Blue", BigDecimal.valueOf(530.62));
			db.create(_companyAccount);
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testCreateTenantRecordMultipleTenants() throws Exception {
		tm.beginTransaction();
		try {
			setSessionUserTokenTenantId(2L);
			CompanyAccount companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
			assertEquals(Long.valueOf(2L), companyAccount.getCompanyId());

			setSessionUserTokenTenantId(3L);
			companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
			assertEquals(Long.valueOf(3L), companyAccount.getCompanyId());

			setSessionUserTokenTenantId(4L);
			companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
			assertEquals(Long.valueOf(4L), companyAccount.getCompanyId());
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test(expected = Exception.class)
	public void testCreateTenantRecordUniqueViolationSingleTenant() throws Exception {
		tm.beginTransaction();
		try {
			setSessionUserTokenTenantId(2L);
			CompanyAccount companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
			assertEquals(Long.valueOf(2L), companyAccount.getCompanyId());

			companyAccount = new CompanyAccount("0193884777", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testFindTenantRecordMultipleTenants() throws Exception {
		tm.beginTransaction();
		try {
			setSessionUserTokenTenantId(2L);
			CompanyAccount companyAccount = new CompanyAccount("0193884111", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);

			setSessionUserTokenTenantId(3L);
			companyAccount = new CompanyAccount("0193884111", "Team Blue", BigDecimal.valueOf(350.21));
			db.create(companyAccount);

			setSessionUserTokenTenantId(4L);
			companyAccount = new CompanyAccount("0193884111", "Team Red", BigDecimal.valueOf(400.54));
			db.create(companyAccount);

			setSessionUserTokenTenantId(2L);
			CompanyAccount foundCompanyAccount = db.find(new CompanyAccountQuery().accountNo("0193884111"));
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(2L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Green", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(340.62), foundCompanyAccount.getBalance());

			setSessionUserTokenTenantId(3L);
			foundCompanyAccount = db.find(new CompanyAccountQuery().accountNo("0193884111"));
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(3L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Blue", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(350.21), foundCompanyAccount.getBalance());

			setSessionUserTokenTenantId(4L);
			foundCompanyAccount = db.find(new CompanyAccountQuery().accountNo("0193884111"));
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(4L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Red", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(400.54), foundCompanyAccount.getBalance());
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testFindTenantRecordMultipleTenantsIgnoreTenancy() throws Exception {
		tm.beginTransaction();
		try {
			setSessionUserTokenTenantId(2L);
			CompanyAccount companyAccount = new CompanyAccount("0193884111", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);

			setSessionUserTokenTenantId(3L);
			companyAccount = new CompanyAccount("0193884111", "Team Blue", BigDecimal.valueOf(350.21));
			db.create(companyAccount);

			setSessionUserTokenTenantId(4L);
			companyAccount = new CompanyAccount("0193884111", "Team Red", BigDecimal.valueOf(400.54));
			db.create(companyAccount);

			setSessionUserTokenTenantId(2L);
			List<CompanyAccount> list = db.findAll(
					new CompanyAccountQuery().accountNo("0193884111").ignoreTenancy(true).addOrder("companyId"));
			assertEquals(3, list.size());
			CompanyAccount foundCompanyAccount = list.get(0);
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(2L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Green", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(340.62), foundCompanyAccount.getBalance());

			foundCompanyAccount = list.get(1);
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(3L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Blue", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(350.21), foundCompanyAccount.getBalance());

			foundCompanyAccount = list.get(2);
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(4L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Red", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(400.54), foundCompanyAccount.getBalance());
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testFindAllTenantRecordMultipleTenants() throws Exception {
		tm.beginTransaction();
		try {
			setSessionUserTokenTenantId(2L);
			CompanyAccount companyAccount = new CompanyAccount("0193884111", "Team Green", BigDecimal.valueOf(340.62));
			db.create(companyAccount);

			setSessionUserTokenTenantId(3L);
			companyAccount = new CompanyAccount("0193884111", "Team Blue", BigDecimal.valueOf(350.21));
			db.create(companyAccount);

			setSessionUserTokenTenantId(4L);
			companyAccount = new CompanyAccount("0193884111", "Team Red", BigDecimal.valueOf(400.54));
			db.create(companyAccount);

			setSessionUserTokenTenantId(2L);
			List<CompanyAccount> list = db.findAll(new CompanyAccountQuery().accountNo("0193884111"));
			assertEquals(1, list.size());
			CompanyAccount foundCompanyAccount = list.get(0);
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(2L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Green", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(340.62), foundCompanyAccount.getBalance());

			setSessionUserTokenTenantId(3L);
			list = db.findAll(new CompanyAccountQuery().accountNo("0193884111"));
			assertEquals(1, list.size());
			foundCompanyAccount = list.get(0);
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(3L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Blue", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(350.21), foundCompanyAccount.getBalance());

			setSessionUserTokenTenantId(4L);
			list = db.findAll(new CompanyAccountQuery().accountNo("0193884111"));
			assertEquals(1, list.size());
			foundCompanyAccount = list.get(0);
			assertNotNull(foundCompanyAccount);
			assertEquals(Long.valueOf(4L), foundCompanyAccount.getCompanyId());
			assertEquals("0193884111", foundCompanyAccount.getAccountNo());
			assertEquals("Team Red", foundCompanyAccount.getAccountName());
			assertEquals(BigDecimal.valueOf(400.54), foundCompanyAccount.getBalance());
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {
		addContainerSetting(UnifyCorePropertyConstants.APPLICATION_QUERY_LIMIT, 8);
		addContainerSetting(UnifyCorePropertyConstants.APPLICATION_TENANCY_ENABLED, "true");
	}

	@Override
	protected void onSetup() throws Exception {
		tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
		db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void onTearDown() throws Exception {
		deleteAll(CompanyAccount.class);
	}
}
