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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * Map values store.
 * 
 * @author The Code Department
 * @version 1.0
 */
public class MapValuesStore extends AbstractSingleObjectValueStore<MapValues> {

    public MapValuesStore(MapValues map) {
        super(map, null, -1);
    }

    public MapValuesStore(MapValues map, String dataMarker, int dataIndex) {
        super(map, dataMarker, dataIndex);
    }

	@Override
	public final boolean isMap() {
		return true;
	}

    @Override
    public boolean isGettable(String name) throws UnifyException {
        return isTempValue(name) || storage.isMapValue(name);
    }

    @Override
    public boolean isSettable(String name) throws UnifyException {
        return storage.isMapValue(name);
    }

    @Override
    protected Object doRetrieve(String property) throws UnifyException {
    	Object val = getTempValue(property);
        return val == null ? storage.getValue(property) : val;
    }

    @Override
    protected void doStore(String property, Object value, Formatter<?> formatter) throws UnifyException {
        storage.setValue(property, value, formatter);
    }
}
