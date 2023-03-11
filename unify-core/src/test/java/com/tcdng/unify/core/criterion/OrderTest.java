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

package com.tcdng.unify.core.criterion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.criterion.Order.Part;

/**
 * Order object tests
 * 
 * @author The Code Department
 * @since 1.0
 */
public class OrderTest {

    @Test
    public void testOrderTrailing() {
        Order order = new Order().add("name").add("description");
        List<Part> parts = order.getParts();
        assertNotNull(parts);
        assertEquals(2, parts.size());
        
        Part part = parts.get(0);
        assertNotNull(part);
        assertEquals("name", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
        
        part = parts.get(1);
        assertNotNull(part);
        assertEquals("description", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
    }

    @Test
    public void testOrderTrailingExplicitDirection() {
        Order order = new Order().add("name", OrderType.DESCENDING).add("description");
        List<Part> parts = order.getParts();
        assertNotNull(parts);
        assertEquals(2, parts.size());
        
        Part part = parts.get(0);
        assertNotNull(part);
        assertEquals("name", part.getField());
        assertEquals(OrderType.DESCENDING, part.getType());
        assertFalse(part.isAscending());
        
        part = parts.get(1);
        assertNotNull(part);
        assertEquals("description", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
    }

    @Test
    public void testOrderTrailingReplacement() {
        Order order = new Order().add("name").add("description").add("name", OrderType.DESCENDING);
        List<Part> parts = order.getParts();
        assertNotNull(parts);
        assertEquals(2, parts.size());
        
        Part part = parts.get(0);
        assertNotNull(part);
        assertEquals("description", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
        
        part = parts.get(1);
        assertNotNull(part);
        assertEquals("name", part.getField());
        assertEquals(OrderType.DESCENDING, part.getType());
        assertFalse(part.isAscending());
    }

    @Test
    public void testOrderLeading() {
        Order order = new Order(Order.Policy.ADD_LEADING).add("name").add("description");
        List<Part> parts = order.getParts();
        assertNotNull(parts);
        assertEquals(2, parts.size());
        
        Part part = parts.get(0);
        assertNotNull(part);
        assertEquals("description", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
        
        part = parts.get(1);
        assertNotNull(part);
        assertEquals("name", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
    }

    @Test
    public void testOrderLeadingExplicitDirection() {
        Order order = new Order(Order.Policy.ADD_LEADING).add("name", OrderType.DESCENDING).add("description");
        List<Part> parts = order.getParts();
        assertNotNull(parts);
        assertEquals(2, parts.size());
        
        Part part = parts.get(0);
        assertNotNull(part);
        assertEquals("description", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
        
        part = parts.get(1);
        assertNotNull(part);
        assertEquals("name", part.getField());
        assertEquals(OrderType.DESCENDING, part.getType());
        assertFalse(part.isAscending());
    }

    @Test
    public void testOrderLeadingReplacement() {
        Order order = new Order(Order.Policy.ADD_LEADING).add("name").add("description").add("name", OrderType.DESCENDING);
        List<Part> parts = order.getParts();
        assertNotNull(parts);
        assertEquals(2, parts.size());
        
        Part part = parts.get(0);
        assertNotNull(part);
        assertEquals("name", part.getField());
        assertEquals(OrderType.DESCENDING, part.getType());
        assertFalse(part.isAscending());
        
        part = parts.get(1);
        assertNotNull(part);
        assertEquals("description", part.getField());
        assertEquals(OrderType.ASCENDING, part.getType());
        assertTrue(part.isAscending());
    }
}
