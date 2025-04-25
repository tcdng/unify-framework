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

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.util.WebRegexUtils;
import com.tcdng.unify.web.ui.widget.ListControlInfo;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.widget.control.PeriodField;
import com.tcdng.unify.web.ui.widget.control.TextField;

/**
 * Period field writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(PeriodField.class)
@Component("periodfield-writer")
public class PeriodFieldWriter extends AbstractPopupTextFieldWriter {

    @Override
	protected void writePopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
			throws UnifyException {
		PeriodField periodField = (PeriodField) popupTextField;

		writer.write("<div id=\"").write(periodField.getFramePanelId())
				.write("\" class=\"pfborder\" style=\"overflow-y:auto;overflow-x:hidden;\" tabindex=\"-1\">");
		writer.write("<div id=\"").write(periodField.getListPanelId()).write("\" class=\"pflist\">");
		List<? extends Listable> listableList = periodField.getListables();
		if (periodField.isLoadingFailure()) {
			writer.write("<a>");
			writer.writeWithHtmlEscape(resolveSessionMessage("$m{list.loadingfailure}"));
			writer.write("</a>");
		} else {
			final int length = listableList.size();

			for (int i = 0; i < length; i++) {
				Listable listable = listableList.get(i);
				writer.write("<a");
				writeTagId(writer, periodField.getNamingIndexedId(i));
				writer.write(">");
				writer.writeWithHtmlEscape(listable.getListDescription());
				writer.write("</a>");
			}
		}

		writer.write("</div>");
		writer.write("</div>");
	}

    @Override
    protected void doWritePopupTextFieldBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField, boolean popupEnabled)
            throws UnifyException {
        PeriodField periodField = (PeriodField) popupTextField;
        writer.beginFunction("ux.rigPeriodField");
        writer.writeParam("pId", periodField.getId());
        writer.writeParam("pFrmId", periodField.getFramePanelId());
        writer.writeParam("pLstId", periodField.getListPanelId());
        writer.writeParam("pBtnId", periodField.getPopupButtonId());
        writer.writeParam("pFacId", periodField.getFacadeId());
        ListControlInfo listControlInfo = periodField.getListControlInfo(null);
        writer.writeParam("pICnt", listControlInfo.size());
        writer.writeParam("pSelectIds", listControlInfo.getSelectIds());
        writer.writeParam("pKeys", listControlInfo.getKeys());
        writer.writeParam("pLabels", listControlInfo.getLabels());
        
        writer.writeParam("pNormCls", "norm");
        writer.writeParam("pSelCls", getUserColorStyleClass("sel"));
        writer.writeParam("pEnabled", popupEnabled);
        JsonWriter jw = new JsonWriter();
        jw.beginObject();
        FrequencyUnit unit = periodField.getFrequencyUnit();
        jw.write("unit", unit != null ? unit.code() : FrequencyUnit.SECOND.code());
        jw.write("magnitude", periodField.getMagnitude());
        jw.endObject();
        writer.writeParam("pVal", jw);
        writer.endFunction();
    }

    @Override
    protected void writeTrailingAddOn(ResponseWriter writer, Widget widget) throws UnifyException {
        PeriodField periodField = (PeriodField) widget;
        writer.write("<button");
        writeTagId(writer, periodField.getPopupButtonId());
        writeTagStyleClass(writer, "tplbutton");
        if (!isPopupEnabled(periodField)) {
            writer.write(" disabled");
        }

        writer.write(">");
        writer.write("</button>");
    }

    @Override
    protected String getOnShowAction() throws UnifyException {
        return "ux.pfOnShow";
    }

    @Override
    protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
        PeriodField periodField = (PeriodField) popupTextField;
        JsonWriter jw = new JsonWriter();
        jw.beginObject();
        jw.write("pFrmId", periodField.getFramePanelId());
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
        PeriodField periodField = (PeriodField) textField;
        return WebRegexUtils.getNumberFormatRegex(((NumberFormatter<?>) periodField.getFormatter()).getNumberSymbols(),
                periodField.getUplAttribute(int.class, "precision"), 0,
                false, false, false);
    }
}
