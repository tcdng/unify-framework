/*
 * Copyright 2018-2022 The Code Department.
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

/**
 * Tree item.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TreeItem {

    private TreeItemTypeInfo typeInfo;

    private Object content;

    private boolean expanded;

    public TreeItem(TreeItemTypeInfo typeInfo, Object content) {
        this.typeInfo = typeInfo;
        this.content = content;
    }

    public TreeItem() {

    }

    public TreeItemTypeInfo getTypeInfo() {
        return typeInfo;
    }

    public void setCategory(TreeItemTypeInfo category) {
        this.typeInfo = category;
    }

    public int getBuoyancy() {
        return typeInfo.getBuoyancy();
    }

    public Object getContent() {
        return content;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

}
