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

package com.tcdng.unify.core;

import com.tcdng.unify.core.util.StringUtils;

/**
 * Setting data object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Setting {

    private String name;

    private Object value;

    private boolean autoInject;

    private boolean hidden;

    public Setting(String name, Object value) {
        this(name, value, true, false);
    }

    public Setting(String name, Object value, boolean autoInject, boolean hidden) {
        this.name = name;
        this.value = value;
        this.autoInject = autoInject;
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getValueString() {
        if (this.hidden) {
            return StringUtils.MASK;
        }

        return String.valueOf(this.value);
    }

    public boolean isAutoInject() {
        return autoInject;
    }

    public boolean isHidden() {
        return hidden;
    }

}
