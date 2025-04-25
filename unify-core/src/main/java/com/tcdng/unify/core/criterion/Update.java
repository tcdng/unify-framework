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
package com.tcdng.unify.core.criterion;

import java.util.Map;

import com.tcdng.unify.core.data.FluentMap;

/**
 * Used to specify fields to update with values.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Update extends FluentMap<String, Object> {

    public Update() {

    }

    public Update(Update update) {
        super(update);
    }

    @Override
    public Update add(String field, Object value) {
        return (Update) super.add(field, value);
    }

    @Override
    public Update addAll(Map<? extends String, ? extends Object> map) {
        return (Update) super.addAll(map);
    }

    @Override
    public Update remove(String key) {
        return (Update) super.remove(key);
    }

    @Override
    public Update clear() {
        return (Update) super.clear();
    }
}
