/*
 * Copyright 2018 The Code Department
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
import com.tcdng.unify.web.ui.AbstractMultiControl;

/**
 * A link grid widget.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-linkgrid")
@UplAttributes({ @UplAttribute(name = "columns", type = int.class) })
public class LinkGrid extends AbstractMultiControl {

    public int getColumns() throws UnifyException {
        return getUplAttribute(int.class, "columns");
    }

    @Override
    public boolean isLayoutCaption() throws UnifyException {
        return false;
    }

}
