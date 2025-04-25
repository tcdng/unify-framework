/*
 * Copyright (c) 2018-2025 The Code Department.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;

/**
 * Database table entity CRUD tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DatabaseTableEntityArrayFieldsCRUDTest extends AbstractUnifyComponentTest {

	private DatabaseTransactionManager tm;

	private Database db;

	@Test
	public void testCreateRecordEmpty() throws Exception {
		tm.beginTransaction();
		try {
			Staff staff = new Staff("Tom Jones", new BigDecimal[] {}, null, new boolean[] {}, null);
			Long id = (Long) db.create(staff);
			assertNotNull(id);
			assertEquals(id, staff.getId());

			Staff createdStaff = db.find(Staff.class, id);
			assertEquals(staff.getName(), createdStaff.getName());
			
			assertNotNull(createdStaff.getSalaries());
			assertEquals(0, createdStaff.getSalaries().length);
			assertNotNull(createdStaff.getCases());
			assertEquals(0, createdStaff.getCases().length);
			assertNull(createdStaff.getKpis());
			assertNull(createdStaff.getEmploymentDates());
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testCreateRecord() throws Exception {
		tm.beginTransaction();
		try {
			final Date date = new Date();
			Staff staff = new Staff("Tom Jones",
					new BigDecimal[] { BigDecimal.valueOf(2.35), BigDecimal.valueOf(0.72) }, new int[] { 14 },
					new boolean[] {true, false, true}, new Date[] {date});
			Long id = (Long) db.create(staff);
			assertNotNull(id);
			assertEquals(id, staff.getId());

			Staff createdStaff = db.find(Staff.class, id);
			assertEquals(staff.getName(), createdStaff.getName());

			assertNotNull(createdStaff.getSalaries());
			assertEquals(2, createdStaff.getSalaries().length);
			assertEquals(BigDecimal.valueOf(2.35), createdStaff.getSalaries()[0]);
			assertEquals(BigDecimal.valueOf(0.72), createdStaff.getSalaries()[1]);
			
			assertNotNull(createdStaff.getKpis());
			assertEquals(1, createdStaff.getKpis().length);
			assertEquals(14, createdStaff.getKpis()[0]);
			
			assertNotNull(createdStaff.getCases());
			assertEquals(3, createdStaff.getCases().length);
			assertTrue(createdStaff.getCases()[0]);
			assertFalse(createdStaff.getCases()[1]);
			assertTrue(createdStaff.getCases()[2]);
			
			assertNotNull(createdStaff.getEmploymentDates());
			assertEquals(1, createdStaff.getEmploymentDates().length);
			assertEquals(date, createdStaff.getEmploymentDates()[0]);
		} catch (Exception e) {
			tm.setRollback();
			throw e;
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testUpdateRecordById() throws Exception {
		tm.beginTransaction();
		try {
			final Date date = new Date();
			Staff staff = new Staff("Tom Jones", new BigDecimal[] {}, null, new boolean[] {}, null);
			Long id = (Long) db.create(staff);
			assertNotNull(id);
			assertEquals(id, staff.getId());

			staff.setSalaries(new BigDecimal[] {BigDecimal.valueOf(2.11), BigDecimal.valueOf(0.45), BigDecimal.valueOf(120.72)});
			staff.setKpis(new int[] { 14, 28 });
			staff.setCases(new boolean[] {true, false, true});
			staff.setEmploymentDates(new Date[] {date});
			
			db.updateByIdVersion(staff);

			Staff updatedStaff = db.find(Staff.class, id);
			assertEquals(staff.getName(), updatedStaff.getName());

			assertNotNull(updatedStaff.getSalaries());
			assertEquals(3, updatedStaff.getSalaries().length);
			assertEquals(BigDecimal.valueOf(2.11), updatedStaff.getSalaries()[0]);
			assertEquals(BigDecimal.valueOf(0.45), updatedStaff.getSalaries()[1]);
			assertEquals(BigDecimal.valueOf(120.72), updatedStaff.getSalaries()[2]);
			
			assertNotNull(updatedStaff.getKpis());
			assertEquals(2, updatedStaff.getKpis().length);
			assertEquals(14, updatedStaff.getKpis()[0]);
			assertEquals(28, updatedStaff.getKpis()[1]);
			
			assertNotNull(updatedStaff.getCases());
			assertEquals(3, updatedStaff.getCases().length);
			assertTrue(updatedStaff.getCases()[0]);
			assertFalse(updatedStaff.getCases()[1]);
			assertTrue(updatedStaff.getCases()[2]);
			
			assertNotNull(updatedStaff.getEmploymentDates());
			assertEquals(1, updatedStaff.getEmploymentDates().length);
			assertEquals(date, updatedStaff.getEmploymentDates()[0]);
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
		tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
		db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void onTearDown() throws Exception {
		deleteAll(Staff.class);
	}
}
