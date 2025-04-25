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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;

/**
 * Abstract base class for popup text fields.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplAttributes({
		@UplAttribute(name = "buttonImgSrc", type = String.class, defaultVal = "$t{images/droparrow.png}"),
        @UplAttribute(name = "buttonSymbol", type = String.class, defaultVal = "angle-down"),
        @UplAttribute(name = "timeout", type = long.class, defaultVal = "-1"),
        @UplAttribute(name = "clearable", type = boolean.class),
        @UplAttribute(name = "popupAlways", type = boolean.class, defaultVal = "false") })
public abstract class AbstractPopupTextField extends TextField {

    @Override
    public String getBorderId() throws UnifyException {
        return getPrefixedId("brd_");
    }

    @Override
    public String getStyleClass() throws UnifyException {
        return "ui-text-popup " + super.getStyleClass();
    }

    public String getPopupButtonId() throws UnifyException {
        return getPrefixedId("popb_");
    }

    public String getPopupButtonColorId() throws UnifyException {
        return getPrefixedId("popbc_");
    }

    public String getPopupId() throws UnifyException {
        return getPrefixedId("pop_");
    }

    public String getButtonImageSrc() throws UnifyException {
        return getUplAttribute(String.class, "buttonImgSrc");
    }

    public String getButtonSymbol() throws UnifyException {
        return getUplAttribute(String.class, "buttonSymbol");
    }

    public long getDisplayTimeOut() throws UnifyException {
        return getUplAttribute(long.class, "timeout");
    }

    public boolean isClearable() throws UnifyException {
        return getUplAttribute(boolean.class, "clearable");
    }

    public boolean isPopupAlways() throws UnifyException {
        return getUplAttribute(boolean.class, "popupAlways");
    }

    public boolean isOpenPopupOnFac() {
        return true;
    }
    
    public abstract boolean isPopupOnEditableOnly();
}
