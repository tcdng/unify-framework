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
import com.tcdng.unify.web.ui.data.TreeItemInfo;

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
public class Tree<T> extends AbstractMultiControl {

    private Control eventTypeCtrl;

    private Control menuCodeCtrl;

    private Control selectedItemIndexCtrl;

    private Control selectedCtrlIndexCtrl;

    private EventType eventType;

    private String menuCode;

    private List<Integer> selectedItemIndexes;

    private int selectedCtrlIndex;

    @Override
    public void onPageInitialize() throws UnifyException {
        eventTypeCtrl = (Control) addInternalChildControl("!ui-hidden binding:eventType");
        menuCodeCtrl = (Control) addInternalChildControl("!ui-hidden binding:menuCode");
        selectedItemIndexCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedItemIndexes");
        selectedCtrlIndexCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedCtrlIndex");
        selectedItemIndexes = Collections.emptyList();
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

    public Control getSelectedItemIndexCtrl() {
        return selectedItemIndexCtrl;
    }

    public Control getSelectedCtrlIndexCtrl() {
        return selectedCtrlIndexCtrl;
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

    public List<Integer> getSelectedItemIndexes() {
        return selectedItemIndexes;
    }

    @SuppressWarnings("unchecked")
    public void setSelectedItemIndexes(List<Integer> selectedItemIndexes) {
        this.selectedItemIndexes = selectedItemIndexes;
        try {
            TreeInfo<T> treeInfo = (TreeInfo<T>) getValue();
            if (treeInfo != null) {
                treeInfo.setSelectedItems(selectedItemIndexes);
            }
        } catch (UnifyException e) {
            logError(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void setSelectedItem(Object id) {
        try {
            TreeInfo<T> treeInfo = (TreeInfo<T>) getValue();
            if (treeInfo != null) {
                int index = treeInfo.setSelectedItem(id);
                if (index >= 0) {
                    selectedItemIndexes = Arrays.asList(index);
                }
            }
        } catch (UnifyException e) {
            logError(e);
        }
    }

    public String getControlImgIdBase() throws UnifyException {
        return getPrefixedId("ctrl_");
    }

    public String getCaptionIdBase() throws UnifyException {
        return getPrefixedId("cap_");
    }

    public int getItemCount() throws UnifyException {
        TreeInfo<T> treeInfo = getTreeInfo();
        if (treeInfo != null) {
            return treeInfo.itemCount();
        }
        return 0;
    }

    public TreeItemInfo<T> getTreeItemInfo(int index) throws UnifyException {
        TreeInfo<T> treeInfo = getTreeInfo();
        if (treeInfo != null && index >= 0 && index < treeInfo.itemCount()) {
            return treeInfo.getTreeItemInfo(index);
        }
        return null;
    }

    public List<TreeItemInfo<T>> getSelectedItems() throws UnifyException {
        TreeInfo<T> treeInfo = getTreeInfo();
        if (treeInfo != null) {
            return treeInfo.getSelectedItems();
        }

        return Collections.emptyList();
    }

    @Action
    public void collapse() throws UnifyException {
        TreeInfo<T> treeInfo = getTreeInfo();
        if (treeInfo != null) {
            treeInfo.collapse(selectedCtrlIndex);
        }
    }

    @Action
    public void expand() throws UnifyException {
        TreeInfo<T> treeInfo = getTreeInfo();
        if (treeInfo != null) {
            treeInfo.expand(selectedCtrlIndex);
        }
    }

    @Action
    public void executeEventPath() throws UnifyException {
        String treeEventPath = getTreeEventPath();
        if (!StringUtils.isBlank(treeEventPath)) {
            TreeInfo<T> treeInfo = getTreeInfo();
            if (treeInfo != null) {
                treeInfo.registerEvent(eventType, menuCode);
                getRequestContextUtil().setCommandResponsePath(treeEventPath);
                menuCode = null;
            }
        }
    }

    public void addPageAliases() throws UnifyException {
        addPageAlias(menuCodeCtrl);
        addPageAlias(selectedItemIndexCtrl);
        addPageAlias(selectedCtrlIndexCtrl);
        addPageAlias(eventTypeCtrl);
    }

    public int getSelectedCtrlIndex() {
        return selectedCtrlIndex;
    }

    public void setSelectedCtrlIndex(int selectedCtrlIndex) {
        this.selectedCtrlIndex = selectedCtrlIndex;
    }

    @SuppressWarnings("unchecked")
    private TreeInfo<T> getTreeInfo() throws UnifyException {
        return (TreeInfo<T>) getValue();
    }

}
