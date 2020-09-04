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
import com.tcdng.unify.core.data.Money;
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.web.ui.ListControlJsonData;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.control.MoneyField;
import com.tcdng.unify.web.ui.control.TextField;

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
    protected void appendPopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        MoneyField moneyField = (MoneyField) popupTextField;

        writer.write("<div id=\"").write(moneyField.getFramePanelId())
                .write("\" class=\"mfborder\" style=\"overflow-y:auto;overflow-x:hidden;\" tabindex=\"0\">");
        writer.write("<div id=\"").write(moneyField.getListPanelId()).write("\" class=\"mflist\">");
        List<? extends Listable> listableList = moneyField.getListables();
        int length = listableList.size();

        String currencyCode = getCurrencyCode(moneyField);
        String selStyleClass = getUserColorStyleClass("sel");
        for (int i = 0; i < length; i++) {
            Listable listable = listableList.get(i);
            String key = listable.getListKey();
            writer.write("<a");
            writeTagId(writer, moneyField.getNamingIndexedId(i));
            if (key.equals(currencyCode)) {
                writeTagStyleClass(writer, selStyleClass);
            } else {
                writeTagStyleClass(writer, "norm");
            }
            writer.write(">");
            writer.writeWithHtmlEscape(listable.getListDescription());
            writer.write("</a>");
        }
        writer.write("</div>");
        writer.write("</div>");
    }

    @Override
    protected void appendPopupBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField)
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
        writer.endFunction();
    }

    @Override
    protected boolean isAppendPopup(AbstractPopupTextField popupTextField) throws UnifyException {
        if (super.isAppendPopup(popupTextField)) {
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
        if (!isAppendPopup(moneyField)) {
            writer.write(" disabled");
        }

        writer.write(">");
        writer.write(getCurrencyCode(moneyField));
        writer.write("</button>");
    }

    @Override
    protected String getOnShowAction() throws UnifyException {
        return "ux.moneyFieldOnShow";
    }

    @Override
    protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
        MoneyField moneyField = (MoneyField) popupTextField;
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"pFrmId\":\"").append(moneyField.getFramePanelId()).append('"');
        sb.append('}');
        return sb.toString();
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
    protected void writeFormatRegex(ResponseWriter writer, TextField textField) throws UnifyException {
        MoneyField moneyField = (MoneyField) textField;
        int scale = 0;
        if (textField.isUplAttribute("scale")) {
            scale = moneyField.getUplAttribute(int.class, "scale");
        }

        writer.writeNumberFormatRegex(((NumberFormatter<?>) moneyField.getFormatter()).getNumberSymbols(),
                moneyField.getUplAttribute(int.class, "precision"), scale,
                moneyField.getUplAttribute(boolean.class, "acceptNegative"),
                moneyField.getUplAttribute(boolean.class, "useGrouping"));
    }

    private String getCurrencyCode(MoneyField moneyField) throws UnifyException {
        String currencyCode = moneyField.getCurrencyCode();
        Money money = moneyField.getValue(Money.class);
        if (money != null && money.getCurrencyCode() != null) {
            currencyCode = money.getCurrencyCode();
        }

        return currencyCode;
    }
}
