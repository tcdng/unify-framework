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
package com.tcdng.unify.convert.converters;

import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import com.tcdng.unify.convert.util.ConverterUtils;


/**
 * A value to string converter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class StringConverter extends AbstractConverter<String> {

    @SuppressWarnings("unchecked")
    @Override
    protected String doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value != null) {
            if (value instanceof String) {
                if (formatter != null && formatter.getDataType().isAssignableFrom(String.class)) {
                    return ((ConverterFormatter<Object>) formatter).format(value);
                }

                return (String) value;
            }

            if (value instanceof byte[]) {
            	return Base64.encodeBase64String((byte[]) value);
            }

            if (formatter == null) {
                if (value instanceof Date) {
                    formatter = ConverterUtils.getDefaultDateTimeFormatter();
                }
                
                if (formatter == null) {
                    return String.valueOf(value);
                }
            }

            return ((ConverterFormatter<Object>) formatter).format(value);
        }
        return null;
    }
}
