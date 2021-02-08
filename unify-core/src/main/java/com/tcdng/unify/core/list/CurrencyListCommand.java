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
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Currency list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("currencylist")
public class CurrencyListCommand extends AbstractListCommand<CurrencyListParams> {

    private FactoryMap<Locale, List<ListData>> currencyListByLocale;

    public CurrencyListCommand() {
        super(CurrencyListParams.class);
        currencyListByLocale = new FactoryMap<Locale, List<ListData>>() {

            @Override
            protected List<ListData> create(Locale locale, Object... params) throws Exception {
                List<ListData> list = new ArrayList<ListData>();
                for (Currency currency : Currency.getAvailableCurrencies()) {
                    list.add(new ListData(currency.getCurrencyCode(), currency.getDisplayName(locale)));
                }

                DataUtils.sortAscending(list, ListData.class, "listDescription");
                return list;
            }

        };
    }

    @Override
    public List<? extends Listable> execute(Locale locale, CurrencyListParams params) throws UnifyException {
        if (params.isPresent()) {
            List<ListData> list = new ArrayList<ListData>();
            for (String currencyCode : params.getCurrencyCodes()) {
                Currency currency = Currency.getInstance(currencyCode);
                list.add(new ListData(currency.getCurrencyCode(), currency.getDisplayName(locale)));
            }

            DataUtils.sortAscending(list, ListData.class, "listDescription");
            return list;
        }

        return currencyListByLocale.get(locale);
    }

}
