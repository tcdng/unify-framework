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
package com.tcdng.unify.core.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;

/**
 * Database query by field tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DatabaseQueryByFieldTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testQueryEqualsField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList = db.findAll(Query.of(Product.class).addEqualsField("name", "description"));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            prodList =
                    db.findAll(Query.of(Product.class).addEqualsField("costPrice", "salesPrice").addOrder("costPrice"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            product = prodList.get(0);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(45.00), product.getSalesPrice());

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
    public void testQueryNotEqualsField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addNotEqualsField("name", "description").addOrder("id"));
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

            prodList = db.findAll(
                    Query.of(Product.class).addNotEqualsField("costPrice", "salesPrice").addOrder("costPrice"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            product = prodList.get(0);
            assertEquals("pants", product.getName());
            assertEquals("Wonder pants", product.getDescription());
            assertEquals(Double.valueOf(15.00), product.getCostPrice());
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryGreaterThanField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addGreaterThanField("salesPrice", "costPrice").addOrder("id"));
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
    public void testQueryGreaterThanEqualField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 19.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 42.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addGreaterThanEqualField("salesPrice", "costPrice").addOrder("id"));
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
            assertEquals(Double.valueOf(17.45), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryLessThanField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addLessThanField("salesPrice", "costPrice").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(1, prodList.size());

            Product product = prodList.get(0);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
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
    public void testQueryLessThanEqualField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 19.25));
            db.create(new Product("hat", "Red Hat", 60.00, 60.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 42.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addLessThanEqualField("salesPrice", "costPrice").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(3, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(19.25), product.getSalesPrice());

            product = prodList.get(1);
            assertEquals("hat", product.getName());
            assertEquals("Red Hat", product.getDescription());
            assertEquals(Double.valueOf(60.00), product.getCostPrice());
            assertEquals(Double.valueOf(60.00), product.getSalesPrice());

            product = prodList.get(2);
            assertEquals("specs", product.getName());
            assertEquals("Blue Spectacles", product.getDescription());
            assertEquals(Double.valueOf(45.00), product.getCostPrice());
            assertEquals(Double.valueOf(42.00), product.getSalesPrice());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testQueryLikeField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addLikeField("description", "name").addOrder("id"));
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
    public void testQueryNotLikeField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "Red Hat", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addNotLikeField("description", "name").addOrder("id"));
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
    public void testQueryBeginsWithField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addBeginsWithField("description", "name").addOrder("id"));
            assertNotNull(prodList);
            assertEquals(2, prodList.size());

            Product product = prodList.get(0);
            assertEquals("bandana", product.getName());
            assertEquals("bandana", product.getDescription());
            assertEquals(Double.valueOf(20.00), product.getCostPrice());
            assertEquals(Double.valueOf(25.25), product.getSalesPrice());

            product = prodList.get(1);
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
    public void testQueryNotBeginWithField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addNotBeginWithField("description", "name").addOrder("id"));
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
    public void testQueryEndsWithField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addEndsWithField("description", "name").addOrder("id"));
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
    public void testQueryNotEndWithField() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Product("bandana", "bandana", 20.00, 25.25));
            db.create(new Product("hat", "hat In Red", 60.00, 52.00));
            db.create(new Product("specs", "Blue Spectacles", 45.00, 45.00));
            db.create(new Product("pants", "Wonder pants", 15.00, 17.45));

            List<Product> prodList =
                    db.findAll(Query.of(Product.class).addNotEndWithField("description", "name").addOrder("id"));
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
