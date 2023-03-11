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

package com.tcdng.unify.core.script;

import java.util.Collections;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for scripting engines.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractScriptingEngine extends AbstractUnifyComponent implements ScriptingEngine {

    private ScriptEngine scriptEngine;
    
    @Override
    public <T> T evaluate(Class<T> resultType, String script) throws UnifyException {
        return evaluate(resultType, script, Collections.emptyMap());
    }

    @Override
    public <T> T evaluate(Class<T> resultType, String script, Map<String, Object> params) throws UnifyException {
        Object result = null;
        try {
            if (params == null ||  params.isEmpty()) {
                result = scriptEngine.eval(script);
            } else {
                Bindings bindings = scriptEngine.createBindings();
                for (Map.Entry<String, Object> entry: params.entrySet()) {
                    bindings.put(entry.getKey(), entry.getValue());
                }
                
                result = scriptEngine.eval(script, bindings);
            }
        } catch (ScriptException e) {
            throwOperationErrorException(e);
        }

        return DataUtils.convert(resultType, result);
    }

    @Override
    protected void onInitialize() throws UnifyException {
        scriptEngine = createScriptEngine();
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected abstract ScriptEngine createScriptEngine() throws UnifyException;
}
