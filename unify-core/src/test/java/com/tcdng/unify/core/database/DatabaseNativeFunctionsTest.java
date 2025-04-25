/*
 * Copyright 2018-2025 The Code Department.
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

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;

/**
 * Database native functions tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DatabaseNativeFunctionsTest extends AbstractUnifyComponentTest {

	private DatabaseTransactionManager tm;

	private Database db;

	@Test
	public void testUpdateNative() throws Exception {
		tm.beginTransaction();
		try {
			Fruit apple = new Fruit("apple", "red", 20.00, 3);
			Fruit banana = new Fruit("banana", "yellow", 25.00, 10);
			Fruit orange = new Fruit("orange", "orange", 10.00, 5);
			db.create(apple);
			db.create(banana);
			db.create(orange);

			int updated = db.update(
					new NativeUpdate("UPDATE FRUIT SET PRICE = ?, QUANTITY = ?").setParam(0, 32.0).setParam(1, 8));
			assertEquals(3, updated);

			Fruit foundFruit = db.find(Fruit.class, apple.getId());
			assertEquals("apple", foundFruit.getName());
			assertEquals("red", foundFruit.getColor());
			assertEquals(Double.valueOf(32.0), foundFruit.getPrice());
			assertEquals(Integer.valueOf(8), foundFruit.getQuantity());

			foundFruit = db.find(Fruit.class, banana.getId());
			assertEquals("banana", foundFruit.getName());
			assertEquals("yellow", foundFruit.getColor());
			assertEquals(Double.valueOf(32.0), foundFruit.getPrice());
			assertEquals(Integer.valueOf(8), foundFruit.getQuantity());

			foundFruit = db.find(Fruit.class, orange.getId());
			assertEquals("orange", foundFruit.getName());
			assertEquals("orange", foundFruit.getColor());
			assertEquals(Double.valueOf(32.0), foundFruit.getPrice());
			assertEquals(Integer.valueOf(8), foundFruit.getQuantity());
		} finally {
			tm.endTransaction();
		}
	}

	@Test
	public void testUpdateNativeWithClause() throws Exception {
		tm.beginTransaction();
		try {
			Fruit apple = new Fruit("apple", "red", 20.00, 3);
			Fruit banana = new Fruit("banana", "yellow", 25.00, 10);
			Fruit orange = new Fruit("orange", "orange", 10.00, 5);
			db.create(apple);
			db.create(banana);
			db.create(orange);

			int updated = db
					.update(new NativeUpdate("UPDATE FRUIT SET PRICE = ?, QUANTITY = ? WHERE NAME = ? OR QUANTITY = ?")
							.setParam(0, 32.0).setParam(1, 8).setParam(2, "apple").setParam(3, 5));
			assertEquals(2, updated);

			Fruit foundFruit = db.find(Fruit.class, apple.getId());
			assertEquals("apple", foundFruit.getName());
			assertEquals("red", foundFruit.getColor());
			assertEquals(Double.valueOf(32.0), foundFruit.getPrice());
			assertEquals(Integer.valueOf(8), foundFruit.getQuantity());

			foundFruit = db.find(Fruit.class, banana.getId());
			assertEquals("banana", foundFruit.getName());
			assertEquals("yellow", foundFruit.getColor());
			assertEquals(Double.valueOf(25.0), foundFruit.getPrice());
			assertEquals(Integer.valueOf(10), foundFruit.getQuantity());

			foundFruit = db.find(Fruit.class, orange.getId());
			assertEquals("orange", foundFruit.getName());
			assertEquals("orange", foundFruit.getColor());
			assertEquals(Double.valueOf(32.0), foundFruit.getPrice());
			assertEquals(Integer.valueOf(8), foundFruit.getQuantity());
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
		deleteAll(Fruit.class);
	}
}
