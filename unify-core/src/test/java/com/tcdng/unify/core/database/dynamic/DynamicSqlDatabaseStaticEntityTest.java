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
package com.tcdng.unify.core.database.dynamic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.Setting;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceConfig;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceManager;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDatabase;
import com.tcdng.unify.core.database.sql.NameSqlDataSourceSchemaImpl;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Dynamic SQL database static entities test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicSqlDatabaseStaticEntityTest extends AbstractUnifyComponentTest {

    private static final String TEST_CONFIG = "test-config";

    private DatabaseTransactionManager tm;
    
    @Test(expected = NullPointerException.class)
    public void testGetDynamicSqlDatabaseNoDatasourceSetting() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE);
        assertNotNull(db);
        tm.beginTransaction();
        try {
            db.create(new AccountDetails("Bill Ray", "9200567689", BigDecimal.valueOf(5200.00)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testGetDynamicSqlDatabaseNoDatasourceConfigured() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", "mock-datasource"));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            db.create(new AccountDetails("Bill Ray", "9200567689", BigDecimal.valueOf(5200.00)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordAdhocEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            String id = (String) db.create(new AccountDetails("Bill Ray", "9200567689", BigDecimal.valueOf(5200.00)));
            assertEquals("Bill Ray", id);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdAdhocEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            AccountDetails accountDetails = new AccountDetails("Bill Ray", "9200567689", BigDecimal.valueOf(5200.01));
            db.create(accountDetails);

            AccountDetails foundAccountDetails = db.find(AccountDetails.class, accountDetails.getId());
            assertNotNull(foundAccountDetails);
            assertEquals("Bill Ray", foundAccountDetails.getAccountName());
            assertEquals("9200567689", foundAccountDetails.getAccountNo());
            assertEquals(BigDecimal.valueOf(5200.01), foundAccountDetails.getAvailBal());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdAdhocEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            AccountDetails accountDetails = new AccountDetails("Bill Ray", "9200567689", BigDecimal.valueOf(5200.00));
            db.create(accountDetails);
            accountDetails.setAccountNo("9200567444");
            accountDetails.setAvailBal(BigDecimal.valueOf(150.45));
            assertEquals(1, db.updateById(accountDetails));

            AccountDetails foundAccountDetails = db.find(AccountDetails.class, accountDetails.getId());
            assertNotNull(foundAccountDetails);
            assertEquals("Bill Ray", foundAccountDetails.getAccountName());
            assertEquals("9200567444", foundAccountDetails.getAccountNo());
            assertEquals(BigDecimal.valueOf(150.45), foundAccountDetails.getAvailBal());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdAdhocEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            String id = (String) db.create(new AccountDetails("Bill Ray", "9200567689", BigDecimal.valueOf(5200.00)));
            assertEquals(1, db.countAll(new AccountDetailsQuery().addEquals("accountName", id)));
            db.delete(AccountDetails.class, id);
            assertEquals(0, db.countAll(new AccountDetailsQuery().addEquals("accountName", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordPreferredEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            String id = (String) db.create(new Product("Compact Disc", 200, BigDecimal.valueOf(0.25)));
            assertEquals("Compact Disc", id);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdPreferredEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            Product product = new Product("Compact Disc", 200, BigDecimal.valueOf(0.25));
            db.create(product);

            Product foundProduct = db.find(Product.class, product.getId());
            assertNotNull(foundProduct);
            assertEquals("Compact Disc", foundProduct.getName());
            assertEquals(BigDecimal.valueOf(0.25), foundProduct.getPrice());
            assertEquals(Integer.valueOf(200), foundProduct.getQuantity());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdPreferredEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            Product product = new Product("Compact Disc", 200, BigDecimal.valueOf(0.25));
            db.create(product);
            product.setQuantity(125);
            product.setPrice(BigDecimal.valueOf(0.45));
            assertEquals(1, db.updateById(product));

            Product foundProduct = db.find(Product.class, product.getId());
            assertNotNull(foundProduct);
            assertEquals("Compact Disc", foundProduct.getName());
            assertEquals(BigDecimal.valueOf(0.45), foundProduct.getPrice());
            assertEquals(Integer.valueOf(125), foundProduct.getQuantity());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdPreferredEntity() throws Exception {
        DynamicSqlDatabase db = (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                new Setting("dataSourceConfigName", TEST_CONFIG));
        assertNotNull(db);
        tm.beginTransaction();
        try {
            String id = (String) db.create(new Product("Compact Disc", 82, BigDecimal.valueOf(0.72)));
            assertEquals(1, db.countAll(new ProductQuery().addEquals("name", id)));
            db.delete(Product.class, id);
            assertEquals(0, db.countAll(new ProductQuery().addEquals("name", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        super.doAddSettingsAndDependencies();
        addDependency("thirdparty-datasource", NameSqlDataSourceSchemaImpl.class, new Setting("appSchema", "PUBLIC"));
    }

    @Override
    protected void onSetup() throws Exception {
        // Get transaction manager
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);;
        
        // Configure data source and create adhoc (unmanaged) tables
        DynamicSqlDataSourceManager dynamicSqlDataSourceManager = (DynamicSqlDataSourceManager) getComponent(
                ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
        DynamicSqlDataSourceConfig config = new DynamicSqlDataSourceConfig("inventory", TEST_CONFIG, "hsqldb-dialect",
                "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:dyntest", null, null, null, 2, true);
        config.setManageSchema(true);
        dynamicSqlDataSourceManager.configure(config);
        Connection connection = dynamicSqlDataSourceManager.getConnection(TEST_CONFIG);
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE PUBLIC.ACCOUNT_DETAILS (" + "ACCOUNT_NM VARCHAR(48) NOT NULL PRIMARY KEY,"
                    + "ACCOUNT_NO VARCHAR(16) NOT NULL," + "AVAILABLE_BAL DECIMAL(14,2));");
            connection.commit();
        } finally {
            SqlUtils.close(stmt);
            dynamicSqlDataSourceManager.restoreConnection(TEST_CONFIG, connection);
        }
    }

    @Override
    protected void onTearDown() throws Exception {
        // Unconfigure and drop data source
        DynamicSqlDataSourceManager dynamicSqlDataSourceManager = (DynamicSqlDataSourceManager) getComponent(
                ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
        if (dynamicSqlDataSourceManager.isConfigured(TEST_CONFIG)) {
            dynamicSqlDataSourceManager.terminateAll();
        }
    }
}
