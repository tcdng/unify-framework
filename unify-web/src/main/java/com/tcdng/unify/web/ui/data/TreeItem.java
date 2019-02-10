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

/**
 * Tree item.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TreeItem {

    private TreeItemCategory category;

    private Object object;

    private String menuMode;

    private boolean expanded;

    public TreeItem(TreeItemCategory category, Object object) {
        this.category = category;
        this.object = object;
    }

    public TreeItem() {

    }

    public TreeItemCategory getCategory() {
        return category;
    }

    public int getLevel() {
        return category.getLevel();
    }

    public Object getObject() {
        return object;
    }

    public String getMenuMode() {
        return menuMode;
    }

    public void setMenuMode(String menuMode) {
        this.menuMode = menuMode;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

}
