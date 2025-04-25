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
package com.tcdng.unify.core.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.DateStyleConstants;
import com.tcdng.unify.core.data.SimpleDateFormatPool;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Abstract base class for a date formatter.
 * 
 * @author The Code Department
 * @since 4.1
 */
@UplAttributes({ @UplAttribute(name = "style", type = String.class),
        @UplAttribute(name = "timeZoneId", type = String.class), @UplAttribute(name = "pattern", type = String.class) })
public abstract class AbstractDateFormatter extends AbstractFormatter<Date> implements DateFormatter {

    protected enum TYPE {
        DATE, TIME, DATETIME, FIXED
    };

    private TYPE type;

    private String pattern;

    private SimpleDateFormatPool sdfp;

    private static final Map<String, Integer> dateStyleMap = new HashMap<String, Integer>();

    static {
        dateStyleMap.put(DateStyleConstants.DEFAULT_STYLE, DateFormat.DEFAULT);
        dateStyleMap.put(DateStyleConstants.SHORT_STYLE, DateFormat.SHORT);
        dateStyleMap.put(DateStyleConstants.MEDIUM_STYLE, DateFormat.MEDIUM);
        dateStyleMap.put(DateStyleConstants.LONG_STYLE, DateFormat.LONG);
        dateStyleMap.put(DateStyleConstants.FULL_STYLE, DateFormat.FULL);
    }

    protected AbstractDateFormatter(TYPE type) {
        super(Date.class);
        this.type = type;
    }

    @Override
    public String getPattern() throws UnifyException {
        getSimpleDateFormatPool();
        return pattern;
    }

    @Override
    protected String doFormat(Date date) throws UnifyException {
        return getSimpleDateFormatPool().format(date);
    }

    @Override
    protected Date doParse(String string) throws UnifyException {
        return getSimpleDateFormatPool().parse(string);
    }

    private SimpleDateFormatPool getSimpleDateFormatPool() throws UnifyException {
        if (sdfp == null) {
            sdfp = CalendarUtils.getSimpleDateFormatPool(getSessionContext().getLocale(), getDatePattern());
        }
        return sdfp;
    }

    private String getDatePattern() throws UnifyException {
        DateFormat df = null;

        switch (type) {
            case DATETIME:
                Integer styleId = getStyleId();
                df = DateFormat.getDateTimeInstance(styleId, styleId, getLocale());
                break;
            case TIME:
                df = DateFormat.getTimeInstance(getStyleId(), getLocale());
                break;
            case DATE:
                df = DateFormat.getDateInstance(getStyleId(), getLocale());
                break;
            case FIXED:
            default:
                String uplPattern = getUplAttribute(String.class, "pattern");
                df = new SimpleDateFormat(uplPattern, getLocale());
                break;
        }

        String timeZoneId = getUplAttribute(String.class, "timeZoneId");
        if (timeZoneId != null) {
            df.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        }

        SimpleDateFormat sdf = (SimpleDateFormat) df;
        if (DateStyleConstants.CUSTOMSHORT_STYLE.equals(getUplAttribute(String.class, "style"))) {
            sdf.applyPattern(getFormatHelper().getDatePatternWithLongYear(sdf.toPattern()));
        }

        pattern = sdf.toPattern();
        return pattern;
    }

    private Integer getStyleId() throws UnifyException {
        Integer id = null;
        String style = getUplAttribute(String.class, "style");
        if (style != null) {
            String actStyle = style;
            if (DateStyleConstants.CUSTOMSHORT_STYLE.equals(actStyle)) {
                actStyle = DateStyleConstants.SHORT_STYLE;
            }
            id = dateStyleMap.get(actStyle);
        }
        if (id == null) {
            id = dateStyleMap.get(DateStyleConstants.DEFAULT_STYLE);
        }
        return id;
    }
}
