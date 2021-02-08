/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.UplElementReferences;

/**
 * Serves as the base class for a page action.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "components", type = UplElementReferences.class),
        @UplAttribute(name = "confirm", type = String.class),
        @UplAttribute(name = "iconIndex", type = int.class, defaultVal = "3"),
        @UplAttribute(name = "shortcut", type = String.class),
        @UplAttribute(name = "pushSrc", type = boolean.class)})
public abstract class AbstractPageAction extends AbstractBehavior implements PageAction {

    private String action;

    private String pageName;

    public AbstractPageAction(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return this.action;
    }

    @Override
    public String getId() {
        return pageName;
    }

    @Override
    public void setId(String pageName) {
        this.pageName = pageName;
    }
}
