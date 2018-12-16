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
package com.tcdng.unify.web.ui.panel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.ui.MenuItem;
import com.tcdng.unify.web.ui.Control;

/**
 * Abstract base for flyout menu panels.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "sliderGap", type = int.class, defaultValue = "3"),
        @UplAttribute(name = "scrollRate", type = int.class, defaultValue = "200"),
        @UplAttribute(name = "scrollStepRate", type = int.class, defaultValue = "50"),
        @UplAttribute(name = "backImgSrc", type = String.class),
        @UplAttribute(name = "forwardImgSrc", type = String.class) })
public abstract class AbstractFlyoutMenu extends AbstractMenuPanel implements FlyoutMenu {

    private Map<String, MenuItem> visibleMenuItemMap;

    private Control currentSelCtrl;

    private int currentSel;

    public AbstractFlyoutMenu() {
        visibleMenuItemMap = new HashMap<String, MenuItem>();
    }

    @Override
    public void onPageInitialize() throws UnifyException {
        super.onPageInitialize();
        currentSelCtrl = (Control) addInternalControl("!ui-hidden binding:currentSel");
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
    public String getSliderId() throws UnifyException {
        return getPrefixedId("sld_");
    }

    @Override
    public String getNavId() throws UnifyException {
        return getPrefixedId("nav_");
    }

    @Override
    public String getBackButtonId() throws UnifyException {
        return getPrefixedId("btnb_");
    }

    @Override
    public String getForwardButtonId() throws UnifyException {
        return getPrefixedId("btnf_");
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
    public int getSliderGap() throws UnifyException {
        return getUplAttribute(int.class, "sliderGap");
    }

    @Override
    public int getScrollRate() throws UnifyException {
        return getUplAttribute(int.class, "scrollRate");
    }

    @Override
    public int getScrollStepRate() throws UnifyException {
        return getUplAttribute(int.class, "scrollStepRate");
    }

    @Override
    public Set<String> getMenuItemIds() {
        return visibleMenuItemMap.keySet();
    }

    @Override
    public MenuItem getMenuItem(String pageName) {
        return visibleMenuItemMap.get(pageName);
    }

    public void clear() {
        visibleMenuItemMap.clear();
    }

    public void addMenuItem(String name, MenuItem menuItem) {
        visibleMenuItemMap.put(name, menuItem);
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
    public String getLegend() throws UnifyException {
        return null;
    }
}
