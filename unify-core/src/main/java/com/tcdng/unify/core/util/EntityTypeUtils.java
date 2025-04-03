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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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
		return type.charAt(type.length() - 1) == 'z' && type.indexOf(".z.") > 0;
	}

	public static boolean isDelegateType(String type) {
		return type.charAt(type.length() - 1) == 'u' && type.indexOf(".u.") > 0;
	}

	public static List<EntityTypeInfo> getEntityTypeInfoFromCsv(final String csv) throws UnifyException {
		return EntityTypeUtils.getEntityTypeInfoFromJson(null, csv);
	}

	@SuppressWarnings("deprecation")
	public static List<EntityTypeInfo> getEntityTypeInfoFromCsv(final String name, final String csv)
			throws UnifyException {
		if (csv != null) {
			List<EntityTypeInfo> list = new ArrayList<EntityTypeInfo>();
			EntityTypeInfo.Builder deib = EntityTypeInfo.newBuilder(name, 0);

			try (CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new StringReader(csv))) {
				final List<String> headerNames = csvParser.getHeaderNames();
				final CSVRecord record = csvParser.iterator().next();
				final int len = headerNames.size();
				for (int i = 0; i < len; i++) {
					final String headerName = headerNames.get(i);
					final String fieldName = StringUtils.decapitalize(StringUtils.underscore(headerName));
					final String columnName = SqlUtils.generateSchemaElementName(fieldName);

					final String val = record.get(i);
					if (val.matches("-?\\d+")) {
						deib.addFieldInfo(DataType.INTEGER, fieldName, null, columnName, val, false);
					} else if (val.matches("-?\\d*\\.\\d+")) {
						deib.addFieldInfo(DataType.DECIMAL, fieldName, null, columnName, val, false);
					} else {
						deib.addFieldInfo(DataType.STRING, fieldName, null, columnName, val, false);
					}
				}

				list.add(deib.build());
			} catch (Exception e) {
				throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
			}

			return list;
		}

		return Collections.emptyList();
	}

	public static List<EntityTypeInfo> getEntityTypeInfoFromJson(final String json) throws UnifyException {
		return EntityTypeUtils.getEntityTypeInfoFromJson(null, json);
	}

	public static List<EntityTypeInfo> getEntityTypeInfoFromJson(final String name, final String json)
			throws UnifyException {
		if (json != null) {
			List<EntityTypeInfo> list = new ArrayList<EntityTypeInfo>();
			try {
				final String _name = !StringUtils.isBlank(name) ? name : "root";
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
			deib.addForeignKeyInfo(_parentEntityTypeInfo.getName(), _fieldName, _fieldName, _columnName);
		}

		for (String jsonFieldName : object.names()) {
			final String nrmFieldName = NameUtils.inflateAsName(jsonFieldName);
			final String columnName = SqlUtils.generateSchemaElementName(nrmFieldName);
			final String longName = name + StringUtils.capitalizeFirstLetter(nrmFieldName);
			JsonValue field = object.get(jsonFieldName);
			final boolean array = field.isArray();
			if (array) {
				JsonArray _array = (JsonArray) field;
				if (_array.isEmpty()) {
					throw new IllegalArgumentException(
							"Can not resolve element type of empty array field [" + jsonFieldName + "].");
				}

				field = _array.get(0);
			}

			if (field.isString()) {
				deib.addFieldInfo(DataType.STRING, nrmFieldName, jsonFieldName, columnName, field.asString(), array);
			} else if (field.isNumber()) {
				if (field.toString().indexOf('.') >= 0) {
					deib.addFieldInfo(DataType.DECIMAL, nrmFieldName, jsonFieldName, columnName,
							field.isNull() ? null : String.valueOf(field.asDouble()), array);
				} else {
					deib.addFieldInfo(DataType.INTEGER, nrmFieldName, jsonFieldName, columnName,
							field.isNull() ? null : String.valueOf(field.asInt()), array);
				}
			} else if (field.isBoolean()) {
				deib.addFieldInfo(DataType.BOOLEAN, nrmFieldName, jsonFieldName, columnName,
						field.isNull() ? null : String.valueOf(field.asBoolean()), array);
			} else if (field.isObject()) {
				final EntityTypeInfo _childEntityInfo = getEntityInfo(list, (JsonObject) field, longName, _entityInfo,
						depth + 1);
				if (array) {
					deib.addChildListInfo(_childEntityInfo.getName(), nrmFieldName, jsonFieldName);
				} else {
					deib.addChildInfo(_childEntityInfo.getName(), nrmFieldName, jsonFieldName);
				}
			}
		}

		return deib.build();
	}

}
