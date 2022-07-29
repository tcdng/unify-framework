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
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Parameterized string generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ParameterizedStringGenerator {

    private ValueStore paramValueStore;

    private ValueStore generatorValueStore;

    private List<StringToken> tokenList;
    
    private Map<StringToken, ParamGenerator> generators;

    public ParameterizedStringGenerator(ValueStore paramValueStore, ValueStore generatorValueStore,
            List<StringToken> tokenList, Map<StringToken, ParamGenerator> generators) {
        this.paramValueStore = paramValueStore;
        this.generatorValueStore = generatorValueStore;
        this.tokenList = tokenList;
        this.generators = generators;
    }

    public String generate() throws UnifyException {
        if (DataUtils.isBlank(tokenList)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (StringToken stringToken : tokenList) {
            Object val = getParam(stringToken);
            if (val != null) {
                sb.append(val);
            }
        }

        return sb.toString();
    }

    public int getDataIndex() {
        return paramValueStore.getDataIndex();
    }

    public ParameterizedStringGenerator setDataIndex(int dataIndex) {
        paramValueStore.setDataIndex(dataIndex);
        return this;
    }

    private Object getParam(StringToken key) throws UnifyException {
        Object val = null;
        if (key.isParam()) {
            if (key.isGenerator()) {
                ParamGenerator generator = generators.get(key);
                val = generator != null ? generator.generate(generatorValueStore.getReader(), key) : null;
            } else {
                val = paramValueStore.getTempValue(key.getToken());
                if (val == null) {
                    val = paramValueStore.retrieve(key.getToken());
                }
            }
        } else {
            val = key.getToken();
        }

        return val;
    }
}
