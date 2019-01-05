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
package com.tcdng.unify.core.database.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.StaticReference;
import com.tcdng.unify.core.database.StaticReferenceQuery;

/**
 * Provides utility methods for SQL manipulation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class SqlUtils {

    private static final List<Class<? extends Number>> versionNoTypes;

    private static final Map<Integer, Class<?>> sqlToJavaTypeMap;

    private static final int DEFAULT_CHARACTER_LEN = 1;
    private static final int DEFAULT_DECIMAL_PRECISION = 18;
    private static final int DEFAULT_DECIMAL_SCALE = 2;
    private static final int DEFAULT_INTEGER_PRECISION = 10;
    private static final int DEFAULT_LONG_PRECISION = 20;
    private static final int DEFAULT_SHORT_PRECISION = 5;
    private static final int DEFAULT_STRING_LEN = 32;
    private static final int DEFAULT_STRINGARRAY_LEN = 256;
    private static final int DEFAULT_ENUMCONST_LEN = StaticReference.CODE_LENGTH;

    private static final Set<String> defaultConstants;
    
    
    static {
        versionNoTypes = new ArrayList<Class<? extends Number>>();
        versionNoTypes.add(Long.class);
        versionNoTypes.add(long.class);
        versionNoTypes.add(Integer.class);
        versionNoTypes.add(int.class);
        versionNoTypes.add(Short.class);
        versionNoTypes.add(short.class);

        sqlToJavaTypeMap = new HashMap<Integer, Class<?>>();
        sqlToJavaTypeMap.put(Types.BIGINT, Long.class);
        sqlToJavaTypeMap.put(Types.BIT, Boolean.class);
        sqlToJavaTypeMap.put(Types.LONGVARBINARY, byte[].class);
        sqlToJavaTypeMap.put(Types.BLOB, byte[].class);
        sqlToJavaTypeMap.put(Types.BOOLEAN, Boolean.class);
        sqlToJavaTypeMap.put(Types.CHAR, String.class);
        sqlToJavaTypeMap.put(Types.CLOB, String.class);
        sqlToJavaTypeMap.put(Types.DATE, java.util.Date.class);
        sqlToJavaTypeMap.put(Types.DECIMAL, BigDecimal.class);
        sqlToJavaTypeMap.put(Types.DOUBLE, Double.class);
        sqlToJavaTypeMap.put(Types.FLOAT, Float.class);
        sqlToJavaTypeMap.put(Types.INTEGER, Integer.class);
        sqlToJavaTypeMap.put(Types.LONGVARCHAR, String.class);
        sqlToJavaTypeMap.put(Types.NUMERIC, BigDecimal.class);
        sqlToJavaTypeMap.put(Types.REAL, Float.class);
        sqlToJavaTypeMap.put(Types.SMALLINT, Integer.class);
        sqlToJavaTypeMap.put(Types.TIME, java.util.Date.class);
        sqlToJavaTypeMap.put(Types.TIMESTAMP, java.util.Date.class);
        sqlToJavaTypeMap.put(Types.TINYINT, Integer.class);
        sqlToJavaTypeMap.put(Types.VARCHAR, String.class);
        
        defaultConstants = new HashSet<String>(Arrays.asList("0000-00-00 00:00:00"));
    };

    private SqlUtils() {

    }

    public static boolean isSupportedSqlType(int sqlType) {
        return sqlToJavaTypeMap.containsKey(sqlType);
    }

    public static Class<?> getJavaType(int sqlType) {
        return sqlToJavaTypeMap.get(sqlType);
    }

    public static boolean isDefaultConstant(String defaultVal) {
        return defaultConstants.contains(defaultVal);
    }
    
    public static Class<?> getEntityClass(Entity record) {
        Class<?> entityClass = record.getClass();
        if (StaticReference.class.equals(entityClass)) {
            return ((StaticReference) record).getEnumConstType();
        }

        return entityClass;
    }

    public static Class<?> getEntityClass(Query<? extends Entity> query) {
        Class<?> entityClass = query.getEntityClass();
        if (StaticReference.class.equals(entityClass)) {
            return ((StaticReferenceQuery) query).getEnumConstType();
        }
        return entityClass;
    }

    /**
     * Translate name to SQL schema equivalent. Used for automatic generation of
     * table and column names. This implementation converts all lower-case
     * characters in name to upper-case.
     * 
     * @param name
     *            the name to convert
     * @return String the converted name
     */
    public static String generateSchemaElementName(String name) {
        return generateSchemaElementName(name, true);
    }

    /**
     * Translate name to SQL schema equivalent. Used for automatic generation of
     * table and column names. This implementation converts all lower-case
     * characters in name to upper-case.
     * 
     * @param name
     *            the name to convert
     * @param applySpacing
     *            indicates if spacing with undescore be applied at name
     *            lowercase-uppercase boundaries. For example age -&gt; AGE sQLName
     *            - SQLNAME sortCode -&gt; SORT_CODE., moduleActivityId -&gt;
     *            MODULE_ACTIVITY_ID
     * @return String the converted name
     */
    public static String generateSchemaElementName(String name, boolean applySpacing) {
        if (applySpacing) {
            StringBuilder sb = new StringBuilder();
            int strLen = name.length();
            boolean lastLowerCase = true;
            for (int i = 0; i < strLen; i++) {
                char ch = name.charAt(i);
                boolean currentLowerCase = Character.isLowerCase(ch);
                if (lastLowerCase && !currentLowerCase && i > 1) {
                    sb.append('_');
                }
                sb.append(ch);
                lastLowerCase = currentLowerCase;
            }
            return sb.toString().toUpperCase();
        }

        return name.toUpperCase();
    }

    /**
     * Generates a foreign key constraint name.
     * 
     * @param tableName
     *            the table name
     * @param index
     *            the index
     * @return the generated foreign key constraint name
     */
    public static String generateForeignKeyConstraintName(String tableName, int index) {
        return String.format("%s_FK%02d", tableName, index);
    }

    /**
     * Generates a unique constraint name.
     * 
     * @param tableName
     *            the table name
     * @param index
     *            the index
     * @return the generated constraint name string
     */
    public static String generateUniqueConstraintName(String tableName, int index) {
        return String.format("%s_UC%02d", tableName, index);
    }

    /**
     * Generates an index name.
     * 
     * @param tableName
     *            the table name
     * @param index
     *            the index
     * @return the generated index string
     */
    public static String generateIndexName(String tableName, int index) {
        return String.format("%s_IDX%02d", tableName, index);
    }

    /**
     * Tests if supplied type is a supported version number type.
     * 
     * @param type
     *            the type to test
     * @return a true value if type is a supported version number type
     */
    public static boolean isVersionNumberType(Class<?> type) {
        return versionNoTypes.contains(type);
    }

    /**
     * Closes a prepared statement quietly.
     * 
     * @param pstmt
     *            the prepared statement to close
     */
    public static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Closes a statement quietly.
     * 
     * @param stmt
     *            the prepared statement to close
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Closes result set quietly.
     * 
     * @param rs
     *            the result set to close
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Closes connection quietly.
     * 
     * @param conn
     *            the connection to close
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    public static Boolean getBoolean(String value) {
        if (value == null) {
            return null;
        }

        return Boolean.valueOf("Y".equalsIgnoreCase(value));
    }

    public static String getString(Boolean value) {
        if (value == null) {
            return null;
        }

        if ((Boolean) value) {
            return "Y";
        }

        return "N";
    }

    public static String getQualifierColumnName(String tableName, String columnName) {
        return tableName + '.' + columnName;
    }
    
    public static SqlFieldDimensions getNormalizedSqlFieldDimensions(ColumnType columnType, int length, int precision,
            int scale) {
        int nLength = 0, nPrecision = 0, nScale = 0;
        switch (columnType) {
            case BOOLEAN:
            case CHARACTER:
                nLength = DEFAULT_CHARACTER_LEN;
                break;
            case DECIMAL:
                if (precision <= 0) {
                    nPrecision = DEFAULT_DECIMAL_PRECISION;
                } else {
                    nPrecision = precision;
                }
                
                if (scale < 0) {
                    nScale = DEFAULT_DECIMAL_SCALE;
                } else {
                    nScale = scale;
                }
                break;
            case ENUMCONST:
                nLength = DEFAULT_ENUMCONST_LEN;
                break;
            case INTEGER:
                if (precision <= 0) {
                    nPrecision = DEFAULT_INTEGER_PRECISION;
                } else {
                    nPrecision = precision;
                }
                break;
            case LONG:
                if (precision <= 0) {
                    nPrecision = DEFAULT_LONG_PRECISION;
                } else {
                    nPrecision = precision;
                }
                break;
            case SHORT:
                if (precision <= 0) {
                    nPrecision = DEFAULT_SHORT_PRECISION;
                } else {
                    nPrecision = precision;
                }
                break;
            case STRING:
                if (length <= 0) {
                    nLength = DEFAULT_STRING_LEN;
                } else {
                    nLength = length;
                }
                break;
            case BOOLEAN_ARRAY:
            case DOUBLE_ARRAY:
            case FLOAT_ARRAY:
            case INTEGER_ARRAY:
            case LONG_ARRAY:
            case SHORT_ARRAY:
            case STRING_ARRAY:
                if (length <= 0) {
                    nLength = DEFAULT_STRINGARRAY_LEN;
                } else {
                    nLength = length;
                }
                break;
            case AUTO:
            case BLOB:
            case CLOB:
            case DATE:
            case DOUBLE:
            case FLOAT:
            case TIMESTAMP:
            default:
                break;
        }
        return new SqlFieldDimensions(nLength, nPrecision, nScale);
    }
}
