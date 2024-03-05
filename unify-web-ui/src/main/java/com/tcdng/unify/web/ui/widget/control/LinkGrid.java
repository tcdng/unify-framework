/*
 * Copyright 2018-2024 The Code Department.
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
import com.tcdng.unify.web.ui.widget.data.LinkGridInfo;

/**
 * A link grid widget.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-linkgrid")
@UplAttributes({
	@UplAttribute(name = "columns", type = int.class),
	@UplAttribute(name = "columnsBinding", type = String.class)})
public class LinkGrid extends AbstractMultiControl {

    public int getColumns() throws UnifyException {
		String columnsBinding = getUplAttribute(String.class, "columnsBinding");
		if (columnsBinding != null && !columnsBinding.isEmpty()) {
			return getValue(int.class, columnsBinding);
		}

        return getUplAttribute(int.class, "columns");
    }

    public LinkGridInfo getLinkGridInfo() throws UnifyException {
    	return getValue(LinkGridInfo.class);
    }
    
    @Override
    public boolean isLayoutCaption() throws UnifyException {
        return false;
    }

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        
    }

}
