/*
 * Copyright 2018-2023 The Code Department.
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
 * @author The Code Department
 * @since 1.0
 */
@Writes(ContentPanelImpl.class)
@Component("contentpanel-writer")
public class ContentPanelWriter extends AbstractPanelWriter {

	private static final String CPREMOTE_CATEGORYBASE = "cpcat";

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		ContentPanelImpl contentPanel = (ContentPanelImpl) widget;

		// Write content variables
		writer.beginFunction("ux.rigContentPanel");
		writer.writeParam("pId", contentPanel.getId());
		writer.writeParam("pHintPanelId", contentPanel.getHintPanelId());
		writer.writeParam("pBdyPanelId", contentPanel.getBodyPanelId());
		if (contentPanel.getPageCount() > 0) {
			// Close image
			String closeImgId = contentPanel.getTabItemImgId(contentPanel.getPageIndex());
			writer.writeParam("pCloseImgId", closeImgId);
		}

		PageRequestContextUtil rcUtil = getRequestContextUtil();
		final boolean lowLatency = rcUtil.isLowLatencyRequest();
		if (lowLatency) {
			writer.writeParam("pLatency", lowLatency);
			writer.write(",\"pContentURL\":\"");
			writer.writeContextURL(contentPanel.getDocumentPath(), "/content");
			writer.write('"');
		}

		if (contentPanel.getPageCount() == 0) {
			writer.writeParam("pImmURL", getContextURL(contentPanel.getPath()));
		} else {
			writer.writeParam("pCurIdx", contentPanel.getPageIndex());
			ContentInfo currentContentInfo = contentPanel.getCurrentContentInfo();
			if (currentContentInfo.isRemoteSave()) {
				writer.writeParam("pSavePath", currentContentInfo.getSavePath());
				writer.writeParam("pSaveIsRem", true);
			} else {
				writer.writeParam("pSavePath", getContextURL(currentContentInfo.getSavePath()));
				writer.writeParam("pSaveIsRem", false);
			}

			JsonWriter jw = new JsonWriter();
			jw.beginArray();
			for (int i = 0; i < contentPanel.getPageCount(); i++) {
				ContentInfo contentInfo = contentPanel.getContentInfo(i);
				jw.beginObject();
				jw.write("tabId", contentPanel.getTabItemId(i));
				jw.write("tabImgId", contentPanel.getTabItemImgId(i));
				jw.write("openPath", getContextURL(contentInfo.getOpenPath()));
				jw.write("closePath", getContextURL(contentInfo.getClosePath()));
				jw.endObject();
			}
			jw.endArray();
			writer.writeParam("pContent", jw);
		}

		final boolean tabbed = contentPanel.isTabbed();
		writer.writeParam("pTabbed", tabbed);
		if (tabbed && contentPanel.getPageCount() > 0) {
			writer.writeParam("pTabPaneId", contentPanel.getTabPaneId());
			writer.writeParam("pMenuId", contentPanel.getMenuId());
		} else {
			rcUtil.setContentScrollReset();
		}

		writer.endFunction();

		if (contentPanel.isSidebar()) {
			writer.writeBehavior(contentPanel.getSidebar());
		}

		if (contentPanel.getPageCount() > 0) {
			if (lowLatency) {
				// TODO
			} else {
				// Set response page controller
				ControllerPathParts currentRespPathParts = rcUtil.getResponsePathParts();
				Page currentContentPage = rcUtil.getContentPage();
				ContentInfo currentContentInfo = contentPanel.getCurrentContentInfo();
				try {
					rcUtil.setResponsePathParts(currentContentInfo.getPathParts());
					rcUtil.setContentPage(currentContentInfo.getPage());
					writer.writeBehavior(currentContentInfo.getPage());
				} finally {
					// Restore response controller
					rcUtil.setContentPage(currentContentPage);
					rcUtil.setResponsePathParts(currentRespPathParts);
				}
			}
		}
	}

	@Override
	protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
		ContentPanelImpl contentPanel = (ContentPanelImpl) container;
		writer.write("<div id=\"").write(contentPanel.getHintPanelId()).write("\" class=\"cphint\"></div>");
		writer.write("<div id=\"").write(contentPanel.getBaseContentId())
				.write("\" style=\"display:table;width:100%;height:100%;\">");
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

	private void writeContentPanel(ResponseWriter writer, ContentPanelImpl contentPanel) throws UnifyException {
		logDebug("Writing structure for content panel [{0}]...", contentPanel.getLongName());
		PageRequestContextUtil rcUtil = getRequestContextUtil();
		ContentInfo currentContentInfo = contentPanel.getCurrentContentInfo();

		// Tabs
		final boolean tabbed = contentPanel.isTabbed();
		if (tabbed) {
			writer.write("<div style=\"display:table-row;width:100%;\">");
			writer.write("<div style=\"display:table-cell;\">");
			writer.write("<div id=\"").write(contentPanel.getTabPaneId()).write("\" class=\"cptabbar\">");

			logDebug("Writing header tabs for content panel [{0}]...", contentPanel.getLongName());
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

				writer.write(" id=\"").write(contentPanel.getTabItemId(i)).write("\">");
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

			// Menu
			String menuId = contentPanel.getMenuId();
			writer.write("<div");
			writeTagId(writer, menuId);
			writeTagStyleClass(writer, "contentpanel-popup");
			writer.write(">");
			writer.write("<ul id=\"").write(contentPanel.getMenuBaseId()).write("\">");
			writer.write("<li><a class=\"mitem\" id=\"").write(contentPanel.getMenuCloseId()).write("\">");
			writer.writeWithHtmlEscape(resolveSessionMessage("$m{contentpanel.close}"));
			writer.write("</a></li>");
			writer.write("<li><a class=\"mitem\" id=\"").write(contentPanel.getMenuCloseOtherId()).write("\">");
			writer.writeWithHtmlEscape(resolveSessionMessage("$m{contentpanel.closeothertabs}"));
			writer.write("</a></li>");
			writer.write("<li class=\"msep\"><a class=\"mitem\" id=\"").write(contentPanel.getMenuCloseAllId())
					.write("\">");
			writer.writeWithHtmlEscape(resolveSessionMessage("$m{contentpanel.closealltabs}"));
			writer.write("</a></li>");
			writer.write("</ul>");
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
		writer.write("<div id=\"").write(contentPanel.getBodyPanelId()).write("\" class=\"cpbody\">");		
		if (rcUtil.isLowLatencyRequest()) {
			// TODO
		} else {
			ControllerPathParts currentRespPathParts = rcUtil.getResponsePathParts();
			Page currentContentPage = rcUtil.getContentPage();
			try {
				rcUtil.setResponsePathParts(currentContentInfo.getPathParts());
				rcUtil.setContentPage(currentContentInfo.getPage());
				writer.writeStructureAndContent(currentContentInfo.getPage());
			} finally {
				rcUtil.setContentPage(currentContentPage);
				rcUtil.setResponsePathParts(currentRespPathParts);
			}
		}

		writer.write("</div>");
		writer.write("</div>");
		writer.write("</div>");
		// End body
	}
}
