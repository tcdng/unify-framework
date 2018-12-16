/*
 * Copyright 2018 The Code Department
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Packable document audit.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDocAudit {

    private Map<String, TrailItem> items;

    public PackableDocAudit(List<TrailItem> items) {
        Map<String, TrailItem> map = new HashMap<String, TrailItem>();
        for (TrailItem item : items) {
            map.put(item.getFieldName(), item);
        }

        this.items = Collections.unmodifiableMap(map);
    }

    public Map<String, TrailItem> getTrailItems() {
        return items;
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
    }
}
