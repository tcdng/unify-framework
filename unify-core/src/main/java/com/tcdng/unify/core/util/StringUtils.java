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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.tcdng.unify.common.util.StringToken;
import com.tcdng.unify.common.util.StringTokenUtils;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.StringComposition;

/**
 * Provides utility methods for string manipulation.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class StringUtils {

	private static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static final String MASK = "********";

	public static final String NULL_STRING = null;

	private StringUtils() {

	}

	/**
	 * Gets string composition.
	 * 
	 * @param str the string
	 * @return the composition
	 */
	public static StringComposition getComposition(String str) {
		int passwordLen = 0;
		int letters = 0;
		int digits = 0;
		int special = 0;
		int uppercase = 0;
		int lowercase = 0;

		if (str != null) {
			passwordLen = str.length();
			for (int i = 0; i < passwordLen; i++) {
				char ch = str.charAt(i);
				if (Character.isLetter(ch)) {
					letters++;
					if (Character.isLowerCase(ch)) {
						lowercase++;
					} else {
						uppercase++;
					}
				} else if (Character.isDigit(ch)) {
					digits++;
				} else {
					special++;
				}
			}
		}

		return new StringComposition(passwordLen, letters, digits, special, uppercase, lowercase);
	}

	/**
	 * Tests is a string is an integer.
	 * 
	 * @param str the string to check
	 * @return true if integer otherwise false
	 */
	public static boolean isInteger(String str) {
		if (str != null && !str.isEmpty()) {
			final int len = str.length();
			for (int i = 0; i < len; i++) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
			
			return true;
		}

		return false;
	}
	
	/**
	 * Generates a random alphanumeric string.
	 * 
	 * @param length the length
	 * @return the generated string
	 * @throws UnifyException if an error occurs
	 */
	public static String generateRandomAlphanumeric(int length) throws UnifyException {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		final int slen = ALPHANUMERIC.length();
		for (int i = 0; i < length; i++) {
			sb.append(ALPHANUMERIC.charAt(random.nextInt(slen)));
		}

		return sb.toString();
	}

	/**
	 * Gets printable stack trace from throwable with header message.
	 * 
	 * @param headerMessage the header message
	 * @param e             the throwable
	 * @return the printable stack trace
	 */
	public static String getPrintableStackTrace(Throwable e, String headerMessage) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(headerMessage);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	/**
	 * Gets printable stack trace from throwable.
	 * 
	 * @param e the throwable
	 * @return the printable stack trace
	 */
	public static String getPrintableStackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	/**
	 * Removes duplicates from a string list.
	 * 
	 * @param valueList the string list
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
	 * @param string the string to split
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

	public static int getCharOccurrences(String str, char ch) {
		if (str != null) {
			final int len = str.length();
			int count = 0;
			for (int i = 0; i < len; i++) {
				if (ch == str.charAt(i)) {
					count++;
				}
			}

			return count;
		}

		return 0;
	}

	/**
	 * Split a string into tokens using the comma character.
	 * 
	 * @param string the string to split
	 * @return the result tokens
	 */
	public static String[] commaSplit(String string) {
		return StringUtils.charSplit(string, ',');
	}

	/**
	 * Split a string into tokens using the dot character.
	 * 
	 * @param string the string to split
	 * @return the result tokens
	 */
	public static String[] dotSplit(String string) {
		return StringUtils.charSplit(string, '.');
	}

	/**
	 * Split a string into tokens using supplied character character.
	 * 
	 * @param string the string to split
	 * @param ch     the character to use
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
	 * @param string the string to split
	 * @param ch     the character to use
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
	 * @param string    the string to split
	 * @param separator the separator
	 * @return the result tokens
	 */
	public static String[] split(String string, String separator) {
		if (string != null) {
			return string.split(separator);
		}
		return DataUtils.ZEROLEN_STRING_ARRAY;
	}

	/**
	 * Splits supplied string into lengths.
	 * 
	 * @param str  the string to split
	 * @param size the lengths to split by
	 * @return array of split strings
	 */
	public static String[] splitIntoLengths(String str, int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Length must be greater than zero.");
		}

		if (str != null) {
			int len = str.length();
			int times = len / size;
			if ((len % size) > 0) {
				times++;
			}

			String[] result = new String[times];
			for (int start = 0, i = 0; start < len; i++) {
				result[i] = str.substring(start, Math.min(len, start += size));
			}

			return result;
		}

		return null;
	}

	/**
	 * Concatenates a set of string value of objects using dot.
	 * 
	 * @param objects the objects to concatenate
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
	 * @param strings the supplied array
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
	 * @param strings the supplied array
	 * @return the CSV string
	 */
	public static String buildSpacedCommaSeparatedString(String[] strings) {
		return StringUtils.buildCommaSeparatedString(Arrays.asList(strings), true, false);
	}

	/**
	 * Builds a CSV string from an array of string. A CSV string is a string with
	 * tokens separated with the comma symbol. Any element of the string array with
	 * a comma is surrounded with a double quote.
	 * 
	 * @param strings         the supplied array
	 * @param includeBrackets indicates if enclosing brackets are to be included.
	 * @return the CSV string otherwise null
	 */
	public static String buildCommaSeparatedString(String[] strings, boolean includeBrackets) {
		if (strings != null) {
			return StringUtils.buildCommaSeparatedString(Arrays.asList(strings), false, includeBrackets);
		}

		return null;
	}

	/**
	 * Builds a CSV string from an array of string. A CSV string is a string with
	 * tokens separated with the comma symbol. Any element of the string array with
	 * a comma is surrounded with a double quote.
	 * 
	 * @param objects the supplied collection
	 * @return the CSV string
	 */
	public static String buildCommaSeparatedString(Collection<? extends Object> objects) {
		return StringUtils.buildCommaSeparatedString(objects, false, false);
	}

	/**
	 * Builds a CSV string from an array of string. A CSV string is a string with
	 * tokens separated with the comma symbol. Any element of the string array with
	 * a comma is surrounded with a double quote.
	 * 
	 * @param objects the supplied collection
	 * @return the CSV string
	 */
	public static String buildSpacedCommaSeparatedString(Collection<? extends Object> objects) {
		return StringUtils.buildCommaSeparatedString(objects, true, false);
	}

	/**
	 * Builds a CSV string from a collection of objects. A CSV string is a string
	 * with tokens separated with the comma symbol. Any element of the string array
	 * with a comma is surrounded with a double quote.
	 * 
	 * @param objects         the object list
	 * @param spaced          indicates if space between items.
	 * @param includeBrackets indicates if enclosing brackets are to be included.
	 * @return the CSV string
	 */
	public static String buildCommaSeparatedString(Collection<? extends Object> objects, boolean spaced,
			boolean includeBrackets) {
		StringBuilder sb = new StringBuilder();
		if (includeBrackets) {
			sb.append('[');
		}

		boolean appendSym = false;
		for (Object obj : objects) {
			if (appendSym) {
				sb.append(',');
				if (spaced) {
					sb.append(' ');
				}
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
	 * @param values          the supplied array
	 * @param includeBrackets indicates if enclosing brackets are to be included.
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
	 * @param string the CSv string
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
	 * @param string the string to test
	 */
	public static boolean isBlank(String string) {
		return string == null || string.trim().isEmpty();
	}

	/**
	 * Tests if supplied string is not null and is not white space.
	 * 
	 * @param string the string to test
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
	 * @param string the supplied string
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
	 * @param string the string to pad
	 * @param ch     the padding character
	 * @param length the length to pad supplied string to
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
	 * @param string the string to pad
	 * @param ch     the padding character
	 * @param length the length to pad supplied string to
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
	 * @param string the static list
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
	 * @param text   the text to ellipsize
	 * @param maxLen the maximum length
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
	 * @param text the input string
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
	 * @param text the input string
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
	 * @param text the string to dashen
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
	 * @param text the string to squeeze
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
	 * @param text the string to flatten
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
	 * @param text the string to underscore
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
	 * @param objects the composing objects
	 * @return the built string
	 */
	public static String concatenate(Object... objects) {
		if (objects.length == 1) {
			return String.valueOf(objects[0]);
		}

		if (objects.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (Object object : objects) {
				if (object != null) {
					sb.append(object);
				}
			}
			return sb.toString();
		}

		return "";
	}

	/**
	 * Builds a string by concatenating supplied list of objects.
	 * 
	 * @param objects the composing objects
	 * @return the built string
	 */
	public static String concatenate(List<? extends Object> objects) {
		if (objects.size() == 1) {
			return String.valueOf(objects.get(0));
		}

		if (objects.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Object object : objects) {
				if (object != null) {
					sb.append(object);
				}
			}
			return sb.toString();
		}

		return "";
	}

	/**
	 * Builds a string by concatenating supplied objects separated by supplied
	 * character.
	 * 
	 * @param ch      the separator character
	 * @param objects the composing objects
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
				if (object != null) {
					if (appendSym) {
						sb.append(ch);
					} else {
						appendSym = true;
					}

					sb.append(object);
				}
			}
			return sb.toString();
		}

		return "";
	}

	/**
	 * Builds a string by concatenating supplied objects separated by supplied
	 * separator.
	 * 
	 * @param sp      the separator
	 * @param objects the composing objects
	 * @return the built string
	 */
	public static String concatenateUsingSeparator(String sp, Object... objects) {
		if (objects.length == 1) {
			return String.valueOf(objects[0]);
		}

		if (objects.length > 0) {
			StringBuilder sb = new StringBuilder();
			boolean appendSym = false;
			for (Object object : objects) {
				if (object != null) {
					if (appendSym) {
						sb.append(sp);
					} else {
						appendSym = true;
					}

					sb.append(object);
				}
			}
			return sb.toString();
		}

		return "";
	}

	/**
	 * Builds a string by concatenating supplied list of objects separated by
	 * supplied character.
	 * 
	 * @param ch      the separator character
	 * @param objects the composing objects
	 * @return the built string
	 */
	public static String concatenateUsingSeparator(char ch, List<? extends Object> objects) {
		if (objects.size() == 1) {
			return String.valueOf(objects.get(0));
		}

		if (objects.size() > 0) {
			StringBuilder sb = new StringBuilder();
			boolean appendSym = false;
			for (Object object : objects) {
				if (object != null) {
					if (appendSym) {
						sb.append(ch);
					} else {
						appendSym = true;
					}

					sb.append(object);
				}
			}
			return sb.toString();
		}

		return "";
	}

	/**
	 * Builds a string by concatenating supplied list of objects separated by
	 * supplied separator.
	 * 
	 * @param sp      the separator
	 * @param objects the composing objects
	 * @return the built string
	 */
	public static String concatenateUsingSeparator(String sp, List<? extends Object> objects) {
		if (objects.size() == 1) {
			return String.valueOf(objects.get(0));
		}

		if (objects.size() > 0) {
			StringBuilder sb = new StringBuilder();
			boolean appendSym = false;
			for (Object object : objects) {
				if (object != null) {
					if (appendSym) {
						sb.append(sp);
					} else {
						appendSym = true;
					}

					sb.append(object);
				}
			}
			return sb.toString();
		}

		return "";
	}

	/**
	 * Returns the string representation of a bean.
	 * 
	 * @param bean the supplied bean
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

	public static String replaceFirst(String str, String toReplace, String replaceWith) {
		if (str != null) {
			if (toReplace == null || replaceWith == null) {
				throw new IllegalArgumentException();
			}

			int index = str.indexOf(toReplace);
			if (index >= 0) {
				return str.substring(0, index) + replaceWith + str.substring(index + toReplace.length());
			}
		}

		return str;
	}

	public static String replaceLast(String str, String toReplace, String replaceWith) {
		if (str != null) {
			if (toReplace == null || replaceWith == null) {
				throw new IllegalArgumentException();
			}

			int index = str.lastIndexOf(toReplace);
			if (index >= 0) {
				return str.substring(0, index) + replaceWith + str.substring(index + toReplace.length());
			}
		}

		return str;
	}

	public static int charOccurences(String str, char ch) {
		if (str != null) {
			final int len = str.length();
			int occurences = 0;
			for (int i = 0; i < len; i++) {
				if (ch == str.charAt(i)) {
					occurences++;
				}
			}

			return occurences;
		}

		return 0;
	}

	public static String buildParameterizedString(List<StringToken> tokens) {
		return StringTokenUtils.buildParameterizedString(tokens);
	}

	public static List<List<StringToken>> breakdownParameterizedString(final String text, final String pageBreak) {
		return StringTokenUtils.breakdownParameterizedString(text, pageBreak);
	}

	public static List<List<StringToken>> breakdownParameterizedString(final String text, final int linesPerPage) {
		return StringTokenUtils.breakdownParameterizedString(text, linesPerPage);
	}

	public static List<StringToken> breakdownParameterizedString(String string) {
		return StringTokenUtils.breakdownParameterizedString(string);
	}

	public static void truncate(StringBuilder sb) {
		if (sb != null) {
			sb.delete(0, sb.length());
		}
	}

}
