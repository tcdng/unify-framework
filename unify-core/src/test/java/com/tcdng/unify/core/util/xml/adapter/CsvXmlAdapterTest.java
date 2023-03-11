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
package com.tcdng.unify.core.util.xml.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Comma-separated string XML adapter tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class CsvXmlAdapterTest {

	@Test
	public void testMarshalNull() throws Exception {
	    CsvXmlAdapter adapter = new CsvXmlAdapter();
		assertNull(adapter.marshal(null));
	}

	@Test
	public void testMarshal() throws Exception {
	    CsvXmlAdapter adapter = new CsvXmlAdapter();
        assertEquals("Red", adapter.marshal(new String[]{"Red"}));
        assertEquals("Red,Green,Blue", adapter.marshal(new String[]{"Red", "Green", "Blue"}));
	}

	@Test
	public void testUnmarshalNull() throws Exception {
	    CsvXmlAdapter adapter = new CsvXmlAdapter();
        String[] items = adapter.unmarshal(null);
        assertNotNull(items);
        assertEquals(0, items.length);
	}

    @Test
    public void testUnmarshal() throws Exception {
        CsvXmlAdapter adapter = new CsvXmlAdapter();
        String[] items = adapter.unmarshal("Red");
        assertNotNull(items);
        assertEquals(1, items.length);
        assertEquals("Red", items[0]);

        items = adapter.unmarshal("Red,Green,Blue");
        assertNotNull(items);
        assertEquals(3, items.length);
        assertEquals("Red", items[0]);
        assertEquals("Green", items[1]);
        assertEquals("Blue", items[2]);
    }

    @Test
    public void testUnmarshalTrim() throws Exception {
        CsvXmlAdapter adapter = new CsvXmlAdapter();
        String[] items = adapter.unmarshal("Red ");
        assertNotNull(items);
        assertEquals(1, items.length);
        assertEquals("Red", items[0]);

        items = adapter.unmarshal(" Red,  Green, Blue  ");
        assertNotNull(items);
        assertEquals(3, items.length);
        assertEquals("Red", items[0]);
        assertEquals("Green", items[1]);
        assertEquals("Blue", items[2]);
    }
}
