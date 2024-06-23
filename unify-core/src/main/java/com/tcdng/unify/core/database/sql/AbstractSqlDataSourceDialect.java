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
package com.tcdng.unify.core.database.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.convert.util.ConverterUtils;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCoreConstants;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.constant.ForeignConstraints;
import com.tcdng.unify.core.constant.Indexes;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.constant.QueryAgainst;
import com.tcdng.unify.core.constant.TimeSeriesType;
import com.tcdng.unify.core.constant.UniqueConstraints;
import com.tcdng.unify.core.constant.Views;
import com.tcdng.unify.core.criterion.AggregateFunction;
import com.tcdng.unify.core.criterion.AggregateType;
import com.tcdng.unify.core.criterion.And;
import com.tcdng.unify.core.criterion.Equals;
import com.tcdng.unify.core.criterion.GroupingFunction;
import com.tcdng.unify.core.criterion.Order;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.criterion.Select;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.criterion.UpdateExpression;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.CallableProc;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.NativeParam;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.database.NativeUpdate;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.sql.criterion.policy.AmongstPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.AndPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.BetweenPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.EqualPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.GreaterOrEqualPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.GreaterPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.IEqualPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.ILikeBeginPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.ILikeEndPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.ILikePolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.INotEqualsPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.IsNotNullPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.IsNullPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LessOrEqualPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LessPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LikeBeginPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LikeEndPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LikePolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotAmongstPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotBetweenPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotEqualsPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotLikeBeginPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotLikeEndPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotLikePolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.OrPolicy;
import com.tcdng.unify.core.database.sql.data.policy.BigDecimalPolicy;
import com.tcdng.unify.core.database.sql.data.policy.BlobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.BooleanArrayPolicy;
import com.tcdng.unify.core.database.sql.data.policy.BooleanPolicy;
import com.tcdng.unify.core.database.sql.data.policy.CharacterPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ClobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.DatePolicy;
import com.tcdng.unify.core.database.sql.data.policy.DoubleArrayPolicy;
import com.tcdng.unify.core.database.sql.data.policy.DoublePolicy;
import com.tcdng.unify.core.database.sql.data.policy.EnumConstPolicy;
import com.tcdng.unify.core.database.sql.data.policy.FloatArrayPolicy;
import com.tcdng.unify.core.database.sql.data.policy.FloatPolicy;
import com.tcdng.unify.core.database.sql.data.policy.IntegerArrayPolicy;
import com.tcdng.unify.core.database.sql.data.policy.IntegerPolicy;
import com.tcdng.unify.core.database.sql.data.policy.LongArrayPolicy;
import com.tcdng.unify.core.database.sql.data.policy.LongPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ShortArrayPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ShortPolicy;
import com.tcdng.unify.core.database.sql.data.policy.StringArrayPolicy;
import com.tcdng.unify.core.database.sql.data.policy.StringPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampUTCPolicy;
import com.tcdng.unify.core.transform.Transformer;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.QueryUtils;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Abstract SQL dialect implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Singleton(false)
public abstract class AbstractSqlDataSourceDialect extends AbstractUnifyComponent implements SqlDataSourceDialect {

	@Configurable
	private SqlEntityInfoFactory sqlEntityInfoFactory;

	@Configurable("4000")
	private long getStatementInfoTimeout;

	@Configurable("1")
	private int minStatementInfo;

	@Configurable("64")
	private int maxStatementInfo;

	private final Set<String> reservedWords;

	private SqlCacheFactory sqlCacheFactory;

	private SqlStatementPoolsFactory sqlStatementPoolsFactory;

	private SqlCallableStatementPools sqlCallableStatementPools;

	private Set<String> noPrecisionTypes;

	private String terminationSql;

	private String newLineSql;

	private String dataSourceName;

	private boolean allObjectsInLowerCase;

	private boolean useCallableFunctionMode;

	private boolean appendNullOnTblCreate;

	private boolean tenancyEnabled;

	public AbstractSqlDataSourceDialect(Collection<String> reservedWords, boolean useCallableFunctionMode) {
		this(reservedWords, useCallableFunctionMode, false);
	}

	public AbstractSqlDataSourceDialect(Collection<String> reservedWords, boolean useCallableFunctionMode,
			boolean appendNullOnTblCreate) {
		Set<String> _reservedWords = new HashSet<String>();
		for (String word: reservedWords) {
			_reservedWords.add(word.toUpperCase());
		}

		this.reservedWords = Collections.unmodifiableSet(_reservedWords);
		this.useCallableFunctionMode = useCallableFunctionMode;
		this.appendNullOnTblCreate = appendNullOnTblCreate;
		sqlCacheFactory = new SqlCacheFactory();
		sqlStatementPoolsFactory = new SqlStatementPoolsFactory();
		noPrecisionTypes = new HashSet<String>(
				Arrays.asList("BIGINT", "DATETIME", "TIMESTAMP", "INT2", "INT4", "INT8"));
	}

	@Override
	public boolean isTenancyEnabled() throws UnifyException {
		return tenancyEnabled;
	}

	@Override
	public Long getUserTenantId() throws UnifyException {
		return super.getUserTenantId();
	}

	@Override
	public String getDataSourceName() {
		return dataSourceName;
	}

	@Override
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	@Override
	public boolean matchColumnDefault(String nativeVal, String defaultVal) throws UnifyException {
		return DataUtils.equals(nativeVal, defaultVal)
				|| (defaultVal == null && "NULL".equalsIgnoreCase(String.valueOf(nativeVal)));
	}

	@Override
	public String generateAllCreateSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, ForeignConstraints foreignConstraints,
			UniqueConstraints uniqueConstraints, Indexes indexes, Views views, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append(generateCreateTableSql(sqlEntitySchemaInfo, PrintFormat.PRETTY));
		sb.append(terminationSql);
		if (format.isPretty()) {
			sb.append(newLineSql);
			sb.append(newLineSql);
		}

