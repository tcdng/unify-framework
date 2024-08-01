/*
 * Copyright 2018-2024 The Code Department.
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

package com.tcdng.unify.core.util.xml;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tcdng.unify.core.constant.DataType;

/**
 * XML adapter tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class XmlAdapterTest {

	@Test
	public void testEnumConstXmlAdapterMarshall() throws Exception {
		XmlMapper marshaller = new XmlMapper();
		StringWriter sw = new StringWriter();
		marshaller.writeValue(sw, new TestField("age", DataType.INTEGER));
		assertEquals("<test-field><name>age</name><type>INTEGER</type></test-field>", sw.toString());
	}

	@Test
	public void testEnumConstXmlAdapterUnmarshall() throws Exception {
		XmlMapper unmarshaller = new XmlMapper();
		StringReader sr = new StringReader("<test-field><name>firstName</name><type>string</type></test-field>");
		TestField testField = unmarshaller.readValue(sr, TestField.class);
		assertEquals("firstName", testField.getName());
		assertEquals(DataType.STRING, testField.getType());
	}
}
