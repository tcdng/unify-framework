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
package com.tcdng.unify.core.database;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.Table;

/**
 * Test report parameter option entity.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "REPORT_PARAMETEROPT")
public class ReportParameterOptions extends AbstractTestTableEntity {

    @ForeignKey(ReportParameter.class)
    private Long reportParameterId;

    @Column
    private String name;

    public ReportParameterOptions(String name) {
        this.name = name;
    }

    public ReportParameterOptions() {

    }

    public Long getReportParameterId() {
        return reportParameterId;
    }

    public void setReportParameterId(Long reportParameterId) {
        this.reportParameterId = reportParameterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
