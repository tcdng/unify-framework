/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.Pattern;

/**
 * Input control for presenting and capturing a date.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-date")
@UplAttributes({
        @UplAttribute(name = "buttonImgSrc", type = String.class, defaultVal = "$t{images/calendar.png}"),
        @UplAttribute(name = "buttonSymbol", type = String.class, defaultVal = "calendar-alt"),
        @UplAttribute(name = "formatter", type = Formatter.class, defaultVal = "$d{!dateformat style:customshort}"),
        @UplAttribute(name = "type", type = DateFieldType.class, defaultVal = "standard"),
        })
public class DateField extends AbstractTimeField {

    private String[] shortDayList;

    private String[] longMonthList;

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();
        Formatter<?> formatter = getFormatter();
        for (Pattern p : super.getPattern()) {
            if (!p.isFiller()) {
                if ('M' == p.getPattern().charAt(0)) {
                    DateTimeFormat monthDateTimeFormat = formatter.getFormatHelper()
                            .getSubPatternDateTimeFormat(p.getPattern(), formatter.getLocale());
                    longMonthList = monthDateTimeFormat.getSubPatternDescriptions();
                }
            }
        }

        shortDayList = formatter.getFormatHelper().getSubPatternDateTimeFormat("EEE", formatter.getLocale())
                .getSubPatternKeys();
    }

    @Override
    public Pattern[] getPattern() throws UnifyException {
        Pattern[] pattern = super.getPattern();
        for (Pattern p : pattern) {
            if (!p.isFiller()) {
                switch (p.getPattern().charAt(0)) {
                    case 'y':
                        p.setTarget("year_");
                        break;
                    case 'd':
                        p.setTarget("day_");
                        break;
                    case 'M':
                        p.setTarget("mon_");
                        break;
                    default:
                }
            }
        }
        return pattern;
    }

    public DateFieldType getType() throws UnifyException {
        return getUplAttribute(DateFieldType.class, "type");
    }
    
    public String[] getShortDayList() {
        return shortDayList;
    }

    public String[] getLongMonthList() {
        return longMonthList;
    }
}
