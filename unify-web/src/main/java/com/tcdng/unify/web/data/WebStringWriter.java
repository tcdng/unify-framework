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

package com.tcdng.unify.web.data;

import com.tcdng.unify.core.data.LargeStringWriter;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Web string writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class WebStringWriter extends LargeStringWriter {

    public WebStringWriter() {

    }

    public WebStringWriter(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public WebStringWriter append(char c) {
        return (WebStringWriter) super.append(c);
    }

    @Override
    public WebStringWriter append(boolean bool) {
        return (WebStringWriter) super.append(bool);
    }

    @Override
    public WebStringWriter append(String str) {
        return (WebStringWriter) super.append(str);
    }

    @Override
    public WebStringWriter append(Object obj) {
        return (WebStringWriter) super.append(obj);
    }

    @Override
    public WebStringWriter append(LargeStringWriter lsw) {
        return (WebStringWriter) super.append(lsw);
    }

    public WebStringWriter appendHtmlEscaped(String str) {
        if (str == null) {
            append(str);
        } else {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                char ch = str.charAt(i);
                switch (ch) {
                case '<':
                    append("&lt;");
                    break;
                case '>':
                    append("&gt;");
                    break;
                case '&':
                    append("&amp;");
                    break;
                case '"':
                    append("&quot;");
                    break;
                case '\'':
                    append("&apos;");
                    break;
                default:
                    append(ch);
                }
            }
        }

        return this;
    }

    public WebStringWriter appendHtmlEscaped(WebStringWriter wsw) {
        if (wsw == null) {
            append(StringUtils.NULL_STRING);
        } else {
            char[] data = wsw.getData();
            int len = wsw.length();
            for (int i = 0; i < len; i++) {
                char ch = data[i];
                switch (ch) {
                case '<':
                    append("&lt;");
                    break;
                case '>':
                    append("&gt;");
                    break;
                case '&':
                    append("&amp;");
                    break;
                case '"':
                    append("&quot;");
                    break;
                case '\'':
                    append("&apos;");
                    break;
                default:
                    append(ch);
                }
            }
        }

        return this;
    }

    public WebStringWriter appendJsonQuoted(String str) {
        if (str == null || str.length() == 0) {
            append("\"\"");
        } else {
            append('"');
            int len = str.length();
            for (int i = 0; i < len; i++) {
                char ch = str.charAt(i);
                switch (ch) {
                case '"':
                case '\\':
                case '/':
                    append('\\').append(ch);
                    break;
                case '\t':
                    append("\\t");
                    break;
                case '\f':
                    append("\\f");
                    break;
                case '\b':
                    append("\\b");
                    break;
                case '\r':
                    append("\\r");
                    break;
                case '\n':
                    append("\\n");
                    break;
                default:
                    if (ch < ' ' || ch > 127) {
                        String hex = Integer.toHexString(ch);
                        int padLen = 4 - hex.length();
                        append("\\u");
                        while (padLen-- > 0) {
                            append('0');
                        }
                        append(hex);
                    } else {
                        append(ch);
                    }
                }
            }
            append('"');
        }

        return this;
    }

    public WebStringWriter appendJsonQuoted(WebStringWriter wsw) {
        if (wsw == null) {
            append("\"\"");
        } else {
            append('"');
            char[] data = wsw.getData();
            int len = wsw.length();
            for (int i = 0; i < len; i++) {
                char ch = data[i];
                switch (ch) {
                case '"':
                case '\\':
                case '/':
                    append('\\').append(ch);
                    break;
                case '\t':
                    append("\\t");
                    break;
                case '\f':
                    append("\\f");
                    break;
                case '\b':
                    append("\\b");
                    break;
                case '\r':
                    append("\\r");
                    break;
                case '\n':
                    append("\\n");
                    break;
                default:
                    if (ch < ' ' || ch > 127) {
                        String hex = Integer.toHexString(ch);
                        int padLen = 4 - hex.length();
                        append("\\u");
                        while (padLen-- > 0) {
                            append('0');
                        }
                        append(hex);
                    } else {
                        append(ch);
                    }
                }
            }
            append('"');
        }

        return this;
    }
}
