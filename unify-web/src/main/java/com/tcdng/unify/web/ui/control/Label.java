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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.ui.AbstractFormattedControl;

/**
 * A label control.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-label")
@UplAttributes({ @UplAttribute(name = "htmlEscape", type = boolean.class, defaultValue = "true"),
        @UplAttribute(name = "layoutCaption", type = boolean.class, defaultValue = "false"),
        @UplAttribute(name = "draggable", type = boolean.class, defaultValue = "false") })
public class Label extends AbstractFormattedControl {

    public Label() {
        super.setEditable(false);
    }

    @Override
    public void setEditable(boolean editable) {

    }

    @Override
    public boolean isLayoutCaption() throws UnifyException {
        super.isLayoutCaption();
        return getUplAttribute(boolean.class, "layoutCaption");
    }

    public boolean isHtmlEscape() throws UnifyException {
        return getUplAttribute(boolean.class, "htmlEscape");
    }
}
