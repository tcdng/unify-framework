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
package com.tcdng.unify.core.util.json;

/**
 * JSON utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class JsonUtils {

    private JsonUtils() {

    }

    public static String getFieldEntry(String fieldName, String val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, String[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, Number val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, Number[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, Boolean val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, Boolean[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, char val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, char[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, int val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, int[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, long val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, long[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, short val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, short[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, float val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, float[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, double val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, double[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, boolean val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static String getFieldEntry(String fieldName, boolean[] val) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.writeField(sb, fieldName, val);
        return sb.toString();
    }

    public static void writeField(StringBuilder sb, String fieldName, String[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, String val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":\"");
            int len = val.length();
            for (int i = 0; i < len; i++) {
                JsonUtils.writeChar(sb, val.charAt(i));
            }
            sb.append('"');
        } else {
            sb.append("\":null");
        }

    }

    public static void writeField(StringBuilder sb, String fieldName, Number[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, Number val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":").append(val);
        } else {
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, Boolean[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, Boolean val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":").append(val);
        } else {
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, char[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, char val) {
        sb.append('"').append(fieldName).append("\":\"");
        JsonUtils.writeChar(sb, val);
        sb.append('"');
    }

    public static void writeField(StringBuilder sb, String fieldName, int[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, int val) {
        sb.append('"').append(fieldName).append("\":").append(val);
    }

    public static void writeField(StringBuilder sb, String fieldName, long[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, long val) {
        sb.append('"').append(fieldName).append("\":").append(val);
    }

    public static void writeField(StringBuilder sb, String fieldName, short[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, short val) {
        sb.append('"').append(fieldName).append("\":").append(val);
    }

    public static void writeField(StringBuilder sb, String fieldName, float[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, float val) {
        sb.append('"').append(fieldName).append("\":").append(val);
    }

    public static void writeField(StringBuilder sb, String fieldName, double[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, double val) {
        sb.append('"').append(fieldName).append("\":").append(val);
    }

    public static void writeField(StringBuilder sb, String fieldName, boolean[] val) {
        sb.append('"').append(fieldName);
        if (val != null) {
            sb.append("\":[");
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
            sb.append("\":null");
        }
    }

    public static void writeField(StringBuilder sb, String fieldName, boolean val) {
        sb.append('"').append(fieldName).append("\":").append(val);
    }

    private static void writeChar(StringBuilder sb, char ch) {
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
}
