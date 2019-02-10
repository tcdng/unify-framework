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

import com.tcdng.unify.core.annotation.Component;

/**
 * Default implementation of a tree policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("default-treepolicy")
public class DefaultTreePolicy extends AbstractTreePolicy {

    @Override
    public void performOnAdd(TreeItem targetParentItem, TreeItem childItem) {

    }

    @Override
    public void performOnRemove(TreeItem targetParentItem, TreeItem childItem) {

    }

}
