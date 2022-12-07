/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;

/**
 * Restriction field swap tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class RestrictionFieldSwapTest extends AbstractUnifyComponentTest {

	private static final Map<String, String> swap;

	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "name0");
		map.put("description", "description0");
		swap = Collections.unmodifiableMap(map);
	}

	private RestrictionTranslator rt;

	@Test
	public void testFieldSwapNoMatch() throws Exception {
		Restriction restriction = new Equals("age", "specs");
		String str1 = rt.translate(restriction);
		restriction.fieldSwap(swap);
		String str2 = rt.translate(restriction);
		assertEquals("$f{age} == 'specs'", str1);
		assertEquals("$f{age} == 'specs'", str2);
	}

	@Test
	public void testFieldSwapMatchAll() throws Exception {
		Restriction restriction = new And().add(new Equals("name", "specs")).add(new Equals("description", "aries"));
		String str1 = rt.translate(restriction);
		restriction.fieldSwap(swap);
		String str2 = rt.translate(restriction);
		assertEquals("$f{name} == 'specs' AND $f{description} == 'aries'", str1);
		assertEquals("$f{name0} == 'specs' AND $f{description0} == 'aries'", str2);
	}

	@Test
	public void testFieldSwapMatchSome() throws Exception {
		Restriction restriction = new And().add(new Equals("name", "specs")).add(new Equals("description", "aries"))
				.add(new Equals("salary", 2300));
		String str1 = rt.translate(restriction);
		restriction.fieldSwap(swap);
		String str2 = rt.translate(restriction);
		assertEquals("$f{name} == 'specs' AND $f{description} == 'aries' AND $f{salary} == 2300", str1);
		assertEquals("$f{name0} == 'specs' AND $f{description0} == 'aries' AND $f{salary} == 2300", str2);
	}

	@Override
	protected void onSetup() throws Exception {
		rt = (RestrictionTranslator) getComponent(ApplicationComponents.APPLICATION_RESTRICTIONTRANSLATOR);
	}

	@Override
	protected void onTearDown() throws Exception {

	}

}
