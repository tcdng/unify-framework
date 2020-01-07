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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.criterion.OrBuilder;

/**
 * Query tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class QueryTest {

    @Test
    public void testIsRetricted() throws Exception {
        Query<Fruit> query = Query.of(Fruit.class);
        assertFalse(query.isRestrictedField("name"));

        query.addEquals("name", "tangerine");
        assertTrue(query.isRestrictedField("name"));
        assertFalse(query.isRestrictedField("price"));
    }

    @Test
    public void testIsRetrictedCompound() throws Exception {
        Query<Fruit> query = Query.of(Fruit.class);
        assertFalse(query.isRestrictedField("name"));
        assertFalse(query.isRestrictedField("price"));

        query.addEquals("name", "tangerine");
        query.addRestriction(new OrBuilder().equals("price", 45.0).equals("price", 60.0).build());
        assertTrue(query.isRestrictedField("name"));
        assertTrue(query.isRestrictedField("price"));
    }
}
