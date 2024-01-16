/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.DataSource;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceManager;

/**
 * Convenient abstract report processor.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractReportProcessor extends AbstractUnifyComponent implements ReportProcessor {

    @Configurable
    private DynamicSqlDataSourceManager dynamicSqlDataSourceManager;

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected DataSource getDataSource(Report report) throws UnifyException {
        if (report.isDynamicDataSource()) {
            return dynamicSqlDataSourceManager.getDataSource(report.getDataSource());
        }

        return (DataSource) getComponent(report.getDataSource());
    }

    protected void setReportHeaderParameter(Report report, String paramName, String paramDesc, Object paramValue)
            throws UnifyException {
        setReportHeaderParameter(report, paramName, paramDesc, null, paramValue);
    }

    protected void setReportHeaderParameter(Report report, String paramName, String paramDesc, String formatter,
            Object paramValue) throws UnifyException {
        report.setParameter(paramName, paramDesc, formatter, paramValue, true, false);
    }

    protected void setReportHeaderParameter(ReportParameters reportParameters, String paramName, String paramDesc,
            Object paramValue) throws UnifyException {
        setReportHeaderParameter(reportParameters, paramName, paramDesc, null, paramValue);
    }

    protected void setReportHeaderParameter(ReportParameters reportParameters, String paramName, String paramDesc,
            String formatter, Object paramValue) throws UnifyException {
        reportParameters.addParameter(paramName, paramDesc, formatter, paramValue, true, false);
    }
}
