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

package com.tcdng.unify.core.util;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.common.data.Listable;

/**
 * List utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class ListUtils {

    private ListUtils() {

    }

    /**
     * Gets a sub list based on filter and or limit.
     * 
     * @param srcList
     *                the source list
     * @param filter
     *                optional description filter
     * @param limit
     *                the limit
     * @return sub list
     */
    public static List<? extends Listable> getSubList(List<? extends Listable> srcList, String filter, int limit) {
        if (StringUtils.isBlank(filter)) {
            if (limit <= 0 || limit > srcList.size()) {
                return srcList;
            }

            return srcList.subList(0, limit);
        }

        List<Listable> calcList = new ArrayList<Listable>();
        int i = 0;
        for (Listable listable : srcList) {
            if (listable.getListDescription().contains(filter)) {
                calcList.add(listable);
                if (limit > 0 && (++i) >= limit) {
                    break;
                }
            }
        }

        return calcList;
    }

    /**
     * Gets a sub list based on filter and or limit.
     * 
     * @param srcList
     *                the source list
     * @param filter
     *                optional description filter
     * @param limit
     *                the limit
     * @return sub list
     */
    public static List<? extends Listable> getCaseInsensitiveSubList(List<? extends Listable> srcList, String filter,
            int limit) {
        if (StringUtils.isBlank(filter)) {
            if (limit <= 0 || limit > srcList.size()) {
                return srcList;
            }

            return srcList.subList(0, limit);
        }

        List<Listable> calcList = new ArrayList<Listable>();
        filter = filter.toLowerCase();
        int i = 0;
        for (Listable listable : srcList) {
            if (listable.getListDescription().toLowerCase().contains(filter)) {
                calcList.add(listable);
                if (limit > 0 && (++i) >= limit) {
                    break;
                }
            }
        }

        return calcList;
    }
}
