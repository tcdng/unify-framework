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
package com.tcdng.unify.web.ui.widget.panel;

import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.list.SearchProvider;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.QueryUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.widget.AbstractPanel;
import com.tcdng.unify.web.ui.widget.data.SearchBox;

/**
 * A search box panel.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-searchboxpanel")
@UplBinding("web/panels/upl/searchboxpanel.upl")
@UplAttributes({ @UplAttribute(name = "searchProvider", type = String.class, mandatory = true) })
public class SearchBoxPanel extends AbstractPanel {

    private String filter;

    private List<?> resultList;

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();
        setComponentValueBeanToThis("filter");
        setComponentValueBeanToThis("searchResultTablePanel");
    }

    @Override
    @Action
    public void switchState() throws UnifyException {
        if (QueryUtils.isValidStringCriteria(filter)) {
            SearchProvider searchProvider =
                    (SearchProvider) this.getComponent(getUplAttribute(String.class, "searchProvider"));
            resultList = searchProvider.search(filter);
        } else {
            resultList = Collections.emptyList();
        }
    }

    public void clear() throws UnifyException {
        filter = null;
        resultList = null;
    }

    public void setResultBeanProperties() throws UnifyException {
        SearchBox searchBoxInfo = (SearchBox) getValue();
        TablePanel tablePanel = (TablePanel) getWidgetByShortName("searchResultTablePanel");
        Object item = resultList.get(tablePanel.getTable().getViewIndex());
        Object resultBean = searchBoxInfo.getResultBean();
        for (SearchBox.Mapping mapping : searchBoxInfo.getMappings()) {
            Object fieldValue = ReflectUtils.getBeanProperty(item, mapping.getSelectFieldName());
            DataUtils.setBeanProperty(resultBean, mapping.getResultFieldName(), fieldValue);
        }
    }

    public <T> T getSelectedItemProperty(Class<T> typeClass, String property) throws UnifyException {
        TablePanel tablePanel = (TablePanel) getWidgetByShortName("searchResultTablePanel");
        Object item = resultList.get(tablePanel.getTable().getViewIndex());
        return convert(typeClass, ReflectUtils.getBeanProperty(item, property), null);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<?> getResultList() {
        return resultList;
    }

    public void setResultList(List<?> resultList) {
        this.resultList = resultList;
    }
}
