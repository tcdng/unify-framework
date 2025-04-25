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
package com.tcdng.unify.core.database.dynamic.sql;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * SQL data source column list command.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("sqldatasourcecolumnlist")
public class DynamicSqlDataSourceColumnListCommand extends AbstractDynamicSqlDataSourceListCommand {

    @Override
    public List<? extends Listable> execute(Locale locale, DynamicSqlParams params) throws UnifyException {
        if (StringUtils.isNotBlank(params.getConfigName()) && StringUtils.isNotBlank(params.getSchemaName())
                && StringUtils.isNotBlank(params.getTableName())) {
            List<SqlColumnInfo> columnList =
                    getDsManager().getColumns(params.getConfigName(), params.getSchemaName(), params.getTableName());
            DataUtils.sortAscending(columnList, SqlColumnInfo.class, "listDescription");
            return columnList;
        }

        return Collections.emptyList();
    }
}
