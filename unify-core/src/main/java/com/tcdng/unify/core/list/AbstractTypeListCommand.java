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
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Abstract base class for a component type list command. Gets the
 * configurations of all components of a particular type and uses the name and
 * description values to form a list of listables, which is returned on execute.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractTypeListCommand<T extends UnifyComponent> extends AbstractZeroParamsListCommand {

    private Class<T> typeClass;

    private FactoryMap<Locale, List<Listable>> typeListMap;

    public AbstractTypeListCommand(Class<T> typeClazz) {
        this.typeClass = typeClazz;
        typeListMap = new FactoryMap<Locale, List<Listable>>() {

            @Override
            protected List<Listable> create(Locale key, Object... params) throws Exception {
                List<Listable> list = new ArrayList<Listable>();
                for (UnifyComponentConfig unifyComponentConfig : getComponentConfigs(typeClass)) {
                    list.add(new ListData(unifyComponentConfig.getName(),
                            resolveSessionMessage(unifyComponentConfig.getDescription())));
                }
                DataUtils.sort(list, Listable.class, "listDescription", true);
                return list;
            }
        };
    }

    @Override
    public List<? extends Listable> execute(Locale locale, ZeroParams params) throws UnifyException {
        return typeListMap.get(locale);
    }
}
