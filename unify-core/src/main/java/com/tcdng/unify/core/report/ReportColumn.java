/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.Bold;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.constant.VAlignType;

/**
 * A report column.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ReportColumn extends ReportField {

	private String title;

	private String table;

	private OrderType order;

	private int widthRatio;

	private boolean group;

	private boolean groupOnNewPage;

	private boolean sum;

	private ReportColumn(String title, String table, String name, String className, String sqlBlobTypeName,
			String formatterUpl, OrderType order, HAlignType hAlign, VAlignType vAlign, int widthRatio, Bold bold,
			boolean group, boolean groupOnNewPage, boolean sum) {
		super(name, className, sqlBlobTypeName, formatterUpl, hAlign, vAlign, bold);
		this.title = title;
		this.table = table;
		this.widthRatio = widthRatio;
		this.order = order;
		this.group = group;
		this.groupOnNewPage = groupOnNewPage;
		this.sum = sum;
	}

	public String getTitle() {
		return title;
	}

	public String getTable() {
		return table;
	}

	public int getWidthRatio() {
		return widthRatio;
	}

	public OrderType getOrder() {
		return order;
	}

	public boolean isGroup() {
		return group;
	}

	public boolean isGroupOnNewPage() {
		return groupOnNewPage;
	}

	public boolean isSum() {
		return sum;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private String title;

		private String table;

		private String name;

		private String className;

		private String sqlBlobTypeName;

		private String formatterUpl;

		private OrderType order;

		private HAlignType hAlign;

		private VAlignType vAlign;

		private int widthRatio;

		private Bold bold;

		private boolean group;

		private boolean groupOnNewPage;

		private boolean sum;

		private Builder() {

		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder table(String table) {
			this.table = table;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder className(String className) {
			this.className = className;
			return this;
		}

		public Builder sqlBlobTypeName(String sqlBlobTypeName) {
			this.sqlBlobTypeName = sqlBlobTypeName;
			return this;
		}

		public Builder formatter(String formatterUpl) {
			this.formatterUpl = formatterUpl;
			return this;
		}

		public Builder hAlign(HAlignType hAlign) {
			this.hAlign = hAlign;
			return this;
		}

		public Builder vAlign(VAlignType vAlign) {
			this.vAlign = vAlign;
			return this;
		}

		public Builder widthRatio(int widthRatio) {
			this.widthRatio = widthRatio;
			return this;
		}

		public Builder order(OrderType order) {
			this.order = order;
			return this;
		}

		public Builder group(boolean group) {
			this.group = group;
			return this;
		}

		public Builder groupOnNewPage(boolean groupOnNewPage) {
			this.groupOnNewPage = groupOnNewPage;
			return this;
		}

		public Builder sum(boolean sum) {
			this.sum = sum;
			return this;
		}

		public Builder bold(Bold bold) {
			this.bold = bold;
			return this;
		}

		public ReportColumn build() throws UnifyException {
			ReportColumn reportColumn = new ReportColumn(title, table, name, className, sqlBlobTypeName, formatterUpl,
					order, hAlign, vAlign, widthRatio, bold, group, groupOnNewPage, sum);
			return reportColumn;
		}
	}
}
