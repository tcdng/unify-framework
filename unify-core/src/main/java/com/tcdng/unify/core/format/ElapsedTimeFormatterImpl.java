/*
 * Copyright 2018-2025 The Code Department.
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

import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Default implementation of an elapsed time formatter.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = "elapsedtimeformat", description = "$m{format.elapsedtime}")
public class ElapsedTimeFormatterImpl extends AbstractFormatter<Date> implements ElapsedTimeFormatter {

    public ElapsedTimeFormatterImpl() {
        super(Date.class);
    }

    @Override
    protected String doFormat(Date fromDate) throws UnifyException {
        CalendarUtils.DateDifference difference = CalendarUtils.getDateDifference(fromDate, new Date());
        String elapsedTime = getSessionMessage("elapsedtimeformat.message", difference.getDays(), difference.getHours(),
                difference.getMinutes());
        return elapsedTime;
    }

    @Override
    protected Date doParse(String string) throws UnifyException {
        return null;
    }

    @Override
    public String getPattern() throws UnifyException {
        return null;
    }
}
