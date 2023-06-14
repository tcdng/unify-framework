/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * Map values list store.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class MapValuesListStore extends AbstractListValueStore<MapValues> {

    public MapValuesListStore(List<MapValues> storage) {
        this(storage, null, 0);
    }

    public MapValuesListStore(List<MapValues> storage, String dataMarker, int dataIndex) {
        super(storage, dataMarker, dataIndex);
    }

    @Override
    protected boolean doSettable(MapValues storage, String property) throws UnifyException {
        return storage != null && storage.isMapValue(property);
    }

    @Override
    protected boolean doGettable(MapValues storage, String property) throws UnifyException {
        return storage != null && storage.isMapValue(property);
    }

    @Override
    protected Object doRetrieve(MapValues storage, String property) throws UnifyException {
    	Object val = getTempValue(property);
        return val == null ? storage.getValue(property) : val;
    }

    @Override
    protected void doStore(MapValues storage, String property, Object value, Formatter<?> formatter)
            throws UnifyException {
        storage.setValue(property, value, formatter);
    }

}
