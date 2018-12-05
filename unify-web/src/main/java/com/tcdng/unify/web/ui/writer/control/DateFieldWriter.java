/*
 * Copyright 2014 The Code Department
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

import java.util.Calendar;
import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.control.DateField;

/**
 * Date field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(DateField.class)
@Component("datefield-writer")
public class DateFieldWriter extends AbstractPopupTextFieldWriter {

	@Override
	protected void appendPopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
			throws UnifyException {
		DateField dateField = (DateField) popupTextField;
		Date date = dateField.getValue(Date.class);
		if (date == null) {
			date = new Date();
		}

		writer.write("<div");
		this.writeTagId(writer, dateField.getPrefixedId("cal_"));
		this.writeTagStyleClass(writer, "dborder");
		writer.write(">");

		writer.write("<div class=\"cnav\">");
		this.writeButton(writer, dateField.getPrefixedId("decy_"), "cscroll", null, "<<");
		this.writeButton(writer, dateField.getPrefixedId("decm_"), "cscroll", null, "<");
		writer.write("<span");
		this.writeTagId(writer, dateField.getPrefixedId("disp_"));
		this.writeTagStyleClass(writer, "cdisplay");
		writer.write("></span>");
		this.writeButton(writer, dateField.getPrefixedId("incm_"), "cscroll", null, ">");
		this.writeButton(writer, dateField.getPrefixedId("incy_"), "cscroll", null, ">>");
		writer.write("</div>");
		writer.write("<div");
		this.writeTagId(writer, dateField.getPrefixedId("cont_"));
		this.writeTagStyleClass(writer, "ccontent");
		writer.write(">");
		writer.write("</div>");

		writer.write("<div class=\"ccontrol\">");
		this.writeButton(writer, dateField.getPrefixedId("btnt_"), "cactbutton", "float:left;",
				this.getSessionMessage("button.today"));
		this.writeButton(writer, dateField.getPrefixedId("btnc_"), "cactbutton", "float:right;",
				this.getSessionMessage("button.clear"));
		writer.write("</div>");

		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(date);
		this.writeHidden(writer, dateField.getPrefixedId("day_"), dateCal.get(Calendar.DAY_OF_MONTH));
		this.writeHidden(writer, dateField.getPrefixedId("mon_"), dateCal.get(Calendar.MONTH) + 1);
		this.writeHidden(writer, dateField.getPrefixedId("year_"), dateCal.get(Calendar.YEAR));
		writer.write("</div>");
	}

	@Override
	protected void appendPopupBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField)
			throws UnifyException {
		DateField dateField = (DateField) popupTextField;
		writer.write("ux.rigDateField({");
		writer.write("\"pId\":\"").write(dateField.getId()).write('"');
		writer.write(",\"pDayClass\":\"cday\"");
		writer.write(",\"pCurrClass\":\"ccurrent\"");
		writer.write(",\"pTodayClass\":\"ctoday\"");
		writer.write(",\"pClearable\":").write(dateField.isClearable());
		writer.write(",\"pPadLeft\":true");
		writer.write(",\"pShortDayNm\":");
		writer.writeJsonStringArray((Object[]) dateField.getShortDayList());
		writer.write(",\"pLongMonthNm\":");
		writer.writeJsonStringArray((Object[]) dateField.getLongMonthList());
		writer.write(",\"pPattern\":");
		writer.writeJsonPatternObject(dateField.getPattern());
		writer.write("});");
	}

	@Override
	protected String getOnShowAction() throws UnifyException {
		return null;
	}

	@Override
	protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
		return null;
	}

	@Override
	protected String getOnHideAction() throws UnifyException {
		return null;
	}

	@Override
	protected String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException {
		return null;
	}

}
