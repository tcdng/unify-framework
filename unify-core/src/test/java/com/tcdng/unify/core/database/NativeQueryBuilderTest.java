/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.criterion.RestrictionType;

/**
 * Native query builder tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class NativeQueryBuilderTest {

    @Test
    public void testBuildSingleCompoundFilterQuery() throws Exception {
        NativeQuery nq = NativeQuery.newBuilder().beginCompoundFilter(RestrictionType.AND)
                .addSimpleFilter(RestrictionType.EQUALS, "TESTTBL", "TESTTBL_ID", 101L, null).endCompoundFilter()
                .build();
        assertNotNull(nq);

        NativeQuery.Filter rootFilter = nq.getRootFilter();
        assertNotNull(rootFilter);
        assertEquals(RestrictionType.AND, rootFilter.getOp());
        List<NativeQuery.Filter> subFilterList = rootFilter.getSubFilterList();
        assertNotNull(subFilterList);
        assertEquals(1, subFilterList.size());

        NativeQuery.Filter subFilter = subFilterList.get(0);
        assertNotNull(subFilter);
        assertEquals(RestrictionType.EQUALS, subFilter.getOp());
        assertEquals("TESTTBL", subFilter.getTableName());
        assertEquals("TESTTBL_ID", subFilter.getColumnName());
        assertEquals(101L, subFilter.getParam1());
        assertNull(subFilter.getParam2());
    }

    @Test
    public void testBuildDeepCompoundFilterQuery() throws Exception {
        NativeQuery nq = NativeQuery.newBuilder().beginCompoundFilter(RestrictionType.AND)
                .addSimpleFilter(RestrictionType.EQUALS, "TESTTBL", "TESTTBL_ID", 101L, null)
                .addSimpleFilter(RestrictionType.BETWEEN, "TESTTBL", "AGE", 24, 46)
                .beginCompoundFilter(RestrictionType.OR)
                .addSimpleFilter(RestrictionType.BEGINS_WITH, "TESTTBL", "NAME", "A", null)
                .addSimpleFilter(RestrictionType.BEGINS_WITH, "TESTTBL", "NAME", "B", null)
                .endCompoundFilter()
                .addSimpleFilter(RestrictionType.IS_NOT_NULL, "TESTTBL", "ADDRESS", null, null)
                .endCompoundFilter()
                .build();
        assertNotNull(nq);

        NativeQuery.Filter rootFilter = nq.getRootFilter();
        assertNotNull(rootFilter);
        assertEquals(RestrictionType.AND, rootFilter.getOp());
        List<NativeQuery.Filter> subFilterList = rootFilter.getSubFilterList();
        assertNotNull(subFilterList);
        assertEquals(4, subFilterList.size());

        NativeQuery.Filter subFilter1 = subFilterList.get(0);
        assertNotNull(subFilter1);
        assertEquals(RestrictionType.EQUALS, subFilter1.getOp());
        assertEquals("TESTTBL", subFilter1.getTableName());
        assertEquals("TESTTBL_ID", subFilter1.getColumnName());
        assertEquals(101L, subFilter1.getParam1());
        assertNull(subFilter1.getParam2());
        assertNull(subFilter1.getSubFilterList());

        subFilter1 = subFilterList.get(1);
        assertNotNull(subFilter1);
        assertEquals(RestrictionType.BETWEEN, subFilter1.getOp());
        assertEquals("TESTTBL", subFilter1.getTableName());
        assertEquals("AGE", subFilter1.getColumnName());
        assertEquals(24, subFilter1.getParam1());
        assertEquals(46, subFilter1.getParam2());
        assertNull(subFilter1.getSubFilterList());

        subFilter1 = subFilterList.get(2);
        assertNotNull(subFilter1);
        assertEquals(RestrictionType.OR, subFilter1.getOp());
        assertNotNull(subFilter1.getSubFilterList());
        assertEquals(2, subFilter1.getSubFilterList().size());
        
        NativeQuery.Filter subFilter2 = subFilter1.getSubFilterList().get(0);
        assertNotNull(subFilter2);
        assertEquals(RestrictionType.BEGINS_WITH, subFilter2.getOp());
        assertEquals("TESTTBL", subFilter2.getTableName());
        assertEquals("NAME", subFilter2.getColumnName());
        assertEquals("A", subFilter2.getParam1());
        assertNull(subFilter2.getParam2());
        assertNull(subFilter2.getSubFilterList());
        
        subFilter2 = subFilter1.getSubFilterList().get(1);
        assertNotNull(subFilter2);
        assertEquals(RestrictionType.BEGINS_WITH, subFilter2.getOp());
        assertEquals("TESTTBL", subFilter2.getTableName());
        assertEquals("NAME", subFilter2.getColumnName());
        assertEquals("B", subFilter2.getParam1());
        assertNull(subFilter2.getParam2());
        assertNull(subFilter2.getSubFilterList());

        subFilter1 = subFilterList.get(3);
        assertNotNull(subFilter1);
        assertEquals(RestrictionType.IS_NOT_NULL, subFilter1.getOp());
        assertEquals("TESTTBL", subFilter1.getTableName());
        assertEquals("ADDRESS", subFilter1.getColumnName());
        assertNull(subFilter1.getParam1());
        assertNull(subFilter1.getParam2());
        assertNull(subFilter1.getSubFilterList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBeginCompoundFilterBadOp() throws Exception {
        NativeQuery.newBuilder().beginCompoundFilter(RestrictionType.EQUALS);
    }

    @Test(expected = IllegalStateException.class)
    public void testEndCompoundFilterNoContext() throws Exception {
        NativeQuery.newBuilder().endCompoundFilter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSimpleFilterBadOp() throws Exception {
        NativeQuery.newBuilder().addSimpleFilter(RestrictionType.AND, "TESTTBL", "TESTTBL_ID", null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddSimpleFilterNoContext() throws Exception {
        NativeQuery.newBuilder().addSimpleFilter(RestrictionType.EQUALS, "TESTTBL", "TESTTBL_ID", 201, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildOnCompoundFilterContextOpen() throws Exception {
        NativeQuery.newBuilder().beginCompoundFilter(RestrictionType.AND).build();
    }
}
