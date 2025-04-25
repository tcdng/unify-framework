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
package com.tcdng.unify.core.util.json;

import java.util.Date;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;
import com.tcdng.unify.convert.util.ConverterUtils;

/**
 * JSON date value converter.
 * 
 * @author Lateef
 * @since 4.1
 */
public class JsonDateConverter implements JsonValueConverter<Date> {

    @Override
    public Date read(Class<Date> clazz, JsonValue jsonValue) throws Exception {
        if (jsonValue.isNull()) {
            return null;
        }
        
        return ConverterUtils.convert(Date.class, jsonValue.asString());
    }

    @Override
    public JsonValue write(Object value) throws Exception {
        return Json.value(ConverterUtils.convert(String.class, (Date) value));
    }
}