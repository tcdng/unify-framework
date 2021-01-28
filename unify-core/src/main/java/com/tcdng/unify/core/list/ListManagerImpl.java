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
package com.tcdng.unify.core.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.data.LocaleFactoryMap;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ListUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of a list manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_LISTMANAGER)
public class ListManagerImpl extends AbstractUnifyComponent implements ListManager {

    private LocaleFactoryMap<Map<String, StaticListInfo>> staticListMaps;

    private LocaleFactoryMap<List<StaticListInfo>> staticLists;

    public ListManagerImpl() {
        staticLists = new LocaleFactoryMap<List<StaticListInfo>>() {

            @Override
            protected List<StaticListInfo> create(Locale locale, Object... params) throws Exception {
                List<StaticListInfo> list = new ArrayList<StaticListInfo>();
                for (Class<? extends EnumConst> enumClass : getAnnotatedClasses(EnumConst.class,
                        StaticList.class)) {
                    StaticList sa = enumClass.getAnnotation(StaticList.class);
                    list.add(new StaticListInfo(locale, sa.name(), resolveMessage(locale, sa.description())));
                }

                DataUtils.sortAscending(list, StaticListInfo.class, "description");
                return Collections.unmodifiableList(list);
            }
            
        };

        staticListMaps = new LocaleFactoryMap<Map<String, StaticListInfo>>()
            {

                @Override
                protected Map<String, StaticListInfo> create(Locale locale, Object... params) throws Exception {
                    Map<String, StaticListInfo> map = new LinkedHashMap<String, StaticListInfo>();
                    for (StaticListInfo staticListInfo : staticLists.get(locale)) {
                        map.put(staticListInfo.getName(), staticListInfo);
                    }

                    return Collections.unmodifiableMap(map);
                }

            };

    }

    @Override
    public List<? extends Listable> getAllStaticLists(Locale locale) throws UnifyException {
        return staticLists.get(locale);
    }

    @Override
    public List<? extends Listable> getList(Locale locale, String listName, Object... params) throws UnifyException {
        StaticListInfo staticListInfo = staticListMaps.get(locale).get(listName);
        if (staticListInfo != null) {
            return staticListInfo.getList();
        }

        return executeListCommand(listName, locale, params);
    }

    @Override
    public List<? extends Listable> getSubList(Locale locale, String listName, String filter, int limit, Object... params)
            throws UnifyException {
        StaticListInfo staticListInfo = staticListMaps.get(locale).get(listName);
        if (staticListInfo != null) {
            return staticListInfo.getList(filter, limit);
        }

        return executeListCommand(listName, locale, filter, limit, params);
    }

    @Override
    public Map<String, Listable> getListMap(Locale locale, String listName, Object... params) throws UnifyException {
        StaticListInfo staticListInfo = staticListMaps.get(locale).get(listName);
        if (staticListInfo != null) {
            return staticListInfo.getMap();
        }

        Map<String, Listable> listMap = new HashMap<String, Listable>();
        for (Listable listable : executeListCommand(listName, locale, params)) {
            listMap.put(listable.getListKey(), listable);
        }
        return listMap;
    }

    @Override
    public Map<String, Listable> getSubListMap(Locale locale, String listName, String filter, int limit, Object... params)
            throws UnifyException {
        StaticListInfo staticListInfo = staticListMaps.get(locale).get(listName);
        if (staticListInfo != null) {
            return staticListInfo.getMap(filter, limit);
        }

        Map<String, Listable> listMap = new HashMap<String, Listable>();
        for (Listable listable : executeListCommand(listName, locale, filter, limit, params)) {
            listMap.put(listable.getListKey(), listable);
        }
        return listMap;
    }

    @Override
    public Listable getListItem(Locale locale, String listName, String listKey, Object... params)
            throws UnifyException {
        return getListMap(locale, listName, params).get(listKey);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    @SuppressWarnings("unchecked")
    private <T extends ListParam> List<? extends Listable> executeListCommand(String listName, Locale locale,
            Object... params) throws UnifyException {
        ListCommand<T> listCommand = (ListCommand<T>) getComponent(listName);
        return listCommand.execute(locale, DataUtils.constructDataObject(listCommand.getParamType(), params));
    }

    private <T extends ListParam> List<? extends Listable> executeListCommand(String listName, Locale locale,
            String filter, int limit, Object... params) throws UnifyException {
        return ListUtils.getSubList(executeListCommand(listName, locale, params), filter, limit);
    }

    public class StaticListInfo implements Listable {

        private Locale locale;

        private String name;

        private String description;

        private List<? extends Listable> list;

        private Map<String, Listable> map;

        public StaticListInfo(Locale locale, String name, String description) {
            this.locale = locale;
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<? extends Listable> getList() throws UnifyException {
            if (list == null) {
                synchronized (this) {
                    if (list == null) {
                        String keyValueList = getMessage(locale, name);
                        list = Collections.unmodifiableList(StringUtils.readStaticList(keyValueList));
                    }
                }
            }

            return list;
        }

        public List<? extends Listable> getList(String filter, int limit) throws UnifyException {
            return ListUtils.getSubList(getList(), filter, limit);
        }

        public Map<String, Listable> getMap() throws UnifyException {
            if (map == null) {
                synchronized (this) {
                    if (map == null) {
                        map = new LinkedHashMap<String, Listable>();
                        for (Listable listable : getList()) {
                            map.put(listable.getListKey(), listable);
                        }

                        map = Collections.unmodifiableMap(map);
                    }
                }
            }

            return map;
        }

        public Map<String, Listable> getMap(String filter, int limit) throws UnifyException {
            List<? extends Listable> list = getList(filter, limit);
            if (list == this.list) {
                return getMap();
            }

            Map<String, Listable> map = new LinkedHashMap<String, Listable>();
            for (Listable listable : list) {
                map.put(listable.getListKey(), listable);
            }
            return map;
        }

        @Override
        public String getListKey() {
            return name;
        }

        @Override
        public String getListDescription() {
            return description;
        }
    }
}
