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

package com.tcdng.unify.core.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.tcdng.unify.core.annotation.CallableDataType;

/**
 * Holds callable parameter information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlCallableParamInfo extends SqlCallableFieldInfo {

    private boolean input;

    private boolean output;

    public SqlCallableParamInfo(CallableDataType dataType, String name, Field field, Method getter, Method setter,
            boolean input, boolean output) {
        super(dataType, name, field, getter, setter);
        this.input = input;
        this.output = output;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return output;
    }
}
