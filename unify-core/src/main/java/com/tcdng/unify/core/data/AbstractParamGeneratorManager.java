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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for parameter generator manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractParamGeneratorManager extends AbstractUnifyComponent implements ParamGeneratorManager {

    private FactoryMap<StringToken, ParamGenerator> generators;

    public AbstractParamGeneratorManager() {
        this.generators = new FactoryMap<StringToken, ParamGenerator>()
            {

                @Override
                protected ParamGenerator create(StringToken key, Object... params) throws Exception {
                    if ("g".equals(key.getGenType())) {
                        return (ParamGenerator) getComponent(key.getGenKey());
                    }

                    ParamGenerator generator = resolveParamGenerator(key);
                    if (generator == null) {
                        throwOperationErrorException(new IllegalArgumentException(
                                "Could not resolve generator for [" + key.getToken() + "]."));
                    }

                    return generator;
                }

            };
    }

    @Override
    public ParameterizedStringGenerator getParameterizedStringGenerator(ValueStore paramValueStore,
            ValueStore generatorValueStore, List<StringToken> tokenList) throws UnifyException {
        Map<StringToken, ParamGenerator> _generators = new HashMap<StringToken, ParamGenerator>();
        if (!DataUtils.isBlank(tokenList)) {
            for (StringToken token : tokenList) {
                if (token.isGenerator()) {
                    ParamGenerator _generator = generators.get(token);
                    _generators.put(token, _generator);
                }
            }
        }

        return new ParameterizedStringGenerator(paramValueStore, generatorValueStore, tokenList, _generators);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected abstract ParamGenerator resolveParamGenerator(StringToken key) throws UnifyException;
}
