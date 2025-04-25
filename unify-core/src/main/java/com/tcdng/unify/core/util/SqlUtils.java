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
package com.tcdng.unify.core.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.common.annotation.ColumnType;
import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.StaticReference;
import com.tcdng.unify.core.database.StaticReferenceQuery;
import com.tcdng.unify.core.database.sql.SqlFieldDimensions;

/**
 * Provides utility methods for SQL manipulation.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class SqlUtils {

	private static final String IDENTIFIER_PREFIX_VIEW = "V_";

	private static final String IDENTIFIER_SUFFIX_FOREIGNKEY = "_FK";
	private static final String IDENTIFIER_SUFFIX_UNIQUECONSTRAINT = "_UC";
	private static final String IDENTIFIER_SUFFIX_INDEX = "_IX";

	private static final String IDENTIFIER_SUFFIX_FOREIGNKEY_LOWERCASE = IDENTIFIER_SUFFIX_FOREIGNKEY.toLowerCase();
	private static final String IDENTIFIER_SUFFIX_UNIQUECONSTRAINT_LOWERCASE = IDENTIFIER_SUFFIX_UNIQUECONSTRAINT
			.toLowerCase();
	private static final String IDENTIFIER_SUFFIX_INDEX_LOWERCASE = IDENTIFIER_SUFFIX_INDEX.toLowerCase();

	private static final List<String> VENDOR_IDENTIFIER_PREFIXES = Collections
			.unmodifiableList(Arrays.asList("SYS_IDX_", "SYS_"));

	private static final List<String> MANAGED_IDENTIFIER_SUFFIXES = Collections
			.unmodifiableList(Arrays.asList(SqlUtils.IDENTIFIER_SUFFIX_FOREIGNKEY,
					SqlUtils.IDENTIFIER_SUFFIX_UNIQUECONSTRAINT, SqlUtils.IDENTIFIER_SUFFIX_INDEX));

	private static final List<String> VENDOR_IDENTIFIER_PREFIXES_LOWERCASE = Collections
			.unmodifiableList(Arrays.asList("sys_idx_", "sys_"));

	private static final List<String> MANAGED_IDENTIFIER_SUFFIXES_LOWERCASE = Collections
			.unmodifiableList(Arrays.asList(SqlUtils.IDENTIFIER_SUFFIX_FOREIGNKEY_LOWERCASE,
					SqlUtils.IDENTIFIER_SUFFIX_UNIQUECONSTRAINT_LOWERCASE, SqlUtils.IDENTIFIER_SUFFIX_INDEX_LOWERCASE));

	private static final List<Class<? extends Number>> versionNoTypes;

	private static final Map<Integer, Class<?>> sqlToJavaTypeMap;

	private static final int DEFAULT_CHARACTER_LEN = 1;
	private static final int DEFAULT_DECIMAL_PRECISION = 18;
	private static final int DEFAULT_DECIMAL_SCALE = 2;
	private static final int DEFAULT_INTEGER_PRECISION = 10;
	private static final int DEFAULT_LONG_PRECISION = 20;
	private static final int DEFAULT_SHORT_PRECISION = 5;
	private static final int DEFAULT_STRING_LEN = 32;
	private static final int DEFAULT_STRINGARRAY_LEN = 1024;
	private static final int DEFAULT_ENUMCONST_LEN = StaticReference.CODE_LENGTH;

	private static final int MAX_CONSTRAINT_TABLE_PREFIX_LEN = 14;
	private static final int MAX_CONSTRAINT_TOTAL_FIELD_PREFIX_LEN = 12;

	private static final String CONSTRAINT_PREFIX = "CN_";
	private static final int MAX_CONSTRAINT_FIELDS = 3;
	private static final int MAX_CONSTRAINT_FIELD_PADDING_LEN = 4;

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
		sqlToJavaTypeMap.put(Types.VARBINARY, byte[].class);
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
		sqlToJavaTypeMap.put(Types.BINARY, java.util.Date.class);
		sqlToJavaTypeMap.put(Types.TINYINT, Integer.class);
		sqlToJavaTypeMap.put(Types.VARCHAR, String.class);
	};

	private SqlUtils() {

	}

	public static boolean isSupportedSqlType(int sqlType) {
		return sqlToJavaTypeMap.containsKey(sqlType);
	}

	public static Class<?> getJavaType(int sqlType) {
		return sqlToJavaTypeMap.get(sqlType);
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

	@SuppressWarnings("unchecked")
	public static List<Class<? extends Entity>> getEntityClassList(List<Class<?>> classList) {
		List<Class<? extends Entity>> entityClassList = new ArrayList<Class<? extends Entity>>();
		for (Class<?> entityClass : classList) {
			if (Entity.class.isAssignableFrom(entityClass)) {
				entityClassList.add((Class<? extends Entity>) entityClass);
			}
		}

		return entityClassList;
	}

	@SuppressWarnings("unchecked")
	public static List<Class<? extends Entity>> getDynamicEntityClassList(List<Class<?>> classList) {
		List<Class<? extends Entity>> entityClassList = new ArrayList<Class<? extends Entity>>();
		for (Class<?> entityClass : classList) {
			if (Entity.class.isAssignableFrom(entityClass)) {
				if (EntityTypeUtils.isDynamicType(entityClass.getName())) {
					entityClassList.add((Class<? extends Entity>) entityClass);
				}
			}
		}

		return entityClassList;
	}

	/**
	 * Translate name to SQL schema equivalent. Used for automatic generation of
	 * table and column names. This implementation converts all lower-case
	 * characters in name to upper-case.
	 * 
	 * @param name the name to convert
	 * @return String the converted name
	 */
	public static String generateSchemaElementName(String name) {
		return generateSchemaElementName(name, true);
	}

	/**
	 * Generates full schema element name.
	 * 
	 * @param schema the schema name
	 * @param name   the element name
	 * @return the full schema element name
	 */
	public static String generateFullSchemaElementName(String schema, String name) {
		if (StringUtils.isNotBlank(schema)) {
			return StringUtils.dotify(schema, name);
		}

		return name;
	}

	/**
	 * Translate name to SQL schema equivalent. Used for automatic generation of
	 * table and column names. This implementation converts all lower-case
	 * characters in name to upper-case.
	 * 
	 * @param name         the name to convert
	 * @param applySpacing indicates if spacing with underscore be applied at name
	 *                     lowercase-uppercase boundaries. For example age -&gt; AGE
	 *                     sQLName - SQLNAME sortCode -&gt; SORT_CODE.,
	 *                     moduleActivityId -&gt; MODULE_ACTIVITY_ID
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
	 * Translate name to SQL view name equivalent. Used for automatic generation of
	 * table and column names. This implementation converts all lower-case
	 * characters in name to upper-case.
	 * 
	 * @param name         the name to convert
	 * @param applySpacing indicates if spacing with underscore be applied at name
	 *                     lowercase-uppercase boundaries. For example address -&gt;
	 *                     V_ADDRESS, moduleActivity -&gt; V_MODULE_ACTIVITY
	 * @return String the converted name
	 */
	public static String generateViewName(String name, boolean applySpacing) {
		return String.format("%s%s", IDENTIFIER_PREFIX_VIEW, SqlUtils.generateSchemaElementName(name, applySpacing));
	}

	/**
	 * Genarates a view name.
	 * 
	 * @param tableSchemaName the table name
	 * @return the view name
	 */
	public static String generateViewName(String tableSchemaName) {
		return String.format("%s%s", IDENTIFIER_PREFIX_VIEW, tableSchemaName);
	}

	/**
	 * Generates a foreign key constraint name.
	 * 
	 * @param constraintIndex constraint index
	 * @param uniqueTableId   optional unique table ID
	 * @param tableName       the table name
	 * @param fieldName       the field name
	 * @return the generated foreign key constraint name
	 */
	public static String generateForeignKeyConstraintName(int constraintIndex, Long uniqueTableId, String tableName,
			String fieldName) {
		return String.format("%s%s",
				SqlUtils.generateConstraintName(constraintIndex, uniqueTableId, tableName, fieldName),
				IDENTIFIER_SUFFIX_FOREIGNKEY);
	}

	/**
	 * Generates a unique constraint name.
	 * 
	 * @param constraintIndex constraint index
	 * @param uniqueTableId   optional unique table ID
	 * @param tableName       the table name
	 * @param fieldNames      the field names
	 * @return the generated constraint name string
	 */
	public static String generateUniqueConstraintName(int constraintIndex, Long uniqueTableId, String tableName,
			String... fieldNames) {
		return String.format("%s%s",
				SqlUtils.generateConstraintName(constraintIndex, uniqueTableId, tableName, fieldNames),
				IDENTIFIER_SUFFIX_UNIQUECONSTRAINT);
	}

	/**
	 * Generates an index name.
	 * 
	 * @param constraintIndex constraint index
	 * @param uniqueTableId   optional unique table ID
	 * @param tableName       the table name
	 * @param fieldNames      the field names
	 * @return the generated index string
	 */
	public static String generateIndexName(int constraintIndex, Long uniqueTableId, String tableName,
			String... fieldNames) {
		return String.format("%s%s",
				SqlUtils.generateConstraintName(constraintIndex, uniqueTableId, tableName, fieldNames),
				IDENTIFIER_SUFFIX_INDEX);
	}

	/**
	 * Resolves a constraint name from a suggested vendor object name.
	 * 
	 * @param suggestedName           the suggested name
	 * @param isAllObjectsInLowerCase all objects in lower case
	 * @return the resolved constraint name otherwise null
	 */
	public static String resolveConstraintName(String suggestedName, boolean isAllObjectsInLowerCase) {
		if (StringUtils.isNotBlank(suggestedName)) {
			// Trim prefix
			List<String> refList = VENDOR_IDENTIFIER_PREFIXES;
			if (isAllObjectsInLowerCase) {
				refList = VENDOR_IDENTIFIER_PREFIXES_LOWERCASE;
			}

			for (String vendorPrefix : refList) {
				if (suggestedName.startsWith(vendorPrefix)) {
					suggestedName = suggestedName.substring(vendorPrefix.length());
					break;
				}
			}

			// Trim and detect suffix
			refList = MANAGED_IDENTIFIER_SUFFIXES;
			if (isAllObjectsInLowerCase) {
				refList = MANAGED_IDENTIFIER_SUFFIXES_LOWERCASE;
			}

			for (String managedSuffix : refList) {
				int index = suggestedName.lastIndexOf(managedSuffix);
				if (index > 0) {
					int nIndex = suggestedName.indexOf('_', index + 1);
					if (nIndex > 0) {
						return suggestedName.substring(0, nIndex);
					} else {
						return suggestedName;
					}
				}
			}
		}
		return null;
	}

	public static boolean isUniqueConstraintName(String elementName) {
		return elementName.indexOf(SqlUtils.IDENTIFIER_SUFFIX_UNIQUECONSTRAINT) > 0
				|| elementName.indexOf(SqlUtils.IDENTIFIER_SUFFIX_UNIQUECONSTRAINT_LOWERCASE) > 0;
	}

	/**
	 * Tests if supplied type is a supported version number type.
	 * 
	 * @param type the type to test
	 * @return a true value if type is a supported version number type
	 */
	public static boolean isVersionNumberType(Class<?> type) {
		return versionNoTypes.contains(type);
	}

	/**
	 * Closes a prepared statement quietly.
	 * 
	 * @param pstmt the prepared statement to close
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
	 * @param stmt the prepared statement to close
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
	 * @param rs the result set to close
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
	 * @param conn the connection to close
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
		case DECIMAL_ARRAY:
		case BOOLEAN_ARRAY:
		case DATE_ARRAY:
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
		case TIMESTAMP_UTC:
		default:
			break;
		}
		return new SqlFieldDimensions(nLength, nPrecision, nScale);
	}

	private static String generateConstraintName(int constraintIndex, Long uniqueTableId, String tableName,
			String... fieldNames) {
		StringBuilder sb = new StringBuilder();
		if (uniqueTableId != null) {
			sb.append(CONSTRAINT_PREFIX);
			sb.append(String.format("%03X", constraintIndex));
			sb.append("_");

			for (int i = 0; i < fieldNames.length && i < MAX_CONSTRAINT_FIELDS; i++) {
				String fieldName = fieldNames[i];
				if (fieldName.length() > MAX_CONSTRAINT_FIELD_PADDING_LEN) {
					sb.append(fieldName.substring(0, MAX_CONSTRAINT_FIELD_PADDING_LEN));
				} else {
					sb.append(fieldName);
				}
			}

			sb.append("_");
			sb.append(String.format("%08X", uniqueTableId));
		} else {
			if (tableName.length() > MAX_CONSTRAINT_TABLE_PREFIX_LEN) {
				int midLen = tableName.length() / 2;
				sb.append(tableName.substring(0, MAX_CONSTRAINT_TABLE_PREFIX_LEN / 2))
						.append(tableName.substring(midLen, midLen + MAX_CONSTRAINT_TABLE_PREFIX_LEN / 2));
			} else {
				sb.append(tableName);
			}

			if (fieldNames.length > 0) {
				sb.append('_');
				int fMaxLen = MAX_CONSTRAINT_TOTAL_FIELD_PREFIX_LEN / fieldNames.length;
				if (fMaxLen <= 1) {
					fMaxLen = 2;
				}

				for (String fieldName : fieldNames) {
					if (fieldName.length() > fMaxLen) {
						int midLen = fieldName.length() / 2;
						sb.append(fieldName.substring(0, fMaxLen / 2))
								.append(fieldName.substring(midLen, midLen + fMaxLen / 2));
					} else {
						sb.append(fieldName);
					}
				}
			}
		}

		return sb.toString().toUpperCase();
	}

}
