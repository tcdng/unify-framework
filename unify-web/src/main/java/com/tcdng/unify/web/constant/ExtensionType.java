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
package com.tcdng.unify.web.constant;

/**
 * Control extension type.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ExtensionType {

    NONE(false, false, false, false),
    EXTENDED(true, false, false, false),
    FACADE(true, true, false, false),
    FACADE_HIDDEN(true, true, true, false),
    FACADE_HIDDEN_LIST(true, true, true, false),
    FACADE_HIDDEN_EDIT(true, true, true, true);

    private final boolean extended;

    private final boolean facade;

    private final boolean hidden;

    private final boolean edit;

    private ExtensionType(boolean extended, boolean facade, boolean hidden, boolean edit) {
        this.extended = extended;
        this.facade = facade;
        this.hidden = hidden;
        this.edit = edit;
    }

    public boolean isExtended() {
        return extended;
    }

    public boolean isFacade() {
        return facade;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isEdit() {
        return edit;
    }

    public boolean isFacadeStringValue() {
        return edit || FACADE_HIDDEN_LIST.equals(this);
    }

    public boolean isFacadeHidden() {
        return facade && hidden;
    }

    public boolean isFacadeEdit() {
        return facade && edit;
    }

}
