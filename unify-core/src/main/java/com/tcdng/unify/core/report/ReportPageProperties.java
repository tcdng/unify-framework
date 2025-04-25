/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.constant.PageSizeType;

/**
 * Report page properties.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ReportPageProperties {

	public static final ReportPageProperties DEFAULT = ReportPageProperties.newBuilder().build();

	private PageSizeType size;

	private String resourceBaseUri;

	private int pageWidth;

	private int pageHeight;

	private int marginTop;

	private int marginBottom;

	private int marginLeft;

	private int marginRight;

	private boolean landscape;

	private ReportPageProperties(PageSizeType size, String resourceBaseUri, int pageWidth, int pageHeight,
			int marginTop, int marginBottom, int marginLeft, int marginRight, boolean landscape) {
		this.size = size;
		this.resourceBaseUri = resourceBaseUri;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.landscape = landscape;
	}

	public PageSizeType getSize() {
		return size;
	}

	public String getResourceBaseUri() {
		return resourceBaseUri;
	}

	public int getPageWidth() {
		return pageWidth;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public int getMarginTop() {
		return marginTop;
	}

	public int getMarginBottom() {
		return marginBottom;
	}

	public int getMarginLeft() {
		return marginLeft;
	}

	public int getMarginRight() {
		return marginRight;
	}

	public boolean isLandscape() {
		return landscape;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private PageSizeType size;

		private String resourceBaseUri;

		private int pageWidth;

		private int pageHeight;

		private int marginTop;

		private int marginBottom;

		private int marginLeft;

		private int marginRight;

		private boolean landscape;

		public Builder() {
			this.size = PageSizeType.A4;
			this.pageWidth = -1;
			this.pageHeight = -1;
			this.landscape = false;
		}

		public Builder size(PageSizeType size) {
			this.size = size;
			return this;
		}

		public Builder resourceBaseUri(String resourceBaseUri) {
			this.resourceBaseUri = resourceBaseUri;
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

		public Builder allMargin(int margin) {
			this.marginTop = margin;
			this.marginBottom = margin;
			this.marginLeft = margin;
			this.marginRight = margin;
			return this;
		}

		public Builder marginTop(int marginTop) {
			this.marginTop = marginTop;
			return this;
		}

		public Builder marginBottom(int marginBottom) {
			this.marginBottom = marginBottom;
			return this;
		}

		public Builder marginLeft(int marginLeft) {
			this.marginLeft = marginLeft;
			return this;
		}

		public Builder marginRight(int marginRight) {
			this.marginRight = marginRight;
			return this;
		}

		public Builder landscape(boolean landscape) {
			this.landscape = landscape;
			return this;
		}

		public ReportPageProperties build() {
			return new ReportPageProperties(size, resourceBaseUri, pageWidth, pageHeight, marginTop, marginBottom,
					marginLeft, marginRight, landscape);
		}
	}
}
