/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.nashorn;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.script.ScriptingEngine;

/**
 * Simple password authentication tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class NashornScriptingEngineTest extends AbstractUnifyComponentTest {

    @Test
    public void testEvaluateScript() throws Exception {
        ScriptingEngine engine = (ScriptingEngine) getComponent(NashornApplicationComponents.NASHORN_SCRIPTING_ENGINE);
        BigDecimal result = engine.evaluate(BigDecimal.class, "100 * .25");
        assertEquals(BigDecimal.valueOf(25.0), result);
    }

    @Test
    public void testEvaluateScriptWithParams() throws Exception {
        ScriptingEngine engine = (ScriptingEngine) getComponent(NashornApplicationComponents.NASHORN_SCRIPTING_ENGINE);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("a", 100);
        params.put("b", .25);
        BigDecimal result = engine.evaluate(BigDecimal.class, "200 + a * b", params);
        assertEquals(BigDecimal.valueOf(225.0), result);
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
