/*
 * Copyright 2018-2019 The Code Department.
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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

/**
 * JSON boolean array value converter.
 * 
 * @author Lateef
 * @since 1.0
 */
public class JsonBooleanArrayConverter extends AbstractJsonArrayConverter<Boolean> {

    public JsonBooleanArrayConverter() {
        super(Boolean.class);
    }

    @Override
    protected Boolean getValue(JsonValue jsonValue) throws Exception {
        return jsonValue.asBoolean();
    }

    @Override
    protected JsonValue setValue(Boolean value) throws Exception {
        return Json.value(value);
    }
}