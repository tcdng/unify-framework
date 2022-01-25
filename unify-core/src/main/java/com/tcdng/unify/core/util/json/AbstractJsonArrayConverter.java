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
package com.tcdng.unify.core.util.json;

import java.lang.reflect.Array;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

/**
 * Abstract base JSON array converter,
 * 
 * @author Lateef
 * @since 1.0
 */
public abstract class AbstractJsonArrayConverter<T> implements JsonValueConverter<T[]> {

    @SuppressWarnings("unchecked")
    @Override
    public T[] read(Class<T[]> clazz, JsonValue jsonValue) throws Exception {
        JsonArray array = jsonValue.asArray();
        T[] result = (T[]) Array.newInstance(clazz.getComponentType(), array.size());
        for (int i = 0; i < result.length; i++) {
            result[i] = getValue(array.get(i));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonValue write(Object value) throws Exception {
        JsonArray array = (JsonArray) Json.array();
        T[] arrayValues = (T[]) value;
        for (int i = 0; i < arrayValues.length; i++) {
            array.add(setValue(arrayValues[i]));
        }
        return array;
    }

    protected abstract T getValue(JsonValue jsonValue) throws Exception;

    protected abstract JsonValue setValue(T value) throws Exception;
}