/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.constant.BooleanType;

/**
 * Test report parameter entity.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "REPORT_PARAMETER")
public class ReportParameter extends AbstractTestEntity {

    @ForeignKey(Report.class)
    private Long reportId;

    @ForeignKey
    private BooleanType scheduled;

    @Column
    private String name;

    @ChildList
    private List<ReportParameterOptions> options;

    @ListOnly(key = "reportId", property = "description")
    private String reportDesc;

    @ListOnly(key = "scheduled", property = "description")
    private String scheduledDesc;

    public ReportParameter(String name) {
        this(name, BooleanType.FALSE);
    }

    public ReportParameter(String name, BooleanType scheduled) {
        this.name = name;
        this.scheduled = scheduled;
    }

    public ReportParameter() {

    }

    public ReportParameter addOption(ReportParameterOptions rp) {
        if (this.options == null) {
            this.options = new ArrayList<ReportParameterOptions>();
        }
        this.options.add(rp);
        return this;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReportParameterOptions> getOptions() {
        return options;
    }

    public void setOptions(List<ReportParameterOptions> options) {
        this.options = options;
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
