/*
 * Copyright 2018-2024 The Code Department.
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

import com.tcdng.unify.core.data.ValueStoreReader;

/**
 * Button group information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ButtonGroupInfo {

	private ValueStoreReader parentReader;
	
	private List<ButtonInfo> infoList;

	private ButtonGroupInfo(List<ButtonInfo> infoList) {
		this.infoList = infoList;
	}
	
	public ValueStoreReader getParentReader() {
		return parentReader;
	}

	public void setParentReader(ValueStoreReader parentReader) {
		this.parentReader = parentReader;
	}

	public boolean isWithParentReader() {
		return parentReader != null;
	}
	
	public List<ButtonInfo> getInfoList() {
		return infoList;
	}

	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private List<ButtonInfo> infoList;
		
		public Builder() {
			this.infoList = new ArrayList<ButtonInfo> ();
		}
		
		public Builder addItems(List<ButtonInfo> infoList) {
			this.infoList.addAll(infoList);
			return this;
		}
		
		public Builder addItem(ButtonInfo buttonInfo) {
			infoList.add(buttonInfo);
			return this;
		}
		
		public Builder addItem(String value, String label) {
			infoList.add(new ButtonInfo(value, label));
			return this;
		}
		
		public ButtonGroupInfo build() {
			return new ButtonGroupInfo(new ArrayList<ButtonInfo>(infoList));
		}
	}
}
