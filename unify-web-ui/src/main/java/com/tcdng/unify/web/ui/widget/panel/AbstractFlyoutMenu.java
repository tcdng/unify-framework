/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.ui.MenuItem;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * Abstract base for flyout menu panels.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractFlyoutMenu extends AbstractMenuPanel implements FlyoutMenu {

    private Map<String, MenuItem> activeMenuItemMap;

    private Control currentSelCtrl;

    private int currentSel;

    public AbstractFlyoutMenu() {
        activeMenuItemMap = new HashMap<String, MenuItem>();
    }

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();
        currentSelCtrl = (Control) addInternalWidget("!ui-hidden binding:currentSel");
    }

    @Override
    public String getSelectId() throws UnifyException {
        return getPrefixedId("sel_");
    }

    @Override
    public String getSliderWinId() throws UnifyException {
        return getPrefixedId("sldw_");
    }

    @Override
    public String getNavId() throws UnifyException {
        return getPrefixedId("nav_");
    }

    @Override
    public String getPopupId() throws UnifyException {
        return getPrefixedId("pop_");
    }

    @Override
    public String getPopupWinId() throws UnifyException {
        return getPrefixedId("popw_");
    }

    @Override
    public String getPopupContentId() throws UnifyException {
        return getPrefixedId("popc_");
    }

    @Override
    public boolean isVertical() {
        return false;
    }

    @Override
    public Set<String> getActiveMenuItemIds() {
        return activeMenuItemMap.keySet();
    }

    @Override
    public MenuItem getActiveMenuItem(String pageName) {
        return activeMenuItemMap.get(pageName);
    }

    public void clear() {
        activeMenuItemMap.clear();
    }

    public void addActiveMenuItem(String name, MenuItem menuItem) {
        activeMenuItemMap.put(name, menuItem);
    }

    @Override
    public Control getCurrentSelCtrl() {
        return currentSelCtrl;
    }

    @Override
    public int getCurrentSel() {
        return currentSel;
    }

    public void setCurrentSel(int currentSel) {
        this.currentSel = currentSel;
    }

    @Override
    public boolean isLayoutCaption() {
        return false;
    }

    @Override
    public boolean isAllowRefresh() {
        return false;
    }

    @Override
    public String getBackImageSrc() throws UnifyException {
        return null;
    }

    @Override
    public String getRefreshPath() throws UnifyException {
        return null;
    }

    @Override
    public int getRefreshEvery() throws UnifyException {
        return 0;
    }

    @Override
    public boolean isRefreshOnUserAct() throws UnifyException {
        return false;
    }

    @Override
    public String getLegend() throws UnifyException {
        return null;
    }
}
