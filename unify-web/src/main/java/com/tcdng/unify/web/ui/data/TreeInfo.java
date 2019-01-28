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
public class TreeInfo {

    private Map<String, TreeItemCategoryInfo> categories;

    private Map<Integer, TreeItemInfo> treeItemsById;

    private List<MenuInfo> menuList;

    private List<Integer> selectedItemIdList;

    private TreeItemInfo firstTreeItemInfo;

    private Queue<TreeEvent> eventQueue;

    private int idCounter;
    
    private TreeInfo(Map<String, TreeItemCategoryInfo> categories, List<MenuInfo> menuList,
            Map<Integer, TreeItemInfo> treeItemsById, TreeItemInfo firstTreeItemInfo, int idCounter) {
        this.categories = categories;
        this.treeItemsById = treeItemsById;
        this.firstTreeItemInfo = firstTreeItemInfo;
        this.menuList = menuList;
        selectedItemIdList = Collections.emptyList();
        eventQueue = new LinkedList<TreeEvent>();
        this.idCounter = idCounter;
        evaluate();
    }

    public TreeItemCategoryInfo getTreeCategoryInfo(String name) {
        return categories.get(name);
    }

    public Collection<TreeItemCategoryInfo> getTreeCategoryInfos() {
        return categories.values();
    }

    public TreeItemInfo getFirstTreeItemInfo() {
        return firstTreeItemInfo;
    }

    public TreeItemInfo getTreeItemInfo(Integer id) {
        return treeItemsById.get(id);
    }

    public int itemCount() {
        return treeItemsById.size();
    }

    public List<Integer> getSelectedItemIds() {
        return selectedItemIdList;
    }

    public List<MenuInfo> getMenuList() {
        return menuList;
    }

    public boolean isMenu() {
        return !DataUtils.isBlank(menuList);
    }

    public void setSelectedItem(Integer id) {
        TreeItemInfo treeItemInfo = treeItemsById.get(id);
        if (treeItemInfo != null) {
            selectedItemIdList = new ArrayList<Integer>(1);
            selectedItemIdList.add(id);
            expandParents(treeItemInfo);
            evaluate();
        }

        selectedItemIdList = Collections.emptyList();
    }

    public void setSelectedItems(List<Integer> selectedItemIdList) {
        this.selectedItemIdList = selectedItemIdList;
    }

    public void registerEvent(EventType type, String menuCode) {
        eventQueue.offer(new TreeEvent(type, menuCode, selectedItemIdList));
    }

    public TreeEvent getEvent() {
        return eventQueue.poll();
    }

    public void collapseAll() {
        TreeItemInfo treeItemInfo = firstTreeItemInfo;
        while (treeItemInfo != null) {
            treeItemInfo.setExpanded(false);
            treeItemInfo = treeItemInfo.getNext();
        }

        evaluate();
    }

    public boolean collapse(Integer itemId) {
        return setExpand(itemId, false);
    }

    public void expandAll() {
        TreeItemInfo treeItemInfo = firstTreeItemInfo;
        while (treeItemInfo != null) {
            treeItemInfo.setExpanded(true);
            treeItemInfo = treeItemInfo.getNext();
        }

        evaluate();
    }

    public boolean expand(Integer itemId) {
        return setExpand(itemId, true);
    }

    private boolean setExpand(Integer itemId, boolean expand) {
        TreeItemInfo treeItemInfo = treeItemsById.get(itemId);
        if (treeItemInfo != null) {
            treeItemInfo.setExpanded(expand);
            evaluate();
            return true;
        }

        return false;
    }

    private void expandParents(TreeItemInfo treeItemInfo) {
        while ((treeItemInfo = getParent(treeItemInfo)) != null) {
            treeItemInfo.setExpanded(true);
        }
    }

    private TreeItemInfo getParent(TreeItemInfo treeItemInfo) {
        TreeItemInfo topTreeItemInfo = treeItemInfo;
        int depth = treeItemInfo.getDepth();
        while ((topTreeItemInfo = topTreeItemInfo.getPrevious()) != null) {
            if (topTreeItemInfo.getDepth() < depth) {
                return topTreeItemInfo;
            }
        }

        return null;
    }

