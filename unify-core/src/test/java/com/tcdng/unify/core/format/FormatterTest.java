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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.UnifyException;

/**
 * Formatter tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FormatterTest extends AbstractUnifyComponentTest {

	@Test
	public void testNumberFormatterGetNumberSymbols() throws Exception {
		DecimalFormatter numberFormatter = (DecimalFormatter) getUplComponent(Locale.ENGLISH, "!decimalformat");
		assertNotNull(numberFormatter.getNumberSymbols());
	}

	@Test
	public void testNumberFormatterGetPattern() throws Exception {
		DecimalFormatter numberFormatter = (DecimalFormatter) getUplComponent(Locale.ENGLISH, "!decimalformat");
		assertNotNull(numberFormatter.getPattern());
	}

	@Test
	public void testDateTimeFormatter() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 14);
		cal.set(Calendar.SECOND, 27);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 19);
		cal.set(Calendar.MONTH, Calendar.OCTOBER);
		cal.set(Calendar.YEAR, 2014);
		Date testDate = cal.getTime();

		DateTimeFormatter dateTimeFormatter = (DateTimeFormatter) getUplComponent(Locale.UK,
				"!datetimeformat style:default");
		assertEquals("19-Oct-2014 18:14:27", dateTimeFormatter.format(testDate));
		assertEquals(testDate, dateTimeFormatter.parse("19-Oct-2014 18:14:27"));

		dateTimeFormatter = (DateTimeFormatter) getUplComponent(Locale.UK, "!datetimeformat style:medium");
		assertEquals("19-Oct-2014 18:14:27", dateTimeFormatter.format(testDate));
		assertEquals(testDate, dateTimeFormatter.parse("19-Oct-2014 18:14:27"));

		dateTimeFormatter = (DateTimeFormatter) getUplComponent(Locale.UK, "!datetimeformat style:short");
		assertEquals("19/10/14 18:14", dateTimeFormatter.format(testDate));
		cal.set(Calendar.SECOND, 0);
		assertEquals(cal.getTime(), dateTimeFormatter.parse("19/10/14 18:14"));
	}

	@Test(expected = UnifyException.class)
	public void testDateTimeFormatterParsingWithInvalidDateTime() throws Exception {
		DateTimeFormatter dateTimeFormatter = (DateTimeFormatter) getUplComponent(Locale.UK,
				"!datetimeformat style:default");
		dateTimeFormatter.parse("October 19, 2014 44:14:27");
	}

	@Test
	public void testDecimalFormatter() throws Exception {
		DecimalFormatter decimalFormatter = (DecimalFormatter) getUplComponent(Locale.UK, "!decimalformat");
		assertEquals("5.5", decimalFormatter.format(BigDecimal.valueOf(5.5)));
		assertEquals("3000", decimalFormatter.format(BigDecimal.valueOf(3000)));
		assertEquals(BigDecimal.valueOf(270000), decimalFormatter.parse("270000"));
		assertFalse(BigDecimal.valueOf(270000).equals(decimalFormatter.parse("270,000")));// No
																							// grouping

		decimalFormatter.setGroupingUsed(true);
		assertEquals("3,000", decimalFormatter.format(BigDecimal.valueOf(3000)));
		assertEquals("28,900.206", decimalFormatter.format(BigDecimal.valueOf(28900.206)));
		assertEquals(BigDecimal.valueOf(45010), decimalFormatter.parse("45010"));
		assertEquals(BigDecimal.valueOf(270000), decimalFormatter.parse("270,000"));

		decimalFormatter.setScale(2);
		assertEquals("3,000.00", decimalFormatter.format(BigDecimal.valueOf(3000)));
		assertEquals("28,900.21", decimalFormatter.format(BigDecimal.valueOf(28900.206)));
		assertEquals(BigDecimal.valueOf(270000.17), decimalFormatter.parse("270,000.17"));
	}

	@Test(expected = UnifyException.class)
	public void testDecimalFormatterParsingWithInvalidString() throws Exception {
		DecimalFormatter decimalFormatter = (DecimalFormatter) getUplComponent(Locale.UK, "!decimalformat");
		decimalFormatter.parse("$20.50");
	}

	@Test
	public void testIntegerFormatter() throws Exception {
		IntegerFormatter integerFormatter = (IntegerFormatter) getUplComponent(Locale.UK, "!integerformat");
		assertEquals("3000", integerFormatter.format(3000));
		assertEquals(Integer.valueOf(270000), integerFormatter.parse("270000"));
		assertFalse(Integer.valueOf(270000).equals(integerFormatter.parse("270,000")));// No
																						// grouping

		integerFormatter.setGroupingUsed(true);
		assertEquals("3,000", integerFormatter.format(3000));
		assertEquals(Integer.valueOf(45010), integerFormatter.parse("45010"));
		assertEquals(Integer.valueOf(270000), integerFormatter.parse("270,000"));

		integerFormatter.setScale(2);
		assertEquals("3,000", integerFormatter.format(3000));
		assertEquals(Integer.valueOf(270000), integerFormatter.parse("270,000.17"));
	}

	@Test(expected = UnifyException.class)
	public void testIntegerFormatterParsingWithInvalidString() throws Exception {
		IntegerFormatter integerFormatter = (IntegerFormatter) getUplComponent(Locale.UK, "!integerformat");
		integerFormatter.parse("$20.50");
	}

	@Test
	public void testPercentFormatter() throws Exception {
		PercentFormatter percentFormatter = (PercentFormatter) getUplComponent(Locale.UK, "!percentformat");
		assertEquals("1%", percentFormatter.format(BigDecimal.valueOf(.01)));
		assertEquals(BigDecimal.valueOf(1), percentFormatter.parse("100%"));

		percentFormatter.setGroupingUsed(true);
		assertEquals("2,602%", percentFormatter.format(BigDecimal.valueOf(26.015)));
		assertEquals(BigDecimal.valueOf(20), percentFormatter.parse("2,000%"));
		assertEquals(BigDecimal.valueOf(2700), percentFormatter.parse("270,000%"));

		percentFormatter.setScale(2);
		assertEquals("1.20%", percentFormatter.format(BigDecimal.valueOf(0.012)));
		assertEquals("1.21%", percentFormatter.format(BigDecimal.valueOf(0.01206)));
		assertEquals(BigDecimal.valueOf(12.8517), percentFormatter.parse("1,285.17%"));
	}

	@Test
	public void testMessageFormatter() throws Exception {
		MessageFormatter messageFormatter = (MessageFormatter) getUplComponent(Locale.UK, "!messageformat");
		assertEquals("Hello World!", messageFormatter.format("Hello World!"));
		assertEquals("Hello World!", messageFormatter.format("$s{Hello World!}"));
		assertEquals("Message Format", messageFormatter.format("$m{format.message}"));
	}

	@Test(expected = UnifyException.class)
	public void testPercentFormatterParsingWithInvalidString() throws Exception {
		PercentFormatter percentFormatter = (PercentFormatter) getUplComponent(Locale.UK, "!percentformat");
		percentFormatter.parse("20.50");
	}

	@Test
	public void testSimpleDateFormatter() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 19);
		cal.set(Calendar.MONTH, Calendar.OCTOBER);
		cal.set(Calendar.YEAR, 2014);
		Date testDate = cal.getTime();

		DateFormatter dateFormatter = (DateFormatter) getUplComponent(Locale.UK, "!dateformat style:default");
		assertEquals("19-Oct-2014", dateFormatter.format(testDate));
		assertEquals(testDate, dateFormatter.parse("19-Oct-2014"));

		dateFormatter = (DateFormatter) getUplComponent(Locale.UK, "!dateformat style:medium");
		assertEquals("19-Oct-2014", dateFormatter.format(testDate));
		assertEquals(testDate, dateFormatter.parse("19-Oct-2014"));

		dateFormatter = (DateFormatter) getUplComponent(Locale.UK, "!dateformat style:short");
		assertEquals("19/10/14", dateFormatter.format(testDate));
		assertEquals(testDate, dateFormatter.parse("19/10/14"));
	}

	@Test(expected = UnifyException.class)
	public void testSimpleDateFormatterParsingWithInvalidDate() throws Exception {
		DateFormatter dateFormatter = (DateFormatter) getUplComponent(Locale.UK, "!dateformat style:default");
		dateFormatter.parse("19/Oct/2014");
	}

	@Test
	public void testTimeFormatter() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 14);
		cal.set(Calendar.SECOND, 27);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 1970);
		Date testTime = cal.getTime();

		TimeFormatter timeFormatter = (TimeFormatter) getUplComponent(Locale.UK, "!timeformat style:default");
		assertEquals("18:14:27", timeFormatter.format(testTime));
		assertEquals(testTime, timeFormatter.parse("18:14:27"));

		timeFormatter = (TimeFormatter) getUplComponent(Locale.UK, "!timeformat style:medium");
		assertEquals("18:14:27", timeFormatter.format(testTime));
		assertEquals(testTime, timeFormatter.parse("18:14:27"));

		timeFormatter = (TimeFormatter) getUplComponent(Locale.UK, "!timeformat style:short");
		assertEquals("18:14", timeFormatter.format(testTime));
		cal.set(Calendar.SECOND, 0);
		assertEquals(cal.getTime(), timeFormatter.parse("18:14"));
	}

	@Test(expected = UnifyException.class)
	public void testTimeFormatterParsingWithInvalidTime() throws Exception {
		TimeFormatter timeFormatter = (TimeFormatter) getUplComponent(Locale.UK, "!timeformat style:default");
		timeFormatter.parse("44 14:27");
	}

	@Test
	public void testFileSizeFormatter() throws Exception {
		FileSizeFormatter fileSizeFormatter = (FileSizeFormatter) getUplComponent(Locale.UK, "!filesizeformat");
		assertEquals("1000 Bytes", fileSizeFormatter.format(1000L));
		assertEquals("1.0 KB", fileSizeFormatter.format(1024L));
		assertEquals("9.6 MB", fileSizeFormatter.format(10094222L));
		assertEquals("896.5 MB", fileSizeFormatter.format(940094465L));
		assertEquals("10.2 GB", fileSizeFormatter.format(10940094465L));
		assertEquals("51.6 TB", fileSizeFormatter.format(56710940094465L));
		assertEquals("8.0 EB", fileSizeFormatter.format(Long.MAX_VALUE));
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
