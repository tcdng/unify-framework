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

package com.tcdng.unify.web.ui.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.data.TreeInfo.MenuInfo;

/**
 * Tree item category info.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TreeItemCategoryInfo {

	private String name;

	private String icon;

	private Set<EventType> eventTypes;

	private List<MenuInfo> menuList;

	private TreeItemCategoryInfo(String name, String icon, Set<EventType> eventTypes, List<MenuInfo> menuList) {
		this.name = name;
		this.icon = icon;
		this.eventTypes = Collections.unmodifiableSet(eventTypes);
		this.menuList = Collections.unmodifiableList(menuList);
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public Set<EventType> getEventTypes() {
		return eventTypes;
	}

	public List<MenuInfo> getMenuList() {
		return menuList;
	}

	public boolean isMenu() {
		return !DataUtils.isBlank(menuList);
	}

	public static Builder newBuilder(String name) {
		return new Builder(name);
	}

	public static class Builder {

		private String name;

		private String icon;

		private Set<EventType> eventTypes;

		private Map<String, MenuInfo> menuList;

		private Builder(String name) {
			this.name = name;
			eventTypes = new HashSet<EventType>();
			menuList = new LinkedHashMap<String, MenuInfo>();
		}

		public Builder useIcon(String icon) {
			this.icon = icon;
			return this;
		}

		public Builder listenTo(EventType eventType) {
			this.eventTypes.add(eventType);

			return this;
		}

		/**
		 * Adds a menu item for category. Item is not visible on multiple selection.
		 * 
		 * @param code
		 *            the item code sent to event handler on click.
		 * @param caption
		 *            the item caption
		 * @return this builder
		 * @throws UnifyException
		 *             if menu with code already exists
		 */
		public Builder addMenuItem(String code, String caption) throws UnifyException {
			return addMenuItem(code, caption, false);
		}

		/**
		 * Adds a menu item for category.
		 * 
		 * @param code
		 *            the item code sent to event handler on click.
		 * @param caption
		 *            the item caption
		 * @param separator
		 *            the separator flag
		 * @return this builder
		 * @throws UnifyException
		 *             if menu with code already exists
		 */
		public Builder addMenuItem(String code, String caption, boolean separator) throws UnifyException {
			return addMenuItem(code, caption, separator, false);
		}

		/**
		 * Adds a menu item for category with separator option.
		 * 
		 * @param code
		 *            the item code sent to event handler on click.
		 * @param caption
		 *            the item caption
		 * @param separator
		 *            the separator flag
		 * @param showOnMultiple
		 *            Indicates menu item should be visible on multiple selection
		 * @return this builder
		 * @throws UnifyException
		 *             if menu with code already exists
		 */
		public Builder addMenuItem(String code, String caption, boolean separator, boolean showOnMultiple)
				throws UnifyException {
			if (menuList.containsKey(code)) {
				throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
						"Menu item with code [" + code + "] exists.");
			}

			listenTo(EventType.MOUSE_RIGHTCLICK);
			menuList.put(code, new MenuInfo(code, caption, showOnMultiple, separator));
			return this;
		}

		public TreeItemCategoryInfo build() {
			return new TreeItemCategoryInfo(this.name, this.icon, this.eventTypes,
					new ArrayList<MenuInfo>(this.menuList.values()));
		}
	}
}
