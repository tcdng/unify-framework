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
 * Represents a drop-down list with options from which a user can select only
 * one.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-select")
@UplAttributes({ 
    @UplAttribute(name = "blankOption", type = String.class),
    @UplAttribute(name = "colors", type = boolean.class) })
public class SingleSelect extends AbstractListPopupTextField {

    @Override
    public boolean isContainerDisabled() throws UnifyException {
        return super.isContainerDisabled() || !isContainerEditable();
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public boolean isSupportReadOnly() {
        return false;
    }

    @Override
    public boolean isUseFacadeFocus() throws UnifyException {
        return true;
    }

    @Override
    public ExtensionType getExtensionType() {
        return ExtensionType.FACADE_HIDDEN;
    }

    public String getBlankOption() throws UnifyException {
        return getUplAttribute(String.class, "blankOption");
    }

    public boolean isColors() throws UnifyException {
        return getUplAttribute(boolean.class, "colors");
    }

    public String getFramePanelId() throws UnifyException {
        return getPrefixedId("frm_");
    }

    public String getListPanelId() throws UnifyException {
        return getPrefixedId("lst_");
    }

    public String getBlankOptionId() throws UnifyException {
        return getPrefixedId("blnk_");
    }

}
