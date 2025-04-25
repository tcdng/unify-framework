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
package com.tcdng.unify.convert.converters;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tcdng.unify.convert.util.ConverterUtils;

/**
 * A value to date converter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DateConverter extends AbstractConverter<Date> {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    protected Date doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof Date) {
            return (Date) value;
        }

        if (value instanceof Long) {
            return new Date((Long) value);
        }

        if (value instanceof String) {
            String string = ((String) value).trim();
            if (!string.isEmpty()) {
                if (formatter == null) {
                    formatter = ConverterUtils.getDefaultDateTimeFormatter();
                }

                if (formatter != null) {
                    return doConvert(formatter.parse((String) value), null);
                }
                
                return new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse((String) value);
            }
        }
        return null;
    }
}
