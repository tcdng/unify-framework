/*
 * Copyright 2018-2023 The Code Department.
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

package com.tcdng.unify.core.criterion;

/**
 * Filter parameter type.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum FilterParamType {
    NONE,
    IMMEDIATE,
    FIELD,
    PARAMETER,
    COLLECTION;
    
    public boolean isNone() {
        return NONE.equals(this);
    }
    
    public boolean isImmediate() {
        return IMMEDIATE.equals(this);
    }
    
    public boolean isField() {
        return FIELD.equals(this);
    }
    
    public boolean isParameter() {
        return PARAMETER.equals(this);
    }
    
    public boolean isCollection() {
        return COLLECTION.equals(this);
    }
}
