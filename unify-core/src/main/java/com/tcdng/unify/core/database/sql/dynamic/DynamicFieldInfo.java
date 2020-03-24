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

package com.tcdng.unify.core.database.sql.dynamic;

import com.tcdng.unify.core.constant.DataType;

/**
 * Dynamic field information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicFieldInfo {

    private DataType dataType;

    private String fieldName;

    private String transformer;

    private String defaultVal;

    private int length;

    private int precision;

    private int scale;

    private boolean nullable;

    public DynamicFieldInfo(DataType dataType, String fieldName, String transformer, String defaultVal, int length,
            int precision, int scale, boolean nullable) {
        this.dataType = dataType;
        this.fieldName = fieldName;
        this.transformer = transformer;
        this.defaultVal = defaultVal;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
        this.nullable = nullable;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTransformer() {
        return transformer;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public int getLength() {
        return length;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public boolean isNullable() {
        return nullable;
    }

}
