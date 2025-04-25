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
package com.tcdng.unify.core.report;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used for setting the parameters for a report to be generated.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ReportParameters {

    private Map<String, ReportParameter> parameters;

    private int showInHeaderCount;

    private int showInFooterCount;

    public ReportParameters() {
        parameters = new LinkedHashMap<String, ReportParameter>();
    }

    public Set<String> getNames() {
        return parameters.keySet();
    }

    public boolean isParameter(String name) {
        return parameters.containsKey(name);
    }

    public boolean isParameterNotNull(String name) {
        ReportParameter reportParameter = parameters.get(name);
        return reportParameter != null && reportParameter.getValue() != null;
    }

    public ReportParameter getParameter(String name) {
        return parameters.get(name);
    }

    public Object getParameterValue(String name) {
        ReportParameter reportParameter = parameters.get(name);
        if (reportParameter != null) {
            return reportParameter.getValue();
        }
        
        return null;
    }

    public boolean setParameterValue(String name, Object val) {
        ReportParameter reportParameter = parameters.get(name);
        if (reportParameter != null) {
            reportParameter.setValue(val);
            return true;
        }
        
        return false;
    }
    
    public void addParameter(String name, String description, Object value) {
        addParameter(name, description, null, value, false, false);
    }

    public void addParameter(String name, String description, String formatter, Object value, boolean headerDetail,
            boolean footerDetail) {
        addParameter(new ReportParameter(name, description, formatter, value, headerDetail, footerDetail));
    }

    public void addParameter(ReportParameter parameter) {
        if (!parameters.containsKey(parameter.getName())) {
            if (parameter.isHeaderDetail()) {
                showInHeaderCount++;
            }

            if (parameter.isFooterDetail()) {
                showInFooterCount++;
            }
        }

        parameters.put(parameter.getName(), parameter);
    }

    public Collection<ReportParameter> getParameters() {
        return parameters.values();
    }

    public Map<String, Object> getParameterValues() {
        Map<String, Object> paramValues = new LinkedHashMap<String, Object>();
        for (ReportParameter parameter : parameters.values()) {
            paramValues.put(parameter.getName(), parameter.getValue());
        }
        return paramValues;
    }

    public boolean isWithShowInHeader() {
        return showInHeaderCount > 0;
    }

    public boolean isWithShowInFooter() {
        return showInFooterCount > 0;
    }

    public int size() {
        return parameters.size();
    }

    public int getShowInHeaderCount() {
        return showInHeaderCount;
    }

    public int getShowInFooterCount() {
        return showInFooterCount;
    }
}
