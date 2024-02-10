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
package com.tcdng.unify.web.util;

import com.tcdng.unify.core.data.LargeStringWriter;

/**
 * HTML utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class HtmlUtils {

    private HtmlUtils() {

    }

    public static String getStringWithHtmlEscape(String string) {
        StringBuilder sb = new StringBuilder();
        HtmlUtils.writeStringWithHtmlEscape(sb, string);
        return sb.toString();
    }

    public static void writeStringWithHtmlEscape(StringBuilder sb, String string) {
        if (string == null) {
            sb.append(string);
            return;
        }

        int length = string.length();
        for (int i = 0; i < length; i++) {
            writeChar(sb, string.charAt(i));
        }
    }

    public static void writeChar(StringBuilder sb, char ch) {
        switch (ch) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            default:
                sb.append(ch);
        }
    }
    
    public static void writeChar(LargeStringWriter lsw, char ch) {
        switch (ch) {
            case '<':
                lsw.append("&lt;");
                break;
            case '>':
                lsw.append("&gt;");
                break;
            case '&':
                lsw.append("&amp;");
                break;
            case '"':
                lsw.append("&quot;");
                break;
            case '\'':
                lsw.append("&apos;");
                break;
            default:
                lsw.append(ch);
        }
    }
    
    public static String extractStyleAttribute(String style, String attributeName) {
        if (style != null && !style.isEmpty()) {
            int startIndex = style.indexOf(attributeName);
            if (startIndex >= 0) {
                int stopIndex = style.indexOf(';', startIndex);
                if (stopIndex > 0) {
                    return style.substring(startIndex, ++stopIndex);
                }
            }
        }

        return "";
    }

}
