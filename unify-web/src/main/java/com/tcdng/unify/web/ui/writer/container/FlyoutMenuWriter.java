/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.web.ui.writer.container;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.ApplicationAttributeConstants;
import com.tcdng.unify.core.ui.Menu;
import com.tcdng.unify.core.ui.MenuItem;
import com.tcdng.unify.core.ui.MenuItemSet;
import com.tcdng.unify.core.ui.MenuSet;
import com.tcdng.unify.web.PageController;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.panel.AbstractFlyoutMenu;
import com.tcdng.unify.web.ui.panel.FlyoutMenu;
import com.tcdng.unify.web.ui.writer.AbstractPanelWriter;
import com.tcdng.unify.web.util.HtmlUtils;

/**
 * Flyout menu writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(FlyoutMenu.class)
@Component("flyoutmenu-writer")
public class FlyoutMenuWriter extends AbstractPanelWriter {

	private static final int DYNAMIC_CHILD_OFFSET = 10;

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		FlyoutMenu flyoutMenu = (FlyoutMenu) widget;
		List<String> menuWinIdList = new ArrayList<String>();
		for (String id : flyoutMenu.getMenuItemIds()) {
			MenuItem menuItem = flyoutMenu.getMenuItem(id);
			if (menuItem.isMain()) {
				String popupId = "pop_" + id;
				String popupContentId = "popc_" + id;
				String menuWinId = "win_" + id;
				StringBuilder psb = new StringBuilder();
				psb.append("{\"menuWinId\":\"").append(menuWinId);
				psb.append("\",\"windowId\":\"").append(flyoutMenu.getSliderWinId());
				psb.append("\",\"popupId\":\"").append(popupId);
				psb.append("\",\"popupContentId\":\"").append(popupContentId).append("\"");
				psb.append(",\"vertical\":").append(flyoutMenu.isVertical()).append("}");
				this.writeOpenPopupJS(writer, "onmouseover", id, null, popupId, 500, "repositionmenupopup",
						psb.toString(), null, null);
				menuWinIdList.add(menuWinId);
			}
		}

		writer.write("ux.rigFlyoutMenu({");
		writer.write("\"pId\":\"").write(flyoutMenu.getId()).write("\"");
		writer.write(",\"pContId\":\"").write(flyoutMenu.getContainerId()).write('"');
		writer.write(",\"pCmdURL\":\"");
		// Resolves out of bean context error which usually happens of menu reload
		Object valueObject = flyoutMenu.getValueStore().getValueObject();
		if (valueObject instanceof PageController) {
			writer.writeCommandURL(((PageController) valueObject).getSessionId());
		} else {
			writer.writeCommandURL();
		}

		writer.write('"');
		writer.write(",\"pMenuWinId\":");
		writer.writeJsonStringArray(menuWinIdList.toArray());
		writer.write(",\"pNavId\":\"").write(flyoutMenu.getNavId()).write("\"");
		writer.write(",\"pSliderId\":\"").write(flyoutMenu.getSliderId()).write("\"");
		writer.write(",\"pSliderWinId\":\"").write(flyoutMenu.getSliderWinId()).write("\"");
		writer.write(",\"pBackBtnId\":\"").write(flyoutMenu.getBackButtonId()).write("\"");
		writer.write(",\"pForwardBtnId\":\"").write(flyoutMenu.getForwardButtonId()).write("\"");
		writer.write(",\"pSliderGap\":").write(flyoutMenu.getSliderGap());
		writer.write(",\"pVertical\":").write(flyoutMenu.isVertical());
		writer.write(",\"pRate\":").write(flyoutMenu.getScrollRate());
		writer.write(",\"pStepRate\":").write(flyoutMenu.getScrollStepRate());

		MenuSet menuSet = (MenuSet) this.getApplicationAttribute(ApplicationAttributeConstants.APPLICATION_MENUSET);
		if (menuSet.isShowSelect()) {
			writer.write(",\"pSelId\":\"").write(flyoutMenu.getSelectId()).write("\"");
			writer.write(",\"pCurSelId\":\"").write(flyoutMenu.getCurrentSelCtrl().getId()).write("\"");
		}

		writer.write(",\"pMenuItems\":[");
		boolean appendSym = false;
		for (String id : flyoutMenu.getMenuItemIds()) {
			MenuItem menuItem = flyoutMenu.getMenuItem(id);
			if (menuItem.getActionPath() != null) {
				if (appendSym) {
					writer.write(",");
				} else {
					appendSym = true;
				}

				writer.write("{\"id\":\"").write(id).write("\"");
				writer.write(",\"main\":").write(menuItem.isMain());
				writer.write(",\"actionPath\":\"").writeContextURL(menuItem.getActionPath()).write("\"}");
			}
		}
		writer.write("]");

		writer.write("});");
	}

	@Override
	protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
		AbstractFlyoutMenu flyoutMenu = (AbstractFlyoutMenu) container;
		String backImg = flyoutMenu.getUplAttribute(String.class, "backImgSrc");
		String forwardImg = flyoutMenu.getUplAttribute(String.class, "forwardImgSrc");
		flyoutMenu.clear();
		writer.write("<div style=\"display:table;width:100%;height:100%;table-layout:fixed;\">");
		writer.write("<div style=\"display:table-row;\">");
		if (flyoutMenu.isVertical()) {
			writer.write("<div style=\"display:table-cell; height:100%;\">");
		} else {
			writer.write("<div style=\"display:table-cell;\">");
		}

		this.appendScrollButton(writer, flyoutMenu, flyoutMenu.getBackButtonId(), backImg);

		writer.write("<div id=\"").write(flyoutMenu.getSliderWinId()).write("\"");
		if (flyoutMenu.isVertical()) {
			writer.write(" style=\"display:inline-block;width:100%;height:100%;overflow-y:hidden;\">");
		} else {
			writer.write(" style=\"display:inline-block;width:100%;height:100%;overflow-x:hidden;\">");
		}
		writer.write("<div id=\"").write(flyoutMenu.getSliderId())
				.write("\" style = \"visibility:hidden;position:relative;\">");
		writer.write("<ul id=\"").write(flyoutMenu.getNavId()).write("\" class=\"nav\">");
		StringBuilder psb = new StringBuilder();

		// Menu set select
		MenuSet menuSet = (MenuSet) this.getApplicationAttribute(ApplicationAttributeConstants.APPLICATION_MENUSET);
		boolean isShowMenu = true;
		if (menuSet.isShowSelect()) {
			List<Integer> visibleIndexList = new ArrayList<Integer>();
			for (int i = 0; i < menuSet.size(); i++) {
				if (this.getPrivilegeSettings(menuSet.getMenu(i).getPrivilege()).isVisible()) {
					visibleIndexList.add(i);
				}
			}

			if (!visibleIndexList.isEmpty() && !visibleIndexList.contains(flyoutMenu.getCurrentSel())) {
				flyoutMenu.setCurrentSel(visibleIndexList.get(0));
			}

			writer.write("<li><select id=\"").write(flyoutMenu.getSelectId()).write("\" class=\"mselect\">");
			for (Integer i : visibleIndexList) {
				writer.write("<option value=\"").write(i).write("\"");
				if (i == flyoutMenu.getCurrentSel()) {
					writer.write(" selected");
				}

				writer.write(">");
				writer.writeWithHtmlEscape(menuSet.getMenu(i).getCaption());
				writer.write("</option>");
			}
			writer.write("</select></li>");

			isShowMenu = !visibleIndexList.isEmpty();
		}

		if (isShowMenu && !menuSet.isEmpty()) {
			// Menu items
			int childIndex = DYNAMIC_CHILD_OFFSET;
			Menu menu = menuSet.getMenu(flyoutMenu.getCurrentSel());
			for (MenuItemSet menuItemSet : menu.getMenuItemSetList()) {
				if (this.getPrivilegeSettings(menuItemSet.getPrivilege()).isVisible()) {
					String menuId = flyoutMenu.getNamingIndexedId(childIndex++);
					flyoutMenu.addMenuItem(menuId, menuItemSet);
					writer.write("<li id=\"").write("win_" + menuId).write("\">");
					writer.write("<a class=\"option\" id=\"").write(menuId).write("\">");
					writer.writeWithHtmlEscape(this.resolveSessionMessage(menuItemSet.getCaption()));
					writer.write("</a>");
					writer.write("</li>");

					List<MenuItem> menuItemList = menuItemSet.getMenuItemList();
					if (!menuItemList.isEmpty()) {
						psb.append("<div");
						this.writeTagId(psb, "pop_" + menuId);
						this.writeTagStyleClass(psb, "flyoutmenu-popup");
						psb.append(">");
						psb.append("<ul id=\"").append("popc_" + menuId).append("\">");
						for (MenuItem menuItem : menuItemList) {
							if (this.getPrivilegeSettings(menuItem.getPrivilege()).isVisible()) {
								String menuItemId = flyoutMenu.getNamingIndexedId(childIndex++);
								flyoutMenu.addMenuItem(menuItemId, menuItem);
								psb.append("<li><a class=\"mitem\" id=\"").append(menuItemId).append("\">");
								HtmlUtils.writeStringWithHtmlEscape(psb,
										this.resolveSessionMessage(menuItem.getCaption()));
								psb.append("</a></li>");
							}
						}
						psb.append("</ul>");
						psb.append("</div>");
					}
				}
			}
		}

		writer.write("</ul>");
		writer.write("</div>");
		writer.write("</div>");

		this.appendScrollButton(writer, flyoutMenu, flyoutMenu.getForwardButtonId(), forwardImg);

		writer.write("</div>");
		writer.write("</div>");
		writer.write("</div>");

		writer.writeStructureAndContent(flyoutMenu.getCurrentSelCtrl());

		writer.write(psb.toString());
	}

	private void appendScrollButton(ResponseWriter writer, AbstractFlyoutMenu flyoutMenu, String buttonId,
			String imageSrc) throws UnifyException {
		writer.write("<div id=\"").write(buttonId);
		if (flyoutMenu.isVertical()) {
			writer.write("\" class=\"scroll\" style=\"display:table-cell; width:100%; vertical-align:middle;\">");
		} else {
			writer.write("\" class=\"scroll\" style=\"display:table-cell; height:100%; vertical-align:middle;\">");
		}
		writer.write("<img src=\"");
		writer.writeFileImageContextURL(imageSrc);
		if (flyoutMenu.isVertical()) {
			writer.write("\" style=\"height:100%;max-width:100%;\"/>");
		} else {
			writer.write("\" style=\"width:100%;max-height:100%;\"/>");
		}
		writer.write("</div>");
	}

}
