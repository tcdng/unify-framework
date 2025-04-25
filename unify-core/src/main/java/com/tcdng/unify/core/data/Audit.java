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
package com.tcdng.unify.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Audit.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Audit {

    private Map<String, TrailItem> items;

    public Audit(List<TrailItem> items) {
        Map<String, TrailItem> map = new HashMap<String, TrailItem>();
        for (TrailItem item : items) {
            map.put(item.getFieldName(), item);
        }

        this.items = Collections.unmodifiableMap(map);
    }

    public Map<String, TrailItem> getTrailItems() {
        return items;
    }

    public int size() {
        return items.size();
    }

    public boolean isWithItem(String name) {
        return items.containsKey(name);
    }

    public boolean isWithAnyItem(String... names) {
        for (String name : names) {
            if (items.containsKey(name)) {
                return true;
            }
        }

        return false;
    }

    public Set<String> getItemNames() {
        return items.keySet();
    }
    
    public TrailItem getTrailItem(String name) {
        return items.get(name);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Audit [items=" + items + "]";
    }

    public static class Builder {

        private List<TrailItem> items;

        public Builder() {
            this.items = new ArrayList<TrailItem>();
        }

        public Builder addItem(String fieldName, Object oldValue, Object newValue) {
            this.items.add(new TrailItem(fieldName, oldValue, newValue));
            return this;
        }

        public Audit build() {
            return new Audit(items);
        }
    }

    public static class TrailItem {

        private String fieldName;

        private Object oldValue;

        private Object newValue;

        public TrailItem(String fieldName, Object oldValue, Object newValue) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        @Override
        public String toString() {
            return "TrailItem [fieldName=" + fieldName + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
        }
    }
}
