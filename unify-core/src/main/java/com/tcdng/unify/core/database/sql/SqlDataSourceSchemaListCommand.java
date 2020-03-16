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
package com.tcdng.unify.core.database.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * SQL datasource schema list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("sqldatasourceschemalist")
public class SqlDataSourceSchemaListCommand extends AbstractDynamicSqlDataSourceListCommand {

    @Override
    public List<? extends Listable> execute(Locale locale, DynamicSqlParams params) throws UnifyException {
        if (StringUtils.isNotBlank(params.getConfigName())) {
            List<Listable> schemaList = new ArrayList<Listable>();
            for (String schema : getDsManager().getSchemas(params.getConfigName())) {
                schemaList.add(new ListData(schema, schema));
            }
            DataUtils.sortAscending(schemaList, Listable.class, "listDescription");
            return schemaList;
        }

        return Collections.emptyList();
    }

}
