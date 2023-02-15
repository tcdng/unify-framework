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

import java.awt.Color;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.Bold;
import com.tcdng.unify.core.constant.HAlignType;
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

	private int x;

	private int y;

	private int width;

	private int height;

	private ReportPlacement(ReportPlacementType type, ThemeColors colors, String name, String className,
			String sqlBlobTypeName, String formatter, int x, int y, int width, int height,
			HAlignType horizontalAlignment, Bold bold) {
		super(name, className, sqlBlobTypeName, formatter, horizontalAlignment, bold);
		this.type = type;
		this.colors = colors;
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

	public boolean isLine() {
		return type.isLine();
	}

	public boolean isRectangle() {
		return type.isRectangle();
	}

	public boolean isImage() {
		return type.isImage();
	}

	public static Builder newBuilder(ReportPlacementType type, String name) {
		return new Builder(type, name);
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

		private int x;

		private int y;

		private int width;

		private int height;

		private HAlignType horizontalAlignment;

		private Bold bold;

		private Builder(ReportPlacementType type, String name) {
			this.colors = ReportTheme.DEFAULT_THEME.getDetailTheme();
			this.type = type;
			this.name = name;
		}

		private Builder(ReportPlacementType type) {
			this.colors = ReportTheme.DEFAULT_THEME.getDetailTheme();
			this.type = type;
		}

		public Builder className(String className) {
			this.className = className;
			return this;
		}

		public Builder sqlBlobTypeName(String sqlBlobTypeName) {
			this.sqlBlobTypeName = sqlBlobTypeName;
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

		public Builder formatter(String formatter) {
			this.formatter = formatter;
			return this;
		}

		public Builder horizontalAlignment(HAlignType horizontalAlignment) {
			this.horizontalAlignment = horizontalAlignment;
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
			return new ReportPlacement(type, colors, name, className, sqlBlobTypeName, formatter, x, y, width, height,
					horizontalAlignment, bold);
		}
	}
}
