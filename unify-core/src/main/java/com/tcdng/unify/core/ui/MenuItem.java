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
package com.tcdng.unify.core.ui;

import java.util.Collections;
import java.util.List;

/**
 * Menu item data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class MenuItem {

	private String caption;

	private String privilege;

	private String actionPath;

	private List<MenuItem> menuItemList;

	public MenuItem(String caption, String privilege, String actionPath, List<MenuItem> menuItemList) {
		this.caption = caption;
		this.privilege = privilege;
		this.actionPath = actionPath;
		if (menuItemList != null) {
			this.menuItemList = Collections.unmodifiableList(menuItemList);
		}
	}

	public String getCaption() {
		return caption;
	}

	public String getPrivilege() {
		return privilege;
	}

	public String getActionPath() {
		return actionPath;
	}

	public List<MenuItem> getMenuItemList() {
		return menuItemList;
	}

	public boolean isMain() {
		return false;
	}
}
