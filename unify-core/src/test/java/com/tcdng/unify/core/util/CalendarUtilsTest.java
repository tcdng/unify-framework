/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.core.constant.FrequencyUnit;

/**
 * Calendar utilities tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class CalendarUtilsTest {

    @Test
    public void testGetMilliSecondsByFrequency() throws Exception {
        assertEquals(1000L, CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.SECOND, 1));
        assertEquals(5000L, CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.SECOND, 5));
        assertEquals(60000L, CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.MINUTE, 1));
        assertEquals(480000L, CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.MINUTE, 8));
        assertEquals(2520000L, CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.MINUTE, 42));
        assertEquals(3600000L, CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.HOUR, 1));
        assertEquals(32400000L, CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.HOUR, 9));
    }

    @Test
    public void testGetDateDifference() throws Exception {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();

        cal.add(Calendar.MINUTE, 22);
        Date newDate = cal.getTime();
        CalendarUtils.DateDifference difference = CalendarUtils.getDateDifference(now, newDate);
        assertEquals(0, difference.getDays());
        assertEquals(0, difference.getHours());
        assertEquals(22, difference.getMinutes());
        assertEquals(0, difference.getSeconds());

        cal.add(Calendar.HOUR, 18);
        newDate = cal.getTime();
        difference = CalendarUtils.getDateDifference(now, newDate);
        assertEquals(0, difference.getDays());
        assertEquals(18, difference.getHours());
        assertEquals(22, difference.getMinutes());
        assertEquals(0, difference.getSeconds());

        cal.add(Calendar.HOUR, 7);
        newDate = cal.getTime();
        difference = CalendarUtils.getDateDifference(now, newDate);
        assertEquals(1, difference.getDays());
        assertEquals(1, difference.getHours());
        assertEquals(22, difference.getMinutes());
        assertEquals(0, difference.getSeconds());

        cal.add(Calendar.MINUTE, 40);
        newDate = cal.getTime();
        difference = CalendarUtils.getDateDifference(now, newDate);
        assertEquals(1, difference.getDays());
        assertEquals(2, difference.getHours());
        assertEquals(2, difference.getMinutes());
        assertEquals(0, difference.getSeconds());
    }
}
