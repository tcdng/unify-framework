/*
 * Copyright 2018-2025 The Code Department.
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

package com.tcdng.unify.exp4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.script.ScriptingEngine;

/**
 * Exp4j scripting engine tests
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Exp4jScriptingEngineTest extends AbstractUnifyComponentTest {

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateNullScript() throws Exception {
		final ScriptingEngine se = (ScriptingEngine) getComponent(
				Exp4jApplicationComponents.EXP4J_SCRIPTINGENGINE);
		assertNotNull(se);
		se.evaluate(Integer.class, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateBlankScript() throws Exception {
		final ScriptingEngine se = (ScriptingEngine) getComponent(
				Exp4jApplicationComponents.EXP4J_SCRIPTINGENGINE);
		assertNotNull(se);
		se.evaluate(Integer.class, "");
	}

	@Test
	public void testEvaluateSimpleScript() throws Exception {
		final ScriptingEngine se = (ScriptingEngine) getComponent(
				Exp4jApplicationComponents.EXP4J_SCRIPTINGENGINE);
		assertNotNull(se);
		Integer val = se.evaluate(Integer.class, "1 + 1");
		assertEquals(Integer.valueOf(2), val);
	}

	@Test
	public void testEvaluateCompoundScript() throws Exception {
		final ScriptingEngine se = (ScriptingEngine) getComponent(
				Exp4jApplicationComponents.EXP4J_SCRIPTINGENGINE);
		assertNotNull(se);
		Integer val = se.evaluate(Integer.class, "2 + 25(4)");
		assertEquals(Integer.valueOf(102), val);
	}

	@Test
	public void testEvaluateSimpleScriptVariable() throws Exception {
		final ScriptingEngine se = (ScriptingEngine) getComponent(
				Exp4jApplicationComponents.EXP4J_SCRIPTINGENGINE);
		assertNotNull(se);
		Map<String, Number> map = new HashMap<String, Number>();
		map.put("x", 3);
		Integer val = se.evaluate(Integer.class, "1 + x", map);
		assertEquals(Integer.valueOf(4), val);
	}

	@Test
	public void testEvaluateCompoundScriptVariable() throws Exception {
		final ScriptingEngine se = (ScriptingEngine) getComponent(
				Exp4jApplicationComponents.EXP4J_SCRIPTINGENGINE);
		assertNotNull(se);
		Map<String, Number> map = new HashMap<String, Number>();
		map.put("x1", 4);
		map.put("x2", 8);
		Integer val = se.evaluate(Integer.class, "x1 + 25(x2)", map);
		assertEquals(Integer.valueOf(204), val);
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {
		
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
