/*
 * Copyright (c) 2018-2025 The Code Department.
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

package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.criterion.Amongst;
import com.tcdng.unify.core.criterion.Between;
import com.tcdng.unify.core.criterion.CompoundRestriction;
import com.tcdng.unify.core.criterion.CriteriaBuilder;
import com.tcdng.unify.core.criterion.DoubleParamRestriction;
import com.tcdng.unify.core.criterion.Equals;
import com.tcdng.unify.core.criterion.FilterConditionType;
import com.tcdng.unify.core.criterion.IsNull;
import com.tcdng.unify.core.criterion.MultipleParamRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.SingleParamRestriction;
import com.tcdng.unify.core.criterion.ZeroParamRestriction;

/**
 * Criteria utilities test.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class CriteriaUtilsTest {

    @Test
    public void testUnmodifiableZeroParamRestriction() throws Exception {
        ZeroParamRestriction zo = CriteriaUtils.unmodifiableRestriction(new IsNull("name"));
        assertNotNull(zo);
        assertEquals("name", zo.getFieldName());
    }

    @Test
    public void testUnmodifiableSingleParamRestriction() throws Exception {
        SingleParamRestriction so = CriteriaUtils.unmodifiableRestriction(new Equals("name", "Tom"));
        assertNotNull(so);
        assertEquals("name", so.getFieldName());
        assertEquals("Tom", so.getParam());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableSingleParamRestrictionWrite() throws Exception {
        SingleParamRestriction so = CriteriaUtils.unmodifiableRestriction(new Equals("name", "Tom"));
        assertNotNull(so);
        so.setParam("Harry");
    }

    @Test
    public void testUnmodifiableDoubleParamRestriction() throws Exception {
        DoubleParamRestriction so = CriteriaUtils.unmodifiableRestriction(new Between("price", 22.6, 30.20));
        assertNotNull(so);
        assertEquals("price", so.getFieldName());
        assertEquals(Double.valueOf(22.6), so.getFirstParam());
        assertEquals(Double.valueOf(30.20), so.getSecondParam());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableDoubleParamRestrictionWrite() throws Exception {
        DoubleParamRestriction so = CriteriaUtils.unmodifiableRestriction(new Between("price", 22.6, 30.20));
        assertNotNull(so);
        so.setParams(34, 60);
    }

    @Test
    public void testUnmodifiableMultipleParamRestriction() throws Exception {
        MultipleParamRestriction so =
                CriteriaUtils.unmodifiableRestriction(new Amongst("price", Arrays.asList(22.6, 30.20)));
        assertNotNull(so);
        assertEquals("price", so.getFieldName());
        assertEquals(Arrays.asList(22.6, 30.20), so.getParams());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableMultipleParamRestrictionWrite() throws Exception {
        MultipleParamRestriction so =
                CriteriaUtils.unmodifiableRestriction(new Amongst("price", Arrays.asList(22.6, 30.20)));
        assertNotNull(so);
        so.setParams(Arrays.asList(22.65, 30.28));
    }

    @Test
    public void testUnmodifiableCompoundRestriction() throws Exception {
        CompoundRestriction co = CriteriaUtils.unmodifiableRestriction(new CriteriaBuilder().beginAnd()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertNotNull(co);
        assertEquals(FilterConditionType.AND, co.getConditionType());
        List<Restriction> restrictionList = co.getRestrictionList();
        assertNotNull(restrictionList);
        assertEquals(2, restrictionList.size());

        DoubleParamRestriction dro = (DoubleParamRestriction) restrictionList.get(0);
        assertNotNull(dro);
        assertEquals("costPrice", dro.getFieldName());
        assertEquals(Double.valueOf(45.00), dro.getFirstParam());
        assertEquals(Double.valueOf(50.00), dro.getSecondParam());
        
        SingleParamRestriction so = (SingleParamRestriction) restrictionList.get(1);
        assertNotNull(so);
        assertEquals("description", so.getFieldName());
        assertEquals("B", so.getParam());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableCompoundRestrictionAddRestriction() throws Exception {
        CompoundRestriction co = CriteriaUtils.unmodifiableRestriction(new CriteriaBuilder().beginAnd()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertNotNull(co);
        co.add(new Equals("name", "Tom"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableCompoundRestrictionAddRestrictionToList() throws Exception {
        CompoundRestriction co = CriteriaUtils.unmodifiableRestriction(new CriteriaBuilder().beginAnd()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertNotNull(co);
        co.getRestrictionList().add(new Equals("name", "Tom"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableCompoundRestrictionChangeElement() throws Exception {
        CompoundRestriction co = CriteriaUtils.unmodifiableRestriction(new CriteriaBuilder().beginAnd()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertNotNull(co);
        assertEquals(FilterConditionType.AND, co.getConditionType());
        List<Restriction> restrictionList = co.getRestrictionList();
        assertNotNull(restrictionList);
        assertEquals(2, restrictionList.size());

        DoubleParamRestriction dro = (DoubleParamRestriction) restrictionList.get(0);
        assertNotNull(dro);
        dro.setParams(34, 60);
    }
}
