/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.common.util;

/**
 * Process variable utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class ProcessVariableUtils {

    private static final String VARIABLE_PREFIX = "pv:";

    public static String getVariable(String name) {
        return VARIABLE_PREFIX + name;
    }
    
    public static boolean isProcessVariable(String variable) {
        return variable.startsWith(VARIABLE_PREFIX);
    }
}
