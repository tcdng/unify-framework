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
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.ListControlJsonData;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.control.MoneyField;
import com.tcdng.unify.web.ui.control.TextField;
import com.tcdng.unify.web.util.WebRegexUtils;

/**
 * Money field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(MoneyField.class)
@Component("moneyfield-writer")
public class MoneyFieldWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void writePopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        MoneyField moneyField = (MoneyField) popupTextField;

        writer.write("<div id=\"").write(moneyField.getFramePanelId())
                .write("\" class=\"mfborder\" style=\"overflow-y:auto;overflow-x:hidden;\" tabindex=\"0\">");
        writer.write("<div id=\"").write(moneyField.getListPanelId()).write("\" class=\"mflist\">");
        List<? extends Listable> listableList = moneyField.getListables();
        int length = listableList.size();

        for (int i = 0; i < length; i++) {
            Listable listable = listableList.get(i);
            writer.write("<a");
            writeTagId(writer, moneyField.getNamingIndexedId(i));
            writer.write(">");
            writer.writeWithHtmlEscape(listable.getListDescription());
            writer.write("</a>");
        }
        writer.write("</div>");
        writer.write("</div>");
    }

    @Override
    protected void doWritePopupTextFieldBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField, boolean popupEnabled)
            throws UnifyException {
        MoneyField moneyField = (MoneyField) popupTextField;
        ListControlJsonData listControlJsonData = moneyField.getListControlJsonData(true, true, false);

        // Append rigging
        writer.beginFunction("ux.rigMoneyField");
        writer.writeParam("pId", moneyField.getId());
        writer.writeParam("pFrmId", moneyField.getFramePanelId());
        writer.writeParam("pLstId", moneyField.getListPanelId());
        writer.writeParam("pBtnId", moneyField.getPopupButtonId());
        writer.writeParam("pFacId", moneyField.getFacadeId());
        writer.writeParam("pKeyIdx", listControlJsonData.getValueIndex());
        writer.writeParam("pICnt", listControlJsonData.getSize());
        writer.writeResolvedParam("pLabelIds", listControlJsonData.getJsonSelectIds());
        writer.writeResolvedParam("pKeys", listControlJsonData.getJsonKeys());
        writer.writeParam("pNormCls", "norm");
        writer.writeParam("pSelCls", getUserColorStyleClass("sel"));
        writer.writeParam("pEnabled", popupEnabled);
        JsonWriter jw = new JsonWriter();
        jw.beginObject();
        jw.write("currency", moneyField.getCurrencyString());
        jw.write("amount", moneyField.getAmountString());
        jw.endObject();
        writer.writeParam("pVal", jw);
        writer.endFunction();
    }

    @Override
    protected boolean isPopupEnabled(AbstractPopupTextField popupTextField) throws UnifyException {
        if (super.isPopupEnabled(popupTextField)) {
            MoneyField moneyField = (MoneyField) popupTextField;
            return moneyField.isMultiCurrency();
        }

        return false;
    }

    @Override
    protected void writeTrailingAddOn(ResponseWriter writer, Widget widget) throws UnifyException {
        MoneyField moneyField = (MoneyField) widget;
        writer.write("<button");
        writeTagId(writer, moneyField.getPopupButtonId());
        writeTagStyleClass(writer, "tplbutton");
        if (!isPopupEnabled(moneyField)) {
            writer.write(" disabled");
        }

        writer.write(">");
        writer.write("</button>");
    }

    @Override
    protected String getOnShowAction() throws UnifyException {
        return "ux.mfOnShow";
    }

    @Override
    protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
        MoneyField moneyField = (MoneyField) popupTextField;
        JsonWriter jw = new JsonWriter();
        jw.beginObject();
        jw.write("pFrmId", moneyField.getFramePanelId());
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

    @Override
    protected String getFormatRegex(TextField textField) throws UnifyException {
        MoneyField moneyField = (MoneyField) textField;
        int scale = 0;
        if (textField.isUplAttribute("scale")) {
            scale = moneyField.getUplAttribute(int.class, "scale");
        }

        return WebRegexUtils.getNumberFormatRegex(((NumberFormatter<?>) moneyField.getFormatter()).getNumberSymbols(),
                moneyField.getUplAttribute(int.class, "precision"), scale,
                moneyField.getUplAttribute(boolean.class, "acceptNegative"),
                moneyField.getUplAttribute(boolean.class, "useGrouping"));
    }
}
