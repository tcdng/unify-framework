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
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * CData XML adapter tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class CDataXmlAdapterTest {

	@Test
	public void testMarshalNull() throws Exception {
		CDataXmlAdapter adapter = new CDataXmlAdapter();
		assertEquals("<![CDATA[]]>", adapter.marshal(null));
	}

	@Test
	public void testMarshal() throws Exception {
		CDataXmlAdapter adapter = new CDataXmlAdapter();
		assertEquals("<![CDATA[Hello World!]]>", adapter.marshal("Hello World!"));
		assertEquals("<![CDATA[<span>Hello World!</span>]]>", adapter.marshal("<span>Hello World!</span>"));
	}

	@Test
	public void testUnmarshalNull() throws Exception {
		CDataXmlAdapter adapter = new CDataXmlAdapter();
		assertNull(adapter.unmarshal(null));
	}

	@Test
	public void testUnmarshalPlain() throws Exception {
		CDataXmlAdapter adapter = new CDataXmlAdapter();
		assertEquals("Hello World!", adapter.unmarshal("Hello World!"));
	}

	@Test
	public void testUnmarshalCData() throws Exception {
		CDataXmlAdapter adapter = new CDataXmlAdapter();
		assertEquals("Hello World!", adapter.unmarshal("<![CDATA[Hello World!]]>"));
		assertEquals("<span>Hello World!</span>", adapter.unmarshal("<![CDATA[<span>Hello World!</span>]]>"));
	}
}
