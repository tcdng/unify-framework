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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.widget.ListControlInfo;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.widget.control.SearchField;

/**
 * Search field writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(SearchField.class)
@Component("searchfield-writer")
public class SearchFieldWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void writePopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        SearchField searchField = (SearchField) popupTextField;
        searchField.setKeyOnly(true);

        writer.write("<div");
        writeTagId(writer, searchField.getSearchPanelId());
        writeTagStyleClass(writer, "sfborder");
        writer.write(">");

        // Filter row
        writer.write("<div");
        writeTagStyleClass(writer, "sffilterrow");
        writer.write(">");
        writer.write("<span>").write(searchField.getFilterLabel()).write("</span>");
        writer.write("<input type=\"text\"");
        writeTagId(writer, searchField.getFilterId());
        writeTagValue(writer, searchField.getFilter());
        writer.write("/>");
        writer.write("</div>");

        // AggregateItem row
        writer.write("<div");
        writeTagId(writer, searchField.getResultPanelId());
        writeTagStyleClass(writer, "sfresultrow");
        writer.write(">");
        writeResultList(writer, searchField);
        writer.write("</div>");

        // Action row
        writer.write("<div");
        writeTagStyleClass(writer, "sfactionrow");
        writer.write(">");
        writeButton(writer, searchField.getClearButtonId(), "sfactbutton", "float:left;",
                getSessionMessage("button.clear"));
        writeButton(writer, searchField.getCancelButtonId(), "sfactbutton", "float:right;",
                getSessionMessage("button.cancel"));
        writer.write("</div>");

        writer.write("</div>");
    }

    @Override
    protected void doWritePopupTextFieldBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField,
            boolean popupEnabled) throws UnifyException {
        SearchField searchField = (SearchField) popupTextField;
        searchField.setKeyOnly(true);
        writeRigging(writer, searchField, "ux.rigSearchField", popupEnabled);
    }

    @Override
    protected void doWriteSectionStructureAndContent(ResponseWriter writer, Widget widget, String sectionPageName)
            throws UnifyException {
        SearchField searchField = (SearchField) widget;
        if (searchField.getResultPanelId().equals(sectionPageName)) {
            writeResultList(writer, searchField);
        }
    }

    @Override
    protected void doWriteSectionBehavior(ResponseWriter writer, Widget widget, String sectionPageName)
            throws UnifyException {
        SearchField searchField = (SearchField) widget;
        if (searchField.getResultPanelId().equals(sectionPageName)) {
            writeRigging(writer, searchField, "ux.sfWireResult", isPopupEnabled(searchField));
        }
    }

    @Override
    protected String getOnShowAction() throws UnifyException {
        return "ux.sfOnShow";
    }

    @Override
    protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
        SearchField searchField = (SearchField) popupTextField;
        JsonWriter jw = new JsonWriter();
        jw.beginObject();
        jw.write("pFilId", searchField.getFilterId());
        jw.endObject();
        return jw.toString();
    }

    @Override
    protected String getOnHideAction() throws UnifyException {
        return null;
    }

    @Override
    protected String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException {
        return null;
    }

    private void writeRigging(ResponseWriter writer, SearchField searchField, String functionName, boolean popupEnabled)
            throws UnifyException {
        ListControlInfo listControlInfo = searchField.getListControlInfo(null);

        // Append rigging
        writer.beginFunction(functionName);
        writer.writeParam("pId", searchField.getId());
        writer.writeCommandURLParam("pCmdURL");
        writer.writeParam("pFacId", searchField.getFacadeId());
        writer.writeParam("pFilId", searchField.getFilterId());
        writer.writeParam("pRltId", searchField.getResultPanelId());
        writer.writeParam("pClrId", searchField.getClearButtonId());
        writer.writeParam("pCanId", searchField.getCancelButtonId());
        writer.writeParam("pClearable", searchField.isClearable());
        writer.writeParam("pICnt", listControlInfo.size());
        writer.writeParam("pSelectIds", listControlInfo.getSelectIds());
        writer.writeParam("pKeys", listControlInfo.getKeys());
        writer.writeParam("pLabels", listControlInfo.getLabels());
        writer.writeParam("pVal", searchField.getValue(String.class));
        writer.writeParam("pEnabled", popupEnabled);
        writer.endFunction();
    }

    private void writeResultList(ResponseWriter writer, SearchField searchField) throws UnifyException {
        ListControlInfo listControlInfo = searchField.getListControlInfo(null);
        int length = listControlInfo.size();
        writer.write("<div");
        writeTagStyleClass(writer, "sflist");
        writer.write(">");
        for (int i = 0; i < length; i++) {
            writer.write("<a");
            writeTagId(writer, searchField.getNamingIndexedId(i));
            if (i % 2 == 0) {
                writeTagStyleClass(writer, "odd");
            } else {
                writeTagStyleClass(writer, "even");
            }
            writer.write(">");
//            writer.writeWithHtmlEscape(listableList.get(i).getListDescription());
            writer.write("</a>");
        }
        writer.write("</div>");
    }

}
