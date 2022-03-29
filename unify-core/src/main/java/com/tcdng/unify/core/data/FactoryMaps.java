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
package com.tcdng.unify.core.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * An abstract generic map of generic factory maps.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class FactoryMaps<T, U, V> extends FactoryMap<T, FactoryMap<U, V>> {

    private boolean checkStale;

    public FactoryMaps() {
        this(false);
    }

    public FactoryMaps(boolean checkStale) {
        this.checkStale = checkStale;
    }

    public V get(T mainKey, U key, Object... params) throws UnifyException {
        return get(mainKey).get(key, params);
    }

    public Set<U> keySet(T key) throws UnifyException {
        return get(key).keySet();
    }

    public Collection<V> values(T key) throws UnifyException {
        return get(key).values();
    }

    public V remove(T mainKey, U key) throws UnifyException {
        return get(mainKey).remove(key);
    }

    public void removeSiblingKeys(T mainKey, U key) throws UnifyException {
        for (T siblingMainKey : keySet()) {
            if (!siblingMainKey.equals(mainKey)) {
                get(siblingMainKey).remove(key);
            }
        }
    }

    public Set<V> removeSubkeys(U key) throws UnifyException {
        Set<V> result = new HashSet<V>();
        for (T siblingMainKey : keySet()) {
            V value = get(siblingMainKey).remove(key);
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    public boolean isKey(T mainKey, U key) throws UnifyException {
        if (isKey(mainKey)) {
            return get(mainKey).isKey(key);
        }
        return false;
    }

    @Override
    protected final FactoryMap<U, V> create(final T mainKey, Object... params) throws Exception {
        return new FactoryMap<U, V>(checkStale) {
            @Override
            protected boolean stale(U key, V value) throws Exception {
                if (valueStale(mainKey, key, value)) {
                    removeSiblingKeys(mainKey, key);
                    return true;
                }

                return false;
            }

            @Override
            protected V create(U key, Object... params) throws Exception {
                if (key == null) {
                    throw new IllegalArgumentException("Parameter key can not be null!");
                }

                return createObject(mainKey, key, params);
            }
        };
    };

    protected boolean valueStale(T mainKey, U key, V value) throws Exception {
        return false;
    }

    protected abstract V createObject(T mainKey, U key, Object... params) throws Exception;
}
