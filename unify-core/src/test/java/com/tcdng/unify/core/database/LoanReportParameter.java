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
package com.tcdng.unify.core.database;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.core.annotation.CategoryColumn;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.constant.BooleanType;

/**
 * Test loan report parameter entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Policy("testentitynorest-policy")
@Table(name = "LOAN_REPORT_PARAMETER")
public class LoanReportParameter extends AbstractTestVersionedTableEntity {

    @ForeignKey(LoanReport.class)
    private Long reportId;

    @ForeignKey
    private BooleanType scheduled;

    @CategoryColumn
    private String category;
    
    @Column
    private String name;

    @ListOnly(key = "reportId", property = "description")
    private String reportDesc;

    @ListOnly(key = "scheduled", property = "description")
    private String scheduledDesc;

    public LoanReportParameter(String name) {
        this(name, BooleanType.FALSE);
    }

    public LoanReportParameter(String name, BooleanType scheduled) {
        this.name = name;
        this.scheduled = scheduled;
    }

    public LoanReportParameter() {

    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public BooleanType getScheduled() {
        return scheduled;
    }

    public void setScheduled(BooleanType scheduled) {
        this.scheduled = scheduled;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportDesc() {
        return reportDesc;
    }

    public void setReportDesc(String reportDesc) {
        this.reportDesc = reportDesc;
    }

    public String getScheduledDesc() {
        return scheduledDesc;
    }

    public void setScheduledDesc(String scheduledDesc) {
        this.scheduledDesc = scheduledDesc;
    }
}
