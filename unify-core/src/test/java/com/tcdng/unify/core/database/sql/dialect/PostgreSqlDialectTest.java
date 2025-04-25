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

package com.tcdng.unify.core.database.sql.dialect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlDialectNameConstants;

/**
 * PostgreSQL dialect tests.
 * 
 * @author Lateef Ojulari
 * @since 4.1
 */
public class PostgreSqlDialectTest extends AbstractUnifyComponentTest {

    @Test
    public void testMatchColumnDefaultMatched() throws Exception {
        SqlDataSourceDialect dialect = (SqlDataSourceDialect) getComponent(SqlDialectNameConstants.POSTGRESQL);
        assertTrue(dialect.matchColumnDefault("'SYSTEM'", "SYSTEM"));
        assertTrue(dialect.matchColumnDefault("SYSTEM", "SYSTEM"));
        assertTrue(dialect.matchColumnDefault("'SYSTEM'::character varying", "SYSTEM"));
        assertTrue(dialect.matchColumnDefault("'N'::bpchar", "'N'"));
        assertTrue(dialect.matchColumnDefault("'SYSTEM'", "'SYSTEM'"));        
        assertTrue(dialect.matchColumnDefault(null, null));        
    }

    @Test
    public void testMatchColumnDefaultUnmatched() throws Exception {
        SqlDataSourceDialect dialect = (SqlDataSourceDialect) getComponent(SqlDialectNameConstants.POSTGRESQL);
        assertFalse(dialect.matchColumnDefault("'SYSTEM'", "SYSTEM'"));
        assertFalse(dialect.matchColumnDefault("'SYSTEM::character varying", "SYSTEM"));
        assertFalse(dialect.matchColumnDefault("'SYSTEM::character varying", null));
        assertFalse(dialect.matchColumnDefault(null, "SYSTEM"));
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
