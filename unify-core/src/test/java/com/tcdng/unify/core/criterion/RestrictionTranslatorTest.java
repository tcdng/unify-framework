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

package com.tcdng.unify.core.criterion;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;

/**
 * Restriction translator tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class RestrictionTranslatorTest extends AbstractUnifyComponentTest {

    private static class TestRestrictionTranslatorMapper extends AbstractRestrictionTranslatorMapper {

        private static final Map<String, String> fieldLabels;

        static {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", "Name");
            map.put("description", "Description");
            map.put("costPrice", "Cost Price");
            map.put("salesPrice", "Sales Price");
            fieldLabels = Collections.unmodifiableMap(map);
        }

        @Override
        public String getLabelTranslation(String fieldName) throws UnifyException {
            return fieldLabels.get(fieldName);
        }

        @Override
        public String getValueTranslation(String fieldName, Object val) throws UnifyException {
            return null;
        }
        
    }
    
    private RestrictionTranslator rt;

    private RestrictionTranslatorMapper mapper;

    @Test
    public void testTranslateNullRestriction() throws Exception {
        assertEquals("All", rt.translate(null));
    }

    @Test
    public void testTranslateEquals() throws Exception {
        assertEquals("$f{name} == 'specs'", rt.translate(new Equals("name", "specs")));
        assertEquals("Name == 'specs'", rt.translate(new Equals("name", "specs"), mapper));
    }

    @Test
    public void testTranslateNotEquals() throws Exception {
        assertEquals("$f{name} != 'bandana'", rt.translate(new NotEquals("name", "bandana")));
        assertEquals("Name != 'Mary'", rt.translate(new NotEquals("name", "Mary"), mapper));
    }

    @Test
    public void testTranslateGreaterThan() throws Exception {
        assertEquals("$f{costPrice} > 10.0", rt.translate(new Greater("costPrice", 10.00)));
        assertEquals("Cost Price > 10.0", rt.translate(new Greater("costPrice", 10.00), mapper));
    }

    @Test
    public void testTranslateGreaterThanEqual() throws Exception {
        assertEquals("$f{salesPrice} >= 60.0", rt.translate(new GreaterOrEqual("salesPrice", 60.00)));
        assertEquals("Sales Price >= 60.0", rt.translate(new GreaterOrEqual("salesPrice", 60.00), mapper));
    }

    @Test
    public void testTranslateLessThan() throws Exception {
        assertEquals("$f{costPrice} < 20.0", rt.translate(new Less("costPrice", 20.00)));
        assertEquals("Cost Price < 20.0", rt.translate(new Less("costPrice", 20.00), mapper));
    }

    @Test
    public void testTranslateLessThanEqual() throws Exception {
        assertEquals("$f{costPrice} <= 20.0", rt.translate(new LessOrEqual("costPrice", 20.00)));
        assertEquals("Cost Price <= 20.0", rt.translate(new LessOrEqual("costPrice", 20.00), mapper));
    }

    @Test
    public void testTranslateLike() throws Exception {
        assertEquals("$f{description} like 'an'", rt.translate(new Like("description", "an")));
        assertEquals("Description like 'an'", rt.translate(new Like("description", "an"), mapper));
    }

    @Test
    public void testTranslateNotLike() throws Exception {
        assertEquals("$f{description} not like 'an'", rt.translate(new NotLike("description", "an")));
        assertEquals("Description not like 'an'", rt.translate(new NotLike("description", "an"), mapper));
    }

    @Test
    public void testTranslateBeginsWith() throws Exception {
        assertEquals("$f{description} starts with 'Blue'", rt.translate(new BeginsWith("description", "Blue")));
        assertEquals("Description starts with 'Blue'",
                rt.translate(new BeginsWith("description", "Blue"), mapper));
    }

    @Test
    public void testTranslateNotBeginWith() throws Exception {
        assertEquals("$f{description} not start with 'Blue'", rt.translate(new NotBeginWith("description", "Blue")));
        assertEquals("Description not start with 'Blue'",
                rt.translate(new NotBeginWith("description", "Blue"), mapper));
    }

    @Test
    public void testTranslateEndsWith() throws Exception {
        assertEquals("$f{description} ends with 'Red'", rt.translate(new EndsWith("description", "Red")));
        assertEquals("Description ends with 'Red'", rt.translate(new EndsWith("description", "Red"), mapper));
    }

    @Test
    public void testTranslateNotEndWith() throws Exception {
        assertEquals("$f{description} not end with 'Red'", rt.translate(new NotEndWith("description", "Red")));
        assertEquals("Description not end with 'Red'", rt.translate(new NotEndWith("description", "Red"), mapper));
    }

    @Test
    public void testTranslateBetween() throws Exception {
        assertEquals("$f{costPrice} between (45.0, 50.0)", rt.translate(new Between("costPrice", 45.00, 50.00)));
        assertEquals("Cost Price between (45.0, 50.0)",
                rt.translate(new Between("costPrice", 45.00, 50.00), mapper));
    }

    @Test
    public void testTranslateNotBetween() throws Exception {
        assertEquals("$f{costPrice} not between (45.0, 50.0)", rt.translate(new NotBetween("costPrice", 45.00, 50.00)));
        assertEquals("Cost Price not between (45.0, 50.0)",
                rt.translate(new NotBetween("costPrice", 45.00, 50.00), mapper));
    }

    @Test
    public void testTranslateIsNull() throws Exception {
        assertEquals("$f{salesPrice} is null", rt.translate(new IsNull("salesPrice")));
        assertEquals("Sales Price is null", rt.translate(new IsNull("salesPrice"), mapper));
    }

    @Test
    public void testTranslateIsNotNull() throws Exception {
        assertEquals("$f{salesPrice} is not null", rt.translate(new IsNotNull("salesPrice")));
        assertEquals("Sales Price is not null", rt.translate(new IsNotNull("salesPrice"), mapper));
    }

    @Test
    public void testTranslateAmongst() throws Exception {
        assertEquals("$f{name} in ('specs', 'pants')",
                rt.translate(new Amongst("name", Arrays.asList("specs", "pants"))));
        assertEquals("Name in ('specs', 'pants')",
                rt.translate(new Amongst("name", Arrays.asList("specs", "pants")), mapper));
    }

    @Test
    public void testTranslateNotAmongst() throws Exception {
        assertEquals("$f{name} not in ('specs', 'pants')",
                rt.translate(new NotAmongst("name", Arrays.asList("specs", "pants"))));
        assertEquals("Name not in ('specs', 'pants')",
                rt.translate(new NotAmongst("name", Arrays.asList("specs", "pants")), mapper));
    }

    @Test
    public void testTranslateShallowAnd() throws Exception {
        assertEquals("$f{costPrice} between (45.0, 50.0) AND $f{description} starts with 'B'",
                rt.translate(new CriteriaBuilder().beginAnd().addBetween("costPrice", 45.00, 50.00)
                        .addBeginsWith("description", "B").endCompound().build()));
        assertEquals("Cost Price between (45.0, 50.0) AND Description starts with 'B'",
                rt.translate(new CriteriaBuilder().beginAnd().addBetween("costPrice", 45.00, 50.00)
                        .addBeginsWith("description", "B").endCompound().build(), mapper));
    }

    @Test
    public void testTranslateDeepAnd() throws Exception {
        assertEquals("($f{costPrice} >= 45.0 AND $f{costPrice} <= 50.0) AND $f{description} starts with 'B'",
                rt.translate(new CriteriaBuilder().beginAnd().beginAnd()
                        .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                        .addBeginsWith("description", "B").endCompound().build()));
        assertEquals("(Cost Price >= 45.0 AND Cost Price <= 50.0) AND Description starts with 'B'",
                rt.translate(new CriteriaBuilder().beginAnd().beginAnd()
                        .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                        .addBeginsWith("description", "B").endCompound().build(), mapper));
    }

    @Test
    public void testTranslateShallowOr() throws Exception {
        assertEquals("$f{costPrice} between (45.0, 50.0) OR $f{description} starts with 'B'",
                rt.translate(new CriteriaBuilder().beginOr().addBetween("costPrice", 45.00, 50.00)
                        .addBeginsWith("description", "B").endCompound().build()));
        assertEquals("Cost Price between (45.0, 50.0) OR Description starts with 'B'",
                rt.translate(new CriteriaBuilder().beginOr().addBetween("costPrice", 45.00, 50.00)
                        .addBeginsWith("description", "B").endCompound().build(), mapper));
    }

    @Test
    public void testTranslateDeepOr() throws Exception {
        assertEquals("($f{costPrice} >= 45.0 AND $f{costPrice} <= 50.0) OR $f{description} starts with 'B'",
                rt.translate(new CriteriaBuilder().beginOr().beginAnd()
                        .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                        .addBeginsWith("description", "B").endCompound().build()));
        assertEquals("(Cost Price >= 45.0 AND Cost Price <= 50.0) OR Description starts with 'B'",
                rt.translate(new CriteriaBuilder().beginOr().beginAnd()
                        .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                        .addBeginsWith("description", "B").endCompound().build(), mapper));
    }

    @Override
    protected void onSetup() throws Exception {
        rt = (RestrictionTranslator) getComponent(ApplicationComponents.APPLICATION_RESTRICTIONTRANSLATOR);
        mapper = new TestRestrictionTranslatorMapper();
    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
