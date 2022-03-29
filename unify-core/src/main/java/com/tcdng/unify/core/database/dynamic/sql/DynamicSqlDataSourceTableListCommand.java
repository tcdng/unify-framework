/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.database.sql.SqlTableInfo;
import com.tcdng.unify.core.database.sql.SqlTableType;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * SQL datasource table list command.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("sqldatasourcetablelist")
public class DynamicSqlDataSourceTableListCommand extends AbstractDynamicSqlDataSourceListCommand {

    @Override
    public List<? extends Listable> execute(Locale locale, DynamicSqlParams params) throws UnifyException {
        if (StringUtils.isNotBlank(params.getConfigName()) && StringUtils.isNotBlank(params.getSchemaName())) {
            SqlTableType sqlTableType = SqlTableType.TABLE;
            if (params.getTableName() != null) {
                sqlTableType = SqlTableType.fromName(params.getTableName());
            }

            List<SqlTableInfo> tableList =
                    getDsManager().getTables(params.getConfigName(), params.getSchemaName(), sqlTableType);
            DataUtils.sortAscending(tableList, SqlTableInfo.class, "listDescription");
            return tableList;
        }

        return Collections.emptyList();
    }

}
