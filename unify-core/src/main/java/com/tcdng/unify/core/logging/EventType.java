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
package com.tcdng.unify.core.logging;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Supported event types.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("auditactionlist")
public enum EventType implements EnumConst {

    SEARCH("S", "ui-event-gray", true),
    CREATE("C", "ui-event-green", true),
    VIEW("R", "ui-event-gray", true),
    UPDATE("U", "ui-event-red", true),
    DELETE("D", "ui-event-red", true),
    LOGIN("L", "ui-event-blue", false),
    LOGOUT("O", "ui-event-blue", false),
    GENERATE("G", "ui-event-gray", false),
    UPLOAD("P", "ui-event-gray", false),
    DOWNLOAD("N", "ui-event-gray", false),
    WORKFLOW("W", "ui-event-gray", false),
    MISCELLANEOUS("M", "ui-event-yellow", false),
    SYSTEM("Y", "ui-event-yellow", false);

    private final String code;

    private final String colorMode;

    private final boolean crud;

    private EventType(String code, String colorMode, boolean crud) {
        this.code = code;
        this.colorMode = colorMode;
        this.crud = crud;
    }

    @Override
    public String code() {
        return this.code;
    }

    public String colorMode() {
        return this.colorMode;
    }

    public static EventType fromCode(String code) {
        return EnumUtils.fromCode(EventType.class, code);
    }

    public static EventType fromName(String name) {
        return EnumUtils.fromName(EventType.class, name);
    }

    public boolean isCrud() {
        return crud;
    }
}
