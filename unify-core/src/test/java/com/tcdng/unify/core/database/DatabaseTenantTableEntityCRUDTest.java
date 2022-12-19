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
            assertEquals(Long.valueOf(0L), createdCompanyAccount.getCompanyId());
            assertEquals(createdCompanyAccount, companyAccount);
            assertFalse(createdCompanyAccount.equals(new CompanyAccount("0193884776", "Team Gray", BigDecimal.valueOf(340.62))));
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
            assertEquals(Long.valueOf(0L), companyAccount.getCompanyId());

        	companyAccount = new CompanyAccount("0193884222", "Team Red", BigDecimal.valueOf(340.62));
            db.create(companyAccount);
            assertEquals(Long.valueOf(0L), companyAccount.getCompanyId());
            
        	companyAccount = new CompanyAccount("0193884444", "Team Blue", BigDecimal.valueOf(340.62));
            db.create(companyAccount);
            assertEquals(Long.valueOf(0L), companyAccount.getCompanyId());
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
