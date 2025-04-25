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
package com.tcdng.unify.core.list;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.MonthInYear;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Month-in-year list command.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("monthinyearlist")
public class MonthInYearListCommand extends AbstractZeroParamsListCommand {

    private static final String[] SHORT_MONTHS;

    static {
        SHORT_MONTHS = new String[12];
        SHORT_MONTHS[0] = MonthInYear.JANUARY.code();
        SHORT_MONTHS[1] = MonthInYear.FEBRUARY.code();
        SHORT_MONTHS[2] = MonthInYear.MARCH.code();
        SHORT_MONTHS[3] = MonthInYear.APRIL.code();
        SHORT_MONTHS[4] = MonthInYear.MAY.code();
        SHORT_MONTHS[5] = MonthInYear.JUNE.code();
        SHORT_MONTHS[6] = MonthInYear.JULY.code();
        SHORT_MONTHS[7] = MonthInYear.AUGUST.code();
        SHORT_MONTHS[8] = MonthInYear.SEPTEMBER.code();
        SHORT_MONTHS[9] = MonthInYear.OCTOBER.code();
        SHORT_MONTHS[10] = MonthInYear.NOVEMBER.code();
        SHORT_MONTHS[11] = MonthInYear.DECEMBER.code();

    }
    private FactoryMap<Locale, List<? extends Listable>> monthInYear;

    public MonthInYearListCommand() {
        monthInYear = new FactoryMap<Locale, List<? extends Listable>>() {

            @Override
            protected List<? extends Listable> create(Locale locale, Object... params) throws Exception {
                List<ListData> list = new ArrayList<ListData>();

                DateFormatSymbols dfs = new DateFormatSymbols(locale);
                String months[] = dfs.getMonths();

                if (months != null && months.length >= SHORT_MONTHS.length) {
                    for (int i = 0; i < SHORT_MONTHS.length; i++) {
                        list.add(new ListData(SHORT_MONTHS[i], StringUtils.capitalizeFirstLetter(months[i])));
                    }
                }

                return list;
            }

        };
    }

    @Override
    public List<? extends Listable> execute(Locale locale, ZeroParams params) throws UnifyException {
        return monthInYear.get(locale);
    }
}
