/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;

/**
 * An input data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Input {

	private String name;

	private String value;

	private String description;

	private String editor;

	private Class<?> type;

	private boolean mandatory;

	public Input(Class<?> type, String name, String description, String editor, boolean mandatory) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.editor = editor;
		this.mandatory = mandatory;
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public String getEditor() {
		return editor;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public <T> void setTypeValue(T value) throws UnifyException {
		this.value = DataUtils.convert(String.class, value, null);
	}

	public <T> void setTypeValue(T value, Formatter<?> formatter) throws UnifyException {
		this.value = DataUtils.convert(String.class, value, formatter);
	}

	@SuppressWarnings("unchecked")
	public <T> T getTypeValue() throws UnifyException {
		return (T) DataUtils.convert(this.type, value, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getTypeValue(Formatter<?> formatter) throws UnifyException {
		return (T) DataUtils.convert(this.type, value, formatter);
	}
}
