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

package com.tcdng.unify.core.data;

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Parameter generator manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ParamGeneratorManager extends UnifyComponent {

    /**
     * Gets a parameterized string generator.
     * 
     * @param paramValueStore
     *                            the parameter value store
     * @param generatorValueStore
     *                            the generator value store
     * @param tokenList
     *                            the target token list
     * @return the parameterized string generator
     * @throws UnifyException
     *                        if an error occurs
     */
    ParameterizedStringGenerator getParameterizedStringGenerator(ValueStore paramValueStore,
            ValueStore generatorValueStore, List<StringToken> tokenList) throws UnifyException;
}
