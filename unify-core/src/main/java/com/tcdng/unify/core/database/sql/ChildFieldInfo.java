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
package com.tcdng.unify.core.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.constant.ChildFetch;

/**
 * Child field information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ChildFieldInfo extends OnDeleteCascadeInfo {

	private Method childFkIdSetter;

	private Method childFkTypeSetter;

	private Method childCatSetter;

	private Field field;

	private Method getter;

	private Method setter;

	private String category;

	private boolean editable;

	private boolean list;

	private boolean idNumber;

	public ChildFieldInfo(Class<? extends Entity> childEntityClass, String category, Field childFkIdField,
			Method childFkIdSetter, Field childFkTypeField, Method childFkTypeSetter, Field childCatField,
			Method childCatSetter, Field field, Method getter, Method setter, boolean editable, boolean list,
			boolean idNumber) {
		super(childEntityClass, childFkIdField, childFkTypeField, childCatField);
		this.category = category;
		this.childFkIdSetter = childFkIdSetter;
		this.childFkTypeSetter = childFkTypeSetter;
		this.childCatSetter = childCatSetter;
		this.field = field;
		this.getter = getter;
		this.setter = setter;
		this.editable = editable;
		this.list = list;
		this.idNumber = idNumber;
	}

	public String getName() {
		return field.getName();
	}

	public Method getChildFkIdSetter() {
		return childFkIdSetter;
	}

	public Method getChildFkTypeSetter() {
		return childFkTypeSetter;
	}

	public Method getChildCatSetter() {
		return childCatSetter;
	}

	public boolean isWithChildFkTypeSetter() {
		return childFkTypeSetter != null;
	}

	public boolean isWithChildCatSetter() {
		return childCatSetter != null;
	}

	public Field getField() {
		return field;
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	@Override
	public String getCategory() {
		return category;
	}

	public boolean isList() {
		return list;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isIdNumber() {
		return idNumber;
	}

	public boolean qualifies(ChildFetch fetch) {
		return fetch.isAll() ? true : (editable ? fetch.isEditableOnly() : fetch.isReadOnly());
	}
}
