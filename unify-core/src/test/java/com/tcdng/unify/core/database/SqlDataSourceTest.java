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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlTableInfo;
import com.tcdng.unify.core.database.sql.SqlTableType;

/**
 * SQL data source tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlDataSourceTest extends AbstractUnifyComponentTest {

    private SqlDataSource sqlDataSource;

    @Test
    public void testGetConnection() throws Exception {
        Connection connection = null;
        try {
            connection = sqlDataSource.getConnection();
            assertFalse(connection.isClosed());
        } finally {
            if (connection != null) {
                sqlDataSource.restoreConnection(connection);
            }
        }
    }

    @Test
    public void testRestoreConnection() throws Exception {
        int availableConnections = sqlDataSource.getAvailableConnections();
        Connection connection = null;
        try {
            connection = sqlDataSource.getConnection();
        } finally {
            if (connection != null) {
                assertTrue(sqlDataSource.restoreConnection(connection));
                assertFalse(sqlDataSource.restoreConnection(connection));// Already
                                                                         // restored
            }
        }
        assertEquals(availableConnections, sqlDataSource.getAvailableConnections());
    }

    @Test
    public void testAvailableConnections() throws Exception {
        int availableConnections = sqlDataSource.getAvailableConnections();
        Connection connection = null;
        try {
            connection = sqlDataSource.getConnection();
            assertEquals(availableConnections - 1, sqlDataSource.getAvailableConnections());
        } finally {
            if (connection != null) {
                sqlDataSource.restoreConnection(connection);
            }
        }
        assertEquals(availableConnections, sqlDataSource.getAvailableConnections());
    }

    @Test
    public void testGetMultipleConnections() throws Exception {
        int availableConnections = sqlDataSource.getAvailableConnections();
        Connection connection1 = null;
        Connection connection2 = null;
        try {
            connection1 = sqlDataSource.getConnection();
            assertEquals(availableConnections - 1, sqlDataSource.getAvailableConnections());

            connection2 = sqlDataSource.getConnection();
            assertEquals(availableConnections - 2, sqlDataSource.getAvailableConnections());
        } finally {
            if (connection1 != null) {
                sqlDataSource.restoreConnection(connection1);
            }
            if (connection2 != null) {
                sqlDataSource.restoreConnection(connection2);
            }
        }
        assertEquals(availableConnections, sqlDataSource.getAvailableConnections());
    }

    @Test(expected = UnifyException.class)
    public void testGetConnectionTimeout() throws Exception {
        List<Connection> connectionList = new ArrayList<Connection>();
        try {
            while (connectionList.add(sqlDataSource.getConnection())) {
                Thread.yield();
            }
        } finally {
            for (Connection connection : connectionList) {
                sqlDataSource.restoreConnection(connection);
            }
        }
    }

    @Test
    public void testTestConnection() throws Exception {
        assertTrue(sqlDataSource.testConnection());
    }

    @Test
    public void testGetSchemaList() throws Exception {
        List<String> schemaList = sqlDataSource.getSchemaList();
        assertNotNull(schemaList);
        assertFalse(schemaList.isEmpty());
        assertTrue(schemaList.contains("PUBLIC"));
    }

    @Test
    public void testGetTableList() throws Exception {
        List<SqlTableInfo> tableList = sqlDataSource.getTableList("PUBLIC", null);
        assertNotNull(tableList);
        assertFalse(tableList.isEmpty());
    }

    @Test
    public void testGetTableListNullSchema() throws Exception {
        List<SqlTableInfo> tableList = sqlDataSource.getTableList(null, null);
        assertNotNull(tableList);
        assertTrue(tableList.isEmpty());
    }

    @Test
    public void testGetTableListUnknownSchema() throws Exception {
        List<SqlTableInfo> tableList = sqlDataSource.getTableList("TERRAHAWKS", null);
        assertNotNull(tableList);
        assertTrue(tableList.isEmpty());
    }

    @Test
    public void testGetTableMapAllTableTypes() throws Exception {
        Map<String, SqlTableInfo> tableMap = sqlDataSource.getTableMap("PUBLIC", null);
        assertNotNull(tableMap);
        assertFalse(tableMap.isEmpty());

        SqlTableInfo tInfo = tableMap.get("AUTHOR");
        assertNotNull(tInfo);
        assertFalse(tInfo.getType().isView());

        tInfo = tableMap.get("V_AUTHOR");
        assertNotNull(tInfo);
        assertTrue(tInfo.getType().isView());
    }

    @Test
    public void testGetTableMapTableOnly() throws Exception {
        Map<String, SqlTableInfo> tableMap = sqlDataSource.getTableMap("PUBLIC", SqlTableType.TABLE);
        assertNotNull(tableMap);
        assertFalse(tableMap.isEmpty());

        SqlTableInfo tInfo = tableMap.get("AUTHOR");
        assertNotNull(tInfo);
        assertFalse(tInfo.getType().isView());

        tInfo = tableMap.get("V_AUTHOR");
        assertNull(tInfo);
    }

    @Test
    public void testGetTableMapViewOnly() throws Exception {
        Map<String, SqlTableInfo> tableMap = sqlDataSource.getTableMap("PUBLIC", SqlTableType.VIEW);
        assertNotNull(tableMap);
        assertFalse(tableMap.isEmpty());

        SqlTableInfo tInfo = tableMap.get("AUTHOR");
        assertNull(tInfo);

        tInfo = tableMap.get("V_AUTHOR");
        assertNotNull(tInfo);
        assertTrue(tInfo.getType().isView());
    }

    @Test
    public void testGetColumnList() throws Exception {
        List<SqlColumnInfo> columnList = sqlDataSource.getColumnList("PUBLIC", "AUTHOR");
        assertNotNull(columnList);
        assertFalse(columnList.isEmpty());
    }

    @Test
    public void testGetColumnListNullSchemaTable() throws Exception {
        List<SqlColumnInfo> columnList = sqlDataSource.getColumnList("PUBLIC", null);
        assertNotNull(columnList);
        assertTrue(columnList.isEmpty());

        columnList = sqlDataSource.getColumnList(null, "AUTHOR");
        assertNotNull(columnList);
        assertTrue(columnList.isEmpty());

        columnList = sqlDataSource.getColumnList(null, null);
        assertNotNull(columnList);
        assertTrue(columnList.isEmpty());
    }

    @Test
    public void testGetColumnListUnknownTable() throws Exception {
        List<SqlColumnInfo> columnList = sqlDataSource.getColumnList("PUBLIC", "COUNTRY");
        assertNotNull(columnList);
        assertTrue(columnList.isEmpty());
    }

    @Test
    public void testGetColumnMap() throws Exception {
        Map<String, SqlColumnInfo> columnMap = sqlDataSource.getColumnMap("PUBLIC", "AUTHOR");
        assertNotNull(columnMap);
        assertFalse(columnMap.isEmpty());

        Set<String> columnNames = columnMap.keySet();
        assertTrue(columnNames.contains("GENDER"));
        assertTrue(columnNames.contains("OFFICE_ID"));
        assertTrue(columnNames.contains("NAME"));
        assertTrue(columnNames.contains("RETIRED"));
        assertTrue(columnNames.contains("AUTHOR_ID"));
        assertTrue(columnNames.contains("VERSION_NO"));
        assertTrue(columnNames.contains("AGE"));

        columnMap = sqlDataSource.getColumnMap("PUBLIC", "V_AUTHOR");
        assertNotNull(columnMap);
        assertFalse(columnMap.isEmpty());

        columnNames = columnMap.keySet();
        assertTrue(columnNames.contains("GENDER"));
        assertTrue(columnNames.contains("OFFICE_ID"));
        assertTrue(columnNames.contains("OFFICE_TELEPHONE"));
        assertTrue(columnNames.contains("OFFICE_ADDRESS"));
        assertTrue(columnNames.contains("NAME"));
        assertTrue(columnNames.contains("RETIRED"));
        assertTrue(columnNames.contains("RETIRED_DESC"));
        assertTrue(columnNames.contains("AUTHOR_ID"));
        assertTrue(columnNames.contains("VERSION_NO"));
        assertTrue(columnNames.contains("AGE"));

    }

    @Override
    protected void onSetup() throws Exception {
        sqlDataSource = (SqlDataSource) getComponent(ApplicationComponents.APPLICATION_DATASOURCE);
    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
