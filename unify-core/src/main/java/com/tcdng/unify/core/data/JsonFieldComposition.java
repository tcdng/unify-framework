/*
 * Copyright 2018-2024 The Code Department.
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

import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.DynamicEntityFieldType;

/**
 * JSON field composition.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class JsonFieldComposition {

	private DynamicEntityFieldType fieldType;

	private DataType dataType;

	private String name;

	private String jsonName;

	private String formatter;

	private JsonObjectComposition objectComposition;
	
	public JsonFieldComposition(DynamicEntityFieldType fieldType, DataType dataType, String name, String jsonName,
			String formatter) {
		this.fieldType = fieldType;
		this.dataType = dataType;
		this.name = name;
		this.jsonName = jsonName == null ? name : jsonName;
		this.formatter = formatter;
	}
	
	public JsonFieldComposition(JsonObjectComposition objectComposition, DynamicEntityFieldType fieldType,
			DataType dataType, String name, String jsonName, String formatter) {
		this.fieldType = fieldType;
		this.dataType = dataType;
		this.name = name;
		this.jsonName = jsonName == null ? name : jsonName;
		this.formatter = formatter;
		this.objectComposition = objectComposition;
	}

	public String getName() {
		return name;
	}

	public String getJsonName() {
		return jsonName;
	}

	public String getFormatter() {
		return formatter;
	}

	public boolean isString() {
		return fieldType.isTableColumn() && dataType.isString();
	}

	public boolean isInteger() {
		return fieldType.isTableColumn() && dataType.isInteger();
	}

	public boolean isDecimal() {
		return fieldType.isTableColumn() && dataType.isDecimal();
	}

	public boolean isBoolean() {
		return fieldType.isTableColumn() && dataType.isBoolean();
	}

	public boolean isDate() {
		return fieldType.isTableColumn() && dataType.isDate();
	}

	public boolean isDateTime() {
		return fieldType.isTableColumn() && dataType.isTimestamp();
	}

	public boolean isObject() {
		return fieldType.isChild();
	}

	public boolean isObjectArray() {
		return fieldType.isChildList();
	}

	public JsonObjectComposition getObjectComposition() {
		return objectComposition;
	}
}
