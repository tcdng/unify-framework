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
package com.tcdng.unify.web.ui.widget.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;
import com.tcdng.unify.web.ui.widget.ListControl;
import com.tcdng.unify.web.ui.widget.ListControlInfo;
import com.tcdng.unify.web.ui.widget.ListControlUtils;
import com.tcdng.unify.web.ui.widget.ListParamType;
import com.tcdng.unify.web.ui.widget.WriteWork;

/**
 * Abstract base class for list pop-up text fields.
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
        @UplAttribute(name = "exclude", type = String[].class),})
public abstract class AbstractListPopupTextField extends AbstractPopupTextField implements ListControl {

    private static final String WORK_LIST_INFO = "workListInfo";
    
    private Set<String> excludes;
    
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
        if (!excludes.isEmpty()) {
            List<Listable> list = new ArrayList<Listable>();
            for (Listable listable: getListControlUtils().getListables(this)) {
                if (!excludes.contains(listable.getListKey())) {
                    list.add(listable);
                }
            }
            
            return list;
        }
        
        return getListControlUtils().getListables(this);
    }

    @Override
    public Map<String, Listable> getListMap() throws UnifyException {
        if (!excludes.isEmpty()) {
            Map<String, Listable> map = new HashMap<String, Listable>();
            for (Map.Entry<String, Listable> entry : getListControlUtils().getListMap(this).entrySet()) {
                if (!excludes.contains(entry.getKey())) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }

            return map;
        }

        return getListControlUtils().getListMap(this);
    }

    @Override
    public boolean isPopupOnEditableOnly() {
        return false;
    }

    @Override
	public boolean isHtmlEscape() throws UnifyException {
        return getUplAttribute(boolean.class, "htmlEscape");
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
    protected void onInitialize() throws UnifyException {
        super.onInitialize();

        String[] exclude = getUplAttribute(String[].class, "exclude");
        if (exclude != null && exclude.length > 0) {
            excludes = new HashSet<String>(Arrays.asList(exclude));
        } else {
            excludes = Collections.emptySet();
        }
    }

    private ListControlUtils getListControlUtils() throws UnifyException {
        return (ListControlUtils) getComponent(WebUIApplicationComponents.APPLICATION_LISTCONTROLUTIL);
    }
}
