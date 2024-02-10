/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.container;

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
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.AbstractFlyoutMenu;
import com.tcdng.unify.web.ui.widget.panel.FlyoutMenu;
import com.tcdng.unify.web.ui.widget.writer.AbstractPanelWriter;
import com.tcdng.unify.web.util.HtmlUtils;

/**
 * Flyout menu writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(FlyoutMenu.class)
@Component("flyoutmenu-writer")
public class FlyoutMenuWriter extends AbstractPanelWriter {

    private static final int DYNAMIC_CHILD_OFFSET = 10;

    private static final String MENU_CATEGORY_CLASSBASE = "opcat";

    private static final String MENUITEM_CATEGORY_CLASSBASE = "mcat";

    private static final String ORIGINAL_MENU_PATHID = "ORIGINAL_MENU_PATHID";

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers) throws UnifyException {
        FlyoutMenu flyoutMenu = (FlyoutMenu) widget;
        List<String> menuWinIdList = new ArrayList<String>();
        for (String id : flyoutMenu.getActiveMenuItemIds()) {
            MenuItem menuItem = flyoutMenu.getActiveMenuItem(id);
            if (menuItem.isMain()) {
                String popupId = "pop_" + id;
                String popupContentId = "popc_" + id;
                String menuWinId = "win_" + id;
                String json = new JsonWriter()
                                    .beginObject()
                                        .write("menuWinId", menuWinId)
                                        .write("windowId", flyoutMenu.getSliderWinId())
                                        .write("popupId", popupId)
                                        .write("popupContentId", popupContentId)
                                        .write("vertical", flyoutMenu.isVertical())
                                    .endObject().toString();
                writeOpenPopupJS(writer, flyoutMenu, "onmouseover", id, widget.getBinding(), null, popupId, 500,
                        "repositionmenupopup", json, null, null);
                menuWinIdList.add(menuWinId);
            }
        }

        writer.beginFunction("ux.rigFlyoutMenu");
        writer.writeParam("pId", flyoutMenu.getId());
        writer.writeParam("pContId", flyoutMenu.getContainerId());
        // Resolves out of bean context error which usually happens of menu reload
        String originalPathId = (String) getSessionAttribute(ORIGINAL_MENU_PATHID);
        if (!StringUtils.isBlank(originalPathId)) {
            writer.writeCommandURLParam("pCmdURL", originalPathId);
        } else {
            originalPathId = getRequestContextUtil().getResponsePathParts().getControllerPathId();
            setSessionAttribute(ORIGINAL_MENU_PATHID, originalPathId);
            writer.writeCommandURLParam("pCmdURL");
        }

        writer.writeParam("pMenuWinId", DataUtils.toArray(String.class, menuWinIdList));
        writer.writeParam("pNavId", flyoutMenu.getNavId());
        writer.writeParam("pVertical", flyoutMenu.isVertical());

        MenuSet menuSet = (MenuSet) getApplicationAttribute(ApplicationAttributeConstants.APPLICATION_MENUSET);
        if (menuSet.isShowSelect()) {
            writer.writeParam("pSelId", flyoutMenu.getSelectId());
            writer.writeParam("pCurSelId", flyoutMenu.getCurrentSelCtrl().getId());
        }

        JsonWriter itemsJson = new JsonWriter();
        itemsJson.beginArray();
        for (String id : flyoutMenu.getActiveMenuItemIds()) {
            MenuItem menuItem = flyoutMenu.getActiveMenuItem(id);
            if (getViewDirective(menuItem.getPrivilege()).isVisible()) {
                if (!StringUtils.isBlank(menuItem.getActionPath())) {
                    itemsJson.beginObject();
                    itemsJson.write("id",id);
                    itemsJson.write("main", menuItem.isMain());
                    itemsJson.write("actionPath", getContextURL(menuItem.getActionPath()));
                    if (!StringUtils.isBlank(menuItem.getOriginPath())) {
                        itemsJson.write("originPath", menuItem.getOriginPath());
                    }
                    itemsJson.endObject();
                }
            }
        }
        itemsJson.endArray();
        writer.writeParam("pMenuItems", itemsJson);

        writer.endFunction();
    }

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        AbstractFlyoutMenu flyoutMenu = (AbstractFlyoutMenu) container;
        flyoutMenu.clear();
        writer.write("<div style=\"display:table;width:100%;height:100%;table-layout:fixed;\">");
        writer.write("<div style=\"display:table-row;\">");
        if (flyoutMenu.isVertical()) {
            writer.write("<div style=\"display:table-cell; height:100%;\">");
        } else {
            writer.write("<div style=\"display:table-cell;\">");
        }

        writer.write("<div id=\"").write(flyoutMenu.getSliderWinId()).write("\"");
        writer.write(" style=\"display:inline-block;width:100%;height:100%;\">");
        writer.write("<ul id=\"").write(flyoutMenu.getNavId()).write("\" class=\"nav\">");
        StringBuilder psb = new StringBuilder();

        // Menu set select
        MenuSet menuSet = (MenuSet) getApplicationAttribute(ApplicationAttributeConstants.APPLICATION_MENUSET);
        boolean isShowMenu = true;
        if (menuSet.isShowSelect()) {
            List<Integer> visibleIndexList = new ArrayList<Integer>();
            for (int i = 0; i < menuSet.size(); i++) {
                if (getViewDirective(menuSet.getMenu(i).getPrivilege()).isVisible()) {
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

            String opcat = MENU_CATEGORY_CLASSBASE;
            String mcat = MENUITEM_CATEGORY_CLASSBASE;
            if (StringUtils.isNotBlank(menu.getColorScheme())) {
                String scheme = menu.getColorScheme();
                opcat += scheme;
                mcat += scheme;
            } else {
                opcat = getUserColorStyleClass(opcat);
                mcat = getUserColorStyleClass(mcat);
            }

            for (MenuItemSet menuItemSet : menu.getMenuItemSetList()) {
                if (getViewDirective(menuItemSet.getPrivilege()).isVisible()) {
                    String menuId = flyoutMenu.getNamingIndexedId(childIndex++);
                    flyoutMenu.addActiveMenuItem(menuId, menuItemSet);
                    writer.write("<li id=\"").write("win_" + menuId).write("\">");
                    writer.write("<a class=\"option ").write(opcat).write("\" id=\"").write(menuId).write("\">");
                    writer.writeWithHtmlEscape(resolveSessionMessage(menuItemSet.getCaption()));
                    writer.write("</a>");
                    writer.write("</li>");

                    List<MenuItem> menuItemList = menuItemSet.getMenuItemList();
                    if (!menuItemList.isEmpty()) {
                        psb.append("<div");
                        writeTagId(psb, "pop_" + menuId);
                        writeTagStyleClass(psb, "flyoutmenu-popup");
                        psb.append(">");
                        psb.append("<ul id=\"").append("popc_" + menuId).append("\">");
                        for (MenuItem menuItem : menuItemList) {
                            if (getViewDirective(menuItem.getPrivilege()).isVisible()) {
                                String menuItemId = flyoutMenu.getNamingIndexedId(childIndex++);
                                flyoutMenu.addActiveMenuItem(menuItemId, menuItem);
                                if (!menuItem.isHidden()) {
                                    psb.append("<li><a class=\"mitem ").append(mcat).append("\" id=\"")
                                            .append(menuItemId).append("\">");
                                    HtmlUtils.writeStringWithHtmlEscape(psb,
                                            resolveSessionMessage(menuItem.getCaption()));
                                    psb.append("</a></li>");
                                }
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
        writer.write("</div>");
        writer.write("</div>");

        writer.writeStructureAndContent(flyoutMenu.getCurrentSelCtrl());

        writer.write(psb.toString());
    }

}
