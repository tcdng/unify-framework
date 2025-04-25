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
package com.tcdng.unify.core.util.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Properties configuration.
 * 
 * @author The Code Department
 * @since 4.1
 */
@JsonInclude(Include.NON_NULL)
public class PropertiesConfig {

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "property")
    private List<PropertyConfig> propertyConfigList;

	@JsonIgnore
    private Map<String, PropertyConfig> propertyConfigMap;

    public List<PropertyConfig> getPropertyConfigList() {
        return propertyConfigList;
    }

    public void setPropertyConfigList(List<PropertyConfig> propertyConfigList) {
        this.propertyConfigList = propertyConfigList;
        if (this.propertyConfigList != null) {
            this.propertyConfigMap = new HashMap<String, PropertyConfig>();
            for (PropertyConfig propertyConfig : propertyConfigList) {
                this.propertyConfigMap.put(propertyConfig.getName(), propertyConfig);
            }
        } else {
            this.propertyConfigMap = null;
        }
    }

    public PropertyConfig getPropertyConfig(String name) {
        if (propertyConfigMap != null) {
            propertyConfigMap.get(name);
        }
        return null;
    }
}
