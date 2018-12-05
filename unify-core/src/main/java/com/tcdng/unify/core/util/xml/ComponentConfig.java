/*
 * Copyright 2014 The Code Department
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Unify component configuration.
 * 
 * @author Lateef Ojulari
 */
public class ComponentConfig {

	private String name;

	private String description;

	private String className;

	private Boolean singleton;

	private PropertiesConfig propertiesConfig;

	public String getName() {
		return name;
	}

	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	@XmlAttribute
	public void setDescription(String description) {
		this.description = description;
	}

	public String getClassName() {
		return className;
	}

	@XmlAttribute(name = "class", required = true)
	public void setClassName(String className) {
		this.className = className;
	}

	public Boolean getSingleton() {
		return singleton;
	}

	@XmlAttribute
	public void setSingleton(Boolean singleton) {
		this.singleton = singleton;
	}

	public PropertiesConfig getPropertiesConfig() {
		return propertiesConfig;
	}

	@XmlElement(name = "properties")
	public void setPropertiesConfig(PropertiesConfig propertiesConfig) {
		this.propertiesConfig = propertiesConfig;
	}
}
