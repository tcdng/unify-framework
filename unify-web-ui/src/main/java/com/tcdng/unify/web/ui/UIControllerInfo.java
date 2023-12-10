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
package com.tcdng.unify.web.ui;

import java.util.Map;
import java.util.Set;

import com.tcdng.unify.web.Action;
import com.tcdng.unify.web.ControllerInfo;
import com.tcdng.unify.web.ui.widget.PropertyInfo;

/**
 * User interface controller information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UIControllerInfo extends ControllerInfo {

	private Map<String, PropertyInfo> pageNameToPropertyMap;

	public UIControllerInfo(String controllerName, Map<String, Action> actionByNameMap,
			Map<String, PropertyInfo> pageNameToPropertyMap) {
		super(controllerName, actionByNameMap);
		this.pageNameToPropertyMap = pageNameToPropertyMap;
	}

	public Set<String> getPropertyIds() {
		return pageNameToPropertyMap.keySet();
	}

	public PropertyInfo getPropertyInfo(String pageName) {
		return pageNameToPropertyMap.get(pageName);
	}

	public void addPageNameToPropertyMappings(Map<String, PropertyInfo> pageNameToPropertyMap) {
		pageNameToPropertyMap.putAll(pageNameToPropertyMap);
	}
}
