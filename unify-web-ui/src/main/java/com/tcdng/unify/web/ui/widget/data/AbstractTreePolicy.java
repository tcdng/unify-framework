/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Convenient abstract base class for tree policies.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractTreePolicy extends AbstractUnifyComponent implements TreePolicy {

    private static final TreeItemCollapser TREEITEM_COLLAPSER = new TreeItemCollapser();

    private static final TreeItemExpander TREEITEM_EXPANDER = new TreeItemExpander();

    @Override
    public String getTreeItemCaption(TreeItemTypeInfo category, Object item) throws UnifyException {
        String itemCaptionKey = category.getItemCaptionKey();
        if (StringUtils.isBlank(itemCaptionKey)) {
            return String.valueOf(item);
        }

        return resolveSessionMessage(itemCaptionKey, item);
    }

    @Override
    public TreeItemUpdater getTreeItemExpander() throws UnifyException {
        return TREEITEM_EXPANDER;
    }

    @Override
    public TreeItemUpdater getTreeItemCollapser() throws UnifyException {
        return TREEITEM_COLLAPSER;
    }

    @Override
    public int addDecision(TreeItem siblingItem, TreeItem childItem) {
        if (childItem.getBuoyancy() < siblingItem.getBuoyancy()) {
            return -1;
        }

        return 0;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private static class TreeItemCollapser extends AbstractTreeItemUpdater {

        @Override
        public void update(TreeItem treeItem) {
            treeItem.setExpanded(false);
        }
    }

    private static class TreeItemExpander extends AbstractTreeItemUpdater {

        @Override
        public void update(TreeItem treeItem) {
            treeItem.setExpanded(true);
        }
    }

}
