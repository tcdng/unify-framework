/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.data;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A fluent generic set.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class FluentSet<T> {

    private Set<T> set;

    public FluentSet() {
        this.set = new LinkedHashSet<T>();
    }

    public FluentSet(FluentSet<T> fluentSet) {
        this.set = new LinkedHashSet<T>(fluentSet.set);
    }

    public FluentSet<T> addAll(Collection<? extends T> collection) {
        this.set.addAll(collection);
        return this;
    }

    public FluentSet<T> add(T value) {
        this.set.add(value);
        return this;
    }

    public FluentSet<T> clear() {
        this.set.clear();
        return this;
    }

    public FluentSet<T> removeAll(Collection<? extends T> set) {
        this.set.removeAll(set);
        return this;
    }

    public FluentSet<T> remove(T value) {
        this.set.remove(value);
        return this;
    }

    public int size() {
        return this.set.size();
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public boolean contains(T object) {
        return this.set.contains(object);
    }

    public Set<T> values() {
        return this.set;
    }
}
