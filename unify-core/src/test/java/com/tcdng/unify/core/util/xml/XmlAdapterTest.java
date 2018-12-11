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

package com.tcdng.unify.core.util.xml;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.tcdng.unify.core.constant.DataType;

/**
 * XML adapter tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class XmlAdapterTest {

	@Test
	public void testEnumConstXmlAdapterMarshall() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(TestField.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		StringWriter sw = new StringWriter();
		marshaller.marshal(new TestField("age", DataType.INTEGER), sw);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><test-field><name>age</name><type>INTEGER</type></test-field>",
				sw.toString());
	}

	@Test
	public void testEnumConstXmlAdapterUnmarshall() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(TestField.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader sr = new StringReader(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><test-field><name>firstName</name><type>string</type></test-field>");
		TestField testField = (TestField) unmarshaller.unmarshal(sr);
		assertEquals("firstName", testField.getName());
		assertEquals(DataType.STRING, testField.getType());
	}
}
