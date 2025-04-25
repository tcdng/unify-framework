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
package com.tcdng.unify.web.ui.widget.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.widget.AbstractPanel;

/**
 * A collapsible panel.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-collapsiblepanel")
@UplBinding("web/panels/upl/collapsiblepanel.upl")
public class CollapsiblePanel extends AbstractPanel {

    private boolean collapsed;

    @Action
    @Override
    public void switchState() throws UnifyException {
        super.switchState();

        setVisible("openBtn", collapsed);
        setVisible("closeBtn", !collapsed);
        setVisible("contentPanel", !collapsed);
    }

    @Action
    public void expandContent() throws UnifyException {
        collapsed = false;
    }

    @Action
    public void collapseContent() throws UnifyException {
        collapsed = true;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }
}
