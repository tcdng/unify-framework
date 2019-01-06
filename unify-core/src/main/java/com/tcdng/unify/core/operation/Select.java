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
package com.tcdng.unify.core.operation;

import java.util.Collection;

import com.tcdng.unify.core.data.FluentSet;

/**
 * Used to specify fields to select from the results of a criteria.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Select extends FluentSet<String> {

    private boolean distinct;

    public Select(Select select) {
        super(select);
        this.distinct = select.distinct;
    }

    public Select(String field) {
        super.add(field);
    }

    public Select(String... fields) {
        for (String field : fields) {
            super.add(field);
        }
    }

    public Select() {

    }

    @Override
    public Select add(String field) {
        return (Select) super.add(field);
    }

    @Override
    public Select addAll(Collection<? extends String> collection) {
        return (Select) super.addAll(collection);
    }

    @Override
    public Select removeAll(Collection<? extends String> set) {
        return (Select) super.removeAll(set);
    }

    @Override
    public Select remove(String value) {
        return (Select) super.remove(value);
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Select setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }
}
