/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.util.html;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.tcdng.unify.core.data.LargeStringWriter;
import com.tcdng.unify.core.util.StringUtils;

/**
 * HTML utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class HtmlUtils {

	private enum PartType {
		LIST, BOLD, ITALIC, BOLD_ITALIC, ACTION;

		public boolean supportsNesting() {
			return LIST.equals(this);
		}
	}

	// Spaces are intentional
	private static final List<String> LIST_TYPE = Arrays.asList("disc ", "circle ", "square ", "decimal ", "lower-roman ",
			"upper-roman ", "lower-alpha ", "upper-alpha ");

    private HtmlUtils() {

    }

	/**
	 * Parses text to HTML. (This is a very important function. Once unit test fail,
	 * know that you have broken major functionality)
	 * 
	 * @param writer the response writer
	 * @param text   the text to part
	 * @throws Exception if an error occurs
	 */
	public static void parseTextToHTML(HtmlTextWriter writer, String text) throws Exception {
		HtmlUtils.parseTextToHTML(writer, text, null);
	}

	/**
	 * Parses text to HTML. (This is a very important function. Once unit test fail,
	 * know that you have broken major functionality)
	 * 
	 * @param writer the response writer
	 * @param text   the text to part
	 * @param title  optional title
	 * @throws Exception if an error occurs
	 */
	public static void parseTextToHTML(HtmlTextWriter writer, String text, String title) throws Exception {
		if (text != null && !StringUtils.isBlank(text)) {
			Stack<PartType> part = new Stack<PartType>();
			final String _text = text.trim();
			final int len = _text.length();
			boolean escape = false;
			boolean listOpen = false;
			boolean listItemClosed = false;
			int i = 0;
			writer.write("<p class=\"init\">");
			if (!StringUtils.isBlank(title)) {
				writer.write("<strong>");
				writer.writeWithHtmlEscape(title);
				writer.write(": ");
				writer.write("</strong>");
			}

			while (i < len) {
				char ch = _text.charAt(i);
				if (escape) {
					writer.write(ch);
					escape = false;
				} else {
					if (ch == '\\') {
						escape = true;
					} else if (ch == '[') {
						throw new IllegalArgumentException("Unexpected token opening bracket.");
					} else if (ch == ']') {
						if (part.isEmpty()) {
							throw new IllegalArgumentException("Unexpected token closing bracket.");
						}

						switch (part.pop()) {
						case BOLD:
							writer.write("</strong>");
							break;
						case BOLD_ITALIC:
							writer.write("</em></strong>");
							break;
						case ITALIC:
							writer.write("</em>");
							break;
						case ACTION: {
							final String cmd = writer.toString();
							writer.discardSecondary();
							if ("break".equalsIgnoreCase(cmd)) {
								if (!part.isEmpty()) {
									throw new IllegalArgumentException(
											"Break not allowed in nesting at position " + i + ".");
								}

								writer.write("</p><p>");
							} else {
								final int cstart = cmd.indexOf('<');
								final int cend = cmd.indexOf('>');
								if (cstart < 0 || cend < cstart) {
									throw new IllegalArgumentException("Bad contect format at position " + i + ".");
								}

								final String ctype = cstart > 0 ? cmd.substring(0, cstart).toLowerCase() : "link";
								final String ltext = (cend + 1) < cmd.length() ? cmd.substring(cend + 1) : null;
								final String content = cmd.substring(cstart + 1, cend);

								writer.write("<a href=\"");
								if ("email".equals(ctype)) {
									writer.write("mailto:");
								} else if ("tel".equals(ctype)) {
									writer.write("tel:");
								} else if ("slink".equals(ctype)) {
									writer.write("#");
								}

								writer.write(content);
								writer.write("\"");
								if ("tlink".equals(ctype)) {
									writer.write(" target=\"_blank\"");
								} else if ("down".equals(ctype)) {
									writer.write(" download");
								}

								writer.write(">");
								if (ltext != null) {
									writer.writeWithHtmlEscape(ltext);
								}

								writer.write("</a>");
							}
						}
							break;
						case LIST:
							writer.write("</li>");
							listItemClosed = true;
							while ((i + 1) < len && Character.isWhitespace(_text.charAt(i + 1)))
								i++;
							break;
						default:
							break;
						}
					} else {
						PartType type = getPart(ch);
						if (listOpen && listItemClosed && !PartType.LIST.equals(type)) {
							writer.write("</ul>");
							listOpen = false;
							listItemClosed = false;
						}

						if (type != null && ((i + 1) < len && _text.charAt(i + 1) == '[')) {
							if (!part.isEmpty()) {
								if (!part.peek().supportsNesting() || type.supportsNesting()) {
									throw new IllegalArgumentException("Nesting not allowed for " + part.peek() + ".");
								}
							}

							i++;
							switch (type) {
							case BOLD:
								writer.write("<strong>");
								break;
							case BOLD_ITALIC:
								writer.write("<strong><em>");
								break;
							case ITALIC:
								writer.write("<em>");
								break;
							case ACTION:
								writer.useSecondary();
								break;
							case LIST:
								String ltype = null;
								for (String listType : LIST_TYPE) {
									if (_text.startsWith(listType, i + 1)) {
										ltype = listType;
										i += listType.length();
										break;
									}
								}

								if (!listOpen && ltype == null) {
									ltype = "disc";
								}

								if (ltype != null) {
									if (listOpen) {
										writer.write("</ul>");
									}

									writer.write("<ul style=\"list-style-type:");
									writer.write(ltype.trim());
									writer.write(";margin-left:1em;\">");
									listOpen = true;
								}

								writer.write("<li>");
								listItemClosed = false;
								break;
							default:
								break;
							}

							part.push(type);
						} else {
							writer.write(ch);
						}
					}
				}

				i++;
			}

			if (listOpen) {
				writer.write("</ul>");
			}

			if (!part.isEmpty()) {
				throw new IllegalArgumentException("One or more tokens remain unclosed.");
			}

			writer.write("</p>");
		}

	}

	private static PartType getPart(char ch) {
		switch (ch) {
		case '*':
			return PartType.LIST;
		case '+':
			return PartType.BOLD;
		case '~':
			return PartType.ITALIC;
		case '%':
			return PartType.BOLD_ITALIC;
		case '!':
			return PartType.ACTION;
		}

		return null;
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
