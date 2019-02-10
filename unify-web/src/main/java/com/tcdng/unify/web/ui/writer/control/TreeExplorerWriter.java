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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
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

        try {
            TreeExplorer treeExplorer = (TreeExplorer) widget;
            writer.write("ux.rigTreeExplorer(");
            JsonObject jsonPrm = Json.object();
            jsonPrm.add("pId", treeExplorer.getId());
            jsonPrm.add("pContId", treeExplorer.getContainerId());
            jsonPrm.add("pCmdURL", getCommandURL());
            jsonPrm.add("pSelCtrlId", treeExplorer.getSelectedCtrlIdCtrl().getId());
            jsonPrm.add("pSelItemId", treeExplorer.getSelectedItemIdsCtrl().getId());
            jsonPrm.add("pMenuCodeCtrlId", treeExplorer.getMenuCodeCtrl().getId());
            jsonPrm.add("pEventTypeId", treeExplorer.getEventTypeCtrl().getId());
            jsonPrm.add("pSel", "tsel");
            jsonPrm.add("pNorm", "tnorm");
            jsonPrm.add("pCtrlBase", treeExplorer.getControlImgIdBase());
            jsonPrm.add("pLblBase", treeExplorer.getCaptionIdBase());
            jsonPrm.add("pEventCode", Json.array(EVENT_CODES));

            // Added to be able to push values on tree event
            List<String> pageNames = getPageManager().getExpandedReferences(treeExplorer.getId());
            if (!pageNames.isEmpty()) {
                jsonPrm.add("pEventRef", Json.array(pageNames.toArray(new String[pageNames.size()])));
            }

            if (treeExplorer.hasMenuList()) {
                jsonPrm.add("pMenu", getJsonMenu(treeExplorer.getPrefixedId(RESERVED_MENU_PREFIX), treeExplorer.getMenuList()));
            }

            JsonArray menus = Json.array();
            for (TreeItemCategory treeItemCategory : treeExplorer.getTreeCategories()) {
                if (treeItemCategory.isMenuList()) {
                    menus.add(getJsonMenu(treeExplorer.getPrefixedId(treeItemCategory.getName()),
                            treeItemCategory.getMenuList()));
                }
            }
            jsonPrm.add("pMenus", menus);

            JsonArray items = Json.array();
            Node<TreeItem> root = treeExplorer.getRootNode();
            if (root.isParent()) {
                writeChildListBehaviorItems(items, treeExplorer, root);
            }
            jsonPrm.add("pItemList", items);

            jsonPrm.writeTo(writer.getStringWriter());
            writer.write(");");
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
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
                        tree.getTreePolicy().getTreeItemCaption(treeItemCategory, treeItem.getObject()));
            } else {
                writer.writeWithHtmlEscape(String.valueOf(treeItem.getObject()));
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

    private void writeChildListBehaviorItems(JsonArray items, TreeExplorer tree, Node<TreeItem> node)
            throws UnifyException {
        Node<TreeItem> ch = node.getChild();
        do {
            TreeItem treeItem = ch.getItem();
            TreeItemCategory treeItemCategoryInfo = treeItem.getCategory();
            String popupId = "pop_" + tree.getPrefixedId(treeItemCategoryInfo.getName());
            Set<EventType> eventTypes = treeItemCategoryInfo.getEventTypes();
            JsonObject item = Json.object();
            item.add("idx", ch.getMark());
            if (treeItemCategoryInfo.isMenuList()) {
                item.add("popupId", popupId);
            }

            item.add("parent", ch.isParent());
            item.add("expanded", treeItem.isExpanded());
            item.add("pClick", eventTypes.contains(EventType.MOUSE_CLICK));
            item.add("pDblClick", eventTypes.contains(EventType.MOUSE_DBLCLICK));
            item.add("pRtClick", eventTypes.contains(EventType.MOUSE_RIGHTCLICK));
            items.add(item);

            if (ch.isParent() && treeItem.isExpanded()) {
                writeChildListBehaviorItems(items, tree, ch);
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

    private JsonObject getJsonMenu(String menuId, List<TreeMenuItem> menuItemList) throws UnifyException {
        JsonObject menu = Json.object();
        menu.add("menuId", menuId);
        menu.add("popupId", "pop_" + menuId);

        JsonArray items = Json.array();
        for (int i = 0; i < menuItemList.size(); i++) {
            TreeMenuItem menuItem = menuItemList.get(i);
            JsonObject item = Json.object();
            item.add("id", menuId + i);
            item.add("code", menuItem.getCode());
            item.add("multiple", menuItem.isShowOnMultiple());
            items.add(item);
        }
        menu.add("items", items);
        return menu;
    }

}
