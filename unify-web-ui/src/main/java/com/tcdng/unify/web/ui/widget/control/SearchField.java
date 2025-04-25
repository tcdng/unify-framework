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
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.widget.ListParamType;
import com.tcdng.unify.web.ui.widget.data.RefreshSection;

/**
 * A search input control.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-search")
@UplAttributes({ @UplAttribute(name = "filterLabel", type = String.class, defaultVal = "$m{search.filter}"),
        @UplAttribute(name = "buttonImgSrc", type = String.class, defaultVal = "$t{images/search.png}"),
        @UplAttribute(name = "buttonSymbol", type = String.class, defaultVal = "search"),
        @UplAttribute(name = "listParamType", type = ListParamType.class, defaultVal = "immediate") })
public class SearchField extends AbstractListPopupTextField {

    private String filter;

    private boolean keyOnly;

    @Override
    public ExtensionType getExtensionType() {
        return ExtensionType.FACADE_HIDDEN;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public String[] getListParams() throws UnifyException {
        String[] params = new String[4];
        if (keyOnly) {
            params[0] = getStringValue();
        }

        params[1] = filter;
        return params;
    }

    @Action
    public void search() throws UnifyException {
        filter = getRequestTarget(String.class);
        setKeyOnly(false);

        setRequestAttribute(UnifyWebRequestAttributeConstants.REFRESH_SECTION,
                new RefreshSection(this, getResultPanelId()));
        setCommandResultMapping(ResultMappingConstants.REFRESH_SECTION);
    }

    public String getFilter() {
        return filter;
    }

    public boolean isKeyOnly() {
        return keyOnly;
    }

    public void setKeyOnly(boolean keyOnly) {
        this.keyOnly = keyOnly;
    }

    public String getFilterLabel() throws UnifyException {
        return resolveSessionMessage(getUplAttribute(String.class, "filterLabel"));
    }

    public String getFilterId() throws UnifyException {
        return getPrefixedId("fil_");
    }

    public String getSearchPanelId() throws UnifyException {
        return getPrefixedId("sch_");
    }

    public String getResultPanelId() throws UnifyException {
        return getPrefixedId("rlt_");
    }

    public String getClearButtonId() throws UnifyException {
        return getPrefixedId("clr_");
    }

    public String getCancelButtonId() throws UnifyException {
        return getPrefixedId("can_");
    }
}
