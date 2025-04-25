/*
 * Copyright (c) 2018-2025 The Code Department.
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
 * Test loan report record.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Table("LOANREPORT")
public class LoanReport extends AbstractTestVersionedTableEntity {

    @Column
    private String name;

    @Column
    private String description;

    @Child(category = "form")
    private LoanReportParameter reportForm;

    @ChildList(category = "parameter", editable = false)
    private List<LoanReportParameter> parameters;

    public LoanReport(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public LoanReport() {

    }

    public LoanReport addParameter(LoanReportParameter rp) {
        if (parameters == null) {
            parameters = new ArrayList<LoanReportParameter>();
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

    public LoanReportParameter getReportForm() {
        return reportForm;
    }

    public void setReportForm(LoanReportParameter reportForm) {
        this.reportForm = reportForm;
    }

    public List<LoanReportParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<LoanReportParameter> parameters) {
        this.parameters = parameters;
    }
}
