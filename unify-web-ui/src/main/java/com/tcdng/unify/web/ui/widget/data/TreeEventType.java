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

package com.tcdng.unify.web.ui.widget.data;

import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Tree event type.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public enum TreeEventType implements EnumConst {

    TREEITEM_CLICK("TCL", 0x0001),
    TREEITEM_RIGHTCLICK("TRC", 0x0002),
    TREEITEM_DBCLICK("TDC", 0x0004),
    TREEITEM_DRAG("TDG", 0x0008),
    TREEITEM_DROP("TDP", 0x0010),
    MENUITEM_CLICK("MCL", 0x0020);

    private final String code;

    private final int flag;

    private TreeEventType(String code, int flag) {
        this.code = code;
        this.flag = flag;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return TREEITEM_CLICK.code;
    }

    public int flag() {
        return this.flag;
    }

    public static TreeEventType fromCode(String code) {
        return EnumUtils.fromCode(TreeEventType.class, code);
    }

    public static TreeEventType fromName(String name) {
        return EnumUtils.fromName(TreeEventType.class, name);
    }
}
