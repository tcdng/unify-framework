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
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * Represents a group control.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-group")
@UplAttributes({ @UplAttribute(name = "focus", type = boolean.class),
        @UplAttribute(name = "sortable", type = boolean.class), @UplAttribute(name = "space", type = boolean.class) })
public class GroupControl extends AbstractMultiControl implements Control {

    private String dataGroupId;

    @Override
    public boolean isFocus() throws UnifyException {
        return false;
    }

    @Override
    public void addPageAliases() throws UnifyException {
        if (isContainerEditable()) {
            addPageAlias(getDataGroupId());
        }
    }

    public String getDataGroupId() {
        return dataGroupId;
    }

    public boolean isSpace() throws UnifyException {
        return getUplAttribute(boolean.class, "space");
    }

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        dataGroupId = getPrefixedId("data_");
    }
}
