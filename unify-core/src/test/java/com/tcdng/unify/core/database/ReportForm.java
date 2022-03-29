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
package com.tcdng.unify.core.database;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.annotation.Table;

/**
 * Test report form entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Policy("testentitynorest-policy")
@Table(name = "REPORT_FORM")
public class ReportForm extends AbstractTestVersionedTableEntity {

    @ForeignKey(Report.class)
    private Long reportId;

    @Column
    private String editor;

    public ReportForm(String editor) {
        this.editor = editor;
    }

    public ReportForm() {

    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

}