		if (foreignConstraints.isTrue() && sqlEntitySchemaInfo.isForeignKeys()) {
			for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntitySchemaInfo.getForeignKeyList()) {
				sb.append(
						generateAddForeignKeyConstraintSql(sqlEntitySchemaInfo, sqlForeignKeyInfo, PrintFormat.PRETTY));
				sb.append(terminationSql);
				if (format.isPretty()) {
					sb.append(newLineSql);
					sb.append(newLineSql);
				}
			}
		}

		if (uniqueConstraints.isTrue() && sqlEntitySchemaInfo.isUniqueConstraints()
				&& !isGeneratesUniqueConstraintsOnCreateTable()) {
			for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntitySchemaInfo.getUniqueConstraintList()
					.values()) {
				if (!sqlUniqueConstraintInfo.isWithConditionList()) {
					sb.append(generateAddUniqueConstraintSql(sqlEntitySchemaInfo, sqlUniqueConstraintInfo,
							PrintFormat.PRETTY));
					sb.append(terminationSql);
					if (format.isPretty()) {
						sb.append(newLineSql);
						sb.append(newLineSql);
					}
				}
			}
		}

		if (indexes.isTrue() && sqlEntitySchemaInfo.isIndexes() && !isGeneratesIndexesOnCreateTable()) {
			for (SqlIndexSchemaInfo sqlIndexInfo : sqlEntitySchemaInfo.getIndexList().values()) {
				sb.append(generateCreateIndexSql(sqlEntitySchemaInfo, sqlIndexInfo, PrintFormat.PRETTY));
				sb.append(terminationSql);
				if (format.isPretty()) {
					sb.append(newLineSql);
					sb.append(newLineSql);
				}
			}
		}

		if (views.isTrue() && sqlEntitySchemaInfo.isViewable()) {
			sb.append(generateCreateViewSql(sqlEntitySchemaInfo, PrintFormat.PRETTY));
			sb.append(terminationSql);
			if (format.isPretty()) {
				sb.append(newLineSql);
				sb.append(newLineSql);
			}
		}

		List<Map<String, Object>> staticValueList = sqlEntitySchemaInfo.getStaticValueList();
		if (staticValueList != null) {
			String insertValuesSql = generateInsertValuesSql(sqlEntitySchemaInfo, staticValueList, PrintFormat.PRETTY);
			sb.append(insertValuesSql);
		}
		return sb.toString();
	}

	@Override
	public String generateAllUpgradeSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlEntitySchemaInfo oldSqlEntitySchemaInfo, ForeignConstraints foreignConstraints,
			UniqueConstraints uniqueConstraints, Indexes indexes, Views views, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		// Rename table if name change
		if (!sqlEntitySchemaInfo.getSchemaTableName().equals(oldSqlEntitySchemaInfo.getSchemaTableName())) {
			String renameTableSql = generateRenameTable(sqlEntitySchemaInfo, oldSqlEntitySchemaInfo, format);
			sb.append(renameTableSql);
		}

		// Extract new, old and deleted field markers for non primary and
		// non-listonly fields
		List<Long> newMarkerList = new ArrayList<Long>();
		List<Long> oldMarkerList = new ArrayList<Long>();
		List<Long> deletedMarkerList = new ArrayList<Long>();
		for (SqlFieldSchemaInfo sqlFieldSchemaInfo : sqlEntitySchemaInfo.getFieldInfos()) {
			if (!sqlFieldSchemaInfo.isPrimaryKey() && !sqlFieldSchemaInfo.isListOnly()) {
				Long marker = sqlFieldSchemaInfo.getMarker();
				if (oldSqlEntitySchemaInfo.getFieldInfo(marker) != null) {
					oldMarkerList.add(marker);
				} else {
					newMarkerList.add(marker);
				}
			}
		}

		for (SqlFieldSchemaInfo sqlFieldSchemaInfo : oldSqlEntitySchemaInfo.getFieldInfos()) {
			Long marker = sqlFieldSchemaInfo.getMarker();
			if (!sqlFieldSchemaInfo.isPrimaryKey() && !sqlFieldSchemaInfo.isListOnly()) {
				if (!oldMarkerList.contains(marker)) {
					deletedMarkerList.add(marker);
				}
			}
		}

		// Rename and alter old columns if required (Ideal)
		StringBuilder rsb = new StringBuilder();
		StringBuilder asb = new StringBuilder();
		for (Long marker : oldMarkerList) {
			SqlFieldSchemaInfo sqlFieldSchemaInfo = sqlEntitySchemaInfo.getFieldInfo(marker);
			SqlFieldSchemaInfo oldSqlFieldSchemaInfo = oldSqlEntitySchemaInfo.getFieldInfo(marker);

			// Test for rename
			if (!sqlFieldSchemaInfo.getColumnName().equals(oldSqlFieldSchemaInfo.getColumnName())) {
				rsb.append(generateRenameColumn(sqlEntitySchemaInfo.getSchemaTableName(),
						oldSqlFieldSchemaInfo.getPreferredColumnName(), sqlFieldSchemaInfo, format));
			}

			// Test for alter
			SqlColumnAlterInfo sqlColumnAlterInfo = checkSqlColumnAltered(sqlFieldSchemaInfo, oldSqlFieldSchemaInfo);
			if (sqlColumnAlterInfo.isAltered()) {
				for (String sql : generateAlterColumn(sqlEntitySchemaInfo, sqlFieldSchemaInfo, sqlColumnAlterInfo,
						format)) {
					asb.append(sql);
				}
			}
		}
		sb.append(rsb.toString());
		sb.append(asb.toString());

		// Add new columns (Ideal)
		StringBuilder nsb = new StringBuilder();
		for (Long marker : newMarkerList) {
			SqlFieldSchemaInfo sqlFieldSchemaInfo = sqlEntitySchemaInfo.getFieldInfo(marker);
			nsb.append(generateAddColumn(sqlEntitySchemaInfo, sqlFieldSchemaInfo, format));
		}
		sb.append(nsb.toString());

		// Drop old columns (Ideal)
		StringBuilder dsb = new StringBuilder();
		for (Long marker : deletedMarkerList) {
			SqlFieldSchemaInfo sqlFieldSchemaInfo = oldSqlEntitySchemaInfo.getFieldInfo(marker);
			dsb.append(generateDropColumn(sqlEntitySchemaInfo, sqlFieldSchemaInfo, format));
		}
		sb.append(dsb.toString());

		return sb.toString();
	}

	@Override
	public final List<String> generateAlterColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, SqlColumnAlterInfo sqlColumnAlterInfo, PrintFormat format)
			throws UnifyException {
		if (sqlColumnAlterInfo.isAltered()) {
			if (sqlColumnAlterInfo.isTypeChange() && ColumnType.CLOB.equals(sqlFieldSchemaInfo.getColumnType())) {
				List<String> statements = new ArrayList<String>();
				// Create temporary
				final String tmpColumnName = sqlFieldSchemaInfo.getPreferredColumnName() + "_tmp";
				statements.add(generateAddColumn(sqlEntitySchemaInfo, tmpColumnName, sqlFieldSchemaInfo, format));

				// Copy
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" SET ")
						.append(tmpColumnName).append(" = ").append(sqlFieldSchemaInfo.getPreferredColumnName());
				statements.add(sb.toString());

				// Drop old column
				statements.add(generateDropColumn(sqlEntitySchemaInfo, sqlFieldSchemaInfo, format));

				// Rename new column
				statements.add(generateRenameColumn(sqlEntitySchemaInfo.getSchemaTableName(), tmpColumnName,
						sqlFieldSchemaInfo, format));
				return statements;
			}

			return doGenerateAlterColumn(sqlEntitySchemaInfo, sqlFieldSchemaInfo, sqlColumnAlterInfo, format);
		}

		return Collections.emptyList();
	}

	@Override
	public final String generateCreateTableSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName()).append('(');
		if (format.isPretty()) {
			sb.append(newLineSql);
		}
		boolean appendSym = false;
		for (SqlFieldSchemaInfo sqlFieldSchemaInfo : sqlEntitySchemaInfo.getManagedFieldInfos()) {
			if (appendSym) {
				sb.append(',');
				if (format.isPretty()) {
					sb.append(newLineSql);
				}
			} else {
				appendSym = true;
			}

			if (format.isPretty()) {
				sb.append('\t');
			}

			appendColumnAndTypeSql(sb, sqlFieldSchemaInfo.getPreferredColumnName(), sqlFieldSchemaInfo, false);
		}

		if (isGeneratesUniqueConstraintsOnCreateTable()) {
			for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntitySchemaInfo.getUniqueConstraintList()
					.values()) {
				if (!sqlUniqueConstraintInfo.isWithConditionList()) {
					sb.append(',');
					if (format.isPretty()) {
						sb.append(newLineSql);
					}

					if (format.isPretty()) {
						sb.append('\t');
					}

					sb.append(generateInlineUniqueConstraintSql(sqlEntitySchemaInfo, sqlUniqueConstraintInfo, format));
				}
			}
		}

		if (isGeneratesIndexesOnCreateTable()) {
			for (SqlIndexSchemaInfo sqlIndexInfo : sqlEntitySchemaInfo.getIndexList().values()) {
				sb.append(',');
				if (format.isPretty()) {
					sb.append(newLineSql);
				}

				if (format.isPretty()) {
					sb.append('\t');
				}

				sb.append(generateInlineIndexSql(sqlEntitySchemaInfo, sqlIndexInfo, format));
			}
		}

		if (format.isPretty()) {
			sb.append(newLineSql);
		}

		sb.append(')');
		return sb.toString();
	}

	@Override
	public String generateDropTableSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		return "DROP TABLE " + sqlEntitySchemaInfo.getSchemaTableName();
	}

	@Override
	public String generateRenameTable(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlEntitySchemaInfo oldSqlEntitySchemaInfo, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("RENAME TABLE ").append(oldSqlEntitySchemaInfo.getSchemaTableName()).append(" TO ")
				.append(sqlEntitySchemaInfo.getSchemaTableName());
		return sb.toString();
	}

	@Override
	public final String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, PrintFormat format) throws UnifyException {
		return this.generateAddColumn(sqlEntitySchemaInfo, sqlFieldSchemaInfo.getPreferredColumnName(),
				sqlFieldSchemaInfo, format);
	}

	@Override
	public String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, String columnName,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(' ');
		}
		sb.append("ADD COLUMN ");
		appendColumnAndTypeSql(sb, columnName, sqlFieldSchemaInfo, true);
		return sb.toString();
	}

	@Override
	public String generateRenameColumn(String tableName, String oldColumnName, SqlFieldSchemaInfo newSqlFieldInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(' ');
		}
		sb.append("RENAME COLUMN ").append(oldColumnName).append(" TO ")
				.append(newSqlFieldInfo.getPreferredColumnName());
		return sb.toString();
	}

	@Override
	public String generateDropColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(' ');
		}
		sb.append("DROP ").append(sqlFieldSchemaInfo.getPreferredColumnName());
		return sb.toString();
	}

	@Override
	public String generateAddForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlForeignKeySchemaInfo sqlForeignKeyInfo, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getManagedFieldInfo(sqlForeignKeyInfo.getFieldName());
		SqlEntitySchemaInfo foreignEntityInfo = sqlFieldInfo.getForeignEntityInfo();
		SqlFieldSchemaInfo foreignFieldInfo = sqlFieldInfo.getForeignFieldInfo();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}
		sb.append("ADD CONSTRAINT ").append(sqlFieldInfo.getConstraint());
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}
		sb.append("FOREIGN KEY (").append(sqlFieldInfo.getPreferredColumnName()).append(")");
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}
		sb.append("REFERENCES ").append(foreignEntityInfo.getSchemaTableName()).append('(')
				.append(foreignFieldInfo.getPreferredColumnName()).append(")");
		return sb.toString();
	}

	@Override
	public final String generateDropForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlForeignKeySchemaInfo sqlForeignKeyInfo, PrintFormat format) throws UnifyException {
		return generateDropForeignKeyConstraintSql(sqlEntitySchemaInfo,
				sqlEntitySchemaInfo.getFieldInfo(sqlForeignKeyInfo.getFieldName()).getConstraint(), format);
	}

	@Override
	public String generateDropForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbForeignKeyName,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}

		sb.append("DROP CONSTRAINT ").append(dbForeignKeyName);
		return sb.toString();
	}

	@Override
	public String generateInlineUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, PrintFormat format) throws UnifyException {
		throw new UnifyOperationException(new UnsupportedOperationException());
	}

	@Override
	public String generateAddUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}
		sb.append("ADD CONSTRAINT ").append(sqlUniqueConstraintInfo.getName());
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}
		sb.append("UNIQUE (");
		boolean appendSym = false;
		for (String fieldName : sqlUniqueConstraintInfo.getFieldNameList()) {
			if (appendSym)
				sb.append(',');
			else
				appendSym = true;

			SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getFieldInfo(fieldName);
			sb.append(sqlFieldInfo.getPreferredColumnName());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public final String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, PrintFormat format) throws UnifyException {
		return generateDropUniqueConstraintSql(sqlEntitySchemaInfo, sqlUniqueConstraintInfo.getName(), format);
	}

	@Override
	public String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			String dbUniqueConstraintName, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}

		sb.append("DROP CONSTRAINT ").append(dbUniqueConstraintName);
		return sb.toString();
	}

	@Override
	public String generateInlineIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
			PrintFormat format) throws UnifyException {
		throw new UnifyOperationException(new UnsupportedOperationException());
	}

	@Override
	public String generateCreateIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		sb.append("CREATE");
		if (sqlIndexInfo.isUnique()) {
			sb.append(" UNIQUE");
		}
		sb.append(" INDEX ").append(sqlIndexInfo.getName());
		if (format.isPretty()) {
			sb.append(newLineSql);
		} else {
			sb.append(" ");
		}
		sb.append("ON ").append(tableName).append("(");
		boolean appendSym = false;
		for (String fieldName : sqlIndexInfo.getFieldNameList()) {
			if (appendSym)
				sb.append(',');
			else
				appendSym = true;

			SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getFieldInfo(fieldName);
			sb.append(sqlFieldInfo.getPreferredColumnName());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public final String generateDropIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
			PrintFormat format) throws UnifyException {
		return generateDropIndexSql(sqlEntitySchemaInfo, sqlIndexInfo.getName(), format);
	}

	@Override
	public String generateDropIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbIndexName, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("DROP INDEX ").append(dbIndexName);
		return sb.toString();
	}

	@Override
	public String generateCreateViewSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, PrintFormat format)
			throws UnifyException {
		if (sqlEntitySchemaInfo.isViewOnly()) {
			return generateCreateViewSqlForViewEntity(sqlEntitySchemaInfo, format);
		} else {
			return generateCreateViewSqlForTableEntity(sqlEntitySchemaInfo, format);
		}
	}

	@Override
	public String generateDropViewSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		return "DROP VIEW " + sqlEntitySchemaInfo.getSchemaViewName();
	}

	@Override
	public String generateFindRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, QueryAgainst queryAgainst)
			throws UnifyException {
		StringBuilder findSql = new StringBuilder();
		findSql.append("SELECT ");
		boolean appensSym = false;
		for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getFieldInfos()) {
			if (appensSym) {
				findSql.append(", ");
			} else {
				appensSym = true;
			}

			findSql.append(sqlFieldInfo.getPreferredColumnName());
		}

		findSql.append(" FROM ");
		if (queryAgainst.isAgainstView()) {
			findSql.append(sqlEntitySchemaInfo.getSchemaViewName());
		} else {
			findSql.append(sqlEntitySchemaInfo.getSchemaTableName());
		}

		return findSql.toString();
	}

	@Override
	public String generateFindRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder findByPkSql = new StringBuilder();
		findByPkSql.append(generateFindRecordSql(sqlEntitySchemaInfo, QueryAgainst.TABLE)).append(" WHERE ")
				.append(sqlEntitySchemaInfo.getIdFieldInfo().getPreferredColumnName()).append(" = ?");
		return findByPkSql.toString();
	}

	@Override
	public String generateFindRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder findByPkVersionSql = new StringBuilder();
		if (sqlEntitySchemaInfo.isVersioned()) {
			findByPkVersionSql.append(generateFindRecordByPkSql(sqlEntitySchemaInfo)).append(" AND ")
					.append(sqlEntitySchemaInfo.getVersionFieldInfo().getPreferredColumnName()).append(" = ?");
		}
		return findByPkVersionSql.toString();
	}

	@Override
	public String generateListRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder listSql = new StringBuilder();
		listSql.append("SELECT ");
		boolean appensSym = false;
		for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getListFieldInfos()) {
			if (appensSym) {
				listSql.append(", ");
			} else {
				appensSym = true;
			}

			listSql.append(sqlFieldInfo.getPreferredColumnName());
		}

		listSql.append(" FROM ").append(sqlEntitySchemaInfo.getSchemaViewName());
		return listSql.toString();
	}

	@Override
	public String generateListRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder listByPkSql = new StringBuilder();
		listByPkSql.append(generateListRecordSql(sqlEntitySchemaInfo)).append(" WHERE ")
				.append(sqlEntitySchemaInfo.getIdFieldInfo().getPreferredColumnName()).append(" = ?");
		return listByPkSql.toString();
	}

	@Override
	public String generateListRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder listByPkVersionSql = new StringBuilder();
		if (sqlEntitySchemaInfo.isVersioned()) {
			listByPkVersionSql.append(generateListRecordByPkSql(sqlEntitySchemaInfo)).append(" AND ")
					.append(sqlEntitySchemaInfo.getVersionFieldInfo().getPreferredColumnName()).append(" = ?");
		}
		return listByPkVersionSql.toString();
	}

	@Override
	public String generateInsertRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder fsb = new StringBuilder();
		StringBuilder psb = new StringBuilder();
		for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getFieldInfos()) {
			if (fsb.length() > 0) {
				fsb.append(',');
				psb.append(',');
			}
			fsb.append(sqlFieldInfo.getPreferredColumnName());
			psb.append('?');
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("INSERT INTO ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" (").append(fsb)
				.append(") VALUES (").append(psb).append(")").toString();
		return insertSql.toString();
	}

	@Override
	public String generateInsertUnmanagedIdentityRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo)
			throws UnifyException {
		StringBuilder fsb = new StringBuilder();
		StringBuilder psb = new StringBuilder();
		for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getFieldInfos()) {
			if (!sqlFieldInfo.isPrimaryKey()) {
				if (fsb.length() > 0) {
					fsb.append(',');
					psb.append(',');
				}
				fsb.append(sqlFieldInfo.getPreferredColumnName());
				psb.append('?');
			}
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("INSERT INTO ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" (").append(fsb)
				.append(") VALUES (").append(psb).append(")").toString();
		return insertSql.toString();
	}

	@Override
	public String generateInsertValuesSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, Map<String, Object> params,
			PrintFormat format) throws UnifyException {
		StringBuilder fsb = new StringBuilder();
		StringBuilder psb = new StringBuilder();
		for (String fieldName : sqlEntitySchemaInfo.getFieldNames()) {
			SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getFieldInfo(fieldName);
			if (fsb.length() > 0) {
				fsb.append(',');
				psb.append(',');
			}
			fsb.append(sqlFieldInfo.getPreferredColumnName());
			psb.append(translateNativeSqlParam(params.get(fieldName)));
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("INSERT INTO ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" (").append(fsb)
				.append(")");
		if (format.isPretty()) {
			insertSql.append(newLineSql);
		} else {
			insertSql.append(" ");
		}
		insertSql.append("VALUES (").append(psb).append(")").toString();
		insertSql.append(terminationSql);
		if (format.isPretty()) {
			insertSql.append(newLineSql);
			insertSql.append(newLineSql);
		}
		return insertSql.toString();
	}

	@Override
	public String generateInsertValuesSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			List<Map<String, Object>> insertValueList, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> values : insertValueList) {
			sb.append(generateInsertValuesSql(sqlEntitySchemaInfo, values, format));
		}
		return sb.toString();
	}

	@Override
	public String generateUpdateRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder usb = new StringBuilder();
		for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getFieldInfos()) {
			if (!sqlFieldInfo.isPrimaryKey()) {
				if (usb.length() > 0) {
					usb.append(',');
				}
				usb.append(sqlFieldInfo.getPreferredColumnName()).append(" = ?");
			}
		}

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" SET ").append(usb);
		return updateSql.toString();
	}

	@Override
	public String generateUpdateRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder updateByPkSql = new StringBuilder();
		updateByPkSql.append(generateUpdateRecordSql(sqlEntitySchemaInfo)).append(" WHERE ")
				.append(sqlEntitySchemaInfo.getIdFieldInfo().getPreferredColumnName()).append(" = ?");
		return updateByPkSql.toString();
	}

	@Override
	public String generateUpdateRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder updateByPkVersionSql = new StringBuilder();
		if (sqlEntitySchemaInfo.isVersioned()) {
			updateByPkVersionSql.append(generateUpdateRecordByPkSql(sqlEntitySchemaInfo)).append(" AND ")
					.append(sqlEntitySchemaInfo.getVersionFieldInfo().getPreferredColumnName()).append(" = ?");
		}
		return updateByPkVersionSql.toString();
	}

	@Override
	public String generateDeleteRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("DELETE FROM ").append(sqlEntitySchemaInfo.getSchemaTableName());
		return deleteSql.toString();
	}

	@Override
	public String generateDeleteRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder deleteByPkSql = new StringBuilder();
		deleteByPkSql.append(generateDeleteRecordSql(sqlEntitySchemaInfo)).append(" WHERE ")
				.append(sqlEntitySchemaInfo.getIdFieldInfo().getPreferredColumnName()).append(" = ?");
		return deleteByPkSql.toString();
	}

	@Override
	public String generateDeleteRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
		StringBuilder deleteByPkVersionSql = new StringBuilder();
		if (sqlEntitySchemaInfo.isVersioned()) {
			deleteByPkVersionSql.append(generateDeleteRecordByPkSql(sqlEntitySchemaInfo)).append(" AND ")
					.append(sqlEntitySchemaInfo.getVersionFieldInfo().getPreferredColumnName()).append(" = ?");
		}

		return deleteByPkVersionSql.toString();
	}

	@Override
	public String generateCountRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, QueryAgainst queryAgainst)
			throws UnifyException {
		StringBuffer countSql = new StringBuffer();
		countSql.append("SELECT COUNT(*) FROM ");
		if (queryAgainst.isAgainstView()) {
			countSql.append(sqlEntitySchemaInfo.getSchemaViewName());
		} else {
			countSql.append(sqlEntitySchemaInfo.getSchemaTableName());
		}

		return countSql.toString();
	}

	@Override
	public final String generateLikeParameter(SqlLikeType type, Object param) throws UnifyException {
		return getSqlDataSourceDialectPolicies().generateLikeParameter(type, null, param);
	}

	@Override
	public final int getMaxClauseValues() {
		return getSqlDataSourceDialectPolicies().getMaxClauseValues();
	}

	@Override
	public SqlDataTypePolicy getSqlTypePolicy(Class<?> clazz) throws UnifyException {
		return getSqlDataSourceDialectPolicies().getSqlTypePolicy(DataUtils.getColumnType(clazz), 0);
	}

	@Override
	public SqlDataTypePolicy getSqlTypePolicy(ColumnType columnType, int length) throws UnifyException {
		return getSqlDataSourceDialectPolicies().getSqlTypePolicy(columnType, length);
	}

	@Override
	public List<SqlEntityInfo> findAllChildSqlEntityInfos(Class<?> clazz) throws UnifyException {
		return sqlEntityInfoFactory.findAllChildSqlEntityInfos(clazz);
	}

	@Override
	public SqlEntityInfo findSqlEntityInfo(Class<?> clazz) throws UnifyException {
		return sqlEntityInfoFactory.findSqlEntityInfo(clazz);
	}

	@Override
	public SqlEntityInfo createSqlEntityInfo(Class<?> clazz) throws UnifyException {
		return sqlEntityInfoFactory.createSqlEntityInfo(clazz);
	}

	@Override
	public SqlEntityInfo removeSqlEntityInfo(Class<?> clazz) throws UnifyException {
		return sqlEntityInfoFactory.removeSqlEntityInfo(clazz);
	}

	@Override
	public SqlCallableInfo getSqlCallableInfo(Class<? extends CallableProc> clazz) throws UnifyException {
		return sqlEntityInfoFactory.getSqlCallableInfo(clazz);
	}

	@Override
	public SqlCriteriaPolicy getSqlCriteriaPolicy(RestrictionType restrictionType) throws UnifyException {
		return getSqlDataSourceDialectPolicies().getSqlCriteriaPolicy(restrictionType);
	}

	@Override
	public SqlStatement prepareCountStatement(Query<? extends Entity> query, QueryAgainst queryAgainst)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		StringBuilder countSql = new StringBuilder();
		if (queryAgainst.isAgainstView()) {
			countSql.append(sqlCacheFactory.get(query.getEntityClass()).getCountViewSql());
		} else {
			countSql.append(sqlCacheFactory.get(query.getEntityClass()).getCountSql());
		}

		appendWhereClause(countSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);
		return new SqlStatement(sqlEntityInfo, SqlStatementType.COUNT, countSql.toString(), parameterInfoList);
	}

	@Override
	public SqlStatement prepareMinStatement(String columnName, Query<? extends Entity> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		StringBuilder minSql = new StringBuilder();
		minSql.append("SELECT MIN(").append(columnName).append(") FROM ").append(sqlEntityInfo.getSchemaViewName());
		appendWhereClause(minSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);
		return new SqlStatement(sqlEntityInfo, SqlStatementType.MIN, minSql.toString(), parameterInfoList);
	}

	@Override
	public SqlStatement prepareMaxStatement(String columnName, Query<? extends Entity> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		StringBuilder maxSql = new StringBuilder();
		maxSql.append("SELECT MAX(").append(columnName).append(") FROM ").append(sqlEntityInfo.getSchemaViewName());
		appendWhereClause(maxSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);
		return new SqlStatement(sqlEntityInfo, SqlStatementType.MAX, maxSql.toString(), parameterInfoList);
	}

	@Override
	public SqlStatement[] prepareDataSourceInitStatements() throws UnifyException {
		List<SqlStatement> list = Collections.emptyList();
		List<String> initSqlList = getDataSourceInitStatements();
		if (DataUtils.isNotBlank(initSqlList)) {
			list = new ArrayList<SqlStatement>();
			for (String sqlN : initSqlList) {
				list.add(new SqlStatement(null, SqlStatementType.UPDATE, sqlN));
			}
		}

		return list.toArray(new SqlStatement[list.size()]);
	}

	@Override
	public SqlStatement prepareAggregateStatement(AggregateFunction aggregateFunction, Query<? extends Entity> query)
			throws UnifyException {
		return internalPrepareAggregateStatement(aggregateFunction, query, null);
	}

	@Override
	public SqlStatement prepareAggregateStatement(List<AggregateFunction> aggregateFunctionList,
			Query<? extends Entity> query) throws UnifyException {
		return internalPrepareAggregateStatement(aggregateFunctionList, query, null);
	}

	@Override
	public SqlStatement prepareAggregateStatement(AggregateFunction aggregateFunction, Query<? extends Entity> query,
			List<GroupingFunction> groupingFunction) throws UnifyException {
		if (groupingFunction == null) {
			throw new IllegalArgumentException("Group function is required.");
		}

		return internalPrepareAggregateStatement(aggregateFunction, query, groupingFunction);
	}

	@Override
	public SqlStatement prepareAggregateStatement(List<AggregateFunction> aggregateFunctionList,
			Query<? extends Entity> query, List<GroupingFunction> groupingFunction) throws UnifyException {
		if (groupingFunction == null) {
			throw new IllegalArgumentException("Group function is required.");
		}

		return internalPrepareAggregateStatement(aggregateFunctionList, query, groupingFunction);
	}

	@Override
	public SqlStatement prepareCreateStatement(Entity record) throws UnifyException {
		return sqlStatementPoolsFactory.get(SqlUtils.getEntityClass(record)).getSqlStatement(SqlStatementType.CREATE,
				record);
	}

	@Override
	public SqlStatement prepareCreateStatementWithUnmanagedIdentity(Entity record) throws UnifyException {
		return sqlStatementPoolsFactory.get(SqlUtils.getEntityClass(record))
				.getSqlStatement(SqlStatementType.CREATE_UNMANAGED_IDENTITY, record);
	}

	@Override
	public SqlStatement prepareDeleteByPkStatement(Class<?> clazz, Object pk) throws UnifyException {
		return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.DELETE_BY_PK, pk);
	}

	@Override
	public SqlStatement prepareDeleteByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo)
			throws UnifyException {
		return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.DELETE_BY_PK_VERSION, pk,
				versionNo);
	}

	@Override
	public SqlStatement prepareDeleteStatement(Query<? extends Entity> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		StringBuilder deleteSql = new StringBuilder(sqlCacheFactory.get(sqlEntityInfo.getKeyClass()).getDeleteSql());
		appendWhereClause(deleteSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.DELETE);
		return new SqlStatement(sqlEntityInfo, SqlStatementType.DELETE, deleteSql.toString(), parameterInfoList);
	}

	@Override
	public SqlStatement prepareFindByPkStatement(Class<?> clazz, Object pk) throws UnifyException {
		if (EnumConst.class.isAssignableFrom(clazz)) {
			return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.FIND_BY_PK, pk);
		}

		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		return sqlStatementPoolsFactory.get(sqlEntityInfo.getEntityClass()).getSqlStatement(SqlStatementType.FIND_BY_PK,
				pk);
	}

	@Override
	public SqlStatement prepareFindByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo)
			throws UnifyException {
		if (EnumConst.class.isAssignableFrom(clazz)) {
			return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.FIND_BY_PK_VERSION, pk,
					versionNo);
		}

		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		return sqlStatementPoolsFactory.get(sqlEntityInfo.getEntityClass())
				.getSqlStatement(SqlStatementType.FIND_BY_PK_VERSION, pk, versionNo);
	}

	@Override
	public SqlStatement prepareFindStatement(Query<? extends Entity> query, QueryAgainst queryAgainst)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		List<SqlFieldInfo> returnFieldInfoList = null;
		StringBuilder findSql = new StringBuilder();
		Select select = query.getSelect();

		if ((select == null || (select.isEmpty() && !select.isDistinct())) && !query.isLimit()) {
			if (sqlEntityInfo.isViewOnly()) {
				returnFieldInfoList = sqlEntityInfo.getListFieldInfos();
			} else {
				returnFieldInfoList = sqlEntityInfo.getFieldInfos();
			}

			if (queryAgainst.isAgainstView()) {
				findSql.append(sqlCacheFactory.get(sqlEntityInfo.getKeyClass()).getFindViewSql());
			} else {
				findSql.append(sqlCacheFactory.get(sqlEntityInfo.getKeyClass()).getFindSql());
			}
		} else {
			findSql.append("SELECT ");
			if (select != null && select.isDistinct()) {
				findSql.append("DISTINCT ");
			}

			appendLimitOffsetInfixClause(findSql, query);

			boolean appendSym = false;
			if (select != null && !select.isEmpty()) {
				QueryUtils.setEssentialSelectFields(sqlEntityInfo, select);
				returnFieldInfoList = new ArrayList<SqlFieldInfo>();
				if (sqlEntityInfo.isViewOnly()) {
					for (String name : select.values()) {
						if (!sqlEntityInfo.isChildFieldInfo(name)) {
							SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(name);
							appendSym = appendPreferredColumn(findSql, sqlFieldInfo, appendSym);
							returnFieldInfoList.add(sqlFieldInfo);
						}
					}
				} else {
					for (String name : select.values()) {
						if (!sqlEntityInfo.isChildFieldInfo(name)) {
							SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getFieldInfo(name);
							appendSym = appendPreferredColumn(findSql, sqlFieldInfo, appendSym);
							returnFieldInfoList.add(sqlFieldInfo);
						}
					}
				}

				if (!select.isDistinct()) {
					// Select must always fetch primary keys because of child lists
					SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getIdFieldInfo();
					if (!select.contains(sqlFieldInfo.getName())) {
						appendSym = appendPreferredColumn(findSql, sqlFieldInfo, appendSym);
						returnFieldInfoList.add(sqlFieldInfo);
					}
				}

				// Always include tenant Id
				if (sqlEntityInfo.isWithTenantId()) {
					SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getTenantIdFieldInfo();
					appendSym = appendPreferredColumn(findSql, sqlFieldInfo, appendSym);
					returnFieldInfoList.add(sqlFieldInfo);
				}
			} else {
				if (sqlEntityInfo.isViewOnly()) {
					returnFieldInfoList = sqlEntityInfo.getListFieldInfos();
				} else {
					returnFieldInfoList = sqlEntityInfo.getFieldInfos();
				}

				for (SqlFieldInfo sqlFieldInfo : returnFieldInfoList) {
					appendSym = appendPreferredColumn(findSql, sqlFieldInfo, appendSym);
				}
			}

			// Select from view because criteria can contain view-only
			// properties
			findSql.append(" FROM ").append(sqlEntityInfo.getSchemaViewName());
		}

		appendWhereClause(findSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);
		return new SqlStatement(sqlEntityInfo, SqlStatementType.FIND, findSql.toString(), parameterInfoList,
				getSqlResultList(returnFieldInfoList));
	}

	@Override
	public SqlStatement prepareListByPkStatement(Class<?> clazz, Object pk) throws UnifyException {
		if (EnumConst.class.isAssignableFrom(clazz)) {
			return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.LIST_BY_PK, pk);
		}

		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		return sqlStatementPoolsFactory.get(sqlEntityInfo.getEntityClass()).getSqlStatement(SqlStatementType.LIST_BY_PK,
				pk);
	}

	@Override
	public SqlStatement prepareListByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo)
			throws UnifyException {
		if (EnumConst.class.isAssignableFrom(clazz)) {
			return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.LIST_BY_PK_VERSION, pk,
					versionNo);
		}

		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		return sqlStatementPoolsFactory.get(sqlEntityInfo.getEntityClass())
				.getSqlStatement(SqlStatementType.LIST_BY_PK_VERSION, pk, versionNo);
	}

	@Override
	public SqlStatement prepareListStatement(Query<? extends Entity> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		List<SqlFieldInfo> returnFieldInfoList = null;
		StringBuilder listSql = new StringBuilder();
		Select select = query.getSelect();

		if (select == null || (select.isEmpty() && !select.isDistinct())) {
			returnFieldInfoList = sqlEntityInfo.getListFieldInfos();
			listSql.append(sqlCacheFactory.get(sqlEntityInfo.getKeyClass()).getListSql());
		} else {
			listSql.append("SELECT ");
			if (select.isDistinct()) {
				listSql.append("DISTINCT ");
			}

			appendLimitOffsetInfixClause(listSql, query);

			boolean appendSym = false;
			if (!select.isEmpty()) {
				QueryUtils.setEssentialSelectFields(sqlEntityInfo, select);
				returnFieldInfoList = new ArrayList<SqlFieldInfo>();
				for (String name : select.values()) {
					if (!sqlEntityInfo.isChildFieldInfo(name)) {
						SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(name);
						if (appendSym) {
							listSql.append(", ");
						} else {
							appendSym = true;
						}

						listSql.append(sqlFieldInfo.getPreferredColumnName());
						returnFieldInfoList.add(sqlFieldInfo);
					}
				}

				// Select must always fetch primary keys because of child lists
				if (!select.isDistinct()) {
					SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getIdFieldInfo();
					if (!select.contains(sqlFieldInfo.getName())) {
						if (appendSym) {
							listSql.append(", ");
						} else {
							appendSym = true;
						}
						listSql.append(sqlFieldInfo.getPreferredColumnName());
						returnFieldInfoList.add(sqlFieldInfo);
					}
				}
			} else {
				returnFieldInfoList = sqlEntityInfo.getListFieldInfos();
				for (SqlFieldInfo sqlFieldInfo : returnFieldInfoList) {
					if (appendSym) {
						listSql.append(", ");
					} else {
						appendSym = true;
					}
					listSql.append(sqlFieldInfo.getPreferredColumnName());
				}
			}

			listSql.append(" FROM ").append(sqlEntityInfo.getSchemaViewName());
		}

		appendWhereClause(listSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);
		return new SqlStatement(sqlEntityInfo, SqlStatementType.LIST, listSql.toString(), parameterInfoList,
				getSqlResultList(returnFieldInfoList));
	}

	@Override
	public SqlStatement prepareUpdateStatement(NativeUpdate update) throws UnifyException {
		List<SqlParameter> parameterInfoList = Collections.emptyList();
		if (update.isWithParams()) {
			parameterInfoList = new ArrayList<SqlParameter>();
			for (NativeParam param : update.getParams()) {
				parameterInfoList.add(new SqlParameter(getSqlTypePolicy(param.getType(), 0), param.getParam()));
			}
		}

		return new SqlStatement(SqlStatementType.UPDATE, update.getUpdateSql(), parameterInfoList);
	}

	@Override
	public String generateNativeQuery(Query<? extends Entity> query) throws UnifyException {
		Class<?> entityClass = SqlUtils.getEntityClass(query);
		SqlEntityInfo sqlEntityInfo = findSqlEntityInfo(entityClass);
		StringBuilder listSql = new StringBuilder();
		Select select = query.getSelect();

		if (select == null || select.isEmpty()) {
			listSql.append(sqlCacheFactory.get(entityClass).getListSql());
		} else {
			listSql.append("SELECT ");
			if (select.isDistinct()) {
				listSql.append(" DISTINCT ");
			}

			appendLimitOffsetInfixClause(listSql, query);

			QueryUtils.setEssentialSelectFields(sqlEntityInfo, select);
			StringBuilder fieldsSql = new StringBuilder();
			for (String name : select.values()) {
				SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(name);
				if (fieldsSql.length() > 0) {
					fieldsSql.append(", ");
				}
				fieldsSql.append(sqlFieldInfo.getPreferredColumnName());
			}
			listSql.append(fieldsSql).append(" FROM ").append(sqlEntityInfo.getSchemaViewName());
		}

		appendWhereClause(listSql, sqlEntityInfo, query, SqlQueryType.SELECT);
		return listSql.toString();
	}

	@Override
	public String generateNativeQuery(NativeQuery query) throws UnifyException {
		StringBuilder sql = new StringBuilder();
		SqlTableNativeAliasGenerator aliasGenerator = new SqlTableNativeAliasGenerator();
		sql.append("SELECT ");
		if (query.isDistinct()) {
			sql.append(" DISTINCT ");
		}

		appendLimitOffsetInfixClause(sql, query.getOffset(), query.getLimit());

		String tableName = getTableName(query, query.getTableName());
		String talias = aliasGenerator.getTableNativeAlias(tableName);
		boolean isAppendSymbol = false;
		for (NativeQuery.Column column : query.getColumnList()) {
			if (isAppendSymbol) {
				sql.append(", ");
			} else {
				isAppendSymbol = true;
			}

			sql.append(aliasGenerator.getTableNativeAlias(getTableName(query, column.getTableName()))).append('.')
					.append(column.getColumnName());
		}

		sql.append(" FROM ").append(tableName).append(" ").append(talias);

		if (query.isJoin()) {
			for (NativeQuery.Join join : query.getJoinList()) {
				String tableA = getTableName(query, join.getTableA());
				String tableB = getTableName(query, join.getTableB());
				String aalias = aliasGenerator.getTableNativeAlias(tableA);
				String balias = aliasGenerator.getTableNativeAlias(tableB);
				sql.append(" ").append(join.getType().sql()).append(" ").append(tableB).append(" ").append(balias)
						.append(" ON ").append(balias).append(".").append(join.getColumnB()).append(" = ")
						.append(aalias).append(".").append(join.getColumnA());
			}
		}

		if (query.isRootFilter()) {
			sql.append(" WHERE ");
			NativeQuery.Filter rootFilter = query.getRootFilter();
			getSqlDataSourceDialectPolicies().getSqlCriteriaPolicy(rootFilter.getOp()).translate(sql, null, null,
					aliasGenerator, rootFilter.getSubFilterList());
		}

		if (query.isOrderBy()) {
			sql.append(" ORDER BY ");
			boolean appendSym = false;
			for (NativeQuery.OrderBy orderBy : query.getOrderByList()) {
				if (appendSym) {
					sql.append(',');
				} else {
					appendSym = true;
				}

				sql.append(orderBy.getColumnName()).append(' ').append(orderBy.getOrderType().code());
			}
		}
		appendLimitOffsetSuffixClause(sql, query.getOffset(), query.getLimit(), query.isRootFilter());
		return sql.toString();
	}

	@Override
	public Map<String, String> getFieldToNativeColumnMap(Class<? extends Entity> clazz) throws UnifyException {
		return findSqlEntityInfo(clazz).getListColumnsByFieldNames();
	}

	@Override
	public SqlStatement prepareUpdateByPkStatement(Entity record) throws UnifyException {
		return sqlStatementPoolsFactory.get(SqlUtils.getEntityClass(record))
				.getSqlStatement(SqlStatementType.UPDATE_BY_PK, record);
	}

	@Override
	public SqlStatement prepareUpdateByPkVersionStatement(Entity record, Object oldVersionNo) throws UnifyException {
		return sqlStatementPoolsFactory.get(SqlUtils.getEntityClass(record))
				.getSqlStatement(SqlStatementType.UPDATE_BY_PK_VERSION, record, oldVersionNo);
	}

	@Override
	public SqlStatement prepareUpdateStatement(Class<?> clazz, Object pk, Update update) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = findSqlEntityInfo(clazz);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		String updateParams = translateUpdateParams(sqlEntityInfo, parameterInfoList, update);

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(sqlEntityInfo.getSchemaTableName()).append(" SET ").append(updateParams);
		updateSql.append(" WHERE ");
		SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo("id");
		updateSql.append(sqlFieldInfo.getPreferredColumnName()).append("=?");
		parameterInfoList
				.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType(), sqlFieldInfo.getLength()), pk));

		return new SqlStatement(sqlEntityInfo, SqlStatementType.UPDATE, updateSql.toString(), parameterInfoList);
	}

	@Override
	public SqlStatement prepareUpdateStatement(Query<? extends Entity> query, Update update) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);

		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		String updateParams = translateUpdateParams(sqlEntityInfo, parameterInfoList, update);

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(sqlEntityInfo.getSchemaTableName()).append(" SET ").append(updateParams);
		appendWhereClause(updateSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.UPDATE);
		return new SqlStatement(sqlEntityInfo, SqlStatementType.UPDATE, updateSql.toString(), parameterInfoList);
	}

	@Override
	public SqlCallableStatement prepareCallableStatement(CallableProc callableProc) throws UnifyException {
		SqlCallableInfo sqlCallableInfo = sqlEntityInfoFactory.getSqlCallableInfo(callableProc.getClass());
		return sqlCallableStatementPools.getSqlCallableStatement(sqlCallableInfo, callableProc);
	}

	@Override
	public String getSqlBlobType() {
		return "java.sql.Blob";
	}

	@Override
	public String translateCriteria(Restriction restriction) throws UnifyException {
		StringBuilder sql = new StringBuilder();
		translateCriteria(sql, null, restriction);
		return sql.toString();
	}

	@Override
	public final String translateNativeSqlParam(Object param) throws UnifyException {
		return getSqlDataSourceDialectPolicies().translateToNativeSqlParam(param);
	}

	@Override
	public boolean isQueryOffsetOrLimit(Query<? extends Entity> query) throws UnifyException {
		return query.isOffset() || getQueryLimit(query) > 0;
	}

	@Override
	public void restoreStatement(SqlStatement sqlStatement) throws UnifyException {
		sqlStatementPoolsFactory.get(sqlStatement.getSqlEntityInfo().getKeyClass()).restore(sqlStatement);
	}

	@Override
	public void restoreCallableStatement(SqlCallableStatement sqlCallableStatement) throws UnifyException {
		sqlCallableStatementPools.restore(sqlCallableStatement);
	}

	@Override
	public SqlShutdownHook getShutdownHook() throws UnifyException {
		return null;
	}

	@Override
	public String getPreferredName(String name) {
		return name;
	}

	@Override
	public String ensureUnreservedIdentifier(String name) {
		return reservedWords.contains(name.toUpperCase()) ? "RZ_" + name : name;
	}

	@Override
	public final boolean isAllObjectsInLowerCase() {
		return allObjectsInLowerCase;
	}

	@Override
	public final void setAllObjectsInLowerCase(boolean allObjectsInLowerCase) {
		this.allObjectsInLowerCase = allObjectsInLowerCase;
	}

	@Override
	public boolean isReconstructViewsOnTableSchemaUpdate() throws UnifyException {
		return false;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		sqlEntityInfoFactory.setSqlDataSourceDialect(this);
		sqlCallableStatementPools = new SqlCallableStatementPools(
				getSqlDataSourceDialectPolicies().getSqlDataTypePolicies(), getStatementInfoTimeout, minStatementInfo,
				maxStatementInfo, useCallableFunctionMode);
		terminationSql = ";";
		newLineSql = getLineSeparator();
		tenancyEnabled = getContainerSetting(boolean.class, UnifyCorePropertyConstants.APPLICATION_TENANCY_ENABLED);
	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected static void populateDefaultSqlDataTypePolicies(Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies) {
		sqlDataTypePolicies.put(ColumnType.BLOB, new BlobPolicy());
		sqlDataTypePolicies.put(ColumnType.BOOLEAN, new BooleanPolicy());
		sqlDataTypePolicies.put(ColumnType.BOOLEAN_ARRAY, new BooleanArrayPolicy());
		sqlDataTypePolicies.put(ColumnType.CHARACTER, new CharacterPolicy());
		sqlDataTypePolicies.put(ColumnType.CLOB, new ClobPolicy());
		sqlDataTypePolicies.put(ColumnType.DATE, new DatePolicy());
		sqlDataTypePolicies.put(ColumnType.DECIMAL, new BigDecimalPolicy());
		sqlDataTypePolicies.put(ColumnType.DOUBLE, new DoublePolicy());
		sqlDataTypePolicies.put(ColumnType.DOUBLE_ARRAY, new DoubleArrayPolicy());
		sqlDataTypePolicies.put(ColumnType.FLOAT, new FloatPolicy());
		sqlDataTypePolicies.put(ColumnType.FLOAT_ARRAY, new FloatArrayPolicy());
		sqlDataTypePolicies.put(ColumnType.SHORT, new ShortPolicy());
		sqlDataTypePolicies.put(ColumnType.SHORT_ARRAY, new ShortArrayPolicy());
		sqlDataTypePolicies.put(ColumnType.INTEGER, new IntegerPolicy());
		sqlDataTypePolicies.put(ColumnType.INTEGER_ARRAY, new IntegerArrayPolicy());
		sqlDataTypePolicies.put(ColumnType.LONG, new LongPolicy());
		sqlDataTypePolicies.put(ColumnType.LONG_ARRAY, new LongArrayPolicy());
		sqlDataTypePolicies.put(ColumnType.STRING, new StringPolicy());
		sqlDataTypePolicies.put(ColumnType.STRING_ARRAY, new StringArrayPolicy());
		sqlDataTypePolicies.put(ColumnType.TIMESTAMP_UTC, new TimestampUTCPolicy());
		sqlDataTypePolicies.put(ColumnType.TIMESTAMP, new TimestampPolicy());
		sqlDataTypePolicies.put(ColumnType.ENUMCONST, new EnumConstPolicy());
	}

	protected static void populateDefaultSqlCriteriaPolicies(SqlDataSourceDialectPolicies rootPolicies,
			Map<RestrictionType, SqlCriteriaPolicy> sqlCriteriaPolicies) {
		sqlCriteriaPolicies.put(RestrictionType.EQUALS, new EqualPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.IEQUALS, new IEqualPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.NOT_EQUALS, new NotEqualsPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.INOT_EQUALS, new INotEqualsPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.LESS_THAN, new LessPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.LESS_OR_EQUAL, new LessOrEqualPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.GREATER, new GreaterPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.GREATER_OR_EQUAL, new GreaterOrEqualPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.BETWEEN, new BetweenPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.NOT_BETWEEN, new NotBetweenPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.AMONGST, new AmongstPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.NOT_AMONGST, new NotAmongstPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.LIKE, new LikePolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.ILIKE, new ILikePolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.NOT_LIKE, new NotLikePolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.BEGINS_WITH, new LikeBeginPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.IBEGINS_WITH, new ILikeBeginPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.NOT_BEGIN_WITH, new NotLikeBeginPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.ENDS_WITH, new LikeEndPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.IENDS_WITH, new ILikeEndPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.NOT_END_WITH, new NotLikeEndPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.IS_NULL, new IsNullPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.IS_NOT_NULL, new IsNotNullPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.AND, new AndPolicy(rootPolicies));
		sqlCriteriaPolicies.put(RestrictionType.OR, new OrPolicy(rootPolicies));
	}

	protected abstract SqlDataSourceDialectPolicies getSqlDataSourceDialectPolicies();

	protected boolean includeNoPrecisionType(String sqlType) {
		return noPrecisionTypes.add(sqlType);
	}

	protected List<String> getDataSourceInitStatements() {
		return Collections.emptyList();
	}

	protected void appendCreateTableColumnSql(StringBuilder sb, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			boolean onAlter) {
		sb.append(sqlFieldSchemaInfo.getPreferredColumnName());
		SqlDataTypePolicy sqlDataTypePolicy = getSqlDataSourceDialectPolicies()
				.getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType(), sqlFieldSchemaInfo.getLength());
		sqlDataTypePolicy.appendTypeSql(sb, sqlFieldSchemaInfo.getLength(), sqlFieldSchemaInfo.getPrecision(),
				sqlFieldSchemaInfo.getScale());

		if (sqlFieldSchemaInfo.isPrimaryKey()) {
			sb.append(" PRIMARY KEY NOT NULL");
		} else if (sqlFieldSchemaInfo.isNullable()) {
			if (appendNullOnTblCreate) {
				sb.append(" NULL");
			}
		} else {
			sb.append(" NOT NULL");
		}

		if ((onAlter && !sqlFieldSchemaInfo.isNullable()) || sqlFieldSchemaInfo.isWithDefaultVal()) {
			sqlDataTypePolicy.appendDefaultSql(sb, sqlFieldSchemaInfo.getFieldType(),
					sqlFieldSchemaInfo.getDefaultVal());
		}
	}

	/**
	 * Appends native aggregate function call SQL to string buffer.
	 * 
	 * @param sb            the string buffer
	 * @param aggregateType the aggregate type
	 * @param funcParam     the function parameter
	 * @param distinct      indicates aggregate on distinct values
	 * @throws UnifyException if an error occurs
	 */
	protected void appendAggregateFunctionSql(StringBuilder sb, AggregateType aggregateType, String funcParam,
			boolean distinct) throws UnifyException {
		switch (aggregateType) {
		case AVERAGE:
			sb.append("AVG(");
			break;
		case MAXIMUM:
			sb.append("MAX(");
			break;
		case MINIMUM:
			sb.append("MIN(");
			break;
		case SUM:
			sb.append("SUM(");
			break;
		case COUNT:
		default:
			sb.append("COUNT(");
			break;
		}

		if (distinct) {
			sb.append("DISTINCT ");
		}
		sb.append(funcParam).append(')');
	}

	/**
	 * Appends WHERE clause to a string buffer.
	 * 
	 * @param sql           the string buffer
	 * @param sqlEntityInfo the record info
	 * @param query         the query
	 * @param queryType     the query type
	 * @return a true value if clause was appended
	 * @throws UnifyException if an error occurs
	 */
	protected boolean appendWhereClause(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query,
			SqlQueryType queryType) throws UnifyException {
		boolean isAppend = false;
		final Restriction restriction = resolveRestriction(sqlEntityInfo, query);
		if (!restriction.isEmpty()) {
			sql.append(" WHERE ");
			translateCriteria(sql, sqlEntityInfo, restriction);
			if (query.isMinMax()) {
				sql.append(" AND ");
				StringBuilder critSql = new StringBuilder();
				translateCriteria(critSql, sqlEntityInfo, restriction);
				appendMinMax(sql, sqlEntityInfo, query, critSql);
			}
			isAppend = true;
		} else {
			if (query.isMinMax()) {
				sql.append(" WHERE ");
				appendMinMax(sql, sqlEntityInfo, query, null);
			} else {
				if (!query.isIgnoreEmptyCriteria()) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_CRITERIA_REQ_FOR_STATEMENT);
				}
			}
		}

		if (queryType.includeLimit()) {
			isAppend |= appendWhereLimitOffsetSuffixClause(sql, query.getOffset(), getQueryLimit(query), isAppend);
		}

		boolean orderAppended = false;
		if (queryType.includeOrder() && query.isOrder()) {
			isAppend |= (orderAppended = appendOrderClause(sql, sqlEntityInfo, query));
		}

		if (queryType.includeLimit()) {
			if (!orderAppended && query.isPagination()) {
				isAppend |= appendPseudoOrderClause(sql);
			}

			isAppend |= appendLimitOffsetSuffixClause(sql, query.getOffset(), getQueryLimit(query), isAppend);
		}

		return isAppend;
	}

	/**
	 * Appends WHERE clause to a string buffer.
	 * 
	 * @param sql               the string buffer
	 * @param parameterInfoList the parameter information list
	 * @param sqlEntityInfo     the SQl record information
	 * @param query             the query
	 * @param queryType         the query type
	 * @return true if an clause was appended otherwise false
	 * @throws UnifyException if an error occurs
	 */
	protected final boolean appendWhereClause(StringBuilder sql, List<SqlParameter> parameterInfoList,
			SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query, SqlQueryType queryType) throws UnifyException {
		return internalAppendWhereClause(sql, parameterInfoList, sqlEntityInfo, query, queryType, null);
	}

	/**
	 * Appends a timestamp column truncation function.
	 * 
	 * @param sql            the builder
	 * @param sqlFieldInfo   the timestamp field
	 * @param timeSeriesType the time series type
	 * @param merge          time merge
	 * @throws UnifyException if an error occurs
	 */
	protected abstract void appendTimestampTruncation(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException;

	/**
	 * Appends a timestamp column truncation froup by.
	 * 
	 * @param sql            the builder
	 * @param sqlFieldInfo   the timestamp field
	 * @param timeSeriesType the time series type
	 * @throws UnifyException if an error occurs
	 */
	protected abstract void appendTimestampTruncationGroupBy(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException;

	/**
	 * Appends ORDER clause to a string buffer.
	 * 
	 * @param sql           the buffer to write to
	 * @param sqlEntityInfo the record information
	 * @param query         the query
	 * @return a true value if order clause was appended
	 * @throws UnifyException
	 */
	protected boolean appendOrderClause(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query)
			throws UnifyException {
		if (query.isOrder()) {
			sql.append(" ORDER BY ");
			boolean appendSym = false;
			for (Order.Part part : query.getOrder().getParts()) {
				if (appendSym) {
					sql.append(',');
				} else {
					appendSym = true;
				}

				sql.append(sqlEntityInfo.getListFieldInfo(part.getField()).getPreferredColumnName()).append(' ')
						.append(part.getType().code());
			}

			return true;
		}
		return false;
	}

	/**
	 * Appends pseudo ORDER clause to a string buffer.
	 * 
	 * @param sql           the buffer to write to
	 * @param sqlEntityInfo the record information
	 * @param query         the query
	 * @return a true value if order clause was appended
	 * @throws UnifyException
	 */
	protected boolean appendPseudoOrderClause(StringBuilder sql) throws UnifyException {
		return false;
	}

	/**
	 * Appends LIMIT and OFFSET infix clause to supplied string builder using limit
	 * and offset information in supplied criteria.
	 * 
	 * @param sql    the builder to append to
	 * @param offset the offset
	 * @param limit  the limit
	 * @return a true value if any clause was appended otherwise false
	 * @throws UnifyException if an error occurs
	 */
	protected abstract boolean appendLimitOffsetInfixClause(StringBuilder sql, int offset, int limit)
			throws UnifyException;

	/**
	 * Appends a where LIMIT and OFFSET suffix clause to supplied string builder
	 * using limit and offset information in supplied criteria.
	 * 
	 * @param sql    the builder to append to
	 * @param offset the offset
	 * @param limit  the limit
	 * @param append indicates append mode
	 * @return a true value if any clause was appended otherwise false
	 * @throws UnifyException if an error occurs
	 */
	protected abstract boolean appendWhereLimitOffsetSuffixClause(StringBuilder sql, int offset, int limit,
			boolean append) throws UnifyException;

	/**
	 * Appends LIMIT and OFFSET suffix clause to supplied string builder using limit
	 * and offset information in supplied criteria.
	 * 
	 * @param sql    the builder to append to
	 * @param offset the offset
	 * @param limit  the limit
	 * @param append indicates append mode
	 * @return a true value if any clause was appended otherwise false
	 * @throws UnifyException if an error occurs
	 */
	protected abstract boolean appendLimitOffsetSuffixClause(StringBuilder sql, int offset, int limit, boolean append)
			throws UnifyException;

	/**
	 * Translate name to table name equivalent. Used for automatic generation of
	 * table and column names. This implementation converts all lower-case
	 * characters in name to upper-case and inserts an underscore at a mid lower to
	 * upper-case character boundary. For example age - AGE sQLName - SQLNAME
	 * sortCode - SORT_CODE. Override to change behavior.
	 * 
	 * @param name the name to convert
	 * @return String the converted name
	 */
	protected String generateTableName(String name) {
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

	protected void appendColumnAndTypeSql(StringBuilder sb, String preferredColumnName,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, boolean alter) throws UnifyException {
		SqlDataTypePolicy sqlDataTypePolicy = getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType(),
				sqlFieldSchemaInfo.getLength());
		sb.append(preferredColumnName);
		sqlDataTypePolicy.appendTypeSql(sb, sqlFieldSchemaInfo.getLength(), sqlFieldSchemaInfo.getPrecision(),
				sqlFieldSchemaInfo.getScale());

		if (sqlFieldSchemaInfo.isPrimaryKey()) {
			sb.append(" PRIMARY KEY NOT NULL");
		} else {
			if (!sqlFieldSchemaInfo.isNullable()) {
				if (alter || sqlFieldSchemaInfo.isWithDefaultVal()) {
					sqlDataTypePolicy.appendDefaultSql(sb, sqlFieldSchemaInfo.getFieldType(),
							sqlFieldSchemaInfo.getDefaultVal());
				}
				sb.append(" NOT NULL");
			} else {
				if (sqlFieldSchemaInfo.isWithDefaultVal()) {
					sqlDataTypePolicy.appendDefaultSql(sb, sqlFieldSchemaInfo.getFieldType(),
							sqlFieldSchemaInfo.getDefaultVal());
				}

				sb.append(" NULL");
			}
		}
	}

	protected void appendColumnAndTypeSql(StringBuilder sb, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			SqlColumnAlterInfo sqlColumnAlterInfo) throws UnifyException {
		SqlDataTypePolicy sqlDataTypePolicy = getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType(),
				sqlFieldSchemaInfo.getLength());
		sb.append(sqlFieldSchemaInfo.getPreferredColumnName());
		sqlDataTypePolicy.appendTypeSql(sb, sqlFieldSchemaInfo.getLength(), sqlFieldSchemaInfo.getPrecision(),
				sqlFieldSchemaInfo.getScale());

		if (sqlFieldSchemaInfo.isPrimaryKey()) {
			sb.append(" PRIMARY KEY NOT NULL");
		} else {
			if (sqlColumnAlterInfo.isDefaultChange()) {
				if (sqlFieldSchemaInfo.isWithDefaultVal()) {
					sqlDataTypePolicy.appendDefaultSql(sb, sqlFieldSchemaInfo.getFieldType(),
							sqlFieldSchemaInfo.getDefaultVal());
				}
			}

			if (sqlColumnAlterInfo.isNullableChange()) {
				if (!sqlFieldSchemaInfo.isNullable()) {
					sb.append(" NOT NULL");
				} else {
					sb.append(" NULL");
				}
			}
		}
	}

	protected void appendTypeSql(StringBuilder sb, SqlColumnInfo sqlColumnInfo) {
		String typeName = sqlColumnInfo.getTypeName().toUpperCase();
		sb.append(' ').append(typeName);
		if (sqlColumnInfo.getSize() > 0 && !noPrecisionTypes.contains(typeName)) {
			sb.append('(').append(sqlColumnInfo.getSize());
			if (sqlColumnInfo.getDecimalDigits() > 0) {
				sb.append(',').append(sqlColumnInfo.getDecimalDigits());
			}
			sb.append(')');
		}
	}

	protected SqlEntityInfo resolveSqlEntityInfo(Query<? extends Entity> query) throws UnifyException {
		return resolveSqlEntityInfo(SqlUtils.getEntityClass(query));
	}

	protected SqlEntityInfo resolveSqlEntityInfo(Class<?> clazz) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = sqlEntityInfoFactory.findSqlEntityInfo(clazz);
		if (sqlEntityInfo.isExtended()) {
			return sqlEntityInfo.getExtensionSqlEntityInfo();
		}

		return sqlEntityInfo;
	}

	protected String getTerminationSql() {
		return terminationSql;
	}

	protected void setTerminationSql(String terminationSql) {
		this.terminationSql = terminationSql;
	}

	protected String getNewLineSql() {
		return newLineSql;
	}

	protected void setNewLineSql(String newLineSql) {
		this.newLineSql = newLineSql;
	}

	/**
	 * Returns criteria result rows limit..
	 * 
	 * @param query the supplied record criteria object
	 * @return returns the recordCrityeria limit if set otherwise returns
	 *         container's global criteria limit if
	 *         {@link Query#isApplyAppQueryLimit()} is not set, otherwise returns
	 *         zero.
	 * @throws UnifyException if an error occurs
	 */
	protected int getQueryLimit(Query<? extends Entity> query) throws UnifyException {
		if (query.isLimit()) {
			return query.getLimit();
		}

		if (query.isApplyAppQueryLimit()) {
			return getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_QUERY_LIMIT,
					UnifyCoreConstants.DEFAULT_APPLICATION_QUERY_LIMIT);
		}

		return 0;
	}

	protected abstract List<String> doGenerateAlterColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, SqlColumnAlterInfo sqlColumnAlterInfo, PrintFormat format)
			throws UnifyException;

	private SqlStatement internalPrepareAggregateStatement(AggregateFunction aggregateFunction,
			Query<? extends Entity> query, List<GroupingFunction> groupingFunction) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		final List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		final List<SqlFieldInfo> returnFieldInfoList = new ArrayList<SqlFieldInfo>();

		StringBuilder aggregateSql = new StringBuilder();
		aggregateSql.append("SELECT ");
		SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(aggregateFunction.getFieldName());
		if (!aggregateFunction.getType().supports(ConverterUtils.getWrapperClass(sqlFieldInfo.getFieldType()))) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_SELECT_NOT_SUITABLE_FOR_AGGREGATE,
					aggregateFunction.getFieldName(), sqlEntityInfo.getKeyClass());
		}

		appendAggregateFunctionSql(aggregateSql, aggregateFunction.getType(), sqlFieldInfo.getPreferredColumnName(),
				query.isDistinct());
		returnFieldInfoList.add(sqlFieldInfo);

		addGroupingSelect(sqlEntityInfo, groupingFunction, aggregateSql, returnFieldInfoList, query.isMerge());

		aggregateSql.append(" FROM ").append(sqlEntityInfo.getSchemaViewName());

		internalAppendWhereClause(aggregateSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT,
				groupingFunction);

		SqlStatement statement = new SqlStatement(sqlEntityInfo, SqlStatementType.FIND, aggregateSql.toString(),
				parameterInfoList, getSqlResultList(returnFieldInfoList));
		statement.setMerge(query.isMerge());
		return statement;
	}

	private SqlStatement internalPrepareAggregateStatement(List<AggregateFunction> aggregateFunctionList,
			Query<? extends Entity> query, List<GroupingFunction> groupingFunction) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
		List<SqlFieldInfo> returnFieldInfoList = null;

		final boolean distinct = query.isDistinct();
		returnFieldInfoList = new ArrayList<SqlFieldInfo>();
		StringBuilder aggregateSql = new StringBuilder();
		aggregateSql.append("SELECT ");
		boolean appendSym = false;
		for (AggregateFunction aggregateFunction : aggregateFunctionList) {
			SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(aggregateFunction.getFieldName());
			if (!aggregateFunction.getType().supports(ConverterUtils.getWrapperClass(sqlFieldInfo.getFieldType()))) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_SELECT_NOT_SUITABLE_FOR_AGGREGATE,
						aggregateFunction.getFieldName(), sqlEntityInfo.getKeyClass());
			}

			if (appendSym) {
				aggregateSql.append(", ");
			} else {
				appendSym = true;
			}

			appendAggregateFunctionSql(aggregateSql, aggregateFunction.getType(), sqlFieldInfo.getPreferredColumnName(),
					distinct);
			returnFieldInfoList.add(sqlFieldInfo);
		}

		addGroupingSelect(sqlEntityInfo, groupingFunction, aggregateSql, returnFieldInfoList, query.isMerge());

		aggregateSql.append(" FROM ").append(sqlEntityInfo.getSchemaViewName());

		internalAppendWhereClause(aggregateSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT,
				groupingFunction);

		SqlStatement statement = new SqlStatement(sqlEntityInfo, SqlStatementType.FIND, aggregateSql.toString(),
				parameterInfoList, getSqlResultList(returnFieldInfoList));
		statement.setMerge(query.isMerge());
		return statement;
	}

	private void addGroupingSelect(SqlEntityInfo sqlEntityInfo, List<GroupingFunction> groupingFunction,
			StringBuilder aggregateSql, List<SqlFieldInfo> returnFieldInfoList, boolean merge) throws UnifyException {
		if (!DataUtils.isBlank(groupingFunction)) {
			for (GroupingFunction _groupingFunction : groupingFunction) {
				if (_groupingFunction.isWithFieldGrouping()) {
					SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(_groupingFunction.getFieldName());
					if (!String.class.equals(sqlFieldInfo.getFieldType())
							&& !EnumConst.class.isAssignableFrom(sqlFieldInfo.getFieldType())) {
						throw new UnifyException(UnifyCoreErrorConstants.RECORD_FIELD_NOT_SUITABLE_FOR_GROUPING,
								_groupingFunction.getFieldName(), sqlEntityInfo.getKeyClass());
					}

					aggregateSql.append(", ").append(sqlFieldInfo.getPreferredColumnName());
					returnFieldInfoList.add(sqlFieldInfo);
				} else {
					SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(_groupingFunction.getFieldName());
					if (!Date.class.equals(sqlFieldInfo.getFieldType())) {
						throw new UnifyException(UnifyCoreErrorConstants.RECORD_FIELD_NOT_SUITABLE_FOR_DATE_GROUPING,
								_groupingFunction.getFieldName(), sqlEntityInfo.getKeyClass());
					}

					aggregateSql.append(", ");
					appendTimestampTruncation(aggregateSql, sqlFieldInfo, _groupingFunction.getDateSeriesType(), merge);
					aggregateSql.append(" AS ").append(TRUNC_COLUMN_ALIAS);
					returnFieldInfoList.add(sqlFieldInfo);
				}
			}
		}
	}

	private boolean internalAppendWhereClause(StringBuilder sql, List<SqlParameter> parameterInfoList,
			SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query, SqlQueryType queryType,
			List<GroupingFunction> groupingFunction) throws UnifyException {
		boolean isAppend = false;

		int limit = getQueryLimit(query);
		boolean isCritLimOffset = queryType.isUpdate() && (limit > 0 || query.isOffset());
		if (isCritLimOffset) {
			sql.append(" WHERE ").append(sqlEntityInfo.getIdFieldInfo().getPreferredColumnName()).append(" IN (SELECT ")
					.append(sqlEntityInfo.getIdFieldInfo().getPreferredColumnName()).append(" FROM ")
					.append(sqlEntityInfo.getSchemaViewName());
		}

		final Restriction restriction = resolveRestriction(sqlEntityInfo, query);
		if (!restriction.isEmpty()) {
			SqlCriteriaPolicy sqlCriteriaPolicy = getSqlCriteriaPolicy(
					restriction.getConditionType().restrictionType());
			StringBuilder critSql = new StringBuilder();
			sqlCriteriaPolicy.generatePreparedStatementCriteria(critSql, parameterInfoList, sqlEntityInfo, restriction);
			sql.append(" WHERE ");
			sql.append(critSql);
			if (query.isMinMax()) {
				sql.append(" AND ");
				critSql = new StringBuilder();
				sqlCriteriaPolicy.generatePreparedStatementCriteria(critSql, parameterInfoList, sqlEntityInfo,
						restriction);
				appendMinMax(sql, sqlEntityInfo, query, critSql);
			}
			isAppend = true;
		} else {
			if (query.isMinMax()) {
				sql.append(" WHERE ");
				appendMinMax(sql, sqlEntityInfo, query, null);
				isAppend = true;
			} else {
				if (!query.isIgnoreEmptyCriteria()) {
					throw new UnifyException(UnifyCoreErrorConstants.RECORD_CRITERIA_REQ_FOR_STATEMENT);
				}
			}
		}

		if (!DataUtils.isBlank(groupingFunction)) {
			sql.append(" GROUP BY ");
			boolean appendSym = false;
			for (GroupingFunction _groupingFunction : groupingFunction) {
				if (appendSym) {
					sql.append(", ");
				} else {
					appendSym = true;
				}

				if (_groupingFunction.isWithFieldGrouping()) {
					sql.append(
							sqlEntityInfo.getListFieldInfo(_groupingFunction.getFieldName()).getPreferredColumnName());
				} else {
					appendTimestampTruncationGroupBy(sql,
							sqlEntityInfo.getListFieldInfo(_groupingFunction.getFieldName()),
							_groupingFunction.getDateSeriesType(), query.isMerge());
				}
			}

			sql.append(" ORDER BY ");
			appendSym = false;
			for (GroupingFunction _groupingFunction : groupingFunction) {
				if (appendSym) {
					sql.append(", ");
				} else {
					appendSym = true;
				}

				if (_groupingFunction.isWithFieldGrouping()) {
					sql.append(
							sqlEntityInfo.getListFieldInfo(_groupingFunction.getFieldName()).getPreferredColumnName());
				} else {
					sql.append(TRUNC_COLUMN_ALIAS);
				}
			}

			isAppend = true;
		} else {
			if (query.isGroupBy()) {
				sql.append(" GROUP BY ");
				boolean appendSym = false;
				for (String grpFieldName : query.getGroupBy().values()) {
					if (appendSym) {
						sql.append(", ");
					} else {
						appendSym = true;
					}

					sql.append(sqlEntityInfo.getListFieldInfo(grpFieldName).getPreferredColumnName());
				}
			}

			if (queryType.includeLimit()) {
				isAppend |= appendWhereLimitOffsetSuffixClause(sql, query.getOffset(), limit, isAppend);
			}

			if (queryType.includeOrder() && query.isOrder()) {
				if (!(queryType.isUpdate() && !(query.isLimit() || query.isOffset()))) {
					isAppend |= appendOrderClause(sql, sqlEntityInfo, query);
				}
			}

			if (queryType.includeLimit()) {
				isAppend |= appendLimitOffsetSuffixClause(sql, query.getOffset(), limit, isAppend);
			}

			if (isCritLimOffset) {
				sql.append(")");
			}
		}

		return isAppend;
	}

	private Restriction resolveRestriction(SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query)
			throws UnifyException {
		Restriction restriction = query.getRestrictions();
		if (sqlEntityInfo.isWithDefaultRestrictions() && !query.isInclusiveRestrictedField("id")) {
			And defRestriction = null;
			for (SqlQueryRestrictionInfo sqlQueryRestrictionInfo : sqlEntityInfo.getDefaultRestrictionList()) {
				if (!query.isRestrictedField(sqlQueryRestrictionInfo.getField())) {
					if (defRestriction == null) {
						defRestriction = restriction.isEmpty() ? new And() : (And) new And().add(restriction);
					}

					defRestriction.add(sqlQueryRestrictionInfo.getRestriction());
				}
			}

			if (defRestriction != null) {
				restriction = defRestriction;
			}
		}

		if (tenancyEnabled && sqlEntityInfo.isWithTenantId() && !query.isIgnoreTenancy()) {
			final String tenantIdFieldName = sqlEntityInfo.getTenantIdFieldInfo().getName();
			if (!query.isRestrictedField(tenantIdFieldName)) {
				if (restriction.isEmpty()) {
					restriction = new Equals(tenantIdFieldName, getUserTenantId());
				} else if (!restriction.isIdEqualsRestricted()) {
					restriction = new And().add(restriction).add(new Equals(tenantIdFieldName, getUserTenantId()));
				}
			}
		}

		return restriction;
	}

	private void appendMinMax(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query,
			StringBuilder critSql) throws UnifyException {
		sql.append('(');
		if (query.isMin()) {
			String minColumn = sqlEntityInfo.getListFieldInfo(query.getMinProperty()).getPreferredColumnName();
			sql.append(minColumn).append(" = (SELECT MIN(").append(minColumn);
		} else {
			String maxColumn = sqlEntityInfo.getListFieldInfo(query.getMaxProperty()).getPreferredColumnName();
			sql.append(maxColumn).append(" = (SELECT MAX(").append(maxColumn);
		}

		sql.append(") FROM ").append(sqlEntityInfo.getSchemaViewName());
		if (critSql != null) {
			sql.append(" WHERE ").append(critSql);
		}
		sql.append("))");
	}

	private boolean appendPreferredColumn(StringBuilder sql, SqlFieldInfo sqlFieldInfo, boolean appendSym)
			throws UnifyException {
		if (appendSym) {
			sql.append(", ");
		} else {
			appendSym = true;
		}

		sql.append(sqlFieldInfo.getPreferredColumnName());
		return true;
	}

	private String generateCreateViewSqlForTableEntity(SqlEntitySchemaInfo sqlEntitySchemaInfo, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE VIEW ").append(sqlEntitySchemaInfo.getSchemaViewName()).append('(');
		if (format.isPretty()) {
			sb.append(newLineSql);
		}

		ViewAliasInfo viewAliasInfo = new ViewAliasInfo(sqlEntitySchemaInfo.getTableAlias());
		// 29/07/2021 Solve null on depth issue by separating map
		// Map<SqlFieldSchemaInfo, SqlJoinInfo> sqlJoinMap = new
		// LinkedHashMap<SqlFieldSchemaInfo, SqlJoinInfo>();
		LinkedHashMap<String, SqlJoinInfo> sqlJoinInfos = new LinkedHashMap<String, SqlJoinInfo>();
		Set<SqlFieldSchemaInfo> referenceFieldSet = new HashSet<SqlFieldSchemaInfo>();
		for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getManagedListFieldInfos()) {
			appendCreateViewSQLElements(sqlEntitySchemaInfo, sqlFieldInfo, viewAliasInfo, referenceFieldSet,
					sqlJoinInfos);
		}

		boolean appendSym = false;
		StringBuilder fsb = new StringBuilder();
		StringBuilder ssb = new StringBuilder();
		for (SqlPair sqlPair : viewAliasInfo.getPairs()) {
			if (appendSym) {
				fsb.append(',');
				ssb.append(',');

				if (format.isPretty()) {
					fsb.append(newLineSql);
					ssb.append(newLineSql);
				}
			} else {
				appendSym = true;
			}

			if (format.isPretty()) {
				fsb.append('\t');
				ssb.append('\t');
			}

			fsb.append(sqlPair.getAliasSql());
			ssb.append(sqlPair.getSelectSql());
		}

		sb.append(fsb.toString());
		if (format.isPretty()) {
			sb.append(newLineSql);
		}

		sb.append(") AS SELECT ");
		if (format.isPretty()) {
			sb.append(newLineSql);
		}
		sb.append(ssb.toString());

		if (format.isPretty()) {
			sb.append(newLineSql);
			sb.append("FROM ");
		} else {
			sb.append(" FROM ");
		}
		sb.append(sqlEntitySchemaInfo.getSchemaTableName()).append(' ').append(viewAliasInfo.getViewAlias());
		for (SqlJoinInfo sqlJoinInfo : sqlJoinInfos.values()) {
			if (format.isPretty()) {
				sb.append(newLineSql);
				sb.append('\t');
				sb.append("LEFT JOIN ");
			} else {
				sb.append(" LEFT JOIN ");
			}

			sb.append(sqlJoinInfo.getRightTable()).append(' ').append(sqlJoinInfo.getRightAlias());
			sb.append(" ON ");
			sb.append(sqlJoinInfo.getConditionSQL());
		}
		return sb.toString();
	}

	private String generateCreateViewSqlForViewEntity(SqlEntitySchemaInfo sqlEntitySchemaInfo, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE VIEW ").append(sqlEntitySchemaInfo.getSchemaViewName()).append('(');
		if (format.isPretty()) {
			sb.append(newLineSql);
		}

		boolean appendSym = false;
		StringBuilder fsb = new StringBuilder();
		StringBuilder ssb = new StringBuilder();
		for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getManagedListFieldInfos()) {
			if (appendSym) {
				fsb.append(", ");
				ssb.append(", ");

				if (format.isPretty()) {
					fsb.append(newLineSql);
					ssb.append(newLineSql);
				}
			} else {
				appendSym = true;
			}

			if (format.isPretty()) {
				fsb.append('\t');
				ssb.append('\t');
			}

			fsb.append(sqlFieldInfo.getPreferredColumnName());
			ssb.append(sqlFieldInfo.getForeignEntityPreferredAlias() + '.'
					+ sqlFieldInfo.getForeignFieldInfo().getPreferredColumnName());
		}

		sb.append(fsb.toString());
		if (format.isPretty()) {
			sb.append(newLineSql);
		}

		sb.append(") AS SELECT ");
		if (format.isPretty()) {
			sb.append(newLineSql);
		}
		sb.append(ssb.toString());

		if (format.isPretty()) {
			sb.append(newLineSql);
			sb.append("FROM ");
		} else {
			sb.append(" FROM ");
		}

		appendSym = false;
		for (Map.Entry<String, Class<?>> entry : sqlEntitySchemaInfo.getViewBaseTables().entrySet()) {
			if (appendSym) {
				sb.append(", ");

				if (format.isPretty()) {
					sb.append(newLineSql);
				}
			} else {
				appendSym = true;
			}

			if (format.isPretty()) {
				sb.append('\t');
			}

			sb.append(findSqlEntityInfo(entry.getValue()).getPreferredTableName()).append(' ').append(entry.getKey());
		}

		if (sqlEntitySchemaInfo.isViewRestriction()) {
			if (format.isPretty()) {
				sb.append(newLineSql);
				sb.append("WHERE ");
			} else {
				sb.append(" WHERE ");
			}

			appendSym = false;
			for (SqlViewRestrictionInfo sqlViewRestrictionInfo : sqlEntitySchemaInfo.getViewRestrictionList()) {
				if (appendSym) {
					if (format.isPretty()) {
						sb.append(newLineSql);
						sb.append('\t');
					}
					sb.append(" AND ");
				} else {
					appendSym = true;
				}

				getSqlDataSourceDialectPolicies().getSqlCriteriaPolicy(sqlViewRestrictionInfo.getRestrictionType())
						.translate(sb, sqlViewRestrictionInfo.getTableAlias(), sqlViewRestrictionInfo.getColumnName(),
								sqlViewRestrictionInfo.getParam1(), sqlViewRestrictionInfo.getParam2());
			}
		}

		return sb.toString();
	}

	private class SqlStatementPoolsFactory extends FactoryMap<Class<?>, SqlStatementPools> {
		@Override
		protected SqlStatementPools create(Class<?> clazz, Object... params) throws Exception {
			return new SqlStatementPools(sqlEntityInfoFactory.findSqlEntityInfo(clazz),
					getSqlDataSourceDialectPolicies().getSqlDataTypePolicies(), sqlCacheFactory.get(clazz),
					getStatementInfoTimeout, minStatementInfo, maxStatementInfo);
		}
	};

	private SqlColumnAlterInfo checkSqlColumnAltered(SqlFieldSchemaInfo sqlFieldSchemaInfo,
			SqlFieldSchemaInfo oldSqlFieldSchemaInfo) throws UnifyException {
		final boolean nullableChange = sqlFieldSchemaInfo.isNullable() != oldSqlFieldSchemaInfo.isNullable();
		final boolean defaultChange = false;
		final boolean typeChange = !getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType(), sqlFieldSchemaInfo.getLength())
				.getTypeName()
				.equals(getSqlTypePolicy(oldSqlFieldSchemaInfo.getColumnType(), sqlFieldSchemaInfo.getLength())
						.getTypeName());
		final boolean lenChange = sqlFieldSchemaInfo.getLength() != oldSqlFieldSchemaInfo.getLength()
				|| sqlFieldSchemaInfo.getPrecision() != oldSqlFieldSchemaInfo.getPrecision()
				|| sqlFieldSchemaInfo.getScale() != oldSqlFieldSchemaInfo.getScale();
		return new SqlColumnAlterInfo(nullableChange, defaultChange, typeChange, lenChange);
	}

	private boolean appendLimitOffsetInfixClause(StringBuilder sql, Query<? extends Entity> query)
			throws UnifyException {
		return appendLimitOffsetInfixClause(sql, query.getOffset(), getQueryLimit(query));
	}

	private String getTableName(NativeQuery query, String tableName) {
		if (query.getSchemaName() != null) {
			return query.getSchemaName() + '.' + tableName;
		}

		return tableName;
	}

	@SuppressWarnings("unchecked")
	private String translateUpdateParams(SqlEntityInfo sqlEntityInfo, List<SqlParameter> parameterInfoList,
			Update update) throws UnifyException {
		if (update == null || update.isEmpty()) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_UPDATE_FIELD_REQ_FOR_STATEMENT);
		}

		StringBuilder updateParams = new StringBuilder();
		for (Map.Entry<String, Object> entry : update.entrySet()) {
			SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getFieldInfo(entry.getKey());
			if (sqlFieldInfo.isPrimaryKey()) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_CANT_UPDATE_PRIMARYKEY,
						sqlEntityInfo.getEntityClass(), entry.getKey());
			}
			if (sqlFieldInfo.isListOnly()) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_CANT_UPDATE_LISTONLY_FIELD,
						sqlEntityInfo.getEntityClass(), entry.getKey());
			}
			if (updateParams.length() > 0) {
				updateParams.append(',');
			}

			Object value = entry.getValue();
			if (value instanceof UpdateExpression) {
				UpdateExpression expression = (UpdateExpression) value;
				expression.appendSQLSetExpression(updateParams, sqlFieldInfo.getPreferredColumnName());
				value = expression.getValue();
			} else {
				updateParams.append(sqlFieldInfo.getPreferredColumnName()).append(" = ?");
			}

			if (sqlFieldInfo.isTransformed()) {
				value = ((Transformer<Object, Object>) sqlFieldInfo.getTransformer()).forwardTransform(value);
			}

			// Fix updates for enumerations that come different value type 01/07/19
			if (EnumConst.class.isAssignableFrom(sqlFieldInfo.getFieldType()) && value != null
					&& !value.getClass().equals(sqlFieldInfo.getFieldType())) {
				value = DataUtils.convert(sqlFieldInfo.getFieldType(), value);
			}
			// End fix

			parameterInfoList.add(new SqlParameter(getSqlDataSourceDialectPolicies()
					.getSqlTypePolicy(sqlFieldInfo.getColumnType(), sqlFieldInfo.getLength()), value));
		}

		return updateParams.toString();
	}

	private void translateCriteria(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction restriction)
			throws UnifyException {
		getSqlDataSourceDialectPolicies().getSqlCriteriaPolicy(restriction.getConditionType().restrictionType())
				.translate(sql, sqlEntityInfo, restriction);
	}

	private void appendCreateViewSQLElements(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldInfo,
			ViewAliasInfo viewAliasInfo, Set<SqlFieldSchemaInfo> referenceFieldSet,
			LinkedHashMap<String, SqlJoinInfo> sqlJoinInfos) {
		String column = sqlFieldInfo.getPreferredColumnName();
		String viewAlias = viewAliasInfo.getViewAlias();
		String aliasExt = "";
		ViewAliasInfo.FkChainViewAliasInfo fcvAliasInfo = null;
		// 29/07/2021 Solve null on depth issue by including depth variable
		int depth = 0;
		while (sqlFieldInfo.isListOnly()) {
			SqlFieldSchemaInfo fkSQLFieldInfo = sqlFieldInfo.getForeignKeyFieldInfo();
			if (fcvAliasInfo == null) {
				fcvAliasInfo = viewAliasInfo.getFkChainViewAliasInfo(fkSQLFieldInfo);
			}

			if (!referenceFieldSet.contains(fkSQLFieldInfo) || depth > 0) {
				StringBuilder csb = new StringBuilder();
				String tableAlias = fkSQLFieldInfo.getForeignEntityInfo().getTableAlias() + fkSQLFieldInfo.getName();

				viewAlias = fcvAliasInfo.add(tableAlias);
				csb.append(viewAlias).append('.').append(fkSQLFieldInfo.getForeignFieldInfo().getPreferredColumnName());
				csb.append(" = ");
				csb.append(fcvAliasInfo.getViewAlias(sqlEntitySchemaInfo.getTableAlias() + aliasExt)).append('.')
						.append(fkSQLFieldInfo.getPreferredColumnName());
				referenceFieldSet.add(fkSQLFieldInfo);
				if (!sqlJoinInfos.containsKey(viewAlias)) {
					sqlJoinInfos.put(viewAlias, new SqlJoinInfo(
							fkSQLFieldInfo.getForeignEntityInfo().getSchemaTableName(), viewAlias, csb.toString()));
				}
			}

			sqlEntitySchemaInfo = sqlFieldInfo.getForeignEntityInfo();
			aliasExt = fkSQLFieldInfo.getName();
			viewAlias = fcvAliasInfo.getViewAlias(sqlEntitySchemaInfo.getTableAlias() + aliasExt);
			sqlFieldInfo = sqlFieldInfo.getForeignFieldInfo();
			depth++;
		}

		viewAliasInfo.addPair(viewAlias + '.' + sqlFieldInfo.getPreferredColumnName(), column);
	}

	private class ViewAliasInfo {

		private String tableAlias;

		private Map<SqlFieldSchemaInfo, FkChainViewAliasInfo> fkChainViewAliasInfoMap;

		private List<SqlPair> sqlPairList;

		private int index;

		public ViewAliasInfo(String tableAlias) {
			this.tableAlias = tableAlias;
			fkChainViewAliasInfoMap = new HashMap<SqlFieldSchemaInfo, FkChainViewAliasInfo>();
			sqlPairList = new ArrayList<SqlPair>();
			index++;
		}

		public void addPair(String selectSql, String createSql) {
			sqlPairList.add(new SqlPair(selectSql, createSql));
		}

		public List<SqlPair> getPairs() {
			return sqlPairList;
		}

		public String getViewAlias() {
			return "T1";
		}

		public FkChainViewAliasInfo getFkChainViewAliasInfo(SqlFieldSchemaInfo fkSQLFieldInfo) {
			FkChainViewAliasInfo fkChainViewAliasInfo = fkChainViewAliasInfoMap.get(fkSQLFieldInfo);
			if (fkChainViewAliasInfo == null) {
				fkChainViewAliasInfo = new FkChainViewAliasInfo();
				fkChainViewAliasInfoMap.put(fkSQLFieldInfo, fkChainViewAliasInfo);
			}
			return fkChainViewAliasInfo;
		}

		public class FkChainViewAliasInfo {

			private Map<String, String> aliasMap;

			public FkChainViewAliasInfo() {
				aliasMap = new HashMap<String, String>();
				aliasMap.put(tableAlias, "T1");
			}

			public String add(String tableAlias) {
				if (!aliasMap.containsKey(tableAlias)) {
					aliasMap.put(tableAlias, "T" + (++index));
				}
				return aliasMap.get(tableAlias);
			}

			public String getViewAlias(String tableAlias) {
				return aliasMap.get(tableAlias);
			}
		}
	}

	private class SqlJoinInfo {

		private String rightTable;

		private String rightAlias;

		private String conditionSQL;

		public SqlJoinInfo(String rightTable, String rightAlias, String conditionSQL) {
			this.rightTable = rightTable;
			this.rightAlias = rightAlias;
			this.conditionSQL = conditionSQL;
		}

		public String getRightTable() {
			return rightTable;
		}

		public String getRightAlias() {
			return rightAlias;
		}

		public String getConditionSQL() {
			return conditionSQL;
		}
	}

	private class SqlCacheFactory extends FactoryMap<Class<?>, SqlCache> {
		@Override
		protected SqlCache create(Class<?> clazz, Object... params) throws Exception {
			SqlEntitySchemaInfo sqlEntitySchemaInfo = sqlEntityInfoFactory.findSqlEntityInfo(clazz);
			if (sqlEntitySchemaInfo.isViewOnly()) {
				return new SqlCache(generateListRecordSql(sqlEntitySchemaInfo),
						generateListRecordSql(sqlEntitySchemaInfo), generateListRecordByPkSql(sqlEntitySchemaInfo),
						generateListRecordByPkVersionSql(sqlEntitySchemaInfo),
						generateListRecordSql(sqlEntitySchemaInfo), generateListRecordByPkSql(sqlEntitySchemaInfo),
						generateListRecordByPkVersionSql(sqlEntitySchemaInfo), null, null, null, null, null, null, null,
						null, generateCountRecordSql(sqlEntitySchemaInfo, QueryAgainst.VIEW),
						generateCountRecordSql(sqlEntitySchemaInfo, QueryAgainst.VIEW), generateTestSql());
			}

			return new SqlCache(generateFindRecordSql(sqlEntitySchemaInfo, QueryAgainst.TABLE),
					generateFindRecordSql(sqlEntitySchemaInfo, QueryAgainst.VIEW),
					generateFindRecordByPkSql(sqlEntitySchemaInfo),
					generateFindRecordByPkVersionSql(sqlEntitySchemaInfo), generateListRecordSql(sqlEntitySchemaInfo),
					generateListRecordByPkSql(sqlEntitySchemaInfo),
					generateListRecordByPkVersionSql(sqlEntitySchemaInfo), generateInsertRecordSql(sqlEntitySchemaInfo),
					generateInsertUnmanagedIdentityRecordSql(sqlEntitySchemaInfo),
					generateUpdateRecordSql(sqlEntitySchemaInfo), generateUpdateRecordByPkSql(sqlEntitySchemaInfo),
					generateUpdateRecordByPkVersionSql(sqlEntitySchemaInfo),
					generateDeleteRecordSql(sqlEntitySchemaInfo), generateDeleteRecordByPkSql(sqlEntitySchemaInfo),
					generateDeleteRecordByPkVersionSql(sqlEntitySchemaInfo),
					generateCountRecordSql(sqlEntitySchemaInfo, QueryAgainst.TABLE),
					generateCountRecordSql(sqlEntitySchemaInfo, QueryAgainst.VIEW), generateTestSql());
		}
	};

	private List<SqlResult> getSqlResultList(List<SqlFieldInfo> sqlFieldInfoList) {
		List<SqlResult> resultInfoList = new ArrayList<SqlResult>();
		for (SqlFieldInfo sqlFieldInfo : sqlFieldInfoList) {
			resultInfoList.add(new SqlResult(getSqlDataSourceDialectPolicies()
					.getSqlTypePolicy(sqlFieldInfo.getColumnType(), sqlFieldInfo.getLength()), sqlFieldInfo));
		}
		return resultInfoList;
	}
}

class SqlPair {
	private String selectSql;

	private String aliasSql;

	public SqlPair(String selectSql, String aliasSql) {
		this.selectSql = selectSql;
		this.aliasSql = aliasSql;
	}

	public String getSelectSql() {
		return selectSql;
	}

	public String getAliasSql() {
		return aliasSql;
	}
}
