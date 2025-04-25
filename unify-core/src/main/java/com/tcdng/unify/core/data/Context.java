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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * An abstract class that represents a context. Manages basic context attribute
 * storage and retrieval.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class Context {

    private Map<String, Attribute> attributes;

    public Context() {
        attributes = new HashMap<String, Attribute>();
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }
    
    public void setAttribute(String name, Object value) {
        attributes.put(name, new Attribute(value, false));
    }

    public void setStickyAttribute(String name, Object value) {
        attributes.put(name, new Attribute(value, true));
    }

    public Object getAttribute(String name) throws UnifyException {
        Attribute attr = attributes.get(name);
        if (attr != null) {
            return attr.getValue();
        }

        return null;
    }

    public Object removeAttribute(String name) {
        Attribute attr = attributes.get(name);
        if (attr != null && !attr.isSticky()) {
            attributes.remove(name);
            return attr.getValue();
        }

        return null;
    }

    public void removeAttributes(String... names) {
        for (String name : names) {
            Attribute attr = attributes.get(name);
            if (attr != null && !attr.isSticky()) {
                attributes.remove(name);
            }
        }
    }

    public void removeAttributes(Collection<String> names) {
        for (String name : names) {
            Attribute attr = attributes.get(name);
            if (attr != null && !attr.isSticky()) {
                attributes.remove(name);
            }
        }
    }

    public void removeAllAttributes() {
        // Remove non-sticky attributes
        if (!attributes.isEmpty()) {
            Iterator<Map.Entry<String, Attribute>> iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().getValue().isSticky()) {
                    iterator.remove();
                }
            }
        }
    }

    public boolean isAttribute(String name) {
        return attributes.containsKey(name);
    }

    protected class Attribute {

        private Object value;

        private boolean sticky;

        public Attribute(Object value, boolean sticky) {
            this.value = value;
            this.sticky = sticky;
        }

        public Object getValue() {
            return value;
        }

        public boolean isSticky() {
            return sticky;
        }
    }
}
