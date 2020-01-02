/*
 * Copyright 2018-2020 The Code Department.
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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Convenient abstract base class for tree policies.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractTreePolicy extends AbstractUnifyComponent implements TreePolicy {

    @Override
    public String getTreeItemCaption(TreeItemTypeInfo category, Object item) throws UnifyException {
        String itemCaptionKey = category.getItemCaptionKey();
        if (StringUtils.isBlank(itemCaptionKey)) {
            return String.valueOf(item);
        }

        return resolveSessionMessage(itemCaptionKey, item);
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

}
