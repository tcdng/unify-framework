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

import java.util.List;

import com.tcdng.unify.convert.converters.AbstractConverter;
import com.tcdng.unify.convert.converters.ConverterFormatter;
import com.tcdng.unify.core.upl.UplElementReferences;

/**
 * A value to string converter.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UplElementReferencesConverter extends AbstractConverter<UplElementReferences> {

    @SuppressWarnings("unchecked")
    @Override
    protected UplElementReferences doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof UplElementReferences) {
            return (UplElementReferences) value;
        }

        if (value instanceof List) {
            UplElementReferences uer = new UplElementReferences();
            for (Object listValue : (List<Object>) value) {
                if (listValue instanceof UplElementReferences) {
                    uer.add((UplElementReferences) listValue);
                }
            }
            return uer;
        }
        return null;
    }
}
