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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.constant.ExtensionType;

/**
 * A text input control with a drop-down containing multiple check boxes.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-dropdownchecklist")
@UplAttributes({ @UplAttribute(name = "selectAllOption", type = String.class),
        @UplAttribute(name = "columns", type = int.class),
        @UplAttribute(name = "popupAlways", type = boolean.class, defaultVal = "true") })
public class DropdownCheckList extends AbstractListPopupTextField {

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public ExtensionType getExtensionType() {
        return ExtensionType.FACADE;
    }

    public String getSelectAllId() throws UnifyException {
        return getPrefixedId("sela_");
    }

    public String getSelectAllOption() throws UnifyException {
        return getUplAttribute(String.class, "selectAllOption");
    }

    public int getColumns() throws UnifyException {
        return getUplAttribute(int.class, "columns");
    }
}
