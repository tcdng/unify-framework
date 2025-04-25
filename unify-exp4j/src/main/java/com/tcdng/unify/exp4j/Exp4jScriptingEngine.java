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

import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.script.AbstractScriptingEngine;
import com.tcdng.unify.core.util.DataUtils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Implementation of scripting engine using exp4j library.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(Exp4jApplicationComponents.EXP4J_SCRIPTINGENGINE)
public class Exp4jScriptingEngine extends AbstractScriptingEngine {

	@Override
	public <T extends Number> T evaluate(Class<T> resultType, String script, Map<String, Number> params)
			throws UnifyException {
		final ExpressionBuilder eb = new ExpressionBuilder(script);
		Expression e = null;
		if (params != null && !params.isEmpty()) {
			String[] keys = DataUtils.toArray(String.class, params.keySet());
	        eb.variables(keys);
	        e = eb.build();
	        for (Map.Entry<String, Number> entry: params.entrySet()) {
		        e.setVariable(entry.getKey(), entry.getValue().doubleValue());
			}
		} else {
	        e = eb.build();
		}
		
		final double result = e.evaluate();
		return DataUtils.convert(resultType, result);
	}

}
