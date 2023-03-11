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
package com.tcdng.unify.core.database.sql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Callable;
import com.tcdng.unify.core.annotation.CallableDataType;
import com.tcdng.unify.core.annotation.CallableResult;
import com.tcdng.unify.core.annotation.CategoryColumn;
import com.tcdng.unify.core.annotation.Child;
import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ColumnOverride;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ForeignKeyOverride;
import com.tcdng.unify.core.annotation.FosterParentId;
import com.tcdng.unify.core.annotation.FosterParentType;
import com.tcdng.unify.core.annotation.Id;
import com.tcdng.unify.core.annotation.InOutParam;
import com.tcdng.unify.core.annotation.InParam;
import com.tcdng.unify.core.annotation.Index;
import com.tcdng.unify.core.annotation.Indexes;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Mapped;
import com.tcdng.unify.core.annotation.OutParam;
import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.annotation.ResultField;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.TableExt;
import com.tcdng.unify.core.annotation.TableName;
import com.tcdng.unify.core.annotation.TableRef;
import com.tcdng.unify.core.annotation.TenantId;
import com.tcdng.unify.core.annotation.UniqueConstraint;
import com.tcdng.unify.core.annotation.UniqueConstraints;
import com.tcdng.unify.core.annotation.Version;
import com.tcdng.unify.core.annotation.View;
import com.tcdng.unify.core.annotation.ViewRestriction;
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.data.CycleDetector;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.CallableProc;
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
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_SQLENTITYINFOFACTORY)
public class SqlEntityInfoFactoryImpl extends AbstractSqlEntityInfoFactory {

	private static final String ENUM_TABLE_PREFIX = "RF";

	@Configurable("true")
	private boolean sqlOrderColumns;

	@Configurable("false")
	private boolean sqlGenerationApplySpacing;

	private SqlDataSourceDialect sqlDataSourceDialect;

	private FactoryMap<Class<?>, SqlEntityInfo> sqlEntityInfoMap;

	private FactoryMap<Class<? extends CallableProc>, SqlCallableInfo> sqlCallableInfoMap;

	private int tAliasCounter;

	private int rAliasCounter;

	public SqlEntityInfoFactoryImpl() {
		sqlEntityInfoMap = new FactoryMap<Class<?>, SqlEntityInfo>() {

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

				// Enumerations
				if (EnumConst.class.isAssignableFrom(entityClass)) {
					return createEnumConstEntityInfo(entityClass);
				}

				TableName tn = entityClass.getAnnotation(TableName.class);
				Table ta = entityClass.getAnnotation(Table.class);
				View va = entityClass.getAnnotation(View.class);
				TableExt tae = entityClass.getAnnotation(TableExt.class);
				if ((ta != null && va != null) || (ta != null && tae != null) || (va != null && tae != null)) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_TABLE_VIEW_ANNOTATION_COMBO,
							entityClass);
				}

