/*
 * Copyright 2018-2022 The Code Department.
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

package com.tcdng.unify.core.database.sql.criterion.policy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.constant.Gender;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.database.sql.SqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlDialectNameConstants;

/**
 * SQL criteria policy translation tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class SqlCriteriaPolicyTranslationTest extends AbstractUnifyComponentTest {

    @Test
    public void testTranslateEquals() throws Exception {
        SqlDataSourceDialect dialect = (SqlDataSourceDialect) getComponent(SqlDialectNameConstants.HSQLDB);
        SqlCriteriaPolicy policy = dialect.getSqlCriteriaPolicy(RestrictionType.EQUALS);
        StringBuilder sb = new StringBuilder();
        policy.translate(sb, "BOOK", "TITLE", "C++ for Engineers", null);
        assertEquals("BOOK.TITLE = 'C++ for Engineers'", sb.toString());

        sb = new StringBuilder();
        policy.translate(sb, "BOOK", "PRICE", 23.45, null);
        assertEquals("BOOK.PRICE = 23.45", sb.toString());

        sb = new StringBuilder();
        policy.translate(sb, "BOOK", "AUTHOR_GENDER", Gender.FEMALE, null);
        assertEquals("BOOK.AUTHOR_GENDER = 'F'", sb.toString());
    }

    @Test
    public void testTranslateLike() throws Exception {
        SqlDataSourceDialect dialect = (SqlDataSourceDialect) getComponent(SqlDialectNameConstants.HSQLDB);
        SqlCriteriaPolicy policy = dialect.getSqlCriteriaPolicy(RestrictionType.LIKE);
        StringBuilder sb = new StringBuilder();
        policy.translate(sb, "BOOK", "TITLE", "C++ for Engineers", null);
        assertEquals("BOOK.TITLE LIKE '%C++ for Engineers%'", sb.toString());
    }

    @Test
    public void testTranslateILike() throws Exception {
        SqlDataSourceDialect dialect = (SqlDataSourceDialect) getComponent(SqlDialectNameConstants.HSQLDB);
        SqlCriteriaPolicy policy = dialect.getSqlCriteriaPolicy(RestrictionType.ILIKE);
        StringBuilder sb = new StringBuilder();
        policy.translate(sb, "BOOK", "TITLE", "C++ for Engineers", null);
        assertEquals("LOWER(BOOK.TITLE) LIKE '%c++ for engineers%'", sb.toString());
    }

    @Override
    protected void onSetup() throws Exception {
        
    }

    @Override
    protected void onTearDown() throws Exception {
        
    }
}
