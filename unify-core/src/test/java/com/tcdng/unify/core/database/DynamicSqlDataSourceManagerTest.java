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
package com.tcdng.unify.core.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.sql.DynamicSqlDataSourceConfig;
import com.tcdng.unify.core.database.sql.DynamicSqlDataSourceManager;

/**
 * Dynamic SQL data source manager test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicSqlDataSourceManagerTest extends AbstractUnifyComponentTest {

	private static final String TEST_CONFIG = "test-config";

	@Test
	public void testConfigureDataSource() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		dm.configure(dsConfig);
	}

	@Test(expected = UnifyException.class)
	public void testConfigureSameDataSourceMultiple() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		dm.configure(dsConfig);
		dm.configure(dsConfig);
	}

	@Test
	public void testIsDataSourceConfigured() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		dm.configure(dsConfig);
		assertTrue(dm.isConfigured(TEST_CONFIG));
	}

	@Test
	public void testReconfigureDataSource() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		dm.configure(dsConfig);
		assertTrue(dm.reconfigure(dsConfig));
	}

	@Test
	public void testReconfigureNonManagedDataSource() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		assertFalse(dm.reconfigure(dsConfig));
	}

	@Test
	public void testGetDataSourceCount() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		assertEquals(0, dm.getDataSourceCount());

		DynamicSqlDataSourceConfig dsConfig = getConfig();
		dm.configure(dsConfig);
		assertEquals(1, dm.getDataSourceCount());
	}

	@Test
	public void testTestDataSourceConfiguration() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		assertTrue(dm.testConfiguration(dsConfig));
		assertEquals(0, dm.getDataSourceCount());
	}

	@Test
	public void testGetAndRestoreConnection() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		dm.configure(dsConfig);

		Connection connection = dm.getConnection(TEST_CONFIG);
		assertNotNull(connection);
		assertTrue(dm.restoreConnection(TEST_CONFIG, connection));
	}

	@Test
	public void testTerminateConfiguration() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		DynamicSqlDataSourceConfig dsConfig = getConfig();
		dm.configure(dsConfig);
		assertEquals(1, dm.getDataSourceCount());

		dm.terminateConfiguration(TEST_CONFIG);
		assertEquals(0, dm.getDataSourceCount());
	}

	@Test(expected = UnifyException.class)
	public void testTerminateUnknownConfiguration() throws Exception {
		DynamicSqlDataSourceManager dm = (DynamicSqlDataSourceManager) getComponent(
				ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
		dm.terminateConfiguration(TEST_CONFIG);
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {
		((DynamicSqlDataSourceManager) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER))
				.terminateAll();
	}

	private DynamicSqlDataSourceConfig getConfig() {
		return new DynamicSqlDataSourceConfig(TEST_CONFIG, "hsqldb-dialect", "org.hsqldb.jdbcDriver",
				"jdbc:hsqldb:mem:dyntest", null, null, 2, true);
	}

}
