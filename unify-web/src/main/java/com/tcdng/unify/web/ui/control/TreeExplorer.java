/*
 * Copyright 2018-2020 The Code Department.
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
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.TargetPath;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.data.Tree;
import com.tcdng.unify.web.ui.data.TreeEvent;
import com.tcdng.unify.web.ui.data.TreeEventType;
import com.tcdng.unify.web.ui.data.TreeItem;
import com.tcdng.unify.web.ui.data.TreeItemTypeInfo;
import com.tcdng.unify.web.ui.data.TreeMenuItemInfo;
import com.tcdng.unify.web.ui.data.TreePolicy;
import com.tcdng.unify.web.ui.data.TreeTypeInfo.ExtendedTreeItemTypeInfo;

/**
 * Represents a tree explorer control.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-treeexplorer")
@UplAttributes({ @UplAttribute(name = "collapsedIcon", type = String.class, defaultVal = "$t{images/collapsed.png}"),
        @UplAttribute(name = "expandedIcon", type = String.class, defaultVal = "$t{images/expanded.png}"),
        @UplAttribute(name = "treeEventPath", type = String.class),
        @UplAttribute(name = "dataComponents", type = UplElementReferences.class) })
public class TreeExplorer extends AbstractMultiControl {

    private Control eventTypeCtrl;

    private Control menuCodeCtrl;

    private Control selectedItemIdsCtrl;

    private Control selectedCtrlIdCtrl;

    private Control dropTrgItemIdCtrl;

    private Control dropSrcIdCtrl;

    private Control dropSrcItemIdsCtrl;

    private TreeEventType eventType;

    private String menuCode;

    private List<Long> singleSelectedItemId;

    private Long selectedCtrlId;

    private Long dropTrgItemId;

    private String dropSrcId;

    private String dropSrcItemIds;

    private Tree tree;

    @Override
    public void onPageConstruct() throws UnifyException {
        eventTypeCtrl = (Control) addInternalChildControl("!ui-hidden binding:eventType");
        menuCodeCtrl = (Control) addInternalChildControl("!ui-hidden binding:menuCode");
        selectedItemIdsCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedItemIds");
        selectedCtrlIdCtrl = (Control) addInternalChildControl("!ui-hidden binding:selectedCtrlId");
        dropTrgItemIdCtrl = (Control) addInternalChildControl("!ui-hidden binding:dropTrgItemId");
        dropSrcIdCtrl = (Control) addInternalChildControl("!ui-hidden binding:dropSrcId");
        dropSrcItemIdsCtrl = (Control) addInternalChildControl("!ui-hidden binding:dropSrcItemIds");
        singleSelectedItemId = new ArrayList<Long>(1);
        singleSelectedItemId.add(null);
    }

    @Override
    public void addPageAliases() throws UnifyException {
        addPageAlias(menuCodeCtrl);
        addPageAlias(selectedItemIdsCtrl);
        addPageAlias(selectedCtrlIdCtrl);
        addPageAlias(dropTrgItemIdCtrl);
        addPageAlias(dropSrcIdCtrl);
        addPageAlias(dropSrcItemIdsCtrl);
        addPageAlias(eventTypeCtrl);
    }

    @Action
    public void collapse() throws UnifyException {
        tree.collapse(selectedCtrlId);
    }

    @Action
    public void expand() throws UnifyException {
        tree.expand(selectedCtrlId);
    }

    @SuppressWarnings("unchecked")
    @Action
    public void executeEventPath() throws UnifyException {
        String treeEventPath = getTreeEventPath();
        if (StringUtils.isNotBlank(treeEventPath)) {
            if (TreeEventType.TREEITEM_DROP.equals(eventType)) {
                String srcLongName = getPageManager().getLongName(dropSrcId);
                List<Long> srcItemIdList =
                        DataUtils.convert(List.class, Long.class, StringUtils.commaSplit(dropSrcItemIds), null);
                tree.registerEvent(eventType, dropTrgItemId, srcLongName, srcItemIdList);
            } else {
                tree.registerEvent(eventType, menuCode);
            }

            setCommandResponsePath(new TargetPath(treeEventPath));
            menuCode = null;
        }
    }

    public void setTree(Tree tree) throws UnifyException {
        this.tree = tree;
    }

    public TreePolicy getTreePolicy() {
        return tree.getTreePolicy();
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

    public String getMenuId() throws UnifyException {
        return getPrefixedId("m_");
    }

    public String getMenuBaseId() throws UnifyException {
        return getPrefixedId("mb_");
    }

    public String getMenuSeperatorId() throws UnifyException {
        return getPrefixedId("sp_");
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

    public Control getDropTrgItemIdCtrl() {
        return dropTrgItemIdCtrl;
    }

    public Control getDropSrcIdCtrl() {
        return dropSrcIdCtrl;
    }

    public Control getDropSrcItemIdsCtrl() {
        return dropSrcItemIdsCtrl;
    }

    public TreeEventType getEventType() {
        return eventType;
    }

    public void setEventType(TreeEventType eventType) {
        this.eventType = eventType;
    }

    public Long getDropTrgItemId() {
        return dropTrgItemId;
    }

    public void setDropTrgItemId(Long dropTrgItemId) {
        this.dropTrgItemId = dropTrgItemId;
    }

    public String getDropSrcId() {
        return dropSrcId;
    }

    public void setDropSrcId(String dropSrcId) {
        this.dropSrcId = dropSrcId;
    }

    public String getDropSrcItemIds() {
        return dropSrcItemIds;
    }

    public void setDropSrcItemIds(String dropSrcItemIds) {
        this.dropSrcItemIds = dropSrcItemIds;
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
        return tree.getSelectedItems();
    }

    public void setSelectedItemIds(List<Long> selectedItemIds) {
        tree.setSelectedItems(selectedItemIds);
    }

    public void setSelectedItem(Long itemId) {
        singleSelectedItemId.set(0, itemId);
        setSelectedItemIds(singleSelectedItemId);
    }

    public String getControlImgIdBase() throws UnifyException {
        return getPrefixedId("ctrl_");
    }

    public String getIconIdBase() throws UnifyException {
        return getPrefixedId("icon_");
    }

    public String getCaptionIdBase() throws UnifyException {
        return getPrefixedId("cap_");
    }

    public Long addTreeItem(Long parentItemId, String itemTypeCode, Object item) throws UnifyException {
        return tree.addTreeItem(parentItemId, itemTypeCode, item);
    }

    public TreeItemTypeInfo getTreeItemTypeInfo(String itemTypeCode) {
        return tree.getTreeItemTypeInfo(itemTypeCode);
    }

    public Node<TreeItem> getRootNode() {
        return tree.getRootNode();
    }

    public Node<TreeItem> getParentNode(Long itemId) {
        return tree.getParentNode(itemId);
    }

    public Node<TreeItem> getNode(Long itemId) {
        return tree.getNode(itemId);
    }

    public Node<TreeItem> getNode(TreeEvent treeEvent, int index) {
        return tree.getNode(treeEvent, index);
    }

    public TreeItem getTreeItem(Long itemId) {
        return tree.getTreeItem(itemId);
    }

    public TreeItem getTreeItem(TreeEvent treeEvent, int index) {
        return tree.getTreeItem(treeEvent, index);
    }

    public TreeItem removeTreeItem(Long mark) throws UnifyException {
        return tree.removeTreeItem(mark);
    }

    public int itemCount() {
        return tree.itemCount();
    }

    public Collection<ExtendedTreeItemTypeInfo> getExtendedTreeItemTypeInfos() {
        return tree.getExtendedTreeItemTypeInfos();
    }

    public List<TreeMenuItemInfo> getMenuItemInfoList() {
        return tree.getMenuItemInfoList();
    }

    public boolean hasMenu() {
        return tree.isMenuItemList();
    }

    public TreeEvent getEvent() {
        return tree.getEvent();
    }

    public List<Integer> getMultiSelectMenuSequence() {
        return tree.getMultiSelectMenuSequence();
    }

    public boolean isMultiSelectMenu() {
        return tree.isMultiSelectMenu();
    }

}
