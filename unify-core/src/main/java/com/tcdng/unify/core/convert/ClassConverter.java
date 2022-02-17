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
package com.tcdng.unify.core.convert;

import com.tcdng.unify.convert.converters.AbstractConverter;
import com.tcdng.unify.convert.converters.ConverterFormatter;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * A value to class converter. Does a {@link ReflectUtils#classForName(String)} for string
 * values.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ClassConverter extends AbstractConverter<Class<?>> {

    @Override
    protected Class<?> doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof Class) {
            return (Class<?>) value;
        }

        if (value instanceof String) {
            return ReflectUtils.classForName((String) value);
        }

        return null;
    }
}
