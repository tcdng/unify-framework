/*
 * Copyright 2018-2025 The Code Department.
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
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Bean mapping configuration.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class BeanMappingConfig {

    private Class<?> beanClass;

    private Map<String, String> mappings;

    private BeanMappingConfig(Class<?> beanClass, Map<String, String> mappings) {
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

    public String getMappedField(String beanProperty) {
        return mappings.get(beanProperty);
    }

    public static Builder newBuilder(Class<?> beanClass) {
        return new Builder(beanClass);
    }

    public static class Builder {

        private Class<?> beanClass;

        private Map<String, String> mappings;

        private Builder(Class<?> beanClass) {
            this.beanClass = beanClass;
            this.mappings = new HashMap<String, String>();
        }

        public Builder addMapping(String fieldName, String beanProperty) throws UnifyException {
            // Check if mapping already exists
            if (mappings.containsKey(beanProperty)) {
                throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_BEANPROPERTY_EXISTS, beanProperty,
                        beanClass);
            }

            mappings.put(beanProperty, fieldName);
            return this;
        }
        
        public BeanMappingConfig build() {
            return new BeanMappingConfig(beanClass, mappings);
        }
    }
}