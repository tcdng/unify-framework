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

import java.awt.Color;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.Bold;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.constant.VAlignType;
import com.tcdng.unify.core.constant.XOffsetType;
import com.tcdng.unify.core.constant.YOffsetType;
import com.tcdng.unify.core.report.ReportTheme.ThemeColors;

/**
 * A report placement.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ReportPlacement extends ReportField {

	private ReportPlacementType type;

	private ThemeColors colors;

	private String text;

	private XOffsetType xOffsetType;

	private YOffsetType yOffsetType;

	private int x;

	private int y;

	private int width;

	private int height;

	private ReportPlacement(ReportPlacementType type, ThemeColors colors, String text, String name, String className,
			String sqlBlobTypeName, String formatter, XOffsetType xOffsetType, YOffsetType yOffsetType, int x, int y,
			int width, int height, HAlignType hAlign, VAlignType vAlign, Bold bold) {
		super(name, className, sqlBlobTypeName, formatter, hAlign, vAlign, bold);
		this.type = type;
		this.xOffsetType = xOffsetType;
		this.yOffsetType = yOffsetType;
		this.colors = colors;
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public ThemeColors getColors() {
		return colors;
	}

	public ReportPlacementType getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public XOffsetType getXOffsetType() {
		return xOffsetType;
	}

	public YOffsetType getYOffsetType() {
		return yOffsetType;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isText() {
		return type.isText();
	}

	public boolean isField() {
		return type.isField();
	}

	public boolean isLine() {
		return type.isLine();
	}

	public boolean isRectangle() {
		return type.isRectangle();
	}

	public boolean isImage() {
		return type.isImage();
	}

	public static Builder newBuilder(ReportPlacementType type) {
		return new Builder(type);
	}

	public static class Builder {

		private ReportPlacementType type;

		private ThemeColors colors;

		private String name;

		private String className;

		private String formatter;

		private String sqlBlobTypeName;

		private String text;

		private XOffsetType xOffsetType;

		private YOffsetType yOffsetType;

		private int x;

		private int y;

		private int width;

		private int height;

		private HAlignType hAlign;

		private VAlignType vAlign;

		private Bold bold;

		private Builder(ReportPlacementType type) {
			this.colors = ReportTheme.DEFAULT_THEME.getDetailTheme();
			this.xOffsetType = XOffsetType.LEFT;
			this.yOffsetType = YOffsetType.TOP;
			this.type = type;
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

		public Builder xOffsetType(XOffsetType xOffsetType) {
			this.xOffsetType = xOffsetType;
			return this;
		}

		public Builder yOffsetType(YOffsetType yOffsetType) {
			this.yOffsetType = yOffsetType;
			return this;
		}

		public Builder position(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder dimension(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder bold(Bold bold) {
			this.bold = bold;
			return this;
		}

		public Builder text(String text) {
			this.text = text;
			return this;
		}

		public Builder formatter(String formatter) {
			this.formatter = formatter;
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

		public Builder colors(ThemeColors colors) {
			this.colors = colors;
			return this;
		}

		public Builder colors(Color fontColor, Color foreColor, Color backColor) {
			colors = new ThemeColors(fontColor, foreColor, backColor);
			return this;
		}

		public ReportPlacement build() throws UnifyException {
			return new ReportPlacement(type, colors, text, name, className, sqlBlobTypeName, formatter, xOffsetType,
					yOffsetType, x, y, width, height, hAlign, vAlign, bold);
		}
	}
}
