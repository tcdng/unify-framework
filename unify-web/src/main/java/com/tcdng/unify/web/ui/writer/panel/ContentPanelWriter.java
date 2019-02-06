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
package com.tcdng.unify.web.ui.writer.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.ContentTypeConstants;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ControllerResponseInfo;
import com.tcdng.unify.web.PageAttributeConstants;
import com.tcdng.unify.web.RequestContextUtil;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.panel.ContentPanel;
import com.tcdng.unify.web.ui.panel.ContentPanel.ContentInfo;
import com.tcdng.unify.web.ui.writer.AbstractPanelWriter;

/**
 * Content panel writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(ContentPanel.class)
@Component("contentpanel-writer")
public class ContentPanelWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        ContentPanel contentPanel = (ContentPanel) widget;

        // Write content variables
        writer.write("ux.rigContentPanel({");
        writer.write("\"pHintPanelId\":\"").write(contentPanel.getHintPanelId()).write("\"");
        if (contentPanel.getPageCount() > 0) {
            String closeImgId = contentPanel.getTabItemImgId(contentPanel.getPageIndex());
            writer.write(",\"pCloseImgId\":\"").write(closeImgId).write("\"");
        }

        if (contentPanel.getPageCount() == 0) {
            writer.write(",\"pImmURL\":\"").writeContextURL(contentPanel.getPath()).write("\"");
        } else {
            writer.write(",\"pCurIdx\":").write(contentPanel.getPageIndex());
            ContentInfo currentContentInfo = contentPanel.getCurrentContentInfo();
            if (currentContentInfo.isRemoteSave()) {
                writer.write(",\"pSavePath\":\"").write(currentContentInfo.getSavePath()).write("\"");
            } else {
                writer.write(",\"pSavePath\":\"").writeContextURL(currentContentInfo.getSavePath()).write("\"");
            }

            writer.write(",\"pContent\":[");
            for (int i = 0; i < contentPanel.getPageCount(); i++) {
                ContentInfo contentInfo = contentPanel.getContentInfo(i);
                if (i > 0) {
                    writer.write(",");
                }

                writer.write("{");
                writer.write("\"tabId\":\"").write(contentPanel.getTabItemId(i)).write("\"");
                writer.write(",\"tabImgId\":\"").write(contentPanel.getTabItemImgId(i)).write("\"");
                writer.write(",\"openPath\":\"").writeContextURL(contentInfo.getOpenPath()).write("\"");
                writer.write(",\"closePath\":\"").writeContextURL(contentInfo.getClosePath()).write("\"");
                writer.write("}");
            }

            writer.write("]");
        }

        writer.write("});");

        if (contentPanel.isSidebar()) {
            writer.writeBehaviour(contentPanel.getSidebar());
        }

        if (contentPanel.getPageCount() > 0) {
            // Set response page controller
            RequestContextUtil rcu = getRequestContextUtil();
            ControllerResponseInfo saveRespInfo = rcu.getResponsePageControllerInfo();
            ContentInfo currentContentInfo = contentPanel.getCurrentContentInfo();
            rcu.setResponsePageControllerInfo(currentContentInfo.getRespInfo());

            writer.writeBehaviour(currentContentInfo.getPage());

            // Restore response controller
            rcu.setResponsePageControllerInfo(saveRespInfo);
        }
    }

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        ContentPanel contentPanel = (ContentPanel) container;
        writer.write("<div id=\"").write(contentPanel.getHintPanelId()).write("\" class=\"cphint\"></div>");
        writer.write("<div id=\"").write(contentPanel.getBusyIndicatorId()).write("\" class=\"cpbusy\">");
        writer.write("<img class=\"cpimage\" src=\"");
        writer.writeContextResourceURL("/resource/file", ContentTypeConstants.IMAGE, "$t{images/busy.gif}");
        writer.write("\"></div>");

        writer.write("<div style=\"display:table;width:100%;height:100%;\">");
        boolean isSidebar = contentPanel.isSidebar();
        // Frame
        if (isSidebar) {
            writer.write("<div style=\"display:table-row;width:100%;\">");
            writer.write("<div style=\"display:table-cell;width:100%;height:100%;vertical-align:top;\">");
            writer.write("<div style=\"display:table;width:100%;height:100%;\">");
        }

        if (contentPanel.getPageCount() > 0) {
            writeContentPanel(writer, contentPanel);
        }

        if (isSidebar) {
            writer.write("</div>");
            writer.write("</div>");
            writer.write("<div style=\"display:table-cell;height:100%;vertical-align:top;\">");
            writer.writeStructureAndContent(contentPanel.getSidebar());
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("</div>");
    }

    private void writeContentPanel(ResponseWriter writer, ContentPanel contentPanel) throws UnifyException {
        RequestContextUtil rcUtil = getRequestContextUtil();
        ContentInfo currentContentInfo = contentPanel.getCurrentContentInfo();

        // Tabs
        if (contentPanel.isTabbed()) {
            writer.write("<div style=\"display:table-row;width:100%;\">");
            writer.write("<div style=\"display:table-cell;\">");
            writer.write("<div class=\"cptabbar\">");

            writer.write("<ul class=\"cptab\">");
            for (int i = 0; i < contentPanel.getPageCount(); i++) {
                ContentInfo contentInfo = contentPanel.getContentInfo(i);
                writer.write("<li");
                if (i == contentPanel.getPageIndex()) {
                    writer.write(" class=\"cpactive\"");
                } else {
                    writer.write(" class=\"cpinactive\"");
                }

                Page page = contentInfo.getPage();
                String contentTitle = (String) page.getAttribute(PageAttributeConstants.PAGE_TITLE);
                if (StringUtils.isBlank(contentTitle)) {
                    contentTitle = page.getCaption();
                }

                writer.write("><div><a ");
                if (page.getUplAttribute(boolean.class, "remote")) {
                    writer.write("class=\"cpremote\"");
                }

                writer.write(" id=\"").write(contentPanel.getTabItemId(i)).write("\">");
                writer.writeWithHtmlEscape(contentTitle);
                writer.write("</a>");

                if (i > 0) {
                    writer.write("<img id=\"").write(contentPanel.getTabItemImgId(i)).write("\" src=\"");
                    writer.writeFileImageContextURL("$t{images/cross_gray.png}");
                    writer.write("\"/>");
                }
                writer.write("</div></li>");
            }
            writer.write("</ul>");

            writer.write("</div>");
            writer.write("</div>");
            writer.write("</div>");
        }
        // End tabs

        // Title bar
        if (contentPanel.isTitleBar()) {
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

        ControllerResponseInfo saveRespInfo = rcUtil.getResponsePageControllerInfo();
        rcUtil.setResponsePageControllerInfo(currentContentInfo.getRespInfo());

        writer.writeStructureAndContent(currentContentInfo.getPage());

        // Restore response controller
        rcUtil.setResponsePageControllerInfo(saveRespInfo);

        writer.write("</div>");
        writer.write("</div>");
        writer.write("</div>");
        // End body
    }

}
