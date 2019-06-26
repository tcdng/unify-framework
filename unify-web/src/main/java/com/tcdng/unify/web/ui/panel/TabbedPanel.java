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
package com.tcdng.unify.web.ui.panel;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.Widget;

/**
 * Represents a tabbed panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-tabbedpanel")
@UplBinding("web/panels/upl/tabbedpanel.upl")
@UplAttributes({ @UplAttribute(name = "tabPosition", type = TabPosition.class, defaultValue = "top") })
public class TabbedPanel extends SwitchPanel {

    private String activeTabId;

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();
        setComponentValueBeanToThis("selectedTabId");
        for (String longName : getLayoutWidgetLongNames()) {
            Widget widget = getWidgetByLongName(longName);
            if (!widget.isHidden()) {
                activeTabId = widget.getId();
                break;
            }
        }
    }

    @Override
    @Action
    public void switchState() throws UnifyException {
        String shortName = getWidgetByLongName(getPageManager().getLongName(activeTabId)).getShortName();
        switchContent(shortName);
    }

    public TabPosition getTabPosition() throws UnifyException {
        return getUplAttribute(TabPosition.class, "tabPosition");
    }

    public String getSelectedTabId() throws UnifyException {
        return getWidgetByShortName("selectedTabId").getId();
    }

    public String getActiveTabId() throws UnifyException {
        return activeTabId;
    }

    public void setActiveTabId(String activeTabPageName) {
        activeTabId = activeTabPageName;
    }

    public List<String> getActiveTabExpandedIdList() throws UnifyException {
        Widget widget = getCurrentWidget();
        if (widget instanceof Container) {
            return getPageManager().getPageNames(((Container) widget).getWidgetLongNames());
        }

        List<String> resultList = new ArrayList<String>();
        resultList.add(widget.getId());
        return resultList;
    }

    public List<String> getTabIds() throws UnifyException {
        List<String> resultList = new ArrayList<String>();
        for (String longName : getLayoutWidgetLongNames()) {
            Widget widget = getWidgetByLongName(longName);
            if (!widget.isHidden()) {
                resultList.add(widget.getId());
            }
        }
        return resultList;
    }

    public Control getSelectedTabIdCtrl() throws UnifyException {
        return (Control) getWidgetByShortName("selectedTabId");
    }
}
