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

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.constant.BooleanType;

/**
 * Database table extension entity CRUD tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DatabaseTableExtensionEntityCRUDTest extends AbstractUnifyComponentTest {

    private Office parklaneOffice = new Office("24, Parklane Apapa", "+2348888888", 20);

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testCreateRecordWithNullExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long id = (Long) db.create(branch);
            assertNotNull(id);
            assertEquals(id, branch.getId());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordWithNullExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long id = (Long) db.create(branch);
            assertNotNull(id);
            assertEquals(id, branch.getId());

            Branch foundBranch = db.find(Branch.class, id);
            assertNotNull(foundBranch);
            assertNull(foundBranch.getExt());
            assertEquals(branch, foundBranch);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            branch.setExt(new BranchExt(parklaneOfficeId, "Lagos", "Nigeria", BooleanType.FALSE));
            Long id = (Long) db.create(branch);
            assertNotNull(id);
            assertEquals(id, branch.getId());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordWithExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            branch.setExt(new BranchExt(parklaneOfficeId, "Lagos", "Nigeria", BooleanType.FALSE));
            Long id = (Long) db.create(branch);
            assertNotNull(id);
            assertEquals(id, branch.getId());

            Branch foundBranch = db.find(Branch.class, id);
            assertNotNull(foundBranch);            
            assertEquals("100", foundBranch.getCode());
            assertEquals("Head Office", foundBranch.getDescription());
            assertEquals("221015100", foundBranch.getSortCode());

            assertNotNull(foundBranch.getExt());
            BranchExt foundBranchExt = (BranchExt) foundBranch.getExt();
            assertEquals(parklaneOfficeId, foundBranchExt.getOfficeId());
            assertEquals("Lagos", foundBranchExt.getState());
            assertEquals("Nigeria", foundBranchExt.getCountry());
            assertEquals(BooleanType.FALSE, foundBranchExt.getClosed());
            assertNull(foundBranchExt.getOfficeAddress());
            assertNull(foundBranchExt.getOfficeTelephone());
            assertNull(foundBranchExt.getClosedDesc());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void onSetup() throws Exception {
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(Branch.class, Office.class);
    }

}
