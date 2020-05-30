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

package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.CalendarUtils.DateDifference;
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.Widget;

/**
 * Widget used for selecting a duration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-durationselect")
@UplAttributes({ @UplAttribute(name = "showDays", type = boolean.class, defaultVal = "true"),
        @UplAttribute(name = "showHours", type = boolean.class, defaultVal = "true") })
public class DurationSelect extends AbstractMultiControl {

    private static final int MINUTE_JUMP = 5;

    private Control daySelCtrl;

    private Control hourSelCtrl;

    private Control minuteSelCtrl;

    private Control durationCtrl;

    private int days;

    private int hours;

    private int minutes;

    private int duration; // Duration in minutes

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();

        daySelCtrl = (Control) addInternalChildWidget(
                "!ui-select styleClass:$e{dsselect} list:dayslist size:2 binding:days");
        hourSelCtrl = (Control) addInternalChildWidget(
                "!ui-select styleClass:$e{dsselect} list:hourslist size:2 binding:hours");
        minuteSelCtrl = (Control) addInternalChildWidget(
                "!ui-select styleClass:$e{dsselect} list:minuteslist size:2 listParamType:IMMEDIATE listParams:$l{"
                        + MINUTE_JUMP + "} binding:minutes");
        durationCtrl = (Control) addInternalChildWidget("!ui-hidden binding:duration");
    }

    @Override
    public void updateInternalState() throws UnifyException {
        super.updateInternalState();

        duration = getValue(int.class);
        if (duration < 0) {
            duration = 0;
        }

        duration = duration - (duration % 5);
        DateDifference dayDiff = CalendarUtils
                .getDateDifference(CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.MINUTE, duration));
        days = dayDiff.getDays();
        hours = dayDiff.getHours();
        minutes = dayDiff.getMinutes();

        setValue(duration);
    }

    @Override
    public void addPageAliases() throws UnifyException {
        addPageAlias(daySelCtrl);
        addPageAlias(hourSelCtrl);
        addPageAlias(minuteSelCtrl);
        addPageAlias(durationCtrl);
    }

    @Override
    protected void onInternalChildPopulated(Widget widget) throws UnifyException {
        if(durationCtrl.equals(widget)) {
            setValue(duration);
        }
    }

    public boolean isShowDays() throws UnifyException {
        return getUplAttribute(boolean.class, "showDays");
    }

    public boolean isShowHours() throws UnifyException {
        return getUplAttribute(boolean.class, "showHours");
    }

    public Control getDaySelCtrl() {
        return daySelCtrl;
    }

    public Control getHourSelCtrl() {
        return hourSelCtrl;
    }

    public Control getMinuteSelCtrl() {
        return minuteSelCtrl;
    }

    public Control getDurationCtrl() {
        return durationCtrl;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
