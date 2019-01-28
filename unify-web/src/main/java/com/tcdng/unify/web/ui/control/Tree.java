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
package com.tcdng.unify.web.ui.control;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.data.EventType;
import com.tcdng.unify.web.ui.data.TreeInfo;
import com.tcdng.unify.web.ui.data.TreeInfo.TreeItemInfo;

/**
 * Represents a tree control.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-tree")
@UplAttributes({ @UplAttribute(name = "collapsedIcon", type = String.class, defaultValue = "$t{images/collapsed.png}"),
        @UplAttribute(name = "expandedIcon", type = String.class, defaultValue = "$t{images/expanded.png}"),
        @UplAttribute(name = "treeEventPath", type = String.class),
        @UplAttribute(name = "dataComponents", type = UplElementReferences.class)})
public class Tree extends AbstractMultiControl {

    private Control eventTypeCtrl;

    private Control menuCodeCtrl;

    private Control selectedItemIdsCtrl;

    private Control selectedCtrlIdCtrl;

    private EventType eventType;

    private String menuCode;

    private List<Integer> selectedItemIds;

    private Integer selectedCtrlId;

    @Override
    public void onPageInitialize() throws UnifyException {
        eventTypeCtrl = (Control) addInternalChildControl("!ui-hidden binding:eventType");
        menuCodeCtrl = (Control) addInternalChildControl("!ui-hidden binding:menuCode");
        selectedItemIdsCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedItemIds");
        selectedCtrlIdCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedCtrlId");
        selectedItemIds = Collections.emptyList();
    }

    public String getCollapsedIcon() throws UnifyException {
        return getUplAttribute(String.class, "collapsedIcon");
    }

    public String getExpandedIcon() throws UnifyException {
        return getUplAttribute(String.class, "expandedIcon");
    }

    public String getTreeEventPath() throws UnifyException {
        return getUplAttribute(String.class, "treeEventPath");
    }
    
    public Control getEventTypeCtrl() {
        return eventTypeCtrl;
    }

    public Control getMenuCodeCtrl() {
        return menuCodeCtrl;
    }

    public Control getSelectedItemIdsCtrl() {
        return selectedItemIdsCtrl;
    }

    public Control getSelectedCtrlIdCtrl() {
        return selectedCtrlIdCtrl;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public int getSelectedCtrlId() {
        return selectedCtrlId;
    }

    public void setSelectedCtrlId(Integer selectedCtrlId) {
        this.selectedCtrlId = selectedCtrlId;
    }
    
    public List<Integer> getSelectedItemIds() {
        return selectedItemIds;
    }

    public void setSelectedItemIds(List<Integer> selectedItemIds) {
        this.selectedItemIds = selectedItemIds;
        try {
            TreeInfo treeInfo = getTreeInfo();
            if (treeInfo != null) {
                treeInfo.setSelectedItems(selectedItemIds);
            }
        } catch (UnifyException e) {
            logError(e);
        }
    }

    public void setSelectedItem(Integer itemId) {
        setSelectedItemIds(Arrays.asList(itemId));
    }

    public String getControlImgIdBase() throws UnifyException {
        return getPrefixedId("ctrl_");
    }

    public String getCaptionIdBase() throws UnifyException {
        return getPrefixedId("cap_");
    }

    public int getItemCount() throws UnifyException {
        TreeInfo treeInfo = getTreeInfo();
        if (treeInfo != null) {
            return treeInfo.itemCount();
        }
        return 0;
    }

    public TreeItemInfo getTreeItemInfo(Integer itemId) throws UnifyException {
        TreeInfo treeInfo = getTreeInfo();
        if (treeInfo != null) {
            return treeInfo.getTreeItemInfo(itemId);
        }

        return null;
    }

    @Action
    public void collapse() throws UnifyException {
        TreeInfo treeInfo = getTreeInfo();
        if (treeInfo != null) {
            treeInfo.collapse(selectedCtrlId);
        }
    }

    @Action
    public void expand() throws UnifyException {
        TreeInfo treeInfo = getTreeInfo();
        if (treeInfo != null) {
            treeInfo.expand(selectedCtrlId);
        }
    }

    @Action
    public void executeEventPath() throws UnifyException {
        String treeEventPath = getTreeEventPath();
        if (!StringUtils.isBlank(treeEventPath)) {
            TreeInfo treeInfo = getTreeInfo();
            if (treeInfo != null) {
                treeInfo.registerEvent(eventType, menuCode);
                getRequestContextUtil().setCommandResponsePath(treeEventPath);
                menuCode = null;
            }
        }
    }

    public void addPageAliases() throws UnifyException {
        addPageAlias(menuCodeCtrl);
        addPageAlias(selectedItemIdsCtrl);
        addPageAlias(selectedCtrlIdCtrl);
        addPageAlias(eventTypeCtrl);
    }

    private TreeInfo getTreeInfo() throws UnifyException {
        return (TreeInfo) getValue();
    }

}
