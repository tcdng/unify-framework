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

package com.tcdng.unify.core.util;

import java.util.List;

import com.tcdng.unify.core.data.BeanValueArrayStore;
import com.tcdng.unify.core.data.BeanValueListStore;
import com.tcdng.unify.core.data.BeanValueStore;
import com.tcdng.unify.core.data.MapValues;
import com.tcdng.unify.core.data.MapValuesArrayStore;
import com.tcdng.unify.core.data.MapValuesListStore;
import com.tcdng.unify.core.data.MapValuesStore;
import com.tcdng.unify.core.data.PackableDoc;
import com.tcdng.unify.core.data.PackableDocArrayStore;
import com.tcdng.unify.core.data.PackableDocListStore;
import com.tcdng.unify.core.data.PackableDocStore;
import com.tcdng.unify.core.data.ValueStore;

/**
 * Value store utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class ValueStoreUtils {

    private ValueStoreUtils() {

    }

    public static ValueStore getValueStore(Object storageObject) {
        return getValueStore(storageObject, null, -1);
    }

    public static ValueStore getValueStore(Object storageObject, String dataMarker, int dataIndex) {
        if (storageObject != null) {
            if (storageObject instanceof PackableDoc) {
                return new PackableDocStore((PackableDoc) storageObject, dataMarker, dataIndex);
            }

            if (storageObject instanceof MapValues) {
                return new MapValuesStore((MapValues) storageObject, dataMarker, dataIndex);
            }

            return new BeanValueStore(storageObject, dataMarker, dataIndex);
        }

        return null;
    }

    public static ValueStore getArrayValueStore(Object[] storageObject, String dataMarker, int dataIndex) {
        if (storageObject != null) {
            if (storageObject instanceof PackableDoc[]) {
                return new PackableDocArrayStore((PackableDoc[]) storageObject, dataMarker, dataIndex);
            }

            if (storageObject instanceof MapValues[]) {
                return new MapValuesArrayStore((MapValues[]) storageObject, dataMarker, dataIndex);
            }

            return new BeanValueArrayStore(storageObject, dataMarker, dataIndex);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> ValueStore getListValueStore(Class<T> clazz, List<T> storageObject, String dataMarker,
            int dataIndex) {
        if (storageObject != null) {
            if (PackableDoc.class.equals(clazz)) {
                return new PackableDocListStore((List<PackableDoc>) storageObject, dataMarker, dataIndex);
            }

            if (MapValues.class.equals(clazz)) {
                return new MapValuesListStore((List<MapValues>) storageObject, dataMarker, dataIndex);
            }

            return new BeanValueListStore(storageObject, dataMarker, dataIndex);
        }

        return null;
    }

}
