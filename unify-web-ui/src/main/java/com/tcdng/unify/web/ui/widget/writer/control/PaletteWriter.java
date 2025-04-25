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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.Palette;
import com.tcdng.unify.web.ui.widget.data.InkInfo;
import com.tcdng.unify.web.ui.widget.data.PaletteInfo;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Palette writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(Palette.class)
@Component("palette-writer")
public class PaletteWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		Palette palette = (Palette) widget;
		writer.write("<div");
		writeTagStyleClass(writer, palette);
		writeTagStyle(writer, palette);
		writer.write(">");

		final String id = palette.getId();
		final String currSel = palette.getStringValue();
		if (palette.isShowSelected()) {
			writer.write("<div><span class=\"txtsel\" id=\"");
			writer.write(id).write("_sel\">");
			if (currSel != null) {
				writer.writeWithHtmlEscape(currSel);
			}

			writer.write("</span></div>");
		}

		writer.write("<div style=\"display:table;\">");
		final PaletteInfo paletteInfo = palette.getPaletteInfo();
		if (paletteInfo != null) {
			final int columns = palette.getColumns();
			final int size = paletteInfo.size();
			int j = 0;
			for (int i = 0; i < size;) {
				writer.write("<div style=\"display:table-row;\">");
				for (j = 0; j < columns && i < size; j++, i++) {
					writer.write("<div style=\"display:table-cell;\">");
					InkInfo inkInfo = paletteInfo.getInkAt(i);
					String spClass = inkInfo.getName().equals(currSel) ? "palsel" : "pal";
					spClass = i == 0 ? spClass + " dflt" : spClass;
					
					writer.write("<span id=\"");
					writer.write(id).write(i);
					writer.write("\" class=\"");
					writer.write(spClass).write("\"");
					if (i > 0) {
						writer.write(" style=\"background-color:");
						writer.write(inkInfo.getColor()).write(";\"");
					}
					writer.write("></span>");
					writer.write("</div>");
				}

				while (j < columns) {
					writer.write("<div style=\"display:table-cell;\">");
					writer.write("</div>");
					j++;
				}

				writer.write("</div>");
			}
		}

		writer.write("</div>");

		writeHidden(writer, id, currSel);
		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		final Palette palette = (Palette) widget;
		final PaletteInfo paletteInfo = palette.getPaletteInfo();
		writer.beginFunction("ux.rigPalette");
		writer.writeParam("pId", palette.getId());
		writer.writeParam("pContId", palette.getContainerId());
		if (paletteInfo != null) {
			writer.writeParam("pColors", DataUtils.toArray(String.class, paletteInfo.getInkNames()));
		}

		writer.endFunction();
	}
}
