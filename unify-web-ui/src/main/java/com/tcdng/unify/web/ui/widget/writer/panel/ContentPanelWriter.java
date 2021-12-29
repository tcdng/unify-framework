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
package com.tcdng.unify.web.ui.widget.writer.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.ui.PageAttributeConstants;
import com.tcdng.unify.web.ui.PageRequestContextUtil;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.ContentPanelImpl;
import com.tcdng.unify.web.ui.widget.panel.ContentPanelImpl.ContentInfo;
import com.tcdng.unify.web.ui.widget.writer.AbstractPanelWriter;

/**
 * Content panel writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(ContentPanelImpl.class)
@Component("contentpanel-writer")
public class ContentPanelWriter extends AbstractPanelWriter {

    private static final String CPREMOTE_CATEGORYBASE = "cpcat";

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        ContentPanelImpl contentPanelImpl = (ContentPanelImpl) widget;

        // Write content variables
        writer.beginFunction("ux.rigContentPanel");
        writer.writeParam("pId", contentPanelImpl.getId());
        writer.writeParam("pHintPanelId", contentPanelImpl.getHintPanelId());
        if (contentPanelImpl.getPageCount() > 0) {
            // Close image
            String closeImgId = contentPanelImpl.getTabItemImgId(contentPanelImpl.getPageIndex());
            writer.writeParam("pCloseImgId", closeImgId);
        }

        if (contentPanelImpl.getPageCount() == 0) {
            writer.writeParam("pImmURL", getContextURL(contentPanelImpl.getPath()));
        } else {
            writer.writeParam("pCurIdx", contentPanelImpl.getPageIndex());
            ContentInfo currentContentInfo = contentPanelImpl.getCurrentContentInfo();
            if (currentContentInfo.isRemoteSave()) {
                writer.writeParam("pSavePath", currentContentInfo.getSavePath());
                writer.writeParam("pSaveIsRem", true);
            } else {
                writer.writeParam("pSavePath", getContextURL(currentContentInfo.getSavePath()));
                writer.writeParam("pSaveIsRem", false);
            }

            JsonWriter jw = new JsonWriter();
            jw.beginArray();
            for (int i = 0; i < contentPanelImpl.getPageCount(); i++) {
                ContentInfo contentInfo = contentPanelImpl.getContentInfo(i);
                jw.beginObject();
                jw.write("tabId", contentPanelImpl.getTabItemId(i));
                jw.write("tabImgId", contentPanelImpl.getTabItemImgId(i));
                jw.write("openPath", getContextURL(contentInfo.getOpenPath()));
                jw.write("closePath", getContextURL(contentInfo.getClosePath()));
                jw.endObject();
            }
            jw.endArray();
            writer.writeParam("pContent", jw);
        }

        if (contentPanelImpl.isTabbed() && contentPanelImpl.getPageCount() > 0) {
            writer.writeParam("pTabPaneId", contentPanelImpl.getTabPaneId());
            writer.writeParam("pMenuId", contentPanelImpl.getMenuId());
        }

        writer.endFunction();

        if (contentPanelImpl.isSidebar()) {
            writer.writeBehavior(contentPanelImpl.getSidebar());
        }

        if (contentPanelImpl.getPageCount() > 0) {
            // Set response page controller
            PageRequestContextUtil rcu = getRequestContextUtil();
            ControllerPathParts currentRespPathParts = rcu.getResponsePathParts();
            ContentInfo currentContentInfo = contentPanelImpl.getCurrentContentInfo();
            rcu.setResponsePathParts(currentContentInfo.getPathParts());
            writer.writeBehavior(currentContentInfo.getPage());

            // Restore response controller
            rcu.setResponsePathParts(currentRespPathParts);
        }
    }

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        ContentPanelImpl contentPanelImpl = (ContentPanelImpl) container;
        writer.write("<div id=\"").write(contentPanelImpl.getHintPanelId()).write("\" class=\"cphint\"></div>");
        writer.write("<div id=\"").write(contentPanelImpl.getBusyIndicatorId()).write("\" class=\"cpbusy\">");
        writer.write("<img class=\"cpimage\" src=\"");
        writer.writeContextResourceURL("/resource/file", MimeType.IMAGE.template(), "$t{images/busy.gif}");
        writer.write("\"></div>");

        writer.write("<div id=\"").write(contentPanelImpl.getBaseContentId())
                .write("\" style=\"display:table;width:100%;height:100%;\">");
        boolean isSidebar = contentPanelImpl.isSidebar();
        // Frame
        if (isSidebar) {
            writer.write("<div style=\"display:table-row;width:100%;\">");
            writer.write("<div style=\"display:table-cell;width:100%;height:100%;vertical-align:top;\">");
            writer.write("<div style=\"display:table;width:100%;height:100%;\">");
        }

        if (contentPanelImpl.getPageCount() > 0) {
            writeContentPanel(writer, contentPanelImpl);
        }

        if (isSidebar) {
            writer.write("</div>");
            writer.write("</div>");
            writer.write("<div style=\"display:table-cell;height:100%;vertical-align:top;\">");
            writer.writeStructureAndContent(contentPanelImpl.getSidebar());
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("</div>");
    }

    private void writeContentPanel(ResponseWriter writer, ContentPanelImpl contentPanelImpl) throws UnifyException {
        PageRequestContextUtil rcUtil = getRequestContextUtil();
        ContentInfo currentContentInfo = contentPanelImpl.getCurrentContentInfo();

        // Tabs
        if (contentPanelImpl.isTabbed()) {
            writer.write("<div style=\"display:table-row;width:100%;\">");
            writer.write("<div style=\"display:table-cell;\">");
            writer.write("<div id=\"").write(contentPanelImpl.getTabPaneId()).write("\" class=\"cptabbar\">");

            writer.write("<ul class=\"cptab\">");
            for (int i = 0; i < contentPanelImpl.getPageCount(); i++) {
                ContentInfo contentInfo = contentPanelImpl.getContentInfo(i);
                writer.write("<li");
                if (i == contentPanelImpl.getPageIndex()) {
                    writer.write(" class=\"cpactive\"");
                } else {
                    writer.write(" class=\"cpinactive\"");
                }

                Page page = contentInfo.getPage();
                String title = (String) page.getAttribute(PageAttributeConstants.PAGE_TITLE);
                if (StringUtils.isBlank(title)) {
                    title = page.getCaption();
                }

                String subTitle = page.getSubCaption();

                writer.write("><div><a ");
                if (page.getUplAttribute(boolean.class, "remote")) {
                    String cpcat = CPREMOTE_CATEGORYBASE;
                    if (StringUtils.isNotBlank(contentInfo.getColorScheme())) {
                        cpcat = CPREMOTE_CATEGORYBASE + contentInfo.getColorScheme();
                    }

                    writer.write("class=\"cpremote ").write(cpcat).write("\"");
                } else {
                    if (subTitle != null) {
                        writer.write("class=\"cpt\"");
                    }
                }

                writer.write(" id=\"").write(contentPanelImpl.getTabItemId(i)).write("\">");
                writer.write("<div class=\"cptitle\">");
                writer.write("<span class=\"hd\" title=\"").writeWithHtmlEscape(title).write("\">")
                        .writeWithHtmlEscape(title).write("</span>");
                if (subTitle != null) {
                    writer.write("<span class=\"hds\" title=\"").writeWithHtmlEscape(subTitle).write("\">")
                            .writeWithHtmlEscape(subTitle).write("</span>");
                }
                writer.write("</div>");

                writer.write("</a>");

                if (i > 0) {
                    writer.write("<img id=\"").write(contentPanelImpl.getTabItemImgId(i)).write("\" src=\"");
                    writer.writeFileImageContextURL("$t{images/cross_gray.png}");
                    writer.write("\"/>");
                }
                writer.write("</div></li>");
            }
            writer.write("</ul>");

            writer.write("</div>");
            writer.write("</div>");
            writer.write("</div>");

            // Menu
            String menuId = contentPanelImpl.getMenuId();
            writer.write("<div");
            writeTagId(writer, menuId);
            writeTagStyleClass(writer, "contentpanel-popup");
            writer.write(">");
            writer.write("<ul id=\"").write(contentPanelImpl.getMenuBaseId()).write("\">");
            writer.write("<li><a class=\"mitem\" id=\"").write(contentPanelImpl.getMenuCloseId()).write("\">");
            writer.writeWithHtmlEscape(resolveSessionMessage("$m{contentpanel.close}"));
            writer.write("</a></li>");
            writer.write("<li><a class=\"mitem\" id=\"").write(contentPanelImpl.getMenuCloseOtherId()).write("\">");
            writer.writeWithHtmlEscape(resolveSessionMessage("$m{contentpanel.closeothertabs}"));
            writer.write("</a></li>");
            writer.write("<li class=\"msep\"><a class=\"mitem\" id=\"").write(contentPanelImpl.getMenuCloseAllId())
                    .write("\">");
            writer.writeWithHtmlEscape(resolveSessionMessage("$m{contentpanel.closealltabs}"));
            writer.write("</a></li>");
            writer.write("</ul>");
            writer.write("</div>");
        }
        // End tabs

        // Title bar
        if (contentPanelImpl.isTitleBar()) {
            writer.write("<div style=\"display:table-row;width:100%;\">");
            writer.write("<div style=\"display:table-cell;\">");
            writer.write("<div class=\"cptitlebar\">");

            writer.write("<div class=\"cpbar\">");
            writer.write("<span class=\"cpspan\">");
            writer.writeWithHtmlEscape(currentContentInfo.getPage().getCaption());
            writer.write("</span>");
            writer.write("</div>");

            writer.write("</div>");
            writer.write("</div>");
            writer.write("</div>");
        }
        // End title bar

        // Body
        writer.write("<div style=\"display:table-row;width:100%;height:100%;\">");
        writer.write("<div style=\"display:table-cell;\">");
        writer.write("<div class=\"cpbody\">");

        ControllerPathParts currentRespPathParts = rcUtil.getResponsePathParts();
        rcUtil.setResponsePathParts(currentContentInfo.getPathParts());
        writer.writeStructureAndContent(currentContentInfo.getPage());

        // Restore response controller
        rcUtil.setResponsePathParts(currentRespPathParts);

        writer.write("</div>");
        writer.write("</div>");
        writer.write("</div>");
        // End body
    }

}
