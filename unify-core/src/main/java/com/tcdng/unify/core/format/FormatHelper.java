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
package com.tcdng.unify.core.format;

import java.util.Date;
import java.util.Locale;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A format helper class. Provides utility methods for manipulating and
 * obtaining formatting information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface FormatHelper extends UnifyComponent {

	String yyyyMMdd_HHmmss = "yyyyMMdd_HHmmss";

	/**
	 * Returns the number symbols for specified number type and locale.
	 * 
	 * @param numberType
	 *            the number type
	 * @param locale
	 *            the locale
	 * @throws UnifyException
	 *             if an error occurs
	 */
	NumberSymbols getNumberSymbols(NumberType numberType, Locale locale) throws UnifyException;

	/**
	 * Returns the date-time format object for supplied sub pattern and locale.
	 * 
	 * @param subPattern
	 *            the date time sub pattern
	 * @param locale
	 *            the locale
	 * @throws UnifyException
	 *             if an error occurs
	 */
	DateTimeFormat getSubPatternDateTimeFormat(String subPattern, Locale locale) throws UnifyException;

	/**
	 * Returns true if the supplied string is a supported date-time sub pattern.
	 * 
	 * @param subPattern
	 *            the string to test
	 * @throws UnifyException
	 *             if an error occurs
	 */
	boolean isSupportedDateTimeSubPattern(String subPattern) throws UnifyException;

	/**
	 * Returns the long year version of a date pattern. For instance an input
	 * pattern of <em>dd-MM-yy<em> would return <em>dd-MM-yyyy<em>.
	 * 
	 * @param pattern
	 *            the date pattern, which should contain at least a year element
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getDatePatternWithLongYear(String pattern) throws UnifyException;

	/**
	 * Splits a date pattern into its sub-elements. For instance an input pattern of
	 * <em>dd-MM-yy<em> would be split to five sub-patterns; "dd" a date
	 * sub-pattern, "-" a filler pattern, "MM" a date sub-pattern, "-" another
	 * filler pattern and finally "yy" a date sub-pattern.
	 * 
	 * @param pattern
	 *            the pattern to split
	 * @return the sub-patterns, returned in same sequence as in supplied pattern.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	Pattern[] splitDatePattern(String pattern) throws UnifyException;

	/**
	 * Reconstructs a date pattern from a set of sub-elements.
	 * 
	 * @param subPatterns
	 *            the sub patterns
	 * @return the reconstructed date pattern
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String reconstructDatePattern(Pattern[] subPatterns) throws UnifyException;

	/**
	 * Returns formatted string of current time using supplied pattern.
	 * 
	 * @param pattern
	 *            the date-time pattern
	 * @return the formatted date
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String formatNow(String pattern) throws UnifyException;

	/**
	 * Returns formatted string of specified time using supplied pattern.
	 * 
	 * @param pattern
	 *            the date-time pattern
	 * @param date
	 *            the time to format
	 * @return the formatted date
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String format(String pattern, Date date) throws UnifyException;
}