    private void evaluate() {
        TreeItemInfo treeItemInfo = firstTreeItemInfo;
        int checkDepth = -1;
        while (treeItemInfo != null) {
            boolean hidden = checkDepth >= 0 && checkDepth < treeItemInfo.getDepth();
            if (!hidden) {
                if (treeItemInfo.isExpanded()) {
                    checkDepth = -1;
                } else {
                    checkDepth = treeItemInfo.getDepth();
                }
            }

            treeItemInfo.setHidden(hidden);
            treeItemInfo = treeItemInfo.getNext();
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TreeInfo refTreeInfo) {
        return new Builder(refTreeInfo);
    }

    public static class Builder {

        private Map<String, MenuInfo> menuList;

        private Map<String, TreeItemCategoryInfo> categories;

        private Map<Integer, TreeItemInfo> treeItemsById;

        private TreeItemInfo firstTreeItemInfo;

        private TreeItemInfo lastTreeItemInfo;

        private int idCounter;

        private int depth;

        private Builder() {
            this(null);
        }

        private Builder(TreeInfo treeInfo) {
            menuList = new LinkedHashMap<String, MenuInfo>();
            treeItemsById = new HashMap<Integer, TreeItemInfo>();
            if (treeInfo != null) {
                categories = new HashMap<String, TreeItemCategoryInfo>(treeInfo.categories);
            } else {
                categories = new HashMap<String, TreeItemCategoryInfo>();
            }
        }

        public Builder addCategory(TreeItemCategoryInfo treeCategoryInfo) throws UnifyException {
            if (categories.containsKey(treeCategoryInfo.getName())) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Category with name [" + treeCategoryInfo.getName() + "] exists.");
            }

            categories.put(treeCategoryInfo.getName(), treeCategoryInfo);
            return this;
        }

        public Builder addTreeItem(String categoryName, String caption) throws UnifyException {
            return addTreeItem(categoryName, caption, null);
        }

        public Builder addTreeItem(String categoryName, String caption, Object item) throws UnifyException {
            if (!categories.containsKey(categoryName)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Unknown category [" + categoryName + "].");
            }

            TreeItemInfo treeItemInfo =
                    new TreeItemInfo(categories.get(categoryName), idCounter++, caption, depth, item);
            if (lastTreeItemInfo == null) {
                firstTreeItemInfo = treeItemInfo;
            } else {
                lastTreeItemInfo.setNext(treeItemInfo);
                treeItemInfo.setPrevious(lastTreeItemInfo);
            }

            treeItemsById.put(treeItemInfo.getId(), treeItemInfo);
            lastTreeItemInfo = treeItemInfo;
            return this;
        }

        public boolean isCategory(String name) {
            return categories.containsKey(name);
        }

        public Builder addMenuItem(String code, String caption) throws UnifyException {
            return addMenuItem(code, caption, false);
        }

        public Builder addMenuItem(String code, String caption, boolean separator) throws UnifyException {
            if (menuList.containsKey(code)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Menu item with code [" + code + "] exists.");
            }

            menuList.put(code, new MenuInfo(code, caption, true, separator));
            return this;
        }

        public boolean descend() {
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

        public TreeInfo build() throws UnifyException {
            return new TreeInfo(categories, new ArrayList<MenuInfo>(menuList.values()), treeItemsById,
                    firstTreeItemInfo, idCounter);
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

    public static class TreeEvent {

        private EventType type;

        private String menuCode;

        private List<Integer> itemIdList;

        public TreeEvent(EventType type, String menuCode, List<Integer> itemIdList) {
            this.type = type;
            this.menuCode = menuCode;
            this.itemIdList = itemIdList;
        }

        public EventType getType() {
            return type;
        }

        public String getMenuCode() {
            return menuCode;
        }

        public List<Integer> getItemIds() {
            return itemIdList;
        }

    }

    public static class TreeItemInfo {

        private TreeItemCategoryInfo categoryInfo;

        private TreeItemInfo previous;

        private TreeItemInfo next;

        private Integer id;

        private String caption;

        private Object item;

        private int depth;

        private boolean expanded;

        private boolean hidden;

        public TreeItemInfo(TreeItemCategoryInfo categoryInfo, Integer id, String caption, int depth, Object item) {
            this.categoryInfo = categoryInfo;
            this.id = id;
            this.caption = caption;
            this.depth = depth;
            this.item = item;
        }

        public TreeItemCategoryInfo getCategoryInfo() {
            return categoryInfo;
        }

        public TreeItemInfo getPrevious() {
            return previous;
        }

        private void setPrevious(TreeItemInfo previous) {
            this.previous = previous;
        }

        public boolean isFirst() {
            return previous == null;
        }

        public TreeItemInfo getNext() {
            return next;
        }

        private void setNext(TreeItemInfo next) {
            this.next = next;
        }

        public boolean isLast() {
            return next == null;
        }

        public Integer getId() {
            return id;
        }

        public String getCaption() {
            return caption;
        }

        public int getDepth() {
            return depth;
        }

        public Object getItem() {
            return item;
        }

        public boolean isParent() {
            return next != null && depth < next.getDepth();
        }

        public boolean isExpanded() {
            return expanded;
        }

        private void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public boolean isHidden() {
            return hidden;
        }

        private void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

    }

}
