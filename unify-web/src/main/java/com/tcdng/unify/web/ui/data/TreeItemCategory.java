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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Tree item category.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TreeItemCategory {

    private String name;

    private String icon;

    private String itemCaptionKey;

    private Set<EventType> eventTypes;

    private List<TreeMenuItem> menuList;

    private List<List<Character>> menuModeStateList;

    private Map<String, Integer> menuModeIndexes;

    private int level;

    private TreeItemCategory(String name, String icon, String itemCaptionKey, Set<EventType> eventTypes,
            List<TreeMenuItem> menuList, List<List<Character>> menuModeStateList, Map<String, Integer> menuModeIndexes,
            int level) {
        this.name = name;
        this.icon = icon;
        this.itemCaptionKey = itemCaptionKey;
        this.eventTypes = eventTypes;
        this.menuList = menuList;
        this.menuModeStateList = menuModeStateList;
        this.menuModeIndexes = menuModeIndexes;
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

    public List<TreeMenuItem> getMenuList() {
        return menuList;
    }

    public boolean isMenuList() {
        return menuList != null && !menuList.isEmpty();
    }

    public Integer getMenuModeIndex(String menuMode) {
        return menuModeIndexes.get(menuMode);
    }

    public List<List<Character>> getMenuModeStateList() {
        return menuModeStateList;
    }

    public int getLevel() {
        return level;
    }

    public static Builder newBuilder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private String name;

        private String icon;

        private String itemCaptionKey;

        private Set<EventType> eventTypes;

        private Map<String, TreeMenuItem> menuList;

        private Map<String, Set<String>> menuModes;

        private int level;

        private Builder(String name) {
            this.name = name;
            eventTypes = new HashSet<EventType>();
            menuList = new LinkedHashMap<String, TreeMenuItem>();
            menuModes = new HashMap<String, Set<String>>();
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
            if (menuList != null && menuList.containsKey(code)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Menu item with code [" + code + "] exists.");
            }

            listenTo(EventType.MOUSE_RIGHTCLICK);
            getMenuList().put(code, new TreeMenuItem(code, caption, showOnMultiple, separator));
            return this;
        }

        public Builder addMenuMode(String code, String... hiddenItems) throws UnifyException {
            if (menuModes != null && menuModes.containsKey(code)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Menu mode with code [" + code + "] exists.");
            }

            Set<String> items = new HashSet<String>();
            for (String hiddenItem : hiddenItems) {
                if (menuList == null || !getMenuList().containsKey(hiddenItem)) {
                    throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                            "Menu item with code [" + hiddenItem + "] is unknown.");
                }

                items.add(hiddenItem);
            }

            getMenuModes().put(code, items);
            return this;
        }

        public TreeItemCategory build() {
            List<TreeMenuItem> actMenuList = Collections.emptyList();
            if (menuList != null) {
                actMenuList = Collections.unmodifiableList(new ArrayList<TreeMenuItem>(menuList.values()));
            }

            List<List<Character>> actMenuModeStateList = Collections.emptyList();
            Map<String, Integer> actMenuModeIndexes = Collections.emptyMap();
            if (menuModes != null) {
                actMenuModeStateList = new ArrayList<List<Character>>();
                actMenuModeIndexes = new HashMap<String, Integer>();
                int index = 0;
                for (Map.Entry<String, Set<String>> entry : menuModes.entrySet()) {
                    List<Character> states = new ArrayList<Character>(actMenuList.size());
                    for (TreeMenuItem treeMenuItem : actMenuList) {
                        if (entry.getValue().contains(treeMenuItem.getCode())) {
                            states.add(Character.valueOf('0'));
                        } else {
                            states.add(Character.valueOf('1'));
                        }
                    }

                    actMenuModeStateList.add(Collections.unmodifiableList(states));
                    actMenuModeIndexes.put(entry.getKey(), index++);
                }

                actMenuModeStateList = Collections.unmodifiableList(actMenuModeStateList);
                actMenuModeIndexes = Collections.unmodifiableMap(actMenuModeIndexes);
            }

            return new TreeItemCategory(name, icon, itemCaptionKey, Collections.unmodifiableSet(eventTypes),
                    actMenuList, actMenuModeStateList, actMenuModeIndexes, level);
        }

        private Map<String, TreeMenuItem> getMenuList() {
            if (menuList == null) {
                menuList = new LinkedHashMap<String, TreeMenuItem>();
            }

            return menuList;
        }

        private Map<String, Set<String>> getMenuModes() {
            if (menuModes == null) {
                menuModes = new HashMap<String, Set<String>>();
            }

            return menuModes;
        }
    }
}
