/*
 * Copyright 2018-2020 The Code Department.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.PackableDoc;
import com.tcdng.unify.core.data.ValueStore;

/**
 * Provides utility methods for string manipulation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class StringUtils {

    public static final String MASK = "********";

    public static final String NULL_STRING = null;

    private StringUtils() {

    }

    /**
     * Removes duplicates from a string list.
     * 
     * @param valueList
     *                  the string list
     * @return a new string list with no duplicates
     */
    public static List<String> removeDuplicates(List<String> valueList) {
        List<String> resultList = new ArrayList<String>();
        if (valueList != null && !valueList.isEmpty()) {
            for (String value : valueList) {
                if (!resultList.contains(value)) {
                    resultList.add(value);
                }
            }
            return resultList;
        }
        return valueList;
    }

    /**
     * Split a string into tokens using the whitespace character.
     * 
     * @param string
     *               the string to split
     * @return the result tokens
     */
    public static String[] whiteSpaceSplit(String string) {
        if (string != null) {
            List<String> result = new ArrayList<String>();
            String[] tokens = string.split("\\s+");
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    result.add(token);
                }
            }

            return result.toArray(new String[result.size()]);
        }
        return DataUtils.ZEROLEN_STRING_ARRAY;
    }

    /**
     * Split a string into tokens using the comma character.
     * 
     * @param string
     *               the string to split
     * @return the result tokens
     */
    public static String[] commaSplit(String string) {
        return StringUtils.charSplit(string, ',');
    }

    /**
     * Split a string into tokens using the dot character.
     * 
     * @param string
     *               the string to split
     * @return the result tokens
     */
    public static String[] dotSplit(String string) {
        return StringUtils.charSplit(string, '.');
    }

    /**
     * Split a string into tokens using supplied character character.
     * 
     * @param string
     *               the string to split
     * @param ch
     *               the character to use
     * @return the result tokens
     */
    public static String[] charSplit(String string, char ch) {
        if (string != null) {
            List<String> list = StringUtils.charToListSplit(string, ch);
            if (list != null) {
                return list.toArray(new String[list.size()]);
            }
        }

        return DataUtils.ZEROLEN_STRING_ARRAY;
    }

    /**
     * Split a string into tokens using supplied character character.
     * 
     * @param string
     *               the string to split
     * @param ch
     *               the character to use
     * @return the result tokens
     */
    public static List<String> charToListSplit(String string, char ch) {
        if (string != null) {
            int len = string.length();
            if (len > 0) {
                List<String> list = new ArrayList<String>();
                int start = 0;
                while (start < len) {
                    int end = string.indexOf(ch, start);
                    if (end >= 0) {
                        list.add(string.substring(start, end));
                        start = end + 1;
                    } else {
                        list.add(string.substring(start));
                        start = len;
                    }
                }

                return list;
            }
        }

        return null;
    }

    /**
     * Split a string into tokens using supplied separator.
     * 
     * @param string
     *                  the string to split
     * @param separator
     *                  the separator
     * @return the result tokens
     */
    public static String[] split(String string, String separator) {
        if (string != null) {
            return string.split(separator);
        }
        return DataUtils.ZEROLEN_STRING_ARRAY;
    }

    /**
     * Concatenates a set of string value of objects using dot.
     * 
     * @param objects
     *                the objects to concatenate
     * @return the dotified string
     */
    public static String dotify(Object... objects) {
        if (objects.length == 1) {
            return String.valueOf(objects[0]);
        }

        if (objects.length > 0) {
            StringBuilder sb = new StringBuilder();
            boolean appendSym = false;
            for (Object obj : objects) {
                if (appendSym) {
                    sb.append('.');
                } else {
                    appendSym = true;
                }

                sb.append(obj);
            }

            return sb.toString();
        }

        return "";
    }

    /**
     * Builds a CSV string from an array of string. A CSV string is a string with
     * tokens separated with the comma symbol. Any element of the string array with
     * a comma is surrounded with a double quote.
     * 
     * @param strings
     *                the supplied array
     * @return the CSV string
     */
    public static String buildCommaSeparatedString(String[] strings) {
        return StringUtils.buildCommaSeparatedString(strings, false);
    }

    /**
     * Builds a CSV string from an array of string. A CSV string is a string with
     * tokens separated with the comma symbol. Any element of the string array with
     * a comma is surrounded with a double quote.
     * 
     * @param strings
     *                        the supplied array
     * @param includeBrackets
     *                        indicates if enclosing brackets are to be included.
     * @return the CSV string otherwise null
     */
    public static String buildCommaSeparatedString(String[] strings, boolean includeBrackets) {
        if (strings != null) {
            StringBuilder sb = new StringBuilder();
            if (includeBrackets) {
                sb.append('[');
            }

            boolean appendSym = false;
            for (String string : strings) {
                if (appendSym) {
                    sb.append(',');
                } else {
                    appendSym = true;
                }
                if (string.indexOf(',') >= 0) {
                    sb.append('"').append(string).append('"');
                } else {
                    sb.append(string);
                }
            }

            if (includeBrackets) {
                sb.append(']');
            }

            return sb.toString();
        }

        return null;
    }

    /**
     * Builds a CSV string from a collection of objects. A CSV string is a string
     * with tokens separated with the comma symbol. Any element of the string array
     * with a comma is surrounded with a double quote.
     * 
     * @param objects
     *                        the object list
     * @param includeBrackets
     *                        indicates if enclosing brackets are to be included.
     * @return the CSV string
     */
    public static String buildCommaSeparatedString(Collection<Object> objects, boolean includeBrackets) {
        StringBuilder sb = new StringBuilder();
        if (includeBrackets) {
            sb.append('[');
        }

        boolean appendSym = false;
        for (Object obj : objects) {
            if (appendSym) {
                sb.append(',');
            } else {
                appendSym = true;
            }
            if ((obj instanceof String) && ((String) obj).indexOf(',') >= 0) {
                sb.append('"').append(obj).append('"');
            } else {
                sb.append(obj);
            }
        }

        if (includeBrackets) {
            sb.append(']');
        }

        return sb.toString();
    }

    /**
     * Builds a CSV string from an array of string. A CSV string is a string with
     * tokens separated with the comma symbol. Any element of the string array with
     * a comma is surrounded with a double quote.
     * 
     * @param values
     *                        the supplied array
     * @param includeBrackets
     *                        indicates if enclosing brackets are to be included.
     * @return the CSV string
     */
    public static String buildCommaSeparatedString(Object[] values, boolean includeBrackets) {
        StringBuilder sb = new StringBuilder();
        if (includeBrackets) {
            sb.append('[');
        }

        boolean appendSym = false;
        for (Object value : values) {
            String string = String.valueOf(value);
            if (appendSym) {
                sb.append(',');
            } else {
                appendSym = true;
            }
            if (string.indexOf(',') >= 0) {
                sb.append('"').append(string).append('"');
            } else {
                sb.append(string);
            }
        }

        if (includeBrackets) {
            sb.append(']');
        }

        return sb.toString();
    }

    /**
     * Gets a list of string values from a CSV string.
     * 
     * @param string
     *               the CSv string
     * @return the string values
     */
    public static String[] getCommaSeparatedValues(String string) {
        List<String> values = new ArrayList<String>();
        int index = 0;
        int lastIndex = string.length() - 1;
        while (index <= lastIndex) {
            char ch = string.charAt(index);
            if (ch == ',') {
                values.add("");
                index++;
            } else if (index == lastIndex) {
                values.add(String.valueOf(ch));
                break;
            } else if (ch == '"') {
                int quoteIndex = string.indexOf("\",", index + 1);
                if (quoteIndex < 0) {
                    if (ch == string.charAt(lastIndex)) {
                        values.add(string.substring(index + 1, lastIndex));
                    } else {
                        values.add(string.substring(index + 1));
                    }
                    break;
                }
                values.add(string.substring(index + 1, quoteIndex));
                index = quoteIndex + 2;
            } else {
                int commaIndex = string.indexOf(',', index + 1);
                if (commaIndex < 0) {
                    values.add(string.substring(index));
                    break;
                } else {
                    values.add(string.substring(index, commaIndex));
                    index = commaIndex + 1;
                }
            }
        }
        if (lastIndex >= 0 && string.charAt(lastIndex) == ',') {
            values.add("");
        }
        return values.toArray(new String[values.size()]);
    }

    /**
     * Tests if supplied string is null or is white space.
     * 
     * @param string
     *               the string to test
     */
    public static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    /**
     * Tests if supplied string is not null and is not white space.
     * 
     * @param string
     *               the string to test
     */
    public static boolean isNotBlank(String string) {
        return string != null && !string.trim().isEmpty();
    }

    public static String toNonNullString(Object obj, String nullDefault) {
        if (obj == null) {
            return nullDefault;
        }

        return obj.toString();
    }
    
    /**
     * Tests if supplied string contains a whitespace character.
     * 
     * @param string
     *               the supplied string
     * @return true if string contains whitespace otherwise false
     */
    public static boolean containsWhitespace(String string) {
        if (string != null && !string.isEmpty()) {
            int len = string.length();
            for (int i = 0; i < len; i++) {
                if (Character.isWhitespace(string.charAt(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Pads a string on the left with specified character until its as long as
     * specified length. No padding occurs if length of supplied string is greater
     * than or equal to specified length.
     * 
     * @param string
     *               the string to pad
     * @param ch
     *               the padding character
     * @param length
     *               the length to pad supplied string to
     * @return the padded string
     */
    public static String padLeft(String string, char ch, int length) {
        int left = length - string.length();
        if (left > 0) {
            StringBuilder sb = new StringBuilder();
            while (--left >= 0) {
                sb.append(ch);
            }
            sb.append(string);
            return sb.toString();
        }
        return string;
    }

    /**
     * Pads a string on the right with specified character until its as long as
     * specified length. No padding occurs if length of supplied string is greater
     * than or equal to specified length.
     * 
     * @param string
     *               the string to pad
     * @param ch
     *               the padding character
     * @param length
     *               the length to pad supplied string to
     * @return the padded string
     */
    public static String padRight(String string, char ch, int length) {
        int left = length - string.length();
        if (left > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            while (--left >= 0) {
                sb.append(ch);
            }
            return sb.toString();
        }
        return string;
    }

    /**
     * Reads a static list string into a listable data array. Static list should be
     * in the format
     * 
     * <pre>
     *     key1~description1|key2~description2|...keyN�descriptionN
     * </pre>
     * 
     * @param string
     *               the static list
     * @return the listable data array
     */
    public static List<ListData> readStaticList(String string) {
        List<ListData> list = new ArrayList<ListData>();
        String[] namevalues = string.split("\\|");
        for (String namevalue : namevalues) {
            String[] pair = namevalue.split("~");
            if (pair.length == 2) {
                list.add(new ListData(pair[0], pair[1]));
            }
        }
        return list;
    }

    /**
     * Ellipsizes a text if length is greater than supplied maximum length.
     * 
     * @param text
     *               the text to ellipsize
     * @param maxLen
     *               the maximum length
     * @return the ellipsized text
     */
    public static String ellipsize(String text, int maxLen) {
        if (text != null && text.length() > maxLen) {
            return text.substring(0, maxLen - 3) + "...";
        }

        return text;
    }

    /**
     * Sets the first letter of a text to uppercase.
     * 
     * @param text
     *             the input string
     */
    public static String capitalizeFirstLetter(String text) {
        if (text != null && text.length() > 0) {
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        return text;
    }

    /**
     * Sets the first letter of a text to lowercase.
     * 
     * @param text
     *             the input string
     */
    public static String decapitalize(String text) {
        if (text != null && text.length() > 0) {
            return Character.toLowerCase(text.charAt(0)) + text.substring(1);
        }
        return text;
    }

    /**
     * Dashens a string. Converts text to lower-case and replaces all white spaces
     * with the dash character '-'.
     * 
     * @param text
     *             the string to dashen
     * @return the dashened string
     */
    public static String dashen(String text) {
        if (text != null) {
            return text.replaceAll(" ", "-").toLowerCase();
        }
        return null;
    }

    /**
     * Squeezes a string. Converts text to lower-case and removes all white spaces.
     * 
     * @param text
     *             the string to squeeze
     * @return the squeezed string
     */
    public static String squeeze(String text) {
        if (text != null) {
            return text.replaceAll(" ", "").toLowerCase();
        }
        return null;
    }

    /**
     * Flattens a string. Converts text to lower-case and replaces all white spaces
     * with the underscore character '_'.
     * 
     * @param text
     *             the string to flatten
     * @return the flattened string
     */
    public static String flatten(String text) {
        if (text != null) {
            return text.replaceAll(" ", "_").toLowerCase();
        }
        return null;
    }

    /**
     * Replaces all white spaces in a text with the underscore character '_'.
     * 
     * @param text
     *             the string to underscore
     * @return the underscored string
     */
    public static String underscore(String text) {
        if (text != null) {
            return text.replaceAll(" ", "_");
        }
        return null;
    }

    public static String toUpperCase(String text) {
        if (text != null && text.length() > 0) {
            return text.toUpperCase();
        }
        return text;
    }

    public static String toLowerCase(String text) {
        if (text != null && text.length() > 0) {
            return text.toLowerCase();
        }
        return text;
    }

    /**
     * Builds a string by concatenating supplied objects.
     * 
     * @param objects
     *                the composing objects
     * @return the built string
     */
    public static String concatenate(Object... objects) {
        if (objects.length == 1) {
            return String.valueOf(objects[0]);
        }

        if (objects.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object object : objects) {
                sb.append(object);
            }
            return sb.toString();
        }

        return "";
    }

    /**
     * Builds a string by concatenating supplied objects separated by supplied character.
     * 
     * @param ch
     *                the separator character
     * @param objects
     *                the composing objects
     * @return the built string
     */
    public static String concatenateUsingSeparator(char ch, Object... objects) {
        if (objects.length == 1) {
            return String.valueOf(objects[0]);
        }

        if (objects.length > 0) {
            StringBuilder sb = new StringBuilder();
            boolean appendSym = false;
            for (Object object : objects) {
                if (appendSym) {
                    sb.append(ch);
                } else {
                    appendSym = true;
                }
                
                sb.append(object);
            }
            return sb.toString();
        }

        return "";
    }

    /**
     * Returns the string representation of a bean.
     * 
     * @param bean
     *             the supplied bean
     */
    public static String toXmlString(Object bean) {
        if (bean != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<").append(bean.getClass().getName()).append(">\n");
            try {
                for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(bean.getClass())) {
                    if (getterSetterInfo != null && getterSetterInfo.isGetter()) {
                        String fieldName = getterSetterInfo.getName();
                        sb.append("\t<").append(fieldName).append(">").append(getterSetterInfo.getGetter().invoke(bean))
                                .append("</").append(fieldName).append(">\n");
                    }
                }
            } catch (Exception e) {
            }
            sb.append("</").append(bean.getClass().getName()).append(">\n");
            return sb.toString();
        }

        return "";
    }

    public static String getFirstNonBlank(String... values) {
        for (String val : values) {
            if (StringUtils.isNotBlank(val)) {
                return val;
            }
        }

        return null;
    }

    public static List<StringToken> breakdownParameterizedString(String string) {
        if (StringUtils.isBlank(string)) {
            return Collections.emptyList();
        }

        List<StringToken> tokenList = new ArrayList<StringToken>();
        int index = 0;
        int pStartIndex = 0;
        while ((pStartIndex = string.indexOf("{{", index)) >= 0) {
            int pEndIndex = string.indexOf("}}", pStartIndex);
            if (pEndIndex <= 0) {
                throw new RuntimeException("Invalid parameterized string: parameter closure expected.");
            }

            if ((pEndIndex - pStartIndex) < 4) {
                throw new RuntimeException(
                        "Invalid parameterized string: parameter name expected at index " + (pStartIndex + 1) + ".");
            }

            if (index < pStartIndex) {
                tokenList.add(new StringToken(string.substring(index, pStartIndex)));
            }

            tokenList.add(new StringToken(string.substring(pStartIndex + 2, pEndIndex), true));

            index = pEndIndex + 2;
        }

        if (index < string.length()) {
            tokenList.add(new StringToken(string.substring(index)));
        }

        return Collections.unmodifiableList(tokenList);
    }

    public static String buildParameterizedString(List<StringToken> tokenList, Map<String, Object> parameters) {
        if (DataUtils.isBlank(tokenList)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (StringToken stringToken : tokenList) {
            if (stringToken.isParam()) {
                Object val = parameters.get(stringToken.getToken());
                if (val != null) {
                    sb.append(val);
                }
            } else {
                sb.append(stringToken.getToken());
            }
        }

        return sb.toString();
    }

    public static String buildParameterizedString(List<StringToken> tokenList, PackableDoc packableDoc)
            throws UnifyException {
        if (DataUtils.isBlank(tokenList)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (StringToken stringToken : tokenList) {
            if (stringToken.isParam()) {
                Object val = packableDoc.read(stringToken.getToken());
                if (val != null) {
                    sb.append(val);
                }
            } else {
                sb.append(stringToken.getToken());
            }
        }

        return sb.toString();
    }

    public static String buildParameterizedString(List<StringToken> tokenList, ValueStore valueStore)
            throws UnifyException {
        if (DataUtils.isBlank(tokenList)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (StringToken stringToken : tokenList) {
            if (stringToken.isParam()) {
                Object val = valueStore.getTempValue(stringToken.getToken());
                if (val == null) {
                    val = valueStore.retrieve(stringToken.getToken());
                }

                if (val != null) {
                    sb.append(val);
                }
            } else {
                sb.append(stringToken.getToken());
            }
        }

        return sb.toString();
    }

    public static void truncate(StringBuilder sb) {
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    public static class StringToken {

        private String token;

        private boolean param;

        public StringToken(String token) {
            this(token, false);
        }

        public StringToken(String token, boolean param) {
            this.token = token;
            this.param = param;
        }

        public String getToken() {
            return token;
        }

        public boolean isParam() {
            return param;
        }
    }

}