				if (tn == null && ta == null && va == null && tae == null) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_NO_TABLE_OR_VIEW_ANNOTATION, entityClass);
				}

				// Tables
				if (ta != null || tn != null) {
					return createTableEntityInfo(entityClass, entityCycleDetector);
				}

				// Table extensions
				if (tae != null) {
					return createTableExtensionEntityInfo(entityClass, entityCycleDetector);
				}

				// Views
				return createViewEntityInfo(entityClass, entityCycleDetector);
			}

			@SuppressWarnings("unchecked")
			private SqlEntityInfo createEnumConstEntityInfo(Class<?> entityClass) throws Exception {
				logDebug("Creating enumeration constant entity information for [{0}]...", entityClass);
				String tableName = null;
				Table ta = entityClass.getAnnotation(Table.class);
				if (ta != null) {
					tableName = AnnotationUtils.getAnnotationString(ta.value());
					if (StringUtils.isBlank(tableName)) {
						tableName = AnnotationUtils.getAnnotationString(ta.name());
					}
				}

				if (StringUtils.isBlank(tableName)) {
					tableName = ENUM_TABLE_PREFIX
							+ SqlUtils.generateSchemaElementName(entityClass.getSimpleName(), false);
				}

				final String preferredTableName = sqlDataSourceDialect.getPreferredName(tableName);
				final String schema = (String) getComponentConfig(NameSqlDataSourceSchema.class,
						ApplicationComponents.APPLICATION_DATASOURCE).getSettings().getSettingValue("appSchema");
				final String schemaTableName = SqlUtils.generateFullSchemaElementName(schema, preferredTableName);

				SqlFieldDimensions sqlFieldDimensions = new SqlFieldDimensions(StaticReference.CODE_LENGTH, -1, -1);
				Map<String, SqlFieldInfo> propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
				GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(StaticReference.class, "code");
				final boolean isForeignKey = false;
				final boolean isIgnoreFkConstraint = false;
				final boolean isListOnly = false;
				final boolean isNullable = false;
				final boolean isFosterParentType = false;
				final boolean isFosterParentId = false;
				final boolean isCategoryColumn = false;
				final boolean isTenantId = false;
				final String mapped = null;
				SqlFieldInfo idFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.ID_POSITION,
						ColumnType.STRING, null, null, null, "code", "REF_CD",
						sqlDataSourceDialect.getPreferredName("REF_CD"), null, null, true, isForeignKey, isListOnly,
						isIgnoreFkConstraint, null, sqlFieldDimensions, isNullable, isFosterParentType,
						isFosterParentId, isCategoryColumn, isTenantId, mapped, null,
						ReflectUtils.getField(StaticReference.class, "code"), getterSetterInfo.getGetter(),
						getterSetterInfo.getSetter(), sqlDataSourceDialect.isAllObjectsInLowerCase());

				sqlFieldDimensions = new SqlFieldDimensions(StaticReference.DESCRIPTION_LENGTH, -1, -1);
				getterSetterInfo = ReflectUtils.getGetterSetterInfo(StaticReference.class, "description");
				SqlFieldInfo descFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.COLUMN_POSITION,
						ColumnType.STRING, null, null, null, "description", "REF_DESC",
						sqlDataSourceDialect.getPreferredName("REF_DESC"), null, null, false, isForeignKey, isListOnly,
						isIgnoreFkConstraint, null, sqlFieldDimensions, isNullable, isFosterParentType,
						isFosterParentId, isCategoryColumn, isTenantId, mapped, null,
						ReflectUtils.getField(StaticReference.class, "description"), getterSetterInfo.getGetter(),
						getterSetterInfo.getSetter(), sqlDataSourceDialect.isAllObjectsInLowerCase());

				propertyInfoMap.put(idFieldInfo.getName(), idFieldInfo);
				propertyInfoMap.put(descFieldInfo.getName(), descFieldInfo);

				String tableAlias = "R" + (++rAliasCounter);
				if (sqlDataSourceDialect.isAllObjectsInLowerCase()) {
					tableName = tableName.toLowerCase();
				}
				return new SqlEntityInfo(null, StaticReference.class, (Class<? extends EnumConst>) entityClass, null,
						schema, tableName, preferredTableName, schemaTableName, tableAlias, tableName,
						preferredTableName, schemaTableName, idFieldInfo, null, null, null, null, null, propertyInfoMap,
						null, null, null, null, null, null, null, sqlDataSourceDialect.isAllObjectsInLowerCase(), true);
			}

			@SuppressWarnings("unchecked")
			private SqlEntityInfo createTableEntityInfo(Class<?> entityClass, EntityCycleDetector entityCycleDetector)
					throws Exception {
				logDebug("Creating table entity information for [{0}]...", entityClass);
				TableName tn = entityClass.getAnnotation(TableName.class);
				Table ta = entityClass.getAnnotation(Table.class);
				String tableName = tn != null
						? (AnnotationUtils.getAnnotationString(tn.name()) != null ? tn.name() : tn.value())
						: AnnotationUtils.getAnnotationString(ta.name());
				if (ta != null) {
					if (StringUtils.isBlank(tableName)) {
						tableName = AnnotationUtils.getAnnotationString(ta.value());
					} else if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(ta.value()))) {
						throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_BAD_ATTRIBUTE_COMBINATION, "value",
								"name", Table.class, entityClass);
					}
				}

				if (tableName == null) {
					tableName = SqlUtils.generateSchemaElementName(entityClass.getSimpleName(),
							sqlGenerationApplySpacing);
				}

				String preferredTableName = sqlDataSourceDialect.getPreferredName(tableName);

				String viewName = ta != null ? AnnotationUtils.getAnnotationString(ta.view()) : null;
				if (viewName == null) {
					viewName = tableName;
				}

				final String schema = getWorkingSchema(
						ta != null ? AnnotationUtils.getAnnotationString(ta.schema()) : null,
						sqlDataSourceDialect.getDataSourceName());
				String schemaTableName = SqlUtils.generateFullSchemaElementName(schema, preferredTableName);

				Map<String, ForeignKeyOverride> fkOverrideMap = new HashMap<String, ForeignKeyOverride>();
				if (ta != null) {
					for (ForeignKeyOverride fkoa : ta.foreignKeyOverrides()) {
						fkOverrideMap.put(fkoa.key(), fkoa);
					}
				}

				Map<String, ColumnOverride> colOverrideMap = new HashMap<String, ColumnOverride>();
				if (ta != null) {
					for (ColumnOverride coa : ta.columnOverrides()) {
						colOverrideMap.put(coa.field(), coa);
					}
				} else if (tn != null) {
					for (ColumnOverride coa : tn.columnOverrides()) {
						colOverrideMap.put(coa.field(), coa);
					}
				}

				// Process all fields including super class fields
				Set<String> uniqueConstraintNames = new HashSet<String>();
				int ucConflictIndex = 0;
				Map<String, SqlFieldInfo> propertyInfoMap = new HashMap<String, SqlFieldInfo>();
				List<ChildFieldInfo> childInfoList = new ArrayList<ChildFieldInfo>();
				List<ChildFieldInfo> childListInfoList = new ArrayList<ChildFieldInfo>();
				SqlFieldInfo idFieldInfo = null;
				SqlFieldInfo versionFieldInfo = null;
				SqlFieldInfo tenantIdFieldInfo = null;
				SqlFieldInfo fosterParentTypeFieldInfo = null;
				SqlFieldInfo fosterParentIdFieldInfo = null;
				SqlFieldInfo categoryFieldInfo = null;
				Map<Class<?>, List<Field>> listOnlyFieldMap = new HashMap<Class<?>, List<Field>>();
				Map<Class<?>, Set<String>> childTypeCategoryInfo = new HashMap<Class<?>, Set<String>>();
				Class<?> searchClass = entityClass;
				do {
					Field[] fields = searchClass.getDeclaredFields();
					for (Field field : fields) {
						boolean isEnumConst = EnumConst.class.isAssignableFrom(field.getType());
						boolean isPersistent = false;
						boolean isPrimaryKey = false;
						boolean isForeignKey = false;
						boolean isNullable = false;
						boolean isFosterParentType = false;
						boolean isFosterParentId = false;
						boolean isCategoryColumn = false;
						int length = -1;
						int precision = -1;
						int scale = -1;
						int position = 0;
						String defaultVal = null;
						String category = null;

						String column = null;
						ColumnType columnType = ColumnType.AUTO;
						Transformer<?, ?> transformer = null;
						SqlEntityInfo foreignEntityInfo = null;
						SqlFieldInfo foreignFieldInfo = null;

						TenantId tia = field.getAnnotation(TenantId.class);
						Column ca = field.getAnnotation(Column.class);
						Id ia = field.getAnnotation(Id.class);
						Version va = field.getAnnotation(Version.class);
						ListOnly loa = field.getAnnotation(ListOnly.class);
						ForeignKey fka = field.getAnnotation(ForeignKey.class);
						Child clda = field.getAnnotation(Child.class);
						ChildList cla = field.getAnnotation(ChildList.class);
						FosterParentType fpta = field.getAnnotation(FosterParentType.class);
						FosterParentId fpia = field.getAnnotation(FosterParentId.class);
						CategoryColumn cca = field.getAnnotation(CategoryColumn.class);
						Mapped mpa = field.getAnnotation(Mapped.class);

						final String mapped = mpa != null ? AnnotationUtils.getAnnotationString(mpa.value()) : null;
						if (mapped != null) {
							if (ia != null || va != null || loa != null || fka != null || clda != null || cla != null
									|| fpta != null || fpia != null || cca != null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.BAD_COMBINATION_OF_ANNOTATION_WITH_MAPPED,
										searchClass, field);
							}

							if (ca == null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.COLUMN_ANNOTATION_REQUIRED_WITH_MAPPED, searchClass,
										field);
							}

							if (!Long.class.equals(field.getType())) {
								throw new UnifyException(UnifyCoreErrorConstants.APPLY_MAPPED_TO_LONG_FIELD_ONLY,
										searchClass, field);
							}
						}
						
						final boolean isTenantId = tia != null;
						if (isTenantId) {
							if (tenantIdFieldInfo != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_TENANTID_ANNOTATION,
										searchClass, field);
							}

							if (ia != null || va != null || loa != null || fka != null || clda != null || cla != null
									|| fpta != null || fpia != null || cca != null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.BAD_COMBINATION_OF_ANNOTATION_WITH_TENANT_ID,
										searchClass, field);
							}

							if (ca == null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.COLUMN_ANNOTATION_REQUIRED_WITH_TENANT_ID, searchClass,
										field);
							}

							if (!Long.class.equals(field.getType())) {
								throw new UnifyException(UnifyCoreErrorConstants.APPLY_TENANT_ID_TO_LONG_FIELD_ONLY,
										searchClass, field);
							}
						}

						// Process primary key
						if (ia != null) {
							isPersistent = true;
							isPrimaryKey = true;
							position = ia.position();

							ColumnOverride coa = colOverrideMap.get(field.getName());
							if (coa != null) {
								if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(coa.name()))) {
									column = coa.name();
								}
							}

							if (column == null) {
								column = ta != null ? AnnotationUtils.getAnnotationString(ta.idColumn()) : null;
								if (column == null) {
									column = AnnotationUtils.getAnnotationString(ia.name());
								}

								if (column == null) {
									column = tableName + "_ID";
								}
							}

							if (idFieldInfo != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_ID_ANNOTATION,
										searchClass, field);
							}

							if (va != null || ca != null || loa != null || fka != null || clda != null || cla != null
									|| fpta != null || fpia != null || cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							length = ia.length();
						}

						// Process version number
						if (va != null) {
							isPersistent = true;
							position = va.position();

							ColumnOverride coa = colOverrideMap.get(field.getName());
							if (coa != null) {
								if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(coa.name()))) {
									column = coa.name();
								}
							}

							if (column == null) {
								column = ta != null ? AnnotationUtils.getAnnotationString(ta.versionColumn()) : null;
								if (column == null) {
									column = AnnotationUtils.getAnnotationString(va.name());
								}

								if (column == null) {
									column = "VERSION_NO";
								}
							}

							if (versionFieldInfo != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_VERSION_ANNOTATION,
										searchClass, field);
							}

							if (ca != null || loa != null || fka != null || clda != null || cla != null || fpta != null
									|| fpia != null || cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}
						}

						// Process column
						if (ca != null) {
							isPersistent = true;
							position = ca.position();
							defaultVal = AnnotationUtils.getAnnotationString(ca.defaultVal());

							if (loa != null || fka != null || clda != null || cla != null || fpta != null
									|| fpia != null || cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							ColumnOverride coa = colOverrideMap.get(field.getName());
							if (coa != null) {
								if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(coa.name()))) {
									column = coa.name();
								}
								columnType = !coa.type().isAuto() ? coa.type() : columnType;

								String transformerName = AnnotationUtils.getAnnotationString(coa.transformer());
								if (transformerName != null) {
									transformer = (Transformer<?, ?>) getComponent(transformerName);
								}

								isNullable = coa.nullable() ? coa.nullable() : isNullable;
								length = coa.length() > 0 ? coa.length() : length;
								precision = coa.precision() > 0 ? coa.precision() : precision;
								scale = coa.scale() > 0 ? coa.scale() : scale;
							} else {
								if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(ca.name()))) {
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

							if (isTenantId) {
								defaultVal = "0";
								isNullable = false;
								length = 0;
								scale = 0;
							}
						}

						// Process foreign key
						if (fka != null) {
							position = fka.position();
							defaultVal = AnnotationUtils.getAnnotationString(fka.defaultVal());

							if (loa != null || clda != null || cla != null || fpta != null || fpia != null
									|| cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							isPersistent = true;
							isForeignKey = true;
							String fkName = null;

							ForeignKeyOverride fkoa = fkOverrideMap.get(field.getName());
							Class<?> foreignType = null;
							if (fkoa != null) {
								fkName = fkoa.name();
								isNullable = fkoa.nullable();
								foreignType = fkoa.foreignType();
							} else {
								fkName = fka.name();
								isNullable = fka.nullable();
								foreignType = fka.value();
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

								if (foreignType.isAnnotationPresent(TableExt.class)) {
									throw new UnifyException(
											UnifyCoreErrorConstants.RECORD_EXTENSION_REFERENCE_NOT_ALLOWED, entityClass,
											field);
								}
							}

							// Set column name
							column = AnnotationUtils.getAnnotationString(fkName);
							if (entityClass.equals(foreignType)) { // Self reference
								isNullable = true;
							} else {
								entityCycleDetector.addReference(entityClass, foreignType);
								foreignEntityInfo = get(foreignType, entityCycleDetector);
								foreignFieldInfo = foreignEntityInfo.getIdFieldInfo();
								length = foreignFieldInfo.getLength();
								precision = foreignFieldInfo.getPrecision();
								scale = foreignFieldInfo.getScale();

								if (!isEnumConst && !field.getType().equals(foreignFieldInfo.getField().getType())) {
									throw new UnifyException(
											UnifyCoreErrorConstants.RECORD_FOREIGNKEY_TYPE_MUST_MATCH_ID, searchClass,
											field, foreignFieldInfo.getField());
								}

								// Add on delete info to parent entity class
								if (fka.onDeleteCascade()) {
									foreignEntityInfo.expandOnDeleteCascade(new OnDeleteCascadeInfo(
											(Class<? extends Entity>) entityClass, field, null, null));
								}
							}
						}

						// Save list-only fields. Would be processed later since
						// they depend on foreign key fields
						if (loa != null) {
							if (clda != null || cla != null || fpta != null || fpia != null || cca != null) {
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

						// Process child
						if (clda != null) {
							if (cla != null || fpta != null || fpia != null || cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							Class<?> childType = field.getType();
							if (childType.isAnnotationPresent(TableExt.class)) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_EXTENSION_REFERENCE_NOT_ALLOWED,
										entityClass, field);
							}

							ChildFkFields childFkFields = getFosterParentChildFkFields(childType);
							if (childFkFields == null) {
								childFkFields = getAttributeOnlyChildFkFields(entityClass, childType);
							}

							if (childFkFields == null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_CHILD_NO_MATCHING_FK, field,
										childType);
							}

							category = AnnotationUtils.getAnnotationString(clda.category());
							if (StringUtils.isBlank(category) && childFkFields.isWithCategory()) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_CHILD_ANNOTATION_REQUIRES_CATEGORY, field,
										childType);
							}

							if (!StringUtils.isBlank(category) && !childFkFields.isWithCategory()) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_CHILD_ANNOTATION_CANT_CATEGORY,
										field, childType);
							}

							checkChildCategoryRule(entityClass, category, field, childType, childTypeCategoryInfo);

							childInfoList.add(getChildFieldInfo(entityClass, category, field,
									(Class<? extends Entity>) childType, childFkFields, false));
						}

						// Process child list
						if (cla != null) {
							if (fpta != null || fpia != null || cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							Class<?> listType = field.getType();
							if (!List.class.isAssignableFrom(listType)) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_CHILDLIST_FIELD_TYPE,
										searchClass, field);
							}

							Class<?> argumentType = ReflectUtils.getArgumentType(field.getGenericType(), 0);
							if (argumentType == null) {
								argumentType = cla.listType().equals(Entity.class) ? null : cla.listType();
							}

							if (argumentType.isAnnotationPresent(TableExt.class)) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_EXTENSION_REFERENCE_NOT_ALLOWED,
										entityClass, field);
							}

							ChildFkFields childFkFields = getFosterParentChildFkFields(argumentType);
							if (childFkFields == null) {
								childFkFields = getAttributeOnlyChildFkFields(entityClass, argumentType);
							}

							if (childFkFields == null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_CHILDLIST_NO_MATCHING_FK, field,
										argumentType);
							}

							category = AnnotationUtils.getAnnotationString(cla.category());
							if (StringUtils.isBlank(category) && childFkFields.isWithCategory()) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_CHILD_ANNOTATION_REQUIRES_CATEGORY, field,
										argumentType);
							}

							if (!StringUtils.isBlank(category) && !childFkFields.isWithCategory()) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_CHILD_ANNOTATION_CANT_CATEGORY,
										field, argumentType);
							}

							checkChildCategoryRule(entityClass, category, field, argumentType, childTypeCategoryInfo);

							childListInfoList.add(getChildFieldInfo(entityClass, category, field,
									(Class<? extends Entity>) argumentType, childFkFields, true));
						}

						// Process foster parent type
						if (fpta != null) {
							if (fpia != null || cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							if (!field.getType().equals(String.class)) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_INVALID_FOSTER_PARENT_TYPE_FIELD_TYPE,
										searchClass, field);
							}

							if (fosterParentTypeFieldInfo != null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_MULTIPLE_FOSTER_PARENT_TYPE_ANNOTATION,
										searchClass, field);
							}

							if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(fpta.name()))) {
								column = fpta.name();
							}

							isPersistent = true;
							isFosterParentType = true;
							length = fpta.length();
						}

						// Process foster parent ID
						if (fpia != null) {
							if (cca != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
										searchClass, field);
							}

							if (!field.getType().equals(Long.class)) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_INVALID_FOSTER_PARENT_ID_FIELD_TYPE, searchClass,
										field);
							}

							if (fosterParentIdFieldInfo != null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_MULTIPLE_FOSTER_PARENT_ID_ANNOTATION,
										searchClass, field);
							}

							if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(fpia.name()))) {
								column = fpia.name();
							}

							isPersistent = true;
							isFosterParentId = true;
						}

						// Process category column
						if (cca != null) {
							if (!field.getType().equals(String.class)) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_INVALID_CATEGORY_COLUMN_FIELD_TYPE, searchClass,
										field);
							}

							if (categoryFieldInfo != null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.RECORD_MULTIPLE_CATEGORY_COLUMN_ANNOTATION, searchClass,
										field);
							}

							if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(cca.name()))) {
								column = cca.name();
							}

							isPersistent = true;
							isCategoryColumn = true;
							length = cca.length();
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
								constraintName = SqlUtils.generateForeignKeyConstraintName(tableName, field.getName());
								if (uniqueConstraintNames.contains(constraintName)) {
									constraintName = SqlUtils.resolveForeignKeyConstraintNameConflict(constraintName,
											ucConflictIndex++);
								} else {
									uniqueConstraintNames.add(constraintName);
								}
							}

							SqlFieldDimensions sqlFieldDimensions = SqlUtils.getNormalizedSqlFieldDimensions(columnType,
									length, precision, scale);
							final boolean isIgnoreFkConstraint = false;
							final boolean isListOnly = false;
							GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(searchClass,
									field.getName());
							SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(position, columnType, foreignEntityInfo,
									foreignFieldInfo, null, field.getName(), column,
									sqlDataSourceDialect.getPreferredName(column), constraintName, null, isPrimaryKey,
									isForeignKey, isListOnly, isIgnoreFkConstraint, transformer, sqlFieldDimensions,
									isNullable, isFosterParentType, isFosterParentId, isCategoryColumn, isTenantId,
									mapped, defaultVal, field, getterSetterInfo.getGetter(), getterSetterInfo.getSetter(),
									sqlDataSourceDialect.isAllObjectsInLowerCase());

							if (ia != null) {
								idFieldInfo = sqlFieldInfo;
							}

							if (va != null) {
								versionFieldInfo = sqlFieldInfo;
							}

							if (tia != null) {
								tenantIdFieldInfo = sqlFieldInfo;
							}

							if (fpta != null) {
								fosterParentTypeFieldInfo = sqlFieldInfo;
							}

							if (fpia != null) {
								fosterParentIdFieldInfo = sqlFieldInfo;
							}

							if (cca != null) {
								categoryFieldInfo = sqlFieldInfo;
							}
							propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
						}
					}
				} while ((searchClass = searchClass.getSuperclass()) != null);

				// Rename view if necessary
				if (!listOnlyFieldMap.isEmpty() && ta != null && viewName.equals(tableName)) {
					viewName = SqlUtils.generateViewName(tableName);
				}

				String preferredViewName = sqlDataSourceDialect.getPreferredName(viewName);
				String schemaViewName = SqlUtils.generateFullSchemaElementName(schema, preferredViewName);

				// Process list-only fields
				for (Map.Entry<Class<?>, List<Field>> entry : listOnlyFieldMap.entrySet()) {
					for (Field field : entry.getValue()) {
						ListOnly loa = field.getAnnotation(ListOnly.class);
						SqlFieldInfo foreignKeySqlFieldInfo = propertyInfoMap.get(loa.key());
						if (foreignKeySqlFieldInfo == null) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_UNKNOWN_FOREIGN_KEY, entry.getKey(),
									field, loa.key());
						}

						if (!foreignKeySqlFieldInfo.isForeignKey()) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_LISTONLY_KEY_NOT_REF_FOREIGN_KEY,
									entry.getKey(), field, loa.key());
						}

						SqlEntityInfo foreignEntityInfo = null;
						SqlFieldInfo foreignPropSqlFieldInfo = null;
						if (foreignKeySqlFieldInfo.isUnresolvedForeignKey()) { // Self reference
							foreignPropSqlFieldInfo = propertyInfoMap.get(loa.property());
						} else {
							foreignEntityInfo = foreignKeySqlFieldInfo.getForeignEntityInfo();
							if (foreignEntityInfo.isExtended()) {
								foreignEntityInfo = foreignEntityInfo.getExtensionSqlEntityInfo();
							}

							foreignPropSqlFieldInfo = foreignEntityInfo.getListFieldInfo(loa.property());
						}

						// Make sure field type is the same with foreign field type
						if (!field.getType().equals(foreignPropSqlFieldInfo.getField().getType())) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_FOREIGNKEY_TYPE_MUST_MATCH_ID,
									entry.getKey(), field, foreignPropSqlFieldInfo.getField());
						}

						// Set column name
						String column = AnnotationUtils.getAnnotationString(loa.name());
						if (StringUtils.isBlank(column)) {
							column = SqlUtils.generateSchemaElementName(field.getName(), true);
						}

						SqlFieldDimensions sqlFieldDimensions = new SqlFieldDimensions(
								foreignPropSqlFieldInfo.getLength(), foreignPropSqlFieldInfo.getPrecision(),
								foreignPropSqlFieldInfo.getScale());
						GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(entry.getKey(),
								field.getName());
						final boolean isPrimaryKey = false;
						final boolean isForeignKey = false;
						final boolean isIgnoreFkConstraint = false;
						final boolean isListOnly = true;
						final boolean isFosterParentType = false;
						final boolean isFosterParentId = false;
						final boolean isCategoryColumn = false;
						final boolean isTenantId = false;
						final String mapped = null;
						SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.LIST_POSITION,
								foreignPropSqlFieldInfo.getColumnType(), foreignEntityInfo, foreignPropSqlFieldInfo,
								foreignKeySqlFieldInfo, field.getName(), column,
								sqlDataSourceDialect.getPreferredName(column), null, null, isPrimaryKey, isForeignKey,
								isListOnly, isIgnoreFkConstraint, foreignPropSqlFieldInfo.getTransformer(),
								sqlFieldDimensions, foreignPropSqlFieldInfo.isNullable(), isFosterParentType,
								isFosterParentId, isCategoryColumn, isTenantId, mapped, null, field,
								getterSetterInfo.getGetter(), getterSetterInfo.getSetter(),
								sqlDataSourceDialect.isAllObjectsInLowerCase());

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
					Class<?> type = versionFieldInfo.getFieldType();
					if (!SqlUtils.isVersionNumberType(type)) {
						throw new UnifyException(UnifyCoreErrorConstants.RECORD_VERSION_NOT_INTEGER, entityClass);
					}
				}

				if ((fosterParentTypeFieldInfo != null) ^ (fosterParentIdFieldInfo != null)) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_MUST_COMBINE_FOSTER_ANNOTATIONS,
							entityClass);
				}

				EntityPolicy entityPolicy = null;
				Policy pa = entityClass.getAnnotation(Policy.class);
				if (pa != null) {
					entityPolicy = (EntityPolicy) getComponent(pa.value());
				}

				List<Class<?>> heirachyList = ReflectUtils.getClassHierachyList(entityClass);
				Map<String, SqlUniqueConstraintInfo> uniqueConstraintMap = extractUniqueConstraints(tableName,
						entityClass, heirachyList, propertyInfoMap, tenantIdFieldInfo,
						ta != null ? ta.uniqueConstraints() : new UniqueConstraint[] {});
				Map<String, SqlIndexInfo> indexMap = extractIndexes(tableName, entityClass, heirachyList,
						propertyInfoMap, ta != null ? ta.indexes() : new Index[] {});

				if (sqlOrderColumns) {
					List<SqlFieldInfo> tempList = new ArrayList<SqlFieldInfo>(propertyInfoMap.values());
					DataUtils.sortAscending(tempList, SqlFieldInfo.class, "columnName");
					DataUtils.sortAscending(tempList, SqlFieldInfo.class, "orderIndex");

					propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
					for (SqlFieldInfo sqlFieldInfo : tempList) {
						propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
					}
				}

				String tableAlias = "T" + (++tAliasCounter);
				SqlEntityInfo sqlEntityInfo = new SqlEntityInfo(null, (Class<? extends Entity>) entityClass, null,
						entityPolicy, schema, tableName, preferredTableName, schemaTableName, tableAlias, viewName,
						preferredViewName, schemaViewName, idFieldInfo, versionFieldInfo, tenantIdFieldInfo,
						fosterParentTypeFieldInfo, fosterParentIdFieldInfo, categoryFieldInfo, propertyInfoMap,
						childInfoList, childListInfoList, uniqueConstraintMap, indexMap, null, null, null,
						sqlDataSourceDialect.isAllObjectsInLowerCase(), ta != null ? ta.identityManaged() : true);
				return sqlEntityInfo;
			}

			@SuppressWarnings("unchecked")
			private SqlEntityInfo createTableExtensionEntityInfo(Class<?> entityClass,
					EntityCycleDetector entityCycleDetector) throws Exception {
				logDebug("Creating table extension entity information for [{0}]...", entityClass);
				TableExt tae = entityClass.getAnnotation(TableExt.class);
				Class<?> extendedEntityClass = entityClass.getSuperclass();
				if (extendedEntityClass == null || !extendedEntityClass.isAnnotationPresent(Table.class)) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_EXTENSION_SUPERCLASS_INCOMPATIBLE,
							entityClass, extendedEntityClass, Table.class);
				}

				SqlEntityInfo toExtendSqlEntityInfo = get(extendedEntityClass, entityCycleDetector);
				boolean deprecateExtension = false;
				if (toExtendSqlEntityInfo.isExtended()) {
					deprecateExtension = entityClass.getName()
							.equals(toExtendSqlEntityInfo.getExtensionSqlEntityInfo().getEntityClass().getName());
					if (!deprecateExtension) {
						throw new UnifyException(UnifyCoreErrorConstants.RECORD_SUPERCLASS_ALREADY_EXTENDED,
								entityClass, extendedEntityClass, toExtendSqlEntityInfo.getEntityClass());
					}
				}

				final String tableName = toExtendSqlEntityInfo.getTableName();

				// Process all fields including super class fields
				Set<String> uniqueConstraintNames = new HashSet<String>();
				int ucConflictIndex = 0;
				Map<String, SqlFieldInfo> propertyInfoMap = new HashMap<String, SqlFieldInfo>();
				List<Field> listOnlyFieldList = new ArrayList<Field>();
				Field[] fields = entityClass.getDeclaredFields();
				for (Field field : fields) {
					if (toExtendSqlEntityInfo.isListField(field.getName())) {
						throw new UnifyException(UnifyCoreErrorConstants.RECORD_SUPERCLASS_FIELD_EXIST, entityClass,
								field);
					}

					boolean isEnumConst = EnumConst.class.isAssignableFrom(field.getType());
					boolean isPersistent = false;
					boolean isPrimaryKey = false;
					boolean isForeignKey = false;
					final boolean isFosterParentType = false;
					final boolean isFosterParentId = false;
					final boolean isCategoryColumn = false;
					final boolean isNullable = true; // All extension fields are nullable
					final String mapped = null;
					int length = -1;
					int precision = -1;
					int scale = -1;
					int position = 0;
					String defaultVal = null;

					String column = null;
					ColumnType columnType = ColumnType.AUTO;
					Transformer<?, ?> transformer = null;
					SqlEntityInfo foreignEntityInfo = null;
					SqlFieldInfo foreignFieldInfo = null;

					ForeignKey fka = field.getAnnotation(ForeignKey.class);
					Column ca = field.getAnnotation(Column.class);
					ListOnly loa = field.getAnnotation(ListOnly.class);
					checkUnsupported(field, Id.class);
					checkUnsupported(field, Version.class);
					checkUnsupported(field, Child.class);
					checkUnsupported(field, ChildList.class);

					// Process column
					if (ca != null) {
						isPersistent = true;
						position = ca.position();
						defaultVal = AnnotationUtils.getAnnotationString(ca.defaultVal());

						if (loa != null || fka != null) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
									entityClass, field);
						}

						if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(ca.name()))) {
							column = ca.name();
						}
						columnType = ca.type(); // Overrides default
												// AUTO
												// type

						String transformerName = AnnotationUtils.getAnnotationString(ca.transformer());
						if (transformerName != null) {
							transformer = (Transformer<?, ?>) getComponent(transformerName);
						}

						length = ca.length();
						precision = ca.precision();
						scale = ca.scale();
					}

					// Process foreign key
					if (fka != null) {
						position = fka.position();
						defaultVal = AnnotationUtils.getAnnotationString(fka.defaultVal());

						if (loa != null) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
									entityClass, field);
						}

						isPersistent = true;
						isForeignKey = true;
						String fkName = null;
						boolean onDeleteCascade = false;

						fkName = fka.name();
						Class<?> foreignType = fka.value();
						if (foreignType.equals(Entity.class)) {
							foreignType = fka.type();
						} else if (!fka.type().equals(Entity.class)) {
							throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_BAD_ATTRIBUTE_COMBINATION,
									"value", "foreignType", fka.getClass(), field);
						}

						if (isEnumConst) {
							if (!fka.type().equals(Entity.class)) {
								throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_FOREIGN_TYPE_NOT_PERMITTED,
										field.getType(), fka.getClass(), field);
							}

							foreignType = field.getType();
						}

						if (foreignType.equals(Entity.class)) {
							throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_MUST_SPECIFY_ATTRIBUTE_OF_TWO,
									"value", "foreignType", fka.getClass(), field);
						}

						if (foreignType.isAnnotationPresent(TableExt.class)) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_EXTENSION_REFERENCE_NOT_ALLOWED,
									entityClass, field);
						}

						if (foreignType.equals(extendedEntityClass)) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_SUPERCLASS_REFERENCE_NOT_ALLOWED,
									entityClass, field);
						}

						entityCycleDetector.addReference(extendedEntityClass, foreignType);
						foreignEntityInfo = get(foreignType, entityCycleDetector);
						onDeleteCascade = fka.onDeleteCascade();

						foreignFieldInfo = foreignEntityInfo.getIdFieldInfo();
						length = foreignFieldInfo.getLength();
						precision = foreignFieldInfo.getPrecision();
						scale = foreignFieldInfo.getScale();

						// Set column name
						column = AnnotationUtils.getAnnotationString(fkName);
						if (!isEnumConst && !field.getType().equals(foreignFieldInfo.getField().getType())) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_FOREIGNKEY_TYPE_MUST_MATCH_ID,
									entityClass, field, foreignFieldInfo.getField());
						}

						// Add on delete info to parent entity class
						if (onDeleteCascade) {
							foreignEntityInfo.expandOnDeleteCascade(
									new OnDeleteCascadeInfo((Class<? extends Entity>) entityClass, field, null, null));
						}
					}

					// Save list-only fields. Would be processed later since
					// they depend on foreign key fields
					if (loa != null) {
						listOnlyFieldList.add(field);
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
							constraintName = SqlUtils.generateForeignKeyConstraintName(tableName, field.getName());
							if (uniqueConstraintNames.contains(constraintName)) {
								constraintName = SqlUtils.resolveForeignKeyConstraintNameConflict(constraintName,
										ucConflictIndex++);
							} else {
								uniqueConstraintNames.add(constraintName);
							}
						}

						SqlFieldDimensions sqlFieldDimensions = SqlUtils.getNormalizedSqlFieldDimensions(columnType,
								length, precision, scale);
						final boolean isIgnoreFkConstraint = false;
						final boolean isListOnly = false;
						final boolean isTenantId = false;
						GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(entityClass,
								field.getName());
						SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(position, columnType, foreignEntityInfo,
								foreignFieldInfo, null, field.getName(), column,
								sqlDataSourceDialect.getPreferredName(column), constraintName, null, isPrimaryKey,
								isForeignKey, isListOnly, isIgnoreFkConstraint, transformer, sqlFieldDimensions,
								isNullable, isFosterParentType, isFosterParentId, isCategoryColumn, isTenantId,
								mapped, defaultVal, field, getterSetterInfo.getGetter(), getterSetterInfo.getSetter(),
								sqlDataSourceDialect.isAllObjectsInLowerCase());
						propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
					}
				}

				// Process list-only fields
				String viewName = toExtendSqlEntityInfo.getViewName();
				String preferredViewName = toExtendSqlEntityInfo.getPreferredViewName();
				String schemaViewName = toExtendSqlEntityInfo.getSchemaViewName();
				if (!listOnlyFieldList.isEmpty()) {
					if (!toExtendSqlEntityInfo.isViewable()) {
						viewName = SqlUtils.generateViewName(tableName);
						preferredViewName = sqlDataSourceDialect.getPreferredName(viewName);
						schemaViewName = SqlUtils.generateFullSchemaElementName(toExtendSqlEntityInfo.getSchema(),
								preferredViewName);
					}

					for (Field field : listOnlyFieldList) {
						ListOnly loa = field.getAnnotation(ListOnly.class);
						SqlFieldInfo foreignKeySQLFieldInfo = propertyInfoMap.get(loa.key());
						if (foreignKeySQLFieldInfo == null && toExtendSqlEntityInfo.isListField(loa.key())) {
							foreignKeySQLFieldInfo = toExtendSqlEntityInfo.getListFieldInfo(loa.key());
						}

						if (foreignKeySQLFieldInfo == null) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_UNKNOWN_FOREIGN_KEY, entityClass,
									field, loa.key());
						}

						if (!foreignKeySQLFieldInfo.isForeignKey()) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_LISTONLY_KEY_NOT_REF_FOREIGN_KEY,
									entityClass, field, loa.key());
						}

						SqlEntityInfo foreignEntityInfo = foreignKeySQLFieldInfo.getForeignEntityInfo();
						if (foreignEntityInfo.isExtended()) {
							foreignEntityInfo = foreignEntityInfo.getExtensionSqlEntityInfo();
						}

						SqlFieldInfo foreignFieldInfo = foreignEntityInfo.getListFieldInfo(loa.property());

						// Make sure field type is the same with foreign field
						// type
						if (!field.getType().equals(foreignFieldInfo.getField().getType())) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_FOREIGNKEY_TYPE_MUST_MATCH_ID,
									entityClass, field, foreignFieldInfo.getField());
						}

						// Set column name
						String column = AnnotationUtils.getAnnotationString(loa.name());
						if (StringUtils.isBlank(column)) {
							column = SqlUtils.generateSchemaElementName(field.getName(), true);
						}

						SqlFieldDimensions sqlFieldDimensions = new SqlFieldDimensions(foreignFieldInfo.getLength(),
								foreignFieldInfo.getPrecision(), foreignFieldInfo.getScale());
						GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(entityClass,
								field.getName());
						final boolean isPrimaryKey = false;
						final boolean isForeignKey = false;
						final boolean isIgnoreFkConstraint = false;
						final boolean isListOnly = true;
						final boolean isFosterParentType = false;
						final boolean isFosterParentId = false;
						final boolean isCategoryColumn = false;
						final boolean isTenantId = false;
						final String mapped = null;
						SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.LIST_POSITION,
								foreignFieldInfo.getColumnType(), foreignEntityInfo, foreignFieldInfo,
								foreignKeySQLFieldInfo, field.getName(), column,
								sqlDataSourceDialect.getPreferredName(column), null, null, isPrimaryKey, isForeignKey,
								isListOnly, isIgnoreFkConstraint, foreignFieldInfo.getTransformer(), sqlFieldDimensions,
								foreignFieldInfo.isNullable(), isFosterParentType, isFosterParentId, isCategoryColumn,
								isTenantId, mapped, null, field, getterSetterInfo.getGetter(), getterSetterInfo.getSetter(),
								sqlDataSourceDialect.isAllObjectsInLowerCase());

						propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
					}
				}

				List<Class<?>> heirachyList = ReflectUtils.getClassHierachyList(entityClass);
				Map<String, SqlUniqueConstraintInfo> uniqueConstraintMap = extractUniqueConstraints(tableName,
						entityClass, heirachyList, propertyInfoMap, null, tae.uniqueConstraints());
				Map<String, SqlIndexInfo> indexMap = extractIndexes(tableName, entityClass, heirachyList,
						propertyInfoMap, tae.indexes());

				if (sqlOrderColumns) {
					List<SqlFieldInfo> tempList = new ArrayList<SqlFieldInfo>(propertyInfoMap.values());
					DataUtils.sortAscending(tempList, SqlFieldInfo.class, "columnName");
					DataUtils.sortAscending(tempList, SqlFieldInfo.class, "orderIndex");

					propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
					for (SqlFieldInfo sqlFieldInfo : tempList) {
						propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
					}
				}

				return toExtendSqlEntityInfo.extend(viewName, preferredViewName, schemaViewName,
						(Class<? extends Entity>) entityClass, propertyInfoMap, uniqueConstraintMap, indexMap,
						deprecateExtension);
			}

			@SuppressWarnings("unchecked")
			private SqlEntityInfo createViewEntityInfo(Class<?> entityClass, EntityCycleDetector entityCycleDetector)
					throws Exception {
				View va = entityClass.getAnnotation(View.class);
				String viewName = AnnotationUtils.getAnnotationString(va.name());
				if (StringUtils.isBlank(viewName)) {
					viewName = SqlUtils.generateViewName(entityClass.getSimpleName(), sqlGenerationApplySpacing);
				}

				String preferredViewName = sqlDataSourceDialect.getPreferredName(viewName);

				String schema = getWorkingSchema(AnnotationUtils.getAnnotationString(va.schema()),
						sqlDataSourceDialect.getDataSourceName());
				String schemaViewName = SqlUtils.generateFullSchemaElementName(schema, preferredViewName);

				TableReferences tableReferences = new TableReferences(entityClass);

				// Process all list-only fields including super class fields
				Map<String, SqlFieldInfo> propertyInfoMap = new HashMap<String, SqlFieldInfo>();
				SqlFieldInfo idFieldInfo = null;
				Class<?> searchClass = entityClass;
				do {
					Field[] fields = searchClass.getDeclaredFields();
					for (Field field : fields) {
						boolean isPrimaryKey = false;
						checkUnsupportedByView(Column.class, searchClass, field);
						checkUnsupportedByView(Version.class, searchClass, field);
						checkUnsupportedByView(ForeignKey.class, searchClass, field);
						checkUnsupportedByView(Child.class, searchClass, field);
						checkUnsupportedByView(ChildList.class, searchClass, field);
						checkUnsupportedByView(FosterParentType.class, searchClass, field);
						checkUnsupportedByView(FosterParentId.class, searchClass, field);

						Id ia = field.getAnnotation(Id.class);
						ListOnly loa = field.getAnnotation(ListOnly.class);

						if (ia != null && loa != null) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
									searchClass, field.getName());
						}

						SqlEntityInfo refEntityInfo = null;
						SqlFieldInfo refFieldInfo = null;
						String refEntityPreferredAlias = null;
						String column = null;
						if (ia != null) {
							isPrimaryKey = true;
							refEntityPreferredAlias = va.primaryAlias();
							refEntityInfo = get(tableReferences.getEntityClass(refEntityPreferredAlias),
									entityCycleDetector);
							refFieldInfo = refEntityInfo.getIdFieldInfo();
						}

						if (loa != null) {
							column = AnnotationUtils.getAnnotationString(loa.name());
							PropertyRef propertyRef = tableReferences.getPropertyRef(loa.property());
							refEntityPreferredAlias = propertyRef.getTableAlias();
							refEntityInfo = get(tableReferences.getEntityClass(refEntityPreferredAlias),
									entityCycleDetector);
							// Persistent properties only
							refFieldInfo = refEntityInfo.getFieldInfo(propertyRef.getFieldName());
						}

						if (!field.getType().equals(refFieldInfo.getFieldType())) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_TYPE_MUST_MATCH_TYPE,
									searchClass, field, refFieldInfo.getFieldType());
						}

						if (StringUtils.isBlank(column)) {
							column = SqlUtils.generateSchemaElementName(field.getName(), true);
						}

						SqlFieldDimensions sqlFieldDimensions = new SqlFieldDimensions(refFieldInfo.getLength(),
								refFieldInfo.getPrecision(), refFieldInfo.getScale());
						GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(searchClass,
								field.getName());
						final boolean isForeignKey = false;
						final boolean isIgnoreFkConstraint = false;
						final boolean isListOnly = true;
						final boolean isNullable = false;
						final boolean isFosterParentType = false;
						final boolean isFosterParentId = false;
						final boolean isCategoryColumn = false;
						final boolean isTenantId = false;
						final String mapped = null;
						SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.LIST_POSITION,
								refFieldInfo.getColumnType(), refEntityInfo, refFieldInfo, null, field.getName(),
								column, sqlDataSourceDialect.getPreferredName(column), null,
								refEntityPreferredAlias.toUpperCase(), isPrimaryKey, isForeignKey, isListOnly,
								isIgnoreFkConstraint, refFieldInfo.getTransformer(), sqlFieldDimensions, isNullable,
								isFosterParentType, isFosterParentId, isCategoryColumn, isTenantId, mapped, null, field,
								getterSetterInfo.getGetter(), getterSetterInfo.getSetter(),
								sqlDataSourceDialect.isAllObjectsInLowerCase());

						if (isPrimaryKey) {
							if (idFieldInfo != null) {
								throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_ID_ANNOTATION,
										searchClass, field);
							}

							idFieldInfo = sqlFieldInfo;
						}

						propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);

					}
				} while ((searchClass = searchClass.getSuperclass()) != null);

				// View must have an ID property
				if (idFieldInfo == null) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_REQUIRES_ID, entityClass);
				}

				// View restriction
				List<SqlViewRestrictionInfo> viewRestrictionList = null;
				if (va.restrictions().length > 0) {
					viewRestrictionList = new ArrayList<SqlViewRestrictionInfo>();
					for (ViewRestriction vra : va.restrictions()) {
						RestrictionType restrictionType = vra.type();
						Object param1 = null;
						Object param2 = null;
						switch (restrictionType) {
						case IS_NOT_NULL:
						case IS_NULL:
							break;
						case BEGINS_WITH:
						case ENDS_WITH:
						case EQUALS:
						case GREATER:
						case GREATER_OR_EQUAL:
						case LESS_OR_EQUAL:
						case LESS_THAN:
						case LIKE:
						case NOT_BEGIN_WITH:
						case NOT_END_WITH:
						case NOT_EQUALS:
						case NOT_LIKE:
							String rightProperty = AnnotationUtils.getAnnotationString(vra.rightProperty());
							if (StringUtils.isNotBlank(rightProperty)) {
								param1 = getSqlViewColumnInfo(tableReferences, entityCycleDetector,
										vra.rightProperty());
							} else {
								param1 = vra.values()[0];
							}
							break;
						case BETWEEN:
						case NOT_BETWEEN:
							param1 = vra.values()[0];
							param2 = vra.values()[1];
							break;
						case AMONGST:
						case NOT_AMONGST:
							param1 = DataUtils.convert(List.class, String.class, vra.values());
							break;
						case AND:
						case OR:
							throw new UnifyException(
									UnifyCoreErrorConstants.RECORD_VIEW_COMPOUND_RESTRICTION_UNSUPPORTED, entityClass);
						default:
							break;
						}

						SqlViewColumnInfo columnInfo = getSqlViewColumnInfo(tableReferences, entityCycleDetector,
								vra.leftProperty());
						viewRestrictionList
								.add(new SqlViewRestrictionInfo(columnInfo, restrictionType, param1, param2));
					}
				}

				SqlEntityInfo sqlEntityInfo = new SqlEntityInfo(null, (Class<? extends Entity>) entityClass, null, null,
						schema, viewName, preferredViewName, schemaViewName, null, viewName, preferredViewName,
						schemaViewName, idFieldInfo, null, null, null, null, null, propertyInfoMap, null, null, null,
						null, null, tableReferences.getBaseTables(), viewRestrictionList,
						sqlDataSourceDialect.isAllObjectsInLowerCase(), true);
				return sqlEntityInfo;
			}

			private <T extends Annotation> void checkUnsupportedByView(Class<T> clazz, Class<?> searchClass,
					Field field) throws UnifyException {
				if (field.getAnnotation(clazz) != null) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_UNSUPPORTED_ANNOTATION, clazz,
							searchClass, field.getName());
				}
			}

			private SqlViewColumnInfo getSqlViewColumnInfo(TableReferences tableReferences,
					EntityCycleDetector entityCycleDetector, String property) throws UnifyException {
				PropertyRef propertyRef = tableReferences.getPropertyRef(property);
				SqlEntityInfo sqlEntityInfo = get(tableReferences.getEntityClass(propertyRef.getTableAlias()),
						entityCycleDetector);
				String columnName = sqlEntityInfo.getFieldInfo(propertyRef.getFieldName()).getPreferredColumnName();
				return new SqlViewColumnInfo(propertyRef.getTableAlias(), columnName);
			}

			private void checkUnsupported(Field field, Class<? extends Annotation> annotationClass)
					throws UnifyException {
				if (field.getAnnotation(annotationClass) != null) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_EXTENSION_UNSUPPORTED_ANNOTATION,
							annotationClass.getName(), field);
				}
			}

			private Map<String, SqlUniqueConstraintInfo> extractUniqueConstraints(String tableName,
					Class<?> entityClass, List<Class<?>> heirachyList, Map<String, SqlFieldInfo> propertyInfoMap,
					SqlFieldInfo tenantIdFieldInfo, UniqueConstraint[] uniqueConstraints) throws UnifyException {
				// Unique constraints
				List<UniqueConstraint> resolvedConstraints = new ArrayList<UniqueConstraint>();
				for (Class<?> clazz : heirachyList) {
					UniqueConstraints ucs = clazz.getAnnotation(UniqueConstraints.class);
					if (ucs != null) {
						for (UniqueConstraint uca : ucs.value()) {
							resolvedConstraints.add(uca);
						}
					}
				}

				for (UniqueConstraint uca : uniqueConstraints) {
					resolvedConstraints.add(uca);
				}

				Map<String, SqlUniqueConstraintInfo> uniqueConstraintMap = null;
				if (!resolvedConstraints.isEmpty()) {
					uniqueConstraintMap = new LinkedHashMap<String, SqlUniqueConstraintInfo>();
					for (UniqueConstraint uca : resolvedConstraints) {
						String[] fieldNames = uca.value();
						String name = SqlUtils.generateUniqueConstraintName(tableName, fieldNames);
						if (fieldNames.length == 0) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_PROPERTY_REQUIRED_UNIQUECONSTRAINT,
									entityClass, name);
						}

						if (sqlDataSourceDialect.isAllObjectsInLowerCase()) {
							name = name.toLowerCase();
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

						List<String> fieldNameList = Arrays.asList(fieldNames);
						// Include tenant ID if necessary
						if (sqlDataSourceDialect.isTenancyEnabled() && tenantIdFieldInfo != null
								&& !fieldNameList.contains(tenantIdFieldInfo.getName())) {
							fieldNameList = new ArrayList<>(fieldNameList);
							fieldNameList.add(0, tenantIdFieldInfo.getName());
						}

						uniqueConstraintMap.put(name, new SqlUniqueConstraintInfo(name, fieldNameList));
					}
				}
				return uniqueConstraintMap;
			}

			private Map<String, SqlIndexInfo> extractIndexes(String tableName, Class<?> entityClass,
					List<Class<?>> heirachyList, Map<String, SqlFieldInfo> propertyInfoMap, Index[] indexes)
					throws UnifyException {
				// Indexes
				List<Index> resolvedIndexes = new ArrayList<Index>();
				for (Class<?> clazz : heirachyList) {
					Indexes ids = clazz.getAnnotation(Indexes.class);
					if (ids != null) {
						for (Index id : ids.value()) {
							resolvedIndexes.add(id);
						}
					}
				}

				for (Index id : indexes) {
					resolvedIndexes.add(id);
				}

				Map<String, SqlIndexInfo> indexMap = null;
				if (!resolvedIndexes.isEmpty()) {
					indexMap = new LinkedHashMap<String, SqlIndexInfo>();
					for (Index idxa : indexes) {
						String[] fieldNames = idxa.value();
						String name = SqlUtils.generateIndexName(tableName, fieldNames);
						if (fieldNames.length == 0) {
							throw new UnifyException(UnifyCoreErrorConstants.RECORD_PROPERTY_REQUIRED_INDEX,
									entityClass, name);
						}

						if (sqlDataSourceDialect.isAllObjectsInLowerCase()) {
							name = name.toLowerCase();
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

				return indexMap;
			}

		};

		sqlCallableInfoMap = new FactoryMap<Class<? extends CallableProc>, SqlCallableInfo>() {

			@Override
			protected SqlCallableInfo create(Class<? extends CallableProc> callableClass, Object... params)
					throws Exception {
				ReflectUtils.assertAnnotation(callableClass, Callable.class);
				Callable ca = callableClass.getAnnotation(Callable.class);

				String procedureName = ca.procedure();
				String preferredProcedureName = sqlDataSourceDialect.getPreferredName(procedureName);
				String schema = getWorkingSchema(AnnotationUtils.getAnnotationString(ca.schema()), ca.datasource());
				String schemaProcedureName = SqlUtils.generateFullSchemaElementName(schema, preferredProcedureName);

				// Parameters
				List<SqlCallableParamInfo> paramInfoList = null;
				String paramStr = AnnotationUtils.getAnnotationString(ca.params());
				if (StringUtils.isNotBlank(paramStr)) {
					paramInfoList = new ArrayList<SqlCallableParamInfo>();
					String[] fieldNames = StringUtils.commaSplit(paramStr);
					for (String fieldName : fieldNames) {
						fieldName = fieldName.trim();
						Field field = ReflectUtils.getField(callableClass, fieldName);

						CallableDataType type = null;
						boolean isInput = false;
						boolean isOutput = false;
						InParam ipa = field.getAnnotation(InParam.class);
						if (ipa != null) {
							type = resolveCallableDataType(ipa.value(), field);
							isInput = true;
						}

						OutParam opa = field.getAnnotation(OutParam.class);
						if (opa != null) {
							if (ipa != null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.CALLABLE_FIELD_MULTIPLE_PARAM_ANNOTATION, field);
							}

							type = resolveCallableDataType(opa.value(), field);
							isOutput = true;
						}

						InOutParam iopa = field.getAnnotation(InOutParam.class);
						if (iopa != null) {
							if (ipa != null || opa != null) {
								throw new UnifyException(
										UnifyCoreErrorConstants.CALLABLE_FIELD_MULTIPLE_PARAM_ANNOTATION, field);
							}

							type = resolveCallableDataType(iopa.value(), field);
							isInput = true;
							isOutput = true;
						}

						GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(callableClass,
								field.getName());

						paramInfoList.add(new SqlCallableParamInfo(type, fieldName, null, field,
								getterSetterInfo.getGetter(), getterSetterInfo.getSetter(), isInput, isOutput));
					}
				}

				// Results
				List<SqlCallableResultInfo> resultInfoList = null;
				if (ca.results().length > 0) {
					resultInfoList = new ArrayList<SqlCallableResultInfo>();
					for (Class<?> resultClass : ca.results()) {
						ReflectUtils.assertAnnotation(resultClass, CallableResult.class);
						List<SqlCallableFieldInfo> fieldInfoList = new ArrayList<SqlCallableFieldInfo>();
						CallableResult cra = resultClass.getAnnotation(CallableResult.class);
						String fieldsStr = AnnotationUtils.getAnnotationString(cra.fields());
						boolean useIndexing = StringUtils.isNotBlank(fieldsStr);
						if (useIndexing) {
							String[] fieldNames = StringUtils.commaSplit(fieldsStr);
							for (String fieldName : fieldNames) {
								fieldName = fieldName.trim();
								Field field = ReflectUtils.getField(resultClass, fieldName);
								addSqlCallableFieldInfo(fieldInfoList, resultClass, field);
							}

						} else {
							Class<?> searchClass = resultClass;
							do {
								Field[] fields = searchClass.getDeclaredFields();
								for (Field field : fields) {
									addSqlCallableFieldInfo(fieldInfoList, resultClass, field);
								}
							} while ((searchClass = searchClass.getSuperclass()) != null);
						}

						resultInfoList.add(new SqlCallableResultInfo(resultClass, fieldInfoList, useIndexing));
					}
				}

				SqlCallableFieldInfo returnValueInfo = null;
				CallableDataType returnDataType = ca.returnType();
				if (!CallableDataType.AUTO.equals(returnDataType)) {
					GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(callableClass, "returnValue");
					returnValueInfo = new SqlCallableFieldInfo(returnDataType, "returnValue", null, null,
							getterSetterInfo.getGetter(), getterSetterInfo.getSetter());
				}

				return new SqlCallableInfo(callableClass, procedureName, preferredProcedureName, schemaProcedureName,
						paramInfoList, resultInfoList, returnValueInfo);
			}

			private void addSqlCallableFieldInfo(List<SqlCallableFieldInfo> fieldInfoList, Class<?> resultClass,
					Field field) throws UnifyException {
				ResultField ra = field.getAnnotation(ResultField.class);
				if (ra != null) {
					CallableDataType dataType = resolveCallableDataType(ra.value(), field);
					GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(resultClass, field.getName());
					String columnName = AnnotationUtils.getAnnotationString(ra.column());
					if (StringUtils.isBlank(columnName)) {
						columnName = SqlUtils.generateSchemaElementName(field.getName(), true);
					}
					fieldInfoList.add(new SqlCallableFieldInfo(dataType, field.getName(), columnName, field,
							getterSetterInfo.getGetter(), getterSetterInfo.getSetter()));
				}
			}

		};
	}

	public void setSqlOrderColumns(boolean sqlOrderColumns) {
		this.sqlOrderColumns = sqlOrderColumns;
	}

	public void setSqlGenerationApplySpacing(boolean sqlGenerationApplySpacing) {
		this.sqlGenerationApplySpacing = sqlGenerationApplySpacing;
	}

	@Override
	public void setSqlDataSourceDialect(SqlDataSourceDialect sqlDataSourceDialect) {
		this.sqlDataSourceDialect = sqlDataSourceDialect;
	}

	@Override
	public SqlEntityInfo findSqlEntityInfo(Class<?> entityClass) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = sqlEntityInfoMap.find(entityClass);
		if (sqlEntityInfo == null) {
			if (entityClass.isAnnotationPresent(Table.class)) {
				if (entityClass.getAnnotation(Table.class).adhoc()) {
					return sqlEntityInfoMap.get(entityClass);
				}
			}

			if (entityClass.isAnnotationPresent(TableExt.class)) {
				if (entityClass.getAnnotation(TableExt.class).adhoc()) {
					return sqlEntityInfoMap.get(entityClass);
				}
			}

			if (entityClass.isAnnotationPresent(TableName.class)) {
				return sqlEntityInfoMap.get(entityClass);
			}

			throw new UnifyException(UnifyCoreErrorConstants.SQLENTITYINFOFACTORY_ENTITYINFO_NOT_FOUND, entityClass);
		}

		return sqlEntityInfo;
	}

	@Override
	public SqlEntityInfo createSqlEntityInfo(Class<?> entityClass) throws UnifyException {
		return sqlEntityInfoMap.get(entityClass);
	}

	@Override
	public SqlEntityInfo removeSqlEntityInfo(Class<?> entityClass) throws UnifyException {
		return sqlEntityInfoMap.remove(entityClass);
	}

	@Override
	public SqlCallableInfo getSqlCallableInfo(Class<? extends CallableProc> callableClass) throws UnifyException {
		return sqlCallableInfoMap.get(callableClass);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private String getWorkingSchema(String schema, String dataSource) throws UnifyException {
		if (StringUtils.isBlank(schema)) {
			UnifyComponentConfig ucc = getComponentConfig(NameSqlDataSourceSchema.class, dataSource);
			if (ucc != null) {
				schema = (String) ucc.getSettings().getSettingValue("appSchema");
			}
		}

		return schema;
	}

	private CallableDataType resolveCallableDataType(CallableDataType type, Field field) throws UnifyException {
		if (CallableDataType.AUTO.equals(type)) {
			ColumnType columnType = DataUtils.getColumnType(field.getType());
			if (columnType == null) {
				throw new UnifyException(UnifyCoreErrorConstants.CALLABLE_DATATYPE_UNSUPPORTED, field);
			}

			return CallableDataType.fromCode(columnType.code());
		}

		return type;
	}

	private void checkChildCategoryRule(Class<?> entityClass, String category, Field field, Class<?> childType,
			Map<Class<?>, Set<String>> childTypeCategoryInfo) throws UnifyException {
		Set<String> cats = childTypeCategoryInfo.get(childType);
		if (cats == null) {
			if (StringUtils.isBlank(category)) {
				if (childTypeCategoryInfo.containsKey(childType)) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_CHILD_REFERENCE_NO_CATEGORY,
							entityClass, field, childType);
				}
			} else {
				if (childTypeCategoryInfo.containsKey(childType)) {
					throw new UnifyException(
							UnifyCoreErrorConstants.RECORD_MULTIPLE_CHILD_REFERENCE_NO_CATEGORY_WITH_CATEGORY,
							entityClass, field, childType);
				}
				cats = new HashSet<String>();
				cats.add(category);
			}

			childTypeCategoryInfo.put(childType, cats);
			return;
		}

		if (StringUtils.isBlank(category)) {
			if (childTypeCategoryInfo.containsKey(childType)) {
				throw new UnifyException(
						UnifyCoreErrorConstants.RECORD_MULTIPLE_CHILD_REFERENCE_NO_CATEGORY_WITH_CATEGORY, entityClass,
						field, childType);
			}
		}

		if (cats.contains(category)) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_CHILD_REFERENCE_WITH_SAME_CATEGORY,
					entityClass, field, childType, category);
		}

		cats.add(category);
	}

	private ChildFieldInfo getChildFieldInfo(Class<?> parentClass, String category, Field childField,
			Class<? extends Entity> childClass, ChildFkFields childFkFields, boolean list) throws UnifyException {
		boolean idNumber = Number.class.isAssignableFrom(ReflectUtils.getGetterInfo(childClass, "id").getType());
		GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(parentClass, childField.getName());
		Method childFkIdSetter = ReflectUtils.getGetterSetterInfo(childClass, childFkFields.getFkIdField().getName())
				.getSetter();
		Method childFkTypeSetter = childFkFields.getFkTypeField() != null
				? ReflectUtils.getGetterSetterInfo(childClass, childFkFields.getFkTypeField().getName()).getSetter()
				: null;
		Method childCatSetter = childFkFields.getCategoryField() != null
				? ReflectUtils.getGetterSetterInfo(childClass, childFkFields.getCategoryField().getName()).getSetter()
				: null;
		return new ChildFieldInfo(childClass, category, childFkFields.getFkIdField(), childFkIdSetter,
				childFkFields.getFkTypeField(), childFkTypeSetter, childFkFields.getCategoryField(), childCatSetter,
				childField, getterSetterInfo.getGetter(), getterSetterInfo.getSetter(), list, idNumber);
	}

	private ChildFkFields getFosterParentChildFkFields(Class<?> argumentType) throws UnifyException {
		Class<?> searchClass = argumentType;
		Field fkIdField = null;
		Field fkTypeField = null;
		Field categoryField = null;
		do {
			Field[] fields = searchClass.getDeclaredFields();
			for (Field fld : fields) {
				if (fkIdField == null && fld.getAnnotation(FosterParentId.class) != null) {
					fkIdField = fld;
				} else if (fkTypeField == null && fld.getAnnotation(FosterParentType.class) != null) {
					fkTypeField = fld;
				} else if (categoryField == null && fld.getAnnotation(CategoryColumn.class) != null) {
					categoryField = fld;
				}
			}
		} while ((searchClass = searchClass.getSuperclass()) != null);

		if (fkIdField != null && fkTypeField != null) {
			return new ChildFkFields(fkIdField, fkTypeField, categoryField);
		}

		return null;
	}

	private ChildFkFields getAttributeOnlyChildFkFields(Class<?> entityClass, Class<?> argumentType)
			throws UnifyException {
		Class<?> searchClass = argumentType;
		Field fkIdField = null;
		Field categoryField = null;
		do {
			Field[] fields = searchClass.getDeclaredFields();
			for (Field fld : fields) {
				ForeignKey fka = fld.getAnnotation(ForeignKey.class);
				if (fkIdField == null && fka != null && fka.childKey()) {
					if (entityClass.equals(fka.value()) || entityClass.equals(fka.type())) {
						fkIdField = fld;
						continue;
					}
				}

				if (categoryField == null && fld.getAnnotation(CategoryColumn.class) != null) {
					categoryField = fld;
				}
			}
		} while ((searchClass = searchClass.getSuperclass()) != null);

		if (fkIdField != null) {
			return new ChildFkFields(fkIdField, categoryField);
		}

		return null;
	}

	private class EntityCycleDetector extends CycleDetector<Class<?>> {

	}

	private class ChildFkFields {

		private Field fkIdField;

		private Field fkTypeField;

		private Field categoryField;

		public ChildFkFields(Field fkIdField, Field fkTypeField, Field categoryField) {
			this.fkIdField = fkIdField;
			this.fkTypeField = fkTypeField;
			this.categoryField = categoryField;
		}

		public ChildFkFields(Field fkIdField, Field categoryField) {
			this.fkIdField = fkIdField;
			this.categoryField = categoryField;
		}

		public Field getFkIdField() {
			return fkIdField;
		}

		public Field getFkTypeField() {
			return fkTypeField;
		}

		public Field getCategoryField() {
			return categoryField;
		}

		public boolean isWithCategory() {
			return categoryField != null;
		}
	}

	private class TableReferences {

		private String primaryAlias;

		private Class<?> entityClass;

		private Map<String, Class<?>> tables;

		public TableReferences(Class<?> entityClass) throws UnifyException {
			ReflectUtils.assertAnnotation(entityClass, View.class);
			View va = entityClass.getAnnotation(View.class);
			if (va.tables().length == 0) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_AT_LEAST_ONE_TABLE, entityClass);
			}

			this.primaryAlias = va.primaryAlias().toUpperCase();
			this.entityClass = entityClass;
			tables = new LinkedHashMap<String, Class<?>>();
			for (TableRef tableRef : va.tables()) {
				String tableAlias = tableRef.alias().toUpperCase();
				if (StringUtils.isBlank(tableAlias)) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_TABLE_ALIAS_BLANK, entityClass);
				}

				if (tables.containsKey(tableAlias)) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_MULTIPLE_TABLEREF_WITH_ALIAS,
							entityClass, tableAlias);
				}

				tables.put(tableAlias, tableRef.entity());
			}

			tables = Collections.unmodifiableMap(tables);
		}

		public Class<?> getEntityClass(String tableAlias) throws UnifyException {
			Class<?> entityType = tables.get(tableAlias.toUpperCase());
			if (entityType == null) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_UNKNOWN_TABLEREF_WITH_ALIAS, entityClass,
						tableAlias);
			}

			return entityType;
		}

		public PropertyRef getPropertyRef(String property) {
			// Default assumes reference to primary table property
			String tableAlias = primaryAlias;
			String fieldName = property;

			// Check if long name
			String[] parts = StringUtils.dotSplit(property);
			if (parts.length == 2) {
				tableAlias = parts[0];
				fieldName = parts[1];
			}

			return new PropertyRef(tableAlias.toUpperCase(), fieldName);
		}

		public Map<String, Class<?>> getBaseTables() {
			return tables;
		}

	}

	private class PropertyRef {

		private String tableAlias;
		private String fieldName;

		public PropertyRef(String tableAlias, String fieldName) {
			this.tableAlias = tableAlias;
			this.fieldName = fieldName;
		}

		public String getTableAlias() {
			return tableAlias;
		}

		public String getFieldName() {
			return fieldName;
		}
	}
}
