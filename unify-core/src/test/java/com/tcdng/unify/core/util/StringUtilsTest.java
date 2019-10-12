/*
 * Copyright 2018-2019 The Code Department.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.util.StringUtils.StringToken;

/**
 * StringUtils tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class StringUtilsTest {

    @Test
    public void testCapitalize() throws Exception {
        assertNull(StringUtils.capitalizeFirstLetter(null));
        assertEquals("", StringUtils.capitalizeFirstLetter(""));
        assertEquals("Tom", StringUtils.capitalizeFirstLetter("tom"));
        assertEquals("Shehu", StringUtils.capitalizeFirstLetter("shehu"));
        assertEquals("Henry", StringUtils.capitalizeFirstLetter("Henry"));
    }

    @Test
    public void testDecapitalize() throws Exception {
        assertNull(StringUtils.decapitalize(null));
        assertEquals("", StringUtils.decapitalize(""));
        assertEquals("tom", StringUtils.decapitalize("Tom"));
        assertEquals("shehu", StringUtils.decapitalize("shehu"));
        assertEquals("henry", StringUtils.decapitalize("Henry"));
    }

    @Test
    public void testFlatten() throws Exception {
        assertNull(StringUtils.flatten(null));
        assertEquals("runontime", StringUtils.flatten("runOnTime"));
        assertEquals("the_sky_is_blue", StringUtils.flatten("The sky is blue"));
    }

    @Test
    public void testIsBlank() throws Exception {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("  "));
        assertFalse(StringUtils.isBlank("Snuffleupagus"));
    }

    @Test
    public void testIsNotBlank() throws Exception {
        assertFalse(StringUtils.isNotBlank(null));
        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank("  "));
        assertTrue(StringUtils.isNotBlank("Snuffleupagus"));
    }

    @Test
    public void testContainsWhitespace() throws Exception {
        assertFalse(StringUtils.containsWhitespace(null));
        assertFalse(StringUtils.containsWhitespace(""));
        assertFalse(StringUtils.containsWhitespace("Hello"));
        assertTrue(StringUtils.containsWhitespace(" "));
        assertTrue(StringUtils.containsWhitespace("Hello world!"));
        assertTrue(StringUtils.containsWhitespace("\t"));
        assertTrue(StringUtils.containsWhitespace("Hello\tworld!"));
    }

    @Test
    public void testPadLeft() throws Exception {
        assertEquals("0000000000", StringUtils.padLeft("", '0', 10));
        assertEquals("00001234", StringUtils.padLeft("1234", '0', 8));
        assertEquals("1234", StringUtils.padLeft("1234", '0', 3));
        assertEquals("1234", StringUtils.padLeft("1234", '0', 0));
        assertEquals("yyyyyyyyexit", StringUtils.padLeft("exit", 'y', 12));
    }

    @Test(expected = NullPointerException.class)
    public void testPadLeftWithNull() throws Exception {
        StringUtils.padLeft(null, '0', 20);
    }

    @Test
    public void testPadRight() throws Exception {
        assertEquals("0000000000", StringUtils.padRight("", '0', 10));
        assertEquals("12340000", StringUtils.padRight("1234", '0', 8));
        assertEquals("1234", StringUtils.padRight("1234", '0', 3));
        assertEquals("1234", StringUtils.padRight("1234", '0', 0));
        assertEquals("exityyyyyyyy", StringUtils.padRight("exit", 'y', 12));
    }

    @Test(expected = NullPointerException.class)
    public void testPadRightWithNull() throws Exception {
        StringUtils.padRight(null, '0', 20);
    }

    @Test
    public void testReadStaticList() throws Exception {
        List<ListData> list = StringUtils.readStaticList("");
        assertEquals(0, list.size());

        list = StringUtils.readStaticList("S~Second(s)|M~Minute(s)|H~Hour(s)");
        assertEquals(3, list.size());
        assertEquals("S", list.get(0).getListKey());
        assertEquals("Second(s)", list.get(0).getListDescription());
        assertEquals("M", list.get(1).getListKey());
        assertEquals("Minute(s)", list.get(1).getListDescription());
        assertEquals("H", list.get(2).getListKey());
        assertEquals("Hour(s)", list.get(2).getListDescription());
    }

    @Test
    public void testRemoveDuplicates() throws Exception {
        List<String> testValues1 = Arrays.asList("john", "mary", "", "tiger");
        List<String> result = StringUtils.removeDuplicates(testValues1);
        assertEquals(4, result.size());
        assertEquals("john", result.get(0));
        assertEquals("mary", result.get(1));
        assertEquals("", result.get(2));
        assertEquals("tiger", result.get(3));

        List<String> testValues2 = Arrays.asList("mary", "", "john", "tiger", "tiger", "john", "tiger", "");
        result = StringUtils.removeDuplicates(testValues2);
        assertEquals(4, result.size());
        assertEquals("mary", result.get(0));
        assertEquals("", result.get(1));
        assertEquals("john", result.get(2));
        assertEquals("tiger", result.get(3));
    }

    @Test
    public void testWhiteSpaceSplit() throws Exception {
        String testValue1 = "";
        String[] result = StringUtils.whiteSpaceSplit(testValue1);
        assertEquals(0, result.length);

        String testValue2 = "     ";
        result = StringUtils.whiteSpaceSplit(testValue2);
        assertEquals(0, result.length);

        String testValue3 = "I \tDig    ";
        result = StringUtils.whiteSpaceSplit(testValue3);
        assertEquals(2, result.length);
        assertEquals("I", result[0]);
        assertEquals("Dig", result[1]);

        String testValue4 = " I Dig    ";
        result = StringUtils.whiteSpaceSplit(testValue4);
        assertEquals(2, result.length);
        assertEquals("I", result[0]);
        assertEquals("Dig", result[1]);
    }

    @Test
    public void testGetCommaSeparatedString() throws Exception {
        String[] testValue1 = {};
        String result = StringUtils.buildCommaSeparatedString(testValue1, false);
        assertEquals("", result);

        String[] testValue2 = { " " };
        result = StringUtils.buildCommaSeparatedString(testValue2, false);
        assertEquals(" ", result);

        String[] testValue3 = { "Mary", "100", "23.05" };
        result = StringUtils.buildCommaSeparatedString(testValue3, false);
        assertEquals("Mary,100,23.05", result);

        String[] testValue4 = { "Mary", "100", "23.05", "" };
        result = StringUtils.buildCommaSeparatedString(testValue4, false);
        assertEquals("Mary,100,23.05,", result);

        String[] testValue5 = { "", "", "" };
        result = StringUtils.buildCommaSeparatedString(testValue5, false);
        assertEquals(",,", result);

        String[] testValue6 = { "a,b,c" };
        result = StringUtils.buildCommaSeparatedString(testValue6, false);
        assertEquals("\"a,b,c\"", result);

        String[] testValue7 = { "a,b,c", "" };
        result = StringUtils.buildCommaSeparatedString(testValue7, false);
        assertEquals("\"a,b,c\",", result);

        String[] testValue8 = { "a,b,c", "d", "e" };
        result = StringUtils.buildCommaSeparatedString(testValue8, false);
        assertEquals("\"a,b,c\",d,e", result);

        String[] testValue9 = { "", "a,b,c", "d", "e" };
        result = StringUtils.buildCommaSeparatedString(testValue9, false);
        assertEquals(",\"a,b,c\",d,e", result);
    }

    @Test
    public void testGetCommaSeparatedStringEnclosed() throws Exception {
        String[] testValue1 = {};
        String result = StringUtils.buildCommaSeparatedString(testValue1, true);
        assertEquals("[]", result);

        String[] testValue2 = { " " };
        result = StringUtils.buildCommaSeparatedString(testValue2, true);
        assertEquals("[ ]", result);

        String[] testValue3 = { "Mary", "100", "23.05" };
        result = StringUtils.buildCommaSeparatedString(testValue3, true);
        assertEquals("[Mary,100,23.05]", result);

        String[] testValue4 = { "Mary", "100", "23.05", "" };
        result = StringUtils.buildCommaSeparatedString(testValue4, true);
        assertEquals("[Mary,100,23.05,]", result);

        String[] testValue5 = { "", "", "" };
        result = StringUtils.buildCommaSeparatedString(testValue5, true);
        assertEquals("[,,]", result);

        String[] testValue6 = { "a,b,c" };
        result = StringUtils.buildCommaSeparatedString(testValue6, true);
        assertEquals("[\"a,b,c\"]", result);

        String[] testValue7 = { "a,b,c", "" };
        result = StringUtils.buildCommaSeparatedString(testValue7, true);
        assertEquals("[\"a,b,c\",]", result);

        String[] testValue8 = { "a,b,c", "d", "e" };
        result = StringUtils.buildCommaSeparatedString(testValue8, true);
        assertEquals("[\"a,b,c\",d,e]", result);

        String[] testValue9 = { "", "a,b,c", "d", "e" };
        result = StringUtils.buildCommaSeparatedString(testValue9, true);
        assertEquals("[,\"a,b,c\",d,e]", result);
    }

    @Test
    public void testGetCommaSeparatedValues() throws Exception {
        String testValue1 = "";
        String[] result = StringUtils.getCommaSeparatedValues(testValue1);
        assertEquals(0, result.length);

        String testValue2 = "    ";
        result = StringUtils.getCommaSeparatedValues(testValue2);
        assertEquals(1, result.length);
        assertEquals("    ", result[0]);

        String testValue3 = "Mary,100,23.05";
        result = StringUtils.getCommaSeparatedValues(testValue3);
        assertEquals(3, result.length);
        assertEquals("Mary", result[0]);
        assertEquals("100", result[1]);
        assertEquals("23.05", result[2]);

        String testValue4 = "Mary,100,23.05,";
        result = StringUtils.getCommaSeparatedValues(testValue4);
        assertEquals(4, result.length);
        assertEquals("Mary", result[0]);
        assertEquals("100", result[1]);
        assertEquals("23.05", result[2]);
        assertEquals("", result[3]);

        String testValue5 = ",,";
        result = StringUtils.getCommaSeparatedValues(testValue5);
        assertEquals(3, result.length);
        assertEquals("", result[0]);
        assertEquals("", result[1]);
        assertEquals("", result[2]);

        String testValue6 = "\"a,b,c\"";
        result = StringUtils.getCommaSeparatedValues(testValue6);
        assertEquals(1, result.length);
        assertEquals("a,b,c", result[0]);

        String testValue7 = "\"a,b,c\",";
        result = StringUtils.getCommaSeparatedValues(testValue7);
        assertEquals(2, result.length);
        assertEquals("a,b,c", result[0]);
        assertEquals("", result[1]);

        String testValue8 = "\"a,b,c\",d,e";
        result = StringUtils.getCommaSeparatedValues(testValue8);
        assertEquals(3, result.length);
        assertEquals("a,b,c", result[0]);
        assertEquals("d", result[1]);
        assertEquals("e", result[2]);

        String testValue9 = ",\"a,b,c\",d,e";
        result = StringUtils.getCommaSeparatedValues(testValue9);
        assertEquals(4, result.length);
        assertEquals("", result[0]);
        assertEquals("a,b,c", result[1]);
        assertEquals("d", result[2]);
        assertEquals("e", result[3]);
    }

    @Test
    public void testBreakdownParameterizedStringNull() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString(null);
        assertNotNull(tokenList);
        assertTrue(tokenList.isEmpty());
    }

    @Test
    public void testBreakdownParameterizedStringEmpty() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("");
        assertNotNull(tokenList);
        assertTrue(tokenList.isEmpty());
    }

    @Test
    public void testBreakdownParameterizedStringBlank() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("   ");
        assertNotNull(tokenList);
        assertEquals(0, tokenList.size());
    }

    @Test
    public void testBreakdownParameterizedStringNoParameters() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("Hello world!");
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());

        assertEquals("Hello world!", tokenList.get(0).getToken());
    }

    @Test
    public void testBreakdownParameterizedStringSingleParameter() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {color} sky.");
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("The ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(1);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(2);
        assertEquals(" sky.", token.getToken());
        assertFalse(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringSingleParameterOnly() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("{color}");
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringMultipleParametersOnly() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("{color}{adj}");
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(1);
        assertEquals("adj", token.getToken());
        assertTrue(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringMultipleParameterTogether() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {color}{adj} sky.");
        assertNotNull(tokenList);
        assertEquals(4, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("The ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(1);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(2);
        assertEquals("adj", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(3);
        assertEquals(" sky.", token.getToken());
        assertFalse(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringMultipleParameterSeparate() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {color} sky {adj}.");
        assertNotNull(tokenList);
        assertEquals(5, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("The ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(1);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(2);
        assertEquals(" sky ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(3);
        assertEquals("adj", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(4);
        assertEquals(".", token.getToken());
        assertFalse(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringMultipleParameterEdge() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("{color}The sky.{adj}");
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(1);
        assertEquals("The sky.", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(2);
        assertEquals("adj", token.getToken());
        assertTrue(token.isParam());
    }
    
    @Test
    public void testFirstNonBlank() throws Exception {
        assertNull(StringUtils.getFirstNonBlank());
        assertNull(StringUtils.getFirstNonBlank((String) null));
        assertNull(StringUtils.getFirstNonBlank(""));
        assertNull(StringUtils.getFirstNonBlank("", (String) null));
        assertNull(StringUtils.getFirstNonBlank("  "));
        assertEquals("Red", StringUtils.getFirstNonBlank("Red"));
        assertEquals("Orange", StringUtils.getFirstNonBlank("Orange", (String) null));
        assertEquals("Yellow", StringUtils.getFirstNonBlank(" ", "Yellow", (String) null));
        assertEquals("Green", StringUtils.getFirstNonBlank(" ", "Green"));
        assertEquals("Blue", StringUtils.getFirstNonBlank(" ", "Blue", "Green"));
        assertEquals("Indigo", StringUtils.getFirstNonBlank("Indigo", "Blue", "Green"));
        assertEquals("Violet", StringUtils.getFirstNonBlank((String) null, "  ", "Violet"));
    }
}
