/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.operation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Criteria builder tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class CriteriaBuilderTest {

    @Test
    public void testAndBuilderImplicitCalls() throws Exception {
        CriteriaBuilder cb = new AndBuilder();
        cb.like("name", "Mary").greaterEqual("salary", 14000.00).equal("age", 28);
        Criteria criteria = cb.getCriteria();
        assertEquals(Operator.AND, criteria.getOperator());
        Criteria postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.EQUAL, postOp.getOperator());
        assertEquals("age", postOp.getPreOp());
        assertEquals(28, postOp.getPostOp());

        criteria = (Criteria) criteria.getPreOp();
        assertEquals(Operator.AND, criteria.getOperator());
        postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.GREATER_OR_EQUAL, postOp.getOperator());
        assertEquals("salary", postOp.getPreOp());
        assertEquals(14000.00, postOp.getPostOp());

        Criteria preOp = (Criteria) criteria.getPreOp();
        assertEquals(Operator.LIKE, preOp.getOperator());
        assertEquals("name", preOp.getPreOp());
        assertEquals("Mary", preOp.getPostOp());
    }

    @Test
    public void testAndBuilderWithExplicitCalls() throws Exception {
        AndBuilder andb = new AndBuilder();
        andb.and(new Like("name", "Mary")).and(new GreaterOrEqual("salary", 14000.00)).and(new Equal("age", 28));

        Criteria criteria = andb.getCriteria();
        assertEquals(Operator.AND, criteria.getOperator());
        Criteria postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.EQUAL, postOp.getOperator());
        assertEquals("age", postOp.getPreOp());
        assertEquals(28, postOp.getPostOp());

        criteria = (Criteria) criteria.getPreOp();
        assertEquals(Operator.AND, criteria.getOperator());
        postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.GREATER_OR_EQUAL, postOp.getOperator());
        assertEquals("salary", postOp.getPreOp());
        assertEquals(14000.00, postOp.getPostOp());

        Criteria preOp = (Criteria) criteria.getPreOp();
        assertEquals(Operator.LIKE, preOp.getOperator());
        assertEquals("name", preOp.getPreOp());
        assertEquals("Mary", preOp.getPostOp());
    }

    @Test
    public void testOrBuilderImplicitCalls() throws Exception {
        CriteriaBuilder cb = new OrBuilder();
        cb.notLike("name", "Mary").lessEqual("salary", 14000.00).notEqual("age", 28);
        Criteria criteria = cb.getCriteria();
        assertEquals(Operator.OR, criteria.getOperator());
        Criteria postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.NOT_EQUAL, postOp.getOperator());
        assertEquals("age", postOp.getPreOp());
        assertEquals(28, postOp.getPostOp());

        criteria = (Criteria) criteria.getPreOp();
        assertEquals(Operator.OR, criteria.getOperator());
        postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.LESS_OR_EQUAL, postOp.getOperator());
        assertEquals("salary", postOp.getPreOp());
        assertEquals(14000.00, postOp.getPostOp());

        Criteria preOp = (Criteria) criteria.getPreOp();
        assertEquals(Operator.NOT_LIKE, preOp.getOperator());
        assertEquals("name", preOp.getPreOp());
        assertEquals("Mary", preOp.getPostOp());
    }

    @Test
    public void testOrBuilderExlicitCalls() throws Exception {
        OrBuilder orb = new OrBuilder();
        orb.or(new NotLike("name", "Mary")).or(new LessOrEqual("salary", 14000.00)).or(new NotEqual("age", 28));

        Criteria criteria = orb.getCriteria();
        assertEquals(Operator.OR, criteria.getOperator());
        Criteria postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.NOT_EQUAL, postOp.getOperator());
        assertEquals("age", postOp.getPreOp());
        assertEquals(28, postOp.getPostOp());

        criteria = (Criteria) criteria.getPreOp();
        assertEquals(Operator.OR, criteria.getOperator());
        postOp = (Criteria) criteria.getPostOp();
        assertEquals(Operator.LESS_OR_EQUAL, postOp.getOperator());
        assertEquals("salary", postOp.getPreOp());
        assertEquals(14000.00, postOp.getPostOp());

        Criteria preOp = (Criteria) criteria.getPreOp();
        assertEquals(Operator.NOT_LIKE, preOp.getOperator());
        assertEquals("name", preOp.getPreOp());
        assertEquals("Mary", preOp.getPostOp());
    }
}
