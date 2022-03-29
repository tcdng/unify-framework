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

import java.util.Collection;

import com.tcdng.unify.core.constant.BooleanType;

/**
 * Query object for test report parameter record.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ReportParameterQuery extends AbstractTestTableEntityQuery<ReportParameter> {

    public ReportParameterQuery() {
        super(ReportParameter.class);
    }

    public ReportParameterQuery reportId(Long reportId) {
        return (ReportParameterQuery) addEquals("reportId", reportId);
    }

    public ReportParameterQuery scheduled(BooleanType scheduled) {
        return (ReportParameterQuery) addEquals("scheduled", scheduled);
    }

    public ReportParameterQuery scheduledIn(Collection<BooleanType> scheduled) {
        return (ReportParameterQuery) addAmongst("scheduled", scheduled);
    }
}
