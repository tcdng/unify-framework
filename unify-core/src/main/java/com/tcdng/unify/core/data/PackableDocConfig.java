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
import java.util.List;
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

    private Map<Class<?>, BeanConfig> beanConfigs;

    private PackableDocConfig(String name, Map<String, FieldConfig> fieldConfigs,
            Map<Class<?>, BeanConfig> beanConfigs) {
        this.name = name;
        this.fieldConfigs = fieldConfigs;
        this.beanConfigs = beanConfigs;
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

    public Set<Class<?>> getBeanClasses() {
        return beanConfigs.keySet();
    }

    public BeanConfig getBeanConfig(Class<?> beanClass) throws UnifyException {
        BeanConfig bc = beanConfigs.get(beanClass);
        if (bc == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_NO_SUCH_BEANCONFIG, beanClass);
        }

        return bc;
    }

    public boolean isBeanConfig(Class<?> beanClass) {
        return beanConfigs.containsKey(beanClass);
    }

    public static Builder newBuilder(String configName) {
        return new Builder(configName);
    }

    public static class Builder {

        private String name;

        private Map<String, FieldConfig> fieldConfigs;

        private Map<Class<?>, Map<String, FieldConfig>> beanMappings;

        private Builder(String name) {
            this.name = name;
            this.fieldConfigs = new LinkedHashMap<String, FieldConfig>();
            this.beanMappings = new HashMap<Class<?>, Map<String, FieldConfig>>();
        }

        public Builder fieldConfig(String fieldName, DataType dataType) throws UnifyException {
            return fieldConfig(fieldName, dataType, false);
        }

        public Builder fieldConfig(String fieldName, DataType dataType, boolean list) throws UnifyException {
            if (fieldConfigs.containsKey(fieldName)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_FIELD_EXISTS, fieldName);
            }

            fieldConfigs.put(fieldName, new FieldConfig(fieldName, dataType.javaClass(), list));
            return this;
        }

        public Builder complexFieldConfig(String fieldName, PackableDocConfig packableDocConfig) throws UnifyException {
            return complexFieldConfig(fieldName, packableDocConfig, false);
        }

        public Builder complexFieldConfig(String fieldName, PackableDocConfig packableDocConfig, boolean list)
                throws UnifyException {
            if (fieldConfigs.containsKey(fieldName)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_FIELD_EXISTS, fieldName);
            }

            fieldConfigs.put(fieldName, new FieldConfig(fieldName, packableDocConfig, list));
            return this;
        }

        public Builder beanConfig(Class<?> beanClass, String fieldName, String beanProperty) throws UnifyException {
            FieldConfig fc = fieldConfigs.get(fieldName);
            if (fc == null) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_NO_SUCH_FIELDCONFIG, fieldName);
            }

            // Create mappings for bean class if necessary
            Map<String, FieldConfig> mappings = beanMappings.get(beanClass);
            if (mappings == null) {
                mappings = new HashMap<String, FieldConfig>();
                beanMappings.put(beanClass, mappings);
            }

            // Check if mapping already exists
            if (mappings.containsKey(beanProperty)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_BEANPROPERTY_EXISTS, beanProperty,
                        beanClass);
            }

            // Ensure bean type has appropriate setters and getters
            GetterSetterInfo gsInfo = ReflectUtils.getGetterSetterInfo(beanClass, beanProperty);

            // Ensure type is the same
            boolean isDifferentType = false;
            if (fc.isComplex()) {
                if (fc.isList()) {
                    isDifferentType = !List.class.equals(gsInfo.getType())
                            || !fc.getPackableDocConfig().isBeanConfig(gsInfo.getArgumentType());
                } else {
                    isDifferentType = !fc.getPackableDocConfig().isBeanConfig(gsInfo.getType());
                }

            } else {
                if (fc.isList()) {
                    isDifferentType =
                            !List.class.equals(gsInfo.getType()) || !fc.getDataType().equals(gsInfo.getArgumentType());
                } else {
                    isDifferentType = !fc.getDataType().equals(gsInfo.getType());
                }
            }

            if (isDifferentType) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_INCOMPATIBLE_FIELDCONFIG, beanClass,
                        beanProperty, fc.isList(), fc.getDataType());
            }

            mappings.put(beanProperty, fc);
            return this;
        }

        public PackableDocConfig build() {
            Map<Class<?>, BeanConfig> beanConfigs = null;
            if (!beanMappings.isEmpty()) {
                beanConfigs = new HashMap<Class<?>, BeanConfig>();
                for (Map.Entry<Class<?>, Map<String, FieldConfig>> entry : beanMappings.entrySet()) {
                    beanConfigs.put(entry.getKey(), new BeanConfig(entry.getKey(), entry.getValue()));
                }
            }

            return new PackableDocConfig(name, DataUtils.unmodifiableMap(fieldConfigs),
                    DataUtils.unmodifiableMap(beanConfigs));
        }
    }

    public static class BeanConfig {

        private Class<?> beanClass;

        private Map<String, FieldConfig> mappings;

        private BeanConfig(Class<?> beanClass, Map<String, FieldConfig> mappings) {
            this.beanClass = beanClass;
            this.mappings = mappings;
        }

        public Class<?> getBeanClass() {
            return beanClass;
        }

        public Set<String> getBeanProperties() {
            return mappings.keySet();
        }

        public boolean isBeanProperty(String beanProperty) {
            return mappings.containsKey(beanProperty);
        }

        public FieldConfig getFieldConfig(String beanProperty) {
            return mappings.get(beanProperty);
        }
    }

    public static class FieldConfig {

        private String fieldName;

        private Class<?> dataType;

        private PackableDocConfig packableDocConfig;

        private boolean list;

        private FieldConfig(String fieldName, Class<?> dataType, boolean list) {
            this.fieldName = fieldName;
            this.dataType = dataType;
            this.list = list;
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
