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
package com.tcdng.unify.core.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A generic locale based map.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class LocaleMaps<T, U> {

    private Map<Locale, Map<T, U>> localeMaps;

    public LocaleMaps() {
        this.localeMaps = new HashMap<Locale, Map<T, U>>();
    }

    public void put(Locale locale, T key, U value) {
        if (locale == null) {
            throw new IllegalArgumentException("Parameter locale can not be null!");
        }

        Map<T, U> map = this.localeMaps.get(locale);
        if (map == null) {
            map = new HashMap<T, U>();
            this.localeMaps.put(locale, map);
        }

        map.put(key, value);
    }

    public U get(Locale locale, T key) {
        if (locale == null) {
            throw new IllegalArgumentException("Parameter locale can not be null!");
        }

        Map<T, U> map = this.localeMaps.get(locale);
        if (map != null) {
            return map.get(key);
        }

        return null;
    }

    public U remove(Locale locale, T key) {
        if (locale == null) {
            throw new IllegalArgumentException("Parameter locale can not be null!");
        }

        Map<T, U> map = this.localeMaps.get(locale);
        if (map != null) {
            return map.remove(key);
        }

        return null;
    }

    public void clear() {
        this.localeMaps.clear();
    }

    public boolean containsKey(Locale locale, T key) {
        if (locale == null) {
            throw new IllegalArgumentException("Parameter locale can not be null!");
        }

        Map<T, U> map = this.localeMaps.get(locale);
        if (map != null) {
            return map.containsKey(key);
        }

        return false;
    }

    @Override
    public String toString() {
        return localeMaps.toString();
    }
}
