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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.HAlignType;

/**
 * A report placement.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ReportPlacement extends ReportField {

	private int x;

	private int y;

	private int width;

	private int height;

	private ReportPlacement(String name, String className, String sqlBlobTypeName, String formatter, int x, int y,
			int width, int height, HAlignType horizontalAlignment) {
		super(name, className, sqlBlobTypeName, formatter, horizontalAlignment);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	public static Builder newBuilder(String name) {
		return new Builder(name);
	}

	public static class Builder {

		private String name;

		private String className;

		private String formatter;

		private String sqlBlobTypeName;

		private int x;

		private int y;

		private int width;

		private int height;

		private HAlignType horizontalAlignment;

		private Builder(String name) {
			this.name = name;
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

		public Builder formatter(String formatter) {
			this.formatter = formatter;
			return this;
		}

		public Builder horizontalAlignment(HAlignType horizontalAlignment) {
			this.horizontalAlignment = horizontalAlignment;
			return this;
		}

		public ReportPlacement build() throws UnifyException {
			return new ReportPlacement(name, className, sqlBlobTypeName, formatter, x, y, width, height,
					horizontalAlignment);
		}
	}
}
