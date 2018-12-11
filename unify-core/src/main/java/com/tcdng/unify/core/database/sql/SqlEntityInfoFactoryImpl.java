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
package com.tcdng.unify.core.database.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ColumnOverride;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ForeignKeyOverride;
import com.tcdng.unify.core.annotation.Id;
import com.tcdng.unify.core.annotation.Index;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;
import com.tcdng.unify.core.annotation.Version;
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.data.CycleDetector;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.EntityPolicy;
import com.tcdng.unify.core.database.StaticReference;
import com.tcdng.unify.core.transform.Transformer;
import com.tcdng.unify.core.util.AnnotationUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.SqlUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of SQL entity information factory.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton(false)
@Component(ApplicationComponents.APPLICATION_SQLENTITYINFOFACTORY)
public class SqlEntityInfoFactoryImpl extends AbstractSqlEntityInfoFactory {

	@Configurable("true")
	private boolean sqlOrderColumns;

	@Configurable("false")
	private boolean sqlGenerationApplySpacing;

	private FactoryMap<Class<?>, SqlEntityInfo> sqlEntityInfoMap;

	private int tAliasCounter;

	private int rAliasCounter;

	public SqlEntityInfoFactoryImpl() {
		sqlEntityInfoMap = new FactoryMap<Class<?>, SqlEntityInfo>() {

			@SuppressWarnings("unchecked")
			@Override
			protected SqlEntityInfo create(Class<?> entityClass, Object... params) throws Exception {
				EntityCycleDetector entityCycleDetector = null;
				if (params.length == 0) {
					entityCycleDetector = new EntityCycleDetector();
				} else {
					entityCycleDetector = (EntityCycleDetector) params[0];
				}
				List<Class<?>> cycle = entityCycleDetector.detect();
				if (!cycle.isEmpty()) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_CYCLE_DETECTED, entityClass,
							cycle.toString());
				}

				if (EnumConst.class.isAssignableFrom(entityClass)) {
					String tableName = SqlUtils.generateSchemaElementName(entityClass.getSimpleName(), false);

					Map<String, SqlFieldInfo> propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
					GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(StaticReference.class, "code");
					SqlFieldInfo idFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.ID_POSITION,
							ColumnType.STRING, null, null, null, "code", "REF_CD", null, true, false, false, null,
							false, StaticReference.CODE_LENGTH, -1, -1,
							ReflectUtils.getField(StaticReference.class, "code"), getterSetterInfo.getGetter(),
							getterSetterInfo.getSetter());

					getterSetterInfo = ReflectUtils.getGetterSetterInfo(StaticReference.class, "description");
					SqlFieldInfo descFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.COLUMN_POSITION,
							ColumnType.STRING, null, null, null, "description", "REF_DESC", null, false, false, false,
							null, false, StaticReference.DESCRIPTION_LENGTH, -1, -1,
							ReflectUtils.getField(StaticReference.class, "description"), getterSetterInfo.getGetter(),
							getterSetterInfo.getSetter());

					propertyInfoMap.put(idFieldInfo.getName(), idFieldInfo);
					propertyInfoMap.put(descFieldInfo.getName(), descFieldInfo);

