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

import com.tcdng.unify.core.data.Listable;

/**
 * SQL table information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlTableInfo implements Listable {

    private SqlTableType type;

    private String tableName;

    public SqlTableInfo(SqlTableType type, String tableName) {
        this.type = type;
        this.tableName = tableName;
    }

    @Override
    public String getListKey() {
        return tableName;
    }

    @Override
    public String getListDescription() {
        return tableName;
    }

    public SqlTableType getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }
}
