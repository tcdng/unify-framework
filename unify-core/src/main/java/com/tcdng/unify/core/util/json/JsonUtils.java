/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.util.json;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.LargeStringWriter;

/**
 * JSON utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class JsonUtils {

	private JsonUtils() {

	}

	public static String getWriteField(String fieldName, String val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, String[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, Number val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, Number[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, Boolean val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, Boolean[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, char val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, char[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, int val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, int[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, long val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, long[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, short val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, short[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, float val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, float[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, double val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, double[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, boolean val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static String getWriteField(String fieldName, boolean[] val) {
		StringBuilder sb = new StringBuilder();
		JsonUtils.writeField(sb, fieldName, val);
		return sb.toString();
	}

	public static void writeField(StringBuilder sb, String fieldName, String[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, String val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, Number[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, Number val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, Boolean[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, Boolean val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, char[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, char val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, int[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, int val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, long[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, long val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, short[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, short val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, float[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, float val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, double[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, double val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, boolean[] val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(StringBuilder sb, String fieldName, boolean val) {
		JsonUtils.writeFieldPrefix(sb, fieldName);
		JsonUtils.write(sb, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, String[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, String val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, Number[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, Number val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, Boolean[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, Boolean val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, char[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, char val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, int[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, int val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, long[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, long val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, short[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, short val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, float[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, float val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, double[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, double val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, boolean[] val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, boolean val) {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.write(lsw, val);
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, Object val) throws UnifyException {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.writeObject(lsw, new JsonWriter().writeObject(val).toString());
	}

	public static void writeField(LargeStringWriter lsw, String fieldName, Object[] val) throws UnifyException {
		JsonUtils.writeFieldPrefix(lsw, fieldName);
		JsonUtils.writeObject(lsw, new JsonWriter().writeObject(val).toString());
	}

	public static void write(StringBuilder sb, String[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (String _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				if (_val == null) {
					sb.append("null");
				} else {
					sb.append('"');
					int len = _val.length();
					for (int i = 0; i < len; i++) {
						JsonUtils.writeChar(sb, _val.charAt(i));
					}
					sb.append('"');
				}
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, String val) {
		if (val != null) {
			sb.append('"');
			int len = val.length();
			for (int i = 0; i < len; i++) {
				JsonUtils.writeChar(sb, val.charAt(i));
			}
			sb.append('"');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, Number[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (Number _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				if (_val != null) {
					sb.append(_val);
				} else {
					sb.append("null");
				}
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, Number val) {
		if (val != null) {
			sb.append(val);
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, Boolean[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (Boolean _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				if (_val != null) {
					sb.append(_val);
				} else {
					sb.append("null");
				}
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, Boolean val) {
		if (val != null) {
			sb.append(val);
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, char[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (char _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				sb.append('"');
				JsonUtils.writeChar(sb, _val);
				sb.append('"');
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, char val) {
		sb.append('"');
		JsonUtils.writeChar(sb, val);
		sb.append('"');
	}

	public static void write(StringBuilder sb, int[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (int _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				sb.append(_val);
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, int val) {
		sb.append(val);
	}

	public static void write(StringBuilder sb, long[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (long _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				sb.append(_val);
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, long val) {
		sb.append(val);
	}

	public static void write(StringBuilder sb, short[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (short _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				sb.append(_val);
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, short val) {
		sb.append(val);
	}

	public static void write(StringBuilder sb, float[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (float _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				sb.append(_val);
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, float val) {
		sb.append(val);
	}

	public static void write(StringBuilder sb, double[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (double _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				sb.append(_val);
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, double val) {
		sb.append(val);
	}

	public static void write(StringBuilder sb, boolean[] val) {
		if (val != null) {
			sb.append('[');
			boolean appendSym = false;
			for (boolean _val : val) {
				if (appendSym) {
					sb.append(',');
				} else {
					appendSym = true;
				}

				sb.append(_val);
			}
			sb.append(']');
		} else {
			sb.append("null");
		}
	}

	public static void write(StringBuilder sb, boolean val) {
		sb.append(val);
	}

	public static void writeChar(StringBuilder sb, char ch) {
		switch (ch) {
		case '"':
		case '\\':
		case '/':
			sb.append('\\').append(ch);
			break;
		case '\t':
			sb.append("\\t");
			break;
		case '\f':
			sb.append("\\f");
			break;
		case '\b':
			sb.append("\\b");
			break;
		case '\r':
			sb.append("\\r");
			break;
		case '\n':
			sb.append("\\n");
			break;
		default:
			if (ch < ' ' || ch > 127) {
				String hex = Integer.toHexString(ch);
				int padLen = 4 - hex.length();
				sb.append("\\u");
				while (padLen-- > 0) {
					sb.append('0');
				}
				sb.append(hex);
			} else {
				sb.append(ch);
			}
		}
	}

	public static void write(LargeStringWriter lsw, String[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (String _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				if (_val == null) {
					lsw.append("null");
				} else {
					lsw.append('"');
					int len = _val.length();
					for (int i = 0; i < len; i++) {
						JsonUtils.writeChar(lsw, _val.charAt(i));
					}
					lsw.append('"');
				}
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, String val) {
		if (val != null) {
			lsw.append('"');
			int len = val.length();
			for (int i = 0; i < len; i++) {
				JsonUtils.writeChar(lsw, val.charAt(i));
			}
			lsw.append('"');
		} else {
			lsw.append("null");
		}
	}

	public static void writeObject(LargeStringWriter lsw, String val) {
		if (val != null) {
			lsw.append(val);
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, Number[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (Number _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				if (_val != null) {
					lsw.append(_val);
				} else {
					lsw.append("null");
				}
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, Number val) {
		if (val != null) {
			lsw.append(val);
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, Boolean[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (Boolean _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				if (_val != null) {
					lsw.append(_val);
				} else {
					lsw.append("null");
				}
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, Boolean val) {
		if (val != null) {
			lsw.append(val);
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, char[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (char _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				lsw.append('"');
				JsonUtils.writeChar(lsw, _val);
				lsw.append('"');
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, char val) {
		lsw.append('"');
		JsonUtils.writeChar(lsw, val);
		lsw.append('"');
	}

	public static void write(LargeStringWriter lsw, int[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (int _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				lsw.append(_val);
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, int val) {
		lsw.append(val);
	}

	public static void write(LargeStringWriter lsw, long[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (long _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				lsw.append(_val);
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, long val) {
		lsw.append(val);
	}

	public static void write(LargeStringWriter lsw, short[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (short _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				lsw.append(_val);
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, short val) {
		lsw.append(val);
	}

	public static void write(LargeStringWriter lsw, float[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (float _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				lsw.append(_val);
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, float val) {
		lsw.append(val);
	}

	public static void write(LargeStringWriter lsw, double[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (double _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				lsw.append(_val);
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, double val) {
		lsw.append(val);
	}

	public static void write(LargeStringWriter lsw, boolean[] val) {
		if (val != null) {
			lsw.append('[');
			boolean appendSym = false;
			for (boolean _val : val) {
				if (appendSym) {
					lsw.append(',');
				} else {
					appendSym = true;
				}

				lsw.append(_val);
			}
			lsw.append(']');
		} else {
			lsw.append("null");
		}
	}

	public static void write(LargeStringWriter lsw, boolean val) {
		lsw.append(val);
	}

	public static void writeChar(LargeStringWriter lsw, char ch) {
		switch (ch) {
		case '"':
		case '\\':
		case '/':
			lsw.append('\\').append(ch);
			break;
		case '\t':
			lsw.append("\\t");
			break;
		case '\f':
			lsw.append("\\f");
			break;
		case '\b':
			lsw.append("\\b");
			break;
		case '\r':
			lsw.append("\\r");
			break;
		case '\n':
			lsw.append("\\n");
			break;
		default:
			if (ch < ' ' || ch > 127) {
				String hex = Integer.toHexString(ch);
				int padLen = 4 - hex.length();
				lsw.append("\\u");
				while (padLen-- > 0) {
					lsw.append('0');
				}
				lsw.append(hex);
			} else {
				lsw.append(ch);
			}
		}
	}

	private static void writeFieldPrefix(StringBuilder sb, String fieldName) {
		sb.append('"').append(fieldName).append("\":");
	}

	private static void writeFieldPrefix(LargeStringWriter lsw, String fieldName) {
		lsw.append('"').append(fieldName).append("\":");
	}
}
