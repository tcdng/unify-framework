/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.convert;

import java.util.Date;

import com.tcdng.unify.core.format.Formatter;

/**
 * A value to date converter.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DateConverter extends AbstractConverter<Date> {

    @Override
    protected Date doConvert(Object value, Formatter<?> formatter) throws Exception {
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof String) {
            String string = ((String) value).trim();
            if (!string.isEmpty()) {
                return doConvert(formatter.parse((String) value), null);
            }
        }
        return null;
    }
}
