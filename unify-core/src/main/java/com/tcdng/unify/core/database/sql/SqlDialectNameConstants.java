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
package com.tcdng.unify.core.database.sql;

/**
 * SQL dialect name constants.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface SqlDialectNameConstants {

    String HSQLDB = "hsqldb-dialect";

    String JAVADB = "javadb-dialect";

    String MSSQL = "mssql-dialect";

    String MSSQL_2012 = "mssql2012-dialect";

    String MYSQL = "mysql-dialect";

    String ORACLE = "oracle-dialect";

    String ORACLE_12C = "oracle12c-dialect";

    String POSTGRESQL = "postgresql-dialect";
}
