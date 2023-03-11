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

package com.tcdng.unify.web.ui.widget.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Tree type information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TreeTypeInfo {

    private List<TreeMenuItemInfo> menuItemInfoList;

    private List<Integer> multiSelectMenuSequence;

    private Map<String, ExtendedTreeItemTypeInfo> treeItemTypeInfos;

    private TreeTypeInfo(List<TreeMenuItemInfo> menuItemInfoList, List<Integer> multiSelectMenuSequence,
            Map<String, ExtendedTreeItemTypeInfo> treeItemTypeInfos) {
        this.menuItemInfoList = menuItemInfoList;
        this.multiSelectMenuSequence = multiSelectMenuSequence;
        this.treeItemTypeInfos = treeItemTypeInfos;
    }

    public List<TreeMenuItemInfo> getMenuItemInfoList() {
        return menuItemInfoList;
    }

    public List<Integer> getMultiSelectMenuSequence() {
        return multiSelectMenuSequence;
    }

    public Collection<ExtendedTreeItemTypeInfo> getExtendedTreeItemTypeInfos() {
        return treeItemTypeInfos.values();
    }

    public TreeItemTypeInfo getTreeItemTypeInfo(String itemTypeCode) {
        ExtendedTreeItemTypeInfo in = treeItemTypeInfos.get(itemTypeCode);
        if (in != null) {
            return in.getTreeItemTypeInfo();
        }

        return null;
    }

    public boolean isTreeItemType(String code) {
        return treeItemTypeInfos.containsKey(code);
    }

    public List<Integer> getMenuItemSequence(String itemTypeCode) {
        ExtendedTreeItemTypeInfo in = treeItemTypeInfos.get(itemTypeCode);
        if (in != null) {
            return in.getMenuSequence();
        }

        return null;
    }

    public boolean isMultiSelectMenu() {
        return !multiSelectMenuSequence.isEmpty();
    }

    public boolean isMenuItemList() {
        return !menuItemInfoList.isEmpty();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private List<TreeMenuItemInfo> menuItemInfoList;

        private List<String> menuCodeList;

        private List<Integer> multiSelectMenuSequence;

        private Map<String, ExtendedTreeItemTypeInfo> treeItemTypeInfos;

        private int menuItemGroupIndex;

        private Builder() {
            menuItemInfoList = new ArrayList<TreeMenuItemInfo>();
            menuCodeList = new ArrayList<String>();
            treeItemTypeInfos = new HashMap<String, ExtendedTreeItemTypeInfo>();
        }

        public Builder addMenuItem(String code, String caption) throws UnifyException {
            return addMenuItem(code, caption, null);
        }

        public Builder addMenuItem(String code, String caption, String confirm) throws UnifyException {
            if (menuCodeList.contains(code)) {
                throw new UnifyOperationException(
                        "Menu item with code [" + code + "] already exists.");
            }

            menuCodeList.add(code);
            menuItemInfoList.add(new TreeMenuItemInfo(code, caption, confirm, menuItemGroupIndex));
            return this;
        }

        public Builder nextMenuItemGroup() throws UnifyException {
            menuItemGroupIndex++;
            return this;
        }

        public Builder addMultiSelectMenuItem(String menuItemCode) throws UnifyException {
            int menuIndex = menuCodeList.indexOf(menuItemCode);
            if (menuIndex < 0) {
                throw new UnifyOperationException(
                        "Multi-select setup refers to unknown menu item [" + menuItemCode + "].");
            }

            if (multiSelectMenuSequence == null) {
                multiSelectMenuSequence = new ArrayList<Integer>();
            }

            if (!multiSelectMenuSequence.contains(menuIndex)) {
                multiSelectMenuSequence.add(menuIndex);
            }
            return this;
        }

        public Builder addTreeItemType(TreeItemTypeInfo treeItemTypeInfo) throws UnifyException {
            if (treeItemTypeInfos.containsKey(treeItemTypeInfo.getCode())) {
                throw new UnifyOperationException(
                        "Tree item type with code [" + treeItemTypeInfo.getCode() + "] exists.");
            }

            List<Integer> menuSequence = null;
            if (treeItemTypeInfo.isMenuCodeList()) {
                menuSequence = new ArrayList<Integer>();
                for (String menuCode : treeItemTypeInfo.getMenuCodeList()) {
                    int menuIndex = menuCodeList.indexOf(menuCode);
                    if (menuIndex < 0) {
                        throw new UnifyOperationException(
                                "Tree item type with code [" + treeItemTypeInfo.getCode()
                                        + "] refers to unknown menu item [" + menuCode + "].");
                    }

                    menuSequence.add(Integer.valueOf(menuIndex));
                }
            }

            treeItemTypeInfos.put(treeItemTypeInfo.getCode(),
                    new ExtendedTreeItemTypeInfo(treeItemTypeInfo, DataUtils.unmodifiableList(menuSequence)));
            return this;
        }

        public TreeTypeInfo build() throws UnifyException {
            return new TreeTypeInfo(Collections.unmodifiableList(menuItemInfoList),
                    DataUtils.unmodifiableList(multiSelectMenuSequence),
                    Collections.unmodifiableMap(treeItemTypeInfos));
        }
    }

    public static class ExtendedTreeItemTypeInfo {

        private TreeItemTypeInfo treeItemTypeInfo;

        private List<Integer> menuSequence;

        public ExtendedTreeItemTypeInfo(TreeItemTypeInfo treeItemTypeInfo, List<Integer> menuSequence) {
            this.treeItemTypeInfo = treeItemTypeInfo;
            this.menuSequence = menuSequence;
        }

        public TreeItemTypeInfo getTreeItemTypeInfo() {
            return treeItemTypeInfo;
        }

        public List<Integer> getMenuSequence() {
            return menuSequence;
        }
    }

}
