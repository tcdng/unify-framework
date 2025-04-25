/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.control;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.MarkedTree.Node;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.TreeExplorer;
import com.tcdng.unify.web.ui.widget.data.TreeItem;
import com.tcdng.unify.web.ui.widget.data.TreeItemTypeInfo;
import com.tcdng.unify.web.ui.widget.data.TreeMenuItemInfo;
import com.tcdng.unify.web.ui.widget.data.TreeTypeInfo.ExtendedTreeItemTypeInfo;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Tree explorer writer.
 * 
 * @author The Code Department
 * @since 4.1
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
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		TreeExplorer treeExplorer = (TreeExplorer) widget;
		writer.beginFunction("ux.rigTreeExplorer");
		writer.writeParam("pId", treeExplorer.getId());
		writer.writeParam("pContId", treeExplorer.getContainerId());
		writer.writeCommandURLParam("pCmdURL");
		writer.writeParam("pSelCtrlId", treeExplorer.getSelectedCtrlIdCtrl().getId());
		writer.writeParam("pSelItemId", treeExplorer.getSelectedItemIdsCtrl().getId());
		writer.writeParam("pDropTrgItemId", treeExplorer.getDropTrgItemIdCtrl().getId());
		writer.writeParam("pDropSrcId", treeExplorer.getDropSrcIdCtrl().getId());
		writer.writeParam("pDropSrcItemId", treeExplorer.getDropSrcItemIdsCtrl().getId());
		writer.writeParam("pMenuCodeCtrlId", treeExplorer.getMenuCodeCtrl().getId());
		writer.writeParam("pEventTypeId", treeExplorer.getEventTypeCtrl().getId());
		writer.writeParam("pSel", "tsel");
		writer.writeParam("pNorm", "tnorm");
		writer.writeParam("pIco", "ticon");
		writer.writeParam("pIcod", "ticond");
		writer.writeParam("pCtrlBase", treeExplorer.getControlImgIdBase());
		writer.writeParam("pLblBase", treeExplorer.getCaptionIdBase());
		writer.writeParam("pIconBase", treeExplorer.getIconIdBase());

		// Added to be able to push values on tree event
		List<String> pageNames = getPageManager().getExpandedReferences(treeExplorer.getId());
		if (!pageNames.isEmpty()) {
			writer.writeParam("pEventRef", DataUtils.toArray(String.class, pageNames));
		}

		if (treeExplorer.hasMenu()) {
			String getPathId = getRequestContextUtil().getResponsePathParts().getControllerPathId();
			writer.writeParam("pConfURL", getContextURL(getPathId, "/confirm"));

			String menuId = treeExplorer.getMenuId();
			String sepId = treeExplorer.getMenuSeperatorId();

			JsonWriter jwMenu = new JsonWriter();
			jwMenu.beginObject();
			jwMenu.write("id", menuId);
			jwMenu.write("normCls", "mnrm");
			jwMenu.write("sepCls", "msep");
			jwMenu.write("sepId", treeExplorer.getMenuSeperatorId());
			jwMenu.beginArray("items");
			int i = 0;
			for (TreeMenuItemInfo menuItem : treeExplorer.getMenuItemInfoList()) {
				jwMenu.beginObject();
				jwMenu.write("id", sepId + i);
				jwMenu.write("code", menuItem.getCode());
				jwMenu.write("grpIdx", menuItem.getGroupIndex());
				if (!StringUtils.isBlank(menuItem.getConfirm())) {
					writer.write("pConf\":");
					writeStringParameter(writer, resolveSessionMessage(menuItem.getConfirm()));
					jwMenu.write("pIconIndex", 3);
				}
				jwMenu.endObject();

				i++;
			}
			jwMenu.endArray();
			jwMenu.endObject();
			writer.writeParam("pMenu", jwMenu);

			if (treeExplorer.isMultiSelectMenu()) {
				writer.writeParam("pMsMenu",
						DataUtils.toArray(Integer.class, treeExplorer.getMultiSelectMenuSequence()));
			}
		}

		// Write item type information
		JsonWriter jwTypes = new JsonWriter();
		jwTypes.beginArray();
		for (ExtendedTreeItemTypeInfo extTypeInfo : treeExplorer.getExtendedTreeItemTypeInfos()) {
			TreeItemTypeInfo typeInfo = extTypeInfo.getTreeItemTypeInfo();
			jwTypes.beginObject();
			jwTypes.write("code", typeInfo.getCode());
			jwTypes.write("flags", typeInfo.getEventFlags());
			jwTypes.write("acceptdrop", DataUtils.toArray(String.class, typeInfo.getAcceptDropList()));
			jwTypes.write("menu", DataUtils.toArray(Integer.class, extTypeInfo.getMenuSequence()));
			jwTypes.endObject();
		}
		jwTypes.endArray();
		writer.writeParam("pItemTypeList", jwTypes);

		JsonWriter jwItems = new JsonWriter();
		jwItems.beginArray();
		Node<TreeItem> root = treeExplorer.getRootNode();
		if (root.isParent()) {
			writeChildListBehaviorItems(jwItems, treeExplorer, root);
		}
		jwItems.endArray();
		writer.writeParam("pItemList", jwItems);

		// writer.write("]});");
		writer.endFunction();
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
			writer.writeWithHtmlEscape(
					tree.getTreePolicy().getTreeItemCaption(treeItemTypeInfo, treeItem.getContent()));
			writer.write("</span></span>");

			// Close branch
			writer.write("</li>");

			// Child branch
			if (ch.isParent() && treeItem.isExpanded()) {
				writeChildListStructure(writer, tree, ch, visibleItemIds, chIndent);
			}

		} while ((ch = ch.getNext()) != null);
	}

	private void writeChildListBehaviorItems(JsonWriter jwItems, TreeExplorer tree, Node<TreeItem> node)
			throws UnifyException {
		Node<TreeItem> ch = node.getChild();
		do {
			TreeItem treeItem = ch.getItem();
			TreeItemTypeInfo treeItemTypeInfo = treeItem.getTypeInfo();
			jwItems.beginObject();
			jwItems.write("idx", ch.getMark());
			jwItems.write("pidx", node.getMark());
			jwItems.write("type", treeItemTypeInfo.getCode());
			int flags = 0;
			if (ch.isParent()) {
				flags |= PARENT_FLAG;
			}

			if (treeItem.isExpanded()) {
				flags |= EXPANDED_FLAG;
			}

			jwItems.write("flags", flags);
			jwItems.endObject();

			if (ch.isParent() && treeItem.isExpanded()) {
				writeChildListBehaviorItems(jwItems, tree, ch);
			}
		} while ((ch = ch.getNext()) != null);
	}

	private void writeIndent(ResponseWriter writer) throws UnifyException {
		writer.write("<span class=\"tindent\"></span>");
	}

}
