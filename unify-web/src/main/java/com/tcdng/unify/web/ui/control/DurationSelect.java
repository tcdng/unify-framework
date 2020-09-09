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
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;

/**
 * Widget used for selecting a duration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-durationselect")
@UplAttributes({
        @UplAttribute(name = "showDays", type = boolean.class, defaultVal = "true"),
        @UplAttribute(name = "showHours", type = boolean.class, defaultVal = "true"),
        @UplAttribute(name = "minuteStep", type = int.class, defaultVal = "5")})
public class DurationSelect extends AbstractMultiControl {

    private Control daySelCtrl;

    private Control hourSelCtrl;

    private Control minuteSelCtrl;

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        daySelCtrl = (Control) addInternalChildWidget(
                "!ui-select styleClass:$e{dsselect} list:dayslist size:2");
        hourSelCtrl = (Control) addInternalChildWidget(
                "!ui-select styleClass:$e{dsselect} list:hourslist size:2");
        minuteSelCtrl = (Control) addInternalChildWidget(
                "!ui-select styleClass:$e{dsselect} list:minuteslist size:2 listParamType:IMMEDIATE listParams:$l{"
                        + getMinuteJump() + "}");
    }

    public boolean isShowDays() throws UnifyException {
        return getUplAttribute(boolean.class, "showDays");
    }

    public boolean isShowHours() throws UnifyException {
        return getUplAttribute(boolean.class, "showHours");
    }

    public int getMinuteJump() throws UnifyException {
        int jump = getUplAttribute(int.class, "minuteStep");
        if (jump <= 0) {
            return 1;
        }
        
        return jump;
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

}
