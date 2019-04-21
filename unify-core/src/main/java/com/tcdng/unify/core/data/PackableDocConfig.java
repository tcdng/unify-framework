/*
 * Copyright 2018-2019 The Code Department.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.DataType;

/**
 * Packable document configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDocConfig {

    private String name;

    private Map<String, FieldConfig> fieldConfigs;

    public PackableDocConfig(String name, FieldConfig... fieldConfigs) {
        this(name, Arrays.asList(fieldConfigs));
    }

    public PackableDocConfig(String name, List<FieldConfig> fieldConfigList) {
        this.name = name;
        fieldConfigs = new HashMap<String, FieldConfig>();
        for (FieldConfig fieldConfig : fieldConfigList) {
            fieldConfigs.put(fieldConfig.getName(), fieldConfig);
        }
    }

    public String getName() {
        return name;
    }

    public FieldConfig getFieldConfig(String fieldName) throws UnifyException {
        FieldConfig fc = fieldConfigs.get(fieldName);
        if (fc == null) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_NO_SUCH_FIELD, fieldName);
        }

        return fc;
    }

    public boolean isFieldConfig(String fieldName) {
        return fieldConfigs.containsKey(fieldName);
    }

    public Set<String> getFieldNames() {
        return fieldConfigs.keySet();
    }

    public Collection<FieldConfig> getFieldConfigs() {
        return fieldConfigs.values();
    }

    public int getFieldCount() {
        return fieldConfigs.size();
    }

    public static class FieldConfig {

        private String name;

        private Class<?> type;

        private PackableDocConfig packableDocConfig;

        public FieldConfig(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public FieldConfig(String name, DataType type) {
            this.name = name;
            this.type = type.javaClass();
        }

        public FieldConfig(String name, FieldConfig... fieldConfigs) {
            this(name, Arrays.asList(fieldConfigs));
        }

        public FieldConfig(String name, List<FieldConfig> fieldConfigList) {
            this.name = name;
            this.type = DataType.COMPLEX.javaClass();
            this.packableDocConfig = new PackableDocConfig(name, fieldConfigList);
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public PackableDocConfig getPackableDocConfig() {
            return packableDocConfig;
        }

        public boolean isComplex() {
            return packableDocConfig != null;
        }
    }
}
