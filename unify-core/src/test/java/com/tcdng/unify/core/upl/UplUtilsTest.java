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
package com.tcdng.unify.core.upl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * UPLUtils tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UplUtilsTest {

	@Test
	public void testGeneratUplAttributesKeyWithNullFields() throws Exception {
		String key = UplUtils.generateUplAttributesKey(0, null, null, null);
		assertEquals("0[0]>[1]>[2]>", key);
	}

	@Test
	public void testGeneratUplAttributesKey() throws Exception {
		String key1 = UplUtils.generateUplAttributesKey(1, "/tools/calculator", ".addend", null);
		assertEquals("1[0]>/tools/calculator[1]>.addend[2]>", key1);

		String key2 = UplUtils.generateUplAttributesKey(2, null, null, "!ui-text:error size:32");
		assertEquals("2[0]>[1]>[2]>!ui-text:error size:32", key2);

		String key3 = UplUtils.generateUplAttributesKey(1, "/tools/calculator", ".addend", "!ui-text:error size:32");
		assertEquals("1[0]>/tools/calculator[1]>.addend[2]>!ui-text:error size:32", key3);
	}

	@Test
	public void testExtractUplAttributesKeyFields() throws Exception {
		String key1 = "1[0]>/tools/calculator[1]>.addend[2]>!ui-text:error size:32";
		UplAttributesKeyFields uakf = UplUtils.extractUplAtributesKeyFields(key1);
		assertNotNull(uakf);
		assertEquals(1, uakf.getUplType());
		assertEquals("/tools/calculator", uakf.getComponentName());
		assertEquals(".addend", uakf.getLongName());
		assertEquals("!ui-text:error size:32", uakf.getDescriptor());

		String key2 = "3[0]>[1]>.addend[2]>";
		uakf = UplUtils.extractUplAtributesKeyFields(key2);
		assertNotNull(uakf);
		assertEquals(3, uakf.getUplType());
		assertNull(uakf.getComponentName());
		assertEquals(".addend", uakf.getLongName());
		assertNull(uakf.getDescriptor());
	}

	@Test(expected = Exception.class)
	public void testExtractUplAttributesKeyFieldsBad() throws Exception {
		String key1 = "1[0]>/tools/calculator[1]>";
		UplUtils.extractUplAtributesKeyFields(key1);
	}
}
