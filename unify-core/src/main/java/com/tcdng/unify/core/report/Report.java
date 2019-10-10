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
package com.tcdng.unify.core.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.database.sql.SqlJoinType;

/**
 * Used to define a report for generation at runtime.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Report {

    private String code;

    private String title;

    private String template;

    private String processor;

    private String dataSource;

    private String query;

    private String theme;

    private Collection<?> beanCollection;

    private ReportTable table;

    private List<ReportTableJoin> joins;

    private List<ReportColumn> columns;

    private ReportFilter filter;

    private ReportFormat format;

    private ReportLayout layout;

    private ReportParameters reportParameters;

    private int pageWidth;

    private int pageHeight;

    private String summationLegend;

    private String groupSummationLegend;

    private boolean dynamicDataSource;

    private boolean printColumnNames;

    private boolean printGroupColumnNames;

    private boolean underlineRows;

    private boolean shadeOddRows;

    private boolean landscape;

    private Report(String code, String title, String template, String processor, String dataSource, String query,
            String theme, Collection<?> beanCollection, ReportTable table, List<ReportTableJoin> joins,
            List<ReportColumn> columns, ReportFilter filter, ReportFormat format, ReportLayout layout,
            ReportParameters reportParameters, int pageWidth, int pageHeight, String summationLegend,
            String groupSummationLegend, boolean dynamicDataSource, boolean printColumnNames,
            boolean printGroupColumnNames, boolean underlineRows, boolean shadeOddRows, boolean landscape) {
        this.code = code;
        this.title = title;
        this.template = template;
        this.processor = processor;
        this.dataSource = dataSource;
        this.query = query;
        this.theme = theme;
        this.beanCollection = beanCollection;
        this.table = table;
        this.joins = joins;
        this.columns = columns;
        this.filter = filter;
        this.format = format;
        this.layout = layout;
        this.reportParameters = reportParameters;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.summationLegend = summationLegend;
        this.groupSummationLegend = groupSummationLegend;
        this.dynamicDataSource = dynamicDataSource;
        this.printColumnNames = printColumnNames;
        this.printGroupColumnNames = printGroupColumnNames;
        this.underlineRows = underlineRows;
        this.shadeOddRows = shadeOddRows;
        this.landscape = landscape;
    }

    public String getCode() {
        return code;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public ReportLayout getLayout() {
        return layout;
    }

    public String getTitle() {
        return title;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getProcessor() {
        return processor;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTheme() {
        return theme;
    }

    public ReportTable getTable() {
        return table;
    }

    public Collection<?> getBeanCollection() {
        return beanCollection;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public String getSummationLegend() {
        return summationLegend;
    }

    public String getGroupSummationLegend() {
        return groupSummationLegend;
    }

    public boolean isDynamicDataSource() {
        return dynamicDataSource;
    }

    public void setDynamicDataSource(boolean dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public boolean isPrintColumnNames() {
        return printColumnNames;
    }

    public boolean isPrintGroupColumnNames() {
        return printGroupColumnNames;
    }

    public boolean isUnderlineRows() {
        return underlineRows;
    }

    public boolean isShadeOddRows() {
        return shadeOddRows;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public boolean isDynamic() {
        return !columns.isEmpty();
    }

    public boolean isBeanCollection() {
        return beanCollection != null;
    }

    public boolean isQuery() {
        return query != null;
    }

    public ReportParameters getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(ReportParameters reportParameters) {
        this.reportParameters = reportParameters;
    }

    public void setParameter(String name, Object value) {
        reportParameters.setParameter(name, value);
    }

    public Object getParameter(String name) {
        return reportParameters.getParameter(name);
    }

    public List<ReportTableJoin> getJoins() {
        return joins;
    }

    public List<ReportColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public ReportFilter getFilter() {
        return filter;
    }

    public void addColumn(ReportColumn reportColumn) {
        columns.add(reportColumn);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String code;

        private String title;

        private String template;

        private String processor;

        private String dataSource;

        private String query;

        private String theme;

        private ReportTable table;

        private Collection<?> beanCollection;

        private List<ReportTableJoin> joins;

        private List<ReportColumn> columns;

        private Stack<ReportFilter> filters;

        private ReportFilter rootFilter;

        private ReportFormat format;

        private ReportLayout layout;

        private int pageWidth;

        private int pageHeight;

        private String summationLegend;

        private String groupSummationLegend;

        private boolean dynamicDataSource;

        private boolean printColumnNames;

        private boolean printGroupColumnNames;

        private boolean underlineRows;

        private boolean shadeOddRows;

        private boolean landscape;

        private Map<String, Object> parameters;

        private Builder() {
            this.format = ReportFormat.PDF;
            this.layout = ReportLayout.TABULAR;
            this.joins = new ArrayList<ReportTableJoin>();
            this.columns = new ArrayList<ReportColumn>();
            this.filters = new Stack<ReportFilter>();
            this.parameters = new HashMap<String, Object>();
            this.printColumnNames = true;
            this.printGroupColumnNames = true;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder processor(String processor) {
            this.processor = processor;
            return this;
        }

        public Builder dataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder theme(String theme) {
            this.theme = theme;
            return this;
        }

        public Builder beanCollection(Collection<?> beanCollection) {
            this.beanCollection = beanCollection;
            return this;
        }

        public Builder format(ReportFormat format) {
            this.format = format;
            return this;
        }

        public Builder layout(ReportLayout layout) {
            this.layout = layout;
            return this;
        }

        public Builder pageWidth(int pageWidth) {
            this.pageWidth = pageWidth;
            return this;
        }

        public Builder pageHeight(int pageHeight) {
            this.pageHeight = pageHeight;
            return this;
        }

        public Builder summationLegend(String summationLegend) {
            this.summationLegend = summationLegend;
            return this;
        }

        public Builder groupSummationLegend(String groupSummationLegend) {
            this.groupSummationLegend = groupSummationLegend;
            return this;
        }

        public Builder dynamicDataSource(boolean dynamicDataSource) {
            this.dynamicDataSource = dynamicDataSource;
            return this;
        }

        public Builder printColumnNames(boolean printColumnNames) {
            this.printColumnNames = printColumnNames;
            return this;
        }

        public Builder printGroupColumnNames(boolean printGroupColumnNames) {
            this.printGroupColumnNames = printGroupColumnNames;
            return this;
        }

        public Builder underlineRows(boolean underlineRows) {
            this.underlineRows = underlineRows;
            return this;
        }

        public Builder shadeOddRows(boolean shadeOddRows) {
            this.shadeOddRows = shadeOddRows;
            return this;
        }

        public Builder landscape(boolean landscape) {
            this.landscape = landscape;
            return this;
        }

        public Builder table(String tableName) {
            this.table = new ReportTable(tableName);
            return this;
        }

        public Builder addJoin(ReportTableJoin reportJoin) {
            joins.add(reportJoin);
            return this;
        }

        public Builder addJoin(SqlJoinType type, String table1, String column1, String table2, String column2) {
            joins.add(new ReportTableJoin(type, table1, column1, table2, column2));
            return this;
        }

        public Builder addJoin(String table1, String column1, String table2, String column2) {
            joins.add(new ReportTableJoin(table1, column1, table2, column2));
            return this;
        }

        public Builder addColumn(ReportColumn reportColumn) {
            columns.add(reportColumn);
            return this;
        }

        public Builder addColumn(String title, String name, Class<?> type, int widthRatio) throws UnifyException {
            addColumn(title, null, name, type, widthRatio);
            return this;
        }

        public Builder addColumn(String title, String table, String name, Class<?> type, int widthRatio)
                throws UnifyException {
            ReportColumn rc = ReportColumn.newBuilder().title(title).table(table).name(name).className(type.getName())
                    .widthRatio(widthRatio).build();
            columns.add(rc);
            return this;
        }

        public Builder addColumn(String title, String name, String className, String formatterUpl, OrderType order,
                HAlignType hAlignType, int widthRatio, boolean group, boolean groupOnNewPage, boolean sum) throws UnifyException {
            addColumn(title, null, name, className, formatterUpl, order, hAlignType, widthRatio, group, groupOnNewPage, sum);
            return this;
        }

        public Builder addColumn(String title, String table, String name, String className, String formatterUpl,
                OrderType order, HAlignType hAlignType, int widthRatio, boolean group, boolean groupOnNewPage, boolean sum)
                throws UnifyException {
            ReportColumn rc = ReportColumn.newBuilder().title(title).table(table).name(name).className(className)
                    .horizontalAlignment(hAlignType).widthRatio(widthRatio).formatter(formatterUpl).order(order)
                    .group(group).groupOnNewPage(groupOnNewPage).sum(sum).build();
            columns.add(rc);
            return this;
        }

        public Builder beginCompoundFilter(RestrictionType op) {
            if (!op.isCompound()) {
                throw new IllegalArgumentException(op + " is not a compound restriction type.");
            }

            if (rootFilter != null) {
                throw new IllegalStateException("Can not have multiple root compound filter.");
            }

            ReportFilter reportFilter = new ReportFilter(op, new ArrayList<ReportFilter>());
            if (!filters.isEmpty()) {
                filters.peek().getSubFilterList().add(reportFilter);
            }

            filters.push(reportFilter);
            return this;
        }

        public Builder endCompoundFilter() {
            if (filters.isEmpty()) {
                throw new IllegalStateException("No compound filter context currently open.");
            }

            ReportFilter reportFilter = filters.pop();
            if (filters.isEmpty()) {
                rootFilter = reportFilter;
            }

            return this;
        }

        public Builder addSimpleFilter(RestrictionType op, String tableName, String columnName, Object param1,
                Object param2) {
            return addSimpleFilter(new ReportFilter(op, tableName, columnName, param1, param2));
        }

        public Builder addSimpleFilter(ReportFilter reportFilter) {
            if (reportFilter.isCompound()) {
                throw new IllegalArgumentException(reportFilter.getOp() + " is not a simple restriction type.");
            }

            if (filters.isEmpty()) {
                throw new IllegalStateException("No compound filter context currently open.");
            }

            filters.peek().getSubFilterList().add(reportFilter);
            return this;
        }

        public Builder setParameter(String name, Object value) {
            parameters.put(name, value);
            return this;
        }

        public Builder setParameters(Map<String, Object> parameters) {
            parameters.putAll(parameters);
            return this;
        }

        public Report build() throws UnifyException {
            Report report = new Report(code, title, template, processor, dataSource, query, theme, beanCollection,
                    table, Collections.unmodifiableList(joins), columns, rootFilter, format, layout,
                    new ReportParameters(parameters), pageWidth, pageHeight, summationLegend, groupSummationLegend,
                    dynamicDataSource, printColumnNames, printGroupColumnNames, underlineRows, shadeOddRows, landscape);
            return report;
        }
    }
}
