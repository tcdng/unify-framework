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
package com.tcdng.unify.core.util.json;

import java.math.BigDecimal;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

/**
 * JSON decimal value converter.
 * 
 * @author Lateef
 * @since 1.0
 */
public class JsonBigDecimalConverter implements JsonValueConverter<BigDecimal> {

    @Override
    public BigDecimal read(Class<BigDecimal> clazz, JsonValue jsonValue) throws Exception {
        if (jsonValue.isNull()) {
            return null;
        }
        
        return BigDecimal.valueOf(jsonValue.asDouble());
    }

    @Override
    public JsonValue write(Object value) throws Exception {
        return Json.value(((BigDecimal) value).doubleValue());
    }
}