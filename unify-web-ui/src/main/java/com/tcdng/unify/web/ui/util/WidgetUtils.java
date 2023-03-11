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
package com.tcdng.unify.web.ui.util;

/**
 * Widget utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class WidgetUtils {

    private WidgetUtils() {

    }

    /**
     * Returns corresponding id for child id.
     * 
     * @param parentId
     *            the parent id
     * @param childId
     *            the childId id. Can be null.
     * @param childIndex
     *            the child index
     */
    public static String getChildId(String parentId, String childId, int childIndex) {
        if (childId == null) {
            return parentId + ".c" + childIndex;
        } else if (childId.startsWith(parentId)) {
            return childId;
        }

        return parentId + '.' + childId;
    }

    /**
     * Renames a child id.
     * 
     * @param newParentId
     *            the new parent id
     * @param childId
     *            the old child id
     * @return the new child id
     */
    public static String renameChildId(String newParentId, String childId) {
        int chIndex = childId.lastIndexOf('.');
        return newParentId + childId.substring(chIndex);
    }

    /**
     * Returns corresponding naming index id for supplied id.
     * 
     * @param id
     *            the id to use
     * @param index
     *            the index to use
     */
    public static String getNamingIndexId(String id, int index) {
        return id + 'n' + index;
    }

    /**
     * Returns corresponding data index id for supplied id.
     * 
     * @param id
     *            the id to use
     * @param index
     *            the index to use
     */
    public static String getDataIndexId(String id, int index) {
        return id + 'd' + index;
    }
}
