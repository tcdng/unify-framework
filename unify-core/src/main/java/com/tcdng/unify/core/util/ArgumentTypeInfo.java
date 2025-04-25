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

package com.tcdng.unify.core.util;

/**
 * Argument type information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ArgumentTypeInfo {

    private String fieldName;
    
    private String argumentTypeName0;
    
    private String argumentTypeName1;

    public ArgumentTypeInfo(String fieldName, String argumentTypeName0, String argumentTypeName1) {
        this.fieldName = fieldName;
        this.argumentTypeName0 = argumentTypeName0;
        this.argumentTypeName1 = argumentTypeName1;
    }

    public ArgumentTypeInfo(String fieldName, String argumentTypeName0) {
        this.fieldName = fieldName;
        this.argumentTypeName0 = argumentTypeName0;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getArgumentTypeName0() {
        return argumentTypeName0;
    }

    public String getArgumentTypeName1() {
        return argumentTypeName1;
    }

}
