/*
 * Copyright 2018-2023 The Code Department.
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
import com.tcdng.unify.core.UnifyCorePropertyConstants;

/**
 * Database recursive table entity tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DatabaseRecursiveTableEntityTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testCountRecord() throws Exception {
        tm.beginTransaction();
        try {
            Long managerId = (Long) db.create(new Employee("Tom Jones"));
            Long employeeId = (Long) db.create(new Employee(managerId, "Samuel Kim"));
            db.create(new Employee("Hallow Sean"));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(3, db.countAll(new EmployeeQuery().ignoreEmptyCriteria(true)));
            assertEquals(1, db.countAll(new EmployeeQuery().id(employeeId)));
            assertEquals(1, db.countAll(new EmployeeQuery().managerId(managerId)));
            assertEquals(2, db.countAll(new EmployeeQuery().managerIdIsNull()));
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
            Long managerId = (Long) db.create(new Employee("Tom Jones"));
            Long employeeId = (Long) db.create(new Employee(managerId, "Samuel Kim"));
            assertNotNull(managerId);
            assertNotNull(employeeId);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecords() throws Exception {
        tm.beginTransaction();
        try {
            Long managerId = (Long) db.create(new Employee("Tom Jones"));
            db.create(new Employee(managerId, "Samuel Kim"));
            assertEquals(2, db.countAll(new EmployeeQuery().ignoreEmptyCriteria(true)));
            db.deleteAll(new EmployeeQuery().managerId(managerId));
            assertEquals(1, db.countAll(new EmployeeQuery().ignoreEmptyCriteria(true)));
            Employee foundEmployee = db.find(Employee.class, managerId);
            assertNotNull(foundEmployee);
            assertEquals("Tom Jones", foundEmployee.getFullName());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long managerId = (Long) db.create(new Employee("Tom Jones"));
            Long employeeId = (Long) db.create(new Employee(managerId, "Samuel Kim"));

            Employee foundEmployee = db.find(Employee.class, managerId);
            assertNotNull(foundEmployee);
            assertEquals("Tom Jones", foundEmployee.getFullName());
            assertNull(foundEmployee.getManagerId());
            assertNull(foundEmployee.getManagerFullName());

            foundEmployee = db.find(Employee.class, employeeId);
            assertNotNull(foundEmployee);
            assertEquals("Samuel Kim", foundEmployee.getFullName());
            assertEquals(managerId, foundEmployee.getManagerId());
            assertNull(foundEmployee.getManagerFullName());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long managerId = (Long) db.create(new Employee("Tom Jones"));
            Long employeeId = (Long) db.create(new Employee(managerId, "Samuel Kim"));

            Employee foundEmployee = db.list(Employee.class, managerId);
            assertNotNull(foundEmployee);
            assertEquals("Tom Jones", foundEmployee.getFullName());
            assertNull(foundEmployee.getManagerId());
            assertNull(foundEmployee.getManagerFullName());

            foundEmployee = db.list(Employee.class, employeeId);
            assertNotNull(foundEmployee);
            assertEquals("Samuel Kim", foundEmployee.getFullName());
            assertEquals(managerId, foundEmployee.getManagerId());
            assertEquals("Tom Jones", foundEmployee.getManagerFullName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long managerId = (Long) db.create(new Employee("Tom Jones"));
            db.create(new Employee(managerId, "Samuel Kim"));

            Employee foundEmployee = db.list(new EmployeeQuery().managerId(managerId));
            assertNotNull(foundEmployee);
            assertEquals("Samuel Kim", foundEmployee.getFullName());
            assertEquals(managerId, foundEmployee.getManagerId());
            assertEquals("Tom Jones", foundEmployee.getManagerFullName());
            
            foundEmployee = db.list(new EmployeeQuery().managerFullNameLike("Jone"));
            assertNotNull(foundEmployee);
            assertEquals("Samuel Kim", foundEmployee.getFullName());
            assertEquals(managerId, foundEmployee.getManagerId());
            assertEquals("Tom Jones", foundEmployee.getManagerFullName());
            
            foundEmployee = db.list(new EmployeeQuery().managerFullNameLike("Morris"));
            assertNull(foundEmployee);
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
        deleteAll(Employee.class);
    }
}
