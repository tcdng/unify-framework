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

package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Unique history data structure tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class UniqueHistoryTest {

    @Test
    public void testInitilization() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>();
        UniqueHistory<String> hist2 = new UniqueHistory<String>(0);
        UniqueHistory<String> hist3 = new UniqueHistory<String>(-1);
        UniqueHistory<String> hist4 = new UniqueHistory<String>(20);
        assertEquals(10, hist1.capacity());
        assertEquals(1, hist2.capacity());
        assertEquals(1, hist3.capacity());
        assertEquals(20, hist4.capacity());
    }
    
    @Test
    public void testAddItem() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(2);
        assertNull(hist1.add("Mary"));
        assertEquals(1, hist1.size());
        assertNull(hist1.add("Tom"));
        assertEquals(2, hist1.size());
    }
    
    @Test
    public void testGetItem() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(2);
        hist1.add("Mary");
       hist1.add("Tom");
        assertEquals("Mary", hist1.get(0));
        assertEquals("Tom", hist1.get(1));
    }
    
    @Test
    public void testClearAll() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(2);
        hist1.add("Mary");
       hist1.add("Tom");
       hist1.clear();
       assertEquals(0, hist1.size());
    }
    
    @Test
    public void testClearUniqueHistory() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(4);
        hist1.add("Mary");
        hist1.add("Tom");
        hist1.add("Sam");
        hist1.add("Tom");
        assertEquals(3, hist1.size());
        hist1.clear(2);
        assertEquals("Tom", hist1.get(0));
        assertEquals(1, hist1.size());
    }
    
    @Test
    public void testClearUniqueHistoryExaggerated() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(4);
        hist1.add("Mary");
        hist1.add("Tom");
        hist1.add("Sam");
        hist1.add("Tom");
        hist1.clear(20);
        assertEquals(0, hist1.size());
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetItemOutOfBoundsUpper() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(2);
        hist1.add("Mary");
        hist1.add("Tom");
        hist1.get(3);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetItemOutOfBoundsLower() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(2);
        hist1.add("Mary");
        hist1.add("Tom");
        hist1.get(-1);
    }
    
    @Test
    public void testUniqueHistoryRollover() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(2);
        hist1.add("Mary");
        hist1.add("Tom");
        assertEquals("Mary", hist1.add("Sam"));
        assertEquals(2, hist1.size());
        assertEquals("Tom", hist1.get(0));
        assertEquals("Sam", hist1.get(1));
    }
    
    @Test
    public void testHistoryUnique() throws Exception {
        UniqueHistory<String> hist1 = new UniqueHistory<String>(4);
        hist1.add("Mary");
        hist1.add("Tom");
        hist1.add("Sam");
        hist1.add("Tom");
        assertEquals(3, hist1.size());
        assertEquals("Mary", hist1.get(0));
        assertEquals("Sam", hist1.get(1));
        assertEquals("Tom", hist1.get(2));
    }
}
