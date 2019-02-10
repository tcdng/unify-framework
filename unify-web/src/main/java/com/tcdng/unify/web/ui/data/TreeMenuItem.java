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

/**
 * Tree menu item.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TreeMenuItem {

    private String code;

    private String caption;

    private boolean showOnMultiple;

    private boolean separator;

    public TreeMenuItem(String code, String caption, boolean showOnMultiple, boolean separator) {
        this.code = code;
        this.caption = caption;
        this.separator = separator;
        this.showOnMultiple = showOnMultiple;
    }

    public String getCode() {
        return code;
    }

    public String getCaption() {
        return caption;
    }

    public boolean isShowOnMultiple() {
        return showOnMultiple;
    }

    public boolean isSeparator() {
        return separator;
    }
}
