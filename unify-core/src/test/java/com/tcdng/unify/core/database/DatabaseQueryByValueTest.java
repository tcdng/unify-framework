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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;

/**
 * Database query by value tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DatabaseQueryByValueTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testQueryEquals() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addEquals("name", "specs"));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            Product product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());

            prodList = db.findAll(Query.of(Product.class).addEquals("costPrice", 15.00));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            product = prodList.get(0);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryNotEquals() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addNotEquals("name", "bandana").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(3, prodList.size());

            Product product = prodList.get(0);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(60.00), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());

            product = prodList.get(2);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryGreaterThan() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 10.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 9.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addGreaterThan("costPrice", 10.00).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryGreaterThanEqual() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 19.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 42.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 72.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addGreaterThanEqual("salesPrice", 60.00).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(60.00), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(72.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryLessThan() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addLessThan("costPrice", 20.00).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            Product product = prodList.get(0);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryLessThanEqual() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 19.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 42.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addLessThanEqual("costPrice", 20.00).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(19.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryLike() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addLike("description", "an").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryNotLike() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addNotLike("description", "an").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(52.00), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryBeginsWith() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addBeginsWith("description", "Blue").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            Product product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryNotBeginWith() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addNotBeginWith("description", "ban")
                    .addNotBeginWith("description", "hat").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryEndsWith() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addEndsWith("description", "Red").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            Product product = prodList.get(0);
            assertEquals("hat", product.getName());
            assertEquals("hat In Red", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(52.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryNotEndWith() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addNotEndWith("description", "ana")
                    .addNotEndWith("description", "ants").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("hat", product.getName());
            assertEquals("hat In Red", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(52.00), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryBetween() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 49.50, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addBetween("costPrice", 45.00, 50.00).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(49.50), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryNotBetween() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 49.50, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addNotBetween("costPrice", 45.00, 50.00).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(60.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryBetweenStrings() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 49.50, 17.45));

            List<Product> prodList = db
                    .findAll(Query.of(Product.class).addBetween("name", "bandana", "pants").addOrder("name"));
            assertNotNull(prodList);
            assertEquals(3, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(60.00), product.getSalesPrice());

            product = prodList.get(2);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(49.50), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryNotBetweenStrings() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 49.50, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addNotBetween("name", "bandana", "pants").addOrder("name"));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            Product product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryIsNull() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, null));
            db.create(new Product("pants", "Wonder pants", 49.50, null));

            List<Product> prodList = db.findAll(Query.of(Product.class).addIsNull("salesPrice").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertNull(product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(49.50), product.getCostPrice());
            assertNull(product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryIsNotNull() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, null));
            db.create(new Product("pants", "Wonder pants", 49.50, null));

            List<Product> prodList = db.findAll(Query.of(Product.class).addIsNotNull("salesPrice").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(60.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryAmongst() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, null));
            db.create(new Product("pants", "Wonder pants", 49.50, null));

            List<Product> prodList = db.findAll(
                    Query.of(Product.class).addAmongst("name", Arrays.asList("specs", "pants")).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertNull(product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(49.50), product.getCostPrice());
            assertNull(product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryNotAmongst() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, null));
            db.create(new Product("pants", "Wonder pants", 49.50, null));

            List<Product> prodList = db.findAll(
                    Query.of(Product.class).addNotAmongst("name", Arrays.asList("specs", "pants")).addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(60.00), product.getSalesPrice());
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

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(Product.class);
    }
}
