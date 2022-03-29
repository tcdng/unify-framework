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

/**
 * A value to character converter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class CharacterConverter extends AbstractConverter<Character> {

    @Override
    protected Character doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof Character) {
            return (Character) value;
        }

        if (value instanceof Number) {
            return Character.valueOf((char) ((Number) value).shortValue());
        }

        if (value instanceof String) {
            String string = ((String) value).trim();
            if (!string.isEmpty()) {
                if (formatter == null) {
                    return Character.valueOf(string.charAt(0));
                }
                return doConvert(formatter.parse(string), null);
            }
        }
        return null;
    }

}
