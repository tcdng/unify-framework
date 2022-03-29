/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.Date;

/**
 * A value to long converter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class LongConverter extends AbstractConverter<Long> {

    @Override
    protected Long doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        
        if (value instanceof Date) {
            return Long.valueOf(((Date) value).getTime());
        }
        
        if (value instanceof String) {
            String string = ((String) value).trim();
            if (!string.isEmpty()) {
                if (formatter == null) {
                    return Long.decode(string);
                }
                return doConvert(formatter.parse(string), null);
            }
        }
        return null;
    }
}
