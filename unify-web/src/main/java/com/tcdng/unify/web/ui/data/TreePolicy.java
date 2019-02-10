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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.MarkedTree.MarkedTreePolicy;

/**
 * Handles rules relating to a tree.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface TreePolicy extends MarkedTreePolicy<TreeItem> {

    /**
     * Gets a tree item caption based on this policy for supplied category and tree
     * item.
     * 
     * @param category
     *            the tree item category
     * @param item
     *            the tree item
     * @return the tree item caption
     * @throws UnifyException
     *             if an error occurs
     */
    String getTreeItemCaption(TreeItemCategory category, Object item) throws UnifyException;
}
