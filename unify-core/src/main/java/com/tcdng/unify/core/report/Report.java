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
package com.tcdng.unify.core.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.database.sql.SqlJoinType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Used to define a report for generation at runtime.
 * 
 * @author The Code Department
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

	private ReportLayoutType layout;

	private List<?> beanCollection;

	private List<ReportTableJoin> joins;

	private List<ReportColumn> columns;

	private List<ReportHtml> embeddedHtmls;

	private ReportTable table;

	private ReportFilter filter;

	private ReportFormat format;

	private ReportParameters reportParameters;

	private ReportTheme reportTheme;

	private ReportPageProperties pageProperties;

	private String summationLegend;

	private String groupSummationLegend;

	private boolean dynamicDataSource;

	private boolean printColumnNames;

	private boolean printGroupColumnNames;

	private boolean invertGroupColors;

	private boolean showParameterHeader;

	private boolean showGrandFooter;

	private boolean underlineRows;

	private boolean shadeOddRows;

	private Report(String code, String title, String template, String processor, String dataSource, String query,
			String theme, List<?> beanCollection, ReportTable table, List<ReportTableJoin> joins,
			List<ReportColumn> columns, List<ReportHtml> embeddedHtmls, ReportFilter filter, ReportFormat format,
			ReportLayoutType layout, ReportParameters reportParameters, ReportPageProperties pageProperties,
			String summationLegend, String groupSummationLegend, boolean dynamicDataSource, boolean printColumnNames,
			boolean printGroupColumnNames, boolean invertGroupColors, boolean showParameterHeader,
			boolean showGrandFooter, boolean underlineRows, boolean shadeOddRows) {
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
		this.embeddedHtmls = embeddedHtmls;
		this.filter = filter;
		this.format = format;
		this.layout = layout;
		this.reportParameters = reportParameters;
		this.pageProperties = pageProperties;
		this.summationLegend = summationLegend;
		this.groupSummationLegend = groupSummationLegend;
		this.dynamicDataSource = dynamicDataSource;
		this.printColumnNames = printColumnNames;
		this.printGroupColumnNames = printGroupColumnNames;
		this.invertGroupColors = invertGroupColors;
		this.showParameterHeader = showParameterHeader;
		this.showGrandFooter = showGrandFooter;
		this.underlineRows = underlineRows;
		this.shadeOddRows = shadeOddRows;
	}

	public String getCode() {
		return code;
	}

	public ReportFormat getFormat() {
		return format;
	}

	public ReportLayoutType getLayout() {
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

	public void setBeanCollection(List<?> beanCollection) {
		this.beanCollection = beanCollection;
	}

	public List<?> getBeanCollection() {
		return beanCollection;
	}

	public ReportTheme getReportTheme() {
		return reportTheme;
	}

	public void setReportTheme(ReportTheme reportTheme) {
		this.reportTheme = reportTheme;
	}

	public ReportPageProperties getPageProperties() {
		return pageProperties;
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

	public boolean isInvertGroupColors() {
		return invertGroupColors;
	}

	public boolean isShowParameterHeader() {
		return showParameterHeader;
	}

	public boolean isShowGrandFooter() {
		return showGrandFooter;
	}

	public boolean isUnderlineRows() {
		return underlineRows;
	}

	public boolean isShadeOddRows() {
		return shadeOddRows;
	}

	public boolean isGenerated() {
		return !columns.isEmpty() || isEmbeddedHtml();
	}

	public boolean isMultiDocHtmlToPDF() {
		return ReportLayoutType.MULTIDOCHTML_PDF.equals(layout);
	}

	public boolean isEmbeddedHtml() {
		return ReportLayoutType.SINGLECOLUMN_EMBEDDED_HTML.equals(layout);
	}

	public boolean isWithBeanCollection() {
		return beanCollection != null;
	}

	public boolean isQuery() {
		return query != null;
	}

	public ReportParameters getReportParameters() {
		return reportParameters;
	}

	public Map<String, Object> getParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (reportParameters != null) {
			parameters.putAll(reportParameters.getParameterValues());
		}

		return parameters;
	}

	public void setReportParameters(ReportParameters reportParameters) {
		this.reportParameters = reportParameters;
	}

	public void setParameter(String name, String description, Object value) {
		setParameter(name, description, null, value, false, false);
	}

	public void setParameter(String name, String description, String formatter, Object value, boolean headerDetail,
			boolean footerDetail) {
		setParameter(new ReportParameter(name, description, formatter, value, headerDetail, footerDetail));
	}

	public void setParameter(ReportParameter reportParameter) {
		reportParameters.addParameter(reportParameter);
	}

	public boolean isParameter(String name) {
		return reportParameters.isParameter(name);
	}

	public ReportParameter getParameter(String name) {
		return reportParameters.getParameter(name);
	}

	public Object getParameterValue(String name) {
		ReportParameter reportParameter = reportParameters.getParameter(name);
		if (reportParameter != null) {
			return reportParameter.getValue();
		}

		return null;
	}

	public List<ReportTableJoin> getJoins() {
		return joins;
	}

	public List<ReportColumn> getColumns() {
		return DataUtils.unmodifiableList(columns);
	}

	public List<ReportHtml> getEmbeddedHtmls() {
		return embeddedHtmls;
	}

	public ReportFilter getFilter() {
		return filter;
	}

	public void addColumn(ReportColumn reportColumn) {
		columns.add(reportColumn);
	}

	public static Builder newBuilder(ReportLayoutType layout, ReportPageProperties pageProperties) {
		return new Builder(layout, pageProperties);
	}

	public static Builder newBuilder(ReportPageProperties pageProperties) {
		return new Builder(ReportLayoutType.TABULAR, pageProperties);
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

		private ReportFilter rootFilter;

		private ReportFormat format;

		private ReportParameters reportParameters;

		private List<?> beanCollection;

		private List<ReportTableJoin> joins;

		private List<ReportColumn> columns;

		private Map<String, ReportHtml> embeddedHtmls;

		private Stack<ReportFilter> filters;

		private ReportLayoutType layout;

		private ReportPageProperties pageProperties;

		private String summationLegend;

		private String groupSummationLegend;

		private boolean dynamicDataSource;

		private boolean printColumnNames;

		private boolean printGroupColumnNames;

		private boolean invertGroupColors;

		private boolean showParameterHeader;

		private boolean showGrandFooter;

		private boolean underlineRows;

		private boolean shadeOddRows;

		private Builder(ReportLayoutType layout, ReportPageProperties pageProperties) {
			this.format = ReportFormat.PDF;
			this.layout = layout;
			this.pageProperties = pageProperties;
			this.reportParameters = new ReportParameters();
			this.joins = new ArrayList<ReportTableJoin>();
			this.columns = new ArrayList<ReportColumn>();
			this.embeddedHtmls = new LinkedHashMap<String, ReportHtml>();
			this.filters = new Stack<ReportFilter>();
			this.printColumnNames = true;
			this.printGroupColumnNames = true;
			this.showParameterHeader = true;
			this.showGrandFooter = false;
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

		public Builder beanCollection(List<?> beanCollection) {
			this.beanCollection = beanCollection;
			return this;
		}

		public Builder format(ReportFormat format) {
			this.format = format;
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

		public Builder invertGroupColors(boolean invertGroupColors) {
			this.invertGroupColors = invertGroupColors;
			return this;
		}

		public Builder showParameterHeader(boolean showParameterHeader) {
			this.showParameterHeader = showParameterHeader;
			return this;
		}

		public Builder showGrandFooter(boolean showGrandFooter) {
			this.showGrandFooter = showGrandFooter;
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

		public Builder addCompleteHtml(String name, String completeHtml) {
			ReportHtml reportHtml = ReportHtml.newBuilder(pageProperties, name).completeHtml(completeHtml).build();
			return addReportHtml(reportHtml);
		}

		public Builder addBodyContentHtml(String name, String style, String bodyContent) {
			ReportHtml reportHtml = ReportHtml.newBuilder(pageProperties, name).style(style).bodyContent(bodyContent)
					.build();
			return addReportHtml(reportHtml);
		}

		public Builder addReportHtml(ReportHtml reportEmbeddedHtml) {
			if (embeddedHtmls.containsKey(reportEmbeddedHtml.getName())) {
				throw new IllegalArgumentException(
						"Embedded HTML with name [" + reportEmbeddedHtml.getName() + "] already added to this report.");
			}

			embeddedHtmls.put(reportEmbeddedHtml.getName(), reportEmbeddedHtml);
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

		public Builder addColumn(String title, String table, String name, String className, String sqlBlobTypeName,
				String formatterUpl, OrderType order, HAlignType hAlignType, int widthRatio, boolean group,
				boolean groupOnNewPage, boolean sum) throws UnifyException {
			ReportColumn rc = ReportColumn.newBuilder().title(title).table(table).name(name).className(className)
					.horizontalAlignment(hAlignType).widthRatio(widthRatio).sqlBlobTypeName(sqlBlobTypeName)
					.formatter(formatterUpl).order(order).group(group).groupOnNewPage(groupOnNewPage).sum(sum).build();
			columns.add(rc);
			return this;
		}

		public Builder beginCompoundFilter(RestrictionType op) {
			if (!op.isCompound()) {
				throw new IllegalArgumentException(op + " is not a compound restriction type.");
			}

			if (rootFilter != null) {
				throw new IllegalStateException("Can not have multiple rootPolicies compound filter.");
			}

			ReportFilter reportFilter = new ReportFilter(op);
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

		public Builder addParameter(ReportParameter reportParameter) {
			reportParameters.addParameter(reportParameter);
			return this;
		}

		public Builder addParameter(String name, String description, Object value) {
			return addParameter(name, description, null, value, false, false);
		}

		public Builder addParameter(String name, String description, String formatter, Object value,
				boolean headerDetail, boolean footerDetail) {
			reportParameters
					.addParameter(new ReportParameter(name, description, formatter, value, headerDetail, footerDetail));
			return this;
		}

		public Report build() throws UnifyException {
			if (rootFilter == null) {
				rootFilter = new ReportFilter(RestrictionType.AND);
			}

			Report report = new Report(code, title, template, processor, dataSource, query, theme, beanCollection,
					table, Collections.unmodifiableList(joins), columns,
					DataUtils.unmodifiableList(embeddedHtmls.values()), rootFilter, format, layout, reportParameters,
					pageProperties, summationLegend, groupSummationLegend, dynamicDataSource, printColumnNames,
					printGroupColumnNames, invertGroupColors, showParameterHeader, showGrandFooter, underlineRows,
					shadeOddRows);
			return report;
		}
	}
}
