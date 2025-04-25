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

package com.tcdng.unify.web.ui.widget.control;

/**
 * Control color mode.
 * 
 * @author The Code Department
 * @since 4.1
 */
public enum ControlColorMode {
    
    NORMAL(""),
    OK("ui-cmode-ok"),
    WARNING("ui-cmode-warn"),
    GRAYED("ui-cmode-gray"),
    ERROR("ui-cmode-err");

    private final String styleClass;

    private ControlColorMode(String styleClass) {
        this.styleClass = styleClass;
    }

    public String styleClass() {
        return styleClass;
    }

    public boolean isVisual() {
        return !this.equals(NORMAL);
    }
}
