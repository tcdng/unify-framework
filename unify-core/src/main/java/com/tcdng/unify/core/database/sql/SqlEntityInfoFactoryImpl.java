/*
 * Copyright 2018-2019 The Code Department.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Callable;
import com.tcdng.unify.core.annotation.CallableDataType;
import com.tcdng.unify.core.annotation.CallableResult;
import com.tcdng.unify.core.annotation.Child;
import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ColumnOverride;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ForeignKeyOverride;
import com.tcdng.unify.core.annotation.Id;
import com.tcdng.unify.core.annotation.InOutParam;
import com.tcdng.unify.core.annotation.InParam;
import com.tcdng.unify.core.annotation.Index;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.OutParam;
import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.annotation.ResultField;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.TableRef;
import com.tcdng.unify.core.annotation.UniqueConstraint;
import com.tcdng.unify.core.annotation.Version;
import com.tcdng.unify.core.annotation.View;
import com.tcdng.unify.core.annotation.ViewRestriction;
import com.tcdng.unify.core.constant.DefaultColumnPositionConstants;
import com.tcdng.unify.core.constant.EnumConst;
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
 * @author Lateef Ojulari
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

                Table ta = entityClass.getAnnotation(Table.class);
                View va = entityClass.getAnnotation(View.class);
                if (ta != null && va != null) {
                    throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_TABLE_VIEW_ANNOTATION_COMBO,
                            entityClass);
                }

                if (ta == null && va == null) {
                    throw new UnifyException(UnifyCoreErrorConstants.RECORD_NO_TABLE_OR_VIEW_ANNOTATION, entityClass);
                }

                // Tables
                if (ta != null) {
                    return createTableEntityInfo(entityClass, entityCycleDetector);
                }

                // Views
                return createViewEntityInfo(entityClass, entityCycleDetector);
            }

            @SuppressWarnings("unchecked")
            private SqlEntityInfo createEnumConstEntityInfo(Class<?> entityClass) throws Exception {
                String tableName =
                        ENUM_TABLE_PREFIX + SqlUtils.generateSchemaElementName(entityClass.getSimpleName(), false);

                String preferredTableName = sqlDataSourceDialect.getPreferredName(tableName);
                String schema = (String) getComponentConfig(NameSqlDataSourceSchema.class,
                        ApplicationComponents.APPLICATION_DATASOURCE).getSettings().getSettingValue("appSchema");
                String schemaTableName = SqlUtils.generateFullSchemaElementName(schema, preferredTableName);

                SqlFieldDimensions sqlFieldDimensions = new SqlFieldDimensions(StaticReference.CODE_LENGTH, -1, -1);
                Map<String, SqlFieldInfo> propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
                GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(StaticReference.class, "code");
                boolean isForeignKey = false;
                boolean isIgnoreFkConstraint = false;
                boolean isListOnly = false;
                SqlFieldInfo idFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.ID_POSITION,
                        ColumnType.STRING, null, null, null, "code", "REF_CD",
                        sqlDataSourceDialect.getPreferredName("REF_CD"), null, null, true, isForeignKey, isListOnly,
                        isIgnoreFkConstraint, null, sqlFieldDimensions, false, null,
                        ReflectUtils.getField(StaticReference.class, "code"), getterSetterInfo.getGetter(),
                        getterSetterInfo.getSetter(), sqlDataSourceDialect.isAllObjectsInLowerCase());

                sqlFieldDimensions = new SqlFieldDimensions(StaticReference.DESCRIPTION_LENGTH, -1, -1);
                getterSetterInfo = ReflectUtils.getGetterSetterInfo(StaticReference.class, "description");
                SqlFieldInfo descFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.COLUMN_POSITION,
                        ColumnType.STRING, null, null, null, "description", "REF_DESC",
                        sqlDataSourceDialect.getPreferredName("REF_DESC"), null, null, false, isForeignKey, isListOnly,
                        isIgnoreFkConstraint, null, sqlFieldDimensions, false, null,
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
                        preferredTableName, schemaTableName, idFieldInfo, null, propertyInfoMap, null, null, null, null,
                        null, null, null, sqlDataSourceDialect.isAllObjectsInLowerCase());
            }

            @SuppressWarnings("unchecked")
            private SqlEntityInfo createTableEntityInfo(Class<?> entityClass, EntityCycleDetector entityCycleDetector)
                    throws Exception {
                Table ta = entityClass.getAnnotation(Table.class);
                String tableName = AnnotationUtils.getAnnotationString(ta.name());
                if (StringUtils.isBlank(tableName)) {
                    tableName = AnnotationUtils.getAnnotationString(ta.value());
                } else if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(ta.value()))) {
                    throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_BAD_ATTRIBUTE_COMBINATION, "value",
                            "name", Table.class, entityClass);
                }

                if (tableName == null) {
                    tableName =
                            SqlUtils.generateSchemaElementName(entityClass.getSimpleName(), sqlGenerationApplySpacing);
                }

                String preferredTableName = sqlDataSourceDialect.getPreferredName(tableName);

                String viewName = AnnotationUtils.getAnnotationString(ta.view());
                if (viewName == null) {
                    viewName = tableName;
                }

                String schema = getWorkingSchema(AnnotationUtils.getAnnotationString(ta.schema()), ta.datasource());
                String schemaTableName = SqlUtils.generateFullSchemaElementName(schema, preferredTableName);

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
                List<ChildFieldInfo> childInfoList = new ArrayList<ChildFieldInfo>();
                List<ChildFieldInfo> childListInfoList = new ArrayList<ChildFieldInfo>();
                SqlFieldInfo idFieldInfo = null;
                SqlFieldInfo versionFieldInfo = null;
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
                        String defaultVal = null;

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
                        Child clda = field.getAnnotation(Child.class);
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

                            if (va != null || ca != null || loa != null || fka != null || clda != null || cla != null) {
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

                            if (ca != null || loa != null || fka != null || clda != null || cla != null) {
                                throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
                                        searchClass, field);
                            }
                        }

                        // Process column
                        if (ca != null) {
                            isPersistent = true;
                            position = ca.position();
                            defaultVal = AnnotationUtils.getAnnotationString(ca.defaultVal());

                            if (loa != null || fka != null || clda != null || cla != null) {
                                throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
                                        searchClass, field);
                            }

                            ColumnOverride coa = colOverrideMap.get(field.getName());
                            if (coa != null) {
                                if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(coa.name()))) {
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
                        }

                        // Process foreign key
                        if (fka != null) {
                            position = fka.position();
                            defaultVal = AnnotationUtils.getAnnotationString(fka.defaultVal());

                            if (loa != null || clda != null || cla != null) {
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
                            if (clda != null || cla != null) {
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
                            if (cla != null) {
                                throw new UnifyException(UnifyCoreErrorConstants.RECORD_INVALID_ANNOTATION_COMBO,
                                        searchClass, field);
                            }

                            Class<?> childType = field.getType();
                            Field attrFkField = getAttributeOnlyForeignKeyField(entityClass, childType);
                            if (attrFkField == null) {
                                throw new UnifyException(UnifyCoreErrorConstants.RECORD_CHILD_NO_MATCHING_FK, field,
                                        childType);
                            }

                            childInfoList.add(getChildFieldInfo(entityClass, field, (Class<? extends Entity>) childType,
                                    attrFkField, false));
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
                                throw new UnifyException(UnifyCoreErrorConstants.RECORD_CHILDLIST_NO_MATCHING_FK, field,
                                        argumentType);
                            }

                            childListInfoList.add(getChildFieldInfo(entityClass, field,
                                    (Class<? extends Entity>) argumentType, attrFkField, true));
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
                            }

                            SqlFieldDimensions sqlFieldDimensions =
                                    SqlUtils.getNormalizedSqlFieldDimensions(columnType, length, precision, scale);
                            boolean isIgnoreFkConstraint = false;
                            boolean isListOnly = false;
                            GetterSetterInfo getterSetterInfo =
                                    ReflectUtils.getGetterSetterInfo(searchClass, field.getName());
                            SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(position, columnType, foreignEntityInfo,
                                    foreignFieldInfo, null, field.getName(), column,
                                    sqlDataSourceDialect.getPreferredName(column), constraintName, null, isPrimaryKey,
                                    isForeignKey, isListOnly, isIgnoreFkConstraint, transformer, sqlFieldDimensions,
                                    isNullable, defaultVal, field, getterSetterInfo.getGetter(),
                                    getterSetterInfo.getSetter(), sqlDataSourceDialect.isAllObjectsInLowerCase());

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

                String preferredViewName = sqlDataSourceDialect.getPreferredName(viewName);

                String schemaViewName = SqlUtils.generateFullSchemaElementName(schema, preferredViewName);

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

                        SqlFieldDimensions sqlFieldDimensions = new SqlFieldDimensions(foreignFieldInfo.getLength(),
                                foreignFieldInfo.getPrecision(), foreignFieldInfo.getScale());
                        GetterSetterInfo getterSetterInfo =
                                ReflectUtils.getGetterSetterInfo(entry.getKey(), field.getName());
                        boolean isPrimaryKey = false;
                        boolean isForeignKey = false;
                        boolean isIgnoreFkConstraint = false;
                        boolean isListOnly = true;
                        SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.LIST_POSITION,
                                foreignFieldInfo.getColumnType(), foreignEntityInfo, foreignFieldInfo,
                                foreignKeySQLFieldInfo, field.getName(), column,
                                sqlDataSourceDialect.getPreferredName(column), null, null, isPrimaryKey, isForeignKey,
                                isListOnly, isIgnoreFkConstraint, foreignFieldInfo.getTransformer(), sqlFieldDimensions,
                                foreignFieldInfo.isNullable(), null, field, getterSetterInfo.getGetter(),
                                getterSetterInfo.getSetter(), sqlDataSourceDialect.isAllObjectsInLowerCase());

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

                EntityPolicy entityPolicy = null;
                Policy pa = entityClass.getAnnotation(Policy.class);
                if (pa != null) {
                    entityPolicy = (EntityPolicy) getComponent(pa.value());
                }

                // Unique constraints
                Map<String, SqlUniqueConstraintInfo> uniqueConstraintMap = null;
                if (ta.uniqueConstraints().length > 0) {
                    uniqueConstraintMap = new LinkedHashMap<String, SqlUniqueConstraintInfo>();
                    for (UniqueConstraint uca : ta.uniqueConstraints()) {
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

                        uniqueConstraintMap.put(name, new SqlUniqueConstraintInfo(name, Arrays.asList(fieldNames)));
                    }
                }

                // Indexes
                Map<String, SqlIndexInfo> indexMap = null;
                if (ta.indexes().length > 0) {
                    indexMap = new LinkedHashMap<String, SqlIndexInfo>();
                    for (Index idxa : ta.indexes()) {
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

                if (sqlOrderColumns) {
                    List<SqlFieldInfo> tempList = new ArrayList<SqlFieldInfo>(propertyInfoMap.values());
                    DataUtils.sort(tempList, SqlFieldInfo.class, "columnName", true);
                    DataUtils.sort(tempList, SqlFieldInfo.class, "orderIndex", true);

                    propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
                    for (SqlFieldInfo sqlFieldInfo : tempList) {
                        propertyInfoMap.put(sqlFieldInfo.getName(), sqlFieldInfo);
                    }
                }

                String tableAlias = "T" + (++tAliasCounter);
                SqlEntityInfo sqlEntityInfo = new SqlEntityInfo(null, (Class<? extends Entity>) entityClass, null,
                        entityPolicy, schema, tableName, preferredTableName, schemaTableName, tableAlias, viewName,
                        preferredViewName, schemaViewName, idFieldInfo, versionFieldInfo, propertyInfoMap,
                        childInfoList, childListInfoList, uniqueConstraintMap, indexMap, null, null, null,
                        sqlDataSourceDialect.isAllObjectsInLowerCase());
                return sqlEntityInfo;
            }

            @SuppressWarnings("unchecked")
            private SqlEntityInfo createViewEntityInfo(Class<?> entityClass, EntityCycleDetector entityCycleDetector)
                    throws Exception {
                View va = entityClass.getAnnotation(View.class);
                String viewName = AnnotationUtils.getAnnotationString(va.name());
                if (StringUtils.isBlank(viewName)) {
                    viewName = VIEW_PREFIX + SqlUtils.generateSchemaElementName(entityClass.getSimpleName(),
                            sqlGenerationApplySpacing);
                }

                String preferredViewName = sqlDataSourceDialect.getPreferredName(viewName);

                String schema = getWorkingSchema(AnnotationUtils.getAnnotationString(va.schema()), va.datasource());
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
                        Column ca = field.getAnnotation(Column.class);
                        if (ca != null) {
                            throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_UNSUPPORTED_ANNOTATION,
                                    Column.class, searchClass, field.getName());
                        }

                        Version vsa = field.getAnnotation(Version.class);
                        if (vsa != null) {
                            throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_UNSUPPORTED_ANNOTATION,
                                    Version.class, searchClass, field.getName());
                        }

                        ForeignKey fka = field.getAnnotation(ForeignKey.class);
                        if (fka != null) {
                            throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_UNSUPPORTED_ANNOTATION,
                                    ForeignKey.class, searchClass, field.getName());
                        }

                        Child clda = field.getAnnotation(Child.class);
                        if (clda != null) {
                            throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_UNSUPPORTED_ANNOTATION,
                                    Child.class, searchClass, field.getName());
                        }

                        ChildList cla = field.getAnnotation(ChildList.class);
                        if (cla != null) {
                            throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_UNSUPPORTED_ANNOTATION,
                                    ChildList.class, searchClass, field.getName());
                        }

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
                            refEntityInfo =
                                    get(tableReferences.getEntityClass(refEntityPreferredAlias), entityCycleDetector);
                            refFieldInfo = refEntityInfo.getIdFieldInfo();
                        }

                        if (loa != null) {
                            column = AnnotationUtils.getAnnotationString(loa.name());
                            PropertyRef propertyRef = tableReferences.getPropertyRef(loa.property());
                            refEntityPreferredAlias = propertyRef.getTableAlias();
                            refEntityInfo =
                                    get(tableReferences.getEntityClass(refEntityPreferredAlias), entityCycleDetector);
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
                        GetterSetterInfo getterSetterInfo =
                                ReflectUtils.getGetterSetterInfo(searchClass, field.getName());
                        boolean isForeignKey = false;
                        boolean isIgnoreFkConstraint = false;
                        boolean isListOnly = true;
                        SqlFieldInfo sqlFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.LIST_POSITION,
                                refFieldInfo.getColumnType(), refEntityInfo, refFieldInfo, null, field.getName(),
                                column, sqlDataSourceDialect.getPreferredName(column), null,
                                refEntityPreferredAlias.toUpperCase(), isPrimaryKey, isForeignKey, isListOnly,
                                isIgnoreFkConstraint, refFieldInfo.getTransformer(), sqlFieldDimensions, false, null,
                                field, getterSetterInfo.getGetter(), getterSetterInfo.getSetter(),
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
                            case NOT_EQUAL:
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
                                param1 = DataUtils.convert(List.class, String.class, vra.values(), null);
                                break;
                            case AND:
                            case OR:
                                throw new UnifyException(
                                        UnifyCoreErrorConstants.RECORD_VIEW_COMPOUND_RESTRICTION_UNSUPPORTED,
                                        entityClass);
                            default:
                                break;
                        }

                        SqlViewColumnInfo columnInfo =
                                getSqlViewColumnInfo(tableReferences, entityCycleDetector, vra.leftProperty());
                        viewRestrictionList
                                .add(new SqlViewRestrictionInfo(columnInfo, restrictionType, param1, param2));
                    }
                }

                SqlEntityInfo sqlEntityInfo = new SqlEntityInfo(null, (Class<? extends Entity>) entityClass, null, null,
                        schema, viewName, preferredViewName, schemaViewName, null, viewName, preferredViewName,
                        schemaViewName, idFieldInfo, null, propertyInfoMap, null, null, null, null, null,
                        tableReferences.getBaseTables(), viewRestrictionList,
                        sqlDataSourceDialect.isAllObjectsInLowerCase());
                return sqlEntityInfo;
            }

            private SqlViewColumnInfo getSqlViewColumnInfo(TableReferences tableReferences,
                    EntityCycleDetector entityCycleDetector, String property) throws UnifyException {
                PropertyRef propertyRef = tableReferences.getPropertyRef(property);
                SqlEntityInfo sqlEntityInfo =
                        get(tableReferences.getEntityClass(propertyRef.getTableAlias()), entityCycleDetector);
                String columnName = sqlEntityInfo.getFieldInfo(propertyRef.getFieldName()).getPreferredColumnName();
                return new SqlViewColumnInfo(propertyRef.getTableAlias(), columnName);
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

                        GetterSetterInfo getterSetterInfo =
                                ReflectUtils.getGetterSetterInfo(callableClass, field.getName());

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

    @Override
    public void setSqlDataSourceDialect(SqlDataSourceDialect sqlDataSourceDialect) {
        this.sqlDataSourceDialect = sqlDataSourceDialect;
    }

    @Override
    public SqlEntityInfo getSqlEntityInfo(Class<?> entityClass) throws UnifyException {
        return sqlEntityInfoMap.get(entityClass);
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

    private ChildFieldInfo getChildFieldInfo(Class<?> parentClass, Field childField, Class<? extends Entity> childClass,
            Field childFkField, boolean list) throws UnifyException {
        GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(parentClass, childField.getName());
        GetterSetterInfo attrFkGetterSetterInfo = ReflectUtils.getGetterSetterInfo(childClass, childFkField.getName());
        return new ChildFieldInfo(childClass, childFkField, attrFkGetterSetterInfo.getSetter(), childField,
                getterSetterInfo.getGetter(), getterSetterInfo.getSetter(), list);
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
