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
package com.tcdng.unify.core.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.database.sql.SqlJoinType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * A native query object.
 * 
 * @author Lateef
 * @since 1.0
 */
public class NativeQuery {

    private List<Column> columnList;

    private List<Join> joinList;

    private List<OrderBy> orderByList;

    private Filter rootFilter;

    private String schemaName;

    private String tableName;

    private int offset;

    private int limit;

    private boolean distinct;

    private NativeQuery(List<Column> columnList, List<Join> joinList, Filter rootFilter, List<OrderBy> orderByList,
            String schemaName, String tableName, int offset, int limit, boolean distinct) {
        this.columnList = columnList;
        this.joinList = joinList;
        this.rootFilter = rootFilter;
        this.orderByList = orderByList;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.offset = offset;
        this.limit = limit;
        this.distinct = distinct;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public List<Join> getJoinList() {
        return joinList;
    }

    public Filter getRootFilter() {
        return rootFilter;
    }

    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public int columns() {
        return columnList.size();
    }

    public boolean isDistinct() {
        return distinct;
    }

    public boolean isJoin() {
        return !joinList.isEmpty();
    }

    public boolean isRootFilter() {
        return rootFilter != null;
    }

    public boolean isOrderBy() {
        return !orderByList.isEmpty();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private List<Column> columnList;

        private List<Join> joinList;

        private List<OrderBy> orderByList;

        private Stack<Filter> filters;

        private String schemaName;

        private String tableName;

        private Filter rootFilter;

        private int offset;

        private int limit;

        private boolean distinct;

        private Builder() {
            this.columnList = new ArrayList<Column>();
            this.joinList = new ArrayList<Join>();
            this.filters = new Stack<Filter>();
            this.orderByList = new ArrayList<OrderBy>();
            this.limit = 0;
        }

        public Builder schemaName(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder addColumn(String tableName, String columnName) {
            columnList.add(new Column(tableName, columnName));
            return this;
        }

        public Builder addJoin(SqlJoinType type, String tableA, String columnA, String tableB, String columnB) {
            joinList.add(new Join(type, tableA, columnA, tableB, columnB));
            return this;
        }

        public Builder addOrderBy(String columnName) {
            return addOrderBy(OrderType.ASCENDING, columnName);
        }

        public Builder addOrderBy(OrderType type, String columnName) {
            orderByList.add(new OrderBy(type, columnName));
            return this;
        }

        public Builder beginCompoundFilter(RestrictionType op) {
            if (!op.isCompound()) {
                throw new IllegalArgumentException(op + " is not a compound restriction type.");
            }

            if (rootFilter != null) {
                throw new IllegalStateException("Can not have multiple root compound filter.");
            }

            Filter filter = new Filter(op, new ArrayList<Filter>());
            if (!filters.isEmpty()) {
                filters.peek().subFilterList.add(filter);
            }

            filters.push(filter);
            return this;
        }

        public Builder endCompoundFilter() {
            if (filters.isEmpty()) {
                throw new IllegalStateException("No compound filter context currently open.");
            }

            Filter filter = filters.pop();
            if (filters.isEmpty()) {
                rootFilter = filter;
            }

            return this;
        }

        public Builder addSimpleFilter(RestrictionType op, String tableName, String columnName, Object param1,
                Object param2) {
            if (op.isCompound()) {
                throw new IllegalArgumentException(op + " is not a simple restriction type.");
            }

            if (filters.isEmpty()) {
                throw new IllegalStateException("No compound filter context currently open.");
            }

            filters.peek().subFilterList.add(new Filter(op, tableName, columnName, param1, param2));
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        public NativeQuery build() {
            if (rootFilter == null && !filters.isEmpty()) {
                throw new IllegalStateException("Compound filter context is still open.");
            }

            return new NativeQuery(DataUtils.unmodifiableList(columnList), DataUtils.unmodifiableList(joinList),
                    rootFilter, DataUtils.unmodifiableList(orderByList), schemaName, tableName, offset, limit,
                    distinct);
        }
    }

    public static class Column {

        private String tableName;

        private String columnName;

        public Column(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }

        public String getTableName() {
            return tableName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    public static class Join {

        private SqlJoinType type;

        private String tableA;

        private String columnA;

        private String tableB;

        private String columnB;

        public Join(SqlJoinType type, String tableA, String columnA, String tableB, String columnB) {
            this.type = type;
            this.tableA = tableA;
            this.columnA = columnA;
            this.tableB = tableB;
            this.columnB = columnB;
        }

        public SqlJoinType getType() {
            return type;
        }

        public String getTableA() {
            return tableA;
        }

        public String getColumnA() {
            return columnA;
        }

        public String getTableB() {
            return tableB;
        }

        public String getColumnB() {
            return columnB;
        }

    }

    public static class Filter {

        private RestrictionType op;

        private String tableName;

        private String columnName;

        private Object param1;

        private Object param2;

        private List<Filter> subFilterList;

        public Filter(RestrictionType op, String tableName, String columnName, Object param1, Object param2) {
            this.op = op;
            this.tableName = tableName;
            this.columnName = columnName;
            this.param1 = param1;
            this.param2 = param2;
        }

        public Filter(RestrictionType op, List<Filter> subFilterList) {
            this.op = op;
            this.subFilterList = subFilterList;
        }

        public RestrictionType getOp() {
            return op;
        }

        public boolean isCompound() {
            return op.isCompound();
        }

        public String getTableName() {
            return tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public Object getParam1() {
            return param1;
        }

        public Object getParam2() {
            return param2;
        }

        public List<Filter> getSubFilterList() {
            return subFilterList;
        }
    }

    public static class OrderBy {

        private OrderType orderType;

        private String columnName;

        public OrderBy(OrderType orderType, String columnName) {
            this.orderType = orderType;
            this.columnName = columnName;
        }

        public OrderType getOrderType() {
            return orderType;
        }

        public String getColumnName() {
            return columnName;
        }
    }
}
