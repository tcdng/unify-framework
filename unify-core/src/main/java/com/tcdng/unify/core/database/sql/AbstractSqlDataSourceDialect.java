/*
 * Copyright 2018-2019 The Code Department.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyContainer;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.criterion.Order;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.criterion.Select;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.data.AggregateType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.CallableProc;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.sql.criterion.policy.AmongstPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.AndPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.BetweenPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.EqualPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.GreaterOrEqualPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.GreaterPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.IsNotNullPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.IsNullPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LessOrEqualPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LessPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LikeBeginPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LikeEndPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.LikePolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotAmongstPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotBetweenPolicy;
import com.tcdng.unify.core.database.sql.criterion.policy.NotEqualPolicy;
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
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Abstract SQL dialect implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractSqlDataSourceDialect extends AbstractUnifyComponent implements SqlDataSourceDialect {

    private static final String TIMESTAMP_FORMAT = "''yyyy-MM-dd HH:mm:ss''";

    @Configurable
    private SqlEntityInfoFactory sqlEntityInfoFactory;

    @Configurable("4000")
    private long getStatementInfoTimeout;

    @Configurable("1")
    private int minStatementInfo;

    @Configurable("64")
    private int maxStatementInfo;

    private DateFormat timestampFormat;

    private SqlCacheFactory sqlCacheFactory;

    private SqlStatementPoolsFactory sqlStatementPoolsFactory;

    private SqlCallableStatementPools sqlCallableStatementPools;

    private Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies;

    private Map<RestrictionType, SqlCriteriaPolicy> sqlCriteriaPolicies;

    private String terminationSql;

    private String newLineSql;

    private boolean useCallableFunctionMode;

    private boolean appendNullOnTblCreate;

    public AbstractSqlDataSourceDialect(boolean useCallableFunctionMode) {
        this(useCallableFunctionMode, false);
    }

    public AbstractSqlDataSourceDialect(boolean useCallableFunctionMode, boolean appendNullOnTblCreate) {
        this.useCallableFunctionMode = useCallableFunctionMode;
        this.appendNullOnTblCreate = appendNullOnTblCreate;
        timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        sqlCacheFactory = new SqlCacheFactory();
        sqlStatementPoolsFactory = new SqlStatementPoolsFactory();

        sqlDataTypePolicies = new HashMap<ColumnType, SqlDataTypePolicy>();
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

        sqlCriteriaPolicies = new HashMap<RestrictionType, SqlCriteriaPolicy>();
        sqlCriteriaPolicies.put(RestrictionType.EQUALS, new EqualPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.NOT_EQUAL, new NotEqualPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.LESS_THAN, new LessPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.LESS_OR_EQUAL, new LessOrEqualPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.GREATER, new GreaterPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.GREATER_OR_EQUAL, new GreaterOrEqualPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.BETWEEN, new BetweenPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.NOT_BETWEEN, new NotBetweenPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.AMONGST, new AmongstPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.NOT_AMONGST, new NotAmongstPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.LIKE, new LikePolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.NOT_LIKE, new NotLikePolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.BEGINS_WITH, new LikeBeginPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.NOT_BEGIN_WITH, new NotLikeBeginPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.ENDS_WITH, new LikeEndPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.NOT_END_WITH, new NotLikeEndPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.IS_NULL, new IsNullPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.IS_NOT_NULL, new IsNotNullPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.AND, new AndPolicy(this));
        sqlCriteriaPolicies.put(RestrictionType.OR, new OrPolicy(this));
    }

    @Override
    public String generateAllCreateSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, boolean foreignConstraints,
            boolean uniqueConstraints, boolean indexes, boolean views, boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append(generateCreateTableSql(sqlEntitySchemaInfo, true));
        sb.append(terminationSql);
        if (format) {
            sb.append(newLineSql);
            sb.append(newLineSql);
        }

        if (foreignConstraints && sqlEntitySchemaInfo.isForeignKeys()) {
            for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntitySchemaInfo.getForeignKeyList()) {
                sb.append(generateAddForeignKeyConstraintSql(sqlEntitySchemaInfo, sqlForeignKeyInfo, true));
                sb.append(terminationSql);
                if (format) {
                    sb.append(newLineSql);
                    sb.append(newLineSql);
                }
            }
        }

        if (uniqueConstraints && sqlEntitySchemaInfo.isUniqueConstraints()) {
            for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntitySchemaInfo.getUniqueConstraintList()
                    .values()) {
                sb.append(generateAddUniqueConstraintSql(sqlEntitySchemaInfo, sqlUniqueConstraintInfo, true));
                sb.append(terminationSql);
                if (format) {
                    sb.append(newLineSql);
                    sb.append(newLineSql);
                }
            }
        }

        if (indexes && sqlEntitySchemaInfo.isIndexes()) {
            for (SqlIndexSchemaInfo sqlIndexInfo : sqlEntitySchemaInfo.getIndexList().values()) {
                sb.append(generateCreateIndexSql(sqlEntitySchemaInfo, sqlIndexInfo, true));
                sb.append(terminationSql);
                if (format) {
                    sb.append(newLineSql);
                    sb.append(newLineSql);
                }
            }
        }

        if (views && sqlEntitySchemaInfo.isViewable()) {
            sb.append(generateCreateViewSql(sqlEntitySchemaInfo, true));
            sb.append(terminationSql);
            if (format) {
                sb.append(newLineSql);
                sb.append(newLineSql);
            }
        }

        List<Map<String, Object>> staticValueList = sqlEntitySchemaInfo.getStaticValueList();
        if (staticValueList != null) {
            String insertValuesSql = generateInsertValuesSql(sqlEntitySchemaInfo, staticValueList, true);
            sb.append(insertValuesSql);
        }
        return sb.toString();
    }

    @Override
    public String generateAllUpgradeSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlEntitySchemaInfo oldSqlEntitySchemaInfo, boolean foreignConstraints, boolean uniqueConstraints,
            boolean indexes, boolean views, boolean format) throws UnifyException {
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
                rsb.append(
                        generateRenameColumn(sqlEntitySchemaInfo, sqlFieldSchemaInfo, oldSqlFieldSchemaInfo, format));
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
    public String generateCreateTableSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, boolean format)
            throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName()).append('(');
        if (format) {
            sb.append(newLineSql);
        }
        boolean appendSym = false;
        for (SqlFieldSchemaInfo sqlFieldSchemaInfo : sqlEntitySchemaInfo.getFieldInfos()) {
            if (appendSym) {
                sb.append(',');
                if (format) {
                    sb.append(newLineSql);
                }
            } else {
                appendSym = true;
            }

            if (format) {
                sb.append('\t');
            }

            appendColumnAndTypeSql(sb, sqlFieldSchemaInfo, false);
        }

        if (format) {
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
            SqlEntitySchemaInfo oldSqlEntitySchemaInfo, boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("RENAME TABLE ").append(oldSqlEntitySchemaInfo.getSchemaTableName()).append(" TO ")
                .append(sqlEntitySchemaInfo.getSchemaTableName());
        return sb.toString();
    }

    @Override
    public String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(' ');
        }
        sb.append("ADD COLUMN ");
        appendColumnAndTypeSql(sb, sqlFieldSchemaInfo, true);
        return sb.toString();
    }

    @Override
    public String generateRenameColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            SqlFieldSchemaInfo oldSqlFieldSchemaInfo, boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(' ');
        }
        sb.append("RENAME COLUMN ").append(oldSqlFieldSchemaInfo.getPreferredColumnName()).append(" TO ")
                .append(sqlFieldSchemaInfo.getPreferredColumnName());
        return sb.toString();
    }

    @Override
    public String generateDropColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(' ');
        }
        sb.append("DROP ").append(sqlFieldSchemaInfo.getPreferredColumnName());
        return sb.toString();
    }

    @Override
    public String generateAddForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlForeignKeySchemaInfo sqlForeignKeyInfo, boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        String tableName = sqlEntitySchemaInfo.getSchemaTableName();
        SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getFieldInfo(sqlForeignKeyInfo.getFieldName());
        SqlEntitySchemaInfo foreignEntityInfo = sqlFieldInfo.getForeignEntityInfo();
        SqlFieldSchemaInfo foreignFieldInfo = sqlFieldInfo.getForeignFieldInfo();
        sb.append("ALTER TABLE ").append(tableName);
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(" ");
        }
        sb.append("ADD CONSTRAINT ").append(sqlFieldInfo.getConstraint());
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(" ");
        }
        sb.append("FOREIGN KEY (").append(sqlFieldInfo.getPreferredColumnName()).append(")");
        if (format) {
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
            SqlForeignKeySchemaInfo sqlForeignKeyInfo, boolean format) throws UnifyException {
        return generateDropForeignKeyConstraintSql(sqlEntitySchemaInfo,
                sqlEntitySchemaInfo.getFieldInfo(sqlForeignKeyInfo.getFieldName()).getConstraint(), format);
    }

    @Override
    public String generateDropForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbForeignKeyName,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        String tableName = sqlEntitySchemaInfo.getSchemaTableName();
        sb.append("ALTER TABLE ").append(tableName);
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(" ");
        }
        sb.append("DROP CONSTRAINT ")
                .append(SqlUtils.generateFullSchemaElementName(sqlEntitySchemaInfo.getSchema(), dbForeignKeyName));
        return sb.toString();
    }

    @Override
    public String generateAddUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        String tableName = sqlEntitySchemaInfo.getSchemaTableName();
        sb.append("ALTER TABLE ").append(tableName);
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(" ");
        }
        sb.append("ADD CONSTRAINT ").append(sqlUniqueConstraintInfo.getName());
        if (format) {
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
    public String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, boolean format) throws UnifyException {
        return generateDropUniqueConstraintSql(sqlEntitySchemaInfo, sqlUniqueConstraintInfo.getName(), format);
    }

    @Override
    public String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            String dbUniqueConstraintName, boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        String tableName = sqlEntitySchemaInfo.getSchemaTableName();
        sb.append("ALTER TABLE ").append(tableName);
        if (format) {
            sb.append(newLineSql);
        } else {
            sb.append(" ");
        }
        sb.append("DROP CONSTRAINT ").append(
                SqlUtils.generateFullSchemaElementName(sqlEntitySchemaInfo.getSchema(), dbUniqueConstraintName));
        return sb.toString();
    }

    @Override
    public String generateCreateIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        String tableName = sqlEntitySchemaInfo.getSchemaTableName();
        sb.append("CREATE");
        if (sqlIndexInfo.isUnique()) {
            sb.append(" UNIQUE");
        }
        sb.append(" INDEX ").append(sqlIndexInfo.getName());
        if (format) {
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
            boolean format) throws UnifyException {
        return generateDropIndexSql(sqlEntitySchemaInfo, sqlIndexInfo.getName(), format);
    }

    @Override
    public String generateDropIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbIndexName, boolean format)
            throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP INDEX ")
                .append(SqlUtils.generateFullSchemaElementName(sqlEntitySchemaInfo.getSchema(), dbIndexName));
        return sb.toString();
    }

    @Override
    public String generateCreateViewSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, boolean format) throws UnifyException {
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
    public String generateFindRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
        StringBuilder fsb = new StringBuilder();
        for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getFieldInfos()) {
            if (fsb.length() > 0) {
                fsb.append(", ");
            }
            fsb.append(sqlFieldInfo.getPreferredColumnName());
        }

        StringBuilder findSql = new StringBuilder();
        findSql.append("SELECT ").append(fsb).append(" FROM ").append(sqlEntitySchemaInfo.getSchemaTableName());
        return findSql.toString();
    }

    @Override
    public String generateFindRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
        StringBuilder findByPkSql = new StringBuilder();
        findByPkSql.append(generateFindRecordSql(sqlEntitySchemaInfo)).append(" WHERE ")
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
        StringBuilder fsb = new StringBuilder();
        for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getListFieldInfos()) {
            if (fsb.length() > 0) {
                fsb.append(", ");
            }
            fsb.append(sqlFieldInfo.getPreferredColumnName());
        }

        StringBuilder listSql = new StringBuilder();
        listSql.append("SELECT ").append(fsb).append(" FROM ").append(sqlEntitySchemaInfo.getSchemaViewName());
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
    public String generateInsertValuesSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, Map<String, Object> params,
            boolean formatSql) throws UnifyException {
        StringBuilder fsb = new StringBuilder();
        StringBuilder psb = new StringBuilder();
        for (String fieldName : sqlEntitySchemaInfo.getFieldNames()) {
            SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getFieldInfo(fieldName);
            if (fsb.length() > 0) {
                fsb.append(',');
                psb.append(',');
            }
            fsb.append(sqlFieldInfo.getPreferredColumnName());
            psb.append(translateValue(params.get(fieldName)));
        }

        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT INTO ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" (").append(fsb)
                .append(")");
        if (formatSql) {
            insertSql.append(newLineSql);
        } else {
            insertSql.append(" ");
        }
        insertSql.append("VALUES (").append(psb).append(")").toString();
        insertSql.append(terminationSql);
        if (formatSql) {
            insertSql.append(newLineSql);
            insertSql.append(newLineSql);
        }
        return insertSql.toString();
    }

    @Override
    public String generateInsertValuesSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            List<Map<String, Object>> insertValueList, boolean formatSql) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> values : insertValueList) {
            sb.append(generateInsertValuesSql(sqlEntitySchemaInfo, values, formatSql));
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
    public String generateCountRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException {
        StringBuffer countSql = new StringBuffer();
        countSql.append("SELECT COUNT(*) FROM ").append(sqlEntitySchemaInfo.getSchemaViewName());
        return countSql.toString();
    }

    @Override
    public String generateLikeParameter(SqlLikeType type, Object param) throws UnifyException {
        String paramStr = null;
        if (param instanceof SqlViewColumnInfo) {
            SqlViewColumnInfo sqlViewColumnInfo = (SqlViewColumnInfo) param;
            paramStr = sqlViewColumnInfo.getTableAlias() + "." + sqlViewColumnInfo.getColumnName();
        } else {
            paramStr = String.valueOf(param);
        }

        if (type.equals(SqlLikeType.BEGINS_WITH)) {
            return paramStr + "%";
        } else if (type.equals(SqlLikeType.ENDS_WITH)) {
            return "%" + paramStr;
        }

        return "%" + paramStr + "%";
    }

    @Override
    public int getMaxClauseValues() {
        return 0;
    }

    @Override
    public boolean isAllObjectsInLowerCase() throws UnifyException {
        return false;
    }

    @Override
    public SqlDataTypePolicy getSqlTypePolicy(Class<?> clazz) throws UnifyException {
        return sqlDataTypePolicies.get(DataUtils.getColumnType(clazz));
    }

    @Override
    public SqlDataTypePolicy getSqlTypePolicy(ColumnType columnType) throws UnifyException {
        return sqlDataTypePolicies.get(columnType);
    }

    @Override
    public SqlEntityInfo getSqlEntityInfo(Class<?> clazz) throws UnifyException {
        return sqlEntityInfoFactory.getSqlEntityInfo(clazz);
    }

    @Override
    public SqlCallableInfo getSqlCallableInfo(Class<? extends CallableProc> clazz) throws UnifyException {
        return sqlEntityInfoFactory.getSqlCallableInfo(clazz);
    }

    @Override
    public SqlCriteriaPolicy getSqlCriteriaPolicy(RestrictionType restrictionType) throws UnifyException {
        return sqlCriteriaPolicies.get(restrictionType);
    }

    @Override
    public SqlStatement prepareCountStatement(Query<? extends Entity> query) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);
        List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
        StringBuilder countSql = new StringBuilder(sqlCacheFactory.get(query.getEntityClass()).getCountSql());
        appendWhereClause(countSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);
        return new SqlStatement(sqlEntityInfo, SqlStatementType.COUNT, countSql.toString(), parameterInfoList);
    }

    @Override
    public SqlStatement prepareMinStatement(String columnName, Query<? extends Entity> query) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);
        List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
        StringBuilder minSql = new StringBuilder();
        minSql.append("SELECT MIN(").append(columnName).append(") FROM ").append(sqlEntityInfo.getSchemaViewName());
        appendWhereClause(minSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);
        return new SqlStatement(sqlEntityInfo, SqlStatementType.MIN, minSql.toString(), parameterInfoList);
    }

    @Override
    public SqlStatement prepareMaxStatement(String columnName, Query<? extends Entity> query) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);
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
    public SqlStatement prepareAggregateStatement(AggregateType aggregateType, Query<? extends Entity> query)
            throws UnifyException {
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);
        List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
        List<SqlFieldInfo> returnFieldInfoList = null;
        Select select = query.getSelect();
        if (select.isEmpty()) {
            throw new UnifyException(UnifyCoreErrorConstants.RECORD_NO_SELECT_FOR_AGGREGATE,
                    sqlEntityInfo.getKeyClass());
        }

        StringBuilder aggregateSql = new StringBuilder();
        aggregateSql.append("SELECT ");
        appendAggregateFunctionSql(aggregateSql, AggregateType.COUNT, "*", false);
        boolean distinct = query.isDistinct();
        returnFieldInfoList = new ArrayList<SqlFieldInfo>();
        for (String name : select.values()) {
            SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getFieldInfo(name);
            if (!aggregateType.supports(DataUtils.getWrapperClass(sqlFieldInfo.getFieldType()))) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_SELECT_NOT_SUITABLE_FOR_AGGREGATE, name,
                        sqlEntityInfo.getKeyClass());
            }
            aggregateSql.append(", ");
            appendAggregateFunctionSql(aggregateSql, aggregateType, sqlFieldInfo.getPreferredColumnName(), distinct);
            returnFieldInfoList.add(sqlFieldInfo);
        }

        // Select from view because criteria can contain view-only properties
        aggregateSql.append(" FROM ").append(sqlEntityInfo.getSchemaViewName());

        appendWhereClause(aggregateSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.SELECT);

        return new SqlStatement(sqlEntityInfo, SqlStatementType.FIND, aggregateSql.toString(), parameterInfoList,
                getSqlResultList(returnFieldInfoList));
    }

    @Override
    public SqlStatement prepareCreateStatement(Entity record) throws UnifyException {
        return sqlStatementPoolsFactory.get(SqlUtils.getEntityClass(record)).getSqlStatement(SqlStatementType.CREATE,
                record);
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
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);
        List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
        StringBuilder deleteSql = new StringBuilder(sqlCacheFactory.get(sqlEntityInfo.getKeyClass()).getDeleteSql());
        appendWhereClause(deleteSql, parameterInfoList, sqlEntityInfo, query, SqlQueryType.DELETE);
        return new SqlStatement(sqlEntityInfo, SqlStatementType.DELETE, deleteSql.toString(), parameterInfoList);
    }

    @Override
    public SqlStatement prepareFindByPkStatement(Class<?> clazz, Object pk) throws UnifyException {
        return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.FIND_BY_PK, pk);
    }

    @Override
    public SqlStatement prepareFindByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo)
            throws UnifyException {
        return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.FIND_BY_PK_VERSION, pk, versionNo);
    }

    @Override
    public SqlStatement prepareFindStatement(Query<? extends Entity> query) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);
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

            findSql.append(sqlCacheFactory.get(sqlEntityInfo.getKeyClass()).getFindSql());
        } else {
            findSql.append("SELECT ");
            if (select != null && select.isDistinct()) {
                findSql.append(" DISTINCT ");
            }

            appendLimitOffsetInfixClause(findSql, query);

            boolean appendSym = false;
            if (select != null && !select.isEmpty()) {
                returnFieldInfoList = new ArrayList<SqlFieldInfo>();
                if (sqlEntityInfo.isViewOnly()) {
                    for (String name : select.values()) {
                        if (!sqlEntityInfo.isChildFieldInfo(name)) {
                            SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(name);
                            if (appendSym) {
                                findSql.append(", ");
                            } else {
                                appendSym = true;
                            }
                            findSql.append(sqlFieldInfo.getPreferredColumnName());
                            returnFieldInfoList.add(sqlFieldInfo);
                        }
                    }
                } else {
                    for (String name : select.values()) {
                        if (!sqlEntityInfo.isChildFieldInfo(name)) {
                            SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getFieldInfo(name);
                            if (appendSym) {
                                findSql.append(", ");
                            } else {
                                appendSym = true;
                            }
                            findSql.append(sqlFieldInfo.getPreferredColumnName());
                            returnFieldInfoList.add(sqlFieldInfo);
                        }
                    }
                }

                // Select must always fetch primary keys because of child lists
                SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getIdFieldInfo();
                if (!select.contains(sqlFieldInfo.getName())) {
                    if (appendSym) {
                        findSql.append(", ");
                    } else {
                        appendSym = true;
                    }
                    findSql.append(sqlFieldInfo.getPreferredColumnName());
                    returnFieldInfoList.add(sqlFieldInfo);
                }
            } else {
                if (sqlEntityInfo.isViewOnly()) {
                    returnFieldInfoList = sqlEntityInfo.getListFieldInfos();
                } else {
                    returnFieldInfoList = sqlEntityInfo.getFieldInfos();
                }

                for (SqlFieldInfo sqlFieldInfo : returnFieldInfoList) {
                    if (appendSym) {
                        findSql.append(", ");
                    } else {
                        appendSym = true;
                    }
                    findSql.append(sqlFieldInfo.getPreferredColumnName());
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
        return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.LIST_BY_PK, pk);
    }

    @Override
    public SqlStatement prepareListByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo)
            throws UnifyException {
        return sqlStatementPoolsFactory.get(clazz).getSqlStatement(SqlStatementType.LIST_BY_PK_VERSION, pk, versionNo);
    }

    @Override
    public SqlStatement prepareListStatement(Query<? extends Entity> query) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);
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
                listSql.append(" DISTINCT ");
            }

            appendLimitOffsetInfixClause(listSql, query);

            boolean appendSym = false;
            if (!select.isEmpty()) {
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
    public String generateNativeQuery(Query<? extends Entity> query) throws UnifyException {
        Class<?> entityClass = SqlUtils.getEntityClass(query);
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(entityClass);
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
            sqlCriteriaPolicies.get(rootFilter.getOp()).translate(sql, null, null, aliasGenerator,
                    rootFilter.getSubFilterList());
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
        return getSqlEntityInfo(clazz).getListColumnsByFieldNames();
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
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(clazz);
        List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
        String updateParams = translateUpdateParams(sqlEntityInfo, parameterInfoList, update);

        StringBuilder updateSql = new StringBuilder();
        updateSql.append("UPDATE ").append(sqlEntityInfo.getSchemaTableName()).append(" SET ").append(updateParams);
        updateSql.append(" WHERE ");
        SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo("id");
        updateSql.append(sqlFieldInfo.getPreferredColumnName()).append("=?");
        parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()), pk));

        return new SqlStatement(sqlEntityInfo, SqlStatementType.UPDATE, updateSql.toString(), parameterInfoList);
    }

    @Override
    public SqlStatement prepareUpdateStatement(Query<? extends Entity> query, Update update) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = getSqlEntityInfo(query);

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
    public String translateValue(Object param) throws UnifyException {
        if (param instanceof String) {
            String paramStr = (String) param;
            if (paramStr.indexOf('.') > 0) {
                return paramStr;
            }

            return "\'" + param + "\'";
        }

        if (param instanceof Date) {
            return timestampFormat.format((Date) param);
        }

        if (param instanceof Boolean) {
            return "\'" + SqlUtils.getString((Boolean) param) + "\'";
        }

        return String.valueOf(param);
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
    protected void onInitialize() throws UnifyException {
        sqlEntityInfoFactory.setSqlDataSourceDialect(this);
        sqlCallableStatementPools = new SqlCallableStatementPools(sqlDataTypePolicies, getStatementInfoTimeout,
                minStatementInfo, maxStatementInfo, useCallableFunctionMode);
        terminationSql = ";";
        newLineSql = getLineSeparator();
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected void setDataTypePolicy(ColumnType columnType, SqlDataTypePolicy sqlDataTypePolicy) throws UnifyException {
        sqlDataTypePolicies.put(columnType, sqlDataTypePolicy);
    }

    protected List<String> getDataSourceInitStatements() {
        return Collections.emptyList();
    }

    protected void appendCreateTableColumnSQLL(StringBuilder sb, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            boolean onAlter) {
        sb.append(sqlFieldSchemaInfo.getPreferredColumnName());
        SqlDataTypePolicy sqlDataTypePolicy = sqlDataTypePolicies.get(sqlFieldSchemaInfo.getColumnType());
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
     * @param sb
     *            the string buffer
     * @param aggregateType
     *            the aggregate type
     * @param funcParam
     *            the function parameter
     * @param distinct
     *            indicates aggregate on distinct values
     * @throws UnifyException
     *             if an error occurs
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
     * @param sql
     *            the string buffer
     * @param sqlEntityInfo
     *            the record info
     * @param query
     *            the query
     * @param queryType
     *            the query type
     * @return a true value if clause was appended
     * @throws UnifyException
     *             if an error occurs
     */
    protected boolean appendWhereClause(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query,
            SqlQueryType queryType) throws UnifyException {
        boolean isAppend = false;
        if (!query.isEmptyCriteria()) {
            sql.append(" WHERE ");
            translateCriteria(sql, sqlEntityInfo, query.getRestrictions());
            if (query.isMinMax()) {
                sql.append(" AND ");
                StringBuilder critSql = new StringBuilder();
                translateCriteria(critSql, sqlEntityInfo, query.getRestrictions());
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

        if (queryType.includeOrder() && query.isOrder()) {
            isAppend |= appendOrderClause(sql, sqlEntityInfo, query);
        }

        if (queryType.includeLimit()) {
            isAppend |= appendLimitOffsetSuffixClause(sql, query.getOffset(), getQueryLimit(query), isAppend);
        }

        return isAppend;
    }

    /**
     * Appends WHERE clause to a string buffer.
     * 
     * @param sql
     *            the string buffer
     * @param parameterInfoList
     *            the parameter information list
     * @param sqlEntityInfo
     *            the SQl record information
     * @param query
     *            the query
     * @param queryType
     *            the query type
     * @return true if an clause was appended otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    protected boolean appendWhereClause(StringBuilder sql, List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, Query<? extends Entity> query, SqlQueryType queryType) throws UnifyException {
        boolean isAppend = false;

        int limit = getQueryLimit(query);
        boolean isCritLimOffset = queryType.isUpdate() && (limit > 0 || query.isOffset());
        if (isCritLimOffset) {
            sql.append(" WHERE ").append(sqlEntityInfo.getIdFieldInfo().getPreferredColumnName()).append(" IN (SELECT ")
                    .append(sqlEntityInfo.getIdFieldInfo().getPreferredColumnName()).append(" FROM ")
                    .append(sqlEntityInfo.getSchemaViewName());
        }

        if (!query.isEmptyCriteria()) {
            Restriction restriction = query.getRestrictions();
            SqlCriteriaPolicy sqlCriteriaPolicy = getSqlCriteriaPolicy(restriction.getType());
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
            } else {
                if (!query.isIgnoreEmptyCriteria()) {
                    throw new UnifyException(UnifyCoreErrorConstants.RECORD_CRITERIA_REQ_FOR_STATEMENT);
                }
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

        return isAppend;
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

    /**
     * Appends ORDER clause to a string buffer.
     * 
     * @param sql
     *            the buffer to write to
     * @param sqlEntityInfo
     *            the record information
     * @param query
     *            the query
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
     * Appends LIMIT and OFFSET infix clause to supplied string builder using limit
     * and offset information in supplied criteria.
     * 
     * @param sql
     *            the builder to append to
     * @param offset
     *            the offset
     * @param limit
     *            the limit
     * @return a true value if any clause was appended otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract boolean appendLimitOffsetInfixClause(StringBuilder sql, int offset, int limit)
            throws UnifyException;

    /**
     * Appends a where LIMIT and OFFSET suffix clause to supplied string builder
     * using limit and offset information in supplied criteria.
     * 
     * @param sql
     *            the builder to append to
     * @param offset
     *            the offset
     * @param limit
     *            the limit
     * @param append
     *            indicates append mode
     * @return a true value if any clause was appended otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract boolean appendWhereLimitOffsetSuffixClause(StringBuilder sql, int offset, int limit,
            boolean append) throws UnifyException;

    /**
     * Appends LIMIT and OFFSET suffix clause to supplied string builder using limit
     * and offset information in supplied criteria.
     * 
     * @param sql
     *            the builder to append to
     * @param offset
     *            the offset
     * @param limit
     *            the limit
     * @param append
     *            indicates append mode
     * @return a true value if any clause was appended otherwise false
     * @throws UnifyException
     *             if an error occurs
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
     * @param name
     *            the name to convert
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

    protected void appendColumnAndTypeSql(StringBuilder sb, SqlFieldSchemaInfo sqlFieldSchemaInfo, boolean alter)
            throws UnifyException {
        SqlDataTypePolicy sqlDataTypePolicy = getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType());
        sb.append(sqlFieldSchemaInfo.getPreferredColumnName());
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

    protected void appendTypeSql(StringBuilder sb, SqlColumnInfo sqlColumnInfo) {
        sb.append(' ').append(sqlColumnInfo.getTypeName());
        if (sqlColumnInfo.getSize() > 0) {
            sb.append('(').append(sqlColumnInfo.getSize());
            if (sqlColumnInfo.getDecimalDigits() > 0) {
                sb.append(',').append(sqlColumnInfo.getDecimalDigits());
            }
            sb.append(')');
        }
    }

    protected SqlEntityInfo getSqlEntityInfo(Query<? extends Entity> query) throws UnifyException {
        return (SqlEntityInfo) sqlEntityInfoFactory.getSqlEntityInfo(SqlUtils.getEntityClass(query));
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
     * Sets the dialect timestamp format.
     * 
     * @param timestampFormat
     *            the format to set
     */
    protected void setTimestampFormat(DateFormat timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    /**
     * Returns criteria result rows limit..
     * 
     * @param query
     *            the supplied record criteria object
     * @return returns the recordCrityeria limit if set otherwise returns
     *         container's global criteria limit if
     *         {@link Query#isApplyAppQueryLimit()} is not set, otherwise returns
     *         zero.
     * @throws UnifyException
     *             if an error occurs
     */
    protected int getQueryLimit(Query<? extends Entity> query) throws UnifyException {
        if (query.isLimit()) {
            return query.getLimit();
        }

        if (query.isApplyAppQueryLimit()) {
            return getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_QUERY_LIMIT,
                    UnifyContainer.DEFAULT_APPLICATION_QUERY_LIMIT);
        }

        return 0;
    }

    private String generateCreateViewSqlForTableEntity(SqlEntitySchemaInfo sqlEntitySchemaInfo, boolean format)
            throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE VIEW ").append(sqlEntitySchemaInfo.getSchemaViewName()).append('(');
        if (format) {
            sb.append(newLineSql);
        }

        ViewAliasInfo viewAliasInfo = new ViewAliasInfo(sqlEntitySchemaInfo.getTableAlias());
        Map<SqlFieldSchemaInfo, SqlJoinInfo> sqlJoinMap = new LinkedHashMap<SqlFieldSchemaInfo, SqlJoinInfo>();
        for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getListFieldInfos()) {
            appendCreateViewSQLElements(sqlEntitySchemaInfo, sqlFieldInfo, viewAliasInfo, sqlJoinMap);
        }

        boolean appendSym = false;
        StringBuilder fsb = new StringBuilder();
        StringBuilder ssb = new StringBuilder();
        for (SqlPair sqlPair : viewAliasInfo.getPairs()) {
            if (appendSym) {
                fsb.append(',');
                ssb.append(',');

                if (format) {
                    fsb.append(newLineSql);
                    ssb.append(newLineSql);
                }
            } else {
                appendSym = true;
            }

            if (format) {
                fsb.append('\t');
                ssb.append('\t');
            }

            fsb.append(sqlPair.getAliasSql());
            ssb.append(sqlPair.getSelectSql());
        }

        sb.append(fsb.toString());
        if (format) {
            sb.append(newLineSql);
        }

        sb.append(") AS SELECT ");
        if (format) {
            sb.append(newLineSql);
        }
        sb.append(ssb.toString());

        if (format) {
            sb.append(newLineSql);
            sb.append("FROM ");
        } else {
            sb.append(" FROM ");
        }
        sb.append(sqlEntitySchemaInfo.getSchemaTableName()).append(' ').append(viewAliasInfo.getViewAlias());
        for (SqlJoinInfo sqlJoinInfo : sqlJoinMap.values()) {
            if (format) {
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

    private String generateCreateViewSqlForViewEntity(SqlEntitySchemaInfo sqlEntitySchemaInfo, boolean format)
            throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE VIEW ").append(sqlEntitySchemaInfo.getSchemaViewName()).append('(');
        if (format) {
            sb.append(newLineSql);
        }

        boolean appendSym = false;
        StringBuilder fsb = new StringBuilder();
        StringBuilder ssb = new StringBuilder();
        for (SqlFieldSchemaInfo sqlFieldInfo : sqlEntitySchemaInfo.getListFieldInfos()) {
            if (appendSym) {
                fsb.append(", ");
                ssb.append(", ");

                if (format) {
                    fsb.append(newLineSql);
                    ssb.append(newLineSql);
                }
            } else {
                appendSym = true;
            }

            if (format) {
                fsb.append('\t');
                ssb.append('\t');
            }

            fsb.append(sqlFieldInfo.getPreferredColumnName());
            ssb.append(sqlFieldInfo.getForeignEntityPreferredAlias() + '.'
                    + sqlFieldInfo.getForeignFieldInfo().getPreferredColumnName());
        }

        sb.append(fsb.toString());
        if (format) {
            sb.append(newLineSql);
        }

        sb.append(") AS SELECT ");
        if (format) {
            sb.append(newLineSql);
        }
        sb.append(ssb.toString());

        if (format) {
            sb.append(newLineSql);
            sb.append("FROM ");
        } else {
            sb.append(" FROM ");
        }

        appendSym = false;
        for (Map.Entry<String, Class<?>> entry : sqlEntitySchemaInfo.getViewBaseTables().entrySet()) {
            if (appendSym) {
                sb.append(", ");

                if (format) {
                    sb.append(newLineSql);
                }
            } else {
                appendSym = true;
            }

            if (format) {
                sb.append('\t');
            }

            sb.append(getSqlEntityInfo(entry.getValue()).getPreferredTableName()).append(' ').append(entry.getKey());
        }

        if (sqlEntitySchemaInfo.isViewRestriction()) {
            if (format) {
                sb.append(newLineSql);
                sb.append("WHERE ");
            } else {
                sb.append(" WHERE ");
            }

            appendSym = false;
            for (SqlViewRestrictionInfo sqlViewRestrictionInfo : sqlEntitySchemaInfo.getViewRestrictionList()) {
                if (appendSym) {
                    if (format) {
                        sb.append(newLineSql);
                        sb.append('\t');
                    }
                    sb.append(" AND ");
                } else {
                    appendSym = true;
                }

                sqlCriteriaPolicies.get(sqlViewRestrictionInfo.getRestrictionType()).translate(sb,
                        sqlViewRestrictionInfo.getTableAlias(), sqlViewRestrictionInfo.getColumnName(),
                        sqlViewRestrictionInfo.getParam1(), sqlViewRestrictionInfo.getParam2());
            }
        }

        return sb.toString();
    }

    private class SqlStatementPoolsFactory extends FactoryMap<Class<?>, SqlStatementPools> {
        @Override
        protected SqlStatementPools create(Class<?> clazz, Object... params) throws Exception {
            return new SqlStatementPools(sqlEntityInfoFactory.getSqlEntityInfo(clazz), sqlDataTypePolicies,
                    sqlCacheFactory.get(clazz), getStatementInfoTimeout, minStatementInfo, maxStatementInfo);
        }
    };

    private SqlColumnAlterInfo checkSqlColumnAltered(SqlFieldSchemaInfo sqlFieldSchemaInfo,
            SqlFieldSchemaInfo oldSqlFieldSchemaInfo) throws UnifyException {
        boolean nullableChange = sqlFieldSchemaInfo.isNullable() != oldSqlFieldSchemaInfo.isNullable();
        boolean defaultChange = false;
        boolean typeChange = sqlFieldSchemaInfo.getColumnType().equals(oldSqlFieldSchemaInfo.getColumnType());
        boolean lenChange = sqlFieldSchemaInfo.getLength() != oldSqlFieldSchemaInfo.getLength()
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
            updateParams.append(sqlFieldInfo.getPreferredColumnName()).append(" = ?");

            Object value = entry.getValue();
            if (sqlFieldInfo.isTransformed()) {
                value = ((Transformer<Object, Object>) sqlFieldInfo.getTransformer()).forwardTransform(value);
            }

            // Fix updates for enumerations that come different value type 01/07/19
            if (EnumConst.class.isAssignableFrom(sqlFieldInfo.getFieldType()) && value != null
                    && !value.getClass().equals(sqlFieldInfo.getFieldType())) {
                value = DataUtils.convert(sqlFieldInfo.getFieldType(), value, null);
            }
            // End fix

            parameterInfoList.add(new SqlParameter(sqlDataTypePolicies.get(sqlFieldInfo.getColumnType()), value));
        }
        return updateParams.toString();
    }

    private void translateCriteria(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction restriction)
            throws UnifyException {
        sqlCriteriaPolicies.get(restriction.getType()).translate(sql, sqlEntityInfo, restriction);
    }

    private void appendCreateViewSQLElements(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldInfo,
            ViewAliasInfo viewAliasInfo, Map<SqlFieldSchemaInfo, SqlJoinInfo> sqlJoinMap) {
        String column = sqlFieldInfo.getPreferredColumnName();
        String viewAlias = viewAliasInfo.getViewAlias();
        String aliasExt = "";
        ViewAliasInfo.FkChainViewAliasInfo fcvAliasInfo = null;
        while (sqlFieldInfo.isListOnly()) {
            SqlFieldSchemaInfo fkSQLFieldInfo = sqlFieldInfo.getForeignKeyFieldInfo();
            if (fcvAliasInfo == null) {
                fcvAliasInfo = viewAliasInfo.getFkChainViewAliasInfo(fkSQLFieldInfo);
            }

            if (!sqlJoinMap.containsKey(fkSQLFieldInfo)) {
                StringBuilder csb = new StringBuilder();
                String tableAlias = fkSQLFieldInfo.getForeignEntityInfo().getTableAlias() + fkSQLFieldInfo.getName();

                viewAlias = fcvAliasInfo.add(tableAlias);
                csb.append(viewAlias).append('.').append(fkSQLFieldInfo.getForeignFieldInfo().getPreferredColumnName());
                csb.append(" = ");
                csb.append(fcvAliasInfo.getViewAlias(sqlEntitySchemaInfo.getTableAlias() + aliasExt)).append('.')
                        .append(fkSQLFieldInfo.getPreferredColumnName());
                sqlJoinMap.put(fkSQLFieldInfo, new SqlJoinInfo(
                        fkSQLFieldInfo.getForeignEntityInfo().getSchemaTableName(), viewAlias, csb.toString()));
            }

            sqlEntitySchemaInfo = sqlFieldInfo.getForeignEntityInfo();
            aliasExt = fkSQLFieldInfo.getName();
            viewAlias = fcvAliasInfo.getViewAlias(sqlEntitySchemaInfo.getTableAlias() + aliasExt);
            sqlFieldInfo = sqlFieldInfo.getForeignFieldInfo();
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
            SqlEntitySchemaInfo sqlEntitySchemaInfo = sqlEntityInfoFactory.getSqlEntityInfo(clazz);
            if (sqlEntitySchemaInfo.isViewOnly()) {
                return new SqlCache(generateListRecordSql(sqlEntitySchemaInfo),
                        generateListRecordByPkSql(sqlEntitySchemaInfo),
                        generateListRecordByPkVersionSql(sqlEntitySchemaInfo),
                        generateListRecordSql(sqlEntitySchemaInfo), generateListRecordByPkSql(sqlEntitySchemaInfo),
                        generateListRecordByPkVersionSql(sqlEntitySchemaInfo), null, null, null, null, null, null, null,
                        generateCountRecordSql(sqlEntitySchemaInfo), generateTestSql());
            }

            return new SqlCache(generateFindRecordSql(sqlEntitySchemaInfo),
                    generateFindRecordByPkSql(sqlEntitySchemaInfo),
                    generateFindRecordByPkVersionSql(sqlEntitySchemaInfo), generateListRecordSql(sqlEntitySchemaInfo),
                    generateListRecordByPkSql(sqlEntitySchemaInfo),
                    generateListRecordByPkVersionSql(sqlEntitySchemaInfo), generateInsertRecordSql(sqlEntitySchemaInfo),
                    generateUpdateRecordSql(sqlEntitySchemaInfo), generateUpdateRecordByPkSql(sqlEntitySchemaInfo),
                    generateUpdateRecordByPkVersionSql(sqlEntitySchemaInfo),
                    generateDeleteRecordSql(sqlEntitySchemaInfo), generateDeleteRecordByPkSql(sqlEntitySchemaInfo),
                    generateDeleteRecordByPkVersionSql(sqlEntitySchemaInfo),
                    generateCountRecordSql(sqlEntitySchemaInfo), generateTestSql());
        }
    };

    private List<SqlResult> getSqlResultList(List<SqlFieldInfo> sqlFieldInfoList) {
        List<SqlResult> resultInfoList = new ArrayList<SqlResult>();
        for (SqlFieldInfo sqlFieldInfo : sqlFieldInfoList) {
            resultInfoList.add(new SqlResult(sqlDataTypePolicies.get(sqlFieldInfo.getColumnType()), sqlFieldInfo));
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
