/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.database;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.core.annotation.Child;
import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;

/**
 * Test report record.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table("REPORT")
public class Report extends AbstractTestVersionedTableEntity {

    @Column
    private String name;

    @Column
    private String description;

    @Child
    private ReportForm reportForm;

    @ChildList(editable = false)
    private List<ReportParameter> parameters;

    public Report(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Report(String name, String description, ReportForm reportForm) {
        this.name = name;
        this.description = description;
        this.reportForm = reportForm;
    }

    public Report() {

    }

    public Report addParameter(ReportParameter rp) {
        if (parameters == null) {
            parameters = new ArrayList<ReportParameter>();
        }
        parameters.add(rp);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportForm getReportForm() {
        return reportForm;
    }

    public void setReportForm(ReportForm reportForm) {
        this.reportForm = reportForm;
    }

    public List<ReportParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ReportParameter> parameters) {
        this.parameters = parameters;
    }
}
