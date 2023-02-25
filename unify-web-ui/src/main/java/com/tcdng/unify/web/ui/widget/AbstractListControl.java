/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;

/**
 * A convenient base class for controls that make use of a list.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplAttributes({
        @UplAttribute(name = "list", type = String.class),
        @UplAttribute(name = "listBinding", type = String.class),
        @UplAttribute(name = "listParams", type = String[].class),
        @UplAttribute(name = "listKey", type = String.class),
        @UplAttribute(name = "listDescription", type = String.class),
        @UplAttribute(name = "listParamType", type = ListParamType.class, defaultVal = "control"),
        @UplAttribute(name = "htmlEscape", type = boolean.class, defaultVal = "true"),
        @UplAttribute(name = "flow", type = boolean.class) })
public abstract class AbstractListControl extends AbstractControl implements ListControl {

    private static final String WORK_LIST_INFO = "workListInfo";
    
    @Override
    public ListControlInfo getListControlInfo(Formatter<Object> formatter) throws UnifyException {
        WriteWork writeWork = getWriteWork();
        ListControlInfo listControlInfo = writeWork.get(ListControlInfo.class, WORK_LIST_INFO);
        if (listControlInfo == null) {
            listControlInfo = getListControlUtils().getListControlInfo(this, formatter);
            writeWork.set(WORK_LIST_INFO, listControlInfo);
        }

        return listControlInfo;
    }

    @Override
    public List<? extends Listable> getListables() throws UnifyException {
        return getListControlUtils().getListables(this);
    }

    @Override
    public Map<String, Listable> getListMap() throws UnifyException {
        return getListControlUtils().getListMap(this);
    }

    @Override
    public String getList() throws UnifyException {
        return getUplAttribute(String.class, "list", "listBinding");
    }

    @Override
    public String[] getListParams() throws UnifyException {
        return getUplAttribute(String[].class, "listParams");
    }

    @Override
    public ListParamType getListParamType() throws UnifyException {
        return getUplAttribute(ListParamType.class, "listParamType");
    }

    @Override
    public String getListKey() throws UnifyException {
        return getUplAttribute(String.class, "listKey");
    }

    @Override
    public String getListDescription() throws UnifyException {
        return getUplAttribute(String.class, "listDescription");
    }

    @Override
	public boolean isHtmlEscape() throws UnifyException {
    	 return getUplAttribute(boolean.class, "htmlEscape");
    }

	private ListControlUtils getListControlUtils() throws UnifyException {
        return (ListControlUtils) getComponent(WebUIApplicationComponents.APPLICATION_LISTCONTROLUTIL);
    }
}
