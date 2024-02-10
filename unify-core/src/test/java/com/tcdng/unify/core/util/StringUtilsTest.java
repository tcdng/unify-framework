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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.common.constants.StandardFormatType;
import com.tcdng.unify.common.util.NewlineToken;
import com.tcdng.unify.common.util.ParamToken;
import com.tcdng.unify.common.util.StringToken;
import com.tcdng.unify.common.util.TextToken;
import com.tcdng.unify.core.data.ListData;

/**
 * StringUtils tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class StringUtilsTest {

	@Test
	public void testReplaceFirstNull() throws Exception {
        assertNull(StringUtils.replaceFirst(null, null, null));
        assertNull(StringUtils.replaceFirst(null, "abc", null));
        assertNull(StringUtils.replaceFirst(null, "abc", "def"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReplaceFirstNullReplace() throws Exception {
        assertNull(StringUtils.replaceFirst("Check abc characters", "abc", null));
	}
	
	@Test
	public void testReplaceFirst() throws Exception {
        assertEquals("", StringUtils.replaceFirst("", "abc", "def"));
        assertEquals("Check characters", StringUtils.replaceFirst("Check characters", "abc", "def"));
        assertEquals("Check def characters", StringUtils.replaceFirst("Check abc characters", "abc", "def"));
        assertEquals("Check characters def", StringUtils.replaceFirst("Check characters abc", "abc", "def"));
        assertEquals("Check def characters abc", StringUtils.replaceFirst("Check abc characters abc", "abc", "def"));
	}

	@Test
	public void testReplaceLastNull() throws Exception {
        assertNull(StringUtils.replaceLast(null, null, null));
        assertNull(StringUtils.replaceLast(null, "abc", null));
        assertNull(StringUtils.replaceLast(null, "abc", "def"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReplaceLastNullReplace() throws Exception {
        assertNull(StringUtils.replaceLast("Check abc characters", "abc", null));
	}
	
	@Test
	public void testReplaceLast() throws Exception {
        assertEquals("", StringUtils.replaceLast("", "abc", "def"));
        assertEquals("Check characters", StringUtils.replaceLast("Check characters", "abc", "def"));
        assertEquals("Check def characters", StringUtils.replaceLast("Check abc characters", "abc", "def"));
        assertEquals("Check characters def", StringUtils.replaceLast("Check characters abc", "abc", "def"));
        assertEquals("Check abc characters def", StringUtils.replaceLast("Check abc characters abc", "abc", "def"));
	}
	
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
    public void testToNonNullString() throws Exception {
        assertEquals("Tom", StringUtils.toNonNullString("Tom", "Harry"));
        assertEquals("Harry", StringUtils.toNonNullString(null, "Harry"));
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
    public void testCommaSplit() throws Exception {
        String[] result = StringUtils.commaSplit(null);
        assertEquals(0, result.length);

        String testValue1 = "";
        result = StringUtils.commaSplit(testValue1);
        assertEquals(0, result.length);

        String testValue2 = "     ";
        result = StringUtils.commaSplit(testValue2);
        assertEquals(1, result.length);
        assertEquals("     ", result[0]);

        String testValue3 = "I,Dig,It";
        result = StringUtils.commaSplit(testValue3);
        assertEquals(3, result.length);
        assertEquals("I", result[0]);
        assertEquals("Dig", result[1]);
        assertEquals("It", result[2]);

        String testValue4 = ",I Dig";
        result = StringUtils.commaSplit(testValue4);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("I Dig", result[1]);

        String testValue5 = ",I Dig,";
        result = StringUtils.commaSplit(testValue5);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("I Dig", result[1]);

        String testValue6 = ",,";
        result = StringUtils.commaSplit(testValue6);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    public void testDotSplit() throws Exception {
        String[] result = StringUtils.dotSplit(null);
        assertEquals(0, result.length);

        String testValue1 = "";
        result = StringUtils.dotSplit(testValue1);
        assertEquals(0, result.length);

        String testValue2 = "     ";
        result = StringUtils.dotSplit(testValue2);
        assertEquals(1, result.length);
        assertEquals("     ", result[0]);

        String testValue3 = "I.Dig.It";
        result = StringUtils.dotSplit(testValue3);
        assertEquals(3, result.length);
        assertEquals("I", result[0]);
        assertEquals("Dig", result[1]);
        assertEquals("It", result[2]);

        String testValue4 = ".I Dig";
        result = StringUtils.dotSplit(testValue4);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("I Dig", result[1]);

        String testValue5 = ".I Dig.";
        result = StringUtils.dotSplit(testValue5);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("I Dig", result[1]);

        String testValue6 = "..";
        result = StringUtils.dotSplit(testValue6);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("", result[1]);
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
	public void testBuildParameterizedString() throws Exception {
		List<StringToken> tokens = Arrays.asList(new TextToken("Hello "), ParamToken.getParamToken("name"),
				new TextToken(" ID="), ParamToken.getGeneratorParamToken("id-generator"), new TextToken(" dob="),
				ParamToken.getFormattedParamToken(StandardFormatType.DATE_YYYYMMDD_DASH, "dob"), new NewlineToken());
		String pstring = StringUtils.buildParameterizedString(tokens);
		assertEquals("Hello {{name}} ID={{g:id-generator}} dob={{dob#DYD}}\n", pstring);
	}

    @Test
	public void testBuildParameterizedStringCustomGenerator() throws Exception {
		List<StringToken> tokens = Arrays.asList(new TextToken("Hello "), ParamToken.getParamToken("name"),
				new TextToken(" ID="), ParamToken.getGeneratorParamToken("cgen", "id-generator"), new TextToken(" dob="),
				ParamToken.getFormattedParamToken(StandardFormatType.DATE_YYYYMMDD_DASH, "dob"), new NewlineToken());
		String pstring = StringUtils.buildParameterizedString(tokens);
		assertEquals("Hello {{name}} ID={{cgen:id-generator}} dob={{dob#DYD}}\n", pstring);
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
        assertEquals(1, tokenList.size());
        assertEquals("   ", tokenList.get(0).getToken());
    }

    @Test
    public void testBreakdownParameterizedStringNoParameters() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("Hello world!");
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        assertEquals("Hello world!", tokenList.get(0).getToken());
    }

    @Test
    public void testBreakdownParameterizedStringNewline() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("Hello!\n");
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        assertEquals("Hello!", tokenList.get(0).getToken());
        assertEquals("\n", tokenList.get(1).getToken());
        
        tokenList = StringUtils.breakdownParameterizedString("\n");
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        assertEquals("\n", tokenList.get(0).getToken());
        
        tokenList = StringUtils.breakdownParameterizedString("\n\n\n");
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());
        assertEquals("\n", tokenList.get(0).getToken());
        assertEquals("\n", tokenList.get(1).getToken());
        assertEquals("\n", tokenList.get(2).getToken());
        
        tokenList = StringUtils.breakdownParameterizedString("\nHello\nWorld\n");
        assertNotNull(tokenList);
        assertEquals(5, tokenList.size());
        assertEquals("\n", tokenList.get(0).getToken());
        assertEquals("Hello", tokenList.get(1).getToken());
        assertEquals("\n", tokenList.get(2).getToken());
        assertEquals("World", tokenList.get(3).getToken());
        assertEquals("\n", tokenList.get(4).getToken());
                
        tokenList = StringUtils.breakdownParameterizedString("\nHello!");
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        assertEquals("\n", tokenList.get(0).getToken());
        assertEquals("Hello!", tokenList.get(1).getToken());
    }

    @Test
    public void testBreakdownParameterizedStringSingleParameter() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {{color}} sky.");
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
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("{{color}}");
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringMultipleParametersOnly() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("{{color}}{{adj}}");
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
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {{color}}{{adj}} sky.");
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
    public void testBreakdownParameterizedStringImplicitGeneratorParameter() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {{:color}} sky.");
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("The ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(1);
        assertEquals("g:color", token.getToken());       
        assertTrue(token.isParam());
        assertTrue(token.isGeneratorParam());
        assertEquals("g", ((ParamToken) token).getComponent());       
        

        token = tokenList.get(2);
        assertEquals(" sky.", token.getToken());
        assertFalse(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringCustomParameter() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {{m:color}} sky.");
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("The ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(1);
        assertEquals("m:color", token.getToken());       
        assertTrue(token.isParam());
        assertTrue(token.isGeneratorParam());
        assertEquals("m", ((ParamToken) token).getComponent());       
        

        token = tokenList.get(2);
        assertEquals(" sky.", token.getToken());
        assertFalse(token.isParam());
    }

    @Test
    public void testBreakdownParameterizedStringBigCustomParameter() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {{map:color}} sky.");
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("The ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(1);
        assertEquals("map:color", token.getToken());       
        assertTrue(token.isParam());
        assertTrue(token.isGeneratorParam());
        assertEquals("map", ((ParamToken) token).getComponent());       
        

        token = tokenList.get(2);
        assertEquals(" sky.", token.getToken());
        assertFalse(token.isParam());
    }


    @Test
    public void testBreakdownParameterizedStringMultipleParameterSeparate() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {{color}} sky {{adj}}.");
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
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("{{color}}The sky.{{adj}}");
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
    public void testBreakdownParameterizedStringSingleParameterWithBraces() throws Exception {
        List<StringToken> tokenList = StringUtils.breakdownParameterizedString("The {{color}} {sky}.");
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());

        StringToken token = tokenList.get(0);
        assertEquals("The ", token.getToken());
        assertFalse(token.isParam());

        token = tokenList.get(1);
        assertEquals("color", token.getToken());
        assertTrue(token.isParam());

        token = tokenList.get(2);
        assertEquals(" {sky}.", token.getToken());
        assertFalse(token.isParam());
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

    @Test
    public void testConcatenate() throws Exception {
        assertEquals("", StringUtils.concatenate());
        assertEquals("Tom", StringUtils.concatenate("Tom"));
        assertEquals("TomDon", StringUtils.concatenate("Tom", "Don"));
        assertEquals("HenryTomDon", StringUtils.concatenate("Henry", "Tom", "Don"));
    }

    @Test
    public void concatenateWithSeparator() throws Exception {
        assertEquals("", StringUtils.concatenateUsingSeparator(':'));
        assertEquals("Tom", StringUtils.concatenateUsingSeparator(':', "Tom"));
        assertEquals("Tom:Don", StringUtils.concatenateUsingSeparator(':', "Tom", "Don"));
        assertEquals("Henry:Tom:Don", StringUtils.concatenateUsingSeparator(':', "Henry", "Tom", "Don"));
    }

    @Test
    public void testConcatenateList() throws Exception {
        assertEquals("", StringUtils.concatenate(Arrays.asList()));
        assertEquals("Tom", StringUtils.concatenate(Arrays.asList("Tom")));
        assertEquals("TomDon", StringUtils.concatenate(Arrays.asList("Tom", "Don")));
        assertEquals("HenryTomDon", StringUtils.concatenate(Arrays.asList("Henry", "Tom", "Don")));
    }

    @Test
    public void concatenateWithSeparatorList() throws Exception {
        assertEquals("", StringUtils.concatenateUsingSeparator(':', Arrays.asList()));
        assertEquals("Tom", StringUtils.concatenateUsingSeparator(':', Arrays.asList("Tom")));
        assertEquals("Tom:Don", StringUtils.concatenateUsingSeparator(':', Arrays.asList("Tom", "Don")));
        assertEquals("Henry:Tom:Don", StringUtils.concatenateUsingSeparator(':', Arrays.asList("Henry", "Tom", "Don")));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testSplitToLengthsBadSize() throws Exception {
        assertNull(StringUtils.splitIntoLengths(null, 0));
    }
    
    @Test
    public void testSplitToLengthsNull() throws Exception {
        assertNull(StringUtils.splitIntoLengths(null, 2));
    }
    
    @Test
    public void testSplitToLengthsZero() throws Exception {
        String[] res = StringUtils.splitIntoLengths("", 2);
        assertNotNull(res);
        assertEquals(0, res.length);
    }
    
    @Test
    public void testSplitToLengthsSingleLess() throws Exception {
        String[] res = StringUtils.splitIntoLengths("Hello", 10);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertEquals("Hello", res[0]);
    }
    
    @Test
    public void testSplitToLengthsSingleEqual() throws Exception {
        String[] res = StringUtils.splitIntoLengths("Hello", 5);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertEquals("Hello", res[0]);
    }
    
    @Test
    public void testSplitToLengthsMatch() throws Exception {
        String[] res = StringUtils.splitIntoLengths("HelloWorld", 5);
        assertNotNull(res);
        assertEquals(2, res.length);
        assertEquals("Hello", res[0]);
        assertEquals("World", res[1]);
    }
    
    @Test
    public void testSplitToLengthsOverflow() throws Exception {
        String[] res = StringUtils.splitIntoLengths("Hello World!", 5);
        assertNotNull(res);
        assertEquals(3, res.length);
        assertEquals("Hello", res[0]);
        assertEquals(" Worl", res[1]);
        assertEquals("d!", res[2]);
    }
    
    @Test
    public void testCharOccurences() throws Exception {
        assertEquals(0, StringUtils.charOccurences(null, 'l'));
        assertEquals(0, StringUtils.charOccurences("Hello World!", 'Z'));
        assertEquals(1, StringUtils.charOccurences("Hello World!", 'W'));
        assertEquals(3, StringUtils.charOccurences("Hello World!", 'l'));
    }
}
