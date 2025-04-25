/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;

/**
 * Link grid information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class LinkGridInfo {

    private List<LinkCategoryInfo> linkCategoryList;

    public LinkGridInfo(List<LinkCategoryInfo> linkCategoryList) {
        this.linkCategoryList = linkCategoryList;
    }

    public List<LinkCategoryInfo> getLinkCategoryList() {
        return linkCategoryList;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Map<String, CategoryInfo> categories;

        private Builder() {
            categories = new LinkedHashMap<String, CategoryInfo>();
        }

        public Builder addCategory(String name, String caption, String path) throws UnifyException {
            categories.put(name, new CategoryInfo(caption, path));
            return this;
        }

        public Builder addLink(String categoryName, String code, String caption) throws UnifyException {
            CategoryInfo categoryInfo = categories.get(categoryName);
            if (categoryInfo == null) {
                throw new UnifyOperationException(
                        "Category with name [" + categoryName + "] is unknown.");
            }

            categoryInfo.getLinkInfoList().add(new LinkInfo(code, caption));
            return this;
        }

        public boolean isCategory(String name) {
            return categories.containsKey(name);
        }

        public LinkGridInfo build() {
            List<LinkCategoryInfo> linkCategoryList = new ArrayList<LinkCategoryInfo>();
            for (Map.Entry<String, CategoryInfo> entry : categories.entrySet()) {
                CategoryInfo categoryInfo = entry.getValue();
                linkCategoryList.add(new LinkCategoryInfo(entry.getKey(), categoryInfo.getCaption(),
                        categoryInfo.getPath(), Collections.unmodifiableList(categoryInfo.getLinkInfoList())));
            }

            return new LinkGridInfo(Collections.unmodifiableList(linkCategoryList));
        }

        private class CategoryInfo {

            private String caption;

            private String path;

            private List<LinkInfo> linkInfoList;

            public CategoryInfo(String caption, String path) {
                this.caption = caption;
                this.path = path;
                linkInfoList = new ArrayList<LinkInfo>();
            }

            public String getCaption() {
                return caption;
            }

            public String getPath() {
                return path;
            }

            public List<LinkInfo> getLinkInfoList() {
                return linkInfoList;
            }

        }
    }
}
