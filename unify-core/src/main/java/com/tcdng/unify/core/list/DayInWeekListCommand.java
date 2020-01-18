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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.DayInWeek;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Day-in-week list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("dayinweeklist")
public class DayInWeekListCommand extends AbstractZeroParamsListCommand {

    private static final String[] SHORT_DAYS;

    static {
        SHORT_DAYS = new String[7];
        SHORT_DAYS[0] = DayInWeek.SUNDAY.code();
        SHORT_DAYS[1] = DayInWeek.MONDAY.code();
        SHORT_DAYS[2] = DayInWeek.TUESDAY.code();
        SHORT_DAYS[3] = DayInWeek.WEDNESDAY.code();
        SHORT_DAYS[4] = DayInWeek.THURSDAY.code();
        SHORT_DAYS[5] = DayInWeek.FRIDAY.code();
        SHORT_DAYS[6] = DayInWeek.SATURDAY.code();

    }
    private FactoryMap<Locale, List<? extends Listable>> dayInWeek;

    public DayInWeekListCommand() {
        dayInWeek = new FactoryMap<Locale, List<? extends Listable>>() {

            @Override
            protected List<? extends Listable> create(Locale locale, Object... params) throws Exception {
                List<ListData> list = new ArrayList<ListData>();

                DateFormatSymbols dfs = new DateFormatSymbols(locale);
                String weekdays[] = dfs.getWeekdays();
                if (weekdays != null && weekdays.length >= SHORT_DAYS.length) {
                    for (int i = 0; i < SHORT_DAYS.length;) {
                        list.add(new ListData(SHORT_DAYS[i], StringUtils.capitalizeFirstLetter(weekdays[++i])));
                    }
                }
                return list;
            }

        };
    }

    @Override
    public List<? extends Listable> execute(Locale locale, ZeroParams params) throws UnifyException {
        return dayInWeek.get(locale);
    }
}
