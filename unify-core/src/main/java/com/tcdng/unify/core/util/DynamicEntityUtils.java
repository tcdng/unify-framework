/*
 * Copyright 2018-2020 The Code Department.
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.DynamicEntityType;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.TableExt;
import com.tcdng.unify.core.database.dynamic.DynamicColumnFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.dynamic.DynamicFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicForeignKeyFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicListOnlyFieldInfo;

/**
 * Dynamic entity utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class DynamicEntityUtils {

    private DynamicEntityUtils() {

    }

    public static String generateEntityJavaClassSource(DynamicEntityInfo dynamicEntityInfo) throws UnifyException {
        switch (dynamicEntityInfo.getType()) {
            case TABLE:
            case TABLE_EXT:
                return DynamicEntityUtils.generateTableEntityJavaClassSource(dynamicEntityInfo);
            case VIEW:
            case INFO_ONLY:
                throw new UnifyOperationException(DynamicEntityUtils.class,
                        "View or information-only entity type is unsupported for class source generation.");
            default:
                throw new UnifyOperationException(DynamicEntityUtils.class,
                        "Entity type not specified for class source generation.");
        }
    }

    private static String generateTableEntityJavaClassSource(DynamicEntityInfo dynamicEntityInfo)
            throws UnifyException {
        StringBuilder esb = new StringBuilder();
        StringBuilder fsb = new StringBuilder();
        StringBuilder msb = new StringBuilder();
        Set<String> importSet = new HashSet<String>();

        // Evaluate fields
        Set<String> fieldNames = new HashSet<String>();
        for (DynamicFieldInfo dynamicFieldInfo : dynamicEntityInfo.getFieldInfos()) {
            fieldNames.add(dynamicFieldInfo.getFieldName());
            if (dynamicFieldInfo.isGeneration()) {
                TypeInfo enumEntityInfo = null;
                if (dynamicFieldInfo.isEnum()) {
                    enumEntityInfo = new TypeInfo(dynamicFieldInfo.getEnumClassName());
                    importSet.add(dynamicFieldInfo.getEnumClassName());
                }

                if (dynamicFieldInfo.getFieldType().isForeignKey()) {
                    DynamicForeignKeyFieldInfo fkInfo = (DynamicForeignKeyFieldInfo) dynamicFieldInfo;
                    DynamicEntityUtils.generateForeignKeyAnnotation(fsb, fkInfo);
                    importSet.add(ForeignKey.class.getCanonicalName());
                    if (!fkInfo.isEnum()) {
                        importSet.add(fkInfo.getParentDynamicEntityInfo().getClassName());
                    }

                } else if (dynamicFieldInfo.getFieldType().isTableColumn()) {
                    DynamicEntityUtils.generateColumnAnnotation(fsb, (DynamicColumnFieldInfo) dynamicFieldInfo);
                    if (!DataUtils.isMappedColumnType(dynamicFieldInfo.getDataType().columnType())) {
                        importSet.add(ColumnType.class.getCanonicalName());
                    }

                    importSet.add(Column.class.getCanonicalName());
                } else {
                    DynamicEntityUtils.generateLisOnlyAnnotation(fsb, (DynamicListOnlyFieldInfo) dynamicFieldInfo);
                    importSet.add(ListOnly.class.getCanonicalName());
                }

                final Class<?> javaClass = dynamicFieldInfo.getDataType().javaClass();
                if (Date.class.equals(javaClass)) {
                    importSet.add(Date.class.getCanonicalName());
                } else if (BigDecimal.class.equals(javaClass)) {
                    importSet.add(BigDecimal.class.getCanonicalName());
                }

                final String fieldName = dynamicFieldInfo.getFieldName();
                final String simpleName = enumEntityInfo != null ? enumEntityInfo.getSimpleName()
                        : javaClass.getSimpleName();
                fsb.append(" private ").append(simpleName).append(" ").append(fieldName).append(";\n");

                String capField = StringUtils.capitalizeFirstLetter(fieldName);
                msb.append(" public ").append(simpleName).append(" get").append(capField).append("() {return ")
                        .append(fieldName).append(";}\n");
                msb.append(" public void set").append(capField).append("(").append(simpleName).append(" ").append(fieldName)
                        .append(") {this.").append(fieldName).append(" = ").append(fieldName).append(";}\n");
            }
        }

        // Construct class
        TypeInfo baseEntityInfo = new TypeInfo(dynamicEntityInfo.getBaseClassName());
        TypeInfo typeInfo = new TypeInfo(dynamicEntityInfo.getClassName());
        esb.append("package ").append(typeInfo.getPackageName()).append(";\n");
        List<String> importList = new ArrayList<String>(importSet);
        Collections.sort(importList);
        for (String imprt : importList) {
            esb.append("import ").append(imprt).append(";\n");
        }

        esb.append("import ").append(baseEntityInfo.getCanonicalName()).append(";\n");

        if (DynamicEntityType.TABLE.equals(dynamicEntityInfo.getType())) {
            esb.append("import ").append(Table.class.getCanonicalName()).append(";\n");
            esb.append("@Table(\"").append(dynamicEntityInfo.getTableName()).append("\")\n");
        } else {
            esb.append("import ").append(TableExt.class.getCanonicalName()).append(";\n");
            esb.append("@TableExt\n");
        }

        esb.append("public class ").append(typeInfo.getSimpleName()).append(" extends ")
                .append(baseEntityInfo.getSimpleName()).append(" {\n");
        esb.append(fsb);
        if (!fieldNames.contains("description")) {
            esb.append(" public String getDescription(){return null;}\n");
        }

        esb.append(msb);
        esb.append("}\n");
        return esb.toString();
    }

    private static void generateForeignKeyAnnotation(StringBuilder fsb,
            DynamicForeignKeyFieldInfo dynamicForeignKeyFieldInfo) {
        if (dynamicForeignKeyFieldInfo.isEnum()) {
            fsb.append(" @ForeignKey(");
        } else {
            fsb.append(" @ForeignKey(type = ")
                    .append(dynamicForeignKeyFieldInfo.getParentDynamicEntityInfo().getClassName()).append(".class");
        }

        if (!StringUtils.isBlank(dynamicForeignKeyFieldInfo.getColumnName())) {
            fsb.append(", name = \"").append(dynamicForeignKeyFieldInfo.getColumnName()).append("\"");
        }

        if (dynamicForeignKeyFieldInfo.isNullable()) {
            fsb.append(", nullable = true");
        }

        fsb.append(")\n");
    }

    private static void generateColumnAnnotation(StringBuilder fsb, DynamicColumnFieldInfo dynamicColumnFieldInfo) {
        if (dynamicColumnFieldInfo.isEnum()) {
            fsb.append(" @Column");
            boolean appendSym = false;
            if (!StringUtils.isBlank(dynamicColumnFieldInfo.getColumnName())) {
                fsb.append("(name = \"").append(dynamicColumnFieldInfo.getColumnName()).append("\"");
                appendSym = true;
            }
            
            if (dynamicColumnFieldInfo.isNullable()) {
                if (appendSym) {
                    fsb.append(", ");
                } else {
                    fsb.append("(");
                }
                
                fsb.append("nullable = true");
                appendSym = true;
            }

            if (appendSym) {
                fsb.append(")");
            }
            
            fsb.append("\n");
            return;
        }

        fsb.append(" @Column");
        boolean appendSym = false;
        if (!DataUtils.isMappedColumnType(dynamicColumnFieldInfo.getDataType().columnType())) {
            fsb.append("(type = ColumnType.").append(dynamicColumnFieldInfo.getDataType().columnType());
            appendSym = true;
        }

        if (!StringUtils.isBlank(dynamicColumnFieldInfo.getColumnName())) {
            if (appendSym) {
                fsb.append(", ");
            } else {
                fsb.append("(");
            }

            fsb.append("name = \"").append(dynamicColumnFieldInfo.getColumnName()).append("\"");
            appendSym = true;
        }

        if (!StringUtils.isBlank(dynamicColumnFieldInfo.getTransformer())) {
            if (appendSym) {
                fsb.append(", ");
            } else {
                fsb.append("(");
            }

            fsb.append("transformer = \"").append(dynamicColumnFieldInfo.getTransformer()).append("\"");
            appendSym = true;
        }

        if (!StringUtils.isBlank(dynamicColumnFieldInfo.getDefaultVal())) {
            if (appendSym) {
                fsb.append(", ");
            } else {
                fsb.append("(");
            }

            fsb.append("defaultVal = \"").append(dynamicColumnFieldInfo.getDefaultVal()).append("\"");
            appendSym = true;
        }

        if (dynamicColumnFieldInfo.getLength() > 0) {
            if (appendSym) {
                fsb.append(", ");
            } else {
                fsb.append("(");
            }

            fsb.append("length = ").append(dynamicColumnFieldInfo.getLength());
            appendSym = true;
        }

        if (dynamicColumnFieldInfo.getPrecision() > 0) {
            if (appendSym) {
                fsb.append(", ");
            } else {
                fsb.append("(");
            }

            fsb.append("precision = ").append(dynamicColumnFieldInfo.getPrecision());
            appendSym = true;
        }

        if (dynamicColumnFieldInfo.getScale() > 0) {
            if (appendSym) {
                fsb.append(", ");
            } else {
                fsb.append("(");
            }

            fsb.append("scale = ").append(dynamicColumnFieldInfo.getScale());
            appendSym = true;
        }
        
        if (dynamicColumnFieldInfo.isNullable()) {
            if (appendSym) {
                fsb.append(", ");
            } else {
                fsb.append("(");
            }

            fsb.append("nullable = true");
            appendSym = true;
        }

        if (appendSym) {
            fsb.append(")");
        }
        
        fsb.append("\n");
    }

    private static void generateLisOnlyAnnotation(StringBuilder fsb,
            DynamicListOnlyFieldInfo dynamicListOnlyFieldInfo) {
        fsb.append(" @ListOnly(");
        boolean appendSym = false;
        if (!StringUtils.isBlank(dynamicListOnlyFieldInfo.getColumnName())) {
            if (appendSym) {
                fsb.append(",");
            }

            fsb.append(" name = \"").append(dynamicListOnlyFieldInfo.getColumnName()).append("\"");
            appendSym = true;
        }

        if (!StringUtils.isBlank(dynamicListOnlyFieldInfo.getKey())) {
            if (appendSym) {
                fsb.append(",");
            }

            fsb.append(" key = \"").append(dynamicListOnlyFieldInfo.getKey()).append("\"");
            appendSym = true;
        }

        if (!StringUtils.isBlank(dynamicListOnlyFieldInfo.getProperty())) {
            if (appendSym) {
                fsb.append(",");
            }

            fsb.append(" property = \"").append(dynamicListOnlyFieldInfo.getProperty()).append("\"");
            appendSym = true;
        }

        fsb.append(")\n");
    }

}
