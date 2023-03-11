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
package com.tcdng.unify.web.ui.widget.data;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Color legend information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ColorLegendInfo {

	private List<ColorLegendItem> items;
	
	private ColorLegendInfo(List<ColorLegendItem> items) {
		this.items = items;
	}

	public List<ColorLegendItem> getItems() {
		return items;
	}
	
	public ColorLegendItem getItem(int index) {
		return items.get(index);
	}
	
	public int size() {
		return items.size();
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {

		private List<ColorLegendItem> items;
		
		public Builder() {
			this.items = new ArrayList<ColorLegendItem>();
		}
		
		public Builder addItem(String color, String label) {
			if (StringUtils.isBlank(color) || StringUtils.isBlank(label)) {
				throw new IllegalArgumentException();
			}
			
			items.add(new ColorLegendItem(color, label));
			return this;
		}
		
		public ColorLegendInfo build() {
			return new ColorLegendInfo(DataUtils.unmodifiableList(items));
		}
	}
}
