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
package com.tcdng.unify.core.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.database.sql.SqlJoinType;
import com.tcdng.unify.core.operation.Operator;

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

	private Collection<?> beanCollection;

	private ReportTable table;

	private List<ReportTableJoin> joins;

	private List<ReportColumn> columns;

	private List<ReportColumnFilter> filters;

	private ReportFormat format;

	private ReportLayout layout;

	private ReportParameters reportParameters;

	private String columnFontName;

	private int columnFontSize;

	private int columnHeaderHeight;

	private int detailHeight;

	private String summationLegend;

	private String groupSummationLegend;

	private boolean dynamicDataSource;

	private boolean printColumnNames;

	private boolean underlineRows;

	private boolean shadeOddRows;

	private boolean landscape;

	private Report(String code, String title, String template, String processor, String dataSource, String query,
			Collection<?> beanCollection, ReportTable table, List<ReportTableJoin> joins, List<ReportColumn> columns,
			List<ReportColumnFilter> filters, ReportFormat format, ReportLayout layout,
			ReportParameters reportParameters, String columnFontName, int columnFontSize, int columnHeaderHeight,
			int detailHeight, String summationLegend, String groupSummationLegend, boolean dynamicDataSource,
			boolean printColumnNames, boolean underlineRows, boolean shadeOddRows, boolean landscape) {
		this.code = code;
		this.title = title;
		this.template = template;
		this.processor = processor;
		this.dataSource = dataSource;
		this.query = query;
		this.beanCollection = beanCollection;
		this.table = table;
		this.joins = joins;
		this.columns = columns;
		this.filters = filters;
		this.format = format;
		this.layout = layout;
		this.reportParameters = reportParameters;
		this.columnFontName = columnFontName;
		this.columnFontSize = columnFontSize;
		this.columnHeaderHeight = columnHeaderHeight;
		this.detailHeight = detailHeight;
		this.summationLegend = summationLegend;
		this.groupSummationLegend = groupSummationLegend;
		this.dynamicDataSource = dynamicDataSource;
		this.printColumnNames = printColumnNames;
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

	public ReportTable getTable() {
		return table;
	}

	public Collection<?> getBeanCollection() {
		return beanCollection;
	}

	public String getColumnFontName() {
		return columnFontName;
	}

	public int getColumnFontSize() {
		return columnFontSize;
	}

	public int getColumnHeaderHeight() {
		return columnHeaderHeight;
	}

	public int getDetailHeight() {
		return detailHeight;
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

	public List<ReportColumnFilter> getFilters() {
		return filters;
	}

	public void addColumn(ReportColumn reportColumn) {
		columns.add(reportColumn);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		public static String DEFAULT_COLUMN_FONTNAME = "Arial";

		public static int DEFAULT_COLUMN_FONTSIZE = 10;

		public static int DEFAULT_COLUMNHEADER_HEIGHT = 20;

		public static int DEFAULT_DETAIL_HEIGHT = 20;

		private String code;

		private String title;

		private String template;

		private String processor;

		private String dataSource;

		private String query;

		private ReportTable table;

		private Collection<?> beanCollection;

		private List<ReportTableJoin> joins;

		private List<ReportColumn> columns;

		private List<ReportColumnFilter> filters;

		private ReportFormat format;

		private ReportLayout layout;

		private String columnFontName;

		private int columnFontSize;

		private int columnHeaderHeight;

		private int detailHeight;

		private String summationLegend;

		private String groupSummationLegend;

		private boolean dynamicDataSource;

		private boolean printColumnNames;

		private boolean underlineRows;

		private boolean shadeOddRows;

		private boolean landscape;

		private Map<String, Object> parameters;

		private Builder() {
			this.format = ReportFormat.PDF;
			this.layout = ReportLayout.TABULAR;
			this.joins = new ArrayList<ReportTableJoin>();
			this.columns = new ArrayList<ReportColumn>();
			this.filters = new ArrayList<ReportColumnFilter>();
			this.parameters = new HashMap<String, Object>();
			this.printColumnNames = true;
			this.columnFontName = DEFAULT_COLUMN_FONTNAME;
			this.columnFontSize = DEFAULT_COLUMN_FONTSIZE;
			this.columnHeaderHeight = DEFAULT_COLUMNHEADER_HEIGHT;
			this.detailHeight = DEFAULT_DETAIL_HEIGHT;
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

		public Builder columnFontName(String columnFontName) {
			this.columnFontName = columnFontName;
			return this;
		}

		public Builder columnFontSize(int columnFontSize) {
			this.columnFontSize = columnFontSize;
			return this;
		}

		public Builder columnHeaderHeight(int columnHeaderHeight) {
			this.columnHeaderHeight = columnHeaderHeight;
			return this;
		}

		public Builder detailHeight(int detailHeight) {
			this.detailHeight = detailHeight;
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
				HAlignType hAlignType, int widthRatio, boolean group, boolean sum) throws UnifyException {
			addColumn(title, null, name, className, formatterUpl, order, hAlignType, widthRatio, group, sum);
			return this;
		}

		public Builder addColumn(String title, String table, String name, String className, String formatterUpl,
				OrderType order, HAlignType hAlignType, int widthRatio, boolean group, boolean sum)
				throws UnifyException {
			ReportColumn rc = ReportColumn.newBuilder().title(title).table(table).name(name).className(className)
					.horizontalAlignment(hAlignType).widthRatio(widthRatio).formatter(formatterUpl).order(order)
					.group(group).sum(sum).build();
			columns.add(rc);
			return this;
		}

		public Builder addFilter(ReportColumnFilter reportFilter) {
			filters.add(reportFilter);
			return this;
		}

		public Builder addFilter(Operator op, String tableName, String columnName, Object param1, Object param2) {
			filters.add(new ReportColumnFilter(op, tableName, columnName, param1, param2));
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
			Report report = new Report(code, title, template, processor, dataSource, query, beanCollection, table,
					Collections.unmodifiableList(joins), columns, filters, format, layout,
					new ReportParameters(parameters), columnFontName, columnFontSize, columnHeaderHeight, detailHeight,
					summationLegend, groupSummationLegend, dynamicDataSource, printColumnNames, underlineRows,
					shadeOddRows, landscape);
			return report;
		}
	}
}
