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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * A report filter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ReportFilter {

    private RestrictionType op;

    private String tableName;

    private String columnName;

    private Object param1;

    private Object param2;

    private List<ReportFilter> subFilterList;

    public ReportFilter(RestrictionType op, String tableName, String columnName, Object param1, Object param2) {
        if (op.isCompound()) {
            throw new IllegalArgumentException("Restriction type must be simple for this constructor.");
        }

        this.op = op;
        this.tableName = tableName;
        this.columnName = columnName;
        this.param1 = param1;
        this.param2 = param2;
    }

    public ReportFilter(RestrictionType op) {
        if (!op.isCompound()) {
            throw new IllegalArgumentException("Restriction type must be compound for this constructor.");
        }

        this.op = op;
        this.subFilterList = new ArrayList<ReportFilter>();
    }

    public RestrictionType getOp() {
        return op;
    }

    public boolean isCompound() {
        return op.isCompound();
    }

    public boolean isSubFilters() {
        return !DataUtils.isBlank(subFilterList);
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getParam1() {
        return param1;
    }

    public Object getParam2() {
        return param2;
    }

    public List<ReportFilter> getSubFilterList() {
        return subFilterList;
    }

    public boolean isWithFilterColumn(String columnName) {
        if (!DataUtils.isBlank(subFilterList)) {
            for (ReportFilter subFilter : subFilterList) {
                if (subFilter.isCompound()) {
                    if (subFilter.isWithFilterColumn(columnName)) {
                        return true;
                    }
                } else if (columnName.equals(subFilter.columnName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
