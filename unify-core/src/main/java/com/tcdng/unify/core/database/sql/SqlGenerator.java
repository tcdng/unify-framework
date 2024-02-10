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

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.ForeignConstraints;
import com.tcdng.unify.core.constant.Indexes;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.constant.QueryAgainst;
import com.tcdng.unify.core.constant.UniqueConstraints;
import com.tcdng.unify.core.constant.Views;

/**
 * SQL generator interface.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface SqlGenerator extends UnifyComponent {

    /**
     * Generates all native create SQL for record type.
     * 
     * @param sqlEntitySchemaInfo
     *            the record schema information
     * @param foreignConstraints
     *            indicates foreign constraints be generated
     * @param uniqueConstraints
     *            indicates unique constraints be generated
     * @param indexes
     *            indicates indexes be generated
     * @param views
     *            indicates views be generated
     * @param format
     *            indicates generated SQL is formatted
     * @return the generated SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateAllCreateSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, ForeignConstraints foreignConstraints,
            UniqueConstraints uniqueConstraints, Indexes indexes, Views views, PrintFormat format)
            throws UnifyException;

    /**
     * Generates all native upgrade SQL for record type.
     * 
     * @param sqlEntitySchemaInfo
     *            the record schema information
     * @param oldSqlEntitySchemaInfo
     *            the old record schema information
     * @param foreignConstraints
     *            indicates foreign constraints be generated
     * @param uniqueConstraints
     *            indicates unique constraints be generated
     * @param indexes
     *            indicates indexes be generated
     * @param views
     *            indicates vies be generated
     * @param format
     *            indicateds of generated SQL is formatted
     * @return the generated SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateAllUpgradeSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlEntitySchemaInfo oldSqlEntitySchemaInfo,
            ForeignConstraints foreignConstraints, UniqueConstraints uniqueConstraints, Indexes indexes, Views views,
            PrintFormat format) throws UnifyException;

    /**
     * Generates a native create table SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param format
     *            flag that indicates if SQL should be formatted
     * @return the generated create table SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateCreateTableSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates native drop table SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the generated drop table SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropTableSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates rename table SQL.
     * 
     * @param sqlEntitySchemaInfo
     *            the new SQL record schema info
     * @param oldSqlEntitySchemaInfo
     *            the old SQL record schema info
     * @param format
     *            indicates if SQL should be formatted
     * @return generated SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateRenameTable(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlEntitySchemaInfo oldSqlEntitySchemaInfo,
            PrintFormat format) throws UnifyException;

	/**
	 * Generates add column SQL.
	 * 
	 * @param sqlEntitySchemaInfo the old SQL record schema info
	 * @param sqlFieldSchemaInfo  the field schema information
	 * @param format              indicates if SQL should be formatted
	 * @return generated SQL
	 * @throws UnifyException if an error occurs
	 */
	String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			PrintFormat format) throws UnifyException;

	/**
	 * Generates add column SQL.
	 * 
	 * @param sqlEntitySchemaInfo the SQL record schema info
	 * @param columnName          the column name
	 * @param sqlFieldSchemaInfo  the field schema information
	 * @param format              indicates if SQL should be formatted
	 * @return generated SQL
	 * @throws UnifyException if an error occurs
	 */
	String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, String columnName,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates alter column SQL.
     * 
     * @param sqlEntitySchemaInfo
     *            the SQL record schema info
     * @param sqlFieldSchemaInfo
     *            the new SQL field schema info
     * @param sqlColumnAlterInfo
     *            the alter information
     * @param format
     *            indicates if SQL should be formatted
     * @return generated SQL
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> generateAlterColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            SqlColumnAlterInfo sqlColumnAlterInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates alter column to nullable SQL.
     * 
     * @param sqlEntitySchemaInfo
     *            the SQL record schema info
     * @param sqlColumnInfo
     *            the column information object
     * @param format
     *            indicates if SQL should be formatted
     * @return generated SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateAlterColumnNull(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlColumnInfo sqlColumnInfo,
            PrintFormat format) throws UnifyException;

	/**
	 * Generates rename column SQL.
	 * 
	 * @param tableName       the table name
	 * @param oldColumnName   the old column name
	 * @param newSqlFieldInfo the new SQL field information
	 * @param format          indicates if SQL should be formatted
	 * @return generated SQL
	 * @throws UnifyException if an error occurs
	 */
	String generateRenameColumn(String tableName, String oldColumnName, SqlFieldSchemaInfo newSqlFieldInfo,
			PrintFormat format) throws UnifyException;

    /**
     * Generates drop column SQL.
     * 
     * @param sqlEntitySchemaInfo
     *            the old SQL record schema info
     * @param sqlFieldSchemaInfo
     *            the field schema information
     * @param format
     *            indicates if SQL should be formatted
     * @return generated SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            PrintFormat format) throws UnifyException;

    /**
     * Generates native add foreign key constraint SQL for specified constraint.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlForeignKeyInfo
     *            foreign key information
     * @param format
     *            indicates if SQL should be formatted
     * @return the add foreign constraint SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateAddForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlForeignKeySchemaInfo sqlForeignKeyInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates native drop foreign key constraint SQL for specified constraint.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlForeignKeyInfo
     *            foreign key information
     * @param format
     *            indicates if SQL should be formatted
     * @return the drop foreign constraint SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlForeignKeySchemaInfo sqlForeignKeyInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates native drop foreign key constraint SQL for specified constraint.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param dbForeignKeyName
     *            foreign key name
     * @param format
     *            indicates if SQL should be formatted
     * @return the drop foreign constraint SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbForeignKeyName,
            PrintFormat format) throws UnifyException;

    /**
     * Generates native inline unique key constraint SQL for specified unique
     * constraint info.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlUniqueConstraintInfo
     *            unique constraint schema information
     * @param format
     *            indicates if SQL should be formatted
     * @return the add unique constraint SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateInlineUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates native add unique key constraint SQL for specified unique
     * constraint info.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlUniqueConstraintInfo
     *            unique constraint schema information
     * @param format
     *            indicates if SQL should be formatted
     * @return the add unique constraint SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateAddUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates native drop unique key constraint SQL for specified unique
     * constraint info.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlUniqueConstraintInfo
     *            unique constraint schema information
     * @param format
     *            indicates if SQL should be formatted
     * @return the drop unique constraint SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates native drop unique key constraint SQL for specified unique
     * constraint.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param dbUniqueConstraintName
     *            unique constraint name
     * @param format
     *            indicates if SQL should be formatted
     * @return the drop unique constraint SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbUniqueConstraintName,
            PrintFormat format) throws UnifyException;

    /**
     * Generates native inline index SQL for specified index.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlIndexInfo
     *            the index schema information
     * @param format
     *            indicates if SQL should be formatted
     * @return the create index SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateInlineIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
            PrintFormat format) throws UnifyException;

    /**
     * Generates native create index SQL for specified index.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlIndexInfo
     *            the index schema information
     * @param format
     *            indicates if SQL should be formatted
     * @return the create index SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateCreateIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
            PrintFormat format) throws UnifyException;

    /**
     * Generate native drop index SQL for specified index.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param sqlIndexInfo
     *            the index schema information
     * @param format
     *            indicates if SQL should be formatted
     * @return the drop index SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
            PrintFormat format) throws UnifyException;

    /**
     * Generate native drop index SQL for specified index.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param dbIndexName
     *            the index name
     * @param format
     *            indicates if SQL should be formatted
     * @return the drop index SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbIndexName, PrintFormat format)
            throws UnifyException;

    /**
     * Generates native create view SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param format
     *            flag that specifies if SQL is formatted
     * @return the generated create view SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateCreateViewSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, PrintFormat format) throws UnifyException;

    /**
     * Generates native drop view SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the generated drop view SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDropViewSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native find record SQL for specified record type. Typically a
     * select all statement from a table.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param queryAgainst
     *            indicates what to query against
     * @return the generated find SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateFindRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, QueryAgainst queryAgainst)
            throws UnifyException;

    /**
     * Generates native find record by primary key SQL for specified record type.
     * Typically a select all statement where primary key from a table.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the generated find by primary key SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateFindRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native find record by primary key and version number SQL for
     * specified record type. Typically a select all statement where primary key and
     * version number from a table.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the generated find by primary key and version number SQL
     * @throws UnifyException
     *             if record type has no version number. If an error occurs
     */
    String generateFindRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native list record SQL for specified record type. Typically a
     * select all statement from a view.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the generated list SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateListRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native list record by primary key SQL for specified record type.
     * Typically a select all statement where primary key from a view.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the generated list by primary key SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateListRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native list record by primary key and version number SQL for
     * specified record type. Typically a select all statement where primary key and
     * version number from a view.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the generated list by primary key and version number SQL
     * @throws UnifyException
     *             if record type has no version number. If an error occurs
     */
    String generateListRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native insert record SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the create SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateInsertRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native insert record SQL with unmanaged identity for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the create SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateInsertUnmanagedIdentityRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;
    
    /**
     * Generates native insert record SQL using supplied values for specified record
     * type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param params
     *            the parameter data of field name to value map
     * @param format
     *            indicates if SQL is formatted
     * @return the insert record SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateInsertValuesSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, Map<String, Object> params,
            PrintFormat format) throws UnifyException;

    /**
     * Generates native insert record SQL using supplied values for specified record
     * type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param insertValueList
     * @param format
     *            indicates if SQL is formatted
     * @return the insert records SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateInsertValuesSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, List<Map<String, Object>> insertValueList,
            PrintFormat format) throws UnifyException;

    /**
     * Generates native update record SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the update SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateUpdateRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native update record SQL by primary key for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the update by primary key SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateUpdateRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native update record SQL by primary key and version number for
     * specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the update by primary key and version number SQL
     * @throws UnifyException
     *             if record type has no version number. If an error occurs
     */
    String generateUpdateRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native delete record SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the delete SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDeleteRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native delete record SQL by primary key for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the delete by primary key SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateDeleteRecordByPkSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native delete record SQL by primary key and version number for
     * specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @return the delete by primary key and version number SQL
     * @throws UnifyException
     *             if record type has no version number. If an error occurs
     */
    String generateDeleteRecordByPkVersionSql(SqlEntitySchemaInfo sqlEntitySchemaInfo) throws UnifyException;

    /**
     * Generates native count record SQL for specified record type.
     * 
     * @param sqlEntitySchemaInfo
     *            record schema information
     * @param queryAgainst
     *            indicates what to query against
     * @return the count SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateCountRecordSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, QueryAgainst queryAgainst) throws UnifyException;

    /**
     * Generates connection test SQL.
     * 
     * @return the test SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateTestSql() throws UnifyException;

    /**
     * Generates connection current UTC timestamp SQL. Generates SQL is expected to
     * select the current timestamp.
     * 
     * @return the current timestamp SQL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateUTCTimestampSql() throws UnifyException;

    /**
     * Generates a like parameter based on supplied like type and parameter object
     * 
     * @param type
     *            the like type
     * @param param
     *            the base parameter
     * @return the generated parameter
     * @throws UnifyException
     *             if an error occurs
     */
    String generateLikeParameter(SqlLikeType type, Object param) throws UnifyException;
    
    /**
     * Indicates if this component generates unique constraints in generated create table scripts.
     */
    boolean isGeneratesUniqueConstraintsOnCreateTable();
    
    /**
     * Indicates if this component generates indexes in generated create table scripts.
     */
    boolean isGeneratesIndexesOnCreateTable();
}
