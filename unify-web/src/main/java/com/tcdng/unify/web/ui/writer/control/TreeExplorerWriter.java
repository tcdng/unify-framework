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
package com.tcdng.unify.web.ui.writer.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.MarkedTree.Node;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.TreeExplorer;
import com.tcdng.unify.web.ui.data.EventType;
import com.tcdng.unify.web.ui.data.TreeItemCategory;
import com.tcdng.unify.web.ui.data.TreeItem;
import com.tcdng.unify.web.ui.data.TreeMenuItem;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Tree explorer writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(TreeExplorer.class)
@Component("treeexplorer-writer")
public class TreeExplorerWriter extends AbstractControlWriter {

    private static final String RESERVED_MENU_PREFIX = "trermp";

    private static final String[] EVENT_CODES = { EventType.MOUSE_CLICK.code(), EventType.MOUSE_DBLCLICK.code(),
            EventType.MOUSE_RIGHTCLICK.code(), EventType.MOUSE_MENUCLICK.code() };

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        TreeExplorer treeExplorer = (TreeExplorer) widget;
        writer.write("<div");
        writeTagAttributes(writer, treeExplorer);
        writer.write(">");

        // Main list
        writer.write("<ul class=\"tlist\">");
        List<Long> selectedItemIds = treeExplorer.getSelectedItemIds();
        List<Long> visibleItemIds = new ArrayList<Long>();
        Node<TreeItem> root = treeExplorer.getRootNode();
        if (root.isParent()) {
            writeChildListStructure(writer, treeExplorer, root, visibleItemIds, 0);
        }
        writer.write("</ul>");

        // Hidden controls
        writer.write("<select ");
        writeTagId(writer, treeExplorer.getSelectedItemIdsCtrl());
        writeTagStyle(writer, "display:none;");
        writer.write(" multiple=\"multiple\">");
        for (Long itemId : visibleItemIds) {
            writer.write("<option value=\"").write(itemId).write("\"");
            if (selectedItemIds.contains(itemId)) {
                writer.write(" selected");
            }
            writer.write("></option>");
        }
        writer.write("</select>");

        writer.writeStructureAndContent(treeExplorer.getMenuCodeCtrl());
        writer.writeStructureAndContent(treeExplorer.getSelectedCtrlIdCtrl());
        writer.writeStructureAndContent(treeExplorer.getEventTypeCtrl());
        writer.write("</div>");

        // Main Menu
        if (treeExplorer.hasMenuList()) {
            String menuId = treeExplorer.getPrefixedId(RESERVED_MENU_PREFIX);
            writeMenuStructureAndContent(writer, menuId, treeExplorer.getMenuList());
        }

