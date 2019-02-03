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
import com.tcdng.unify.core.data.MarkedTree;
import com.tcdng.unify.core.data.MarkedTree.Matcher;
import com.tcdng.unify.core.data.MarkedTree.Node;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Tree information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TreeInfo {

    private static final DefaultTreeItemExpandPolicy expandChildPolicy = new DefaultTreeItemExpandPolicy();

    private static final DefaultTreeItemCollapsePolicy collapseChildPolicy = new DefaultTreeItemCollapsePolicy();

    private Map<String, TreeItemCategoryInfo> categories;

    private MarkedTree<TreeItemInfo> itemInfoTree;

    private List<TreeMenuInfo> menuList;

    private List<Long> selectedItemIdList;

    private Queue<TreeEvent> eventQueue;

    private TreeInfo(List<TreeMenuInfo> menuList, Map<String, TreeItemCategoryInfo> categories,
            MarkedTree<TreeItemInfo> itemInfoTree) {
        this.menuList = menuList;
        this.categories = categories;
        this.itemInfoTree = itemInfoTree;
        selectedItemIdList = Collections.emptyList();
        eventQueue = new LinkedList<TreeEvent>();
    }

    public Long addTreeItem(Long parentItemId, String categoryName, Object item) throws UnifyException {
        TreeItemCategoryInfo categoryInfo = categories.get(categoryName);
        if (categoryInfo == null) {
            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                    "Unknown category [" + categoryName + "].");
        }

        TreeItemCategoryInfo parentCategoryInfo = itemInfoTree.getNode(parentItemId).getItem().getCategoryInfo();
        Long itemId = itemInfoTree.addChild(parentItemId, new TreeItemInfo(categoryInfo, item),
                parentCategoryInfo.getAddChildPolicy());
        itemInfoTree.updateParentNodes(itemId, expandChildPolicy); // Expand parents
        return itemId;
    }

    public TreeItemCategoryInfo getTreeCategoryInfo(String name) {
        return categories.get(name);
    }

    public Collection<TreeItemCategoryInfo> getTreeCategoryInfos() {
        return categories.values();
    }

    public Node<TreeItemInfo> getRootNode() {
        return itemInfoTree.getRoot();
    }

    public Node<TreeItemInfo> getNode(Long itemId) {
        return itemInfoTree.getNode(itemId);
    }

    public Node<TreeItemInfo> getNode(TreeEvent treeEvent, int index) {
        return itemInfoTree.getNode(treeEvent.getItemIds().get(index));
    }

    public Node<TreeItemInfo> findNode(Long parentItemId, Matcher<TreeItemInfo> childMatcher) throws UnifyException {
        return itemInfoTree.findNode(parentItemId, childMatcher);
    }

    public List<Node<TreeItemInfo>> findNodes(Long parentItemId, Matcher<TreeItemInfo> childMatcher)
            throws UnifyException {
        return itemInfoTree.findNodes(parentItemId, childMatcher);
    }

    public List<Node<TreeItemInfo>> getChildNodes(Long parentItemId) throws UnifyException {
        return itemInfoTree.getChildNodes(parentItemId);
    }

    public List<Node<TreeItemInfo>> getChildNodes(Long parentItemId, Matcher<TreeItemInfo> matcher)
            throws UnifyException {
        return itemInfoTree.getChildNodes(parentItemId, matcher);
    }

    public TreeItemInfo getTreeItemInfo(Long itemId) {
        Node<TreeItemInfo> node = itemInfoTree.getNode(itemId);
        if (node != null) {
            return node.getItem();
        }

        return null;
    }

    public TreeItemInfo getTreeItemInfo(TreeEvent treeEvent, int index) {
        return getTreeItemInfo(treeEvent.getItemIds().get(index));
    }

    public int itemCount() {
        return itemInfoTree.size();
    }

    public List<TreeMenuInfo> getMenuList() {
        return menuList;
    }

    public boolean isMenu() {
        return !DataUtils.isBlank(menuList);
    }

    public void setSelectedItems(List<Long> selectedItemIdList) {
        this.selectedItemIdList = selectedItemIdList;
        for (Long itemId : selectedItemIdList) {
            try {
                itemInfoTree.updateParentNodes(itemId, expandChildPolicy);
            } catch (UnifyException e) {
            }
        }
    }

    public List<Long> getSelectedItems() {
        return selectedItemIdList;
    }

    public void registerEvent(EventType type, String menuCode) {
        eventQueue.offer(new TreeEvent(type, menuCode, selectedItemIdList));
    }

    public TreeEvent getEvent() {
        return eventQueue.poll();
    }

    public void collapseAll() throws UnifyException {
        itemInfoTree.updateNodes(collapseChildPolicy);
    }

    public boolean collapse(Long itemId) {
        TreeItemInfo treeItemInfo = getTreeItemInfo(itemId);
        if (treeItemInfo != null) {
            treeItemInfo.setExpanded(false);
            return true;
        }

        return false;
    }

    public void expandAll() throws UnifyException {
        itemInfoTree.updateNodes(expandChildPolicy);
    }

    public boolean expand(Long itemId) {
        TreeItemInfo treeItemInfo = getTreeItemInfo(itemId);
        if (treeItemInfo != null) {
            treeItemInfo.setExpanded(true);
            return true;
        }

        return false;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TreeInfo refTreeInfo) {
        return new Builder(refTreeInfo);
    }

    public static class Builder {

        private Map<String, TreeMenuInfo> menuList;

        private Map<String, TreeItemCategoryInfo> categories;

        private MarkedTree<TreeItemInfo> itemInfoTree;

        private Builder() {
            this(null);
        }

        private Builder(TreeInfo treeInfo) {
            menuList = new LinkedHashMap<String, TreeMenuInfo>();
            itemInfoTree = new MarkedTree<TreeItemInfo>(new TreeItemInfo());
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

        public Builder addTreeItem(String categoryName, Object item) throws UnifyException {
            if (!categories.containsKey(categoryName)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        "Unknown category [" + categoryName + "].");
            }

            itemInfoTree.add(new TreeItemInfo(categories.get(categoryName), item));
            return this;
        }

        public Node<TreeItemInfo> getLastNode() {
            return itemInfoTree.getChainLast();
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

            menuList.put(code, new TreeMenuInfo(code, caption, true, separator));
            return this;
        }

        public boolean descend() throws UnifyException {
            return itemInfoTree.descend();
        }

        public boolean ascend() throws UnifyException {
            return itemInfoTree.ascend();
        }

        public TreeInfo build() throws UnifyException {
            itemInfoTree.setChain(false); // Enter unchained mode
            return new TreeInfo(Collections.unmodifiableList(new ArrayList<TreeMenuInfo>(menuList.values())),
                    categories, itemInfoTree);
        }
    }

}
