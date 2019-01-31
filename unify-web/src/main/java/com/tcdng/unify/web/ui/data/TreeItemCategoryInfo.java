/*
 * Copyright 2018-2019 The Code Department.
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

    private String itemCaptionKey;

    private Set<EventType> eventTypes;

    private List<MenuInfo> menuList;

    private int level;

    private TreeItemCategoryInfo(String name, String icon, String itemCaptionKey, Set<EventType> eventTypes,
            List<MenuInfo> menuList, int level) {
        this.name = name;
        this.icon = icon;
        this.itemCaptionKey = itemCaptionKey;
        this.eventTypes = Collections.unmodifiableSet(eventTypes);
        this.menuList = Collections.unmodifiableList(menuList);
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getItemCaptionKey() {
        return itemCaptionKey;
    }

    public Set<EventType> getEventTypes() {
        return eventTypes;
    }

    public List<MenuInfo> getMenuList() {
        return menuList;
    }

    public int getLevel() {
        return level;
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

        private String itemCaptionKey;

        private Set<EventType> eventTypes;

        private Map<String, MenuInfo> menuList;

        private int level;

        private Builder(String name) {
            this.name = name;
            eventTypes = new HashSet<EventType>();
            menuList = new LinkedHashMap<String, MenuInfo>();
        }

        public Builder useIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder useItemCaptionKey(String itemCaptionKey) {
            this.itemCaptionKey = itemCaptionKey;
            return this;
        }

        public Builder atLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder listenTo(EventType eventType) {
            eventTypes.add(eventType);
            return this;
        }

        public Builder addMenuItem(String code, String caption) throws UnifyException {
            return addMenuItem(code, caption, false);
        }

        public Builder addMenuItem(String code, String caption, boolean separator) throws UnifyException {
            return addMenuItem(code, caption, separator, false);
        }

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
            return new TreeItemCategoryInfo(name, icon, itemCaptionKey, eventTypes,
                    new ArrayList<MenuInfo>(menuList.values()), level);
        }
    }
}
