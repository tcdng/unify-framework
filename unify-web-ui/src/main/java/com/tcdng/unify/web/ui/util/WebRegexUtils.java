/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.NumberSymbols;

/**
 * Web regex utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class WebRegexUtils {

    private WebRegexUtils() {

    }

    /**
     * Gets a JavaScript REGEX that allows only alphanumeric characters and,
     * optionally, some special characters.
     * 
     * @param underscore
     *            indicates if regex should permit underscore character
     * @param dollar
     *            indicates if regex should permit dollar character
     * @param period
     *            indicates if regex should permit period character
     * @param dash
     *            indicates if regex should permit dash character
     * @param slash
     *            indicates if regex should permit slash character
     * 
     * @return the name format regex
     * @throws UnifyException
     *             if an error occurs
     */
	public static String getNameFormatRegex(boolean underscore, boolean dollar, boolean period, boolean dash,
			boolean slash) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("^[\\\\w");
		if (underscore) {
			sb.append("\\\\_");
		}

		if (dollar) {
			sb.append("\\\\$");
		}

		if (period) {
			sb.append("\\\\.");
		}

		if (dash) {
			sb.append("-");
		}

		if (slash) {
			sb.append("\\\\/");
		}
		sb.append("]*$");
		return sb.toString();
	}

    /**
     * Gets a JavaScript REGEX that allows only paths.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
	public static String getPathFormatRegex() throws UnifyException {
		return "^(?!.*\\\\/\\\\/)[a-zA-Z0-9_$/-]+$";
	}

    /**
     * Gets a JavaScript REGEX that allows alphanumeric characters and all special characters.
     * 
     * @return the name format regex
     * @throws UnifyException
     *             if an error occurs
     */
    public static String getNameAndSpecialCharactersFormatRegex()
            throws UnifyException {
        return "^[\\\\w\\\\^\\\\$\\\\?\\\\*\\\\+\\\\.\\\\<\\\\>\\\\-\\\\=\\\\!\\\\_\\\\@\\\\#\\\\%,]*$";
    }

    /**
     * Gets JavaScript REGEX for identifiers.
     * 
     * @return the identifier format regex
     * @throws UnifyException
     *             if an error occurs
     */
    public static String getIdentifierFormatRegex() throws UnifyException {
        return "^([_a-zA-Z][_a-zA-Z0-9]*)?$";
    }

    /**
     * Gets JavaScript REGEX for alphanumeric only.
     * 
     * @param  special
     * @param  dash
     * @param  space
     * @return the alphanumeric format regex
     * @throws UnifyException
     *             if an error occurs
     */
	public static String getAlphanumericFormatRegex(boolean special, boolean dash, boolean space)
			throws UnifyException {
		if (special) {
			if (space) {
				return "^[ 0-9a-zA-Z/&%\\\\-\\\\.\\\\(\\\\),]*$";
			}

			return "^[0-9a-zA-Z/&%\\\\-\\\\.\\\\(\\\\),]*$";
		}

		if (dash) {
			if (space) {
				return "^[ \\\\-0-9a-zA-Z]*$";
			}
			
			return "^[\\\\-0-9a-zA-Z]*$";
		}

		if (space) {
			return "^[ 0-9a-zA-Z]*$";
		}

		return "^[0-9a-zA-Z]*$";
	}

    /**
     * Gets JavaScript REGEX that allows only alphabetic characters.
     * 
     * @return the word format regex
     * @throws UnifyException
     *             if an error occurs
     */
    public static String getWordFormatRegex() throws UnifyException {
        return "^[a-zA-Z]*$";
    }

    public static String getFullNameFormatRegex(boolean special)  throws UnifyException {
        if (special) {
            return "^[a-zA-Z][ a-zA-Z/&%\\\\-\\\\.\\\\(\\\\),]*$";
        }
        
        return "^[a-zA-Z][ a-zA-Z]*$";
    }

    public static String getSeriesRegex()  throws UnifyException {
        return "^[a-zA-Z][a-zA-Z]*[0-9]*$";
    }
    
    /**
     * Gets JavaScript REGEX that allows only digits.
     * 
     * @param allowPlus
     * @param allowMinus
     * @return the integer text format regex
     * @throws UnifyException
     *             if an error occurs
     */
    public static String getIntegerTextFormatRegex(boolean allowPlus, boolean allowMinus) throws UnifyException {
        if (allowPlus && allowMinus) {
            return "^(\\\\+|\\\\-)?[0-9]*$";
        }
        
        if (allowPlus) {
            return "^(\\\\+)?[0-9]*$";
        }
        
        if (allowMinus) {
            return "^(\\\\-)?[0-9]*$";
        }
        
        return "^[0-9]*$";
    }

    /**
     * Gets a number formatting JavaScript REGEX.
     * 
     * @param numberSymbols
     *            the reference number symbols
     * @param precision
     *            the precision of the number
     * @param scale
     *            the scale of the number
     * @param acceptNegative
     *            if REGEX should accept negative values
     * @param useGrouping
     *            if REGEX should accept grouping characters
     * @param strictFormat indicates if strict rule applies to precision and scale
     * @return the number format regex
     * @throws UnifyException
     *             if an error occurs
     */
    public static String getNumberFormatRegex(NumberSymbols numberSymbols, int precision, int scale,
            boolean acceptNegative, boolean useGrouping, boolean strictFormat) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        if (acceptNegative) {
            appendOptionalFormattingRegex(sb, numberSymbols.getNegativePrefix(), numberSymbols.getPositivePrefix());
        } else {
            appendOptionalFormattingRegex(sb, numberSymbols.getPositivePrefix());
        }

        if (scale > 0 && !numberSymbols.getNumberType().isInteger()) {
            precision = precision - scale;
        }

        String digit = escapeSpecial("d");
        int groupSize = numberSymbols.getGroupSize();
        if (precision > 0) {
            if (useGrouping) {
                int fullGroupCount = precision / groupSize;
                int remainder = precision % groupSize;
                if (remainder > 0) {
                    appendRangeOption(sb, digit, remainder);
                }
                if (fullGroupCount > 0) {
                    sb.append("(");
                    appendOptionalFormattingRegex(sb, numberSymbols.getGroupingSeparator());
                    appendRangeOption(sb, digit, groupSize);
                    sb.append("){0,").append(fullGroupCount).append('}');
                }
            } else {
                appendRangeOption(sb, digit, precision);
            }
        } else {
            if (!strictFormat) {
                if (useGrouping) {
                    appendRangeOption(sb, digit, groupSize);
                    sb.append("(");
                    appendOptionalFormattingRegex(sb, numberSymbols.getGroupingSeparator());
                    appendRangeOption(sb, digit, groupSize);
                    sb.append(")*");
                } else {
                    sb.append("[").append(digit).append("]*");
                }
            }
        }

        if (!numberSymbols.getNumberType().isInteger()) {
            sb.append('(');
            if (scale > 0) {
                escapeSpecial(sb, numberSymbols.getDecimalSeparator());
                appendRangeOption(sb, digit, scale);
            } else {
                if (!strictFormat) {
                    escapeSpecial(sb, numberSymbols.getDecimalSeparator());
                    sb.append("[").append(digit).append("]*");
                }
            }
            sb.append(")?");
        }

        if (acceptNegative) {
            appendOptionalFormattingRegex(sb, numberSymbols.getNegativeSuffix(), numberSymbols.getPositiveSuffix());
        } else {
            appendOptionalFormattingRegex(sb, numberSymbols.getPositiveSuffix());
        }
        sb.append("$");
        return sb.toString();
    }

    private static void appendRangeOption(StringBuilder sb, String pattern, int range) {
        sb.append("[").append(pattern).append(")]{0,").append(range).append('}');
    }

    private static void appendOptionalFormattingRegex(StringBuilder sb, String string) {
        int len = 0;
        if (string != null && (len = string.length()) > 0) {
            sb.append('(');
            boolean appendSym = false;
            for (int i = 1; i <= len; i++) {
                if (appendSym) {
                    sb.append('|');
                } else {
                    appendSym = true;
                }
                escapeSpecial(sb, string.substring(0, i));
            }
            sb.append(")?");
        }
    }

    private static void appendOptionalFormattingRegex(StringBuilder sb, String... strings) {
        List<String> sbList = new ArrayList<String>();
        Set<String> testSet = new HashSet<String>();
        for (String string : strings) {
            if (!testSet.contains(string)) {
                StringBuilder psb = new StringBuilder();
                appendOptionalFormattingRegex(psb, string);
                if (psb.length() > 0) {
                    sbList.add(psb.toString());
                }
                testSet.add(string);
            }
        }

        if (!sbList.isEmpty()) {
            sb.append('(');
            boolean appendSym = false;
            for (String string : sbList) {
                if (appendSym)
                    sb.append('|');
                else
                    appendSym = true;
                sb.append(string);
            }
            sb.append(')');
        }
    }

    private static String escapeSpecial(String string) {
        StringBuilder sb = new StringBuilder();
        escapeSpecial(sb, string);
        return sb.toString();
    }

    private static void escapeSpecial(StringBuilder sb, String string) {
        int len = string.length();
        for (int i = 0; i < len; i++) {
            char ch = string.charAt(i);
            switch (ch) {
            case 'd':
            case '\\':
            case '[':
            case ']':
            case '(':
            case ')':
            case '|':
            case '$':
            case '.':
            case ',':
                sb.append("\\\\");
                sb.append(ch);
                break;
            default:
                sb.append(ch);
            }
        }
    }

}
