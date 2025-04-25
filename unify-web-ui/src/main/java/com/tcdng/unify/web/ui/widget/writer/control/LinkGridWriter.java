/*
 * Copyright 2018-2025 The Code Department.
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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.LinkGrid;
import com.tcdng.unify.web.ui.widget.data.LinkCategoryInfo;
import com.tcdng.unify.web.ui.widget.data.LinkGridInfo;
import com.tcdng.unify.web.ui.widget.data.LinkInfo;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Link grid writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(LinkGrid.class)
@Component("linkgrid-writer")
public class LinkGridWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		LinkGrid linkGrid = (LinkGrid) widget;
		writer.write("<div");
		writeTagAttributes(writer, linkGrid);
		writer.write(">");
		LinkGridInfo linkGridInfo = linkGrid.getLinkGridInfo();
		if (linkGridInfo != null) {
			for (LinkCategoryInfo linkCategoryInfo : linkGridInfo.getLinkCategoryList()) {
				writer.write("<div>");
				String caption = linkCategoryInfo.getCaption();
				if (caption != null) {
					writer.write("<div><span class=\"lgcatcap\">");
					writer.writeWithHtmlEscape(caption);
					writer.write("</span></div>");
				}

				List<LinkInfo> linkInfoList = linkCategoryInfo.getLinkInfoList();
				if (DataUtils.isNotBlank(linkInfoList)) {
					writer.write("<div class=\"lgcatsec\"><table>");
					String catName = linkGrid.getPrefixedId(linkCategoryInfo.getName());
					int columns = linkGrid.getUplAttribute(int.class, "columns");
					if (columns <= 0) {
						columns = 1;
					}

					final int len = linkInfoList.size();
					final int rows = len / columns + (len % columns > 0 ? 1 : 0);
					int layoutIndex = 0;
					for (int r = 0; r < rows; r++) {
						writer.write("<tr>");
						for (int c = 0, i = r; c < columns; c++, i += rows) {
							writer.write("<td class=\"col\">");
							if (i < len) {
								LinkInfo linkInfo = linkInfoList.get(i);
								linkInfo.setLayoutIndex(layoutIndex);
								writer.write("<a class=\"lglink\"");
								writeTagId(writer, catName + layoutIndex);
								writer.write(">");
								writer.writeWithHtmlEscape(linkInfo.getCaption());
								writer.write("</a>");
								layoutIndex++;
							}
							writer.write("</td>");
						}
						writer.write("</tr>");
					}
					writer.write("</table></div>");
				}
				writer.write("</div>");
			}
		}
		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		LinkGrid linkGrid = (LinkGrid) widget;

		// Append link grid rigging
		writer.beginFunction("ux.rigLinkGrid");
		JsonWriter jw = new JsonWriter();
		jw.beginArray();
		LinkGridInfo linkGridInfo = linkGrid.getLinkGridInfo();
		if (linkGridInfo != null) {
			for (LinkCategoryInfo linkCategoryInfo : linkGridInfo.getLinkCategoryList()) {
				jw.beginObject();
				jw.write("pURL", getContextURL(linkCategoryInfo.getPath()));
				jw.beginArray("links");
				List<LinkInfo> linkInfoList = linkCategoryInfo.getLinkInfoList();
				if (DataUtils.isNotBlank(linkInfoList)) {
					String catName = linkGrid.getPrefixedId(linkCategoryInfo.getName());
					int len = linkInfoList.size();
					for (int i = 0; i < len; i++) {
						LinkInfo linkInfo = linkInfoList.get(i);
						jw.beginObject();
						jw.write("pId", catName + linkInfo.getLayoutIndex());
						jw.write("pCode", linkInfo.getCode());
						jw.endObject();
					}
				}
				jw.endArray();
				jw.endObject();

			}
		}
		jw.endArray();
		writer.writeParam("categories", jw);

		writer.endFunction();
	}
}
