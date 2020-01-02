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
package com.tcdng.unify.web.ui.writer.control;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.MarkedTree.Node;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.TreeExplorer;
import com.tcdng.unify.web.ui.data.TreeItem;
import com.tcdng.unify.web.ui.data.TreeItemTypeInfo;
import com.tcdng.unify.web.ui.data.TreeMenuItemInfo;
import com.tcdng.unify.web.ui.data.TreeTypeInfo.ExtendedTreeItemTypeInfo;
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

    private final int PARENT_FLAG = 0x0001;

    private final int EXPANDED_FLAG = 0x0002;

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
        writer.writeStructureAndContent(treeExplorer.getDropTrgItemIdCtrl());
        writer.writeStructureAndContent(treeExplorer.getDropSrcIdCtrl());
        writer.writeStructureAndContent(treeExplorer.getDropSrcItemIdsCtrl());
        writer.writeStructureAndContent(treeExplorer.getEventTypeCtrl());
        writer.write("</div>");

        // Tree menu
        if (treeExplorer.hasMenu()) {
            String menuId = treeExplorer.getMenuId();
            String sepId = treeExplorer.getMenuSeperatorId();
            writer.write("<div");
            writeTagId(writer, menuId);
            writeTagStyleClass(writer, "tree-popup");
            writer.write(">");
            writer.write("<ul id=\"").write(treeExplorer.getMenuBaseId()).write("\">");
            int i = 0;
            for (TreeMenuItemInfo menuItem : treeExplorer.getMenuItemInfoList()) {
                writer.write("<li id=\"").write(sepId + i).write("\" name=\"").write(sepId).write("\">");
                writer.write("<a class=\"mitem\" id=\"").write(menuId + i).write("\">");
                writer.writeWithHtmlEscape(resolveSessionMessage(menuItem.getCaption()));
                writer.write("</a></li>");
                i++;
            }
            writer.write("</ul>");
            writer.write("</div>");
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
        writer.write(",\"pDropTrgItemId\":\"").write(treeExplorer.getDropTrgItemIdCtrl().getId()).write('"');
        writer.write(",\"pDropSrcId\":\"").write(treeExplorer.getDropSrcIdCtrl().getId()).write('"');
        writer.write(",\"pDropSrcItemId\":\"").write(treeExplorer.getDropSrcItemIdsCtrl().getId()).write('"');
        writer.write(",\"pMenuCodeCtrlId\":\"").write(treeExplorer.getMenuCodeCtrl().getId()).write('"');
        writer.write(",\"pEventTypeId\":\"").write(treeExplorer.getEventTypeCtrl().getId()).write('"');
        writer.write(",\"pSel\": \"tsel\"");
        writer.write(",\"pNorm\":\"tnorm\"");
        writer.write(",\"pIco\": \"ticon\"");
        writer.write(",\"pIcod\":\"ticond\"");
        writer.write(",\"pCtrlBase\":\"").write(treeExplorer.getControlImgIdBase()).write('"');
        writer.write(",\"pLblBase\":\"").write(treeExplorer.getCaptionIdBase()).write('"');
        writer.write(",\"pIconBase\":\"").write(treeExplorer.getIconIdBase()).write('"');

        // Added to be able to push values on tree event
        List<String> pageNames = getPageManager().getExpandedReferences(treeExplorer.getId());
        if (!pageNames.isEmpty()) {
            writer.write(",\"pEventRef\":").writeJsonArray(pageNames);
        }

        if (treeExplorer.hasMenu()) {
            String menuId = treeExplorer.getMenuId();
            String sepId = treeExplorer.getMenuSeperatorId();
            writer.write(",\"pMenu\":");
            writer.write("{\"id\":\"").write(menuId).write('"');
            writer.write(",\"normCls\":\"mnrm\"");
            writer.write(",\"sepCls\":\"msep\"");
            writer.write(",\"sepId\":\"").write(treeExplorer.getMenuSeperatorId()).write('"');
            writer.write(",\"items\":[");
            boolean appendSym = false;
            int i = 0;
            for (TreeMenuItemInfo menuItem : treeExplorer.getMenuItemInfoList()) {
                if (appendSym) {
                    writer.write(",");
                } else {
                    appendSym = true;
                }

                writer.write("{\"id\":\"").write(sepId + i).write('"');
                writer.write(",\"code\":\"").write(menuItem.getCode()).write('"');
                writer.write(",\"grpIdx\":").write(menuItem.getGroupIndex());
                writer.write("}");
                i++;
            }
            writer.write("]}");
        }

        // Write item type information
        boolean appendSym = false;
        writer.write(",\"pItemTypeList\":[");
        for (ExtendedTreeItemTypeInfo extTypeInfo : treeExplorer.getExtendedTreeItemTypeInfos()) {
            if (appendSym) {
                writer.write(",");
            } else {
                appendSym = true;
            }

            TreeItemTypeInfo typeInfo = extTypeInfo.getTreeItemTypeInfo();
            writer.write("{\"code\":\"").write(typeInfo.getCode()).write("\"");
            writer.write(",\"flags\":").write(typeInfo.getEventFlags());
            writer.write(",\"acceptdrop\":").writeJsonArray(typeInfo.getAcceptDropList());
            writer.write(",\"menu\":").writeJsonArray(extTypeInfo.getMenuSequence());
            writer.write("}");
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
        String iconIdBase = tree.getIconIdBase();
        int chIndent = indent + 1;
        boolean isTreePolicy = tree.hasTreePolicy();
        do {
            TreeItem treeItem = ch.getItem();
            TreeItemTypeInfo treeItemTypeInfo = treeItem.getTypeInfo();
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
                writer.write("\" class=\"tsel\"");
            } else {
                writer.write("\" class=\"tnorm\"");
            }

            if (treeItemTypeInfo.isDraggable()) {
                writer.write(" draggable=\"true\">");
            } else {
                writer.write(">");
            }

            writeFileImageHtmlElement(writer, treeItemTypeInfo.getIcon(), iconIdBase + itemId, "ticon", null);
            writer.write("<span class=\"titem\">");
            if (isTreePolicy) {
                writer.writeWithHtmlEscape(
                        tree.getTreePolicy().getTreeItemCaption(treeItemTypeInfo, treeItem.getContent()));
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
            TreeItemTypeInfo treeItemTypeInfo = treeItem.getTypeInfo();
            if (appendSym) {
                writer.write(",");
            } else {
                appendSym = true;
            }

            writer.write("{\"idx\":").write(ch.getMark());
            writer.write(",\"pidx\":").write(node.getMark());
            writer.write(",\"type\":\"").write(treeItemTypeInfo.getCode()).write('"');
            int flags = 0;
            if (ch.isParent()) {
                flags |= PARENT_FLAG;
            }

            if (treeItem.isExpanded()) {
                flags |= EXPANDED_FLAG;
            }

            writer.write(",\"flags\":").write(flags);
            writer.write("}");

            if (ch.isParent() && treeItem.isExpanded()) {
                writeChildListBehaviorItems(writer, tree, ch, appendSym);
            }
        } while ((ch = ch.getNext()) != null);
    }

    private void writeIndent(ResponseWriter writer) throws UnifyException {
        writer.write("<span class=\"tindent\"></span>");
    }

}
