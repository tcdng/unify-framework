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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.core.constant.FrequencyUnit;

/**
 * Calendar utilities tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class CalendarUtilsTest {

    @Test
	public void testGetFirstDayOfMonthDate() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date date = CalendarUtils.getFirstDayOfMonthDate(sdf.parse("07-02-2023"));
		assertNotNull(date);
		assertEquals("01-02-2023",  sdf.format(date));
		
		date = CalendarUtils.getFirstDayOfMonthDate(sdf.parse("07-02-2024"));
		assertNotNull(date);
		assertEquals("01-02-2024",  sdf.format(date));
	}

    @Test
	public void testGetLastDayOfMonthDate() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date date = CalendarUtils.getLastDayOfMonthDate(sdf.parse("07-02-2023"));
		assertNotNull(date);
		assertEquals("28-02-2023",  sdf.format(date));
		
		date = CalendarUtils.getLastDayOfMonthDate(sdf.parse("07-02-2024"));
		assertNotNull(date);
		assertEquals("29-02-2024",  sdf.format(date));
	}

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

    @Test
    public void testGetRawUTCOffset() throws Exception {
        Long rawOffset = CalendarUtils.getRawOffset(null);
        assertNull(rawOffset);

        rawOffset = CalendarUtils.getRawOffset("Africa/Luanda");
        assertNull(rawOffset);

        rawOffset = CalendarUtils.getRawOffset("00:00");
        assertEquals(Long.valueOf(0), rawOffset);

        rawOffset = CalendarUtils.getRawOffset("+00:00");
        assertEquals(Long.valueOf(0), rawOffset);

        rawOffset = CalendarUtils.getRawOffset("+01:00");
        assertEquals(Long.valueOf(1 * 60 * 60 * 1000), rawOffset);

        rawOffset = CalendarUtils.getRawOffset("+02:00");
        assertEquals(Long.valueOf(2 * 60 * 60 * 1000), rawOffset);

        rawOffset = CalendarUtils.getRawOffset("+02:45");
        assertEquals(Long.valueOf((2 * 60 + 45) * 60 * 1000), rawOffset);

        rawOffset = CalendarUtils.getRawOffset("-02:00");
        assertEquals(Long.valueOf(-(2 * 60 * 60 * 1000)), rawOffset);

        rawOffset = CalendarUtils.getRawOffset("-03:30");
        assertEquals(Long.valueOf(-((3 * 60 + 30) * 60 * 1000)), rawOffset);

        rawOffset = CalendarUtils.getRawOffset("-09:30");
        assertEquals(Long.valueOf(-((9 * 60 + 30) * 60 * 1000)), rawOffset);
    }

    @Test
    public void testNextEligibleDateNormalSkip() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.DAY_OF_YEAR, 1);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, null, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(cal1.get(Calendar.DAY_OF_YEAR) + 1, cal2.get(Calendar.DAY_OF_YEAR));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateNewYear() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        int daysInYear = cal1.getActualMaximum(Calendar.DAY_OF_YEAR);
        cal1.set(Calendar.DAY_OF_YEAR, daysInYear);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, null, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(1, cal2.get(Calendar.DAY_OF_YEAR));
        assertEquals(cal1.get(Calendar.YEAR) + 1, cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateMonthLimitsNormalSkip() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DATE, 1);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, null, new String[] { "Jan" }, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(cal1.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
        assertEquals(cal1.get(Calendar.DATE) + 1, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateMonthLimitsNewNextMonth() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DATE, 31);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, null, new String[] { "Jan", "Feb" }, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.FEBRUARY, cal2.get(Calendar.MONTH));
        assertEquals(1, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateMonthLimitsNewFarMonth() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DATE, 31);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, null, new String[] { "Jan", "Jun" }, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.JUNE, cal2.get(Calendar.MONTH));
        assertEquals(1, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateMonthLimitsNewYear() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JUNE);
        cal1.set(Calendar.DATE, 30);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, null, new String[] { "Feb", "Jun" }, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.FEBRUARY, cal2.get(Calendar.MONTH));
        assertEquals(1, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR) + 1, cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateWeekdayLimitsNormalSkip() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date nextDt = CalendarUtils.getNextEligibleDate(new String[] { "Mon", "Tue" }, null, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.TUESDAY, cal2.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void testNextEligibleDateWeekdayLimitsNewWeek() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date nextDt = CalendarUtils.getNextEligibleDate(new String[] { "Mon" }, null, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.MONDAY, cal2.get(Calendar.DAY_OF_WEEK));
        assertNotSame(cal1.get(Calendar.DATE), cal2.get(Calendar.DATE));
    }

    @Test
    public void testNextEligibleDateDayLimitsNormalSkip() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DATE, 1);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, new String[] { "1", "2" }, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.JANUARY, cal2.get(Calendar.MONTH));
        assertEquals(cal1.get(Calendar.DATE) + 1, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateDayLimitsFarDay() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DATE, 1);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, new String[] { "1", "18", "22" }, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.JANUARY, cal2.get(Calendar.MONTH));
        assertEquals(18, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateDayLimitsNewMonth() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DATE, 25);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, new String[] { "2", "18", "22" }, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.FEBRUARY, cal2.get(Calendar.MONTH));
        assertEquals(2, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateDayLimitsNewYear() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.DECEMBER);
        cal1.set(Calendar.DATE, 30);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, new String[] { "6", "18", "22" }, null, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.JANUARY, cal2.get(Calendar.MONTH));
        assertEquals(6, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR) + 1, cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateMonthLimitsDayLimitsNewMonth() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DATE, 25);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, new String[] { "4", "18", "22" },
                new String[] { "Apr", "Jun" }, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.APRIL, cal2.get(Calendar.MONTH));
        assertEquals(4, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    }

    @Test
    public void testNextEligibleDateMonthLimitsDayLimitsNewYear() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.MONTH, Calendar.DECEMBER);
        cal1.set(Calendar.DATE, 30);
        Date nextDt = CalendarUtils.getNextEligibleDate(null, new String[] { "6", "18", "22" },
                new String[] { "May", "Jun" }, cal1.getTime());
        assertNotNull(nextDt);
        cal2.setTime(nextDt);
        assertEquals(Calendar.MAY, cal2.get(Calendar.MONTH));
        assertEquals(6, cal2.get(Calendar.DATE));
        assertEquals(cal1.get(Calendar.YEAR) + 1, cal2.get(Calendar.YEAR));
    }
}
