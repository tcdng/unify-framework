/*
 * Copyright 2018-2024 The Code Department.
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Abstract base class for parameter component type list command.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractParamTypeListCommand<T extends ListParam> extends AbstractListCommand<T> {

    private Map<Class<? extends UnifyComponent>, FactoryMap<Locale, List<Listable>>> typeListMap;

    public AbstractParamTypeListCommand(Class<T> paramType) {
        super(paramType);
        typeListMap = new HashMap<Class<? extends UnifyComponent>, FactoryMap<Locale, List<Listable>>>();
    }

    @Override
    public List<? extends Listable> execute(Locale locale, T params) throws UnifyException {
        Class<? extends UnifyComponent> type = getTypeFromParam(params);
        if (type != null) {
            FactoryMap<Locale, List<Listable>> factoryMap = typeListMap.get(type);
            if (factoryMap == null) {
                synchronized (AbstractParamTypeListCommand.class) {
                    factoryMap = typeListMap.get(type);
                    if (factoryMap == null) {
                        factoryMap = new FactoryMap<Locale, List<Listable>>()
                            {
                                @SuppressWarnings("unchecked")
                                @Override
                                protected List<Listable> create(Locale key, Object... params) throws Exception {
                                    List<Listable> list = new ArrayList<Listable>();
                                    Class<? extends UnifyComponent> type = (Class<? extends UnifyComponent>) params[0];
                                    for (UnifyComponentConfig unifyComponentConfig : getComponentConfigs(type)) {
                                        String description = unifyComponentConfig.getDescription() != null
                                                ? resolveSessionMessage(unifyComponentConfig.getDescription())
                                                : unifyComponentConfig.getName();
                                        list.add(new ListData(unifyComponentConfig.getName(), description));
                                    }
                                    DataUtils.sortAscending(list, Listable.class, "listDescription");
                                    return list;
                                }
                            };

                        typeListMap.put(type, factoryMap);
                    }
                }
            }

            return factoryMap.get(locale, type);
        }

        return Collections.emptyList();
    }

    protected abstract Class<? extends UnifyComponent> getTypeFromParam(T params) throws UnifyException;
}
