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
package com.tcdng.unify.core.list;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.data.LocaleFactoryMaps;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of a list manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_LISTMANAGER)
public class ListManagerImpl extends AbstractUnifyComponent implements ListManager {

    private LocaleFactoryMaps<String, List<? extends Listable>> staticLists;

    private LocaleFactoryMaps<String, Map<String, String>> staticListMaps;

    private Set<String> staticListNames;

    public ListManagerImpl() {
        staticListNames = new HashSet<String>();
        staticLists = new LocaleFactoryMaps<String, List<? extends Listable>>() {
            @Override
            protected List<? extends Listable> createObject(Locale locale, String listName, Object... params)
                    throws Exception {
                String keyValueList = getMessage(locale, listName, params);
                return Collections.unmodifiableList(StringUtils.readStaticList(keyValueList));
            }
        };

        staticListMaps = new LocaleFactoryMaps<String, Map<String, String>>() {
            @Override
            protected Map<String, String> createObject(Locale locale, String listName, Object... params)
                    throws Exception {
                Map<String, String> listMap = new HashMap<String, String>();
                for (Listable listable : staticLists.get(locale, listName, params)) {
                    listMap.put(listable.getListKey(), listable.getListDescription());
                }
                return Collections.unmodifiableMap(listMap);
            }
        };
    }

    @Override
    public List<? extends Listable> getList(Locale locale, String listName, Object... params) throws UnifyException {
        if (staticListNames.contains(listName)) {
            return staticLists.get(locale, listName);
        }

        return executeListCommand(listName, locale, params);
    }

    @Override
    public Map<String, String> getListMap(Locale locale, String listName, Object... params) throws UnifyException {
        if (staticListNames.contains(listName)) {
            return staticListMaps.get(locale, listName);
        }

        Map<String, String> listMap = new HashMap<String, String>();
        for (Listable listable : executeListCommand(listName, locale, params)) {
            listMap.put(listable.getListKey(), listable.getListDescription());
        }
        return listMap;
    }

    @Override
    public String getListKeyDescription(Locale locale, String listKey, String listName, Object... params)
            throws UnifyException {
        return getListMap(locale, listName, params).get(listKey);
    }

    @Override
    protected void onInitialize() throws UnifyException {
        for (Class<? extends EnumConst> enumClass : getAnnotatedClasses(EnumConst.class, StaticList.class)) {
            StaticList sa = enumClass.getAnnotation(StaticList.class);
            staticListNames.add(sa.value());
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    @SuppressWarnings("unchecked")
    private <T> List<? extends Listable> executeListCommand(String listName, Locale locale, Object... params)
            throws UnifyException {
        ListCommand<T> listCommand = (ListCommand<T>) getComponent(listName);
        return listCommand.execute(locale, DataUtils.constructDataObject(listCommand.getParamType(), params));
    }
}
