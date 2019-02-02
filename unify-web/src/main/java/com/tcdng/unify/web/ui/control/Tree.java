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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.MarkedTree.Node;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.data.EventType;
import com.tcdng.unify.web.ui.data.TreeInfo;
import com.tcdng.unify.web.ui.data.TreeInfo.MenuInfo;
import com.tcdng.unify.web.ui.data.TreeInfo.TreeEvent;
import com.tcdng.unify.web.ui.data.TreeInfo.TreeItemInfo;
import com.tcdng.unify.web.ui.data.TreeItemCategoryInfo;

/**
 * Represents a tree control.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-tree")
@UplAttributes({ @UplAttribute(name = "collapsedIcon", type = String.class, defaultValue = "$t{images/collapsed.png}"),
        @UplAttribute(name = "expandedIcon", type = String.class, defaultValue = "$t{images/expanded.png}"),
        @UplAttribute(name = "treeRule", type = String.class, defaultValue = "default-treeitemrule"),
        @UplAttribute(name = "treeEventPath", type = String.class),
        @UplAttribute(name = "dataComponents", type = UplElementReferences.class) })
public class Tree extends AbstractMultiControl {

    private Control eventTypeCtrl;

    private Control menuCodeCtrl;

    private Control selectedItemIdsCtrl;

    private Control selectedCtrlIdCtrl;

    private EventType eventType;

    private String menuCode;

    private List<Long> singleSelectedItemId;

    private Long selectedCtrlId;

    private TreeItemRule treeItemRule;

    private TreeInfo treeInfo;

    @Override
    public void onPageInitialize() throws UnifyException {
        eventTypeCtrl = (Control) addInternalChildControl("!ui-hidden binding:eventType");
        menuCodeCtrl = (Control) addInternalChildControl("!ui-hidden binding:menuCode");
        selectedItemIdsCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedItemIds");
        selectedCtrlIdCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedCtrlId");
        singleSelectedItemId = new ArrayList<Long>(1);
        treeItemRule = (TreeItemRule) getComponent(getUplAttribute(String.class, "treeRule"));
    }

    @Override
    public void addPageAliases() throws UnifyException {
        addPageAlias(menuCodeCtrl);
        addPageAlias(selectedItemIdsCtrl);
        addPageAlias(selectedCtrlIdCtrl);
        addPageAlias(eventTypeCtrl);
    }

    @Action
    public void collapse() throws UnifyException {
        treeInfo.collapse(selectedCtrlId);
    }

    @Action
    public void expand() throws UnifyException {
        treeInfo.expand(selectedCtrlId);
    }

    @Action
    public void executeEventPath() throws UnifyException {
        String treeEventPath = getTreeEventPath();
        if (!StringUtils.isBlank(treeEventPath)) {
            treeInfo.registerEvent(eventType, menuCode);
            getRequestContextUtil().setCommandResponsePath(treeEventPath);
            menuCode = null;
        }
    }

    public void setTreeInfo(TreeInfo treeInfo) {
        this.treeInfo = treeInfo;
    }

    public TreeItemRule getTreeItemRule() {
        return treeItemRule;
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

    public Long getSelectedCtrlId() {
        return selectedCtrlId;
    }

    public void setSelectedCtrlId(Long selectedCtrlId) {
        this.selectedCtrlId = selectedCtrlId;
    }

    public List<Long> getSelectedItemIds() {
        return treeInfo.getSelectedItems();
    }

    public void setSelectedItemIds(List<Long> selectedItemIds) {
        treeInfo.setSelectedItems(selectedItemIds);
    }

    public void setSelectedItem(Long itemId) {
        singleSelectedItemId.set(0, itemId);
        setSelectedItemIds(singleSelectedItemId);
    }

    public String getControlImgIdBase() throws UnifyException {
        return getPrefixedId("ctrl_");
    }

    public String getCaptionIdBase() throws UnifyException {
        return getPrefixedId("cap_");
    }

    public Long addTreeItem(Long parentItemId, String categoryName, Object item)
            throws UnifyException {
        return treeInfo.addTreeItem(parentItemId, categoryName, item);
    }

    public TreeItemCategoryInfo getTreeCategoryInfo(String name) {
        return treeInfo.getTreeCategoryInfo(name);
    }

    public Collection<TreeItemCategoryInfo> getTreeCategoryInfos() {
        return treeInfo.getTreeCategoryInfos();
    }

    public Node<TreeItemInfo> getRootNode() {
        return treeInfo.getRootNode();
    }

    public Node<TreeItemInfo> getNode(Long itemId) {
        return treeInfo.getNode(itemId);
    }

    public Node<TreeItemInfo> getNode(TreeEvent treeEvent, int index) {
        return treeInfo.getNode(treeEvent, index);
    }

    public TreeItemInfo getTreeItemInfo(Long itemId) {
        return treeInfo.getTreeItemInfo(itemId);
    }

    public TreeItemInfo getTreeItemInfo(TreeEvent treeEvent, int index) {
        return treeInfo.getTreeItemInfo(treeEvent, index);
    }

    public int itemCount() {
        return treeInfo.itemCount();
    }

    public List<MenuInfo> getMenuList() {
        return treeInfo.getMenuList();
    }

    public boolean isMenu() {
        return treeInfo.isMenu();
    }

    public TreeEvent getEvent() {
        return treeInfo.getEvent();
    }

}
