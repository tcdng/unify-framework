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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Property configuration.
 * 
 * @author The Code Department
 */
@JsonInclude(Include.NON_NULL)
public class PropertyConfig {

	@JacksonXmlProperty(isAttribute = true)
    private String name;

	@JacksonXmlProperty(isAttribute = true)
    private String value;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "valueItem")
    private List<String> valueList;

	@JacksonXmlProperty(isAttribute = true)
    private Boolean hidden;

    public PropertyConfig(String name, String value) {
    	this.name = name;
    	this.value = value;
    }

    public PropertyConfig(String name, List<String> valueList) {
    	this.name = name;
    	this.valueList = valueList;
    }

    public PropertyConfig() {

    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

}
