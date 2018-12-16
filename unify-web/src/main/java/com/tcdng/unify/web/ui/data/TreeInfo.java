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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Tree information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TreeInfo<T> {

    private Map<String, TreeItemCategoryInfo> categories;

    private List<MenuInfo> menuList;

    private List<TreeItemInfo<T>> selectedItems;

    private TreeItemInfo<T>[] treeItems;

    private Map<Object, TreeItemInfo<T>> treeItemsById;

    private Queue<TreeEvent<T>> eventQueue;

    private TreeInfo(Map<String, TreeItemCategoryInfo> categories, TreeItemInfo<T>[] treeItems,
            Map<Object, TreeItemInfo<T>> treeItemsById, List<MenuInfo> menuList) {
        this.categories = categories;
        this.treeItems = treeItems;
        this.treeItemsById = treeItemsById;
        this.menuList = menuList;
        selectedItems = Collections.emptyList();
        eventQueue = new LinkedList<TreeEvent<T>>();
        evaluate();
    }

    public TreeItemCategoryInfo getTreeCategoryInfo(String name) {
        return categories.get(name);
    }

    public Collection<TreeItemCategoryInfo> getTreeCategoryInfos() {
        return categories.values();
    }

    public TreeItemInfo<T> getTreeItemInfo(int index) {
        return treeItems[index];
    }

    public int itemCount() {
        return treeItems.length;
    }

    public List<TreeItemInfo<T>> getSelectedItems() {
        return selectedItems;
    }

    public List<MenuInfo> getMenuList() {
        return menuList;
    }

    public boolean isMenu() {
        return !DataUtils.isBlank(menuList);
    }

    public int setSelectedItem(Object id) {
        TreeItemInfo<T> treeItemInfo = treeItemsById.get(id);
        if (treeItemInfo != null) {
            selectedItems = new ArrayList<TreeItemInfo<T>>();
            selectedItems.add(treeItemInfo);
            expandParents(treeItemInfo);
            evaluate();
            return treeItemInfo.getIndex();
        }
        selectedItems = Collections.emptyList();
        return -1;

    }

    public void setSelectedItems(List<Integer> selectIndexes) {
        if (!DataUtils.isBlank(selectIndexes)) {
            selectedItems = new ArrayList<TreeItemInfo<T>>(selectIndexes.size());
            for (Integer i : selectIndexes) {
                selectedItems.add(treeItems[i]);
            }
        } else {
            selectedItems = Collections.emptyList();
        }
    }

    public void registerEvent(EventType type, String menuCode) {
        eventQueue.offer(new TreeEvent<T>(type, menuCode, selectedItems));
    }

    public TreeEvent<T> getEvent() {
        return eventQueue.poll();
    }

    public void reflectOn(TreeInfo<T> targetTreeInfo, boolean evaluate) {
        if (treeItemsById != null && targetTreeInfo.treeItemsById != null) {
            for (Map.Entry<Object, TreeItemInfo<T>> entry : targetTreeInfo.treeItemsById.entrySet()) {
                TreeItemInfo<T> thisItem = treeItemsById.get(entry.getKey());
                if (thisItem != null) {
                    TreeItemInfo<T> targetItem = entry.getValue();
                    targetItem.setExpanded(thisItem.isExpanded());
                    targetItem.setHidden(thisItem.isHidden());
                }
            }

            if (evaluate) {
                targetTreeInfo.evaluate();
            }
        }
    }

    public void collapseAll() {
        for (TreeItemInfo<T> treeItemInfo : treeItems) {
            treeItemInfo.setExpanded(false);
        }

        evaluate();
    }

    public void expandAll() {
        for (TreeItemInfo<T> treeItemInfo : treeItems) {
            treeItemInfo.setExpanded(true);
        }
        evaluate();
    }

    public boolean collapse(int itemIndex) {
        return setExpand(itemIndex, false);
    }

    public boolean expand(int itemIndex) {
        return setExpand(itemIndex, true);
    }

    private boolean setExpand(int itemIndex, boolean expand) {
        if (itemIndex >= 0 && itemIndex < treeItems.length) {
            treeItems[itemIndex].setExpanded(expand);
            evaluate();
            return true;
        }
        return false;
    }

    private void expandParents(TreeItemInfo<T> treeItemInfo) {
        TreeItemInfo<T> parent = treeItemInfo;
        while ((parent = getParent(parent)) != null) {
            parent.setExpanded(true);
        }
    }

    private TreeItemInfo<T> getParent(TreeItemInfo<T> treeItemInfo) {
        int i = treeItemInfo.getIndex();
        int depth = treeItemInfo.getDepth();
        while (--i >= 0) {
            TreeItemInfo<T> topTreeItemInfo = treeItems[i];
            if (topTreeItemInfo.getDepth() < depth) {
                return topTreeItemInfo;
            }
        }
        return null;
    }

    private void evaluate() {
        // Evaluate and set visibility
        TreeItemInfo<T> mainCollapsed = null;
        for (TreeItemInfo<T> treeItemInfo : treeItems) {
            boolean hidden = false;
            if (!(hidden = mainCollapsed != null && mainCollapsed.getDepth() < treeItemInfo.getDepth())) {
                if (treeItemInfo.isExpanded()) {
                    mainCollapsed = null;
                } else {
                    mainCollapsed = treeItemInfo;
                }
            }
            treeItemInfo.setHidden(hidden);
        }
    }

    public static <U> Builder<U> newBuilder() {
        return new Builder<U>();
    }

    public static <U> Builder<U> newBuilder(TreeInfo<U> refTreeInfo) {
        return new Builder<U>(refTreeInfo);
    }

    public static class Builder<T> {

        private Map<String, MenuInfo> menuList;

        private Map<String, TreeItemCategoryInfo> categories;

        private List<TreeItemInfo<T>> treeItemList;

        private TreeItemIdentifier<T> identifier;

        private int depth;

        private Builder() {
            this(null);
        }

        private Builder(TreeInfo<T> refTreeInfo) {
            menuList = new LinkedHashMap<String, MenuInfo>();
            if (refTreeInfo != null) {
                categories = new HashMap<String, TreeItemCategoryInfo>(refTreeInfo.categories);
            } else {
                categories = new HashMap<String, TreeItemCategoryInfo>();
            }
            treeItemList = new ArrayList<TreeItemInfo<T>>();
        }

        public Builder<T> addCategory(TreeItemCategoryInfo treeCategoryInfo) throws UnifyException {
            if (categories.containsKey(treeCategoryInfo.getName())) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Category with name [" + treeCategoryInfo.getName() + "] exists.");
            }

            categories.put(treeCategoryInfo.getName(), treeCategoryInfo);
            return this;
        }

        public Builder<T> useIdentifier(TreeItemIdentifier<T> identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder<T> addTreeItem(String categoryName, String caption) throws UnifyException {
            return addTreeItem(categoryName, caption, null);
        }

        public Builder<T> addTreeItem(String categoryName, String caption, T item) throws UnifyException {
            TreeItemInfo<T> lastTreeItemInfo = getLastTreeItemInfo();
            if (lastTreeItemInfo != null && lastTreeItemInfo.getDepth() < depth) {
                lastTreeItemInfo.setParent(true);
            }

            if (!categories.containsKey(categoryName)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Unknown category [" + categoryName + "].");
            }

            treeItemList
                    .add(new TreeItemInfo<T>(categories.get(categoryName), item, caption, treeItemList.size(), depth));
            return this;
        }

        public boolean isCategory(String name) {
            return categories.containsKey(name);
        }

        /**
         * Adds a menu item for general tree menu.
         * 
         * @param code
         *            the item code sent to event handler on click.
         * @param caption
         *            the item caption
         * @return this builder
         * @throws UnifyException
         *             if menu with code already exists
         */
        public Builder<T> addMenuItem(String code, String caption) throws UnifyException {
            return addMenuItem(code, caption, false);
        }

        /**
         * Adds a menu item with separator option for general tree menu.
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
        public Builder<T> addMenuItem(String code, String caption, boolean separator) throws UnifyException {
            if (menuList.containsKey(code)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Menu item with code [" + code + "] exists.");
            }

            menuList.put(code, new MenuInfo(code, caption, true, separator));
            return this;
        }

        public boolean descend() {
            TreeItemInfo<T> lastTreeItemInfo = getLastTreeItemInfo();
            if (lastTreeItemInfo != null && depth == lastTreeItemInfo.getDepth()) {
                depth++;
                return true;
            }

            return false;
        }

        public boolean ascend() {
            if (depth > 0) {
                depth--;
                return true;
            }

            return false;
        }

        public int getDepth() {
            return depth;
        }

        @SuppressWarnings("unchecked")
        public TreeInfo<T> build() throws UnifyException {
            Map<Object, TreeItemInfo<T>> treeItemsById = null;
            if (identifier != null) {
                treeItemsById = new HashMap<Object, TreeItemInfo<T>>();
                for (TreeItemInfo<T> treeItemInfo : treeItemList) {
                    Object id = identifier.identify(treeItemInfo.getItem());
                    if (id != null) {
                        treeItemsById.put(id, treeItemInfo);
                    }
                }
            }

            return new TreeInfo<T>(categories,
                    (TreeItemInfo<T>[]) treeItemList.toArray(new TreeItemInfo[treeItemList.size()]), treeItemsById,
                    new ArrayList<MenuInfo>(menuList.values()));
        }

        private TreeItemInfo<T> getLastTreeItemInfo() {
            if (!treeItemList.isEmpty()) {
                return treeItemList.get(treeItemList.size() - 1);
            }

            return null;
        }
    }

    public static class MenuInfo {

        private String code;

        private String caption;

        private boolean showOnMultiple;

        private boolean separator;

        public MenuInfo(String code, String caption, boolean showOnMultiple, boolean separator) {
            this.code = code;
            this.caption = caption;
            this.separator = separator;
            this.showOnMultiple = showOnMultiple;
        }

        public String getCode() {
            return code;
        }

        public String getCaption() {
            return caption;
        }

        public boolean isShowOnMultiple() {
            return showOnMultiple;
        }

        public boolean isSeparator() {
            return separator;
        }
    }

    public class TreeEvent<U> {

        private EventType type;

        private String menuCode;

        private List<TreeItemInfo<U>> items;

        public TreeEvent(EventType type, String menuCode, List<TreeItemInfo<U>> items) {
            this.type = type;
            this.menuCode = menuCode;
            this.items = items;
        }

        public EventType getType() {
            return type;
        }

        public String getMenuCode() {
            return menuCode;
        }

        public List<TreeItemInfo<U>> getItems() {
            return items;
        }

    }
}
