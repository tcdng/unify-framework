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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.DayInWeek;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.constant.MonthInYear;
import com.tcdng.unify.core.data.LocaleFactoryMaps;
import com.tcdng.unify.core.data.SimpleDateFormatPool;

/**
 * Provides utility methods for date manipulation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class CalendarUtils {

	private static final String RFC822_DATEFORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

	private static final LocaleFactoryMaps<String, SimpleDateFormatPool> simpleDateFormatPoolMap = new LocaleFactoryMaps<String, SimpleDateFormatPool>() {

		@Override
		protected SimpleDateFormatPool createObject(Locale locale, String format, Object... params) throws Exception {
			return new SimpleDateFormatPool(format, locale);
		}

	};

	private CalendarUtils() {

	}

	/**
	 * Tests if supplied date is within calendar.
	 * 
	 * @param weekdays
	 *            the calendar week days. Can be null
	 * @param days
	 *            the calendar days. Can be null
	 * @param months
	 *            the calendar months. Can be null
	 * @param date
	 *            the date to test for
	 */
	public static boolean isWithinCalendar(String[] weekdays, String[] days, String[] months, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		String weekDay = DayInWeek.byJavaCalendarIndex(cal.get(Calendar.DAY_OF_WEEK)).code();
		String month = MonthInYear.byJavaCalendarIndex(cal.get(Calendar.MONTH)).code();
		return CalendarUtils.isEmptyOrInclusive(weekdays, weekDay) && CalendarUtils.isEmptyOrInclusive(days, day)
				&& CalendarUtils.isEmptyOrInclusive(months, month);
	}

	/**
	 * Returns the current midnight date.
	 */
	public static Date getCurrentMidnightDate() {
		return CalendarUtils.getMidnightDate(new Date());
	}

	/**
	 * Returns the midnight date for a specified date using default locale. A
	 * midnight date is any date at 12:00:00AM.
	 * 
	 * @param date
	 *            the supplied date
	 */
	public static Date getMidnightDate(Date date) {
		return getMidnightDate(date, Locale.getDefault());
	}

	/**
	 * Returns the midnight date for a specified date using specified locale. A
	 * midnight date is any date at 12:00:00AM.
	 * 
	 * @param date
	 *            the supplied date
	 * @param locale
	 *            the locale
	 */
	public static Date getMidnightDate(Date date, Locale locale) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Returns the last second date value of supplied date using default locale. A
	 * last second date is any date at 11:59:00PM.
	 * 
	 * @param date
	 *            the date convert
	 */
	public static Date getLastSecondDate(Date date) {
		return getLastSecondDate(date, Locale.getDefault());
	}

	/**
	 * Returns the last second date value of supplied date using specified locale. A
	 * last second date is any date at 11:59:00PM.
	 * 
	 * @param date
	 *            the date convert
	 * @param locale
	 *            the locale
	 */
	public static Date getLastSecondDate(Date date, Locale locale) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Returns the date with time-only elements of supplied date using default
	 * locale.
	 * 
	 * @param date
	 *            the date to convert
	 * @throws UnifyException
	 *             if a parse error occurs
	 */
	public static Date getTimeOfDay(Date date) throws UnifyException {
		return getTimeOfDay(date, Locale.getDefault());
	}

	/**
	 * Returns the date with time-only elements of supplied date using locale.
	 * 
	 * @param date
	 *            the date to convert
	 * @param locale
	 *            the locale
	 * @throws UnifyException
	 *             if a parse error occurs
	 */
	public static Date getTimeOfDay(Date date, Locale locale) throws UnifyException {
		try {
			if (date == null) {
				return null;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", locale);
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			throw new UnifyException(UnifyCoreErrorConstants.UTIL_ERROR, e);
		}
	}

	/**
	 * Calculates now plus offset.
	 * 
	 * @param unit
	 *            the offset unit
	 * @param offsetCount
	 *            the offset count
	 * @return the calculated time
	 * @throws UnifyException
	 *             if an error occurs
	 */
	public static Date getNowWithFrequencyOffset(FrequencyUnit unit, int offsetCount) throws UnifyException {
		return new Date(new Date().getTime() + CalendarUtils.getMilliSecondsByFrequency(unit, offsetCount));
	}

	/**
	 * Gets now plus offset.
	 * 
	 * @param offset
	 *            the offset in milliseconds
	 * @return the calculated time
	 */
	public static Date getNowWithOffset(long offset) {
		return new Date(new Date().getTime() + offset);
	}

	/**
	 * Calculates date with offset.
	 * 
	 * @param date
	 *            the supplied date
	 * @param unit
	 *            the offset unit
	 * @param offsetCount
	 *            the offset count
	 * @return the calculated time
	 * @throws UnifyException
	 *             if an error occurs
	 */
	public static Date getDateWithFrequencyOffset(Date date, FrequencyUnit unit, int offsetCount)
			throws UnifyException {
		return new Date(date.getTime() + CalendarUtils.getMilliSecondsByFrequency(unit, offsetCount));
	}

	/**
	 * Gets date plus offset.
	 * 
	 * @param date
	 *            the supplied date
	 * @param offset
	 *            the offset in milliseconds
	 * @return the calculated time
	 */
	public static Date getDateWithOffset(Date date, long offset) {
		return new Date(date.getTime() + offset);
	}

	/**
	 * Converts frequency units to milliseconds.
	 * 
	 * @param unit
	 *            the frequency unit.
	 * @param frequency
	 *            the frequency
	 * @return the value in milliseconds.
	 * @throws UnifyException
	 *             if supplied unit is invalid
	 * @see FrequencyUnit
	 */
	public static long getMilliSecondsByFrequency(FrequencyUnit unit, int frequency) throws UnifyException {
		if (FrequencyUnit.SECOND.equals(unit)) {
			return frequency * 1000;
		} else if (FrequencyUnit.MINUTE.equals(unit)) {
			return frequency * 60 * 1000;
		} else if (FrequencyUnit.HOUR.equals(unit)) {
			return frequency * 60 * 60 * 1000;
		}
		throw new UnifyException(UnifyCoreErrorConstants.INVALID_FREQUENCY_UNIT, unit);
	}

	/**
	 * Returns a cached simple date format pool for specified locale and pattern.
	 * 
	 * @param locale
	 *            the supplied locale
	 * @param pattern
	 *            the supplied pattern
	 * @throws UnifyException
	 *             if an error occurs
	 */
	public static SimpleDateFormatPool getSimpleDateFormatPool(Locale locale, String pattern) throws UnifyException {
		return simpleDateFormatPoolMap.get(locale, pattern);
	}

	/**
	 * Formats a date using default locale and supplied date pattern.
	 * 
	 * @param format
	 *            the date pattern
	 * @param date
	 *            the date to format
	 * @return the formatted date
	 * @throws UnifyException
	 *             if an error occurs
	 */
	public static String format(String format, Date date) throws UnifyException {
		return simpleDateFormatPoolMap.get(Locale.getDefault(), format).format(date);
	}

	/**
	 * Parses a date string using supplied format.
	 * 
	 * @param format
	 *            the format to use
	 * @param dateString
	 *            the date string to parse
	 * @return parsed date
	 * @throws UnifyException
	 *             if an error occurs
	 */
	public static Date parse(String format, String dateString) throws UnifyException {
		return simpleDateFormatPoolMap.get(Locale.getDefault(), format).parse(dateString);
	}

	/**
	 * Parses an RFC822 date string using supplied format.
	 * 
	 * @param dateString
	 *            the date string to parse
	 * @return parsed date
	 * @throws UnifyException
	 *             if an error occurs
	 */
	public static Date parseRfc822Date(String dateString) throws UnifyException {
		return CalendarUtils.parse(RFC822_DATEFORMAT, dateString);
	}

	private static boolean isEmptyOrInclusive(String[] values, String value) {
		if (values != null && values.length > 0) {
			for (String arrVal : values) {
				if (value.equals(arrVal)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public static DateDifference getDateDifference(Date olderDate, Date newerDate) {
		return CalendarUtils.getDateDifference(newerDate.getTime() - olderDate.getTime());
	}

	public static DateDifference getDateDifference(long timeInMilliSecs) {
		int seconds = (int) (timeInMilliSecs / 1000 % 60);
		int minutes = (int) (timeInMilliSecs / (60 * 1000) % 60);
		int hours = (int) (timeInMilliSecs / (60 * 60 * 1000) % 24);
		int days = (int) (timeInMilliSecs / (24 * 60 * 60 * 1000));
		return new DateDifference(days, hours, minutes, seconds);
	}

	public static class DateDifference {

		private int days;
		private int hours;
		private int minutes;
		private int seconds;

		public DateDifference(int days, int hours, int minutes, int seconds) {
			this.days = days;
			this.hours = hours;
			this.minutes = minutes;
			this.seconds = seconds;
		}

		public int getDays() {
			return days;
		}

		public int getHours() {
			return hours;
		}

		public int getMinutes() {
			return minutes;
		}

		public int getSeconds() {
			return seconds;
		}
	}
}
