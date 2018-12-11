/*
 * Copyright 2018 The Code Department
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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.web.ui.ListControlJsonData;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.OptionsTextArea;

/**
 * Options text area writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(OptionsTextArea.class)
@Component("optionstextarea-writer")
public class OptionsTextAreaWriter extends TextAreaWriter {

	@Override
	protected final void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		OptionsTextArea optionsTextArea = (OptionsTextArea) widget;
		writer.write("<div ");
		writeTagStyleClass(writer, optionsTextArea);
		writer.write(">");
		writeTextArea(writer, optionsTextArea, "otatext");

		if (optionsTextArea.isContainerEditable() && !optionsTextArea.isContainerDisabled()) {
			writer.write("<div");
			writeTagId(writer, optionsTextArea.getPopupId());
			writeTagStyleClass(writer, "ui-text-popup-win");
			writer.write(">");

			writer.write("<div id=\"").write(optionsTextArea.getFramePanelId())
					.write("\" class=\"otaborder\" style=\"overflow-y:auto;overflow-x:hidden;\" tabindex=\"0\">");
			writer.write("<div id=\"").write(optionsTextArea.getListPanelId()).write("\" class=\"otalist\">");
			List<? extends Listable> listableList = optionsTextArea.getListables();
			int length = listableList.size();

			for (int i = 0; i < length; i++) {
				Listable listable = listableList.get(i);
				writer.write("<a");
				writeTagId(writer, optionsTextArea.getNamingIndexedId(i));
				if (i == 0) {
					writeTagStyleClass(writer, "sel");
				} else {
					writeTagStyleClass(writer, "norm");
				}
				writer.write(">");
				writer.writeWithHtmlEscape(listable.getListDescription());
				writer.write("</a>");
			}
			writer.write("</div>");
			writer.write("</div>");
			writer.write("</div>");
		}

		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		super.doWriteBehavior(writer, widget);
		OptionsTextArea optionsTextArea = (OptionsTextArea) widget;
		writer.write("ux.rigOptionsTextArea({");
		writer.write("\"pId\":\"").write(optionsTextArea.getId()).write('"');
		writer.write(",\"pScrEnd\":").write(optionsTextArea.isScrollToEnd());

		if (optionsTextArea.isContainerEditable() && !optionsTextArea.isContainerDisabled()) {
			ListControlJsonData listControlJsonData = optionsTextArea.getListControlJsonData(true, true, false);
			writer.write(",\"pPopupId\":\"").write(optionsTextArea.getPopupId()).write('"');
			writer.write(",\"pFrmId\":\"").write(optionsTextArea.getFramePanelId()).write('"');
			writer.write(",\"pLstId\":\"").write(optionsTextArea.getListPanelId()).write('"');
			writer.write(",\"pICnt\":").write(listControlJsonData.getSize());
			writer.write(",\"pLabelIds\":").write(listControlJsonData.getJsonSelectIds());
			writer.write(",\"pKeys\":").write(listControlJsonData.getJsonKeys());
			writer.write(",\"pNormCls\":\"norm\"");
			writer.write(",\"pSelCls\":\"sel\"");
		}

		writer.write("});");
	}
}
