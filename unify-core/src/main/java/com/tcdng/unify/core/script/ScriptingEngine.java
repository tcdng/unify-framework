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

package com.tcdng.unify.core.script;

import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Scripting engine component.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ScriptingEngine extends UnifyComponent {

    /**
     * Evaluates a script.
     * 
     * @param resultType
     *                   the result type
     * @param script
     *                   the script to evaluate
     * @return the evaluation result
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> T evaluate(Class<T> resultType, String script) throws UnifyException;

    /**
     * Evaluates a script.
     * 
     * @param resultType
     *                   the result type
     * @param script
     *                   the script to evaluate
     * @param params
     *                   script parameters
     * @return the evaluation result
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> T evaluate(Class<T> resultType, String script, Map<String, Object> params) throws UnifyException;
}
