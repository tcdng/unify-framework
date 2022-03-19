/*
 * Copyright 2018-2020 The Code Department.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Abstract value store.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractValueStore implements ValueStore {

    @Override
    public Audit diff(ValueStore newSource) throws UnifyException {
        Audit.Builder ab = Audit.newBuilder();
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                Object oldVal = retrieve(fieldName);
                Object newVal = newSource.retrieve(fieldName);
                if (!DataUtils.equals(oldVal, newVal)) {
                    ab.addItem(fieldName, oldVal, newVal);
                }
            }
        }

        return ab.build();
    }

    @Override
    public void copy(ValueStore source) throws UnifyException {
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                store(fieldName, source.retrieve(fieldName));
            }
        }
    }

    @Override
    public void copyWithExclusions(ValueStore source, String... exclusionFieldNames) throws UnifyException {
        Set<String> exclusion = new HashSet<String>(Arrays.asList(exclusionFieldNames));
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                if (!exclusion.contains(fieldName)) {
                    store(fieldName, source.retrieve(fieldName));
                }
            }
        }
    }

    @Override
    public void copyWithInclusions(ValueStore source, String... inclusionFieldNames) throws UnifyException {
        Set<String> inclusion = new HashSet<String>(Arrays.asList(inclusionFieldNames));
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                if (inclusion.contains(fieldName)) {
                    store(fieldName, source.retrieve(fieldName));
                }
            }
        }
    }

    protected abstract Class<?> getDataClass() throws UnifyException;

}
