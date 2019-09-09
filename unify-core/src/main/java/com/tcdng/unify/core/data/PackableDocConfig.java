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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Packable document configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDocConfig {

    private String name;

    private Map<String, FieldConfig> fieldConfigs;

    private Map<Class<?>, BeanMappingConfig> beanMappingConfigs;

    private PackableDocConfig(String name, Map<String, FieldConfig> fieldConfigs,
            Map<Class<?>, BeanMappingConfig> beanMappingConfigs) {
        this.name = name;
        this.fieldConfigs = fieldConfigs;
        this.beanMappingConfigs = beanMappingConfigs;
    }

    public String getName() {
        return name;
    }

    public FieldConfig getFieldConfig(String fieldName) throws UnifyException {
        FieldConfig fc = fieldConfigs.get(fieldName);
        if (fc == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_NO_SUCH_FIELD, fieldName);
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

    public Set<Class<?>> getBeanMappingNames() {
        return beanMappingConfigs.keySet();
    }

    public BeanMappingConfig getBeanMapping(Class<?> beanClass) throws UnifyException {
        BeanMappingConfig bc = beanMappingConfigs.get(beanClass);
        if (bc == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_NO_SUCH_BEANCONFIG, beanClass);
        }

        return bc;
    }

    public boolean isBeanConfig(Class<?> beanClass) {
        return beanMappingConfigs.containsKey(beanClass);
    }

    public static Builder newBuilder(String configName) {
        return new Builder(configName);
    }

    public static class Builder {

        private String name;

        private Map<String, FieldConfig> fieldConfigs;

        private Map<Class<?>, BeanMappingConfig> beanMappings;

        private Builder(String name) {
            this.name = name;
            this.fieldConfigs = new LinkedHashMap<String, FieldConfig>();
            this.beanMappings = new HashMap<Class<?>, BeanMappingConfig>();
        }

        public Builder addFieldConfig(String fieldName, DataType dataType) throws UnifyException {
            return addFieldConfig(fieldName, dataType, false);
        }

        public Builder addFieldConfig(String fieldName, DataType dataType, boolean list) throws UnifyException {
            if (fieldConfigs.containsKey(fieldName)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_FIELD_EXISTS, fieldName);
            }

            fieldConfigs.put(fieldName, new FieldConfig(fieldName, dataType.javaClass(), list));
            return this;
        }

        public Builder addComplexFieldConfig(String fieldName, PackableDocConfig packableDocConfig)
                throws UnifyException {
            return addComplexFieldConfig(fieldName, packableDocConfig, false);
        }

        public Builder addComplexFieldConfig(String fieldName, PackableDocConfig packableDocConfig, boolean list)
                throws UnifyException {
            if (fieldConfigs.containsKey(fieldName)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_FIELD_EXISTS, fieldName);
            }

            fieldConfigs.put(fieldName, new FieldConfig(fieldName, packableDocConfig, list));
            return this;
        }

        public Builder addBeanConfig(BeanMappingConfig beanMappingConfig) throws UnifyException {
            Class<?> beanClass = beanMappingConfig.getBeanClass();
            if (beanMappings.containsKey(beanClass)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_BEANCONFIG_EXISTS, beanClass);
            }

            for (String beanProperty : beanMappingConfig.getBeanProperties()) {
                String fieldName = beanMappingConfig.getMappedField(beanProperty);
                FieldConfig fc = fieldConfigs.get(fieldName);
                if (fc == null) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_NO_SUCH_FIELDCONFIG, fieldName);
                }

                // Ensure bean type has appropriate setters and getters
                GetterSetterInfo gsInfo = ReflectUtils.getGetterSetterInfo(beanClass, beanProperty);

                // Ensure type is the same
                if (!fc.isComplex() && !fc.getDataType().equals(gsInfo.getType())) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_INCOMPATIBLE_FIELDCONFIG, beanClass,
                            beanProperty, fc.getDataType());
                }
            }

            beanMappings.put(beanClass, beanMappingConfig);
            return this;
        }

        public PackableDocConfig build() {
            return new PackableDocConfig(name, DataUtils.unmodifiableMap(fieldConfigs),
                    DataUtils.unmodifiableMap(beanMappings));
        }
    }

    public static class FieldConfig {

        private String fieldName;

        private Class<?> dataType;

        private PackableDocConfig packableDocConfig;

        private boolean list;

        private FieldConfig(String fieldName, Class<?> dataType) {
            this(fieldName, dataType, false);
        }

        private FieldConfig(String fieldName, Class<?> dataType, boolean list) {
            this.fieldName = fieldName;
            this.dataType = dataType;
            this.list = list;
        }

        private FieldConfig(String fieldName, PackableDocConfig packableDocConfig) {
            this(fieldName, packableDocConfig, false);
        }

        private FieldConfig(String fieldName, PackableDocConfig packableDocConfig, boolean list) {
            this.fieldName = fieldName;
            this.packableDocConfig = packableDocConfig;
            this.list = list;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Class<?> getDataType() {
            return dataType;
        }

        public PackableDocConfig getPackableDocConfig() {
            return packableDocConfig;
        }

        public boolean isList() {
            return list;
        }

        public boolean isComplex() {
            return packableDocConfig != null;
        }
    }
}
