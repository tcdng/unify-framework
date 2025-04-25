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

package com.tcdng.unify.web.ui.widget.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.ColorUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Palette information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class PaletteInfo {

	private List<InkInfo> inkList;

	private List<String> names;
	
	private PaletteInfo(List<InkInfo> inkList) {
		this.inkList = inkList;
	}

	public List<InkInfo> getInkList() {
		return inkList;
	}

	public InkInfo getInkAt(int index) {
		return inkList.get(index);
	}
	
	public int size() {
		return inkList.size();
	}
	
	public List<String> getInkNames() {
		if (names == null) {
			synchronized(this) {
				if (names == null) {
					names = new ArrayList<String>();
					for (InkInfo inkInfo: inkList) {
						names.add(inkInfo.getName());
					}
				}
			}
		}
		
		return names;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private Map<String, InkInfo> inks;

		private Builder() {
			this.inks = new LinkedHashMap<String, InkInfo>();
		}

		public Builder addInk(String name, String color) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("Supplied ink name is invalid");
			}
			
			if (inks.containsKey(name)) {
				throw new IllegalArgumentException("Supplied ink name is already added.");
			}
			
			if (!ColorUtils.isValidHexColor(color)) {
				throw new IllegalArgumentException("Supplied ink color does not match hex color format");
			}
			
			inks.put(name, new InkInfo(name, color));
			return this;
		}

		public PaletteInfo build() throws UnifyException {
			return new PaletteInfo(Collections.unmodifiableList(new ArrayList<InkInfo>(inks.values())));
		}
	}

}
