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
package com.tcdng.unify.web.ui;

/**
 * List control data.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ListControlJsonData {

	private String jsonSelectIds;

	private String jsonKeys;

	private String jsonLabels;

	private String valueLabel;

	private int valueIndex;

	private int size;

	public ListControlJsonData(String jsonSelectIds, String jsonKeys, String jsonLabels, String valueLabel,
			int valueIndex, int size) {
		this.jsonSelectIds = jsonSelectIds;
		this.jsonKeys = jsonKeys;
		this.jsonLabels = jsonLabels;
		this.valueLabel = valueLabel;
		this.valueIndex = valueIndex;
		this.size = size;
	}

	public String getJsonSelectIds() {
		return jsonSelectIds;
	}

	public String getJsonKeys() {
		return jsonKeys;
	}

	public String getJsonLabels() {
		return jsonLabels;
	}

	public String getValueLabel() {
		return valueLabel;
	}

	public int getValueIndex() {
		return valueIndex;
	}

	public int getSize() {
		return size;
	}

}
