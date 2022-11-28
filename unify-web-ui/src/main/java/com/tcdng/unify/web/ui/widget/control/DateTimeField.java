/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.web.ui.widget.control;

import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * Date time field.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-datetime")
@UplAttributes({
    @UplAttribute(name = "clearable", type = boolean.class),
    @UplAttribute(name = "dateButtonImgSrc", type = String.class, defaultVal = "$t{images/calendar.png}"),
    @UplAttribute(name = "dateButtonSymbol", type = String.class, defaultVal = "calendar-alt"),
    @UplAttribute(name = "dateFormatter", type = String.class, defaultVal = "$s{!dateformat style:customshort}"),
    @UplAttribute(name = "dateType", type = String.class, defaultVal = "standard"),
    @UplAttribute(name = "timeButtonImgSrc", type = String.class, defaultVal = "$t{images/clock.png}"),
    @UplAttribute(name = "timeButtonSymbol", type = String.class, defaultVal = "clock"),
    @UplAttribute(name = "timeFormatter", type = String.class, defaultVal = "$s{!timeformat style:short}")})
public class DateTimeField extends AbstractMultiControl {

    private Control dateCtrl;

    private Control timeCtrl;

    private Date date;

    private Date time;

    @Override
    public void updateInternalState() throws UnifyException {
        Date dateTime = getValue(Date.class);
        if (dateTime != null) {
        	date = CalendarUtils.getMidnightDate(dateTime);
        	time = CalendarUtils.getTimeOfDay(dateTime);
        }
    }

    @Override
    public void addPageAliases() throws UnifyException {
        addPageAlias(dateCtrl);
        addPageAlias(timeCtrl);
    }

    public Control getDateCtrl() {
		return dateCtrl;
	}

	public Control getTimeCtrl() {
		return timeCtrl;
	}

    public Date getDate() {
		return date;
	}

	public void setDate(Date date) throws UnifyException {
		this.date = date;
		setDateTime();
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) throws UnifyException {
		this.time = time;
		setDateTime();
	}
	
	@Override
    protected void doOnPageConstruct() throws UnifyException {
        dateCtrl = (Control) addInternalChildWidget(constructDate());
        timeCtrl = (Control) addInternalChildWidget(constructTime());
    }

	private void setDateTime() throws UnifyException {
		Date dateTime = CalendarUtils.getDateTime(date, time);
		setValue(dateTime);
	}

	private String constructDate() throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("!ui-date buttonImgSrc:$s{").append(getUplAttribute(String.class, "dateButtonImgSrc")).append("}");
		sb.append(" buttonSymbol:$s{").append(getUplAttribute(String.class, "dateButtonSymbol")).append("}");
		sb.append(" formatter:$d{").append(getUplAttribute(String.class, "dateFormatter")).append("}");
		sb.append(" type:").append(getUplAttribute(String.class, "dateType"));
		sb.append(" clearable:").append(getUplAttribute(boolean.class, "clearable"));
		sb.append(" binding:date style:$s{width:100%;}");
		return sb.toString();
	}

	private String constructTime() throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("!ui-time buttonImgSrc:$s{").append(getUplAttribute(String.class, "timeButtonImgSrc")).append("}");
		sb.append(" buttonSymbol:$s{").append(getUplAttribute(String.class, "timeButtonSymbol")).append("}");
		sb.append(" formatter:$d{").append(getUplAttribute(String.class, "timeFormatter")).append("}");
		sb.append(" clearable:").append(getUplAttribute(boolean.class, "clearable"));
		sb.append(" binding:time style:$s{width:100%;}");
		return sb.toString();
	}
}