					String tableAlias = "R" + (++rAliasCounter);
					return new SqlEntityInfo(null, StaticReference.class, (Class<? extends EnumConst>) entityClass,
							null, tableName, tableAlias, tableName, idFieldInfo, null, propertyInfoMap, null, null,
							null, null);
				}

				ReflectUtils.assertAnnotation(entityClass, Table.class);
				Table ta = entityClass.getAnnotation(Table.class);

				String tableName = AnnotationUtils.getAnnotationString(ta.name());
				if (StringUtils.isBlank(tableName)) {
					tableName = AnnotationUtils.getAnnotationString(ta.value());
				} else if (!StringUtils.isBlank(AnnotationUtils.getAnnotationString(ta.value()))) {
					throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_BAD_ATTRIBUTE_COMBINATION, "value",
							"name", Table.class, entityClass);
				}

				if (tableName == null) {
					tableName = SqlUtils.generateSchemaElementName(entityClass.getSimpleName(),
							sqlGenerationApplySpacing);
				}
				String viewName = AnnotationUtils.getAnnotationString(ta.view());
				if (viewName == null) {
					viewName = tableName;
				}

				Map<String, ForeignKeyOverride> fkOverrideMap = new HashMap<String, ForeignKeyOverride>();
				for (ForeignKeyOverride fkoa : ta.foreignKeyOverrides()) {
					fkOverrideMap.put(fkoa.key(), fkoa);
				}

				Map<String, ColumnOverride> colOverrideMap = new HashMap<String, ColumnOverride>();
				for (ColumnOverride coa : ta.columnOverrides()) {
					colOverrideMap.put(coa.field(), coa);
				}

				// Process all fields including super class fields
				Map<String, SqlFieldInfo> propertyInfoMap = new HashMap<String, SqlFieldInfo>();
				List<ChildListFieldInfo> childListInfoList = new ArrayList<ChildListFieldInfo>();
				SqlFieldInfo idFieldInfo = null;
				SqlFieldInfo versionFieldInfo = null;
				int fkIndex = 0;
				Map<Class<?>, List<Field>> listOnlyFieldMap = new HashMap<Class<?>, List<Field>>();
				Class<?> searchClass = entityClass;
				do {
					Field[] fields = searchClass.getDeclaredFields();
					for (Field field : fields) {
						boolean isEnumConst = EnumConst.class.isAssignableFrom(field.getType());
						boolean isPersistent = false;
						boolean isPrimaryKey = false;
						boolean isForeignKey = false;
						boolean isNullable = false;
						int length = -1;
						int precision = -1;
						int scale = -1;
						int position = 0;

						String column = null;
						ColumnType columnType = ColumnType.AUTO;
						Transformer<?, ?> transformer = null;
						SqlEntityInfo foreignEntityInfo = null;
						SqlFieldInfo foreignFieldInfo = null;

						Column ca = field.getAnnotation(Column.class);
						Id ia = field.getAnnotation(Id.class);
						Version va = field.getAnnotation(Version.class);
						ListOnly loa = field.getAnnotation(ListOnly.class);
						ForeignKey fka = field.getAnnotation(ForeignKey.class);
						ChildList cla = field.getAnnotation(ChildList.class);

						// Process primary key
						if (ia != null) {
							isPersistent = true;
							isPrimaryKey = true;
							position = ia.position();

							column = AnnotationUtils.getAnnotationString(ta.idColumn());
							if (column == null) {
								column = AnnotationUtils.getAnnotationString(ia.name());
							}
							if (column == null) {
								column = tableName + "_ID";
							}
							if (idFieldInfo != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_ID_ANNOTATION,
										searchClass, field);
							}

							if (va != null || ca != null || loa != null || fka != null || cla != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							length = ia.length();
						}

						// Process version number
						if (va != null) {
							isPersistent = true;
							position = va.position();

							column = AnnotationUtils.getAnnotationString(ta.versionColumn());
							if (column == null) {
								column = AnnotationUtils.getAnnotationString(va.name());
							}
							if (column == null) {
								column = "VERSION_NO";
							}
							if (versionFieldInfo != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_VERSION_ANNOTATION,
										searchClass, field);
							}

							if (ca != null || loa != null || fka != null || cla != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}
						}

						// Process column
						if (ca != null) {
							isPersistent = true;
							position = ca.position();

							if (loa != null || fka != null || cla != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							ColumnOverride coa = colOverrideMap.get(field.getName());
							if (coa != null) {
								if (!StringUtils.isBlank(AnnotationUtils.getAnnotationString(coa.name()))) {
									column = coa.name();
								}
								columnType = coa.type();

								String transformerName = AnnotationUtils.getAnnotationString(coa.transformer());
								if (transformerName != null) {
									transformer = (Transformer<?, ?>) getComponent(transformerName);
								}

								isNullable = coa.nullable();
								length = coa.length();
								precision = coa.precision();
								scale = coa.scale();
							} else {
								if (!StringUtils.isBlank(AnnotationUtils.getAnnotationString(ca.name()))) {
									column = ca.name();
								}
								columnType = ca.type(); // Overrides default
														// AUTO
														// type

								String transformerName = AnnotationUtils.getAnnotationString(ca.transformer());
								if (transformerName != null) {
									transformer = (Transformer<?, ?>) getComponent(transformerName);
								}

								isNullable = ca.nullable();
								length = ca.length();
								precision = ca.precision();
								scale = ca.scale();
							}
						}

						// Process foreign key
						if (fka != null) {
							position = fka.position();

							if (loa != null || cla != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							isPersistent = true;
							isForeignKey = true;
							String fkName = null;
							boolean onDeleteCascade = false;

							ForeignKeyOverride fkoa = fkOverrideMap.get(field.getName());
							if (fkoa != null) {
								fkName = fkoa.name();
								isNullable = fkoa.nullable();
								entityCycleDetector.addReference(entityClass, fkoa.foreignType());
								foreignEntityInfo = get(fkoa.foreignType(), entityCycleDetector);
								onDeleteCascade = fkoa.onDeleteCascade();
							} else {
								fkName = fka.name();
								isNullable = fka.nullable();
								Class<?> foreignType = fka.value();
								if (foreignType.equals(Entity.class)) {
									foreignType = fka.type();
								} else if (!fka.type().equals(Entity.class)) {
									throw new UnifyException(
											UnifyCoreErrorConstants.ANNOTATION_BAD_ATTRIBUTE_COMBINATION, "value",
											"foreignType", fka.getClass(), field);
								}

								if (isEnumConst) {
									if (!fka.type().equals(Entity.class)) {
										throw new UnifyException(
												UnifyCoreErrorConstants.ANNOTATION_FOREIGN_TYPE_NOT_PERMITTED,
												field.getType(), fka.getClass(), field);
									}

									foreignType = field.getType();
								}

								if (foreignType.equals(Entity.class)) {
									throw new UnifyException(
											UnifyCoreErrorConstants.ANNOTATION_MUST_SPECIFY_ATTRIBUTE_OF_TWO, "value",
											"foreignType", fka.getClass(), field);
								}

								entityCycleDetector.addReference(entityClass, foreignType);
								foreignEntityInfo = get(foreignType, entityCycleDetector);
								onDeleteCascade = fka.onDeleteCascade();
							}

							foreignFieldInfo = foreignEntityInfo.getIdFieldInfo();
							length = foreignFieldInfo.getLength();
							precision = foreignFieldInfo.getPrecision();
							scale = foreignFieldInfo.getScale();

							// Set column name
							column = AnnotationUtils.getAnnotationString(fkName);
							if (!isEnumConst && !field.getType().equals(foreignFieldInfo.getField().getType())) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_FOREIGNKEY_TYPE_MUST_MATCH_ID,
										searchClass, field, foreignFieldInfo.getField());
							}

							// Add on delete info to parent entity class
							if (onDeleteCascade) {
								foreignEntityInfo.expandOnDeleteCascade(
										new OnDeleteCascadeInfo((Class<? extends Entity>) entityClass, field));
							}
						}

						// Save list-only fields. Would be processed later since
						// they depend on foreign key fields
						if (loa != null) {
							if (cla != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							List<Field> fieldList = listOnlyFieldMap.get(searchClass);
							if (fieldList == null) {
								fieldList = new ArrayList<Field>();
								listOnlyFieldMap.put(searchClass, fieldList);
							}
							fieldList.add(field);
						}

						// Process child list
						if (cla != null) {
							Class<?> listType = field.getType();
							if (!List.class.isAssignableFrom(listType)) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_CHILDLIST_FIELD_TYPE,
										searchClass, field);
							}

							Class<?> argumentType = ReflectUtils.getArgumentType(field.getGenericType(), 0);
							Field attrFkField = getAttributeOnlyForeignKeyField(entityClass, argumentType);
							if (attrFkField == null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_CHILDLIST_HO_MATCHING_FK, field,
										argumentType);
							}

							GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(searchClass,
									field.getName());
							GetterSetterInfo attrFkGetterSetterInfo = ReflectUtils.getGetterSetterInfo(argumentType,
									attrFkField.getName());
							ChildListFieldInfo attrListFieldInfo = new ChildListFieldInfo(
									(Class<? extends Entity>) argumentType, attrFkField,
									attrFkGetterSetterInfo.getSetter(), field, getterSetterInfo.getGetter(),
									getterSetterInfo.getSetter());
							childListInfoList.add(attrListFieldInfo);
						}

						if (isPersistent) {
							if (ColumnType.AUTO.equals(columnType)) {
								columnType = DataUtils.getColumnType(field.getType());
							}

							if (StringUtils.isBlank(column)) {
								column = SqlUtils.generateSchemaElementName(field.getName(), true);
							}

							String constraintName = null;
							if (isForeignKey) {
								constraintName = SqlUtils.generateForeignKeyConstraintName(tableName, ++fkIndex);
							}

							boolean isIgnoreFkConstraint = false;
							GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(searchClass,
									field.getName());
							SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(position, columnType, foreignEntityInfo,
									foreignFieldInfo, null, field.getName(), column, constraintName, isPrimaryKey,
									isForeignKey, isIgnoreFkConstraint, transformer, isNullable, length, precision,
									scale, field, getterSetterInfo.getGetter(), getterSetterInfo.getSetter());

							if (ia != null) {
								idFieldInfo = sqlFieldInfo;
							}

							if (va != null) {
								versionFieldInfo = sqlFieldInfo;
							}

							propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
						}
					}
				} while ((searchClass = searchClass.getSuperclass()) != null);

				// Rename view if necessary
				if (!listOnlyFieldMap.isEmpty() && viewName.equals(tableName)) {
					viewName = VIEW_PREFIX + tableName;
				}

				// Process list-only fields
				for (Map.Entry<Class<?>, List<Field>> entry : listOnlyFieldMap.entrySet()) {
					for (Field field : entry.getValue()) {
						ListOnly loa = field.getAnnotation(ListOnly.class);
						SqlFieldInfo foreignKeySQLFieldInfo = propertyInfoMap.get(loa.key());
						if (foreignKeySQLFieldInfo == null) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_UNKNOWN_FOREIGN_KEY, entry.getKey(),
									field, loa.key());
						}

						if (!foreignKeySQLFieldInfo.isForeignKey()) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_LISTONLY_KEY_NOT_REF_FOREIGN_KEY,
									entry.getKey(), field, loa.key());
						}

						SqlEntityInfo foreignEntityInfo = foreignKeySQLFieldInfo.getForeignEntityInfo();
						SqlFieldInfo foreignFieldInfo = foreignEntityInfo.getListFieldInfo(loa.property());

						// Make sure field type is the same with foreign field
						// type
						if (!field.getType().equals(foreignFieldInfo.getField().getType())) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_FOREIGNKEY_TYPE_MUST_MATCH_ID,
									entry.getKey(), field, foreignFieldInfo.getField());
						}

						// Set column name
						String column = AnnotationUtils.getAnnotationString(loa.name());
						if (StringUtils.isBlank(column)) {
							column = SqlUtils.generateSchemaElementName(field.getName(), true);
						}

						GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(entry.getKey(),
								field.getName());
						SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.LIST_POSITION,
								foreignFieldInfo.getColumnType(), foreignEntityInfo, foreignFieldInfo,
								foreignKeySQLFieldInfo, field.getName(), column, null, false, false, false,
								foreignFieldInfo.getTransformer(), foreignFieldInfo.isNullable(),
								foreignFieldInfo.getLength(), foreignFieldInfo.getPrecision(),
								foreignFieldInfo.getScale(), field, getterSetterInfo.getGetter(),
								getterSetterInfo.getSetter());

						propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
					}
				}

				// Entity must have an ID property
				if (idFieldInfo == null) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_REQUIRES_ID, entityClass);
				}

				// Version number property must be integer type if entity
				// has one
				if (versionFieldInfo != null) {
					Class<?> type = versionFieldInfo.getFieldClass();
					if (!SqlUtils.isVersionNumberType(type)) {
						throw new UnifyException(UnifyCoreErrorConstants.RECORD_VERSION_NOT_INTEGER, entityClass);
					}
				}

				EntityPolicy entityPolicy = null;
				Policy pa = entityClass.getAnnotation(Policy.class);
				if (pa != null) {
					entityPolicy = (EntityPolicy) getComponent(pa.value());
				}

				// Unique constraints
				Map<String, SqlUniqueConstraintInfo> uniqueConstraintMap = null;
				if (ta.uniqueConstraints().length > 0) {
					int index = 0;
					uniqueConstraintMap = new LinkedHashMap<String, SqlUniqueConstraintInfo>();
					for (UniqueConstraint uca : ta.uniqueConstraints()) {
						String name = SqlUtils.generateUniqueConstraintName(tableName, ++index);
						String[] fieldNames = uca.value();
						if (fieldNames.length == 0) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_PROPERTY_REQUIRED_UNIQUECONSTRAINT,
									entityClass, name);
						}

						for (String fieldName : fieldNames) {
							SqlFieldInfo sqlFieldInfo = propertyInfoMap.get(fieldName);
							if (sqlFieldInfo == null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_NO_PROPERTY_FOR_UNIQUECONSTRAINT, entityClass,
										fieldName);
							}

							if (sqlFieldInfo.isListOnly()) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_LISTONLY_PROPERTY_FOR_UNIQUECONSTRAINT,
										entityClass, fieldName);
							}
						}

						uniqueConstraintMap.put(name, new SqlUniqueConstraintInfo(name, Arrays.asList(fieldNames)));
					}
				}

				// Indexes
				Map<String, SqlIndexInfo> indexMap = null;
				if (ta.indexes().length > 0) {
					int index = 0;
					indexMap = new LinkedHashMap<String, SqlIndexInfo>();
					for (Index idxa : ta.indexes()) {
						String name = SqlUtils.generateIndexName(tableName, ++index);
						String[] fieldNames = idxa.value();
						if (fieldNames.length == 0) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_PROPERTY_REQUIRED_INDEX,
									entityClass, name);
						}

						for (String fieldName : fieldNames) {
							SqlFieldInfo sqlFieldInfo = propertyInfoMap.get(fieldName);
							if (sqlFieldInfo == null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_NO_PROPERTY_FOR_INDEX,
										entityClass, fieldName);
							}

							if (sqlFieldInfo.isListOnly()) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_LISTONLY_PROPERTY_FOR_INDEX,
										entityClass, fieldName);
							}
						}

						indexMap.put(name, new SqlIndexInfo(name, Arrays.asList(fieldNames), idxa.unique()));
					}

					if (indexMap.isEmpty()) {
						indexMap = null;
					}
				}

				if (sqlOrderColumns) {
					List<SqlFieldInfo> tempList = new ArrayList<SqlFieldInfo>(propertyInfoMap.values());
					DataUtils.sort(tempList, SqlFieldInfo.class, "column", true);
					DataUtils.sort(tempList, SqlFieldInfo.class, "orderIndex", true);

					propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
					for (SqlFieldInfo sqlFieldInfo : tempList) {
						propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
					}
				}

				String tableAlias = "T" + (++tAliasCounter);
				SqlEntityInfo sqlEntityInfo = new SqlEntityInfo(null, (Class<? extends Entity>) entityClass, null,
						entityPolicy, tableName, tableAlias, viewName, idFieldInfo, versionFieldInfo, propertyInfoMap,
						childListInfoList, uniqueConstraintMap, indexMap, null);
				return sqlEntityInfo;
			}

		};
	}

	@Override
	public SqlEntityInfo getSqlEntityInfo(Class<?> entityClass) throws UnifyException {
		return sqlEntityInfoMap.get(entityClass);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private Field getAttributeOnlyForeignKeyField(Class<?> entityClass, Class<?> argumentType) throws UnifyException {
		Class<?> searchClass = argumentType;
		do {
			Field[] fields = searchClass.getDeclaredFields();
			for (Field fld : fields) {
				ForeignKey fka = fld.getAnnotation(ForeignKey.class);
				if (fka != null && fka.childKey()) {
					if (entityClass.equals(fka.value()) || entityClass.equals(fka.type())) {
						return fld;
					}
				}
			}
		} while ((searchClass = searchClass.getSuperclass()) != null);

		return null;
	}

	private class EntityCycleDetector extends CycleDetector<Class<?>> {

	}
}
