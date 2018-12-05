/*
 * Copyright 2014 The Code Department
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.core.UnifyException;

/**
 * Default format helper tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FormatHelperImplTest {

	@Test
	public void testGetDatePatternWithLongYear() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();

		assertEquals("dd-MM-yyyy", dfh.getDatePatternWithLongYear("dd-MM-yy"));
		assertEquals("dd/MM/yyyy", dfh.getDatePatternWithLongYear("dd/MM/yy"));
		assertEquals("MMM dd, yyyy z", dfh.getDatePatternWithLongYear("MMM dd, yy z"));
		assertEquals("'yy' dd MMMM yyyy", dfh.getDatePatternWithLongYear("'yy' dd MMMM y"));
	}

	@Test
	public void testGetSubPatternDateTimeFormat() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();

		DateTimeFormat dateTimeFormat = dfh.getSubPatternDateTimeFormat("HH", Locale.ENGLISH);
		assertEquals("HH", dateTimeFormat.getSubPattern());
		assertEquals(0, dateTimeFormat.getRange()[0]);
		assertEquals(23, dateTimeFormat.getRange()[1]);
		assertNull(dateTimeFormat.getList());

		dateTimeFormat = dfh.getSubPatternDateTimeFormat("MM", Locale.ENGLISH);
		assertEquals("MM", dateTimeFormat.getSubPattern());
		assertNotNull(dateTimeFormat.getList());
		assertNull(dateTimeFormat.getRange());

		dateTimeFormat = dfh.getSubPatternDateTimeFormat("EEE", Locale.ENGLISH);
		assertEquals("EEE", dateTimeFormat.getSubPattern());
		assertNotNull(dateTimeFormat.getList());
		assertNull(dateTimeFormat.getRange());

		dateTimeFormat = dfh.getSubPatternDateTimeFormat("a", Locale.ENGLISH);
		assertEquals("a", dateTimeFormat.getSubPattern());
		assertNotNull(dateTimeFormat.getList());
		assertNull(dateTimeFormat.getRange());
	}

	@Test(expected = UnifyException.class)
	public void testGetSubPatternDateTimeFormatForUnsupported() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();
		dfh.getSubPatternDateTimeFormat("yyyy", Locale.ENGLISH);
	}

	@Test
	public void testIsSupportedDateTimeSubPattern() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();

		assertTrue(dfh.isSupportedDateTimeSubPattern("HH"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("H"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("kk"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("k"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("KK"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("K"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("hh"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("h"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("mm"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("m"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("ss"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("s"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("M"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("MM"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("EEE"));
		assertTrue(dfh.isSupportedDateTimeSubPattern("a"));
	}

	@Test
	public void testUnupportedDateTimeSubPattern() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();

		assertFalse(dfh.isSupportedDateTimeSubPattern("yyyy"));
		assertFalse(dfh.isSupportedDateTimeSubPattern("yy"));
		assertFalse(dfh.isSupportedDateTimeSubPattern("MMM"));
		assertFalse(dfh.isSupportedDateTimeSubPattern("MMMM"));
	}

	@Test
	public void getNumberSymbolsForAllNumberTypesForLocale() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();

		NumberSymbols ns = dfh.getNumberSymbols(NumberType.PERCENT, Locale.UK);
		assertEquals("-", ns.getNegativePrefix());
		assertEquals("", ns.getPositivePrefix());
		assertEquals("%", ns.getNegativeSuffix());
		assertEquals("%", ns.getPositiveSuffix());
		assertUkSeparatorsAndGroupSizel(ns);

		ns = dfh.getNumberSymbols(NumberType.DECIMAL, Locale.UK);
		assertEquals("-", ns.getNegativePrefix());
		assertEquals("", ns.getPositivePrefix());
		assertEquals("", ns.getNegativeSuffix());
		assertEquals("", ns.getPositiveSuffix());
		assertUkSeparatorsAndGroupSizel(ns);

		ns = dfh.getNumberSymbols(NumberType.INTEGER, Locale.UK);
		assertEquals("-", ns.getNegativePrefix());
		assertEquals("", ns.getPositivePrefix());
		assertEquals("", ns.getNegativeSuffix());
		assertEquals("", ns.getPositiveSuffix());
		assertUkSeparatorsAndGroupSizel(ns);
	}

	@Test
	public void testSplitDatePattern() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();

		Pattern[] subPatterns = dfh.splitDatePattern("dd/MM/yyyy");
		assertEquals(5, subPatterns.length);

		assertEquals("dd", subPatterns[0].getPattern());
		assertFalse(subPatterns[0].isFiller());
		assertFalse(subPatterns[0].isQuoted());

		assertEquals("/", subPatterns[1].getPattern());
		assertTrue(subPatterns[1].isFiller());
		assertFalse(subPatterns[1].isQuoted());

		assertEquals("MM", subPatterns[2].getPattern());
		assertFalse(subPatterns[2].isFiller());
		assertFalse(subPatterns[2].isQuoted());

		assertEquals("/", subPatterns[3].getPattern());
		assertTrue(subPatterns[3].isFiller());
		assertFalse(subPatterns[3].isQuoted());

		assertEquals("yyyy", subPatterns[4].getPattern());
		assertFalse(subPatterns[4].isFiller());
		assertFalse(subPatterns[4].isQuoted());

		subPatterns = dfh.splitDatePattern("HH '' 'o''clock'");
		assertEquals(3, subPatterns.length);

		assertEquals("HH", subPatterns[0].getPattern());
		assertFalse(subPatterns[0].isFiller());
		assertFalse(subPatterns[0].isQuoted());

		assertEquals(" ' ", subPatterns[1].getPattern());
		assertTrue(subPatterns[1].isFiller());
		assertFalse(subPatterns[1].isQuoted());

		assertEquals("o'clock", subPatterns[2].getPattern());
		assertTrue(subPatterns[2].isFiller());
		assertTrue(subPatterns[2].isQuoted());
	}

	@Test
	public void testReconstructDatePattern() throws Exception {
		FormatHelperImpl dfh = new FormatHelperImpl();

		Pattern[] subPatterns = new Pattern[5];
		subPatterns[0] = new Pattern("MMMM", false);
		subPatterns[1] = new Pattern(" ", true);
		subPatterns[2] = new Pattern("dd", false);
		subPatterns[3] = new Pattern(", ", true);
		subPatterns[4] = new Pattern("yyyy", false);
		assertEquals("MMMM dd, yyyy", dfh.reconstructDatePattern(subPatterns));

		subPatterns = new Pattern[3];
		subPatterns[0] = new Pattern("HH", false);
		subPatterns[1] = new Pattern(" ' ", true);
		subPatterns[2] = new Pattern("o'clock", true, true);
		assertEquals("HH '' 'o''clock'", dfh.reconstructDatePattern(subPatterns));
	}

	private void assertUkSeparatorsAndGroupSizel(NumberSymbols ns) throws Exception {
		assertEquals('.', ns.getDecimalSeparator());
		assertEquals(',', ns.getGroupingSeparator());
		assertEquals(3, ns.getGroupSize());
	}
}
