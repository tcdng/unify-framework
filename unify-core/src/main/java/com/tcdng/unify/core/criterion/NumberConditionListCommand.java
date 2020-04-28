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
package com.tcdng.unify.core.criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.criterion.FilterConditionType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.list.AbstractZeroParamsListCommand;
import com.tcdng.unify.core.list.ZeroParams;

/**
 * Number condition list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("numberconditionlist")
public class NumberConditionListCommand extends AbstractZeroParamsListCommand {

    private final FactoryMap<Locale, List<Listable>> listMap  = new FactoryMap<Locale, List<Listable>>() {

        @Override
        protected List<Listable> create(Locale locale, Object... arg1) throws Exception {
            List<Listable> list = new ArrayList<Listable>();
            list.add(getListable(locale, FilterConditionType.EQUALS));
            list.add(getListable(locale, FilterConditionType.GREATER_THAN));
            list.add(getListable(locale, FilterConditionType.GREATER_OR_EQUAL));
            list.add(getListable(locale, FilterConditionType.LESS_THAN));
            list.add(getListable(locale, FilterConditionType.LESS_OR_EQUAL));
            list.add(getListable(locale, FilterConditionType.BETWEEN));
            list.add(getListable(locale, FilterConditionType.AMONGST));
            list.add(getListable(locale, FilterConditionType.IS_NULL));
            list.add(getListable(locale, FilterConditionType.IS_NOT_NULL));
            list.add(getListable(locale, FilterConditionType.NOT_EQUALS));
            list.add(getListable(locale, FilterConditionType.NOT_BETWEEN));
            list.add(getListable(locale, FilterConditionType.NOT_AMONGST));
            list.add(getListable(locale, FilterConditionType.EQUALS_FIELD));
            list.add(getListable(locale, FilterConditionType.GREATER_THAN_FIELD));
            list.add(getListable(locale, FilterConditionType.GREATER_OR_EQUAL_FIELD));
            list.add(getListable(locale, FilterConditionType.LESS_THAN_FIELD));
            list.add(getListable(locale, FilterConditionType.LESS_OR_EQUAL_FIELD));
            list.add(getListable(locale, FilterConditionType.BETWEEN_FIELD));
            list.add(getListable(locale, FilterConditionType.NOT_EQUALS_FIELD));
            list.add(getListable(locale, FilterConditionType.NOT_BETWEEN_FIELD));
            return list;
        }
        
        private Listable getListable(Locale locale, FilterConditionType filterConditionType)  throws UnifyException {
            return new ListData(filterConditionType.code(), getMessage(locale, filterConditionType.labelKey()));
        }
    };
    
    @Override
    public List<? extends Listable> execute(Locale locale, ZeroParams zeroParams) throws UnifyException {
        return listMap.get(locale);
    }

}
