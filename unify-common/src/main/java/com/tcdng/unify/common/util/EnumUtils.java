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
package com.tcdng.unify.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.common.constants.EnumConst;

/**
 * Enumeration utilities.
 * 
 * @author The Code Department
 * @version 1.0
 */
public final class EnumUtils {

    public static final Object[] ZEROLEN_OBJECT_ARRAY = new Object[0];

    private static FactoryMap<Class<? extends EnumConst>, EnumConstMap> enumConstMap =
            new FactoryMap<Class<? extends EnumConst>, EnumConstMap>() {
                @Override
                protected EnumConstMap create(Class<? extends EnumConst> key, Object... params) throws Exception {
                    Map<String, EnumConst> map = new HashMap<String, EnumConst>();
                    String defaultCode = null;
                    for (EnumConst enumConst : key.getEnumConstants()) {
                        map.put(enumConst.code(), enumConst);
                        if (defaultCode == null) {
                            defaultCode = enumConst.defaultCode();
                        }
                    }

                    return new EnumConstMap(key, defaultCode);
                }
            };

    private EnumUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T extends EnumConst> T fromCode(Class<T> clazz, String code) {
        try {
            return (T) enumConstMap.get(clazz).getByCode(code);
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends EnumConst> T fromName(Class<T> clazz, String name) {
        try {
            return (T) enumConstMap.get(clazz).getByName(name);
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends EnumConst> T getDefault(Class<T> clazz) {
        try {
            return (T) enumConstMap.get(clazz).getDefaultConst();
        } catch (Exception e) {
        }
        return null;
    }
    
    private static abstract class FactoryMap<T, U> {

        private final Object accessKey = new Object();

        private Map<T, U> map;

        private boolean checkStale;

        public FactoryMap() {
            this(false);
        }

        public FactoryMap(boolean checkStale) {
            map = new ConcurrentHashMap<T, U>();
            this.checkStale = checkStale;
        }

        public U get(T key) throws Exception {
            if (key == null) {
                throw new IllegalArgumentException("Parameter key can not be null!");
            }

            return get(key, ZEROLEN_OBJECT_ARRAY);
        }

        public U get(T key, Object... params) throws Exception {
            if (key == null) {
                throw new IllegalArgumentException("Parameter key can not be null!");
            }

            U value = map.get(key);
            if (value != null) {
                if (checkStale && stale(key, value)) {
                    remove(key);
                    value = null;
                }
            }

            if (value == null) {
                synchronized (accessKey) {
                    value = map.get(key);
                    if (value == null) {
                        value = create(key, params);
                        if (value != null && keep(value)) {
                            map.put(key, value);
                        }
                    }
                }
            }
            return value;
        }

        public U remove(T key) throws Exception {
            if (key == null) {
                throw new IllegalArgumentException("Parameter key can not be null!");
            }

            U val = map.remove(key);
            onRemove(val);
            return val;
        }

        protected abstract U create(T key, Object... params) throws Exception;

        protected boolean stale(T key, U value) throws Exception {
            return false;
        }

        protected boolean keep(U value) throws Exception {
            return true;
        }

        protected void onRemove(U value) throws Exception {

        }
    }

    private static class EnumConstMap {

        private Map<String, EnumConst> mapByCode;

        private Map<String, EnumConst> mapByName;

        private EnumConst defaultConst;

        public EnumConstMap(Class<? extends EnumConst> key, String defaultCode) {
            mapByCode = new HashMap<String, EnumConst>();
            mapByName = new HashMap<String, EnumConst>();
            for (EnumConst enumConst : key.getEnumConstants()) {
                mapByCode.put(enumConst.code(), enumConst);
                mapByName.put(enumConst.name().toLowerCase(), enumConst);
            }

            this.defaultConst = mapByCode.get(defaultCode);
        }

        public EnumConst getByCode(String code) {
            return mapByCode.get(code);
        }

        public EnumConst getByName(String name) {
            return mapByName.get(name.toLowerCase());
        }

        public EnumConst getDefaultConst() {
            return defaultConst;
        }
    }
}
