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
package com.tcdng.unify.core.data;

/**
 * Indexed target.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class IndexedTarget {

	public static final IndexedTarget BLANK = new IndexedTarget("", "", -1, -1);

	private final String target;

	private final String binding;

	private final int valueIndex;

	private final int tabIndex;

	public IndexedTarget(String target, String binding, int valueIndex, int tabIndex) {
		this.target = target;
		this.binding = binding;
		this.valueIndex = valueIndex;
		this.tabIndex = tabIndex;
	}

	public String getTarget() {
		return target;
	}

	public String getBinding() {
		return binding;
	}

	public int getValueIndex() {
		return valueIndex;
	}

	public boolean isValidValueIndex() {
		return valueIndex >= 0;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public boolean isValidTabIndex() {
		return tabIndex >= 0;
	}
}
