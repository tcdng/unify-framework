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

package com.tcdng.unify.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo.ManagedType;

/**
 * Entity type utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class EntityTypeUtils {

	private EntityTypeUtils() {

	}
	
	public static boolean isReservedType(String type) {
		return EntityTypeUtils.isDynamicType(type) || EntityTypeUtils.isDelegateType(type);
	}
	
	public static boolean isDynamicType(String type) {
		return type.charAt(type.length() -1) == 'z' && type.indexOf(".z.") > 0;
	}

	public static boolean isDelegateType(String type) {
		return type.charAt(type.length() -1) == 'u' && type.indexOf(".u.") > 0;
	}
	
	public static List<DynamicEntityInfo> getDynamicEntityInfoFromJson(final String json) throws UnifyException {
		if (json != null) {
			List<DynamicEntityInfo> list = new ArrayList<DynamicEntityInfo>();
			try {
				JsonValue root = Json.parse(json);
				if (root.isArray()) {
					JsonArray array = (JsonArray) root;
					if (array.size() > 0) {
						JsonValue _root = array.get(0);
						if (_root.isObject()) {
							getDynamicEntityInfo(list, (JsonObject) _root, "root", null);
						}
					}
				} else if (root.isObject()) {
					getDynamicEntityInfo(list, (JsonObject) root, "root", null);
				}
			} catch (Exception e) {
				throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
			}

			return list;
		}

		return Collections.emptyList();
	}
	
	private static DynamicEntityInfo getDynamicEntityInfo(final List<DynamicEntityInfo> list, final JsonObject object,
			final String name, final DynamicEntityInfo _parentDynamicEntityInfo) throws Exception {
		DynamicEntityInfo.Builder deib = DynamicEntityInfo.newBuilder(name, ManagedType.MANAGED, false);
		DynamicEntityInfo _dynamicEntityInfo = deib.prefetch();
		list.add(_dynamicEntityInfo);
		
		if (_parentDynamicEntityInfo != null) {
			final String _fieldName = _parentDynamicEntityInfo.getClassName() + "Id";
			final String _columnName = SqlUtils.generateSchemaElementName(_fieldName);
			deib.addForeignKeyField(DynamicFieldType.INFO_ONLY, _parentDynamicEntityInfo, _columnName, _fieldName, null,
					false, false);
		}

		for (String fieldName : object.names()) {
			final String columnName = SqlUtils.generateSchemaElementName(fieldName);
			final String longName = name + StringUtils.capitalizeFirstLetter(fieldName);
			final JsonValue field = object.get(fieldName);
			if (field.isString()) {
				deib.addField(DynamicFieldType.INFO_ONLY, DataType.STRING, columnName, fieldName, null, null, 64, 0, 0,
						false, false);
			} else if (field.isNumber()) {
				if (field.toString().indexOf('.') >= 0) {
					deib.addField(DynamicFieldType.INFO_ONLY, DataType.DECIMAL, columnName, fieldName, null, null, 0, 0,
							2, false, false);
				} else {
					deib.addField(DynamicFieldType.INFO_ONLY, DataType.INTEGER, columnName, fieldName, null, null, 0, 0,
							0, false, false);
				}
			} else if (field.isBoolean()) {
				deib.addField(DynamicFieldType.INFO_ONLY, DataType.BOOLEAN, columnName, fieldName, null, null, 0, 0, 0,
						false, false);
			} else if (field.isObject()) {
				final DynamicEntityInfo _childDynamicEntityInfo = getDynamicEntityInfo(list, (JsonObject) field,
						longName, _dynamicEntityInfo);
				deib.addChildField(DynamicFieldType.INFO_ONLY, _childDynamicEntityInfo, longName, true);
			} else if (field.isArray()) {
				JsonArray array = (JsonArray) field;
				if (array.size() > 0) {
					JsonValue _field = array.get(0);
					if (_field.isObject()) {
						final DynamicEntityInfo _childDynamicEntityInfo = getDynamicEntityInfo(list,
								(JsonObject) _field, longName, _dynamicEntityInfo);
						deib.addChildListField(DynamicFieldType.INFO_ONLY, _childDynamicEntityInfo, longName, true);
					} else {
						// TODO
					}
				}

			}
		}

		return deib.build();
	}
}
