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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.web.ui.ListControlInfo;
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
                writer.write("<a");
                writeTagId(writer, optionsTextArea.getNamingIndexedId(i));
                writer.write(">");
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
        writer.beginFunction("ux.rigOptionsTextArea");
        writer.writeParam("pId", optionsTextArea.getId());
        writer.writeParam("pScrEnd", optionsTextArea.isScrollToEnd());

        if (optionsTextArea.isContainerEditable() && !optionsTextArea.isContainerDisabled()) {
            ListControlInfo listControlInfo = optionsTextArea.getListControlInfo(null);
            writer.writeParam("pPopupId", optionsTextArea.getPopupId());
            writer.writeParam("pFrmId", optionsTextArea.getFramePanelId());
            writer.writeParam("pLstId", optionsTextArea.getListPanelId());
            writer.writeParam("pICnt", listControlInfo.size());
            writer.writeParam("pSelectIds", listControlInfo.getSelectIds());
            writer.writeParam("pKeys", listControlInfo.getKeys());
            writer.writeParam("pLabels", listControlInfo.getLabels());
            writer.writeParam("pNormCls", "norm");
            writer.writeParam("pSelCls", getUserColorStyleClass("sel"));
            writer.writeParam("pEnabled", true);
        }

        writer.endFunction();
    }
}
