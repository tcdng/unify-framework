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
package com.tcdng.unify.core.report;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.DataSource;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.sql.DynamicSqlDataSourceManager;

/**
 * Abstract report processor for a record type. Subclasses only need to provide
 * report columns and populate a search criteria.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractRecordReportProcessor extends AbstractReportProcessor {

    @Configurable
    private DynamicSqlDataSourceManager dynamicSqlDataSourceManager;

    protected DataSource getDataSource(Report report) throws UnifyException {
        if (report.isDynamicDataSource()) {
            return dynamicSqlDataSourceManager.getDataSource(report.getDataSource());
        }

        return (DataSource) getComponent(report.getDataSource());
    }

    protected abstract ReportColumn[] getReportColumns(String reportCode) throws UnifyException;

    protected abstract void populate(Query<? extends Entity> query, ReportParameters reportParameters)
            throws UnifyException;
}
