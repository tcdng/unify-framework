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

package com.tcdng.unify.core.database.sql;

import com.tcdng.unify.core.constant.ForceConstraints;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.database.DataSourceManagerOptions;

/**
 * SQL schema manager options.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SqlSchemaManagerOptions {

    private PrintFormat printFormat;

    private ForceConstraints forceConstraints;

    public SqlSchemaManagerOptions(PrintFormat printFormat, ForceConstraints forceConstraints) {
        this.printFormat = printFormat;
        this.forceConstraints = forceConstraints;
    }

    public SqlSchemaManagerOptions(DataSourceManagerOptions dsmOptions) {
        this.printFormat = dsmOptions.getPrintFormat();
        this.forceConstraints = dsmOptions.getForceConstraints();
    }

    public PrintFormat getPrintFormat() {
        return printFormat;
    }

    public ForceConstraints getForceConstraints() {
        return forceConstraints;
    }
}
