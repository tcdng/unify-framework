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
package com.tcdng.unify.core.criterion;

import java.util.Collection;

import com.tcdng.unify.core.data.FluentSet;

/**
 * Used to specify fields to group by.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class GroupBy extends FluentSet<String> {

    @Override
    public GroupBy add(String field) {
        return (GroupBy) super.add(field);
    }

    @Override
    public GroupBy addAll(Collection<? extends String> collection) {
        return (GroupBy) super.addAll(collection);
    }

    @Override
    public GroupBy removeAll(Collection<? extends String> set) {
        return (GroupBy) super.removeAll(set);
    }

    @Override
    public GroupBy remove(String value) {
        return (GroupBy) super.remove(value);
    }
}
