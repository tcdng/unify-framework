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
import com.tcdng.unify.core.constant.DataType;

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
	
	public static List<EntityTypeInfo> getEntityTypeInfoFromJson(final String json) throws UnifyException {
		return EntityTypeUtils.getEntityTypeInfoFromJson(null, json);
	}
	
	public static List<EntityTypeInfo> getEntityTypeInfoFromJson(final String name, final String json) throws UnifyException {
		if (json != null) {
			List<EntityTypeInfo> list = new ArrayList<EntityTypeInfo>();
			try {
				final String _name = !StringUtils.isBlank(name) ? name: "root";
				JsonValue root = Json.parse(json);
				if (root.isArray()) {
					JsonArray array = (JsonArray) root;
					if (array.size() > 0) {
						JsonValue _root = array.get(0);
						if (_root.isObject()) {
							getEntityInfo(list, (JsonObject) _root, _name, null, 0);
						}
					}
				} else if (root.isObject()) {
					getEntityInfo(list, (JsonObject) root, _name, null, 0);
				}
			} catch (Exception e) {
				throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
			}

			return list;
		}

		return Collections.emptyList();
	}
	
	private static EntityTypeInfo getEntityInfo(final List<EntityTypeInfo> list, final JsonObject object,
			final String name, final EntityTypeInfo _parentEntityTypeInfo, final int depth) throws Exception {
		EntityTypeInfo.Builder deib = EntityTypeInfo.newBuilder(name, depth);
		EntityTypeInfo _entityInfo = deib.prefetch();
		list.add(_entityInfo);

		if (_parentEntityTypeInfo != null) {
			final String _fieldName = _parentEntityTypeInfo.getName() + "Id";
			final String _columnName = SqlUtils.generateSchemaElementName(_fieldName);
			deib.addForeignKeyInfo(_parentEntityTypeInfo.getName(), _fieldName, _columnName);
		}

		for (String fieldName : object.names()) {
			final String columnName = SqlUtils.generateSchemaElementName(fieldName);
			final String longName = name + StringUtils.capitalizeFirstLetter(fieldName);
			final JsonValue field = object.get(fieldName);
			if (field.isString()) {
				deib.addFieldInfo(DataType.STRING, fieldName, columnName, field.asString());
			} else if (field.isNumber()) {
				if (field.toString().indexOf('.') >= 0) {
					deib.addFieldInfo(DataType.DECIMAL, fieldName, columnName, field.isNull() ?  null: String.valueOf(field.asDouble()));
				} else {
					deib.addFieldInfo(DataType.INTEGER, fieldName, columnName, field.isNull() ?  null: String.valueOf(field.asInt()));
				}
			} else if (field.isBoolean()) {
				deib.addFieldInfo(DataType.BOOLEAN, fieldName, columnName, field.isNull() ?  null: String.valueOf(field.asBoolean()));
			} else if (field.isObject()) {
				final EntityTypeInfo _childEntityInfo = getEntityInfo(list, (JsonObject) field, longName, _entityInfo, depth + 1);
				deib.addChildInfo(_childEntityInfo.getName(), fieldName);
			} else if (field.isArray()) {
				JsonArray array = (JsonArray) field;
				if (array.size() > 0) {
					JsonValue _field = array.get(0);
					if (_field.isObject()) {
						final EntityTypeInfo _childEntityInfo = getEntityInfo(list, (JsonObject) _field, longName,
								_entityInfo, depth + 1);
						deib.addChildListInfo(_childEntityInfo.getName(), fieldName);
					} else {
						// TODO
					}
				}
			}
		}

		return deib.build();
	}
	
}
