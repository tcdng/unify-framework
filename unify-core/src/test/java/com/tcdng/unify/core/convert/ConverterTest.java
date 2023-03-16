/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.core.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.convert.converters.BigDecimalConverter;
import com.tcdng.unify.convert.converters.BooleanConverter;
import com.tcdng.unify.convert.converters.ByteConverter;
import com.tcdng.unify.convert.converters.CharacterConverter;
import com.tcdng.unify.convert.converters.Converter;
import com.tcdng.unify.convert.converters.DateConverter;
import com.tcdng.unify.convert.converters.DoubleConverter;
import com.tcdng.unify.convert.converters.FloatConverter;
import com.tcdng.unify.convert.converters.IntegerConverter;
import com.tcdng.unify.convert.converters.LongConverter;
import com.tcdng.unify.convert.converters.ShortConverter;
import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.data.IndexedTarget;
import com.tcdng.unify.core.format.DateFormatter;
import com.tcdng.unify.core.format.DecimalFormatter;
import com.tcdng.unify.core.upl.UplElementReferences;

/**
 * Converter tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ConverterTest extends AbstractUnifyComponentTest {

    private DecimalFormatter defaultDecimalFormatter;

    private DateFormatter dateFormatter;

    @Test
    public void testBigDecimalConverterWithValidParameters() throws Exception {
        Converter<BigDecimal> converter = new BigDecimalConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertEquals(BigDecimal.valueOf(10), converter.convert(Integer.valueOf(10), null));
        assertEquals(BigDecimal.valueOf(145.38), converter.convert(Double.valueOf(145.38), null));
        assertEquals(BigDecimal.valueOf(3567.6789), converter.convert("3567.6789", null));
        assertEquals(BigDecimal.valueOf(11002.254), converter.convert("11,002.254", defaultDecimalFormatter));
    }

    @Test(expected = Exception.class)
    public void testBigDecimalConverterWithInvalidParameters() throws Exception {
        Converter<BigDecimal> converter = new BigDecimalConverter();
        converter.convert("11,002.254", null); // Expects a formatter
    }

    @Test
    public void testBooleanConverter() throws Exception {
        Converter<Boolean> converter = new BooleanConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertNull(converter.convert(BigDecimal.valueOf(20), null));
        assertEquals(Boolean.FALSE, converter.convert(false, null));
        assertEquals(Boolean.TRUE, converter.convert(true, null));
        assertEquals(Boolean.TRUE, converter.convert("Y", null));
        assertEquals(Boolean.TRUE, converter.convert("Yes", null));
        assertEquals(Boolean.TRUE, converter.convert("on", null));
        assertEquals(Boolean.TRUE, converter.convert("True", null));
        assertEquals(Boolean.FALSE, converter.convert("False", null));
        assertEquals(Boolean.FALSE, converter.convert("No", null));
        assertEquals(Boolean.FALSE, converter.convert("", null));
        assertEquals(Boolean.FALSE, converter.convert("Internet", null));
    }

    @Test
    public void testByteConverterWithValidParameters() throws Exception {
        Converter<Byte> converter = new ByteConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertEquals(Byte.valueOf((byte) 10), converter.convert(Integer.valueOf(10), null));
        assertEquals(Byte.valueOf((byte) 85), converter.convert(Double.valueOf(85.38), null));
        assertEquals(Byte.valueOf((byte) 20), converter.convert("20", null));
    }

    @Test(expected = Exception.class)
    public void testByteConverterWithInvalidParameters() throws Exception {
        Converter<Byte> converter = new ByteConverter();
        converter.convert("200.00", null);
    }

    @Test
    public void testClassConverterWithValidParameters() throws Exception {
        Converter<Class<?>> converter = new ClassConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertNull(converter.convert(16, null));
        assertEquals(String.class, converter.convert(String.class, null));
        assertEquals(Float.class, converter.convert(Float.class, null));
        assertEquals(BigDecimal.class, converter.convert("java.math.BigDecimal", null));
        assertEquals(String.class, converter.convert("java.lang.String", null));
    }

    @Test(expected = Exception.class)
    public void testClassConverterWithInvalidParameters() throws Exception {
        Converter<Class<?>> converter = new ClassConverter();
        assertEquals(String.class, converter.convert("String", null));
    }

    @Test
    public void testDateConverterWithValidParameters() throws Exception {
        Converter<Date> converter = new DateConverter();
        assertNull(converter.convert(null, null));
        final Date now = new Date();
        assertEquals(now, converter.convert(now, null));
        Date date = converter.convert("October 18, 2014", dateFormatter);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(Calendar.OCTOBER, cal.get(Calendar.MONTH));
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(2014, cal.get(Calendar.YEAR));
        
        Date date2 = converter.convert(now.getTime(), null);
        assertNotNull(date2);
        assertEquals(now, date2);
    }

    @Test(expected = Exception.class)
    public void testDateConverterWithInvalidParameters() throws Exception {
        Converter<Date> converter = new DateConverter();
        converter.convert("Some Date", dateFormatter);
    }

    @Test
    public void testDoubleConverterWithValidParameters() throws Exception {
        Converter<Double> converter = new DoubleConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertEquals(Double.valueOf(10), converter.convert(Integer.valueOf(10), null));
        assertEquals(Double.valueOf(145.38), converter.convert(Double.valueOf(145.38), null));
        assertEquals(Double.valueOf(3567.6789), converter.convert("3567.6789", null));
        assertEquals(Double.valueOf(11002.254), converter.convert("11,002.254", defaultDecimalFormatter));
    }

    @Test(expected = Exception.class)
    public void testDoubleConverterWithInvalidParameters() throws Exception {
        Converter<Double> converter = new DoubleConverter();
        converter.convert("11,002.254", null); // Expects a formatter
    }

    @Test
    public void testFloatConverterWithValidParameters() throws Exception {
        Converter<Float> converter = new FloatConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertEquals(Float.valueOf(10), converter.convert(Integer.valueOf(10), null));
        assertEquals(Float.valueOf((float) 145.38), converter.convert(Double.valueOf(145.38), null));
        assertEquals(Float.valueOf((float) 3567.6789), converter.convert("3567.6789", null));
        assertEquals(Float.valueOf((float) 11002.254), converter.convert("11,002.254", defaultDecimalFormatter));
    }

    @Test(expected = Exception.class)
    public void testFloatConverterWithInvalidParameters() throws Exception {
        Converter<Float> converter = new FloatConverter();
        converter.convert("11,002.254", null); // Expects a formatter
    }

    @Test
    public void testIntegerConverterWithValidParameters() throws Exception {
        Converter<Integer> converter = new IntegerConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertEquals(Integer.valueOf(10), converter.convert(Integer.valueOf(10), null));
        assertEquals(Integer.valueOf(145), converter.convert(Double.valueOf(145.38), null));
        assertEquals(Integer.valueOf(3567), converter.convert("3567", null));
        assertEquals(Integer.valueOf(11002), converter.convert("11,002.254", defaultDecimalFormatter));
    }

    @Test
    public void testIntegerConverterWithInvalidParameters() throws Exception {
        Converter<Integer> converter = new IntegerConverter();
        int val = converter.convert("3567.6789", null);
        assertEquals(3567, val);
    }

    @Test
    public void testLongConverterWithValidParameters() throws Exception {
        Converter<Long> converter = new LongConverter();
        assertNull(converter.convert(null, null));
        assertNotNull(converter.convert(new Date(), null));
        assertEquals(Long.valueOf(10), converter.convert(Integer.valueOf(10), null));
        assertEquals(Long.valueOf(145), converter.convert(Double.valueOf(145.38), null));
        assertEquals(Long.valueOf(3567), converter.convert("3567", null));
        assertEquals(Long.valueOf(11002), converter.convert("11,002.254", defaultDecimalFormatter));
    }

    @Test(expected = Exception.class)
    public void testLongConverterWithInvalidParameters() throws Exception {
        Converter<Long> converter = new LongConverter();
        converter.convert("3567.6789", null); // Expects a formatter
    }

    @Test
    public void testShortConverterWithValidParameters() throws Exception {
        Converter<Short> converter = new ShortConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertEquals(Short.valueOf((short) 10), converter.convert(Integer.valueOf(10), null));
        assertEquals(Short.valueOf((short) 145), converter.convert(Double.valueOf(145.38), null));
        assertEquals(Short.valueOf((short) 3567), converter.convert("3567", null));
        assertEquals(Short.valueOf((short) 11002), converter.convert("11,002.254", defaultDecimalFormatter));
    }

    @Test(expected = Exception.class)
    public void testShortConverterWithInvalidParameters() throws Exception {
        Converter<Short> converter = new ShortConverter();
        converter.convert("3567.6789", null); // Expects a formatter
    }

    @Test
    public void testCharacterConverter() throws Exception {
        Converter<Character> converter = new CharacterConverter();
        assertNull(converter.convert(null, null));
        assertNull(converter.convert(new Date(), null));
        assertEquals(new Character('M'), converter.convert('M', null));
        assertEquals(new Character('H'), converter.convert("Hello", null));
        assertEquals(Character.valueOf((char) 10), converter.convert(Integer.valueOf(10), null));
    }

    @Test
    public void testStringConverter() throws Exception {
        Converter<String> converter = new StringConverter();
        assertNull(converter.convert(null, null));
        assertNotNull(converter.convert(new Date(), null));
        assertNotNull(converter.convert(new Date(), dateFormatter));
        assertEquals("Hello", converter.convert("Hello", null));
        assertEquals("10", converter.convert(Integer.valueOf(10), null));
        assertEquals("145.38", converter.convert(Double.valueOf(145.38), null));
        assertEquals("true", converter.convert(Boolean.TRUE, null));
        assertEquals("false", converter.convert(Boolean.FALSE, null));
        assertEquals("A,B", converter.convert(new String[]{"A", "B"}, null));
    }

    @Test
    public void testUplElementReferencesConverter() throws Exception {
        Converter<UplElementReferences> converter = new UplElementReferencesConverter();
        UplElementReferences uerA = new UplElementReferences(new String[] { "The", "Sky", "is" });
        UplElementReferences uerB = new UplElementReferences(new String[] { "Blue", "today" });
        UplElementReferences uerC = new UplElementReferences(new String[] { "only" });
        List<UplElementReferences> uerList = new ArrayList<UplElementReferences>();
        uerList.add(uerA);
        uerList.add(uerB);
        uerList.add(uerC);
        UplElementReferences uerD = converter.convert(uerList, null);
        assertNotNull(uerD);
        List<String> ids = uerD.getIds();
        assertNotNull(ids);
        assertEquals(6, ids.size());
        assertEquals("The", ids.get(0));
        assertEquals("Sky", ids.get(1));
        assertEquals("is", ids.get(2));
        assertEquals("Blue", ids.get(3));
        assertEquals("today", ids.get(4));
        assertEquals("only", ids.get(5));
    }

    @Test
    public void testIndexedTargetConverter() throws Exception {
        Converter<IndexedTarget> converter = new IndexedTargetConverter();
        IndexedTarget target = converter.convert("", null);
        assertNotNull(target);
        assertEquals("", target.getTarget());
        assertEquals(-1, target.getIndex());
        
        target = converter.convert("subtract", null);
        assertNotNull(target);
        assertEquals("subtract", target.getTarget());
        assertEquals(-1, target.getIndex());
        
        target = converter.convert(":5", null);
        assertNotNull(target);
        assertEquals("", target.getTarget());
        assertEquals(5, target.getIndex());
        
        target = converter.convert("add:1062", null);
        assertNotNull(target);
        assertEquals("add", target.getTarget());
        assertEquals(1062, target.getIndex());       
    }

    @Override
    protected void onSetup() throws Exception {
        defaultDecimalFormatter = (DecimalFormatter) getUplComponent(Locale.ENGLISH, "!decimalformat");
        defaultDecimalFormatter.setGroupingUsed(true);
        dateFormatter = (DateFormatter) getUplComponent(Locale.ENGLISH, "!dateformat style:long");
    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