        // Category menu
        for (TreeItemCategory treeItemCategory : treeExplorer.getTreeCategories()) {
            if (treeItemCategory.isMenuList()) {
                String menuId = treeExplorer.getPrefixedId(treeItemCategory.getName());
                writeMenuStructureAndContent(writer, menuId, treeItemCategory.getMenuList());
            }
        }
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);

        TreeExplorer treeExplorer = (TreeExplorer) widget;
        writer.write("ux.rigTreeExplorer({");
        writer.write("\"pId\":\"").write(treeExplorer.getId()).write('"');
        writer.write(",\"pContId\":\"").write(treeExplorer.getContainerId()).write('"');
        writer.write(",\"pCmdURL\":\"");
        writer.writeCommandURL();
        writer.write('"');
        writer.write(",\"pSelCtrlId\":\"").write(treeExplorer.getSelectedCtrlIdCtrl().getId()).write('"');
        writer.write(",\"pSelItemId\":\"").write(treeExplorer.getSelectedItemIdsCtrl().getId()).write('"');
        writer.write(",\"pMenuCodeCtrlId\":\"").write(treeExplorer.getMenuCodeCtrl().getId()).write('"');
        writer.write(",\"pEventTypeId\":\"").write(treeExplorer.getEventTypeCtrl().getId()).write('"');
        writer.write(",\"pSel\": \"tsel\"");
        writer.write(",\"pNorm\":\"tnorm\"");
        writer.write(",\"pCtrlBase\":\"").write(treeExplorer.getControlImgIdBase()).write('"');
        writer.write(",\"pLblBase\":\"").write(treeExplorer.getCaptionIdBase()).write('"');
        writer.write(",\"pEventCode\":");
        writer.writeJsonStringArray((Object[]) EVENT_CODES);
        // Added to be able to push values on tree event
        List<String> pageNames = getPageManager().getExpandedReferences(treeExplorer.getId());
        if (!pageNames.isEmpty()) {
            writer.write(",\"pEventRef\":");
            writer.writeJsonStringArray(pageNames.toArray(new Object[pageNames.size()]));
        }

        if (treeExplorer.hasMenuList()) {
            writer.write(",\"pMenu\":");
            writeMenuJson(writer, treeExplorer.getPrefixedId(RESERVED_MENU_PREFIX), treeExplorer.getMenuList());
        }

        writer.write(",\"pMenus\":[");
        boolean appendSym = false;
        for (TreeItemCategory treeItemCategory : treeExplorer.getTreeCategories()) {
            if (treeItemCategory.isMenuList()) {
                if (appendSym) {
                    writer.write(",");
                } else {
                    appendSym = true;
                }

                writeMenuJson(writer, treeExplorer.getPrefixedId(treeItemCategory.getName()),
                        treeItemCategory.getMenuList());
            }
        }
        writer.write("]");

        writer.write(",\"pItemList\":[");
        Node<TreeItem> root = treeExplorer.getRootNode();
        if (root.isParent()) {
            writeChildListBehaviorItems(writer, treeExplorer, root, false);
        }
        writer.write("]});");
    }

    private void writeChildListStructure(ResponseWriter writer, TreeExplorer tree, Node<TreeItem> node,
            List<Long> visibleItemIds, int indent) throws UnifyException {
        Node<TreeItem> ch = node.getChild();
        List<Long> selectedItemIds = tree.getSelectedItemIds();
        String collapsedSrc = tree.getCollapsedIcon();
        String expandedSrc = tree.getExpandedIcon();
        String ctrlIdBase = tree.getControlImgIdBase();
        String captionIdBase = tree.getCaptionIdBase();
        int chIndent = indent + 1;
        boolean isTreePolicy = tree.hasTreePolicy();
        do {
            TreeItem treeItem = ch.getItem();
            Long itemId = ch.getMark();
            visibleItemIds.add(itemId);

            // Open branch
            writer.write("<li>");

            // Add left tabs
            for (int j = 0; j < indent; j++) {
                writeIndent(writer);
            }

            // Add control icon
            if (ch.isParent()) {
                if (treeItem.isExpanded()) {
                    writeFileImageHtmlElement(writer, expandedSrc, ctrlIdBase + itemId, "timg", null);
                } else {
                    writeFileImageHtmlElement(writer, collapsedSrc, ctrlIdBase + itemId, "timg", null);
                }
            } else {
                writeIndent(writer);
            }

            // Add item icon and caption
            // TODO In future implement dynamic renderer
            writer.write("<span id=\"").write(captionIdBase).write(itemId);
            if (selectedItemIds.contains(itemId)) {
                writer.write("\" class=\"tsel\">");
            } else {
                writer.write("\" class=\"tnorm\">");
            }

            TreeItemCategory treeItemCategory = treeItem.getCategory();
            writeFileImageHtmlElement(writer, treeItemCategory.getIcon(), null, "timg", null);
            writer.write("<span class=\"titem\">");
            if (isTreePolicy) {
                writer.writeWithHtmlEscape(
                        tree.getTreePolicy().getTreeItemCaption(treeItemCategory, treeItem.getContent()));
            } else {
                writer.writeWithHtmlEscape(String.valueOf(treeItem.getContent()));
            }
            writer.write("</span></span>");

            // Close branch
            writer.write("</li>");

            // Child branch
            if (ch.isParent() && treeItem.isExpanded()) {
                writeChildListStructure(writer, tree, ch, visibleItemIds, chIndent);
            }

        } while ((ch = ch.getNext()) != null);
    }

    private void writeChildListBehaviorItems(ResponseWriter writer, TreeExplorer tree, Node<TreeItem> node,
            boolean appendSym) throws UnifyException {
        Node<TreeItem> ch = node.getChild();
        do {
            TreeItem treeItem = ch.getItem();
            TreeItemCategory treeItemCategoryInfo = treeItem.getCategory();
            String popupId = "pop_" + tree.getPrefixedId(treeItemCategoryInfo.getName());
            Set<EventType> eventTypes = treeItemCategoryInfo.getEventTypes();
            if (appendSym) {
                writer.write(",");
            } else {
                appendSym = true;
            }

            writer.write("{\"idx\":").write(ch.getMark());
            if (treeItemCategoryInfo.isMenuList()) {
                writer.write(",\"popupId\":\"").write(popupId).write('"');
            }
            writer.write(",\"parent\":").write(ch.isParent());
            writer.write(",\"expanded\":").write(treeItem.isExpanded());
            writer.write(",\"pClick\":").write(eventTypes.contains(EventType.MOUSE_CLICK));
            writer.write(",\"pDblClick\":").write(eventTypes.contains(EventType.MOUSE_DBLCLICK));
            writer.write(",\"pRtClick\":").write(eventTypes.contains(EventType.MOUSE_RIGHTCLICK));
            writer.write("}");

            if (ch.isParent() && treeItem.isExpanded()) {
                writeChildListBehaviorItems(writer, tree, ch, appendSym);
            }
        } while ((ch = ch.getNext()) != null);
    }

    private void writeIndent(ResponseWriter writer) throws UnifyException {
        writer.write("<span class=\"tindent\"></span>");
    }

    private void writeMenuStructureAndContent(ResponseWriter writer, String menuId, List<TreeMenuItem> menuItemList)
            throws UnifyException {
        writer.write("<div");
        writeTagId(writer, "pop_" + menuId);
        writeTagStyleClass(writer, "tree-popup");
        writer.write(">");
        writer.write("<ul id=\"").write("popc_" + menuId).write("\">");
        for (int i = 0; i < menuItemList.size(); i++) {
            TreeMenuItem menuItem = menuItemList.get(i);
            if (menuItem.isSeparator()) {
                writer.write("<li class=\"msep\">");
            } else {
                writer.write("<li>");
            }
            writer.write("<a class=\"mitem\" id=\"").write(menuId + i).write("\">");
            writer.writeWithHtmlEscape(resolveSessionMessage(menuItem.getCaption()));
            writer.write("</a></li>");
        }
        writer.write("</ul>");
        writer.write("</div>");
    }

    private void writeMenuJson(ResponseWriter writer, String menuId, List<TreeMenuItem> menuItemList)
            throws UnifyException {
        writer.write("{\"menuId\":\"").write(menuId).write('"');
        writer.write(",\"popupId\":\"").write("pop_" + menuId).write('"');

        writer.write(",\"items\":[");
        boolean appendSym = false;
        for (int i = 0; i < menuItemList.size(); i++) {
            TreeMenuItem menuItem = menuItemList.get(i);
            if (appendSym) {
                writer.write(",");
            } else {
                appendSym = true;
            }

            writer.write("{\"id\":\"").write(menuId + i).write('"');
            writer.write(",\"code\":\"").write(menuItem.getCode()).write('"');
            writer.write(",\"multiple\":").write(menuItem.isShowOnMultiple());
            writer.write("}");
        }
        writer.write("]}");
    }

}
