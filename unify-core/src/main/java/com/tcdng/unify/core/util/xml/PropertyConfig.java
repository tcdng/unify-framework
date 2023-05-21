/*
 * Copyright 2018-2023 The Code Department.
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Property configuration.
 * 
 * @author The Code Department
 */
public class PropertyConfig {

    private String name;

    private String value;

    private List<String> valueList;

    private boolean hidden;

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

    @XmlAttribute(required = true)
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    @XmlAttribute
    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValueList() {
        return valueList;
    }

    @XmlElement(name = "value")
    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public boolean isHidden() {
        return hidden;
    }

    @XmlAttribute
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
