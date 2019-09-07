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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.task.TaskExecType;
import com.tcdng.unify.core.task.TaskSetup.Builder;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Packable document configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDocConfig {

    public enum FieldType {
        OBJECT,
        ARRAY,
        LIST
    }

    private String name;

    private Class<?> beanType;

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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Class<?> beanType) {
        return new Builder(beanType);
    }

    public static class Builder {

        private Class<?> beanType;

        private Map<String, FieldConfig> fieldConfigs;

        private Builder() {
            this(null);
        }

        private Builder(Class<?> beanType) {
            this.beanType = beanType;
            fieldConfigs = new LinkedHashMap<String, FieldConfig>();
        }

        public Builder fieldConfig(String fieldName, FieldType fieldType, DataType dataType) throws UnifyException {
            return fieldConfig(fieldName, null, fieldType, dataType);
        }

        public Builder fieldConfig(String fieldName, String beanProperty, FieldType fieldType, DataType dataType)
                throws UnifyException {
            if (fieldConfigs.containsKey(fieldName)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_FIELD_EXISTS, fieldName);
            }
            
            if (!StringUtils.isBlank(beanProperty)) {
                if (beanType == null) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_BUILDER_NOT_SUPPORT_MAPPING);
                }
                
                // Ensure bean type has appropriate setters and getters
                GetterSetterInfo gsInfo = ReflectUtils.getGetterSetterInfo(beanType, beanProperty);
                
                // Ensure type is the same
                if (FieldType.LIST.equals(fieldType)) {
                    
                } else if (FieldType.ARRAY.equals(fieldType)) {
                    
                } else {
                    
                }
            }
            return this;
        }
    }

    public static class FieldConfig {

        private String fieldName;

        private String beanProperty;

        private FieldType fieldType;

        private Class<?> dataType;

        private PackableDocConfig packableDocConfig;

        private FieldConfig(String fieldName, String beanProperty, FieldType fieldType, Class<?> dataType) {
            this.fieldName = fieldName;
            this.beanProperty = beanProperty;
            this.fieldType = fieldType;
            this.dataType = dataType;
        }

        private FieldConfig(String fieldName, String beanProperty, FieldType fieldType, Class<?> dataType,
                PackableDocConfig packableDocConfig) {
            this.fieldName = fieldName;
            this.beanProperty = beanProperty;
            this.fieldType = fieldType;
            this.dataType = dataType;
            this.packableDocConfig = packableDocConfig;
        }

        public String getFieldName() {
            return fieldName;
        }

        public FieldType getFieldType() {
            return fieldType;
        }

        public Class<?> getDataType() {
            return dataType;
        }

        public PackableDocConfig getPackableDocConfig() {
            return packableDocConfig;
        }

        public boolean isArray() {
            return FieldType.ARRAY.equals(fieldType);
        }

        public boolean isList() {
            return FieldType.LIST.equals(fieldType);
        }

        public boolean isMultiple() {
            return FieldType.ARRAY.equals(fieldType) || FieldType.LIST.equals(fieldType);
        }

        public boolean isComplex() {
            return packableDocConfig != null;
        }
    }
}
