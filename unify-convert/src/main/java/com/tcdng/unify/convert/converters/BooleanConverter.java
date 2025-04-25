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
package com.tcdng.unify.convert.converters;

/**
 * A value to boolean converter. Converts, non-case sensitively, strings "y",
 * "on", "true", and "yes" to Boolean.TRUE.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class BooleanConverter extends AbstractConverter<Boolean> {

    @Override
    protected Boolean doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof Boolean) {
            return Boolean.valueOf((Boolean) value);
        }
        if (value instanceof String) {
            String string = ((String) value);
            return Boolean.valueOf("y".equalsIgnoreCase(string) || "on".equalsIgnoreCase(string)
                    || "true".equalsIgnoreCase(string) || "yes".equalsIgnoreCase(string));
        }
        return null;
    }
}
