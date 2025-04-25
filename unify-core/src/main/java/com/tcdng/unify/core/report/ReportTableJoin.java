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

import com.tcdng.unify.core.database.sql.SqlJoinType;

/**
 * A report table join.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ReportTableJoin {

    private SqlJoinType type;

    private String tableA;

    private String columnA;

    private String tableB;

    private String columnB;

    public ReportTableJoin(String tableA, String columnA, String tableB, String columnB) {
        this(SqlJoinType.LEFT, tableA, columnA, tableB, columnB);
    }

    public ReportTableJoin(SqlJoinType type, String tableA, String columnA, String tableB, String columnB) {
        this.type = type;
        this.tableA = tableA;
        this.columnA = columnA;
        this.tableB = tableB;
        this.columnB = columnB;
    }

    public SqlJoinType getType() {
        return type;
    }

    public String getTableA() {
        return tableA;
    }

    public String getColumnA() {
        return columnA;
    }

    public String getTableB() {
        return tableB;
    }

    public String getColumnB() {
        return columnB;
    }
}
