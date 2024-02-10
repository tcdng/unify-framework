/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.constant;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Message type constants.
 * 
 * @author The Code Department
 * @version 1.0
 */
@StaticList(name = "messagetypelist", description="$m{staticlist.messagetypelist}")
public enum MessageType implements EnumConst {

    INFO("INF", "$t{images/info.png}", "ui-msginfo", 0),
    WARNING("WRN", "$t{images/warning.png}", "ui-msgwarn", 1),
    ERROR("ERR", "$t{images/error.png}", "ui-msgerror", 2),
    PASS("PSS", "$t{images/pass.png}", "ui-msgpass", 3);

    private final String code;

    private final String image;

    private final String styleClass;

    private final int severity;

    private MessageType(String code, String image, String styleClass, int severity) {
        this.code = code;
        this.image = image;
        this.styleClass = styleClass;
        this.severity = severity;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return INFO.code;
    }

    public boolean isInfo() {
    	return INFO.equals(this);
    }

    public boolean isWarning() {
    	return WARNING.equals(this);
    }

    public boolean isError() {
    	return ERROR.equals(this);
    }

    public boolean isPass() {
    	return PASS.equals(this);
    }
    
    public String image() {
        return image;
    }

    public String styleClass() {
        return styleClass;
    }

    public int severity() {
        return severity;
    }

    public int compareSeverity(MessageType type) {
        return this.severity - type.severity;
    }
    
    public static MessageType fromCode(String code) {
        return EnumUtils.fromCode(MessageType.class, code);
    }

    public static MessageType fromName(String name) {
        return EnumUtils.fromName(MessageType.class, name);
    }
}
