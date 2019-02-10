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
package com.tcdng.unify.web.util;

import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.data.TreeMenuItem;

/**
 * Tree utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class TreeUtils {

    private TreeUtils() {

    }

    public static boolean isAnyMenuItemVisible(List<TreeMenuItem> menuList, Set<String> hidden) {
        if (!DataUtils.isBlank(menuList)) {
            if (hidden == null) {
                return true;
            }

            for (TreeMenuItem menuItem : menuList) {
                if (!hidden.contains(menuItem.getCode())) {
                    return true;
                }
            }
        }

        return false;
    }
}
