/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.util.StringUtils;

/**
 * Abstract base class for SQL data type policies.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractSqlDataTypePolicy implements SqlDataTypePolicy {

	@Override
    public void appendDefaultSql(StringBuilder sb, Class<?> fieldType, String defaultVal) {
        if (StringUtils.isBlank(defaultVal)) {
            defaultVal = getAltDefault(fieldType);
        }

        if (StringUtils.isNotBlank(defaultVal)) {
            sb.append(" DEFAULT ");
            if (String.class.equals(fieldType)) {
                if (!defaultVal.startsWith("'")) {
                    sb.append('\'');
                }
                
                sb.append(defaultVal);
                if (!defaultVal.endsWith("'")) {
                    sb.append('\'');
                }
            } else {
                sb.append(defaultVal);
            }
        }
    }

    @Override
    public void appendDefaultVal(StringBuilder sb, Class<?> fieldType, String defaultVal) {
        if (StringUtils.isBlank(defaultVal)) {
            defaultVal = getAltDefault(fieldType);
        }

        if (StringUtils.isNotBlank(defaultVal)) {
            if (String.class.equals(fieldType)) {
                if (!defaultVal.startsWith("'")) {
                    sb.append('\'');
                }
                
                sb.append(defaultVal);
                if (!defaultVal.endsWith("'")) {
                    sb.append('\'');
                }
            } else {
                sb.append(defaultVal);
            }
        }
    }
}
