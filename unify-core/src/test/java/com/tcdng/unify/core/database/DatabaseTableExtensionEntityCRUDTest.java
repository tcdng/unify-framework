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
import static org.junit.Assert.assertTrue;

import java.util.List;

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

    private Office warehouseOffice = new Office("38, Warehouse Road Apapa", "+2345555555", 35);

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testGetNewExtensionInstanceBlank() throws Exception {
        tm.beginTransaction();
        try {
            Entity extensionInst = db.getNewExtensionInstance(Author.class);
            assertNull(extensionInst);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testGetNewExtensionInstance() throws Exception {
        tm.beginTransaction();
        try {
            Entity extensionInst = db.getNewExtensionInstance(Branch.class);
            assertNotNull(extensionInst);
            assertTrue(BranchExt.class.equals(extensionInst.getClass()));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }
    
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
    public void testFindAllWithNullExtension() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Branch("100", "Head Office", "221015100"));
            db.create(new Branch("101", "Palm Groove Branch", "221015101"));
            db.create(new Branch("102", "Lekki Phase I", "221015102"));

            List<Branch> branchList = db.findAll(new BranchQuery().addOrder("code").ignoreEmptyCriteria(true));
            assertNotNull(branchList);
            assertEquals(3, branchList.size());
            
            Branch branch = branchList.get(0);
            assertEquals("100", branch.getCode());
            assertEquals("Head Office", branch.getDescription());
            assertEquals("221015100", branch.getSortCode());
            assertNull(branch.getExt());

            branch = branchList.get(1);
            assertEquals("101", branch.getCode());
            assertEquals("Palm Groove Branch", branch.getDescription());
            assertEquals("221015101", branch.getSortCode());
            assertNull(branch.getExt());

            branch = branchList.get(2);
            assertEquals("102", branch.getCode());
            assertEquals("Lekki Phase I", branch.getDescription());
            assertEquals("221015102", branch.getSortCode());
            assertNull(branch.getExt());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordWithNullExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long id = (Long) db.create(branch);
            assertNotNull(id);
            assertEquals(id, branch.getId());

            Branch foundBranch = db.list(Branch.class, id);
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
    public void testListAllWithNullExtension() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Branch("100", "Head Office", "221015100"));
            db.create(new Branch("101", "Palm Groove Branch", "221015101"));
            db.create(new Branch("102", "Lekki Phase I", "221015102"));

            List<Branch> branchList = db.listAll(new BranchQuery().addOrder("code").ignoreEmptyCriteria(true));
            assertNotNull(branchList);
            assertEquals(3, branchList.size());
            
            Branch branch = branchList.get(0);
            assertEquals("100", branch.getCode());
            assertEquals("Head Office", branch.getDescription());
            assertEquals("221015100", branch.getSortCode());
            assertNull(branch.getExt());

            branch = branchList.get(1);
            assertEquals("101", branch.getCode());
            assertEquals("Palm Groove Branch", branch.getDescription());
            assertEquals("221015101", branch.getSortCode());
            assertNull(branch.getExt());

            branch = branchList.get(2);
            assertEquals("102", branch.getCode());
            assertEquals("Lekki Phase I", branch.getDescription());
            assertEquals("221015102", branch.getSortCode());
            assertNull(branch.getExt());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdWithNullExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            db.create(branch);
            branch.setDescription("Head Office Main");
            branch.setSortCode("221015102");
            db.updateById(branch);

            Branch foundBranch = db.find(Branch.class, branch.getId());
            assertNotNull(foundBranch);
            assertNull(foundBranch.getExt());
            assertEquals("100", foundBranch.getCode());
            assertEquals("Head Office Main", foundBranch.getDescription());
            assertEquals("221015102", foundBranch.getSortCode());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithNullExtension() throws Exception {
        tm.beginTransaction();
        try {
            Long id = (Long) db.create(new Branch("100", "Head Office", "221015100"));
            assertEquals(1, db.countAll(new BranchQuery().addEquals("id", id)));
            db.delete(Branch.class, id);
            assertEquals(0, db.countAll(new BranchQuery().addEquals("id", id)));
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

    @Test
    public void testFindAllWithExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            branch.setExt(new BranchExt(parklaneOfficeId, "Lagos", "Nigeria", BooleanType.FALSE));
            db.create(branch);
            db.create(new Branch("101", "Palm Groove Branch", "221015101"));

            branch = new Branch("102", "Lekki Phase I", "221015102");
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            branch.setExt(new BranchExt(warehouseOfficeId, "Abuja", "Nigeria", BooleanType.TRUE));
            db.create(branch);

            List<Branch> branchList = db.findAll(new BranchQuery().addOrder("code").ignoreEmptyCriteria(true));
            assertNotNull(branchList);
            assertEquals(3, branchList.size());
            
            branch = branchList.get(0);
            assertEquals("100", branch.getCode());
            assertEquals("Head Office", branch.getDescription());
            assertEquals("221015100", branch.getSortCode());
            assertNotNull(branch.getExt());
            BranchExt foundBranchExt = (BranchExt) branch.getExt();
            assertEquals(parklaneOfficeId, foundBranchExt.getOfficeId());
            assertEquals("Lagos", foundBranchExt.getState());
            assertEquals("Nigeria", foundBranchExt.getCountry());
            assertEquals(BooleanType.FALSE, foundBranchExt.getClosed());
            assertNull(foundBranchExt.getOfficeAddress());
            assertNull(foundBranchExt.getOfficeTelephone());
            assertNull(foundBranchExt.getClosedDesc());

            branch = branchList.get(1);
            assertEquals("101", branch.getCode());
            assertEquals("Palm Groove Branch", branch.getDescription());
            assertEquals("221015101", branch.getSortCode());
            assertNull(branch.getExt());

            branch = branchList.get(2);
            assertEquals("102", branch.getCode());
            assertEquals("Lekki Phase I", branch.getDescription());
            assertEquals("221015102", branch.getSortCode());
            assertNotNull(branch.getExt());
            foundBranchExt = (BranchExt) branch.getExt();
            assertEquals(warehouseOfficeId, foundBranchExt.getOfficeId());
            assertEquals("Abuja", foundBranchExt.getState());
            assertEquals("Nigeria", foundBranchExt.getCountry());
            assertEquals(BooleanType.TRUE, foundBranchExt.getClosed());
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

    @Test
    public void testListRecordWithExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            branch.setExt(new BranchExt(parklaneOfficeId, "Lagos", "Nigeria", BooleanType.FALSE));
            Long id = (Long) db.create(branch);
            assertNotNull(id);
            assertEquals(id, branch.getId());

            Branch foundBranch = db.list(Branch.class, id);
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
            assertEquals("24, Parklane Apapa", foundBranchExt.getOfficeAddress());
            assertEquals("+2348888888", foundBranchExt.getOfficeTelephone());
            assertEquals("False", foundBranchExt.getClosedDesc());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllWithExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            branch.setExt(new BranchExt(parklaneOfficeId, "Lagos", "Nigeria", BooleanType.FALSE));
            db.create(branch);
            db.create(new Branch("101", "Palm Groove Branch", "221015101"));

            branch = new Branch("102", "Lekki Phase I", "221015102");
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            branch.setExt(new BranchExt(warehouseOfficeId, "Abuja", "Nigeria", BooleanType.TRUE));
            db.create(branch);

            List<Branch> branchList = db.listAll(new BranchQuery().addOrder("code").ignoreEmptyCriteria(true));
            assertNotNull(branchList);
            assertEquals(3, branchList.size());
            
            branch = branchList.get(0);
            assertEquals("100", branch.getCode());
            assertEquals("Head Office", branch.getDescription());
            assertEquals("221015100", branch.getSortCode());
            assertNotNull(branch.getExt());
            BranchExt foundBranchExt = (BranchExt) branch.getExt();
            assertEquals(parklaneOfficeId, foundBranchExt.getOfficeId());
            assertEquals("Lagos", foundBranchExt.getState());
            assertEquals("Nigeria", foundBranchExt.getCountry());
            assertEquals(BooleanType.FALSE, foundBranchExt.getClosed());
            assertEquals("24, Parklane Apapa", foundBranchExt.getOfficeAddress());
            assertEquals("+2348888888", foundBranchExt.getOfficeTelephone());
            assertEquals("False", foundBranchExt.getClosedDesc());

            branch = branchList.get(1);
            assertEquals("101", branch.getCode());
            assertEquals("Palm Groove Branch", branch.getDescription());
            assertEquals("221015101", branch.getSortCode());
            assertNull(branch.getExt());

            branch = branchList.get(2);
            assertEquals("102", branch.getCode());
            assertEquals("Lekki Phase I", branch.getDescription());
            assertEquals("221015102", branch.getSortCode());
            assertNotNull(branch.getExt());
            foundBranchExt = (BranchExt) branch.getExt();
            assertEquals(warehouseOfficeId, foundBranchExt.getOfficeId());
            assertEquals("Abuja", foundBranchExt.getState());
            assertEquals("Nigeria", foundBranchExt.getCountry());
            assertEquals(BooleanType.TRUE, foundBranchExt.getClosed());
            assertEquals("38, Warehouse Road Apapa", foundBranchExt.getOfficeAddress());
            assertEquals("+2345555555", foundBranchExt.getOfficeTelephone());
            assertEquals("True", foundBranchExt.getClosedDesc());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdWithExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            BranchExt branchExt = new BranchExt(parklaneOfficeId, "Lagos", "Nigeria", BooleanType.FALSE);
            branch.setExt(branchExt);
            db.create(branch);
            branch.setDescription("Head Office Main");
            branch.setSortCode("221015102");
            branchExt.setState("Abuja");
            branchExt.setClosed(BooleanType.TRUE);
            db.updateById(branch);

            Branch foundBranch = db.find(Branch.class, branch.getId());
            assertNotNull(foundBranch);
            assertEquals("100", foundBranch.getCode());
            assertEquals("Head Office Main", foundBranch.getDescription());
            assertEquals("221015102", foundBranch.getSortCode());

            assertNotNull(foundBranch.getExt());
            BranchExt foundBranchExt = (BranchExt) foundBranch.getExt();
            assertEquals(parklaneOfficeId, foundBranchExt.getOfficeId());
            assertEquals("Abuja", foundBranchExt.getState());
            assertEquals("Nigeria", foundBranchExt.getCountry());
            assertEquals(BooleanType.TRUE, foundBranchExt.getClosed());
            assertNull(foundBranchExt.getOfficeAddress());
            assertNull(foundBranchExt.getOfficeTelephone());
            assertNull(foundBranchExt.getClosedDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithExtension() throws Exception {
        tm.beginTransaction();
        try {
            Branch branch = new Branch("100", "Head Office", "221015100");
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            BranchExt branchExt = new BranchExt(parklaneOfficeId, "Lagos", "Nigeria", BooleanType.FALSE);
            branch.setExt(branchExt);
            Long id = (Long) db.create(branch);
            assertEquals(1, db.countAll(new BranchQuery().addEquals("id", id)));
            db.delete(Branch.class, id);
            assertEquals(0, db.countAll(new BranchQuery().addEquals("id", id)));
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
