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

import java.util.Collections;
import java.util.List;

/**
 * Index information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SqlIndexInfo implements SqlIndexSchemaInfo {

    private String name;

    private List<String> fieldNameList;

    private boolean unique;

    public SqlIndexInfo(String name, List<String> fieldNameList, boolean unique) {
        this.name = name;
        this.fieldNameList = Collections.unmodifiableList(fieldNameList);
        this.unique = unique;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getFieldNameList() {
        return fieldNameList;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }
}
