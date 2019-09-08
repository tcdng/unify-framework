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

    private Map<String, FieldConfig> beanPropertyConfigs;

    private PackableDocConfig(String name, Class<?> beanType, Map<String, FieldConfig> fieldConfigs,
            Map<String, FieldConfig> beanPropertyConfigs) {
        this.name = name;
        this.beanType = beanType;
        this.fieldConfigs = fieldConfigs;
        this.beanPropertyConfigs = beanPropertyConfigs;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanType() {
        return beanType;
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

    public FieldConfig getBeanPropertyConfig(String beanProperty) throws UnifyException {
        FieldConfig fc = beanPropertyConfigs.get(beanProperty);
        if (fc == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_NO_SUCH_BEANPROPERTY, beanProperty);
        }

        return fc;
    }

    public boolean isBeanPropertyConfig(String beanProperty) {
        return beanPropertyConfigs.containsKey(beanProperty);
    }

    public Set<String> getBeanProperties() {
        return beanPropertyConfigs.keySet();
    }

    public Collection<FieldConfig> getBeanPropertyConfigs() {
        return beanPropertyConfigs.values();
    }

    public int getBeanPropertyCount() {
        return beanPropertyConfigs.size();
    }

    public static Builder newBuilder(String configName) {
        return new Builder(configName);
    }

    public static Builder newBuilder(String configName, Class<?> beanType) {
        return new Builder(configName, beanType);
    }

    public static class Builder {

        private String name;

        private Class<?> beanType;

        private Map<String, FieldConfig> fieldConfigs;
        
        private Map<String, FieldConfig> beanPropertyConfigs;

        private Builder(String name) {
            this(name, null);
        }

        private Builder(String name, Class<?> beanType) {
            this.name = name;
            this.beanType = beanType;
            fieldConfigs = new LinkedHashMap<String, FieldConfig>();
            if (beanType != null) {
                beanPropertyConfigs = new LinkedHashMap<String, FieldConfig>();
            }
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

                if (beanPropertyConfigs.containsKey(beanProperty)) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_BEANPROPERTY_EXISTS, beanProperty);
                }
                
                // Ensure bean type has appropriate setters and getters
                GetterSetterInfo gsInfo = ReflectUtils.getGetterSetterInfo(beanType, beanProperty);

                // Ensure type is the same
                boolean isDifferentType = false;
                if (FieldType.LIST.equals(fieldType)) {
                    isDifferentType = !List.class.equals(gsInfo.getType())
                            || !dataType.javaClass().equals(gsInfo.getArgumentType());
                } else if (FieldType.ARRAY.equals(fieldType)) {
                    isDifferentType = !gsInfo.getType().isArray()
                            || !dataType.javaClass().equals(gsInfo.getType().getComponentType());
                } else {
                    isDifferentType = !dataType.javaClass().equals(gsInfo.getType());
                }

                if (isDifferentType) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_INCOMPATIBLE_FIELDCONFIG, beanType,
                            beanProperty, fieldType, dataType.javaClass());
                }
            }

            fieldConfigs.put(fieldName, new FieldConfig(fieldName, beanProperty, fieldType, dataType.javaClass()));
            return this;
        }

        public Builder complexFieldConfig(String fieldName, FieldType fieldType, PackableDocConfig packableDocConfig)
                throws UnifyException {
            return complexFieldConfig(fieldName, null, fieldType, packableDocConfig);
        }

        public Builder complexFieldConfig(String fieldName, String beanProperty, FieldType fieldType,
                PackableDocConfig packableDocConfig) throws UnifyException {
            if (fieldConfigs.containsKey(fieldName)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_FIELD_EXISTS, fieldName);
            }

            if (!StringUtils.isBlank(beanProperty)) {
                if (beanType == null) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_BUILDER_NOT_SUPPORT_MAPPING);
                }

                if (beanPropertyConfigs.containsKey(beanProperty)) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_BEANPROPERTY_EXISTS, beanProperty);
                }

                // Ensure bean type has appropriate setters and getters
                GetterSetterInfo gsInfo = ReflectUtils.getGetterSetterInfo(beanType, beanProperty);

                // Ensure type is the same
                Class<?> configBeanType = packableDocConfig.getBeanType();
                boolean isDifferentType = configBeanType == null;
                if (!isDifferentType) {
                    if (FieldType.LIST.equals(fieldType)) {
                        isDifferentType = !List.class.equals(gsInfo.getType())
                                || !configBeanType.isAssignableFrom(gsInfo.getArgumentType());
                    } else if (FieldType.ARRAY.equals(fieldType)) {
                        isDifferentType = !gsInfo.getType().isArray()
                                || !configBeanType.isAssignableFrom(gsInfo.getType().getComponentType());
                    } else {
                        isDifferentType = !configBeanType.isAssignableFrom(gsInfo.getType());
                    }
                }

                if (isDifferentType) {
                    throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_INCOMPATIBLE_COMPLEXFIELDCONFIG,
                            beanType, beanProperty, fieldType, configBeanType);
                }
            }

            fieldConfigs.put(fieldName, new FieldConfig(fieldName, beanProperty, fieldType, packableDocConfig));
            return this;
        }
        
        public PackableDocConfig build() {
            return new PackableDocConfig(name, beanType, DataUtils.unmodifiableMap(fieldConfigs),
                    DataUtils.unmodifiableMap(beanPropertyConfigs));
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

        private FieldConfig(String fieldName, String beanProperty, FieldType fieldType,
                PackableDocConfig packableDocConfig) {
            this.fieldName = fieldName;
            this.beanProperty = beanProperty;
            this.fieldType = fieldType;
            this.packableDocConfig = packableDocConfig;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getBeanProperty() {
            return beanProperty;
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
