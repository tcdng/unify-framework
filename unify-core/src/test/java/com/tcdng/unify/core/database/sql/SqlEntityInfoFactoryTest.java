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

package com.tcdng.unify.core.database.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.annotation.CallableDataType;
import com.tcdng.unify.core.database.CallableProcA;
import com.tcdng.unify.core.database.CallableProcB;
import com.tcdng.unify.core.database.CallableProcC;
import com.tcdng.unify.core.database.CallableProcD;
import com.tcdng.unify.core.database.CallableResultA;

/**
 * SQL entity info factory tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SqlEntityInfoFactoryTest extends AbstractUnifyComponentTest {

    private SqlEntityInfoFactory sqlEntityInfoFactory;

    @Test
    public void testGetSqlCallableInfoNoParamsNoResult() throws Exception {
        SqlCallableInfo sqlCallableInfo = getSqlEntityInfoFactory().getSqlCallableInfo(CallableProcA.class);
        assertNotNull(sqlCallableInfo);
        assertEquals(CallableProcA.class, sqlCallableInfo.getCallableClass());
        assertEquals("procedure_a", sqlCallableInfo.getProcedureName());
        assertEquals("procedure_a", sqlCallableInfo.getPreferredProcedureName());
        assertEquals("PUBLIC.procedure_a", sqlCallableInfo.getSchemaProcedureName());
        assertFalse(sqlCallableInfo.isParams());

        List<SqlCallableParamInfo> paramInfoList = sqlCallableInfo.getParamInfoList();
        assertNotNull(paramInfoList);
        assertTrue(paramInfoList.isEmpty());

        assertFalse(sqlCallableInfo.isResults());
    }

    @Test
    public void testGetSqlCallableInfoWithParamsNoResult() throws Exception {
        SqlCallableInfo sqlCallableInfo = getSqlEntityInfoFactory().getSqlCallableInfo(CallableProcB.class);
        assertNotNull(sqlCallableInfo);
        assertEquals(CallableProcB.class, sqlCallableInfo.getCallableClass());
        assertEquals("procedure_b", sqlCallableInfo.getProcedureName());
        assertEquals("procedure_b", sqlCallableInfo.getPreferredProcedureName());
        assertEquals("PUBLIC.procedure_b", sqlCallableInfo.getSchemaProcedureName());
        assertTrue(sqlCallableInfo.isParams());

        List<SqlCallableParamInfo> paramInfoList = sqlCallableInfo.getParamInfoList();
        assertNotNull(paramInfoList);
        assertEquals(5, paramInfoList.size());

        SqlCallableParamInfo sqlCallableParamInfo = paramInfoList.get(0);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.INTEGER, sqlCallableParamInfo.getDataType());
        assertEquals("itemId", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("itemId", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isInput());
        assertFalse(sqlCallableParamInfo.isOutput());

        sqlCallableParamInfo = paramInfoList.get(1);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.TIMESTAMP, sqlCallableParamInfo.getDataType());
        assertEquals("startDt", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("startDt", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isInput());
        assertFalse(sqlCallableParamInfo.isOutput());

        sqlCallableParamInfo = paramInfoList.get(2);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.DATE, sqlCallableParamInfo.getDataType());
        assertEquals("endDt", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("endDt", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isInput());
        assertFalse(sqlCallableParamInfo.isOutput());

        sqlCallableParamInfo = paramInfoList.get(3);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.STRING, sqlCallableParamInfo.getDataType());
        assertEquals("accountName", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("accountName", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isOutput());
        assertFalse(sqlCallableParamInfo.isInput());

        sqlCallableParamInfo = paramInfoList.get(4);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.DECIMAL, sqlCallableParamInfo.getDataType());
        assertEquals("balance", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("balance", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isOutput());
        assertFalse(sqlCallableParamInfo.isInput());

        assertFalse(sqlCallableInfo.isResults());
    }

    @Test
    public void testGetSqlCallableInfoNoParamsWithResult() throws Exception {
        SqlCallableInfo sqlCallableInfo = getSqlEntityInfoFactory().getSqlCallableInfo(CallableProcC.class);
        assertNotNull(sqlCallableInfo);
        assertEquals(CallableProcC.class, sqlCallableInfo.getCallableClass());
        assertEquals("procedure_c", sqlCallableInfo.getProcedureName());
        assertEquals("procedure_c", sqlCallableInfo.getPreferredProcedureName());
        assertEquals("PUBLIC.procedure_c", sqlCallableInfo.getSchemaProcedureName());
        assertFalse(sqlCallableInfo.isParams());

        List<SqlCallableParamInfo> paramInfoList = sqlCallableInfo.getParamInfoList();
        assertNotNull(paramInfoList);
        assertTrue(paramInfoList.isEmpty());

        List<SqlCallableResultInfo> resultInfoList = sqlCallableInfo.getResultInfoList();
        assertNotNull(resultInfoList);

        SqlCallableResultInfo sqlCallableResultInfo = resultInfoList.get(0);
        assertNotNull(sqlCallableResultInfo);
        assertEquals(CallableResultA.class, sqlCallableResultInfo.getCallableResultClass());
        List<SqlCallableFieldInfo> fieldInfoList = sqlCallableResultInfo.getFieldList();
        assertNotNull(fieldInfoList);
        assertEquals(2, fieldInfoList.size());
        
        SqlCallableFieldInfo sqlCallableFieldInfo = fieldInfoList.get(0);
        assertNotNull(sqlCallableFieldInfo);
        assertEquals(CallableDataType.STRING,sqlCallableFieldInfo.getDataType());
        assertEquals("licenseNo", sqlCallableFieldInfo.getName());
        assertNotNull(sqlCallableFieldInfo.getField());
        assertEquals("licenseNo", sqlCallableFieldInfo.getField().getName());
        assertNotNull(sqlCallableFieldInfo.getGetter());
        assertNotNull(sqlCallableFieldInfo.getSetter());

        sqlCallableFieldInfo = fieldInfoList.get(1);
        assertNotNull(sqlCallableFieldInfo);
        assertEquals(CallableDataType.DATE,sqlCallableFieldInfo.getDataType());
        assertEquals("expiryDt", sqlCallableFieldInfo.getName());
        assertNotNull(sqlCallableFieldInfo.getField());
        assertEquals("expiryDt", sqlCallableFieldInfo.getField().getName());
        assertNotNull(sqlCallableFieldInfo.getGetter());
        assertNotNull(sqlCallableFieldInfo.getSetter());
    }

    @Test
    public void testGetSqlCallableInfoWithParamsWithResult() throws Exception {
        SqlCallableInfo sqlCallableInfo = getSqlEntityInfoFactory().getSqlCallableInfo(CallableProcD.class);
        assertNotNull(sqlCallableInfo);
        assertEquals(CallableProcD.class, sqlCallableInfo.getCallableClass());
        assertEquals("procedure_d", sqlCallableInfo.getProcedureName());
        assertEquals("procedure_d", sqlCallableInfo.getPreferredProcedureName());
        assertEquals("PUBLIC.procedure_d", sqlCallableInfo.getSchemaProcedureName());
        assertTrue(sqlCallableInfo.isParams());

        List<SqlCallableParamInfo> paramInfoList = sqlCallableInfo.getParamInfoList();
        assertNotNull(paramInfoList);
        assertEquals(5, paramInfoList.size());

        SqlCallableParamInfo sqlCallableParamInfo = paramInfoList.get(0);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.INTEGER, sqlCallableParamInfo.getDataType());
        assertEquals("itemId", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("itemId", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isInput());
        assertFalse(sqlCallableParamInfo.isOutput());

        sqlCallableParamInfo = paramInfoList.get(1);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.TIMESTAMP, sqlCallableParamInfo.getDataType());
        assertEquals("startDt", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("startDt", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isInput());
        assertFalse(sqlCallableParamInfo.isOutput());

        sqlCallableParamInfo = paramInfoList.get(2);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.DATE, sqlCallableParamInfo.getDataType());
        assertEquals("endDt", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("endDt", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isInput());
        assertFalse(sqlCallableParamInfo.isOutput());

        sqlCallableParamInfo = paramInfoList.get(3);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.STRING, sqlCallableParamInfo.getDataType());
        assertEquals("accountName", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("accountName", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isOutput());
        assertTrue(sqlCallableParamInfo.isInput());

        sqlCallableParamInfo = paramInfoList.get(4);
        assertNotNull(sqlCallableParamInfo);
        assertEquals(CallableDataType.DECIMAL, sqlCallableParamInfo.getDataType());
        assertEquals("balance", sqlCallableParamInfo.getName());
        assertNotNull(sqlCallableParamInfo.getField());
        assertEquals("balance", sqlCallableParamInfo.getField().getName());
        assertNotNull(sqlCallableParamInfo.getGetter());
        assertNotNull(sqlCallableParamInfo.getSetter());
        assertTrue(sqlCallableParamInfo.isOutput());
        assertFalse(sqlCallableParamInfo.isInput());

        List<SqlCallableResultInfo> resultInfoList = sqlCallableInfo.getResultInfoList();
        assertNotNull(resultInfoList);

        SqlCallableResultInfo sqlCallableResultInfo = resultInfoList.get(0);
        assertNotNull(sqlCallableResultInfo);
        assertEquals(CallableResultA.class, sqlCallableResultInfo.getCallableResultClass());
        List<SqlCallableFieldInfo> fieldInfoList = sqlCallableResultInfo.getFieldList();
        assertNotNull(fieldInfoList);
        assertEquals(2, fieldInfoList.size());
        
        SqlCallableFieldInfo sqlCallableFieldInfo = fieldInfoList.get(0);
        assertNotNull(sqlCallableFieldInfo);
        assertEquals(CallableDataType.STRING,sqlCallableFieldInfo.getDataType());
        assertEquals("licenseNo", sqlCallableFieldInfo.getName());
        assertNotNull(sqlCallableFieldInfo.getField());
        assertEquals("licenseNo", sqlCallableFieldInfo.getField().getName());
        assertNotNull(sqlCallableFieldInfo.getGetter());
        assertNotNull(sqlCallableFieldInfo.getSetter());

        sqlCallableFieldInfo = fieldInfoList.get(1);
        assertNotNull(sqlCallableFieldInfo);
        assertEquals(CallableDataType.DATE,sqlCallableFieldInfo.getDataType());
        assertEquals("expiryDt", sqlCallableFieldInfo.getName());
        assertNotNull(sqlCallableFieldInfo.getField());
        assertEquals("expiryDt", sqlCallableFieldInfo.getField().getName());
        assertNotNull(sqlCallableFieldInfo.getGetter());
        assertNotNull(sqlCallableFieldInfo.getSetter());
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

    private SqlEntityInfoFactory getSqlEntityInfoFactory() throws Exception {
        if (sqlEntityInfoFactory == null) {
            sqlEntityInfoFactory =
                    (SqlEntityInfoFactory) getComponent(ApplicationComponents.APPLICATION_SQLENTITYINFOFACTORY);
            sqlEntityInfoFactory
                    .setSqlDataSourceDialect((SqlDataSourceDialect) getComponent(SqlDialectNameConstants.HSQLDB));
        }
        return sqlEntityInfoFactory;
    }
}
