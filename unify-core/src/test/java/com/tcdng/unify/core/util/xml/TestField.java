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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.util.xml.adapter.DataTypeXmlAdapter;

/**
 * Data object for tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@XmlRootElement(name = "test-field")
@XmlType(propOrder = { "name", "type" })
public class TestField {

	private String name;

	private DataType type;

	public TestField(String name, DataType type) {
		this.name = name;
		this.type = type;
	}

	public TestField() {

	}

	public String getName() {
		return name;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public DataType getType() {
		return type;
	}

	@XmlJavaTypeAdapter(DataTypeXmlAdapter.class)
	@XmlElement(name = "type")
	public void setType(DataType type) {
		this.type = type;
	}
}
