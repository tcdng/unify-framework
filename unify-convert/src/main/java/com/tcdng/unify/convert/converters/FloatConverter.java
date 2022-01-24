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
package com.tcdng.unify.convert.converters;

/**
 * A value to float converter.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FloatConverter extends AbstractConverter<Float> {

    @Override
    protected Float doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof Number) {
            return Float.valueOf(((Number) value).floatValue());
        }
        if (value instanceof String) {
            String string = ((String) value).trim();
            if (!string.isEmpty()) {
                if (formatter == null) {
                    return Float.valueOf(string);
                }
                return doConvert(formatter.parse(string), null);
            }
        }
        return null;
    }

}
