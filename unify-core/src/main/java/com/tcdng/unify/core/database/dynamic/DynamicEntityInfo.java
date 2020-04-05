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

package com.tcdng.unify.core.database.dynamic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Dynamic entity information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicEntityInfo {

    private String tableName;

    private String className;

    private Map<String, DynamicFieldInfo> fieldInfos;

    private long version;

    private DynamicEntityInfo(String tableName, String className, Map<String, DynamicFieldInfo> fieldInfos,
            long version) {
        this.tableName = tableName;
        this.className = className;
        this.fieldInfos = DataUtils.unmodifiableMap(fieldInfos);
        this.version = version;
    }

    public String getTableName() {
        return tableName;
    }

    public String getClassName() {
        return className;
    }

    public List<DynamicFieldInfo> getFieldInfos() {
        return new ArrayList<DynamicFieldInfo>(fieldInfos.values());
    }

    public DynamicFieldInfo getDynamicFieldInfo(String fieldName) throws UnifyException {
        DynamicFieldInfo dynamicFieldInfo = fieldInfos.get(fieldName);
        if (dynamicFieldInfo == null) {
            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, this.getClass(),
                    "Field with name [" + fieldName + "] is unknown.");
        }

        return dynamicFieldInfo;
    }

    public long getVersion() {
        return version;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String tableName;

        private String className;

        private Map<String, DynamicForeignKeyFieldInfo> fkFields;

        private Map<String, DynamicColumnFieldInfo> columnFields;

        private Map<String, DynamicListOnlyFieldInfo> listOnlyFields;

        private long version;

        private Builder() {
            fkFields = new LinkedHashMap<String, DynamicForeignKeyFieldInfo>();
            columnFields = new LinkedHashMap<String, DynamicColumnFieldInfo>();
            listOnlyFields = new LinkedHashMap<String, DynamicListOnlyFieldInfo>();
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Builder addForeignKeyField(DynamicEntityInfo parentDynamicEntityInfo, String columnName,
                String fieldName, boolean nullable) throws UnifyException {
            checkFieldNameExist(fieldName);
            fkFields.put(fieldName,
                    new DynamicForeignKeyFieldInfo(parentDynamicEntityInfo, columnName, fieldName, nullable));
            return this;
        }

        public Builder addField(DataType dataType, String columnName, String fieldName, int length, int precision,
                int scale, boolean nullable) throws UnifyException {
            return this.addField(dataType, columnName, fieldName, null, null, length, precision, scale, nullable);
        }

        public Builder addField(DataType dataType, String columnName, String fieldName, String transformer,
                String defaultVal, int length, int precision, int scale, boolean nullable) throws UnifyException {
            checkFieldNameExist(fieldName);
            columnFields.put(fieldName, new DynamicColumnFieldInfo(dataType, columnName, fieldName, transformer,
                    defaultVal, length, precision, scale, nullable));
            return this;
        }

        public Builder addListOnlyField(String columnName, String fieldName, String key, String property)
                throws UnifyException {
            checkFieldNameExist(fieldName);
            DynamicForeignKeyFieldInfo fkFieldInfo = fkFields.get(key);
            if (fkFieldInfo == null) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, this.getClass(),
                        "Unknown foreign key [" + key + "] referenced by [" + fieldName + "].");
            }

            listOnlyFields.put(fieldName,
                    new DynamicListOnlyFieldInfo(fkFieldInfo.getParentDynamicEntityInfo().getDynamicFieldInfo(property),
                            columnName, fieldName, key, property));
            return this;
        }

        private void checkFieldNameExist(String fieldName) throws UnifyException {
            if (fkFields.containsKey(fieldName) || columnFields.containsKey(fieldName)
                    || listOnlyFields.containsKey(fieldName)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, this.getClass(),
                        "Field with name [" + fieldName + "] already exists.");
            }
        }

        public DynamicEntityInfo build() {
            Map<String, DynamicFieldInfo> fieldInfos = new LinkedHashMap<String, DynamicFieldInfo>();
            fieldInfos.putAll(fkFields);
            fieldInfos.putAll(columnFields);
            fieldInfos.putAll(listOnlyFields);
            return new DynamicEntityInfo(tableName, className, fieldInfos, version);
        }
    }

}
