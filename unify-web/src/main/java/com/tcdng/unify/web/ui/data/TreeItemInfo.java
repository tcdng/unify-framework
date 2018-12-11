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

/**
 * Tree item info.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TreeItemInfo<T> {

	private TreeItemCategoryInfo categoryInfo;

	private T item;

	private String caption;

	private int index;

	private int depth;

	private boolean parent;

	private boolean expanded;

	private boolean hidden;

	public TreeItemInfo(TreeItemCategoryInfo categoryInfo, T item, String caption, int index, int depth) {
		this.categoryInfo = categoryInfo;
		this.item = item;
		this.caption = caption;
		this.index = index;
		this.depth = depth;
	}

	public TreeItemCategoryInfo getCategoryInfo() {
		return categoryInfo;
	}

	public T getItem() {
		return item;
	}

	public String getCaption() {
		return caption;
	}

	public int getIndex() {
		return index;
	}

	public int getDepth() {
		return depth;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}
