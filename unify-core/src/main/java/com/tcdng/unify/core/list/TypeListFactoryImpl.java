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
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Default implementation of type list factory.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_TYPELISTFACTORY)
public class TypeListFactoryImpl extends AbstractUnifyComponent implements TypeListFactory {

    private FactoryMap<Class<? extends UnifyComponent>, TypeListFactoryMap> typeListMap;

    public TypeListFactoryImpl() {
        typeListMap = new FactoryMap<Class<? extends UnifyComponent>, TypeListFactoryMap>()
            {

                @Override
                protected TypeListFactoryMap create(Class<? extends UnifyComponent> typeClass, Object... params)
                        throws Exception {
                    return new TypeListFactoryMap(typeClass);
                }

            };
    }

    @Override
    public List<? extends Listable> getTypeList(Locale locale, Class<? extends UnifyComponent> typeClass)
            throws UnifyException {
        return typeListMap.get(typeClass).get(locale);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private class TypeListFactoryMap extends FactoryMap<Locale, List<Listable>> {

        private Class<? extends UnifyComponent> typeClass;

        public TypeListFactoryMap(Class<? extends UnifyComponent> typeClass) {
            this.typeClass = typeClass;
        }

        @Override
        protected List<Listable> create(Locale locale, Object... params) throws Exception {
            List<Listable> list = new ArrayList<Listable>();
            for (UnifyComponentConfig unifyComponentConfig : getComponentConfigs(typeClass)) {
                String description = unifyComponentConfig.getDescription() != null
                        ? resolveSessionMessage(unifyComponentConfig.getDescription())
                        : unifyComponentConfig.getName();
                list.add(new ListData(unifyComponentConfig.getName(), description));
            }
            DataUtils.sortAscending(list, Listable.class, "listDescription");
            return list;
        }

    }
}
