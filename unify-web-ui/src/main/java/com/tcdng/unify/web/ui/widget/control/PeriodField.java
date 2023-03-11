/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.data.Period;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.ui.widget.WriteWork;

/**
 * Represents an input field for capturing period.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-period")
@UplAttributes({ @UplAttribute(name = "precision", type = int.class, defaultVal = "4"),
        @UplAttribute(name = "list", type = String.class, defaultVal = "frequencyunitlist"),
        @UplAttribute(name = "formatter", type = Formatter.class, defaultVal = "$d{!integerformat}"),
        @UplAttribute(name = "extStyleClass", type = String.class, defaultVal = "trread"),
        @UplAttribute(name = "extReadOnly", type = boolean.class, defaultVal = "false") })
public class PeriodField extends AbstractListPopupTextField {

    @Override
    public void onPageConstruct() throws UnifyException {
        NumberFormatter<?> numberFormatter = (NumberFormatter<?>) getFormatter();
        int precision = getUplAttribute(int.class, "precision");
        numberFormatter.setPrecision(precision);

        super.onPageConstruct();
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public ExtensionType getExtensionType() {
        return ExtensionType.FACADE_HIDDEN;
    }

    @Override
    public boolean isPopupOnEditableOnly() {
        return true;
    }

    @Override
    public boolean isBindEventsToFacade() throws UnifyException {
        return false;
    }

    @Override
    public boolean isOpenPopupOnFac() {
        return false;
    }

    public Integer getMagnitude() throws UnifyException {
        Period period = getPeriod();
        return period != null ? period.getMagnitude() : null;
    }

    public FrequencyUnit getFrequencyUnit() throws UnifyException {
        Period period = getPeriod();
        return period != null ? period.getUnit() : null;
    }

    public String getFramePanelId() throws UnifyException {
        return getPrefixedId("frm_");
    }

    public String getListPanelId() throws UnifyException {
        return getPrefixedId("lst_");
    }

    private Period getPeriod() throws UnifyException {
        WriteWork writeWork = getWriteWork();
        Period period = writeWork.get(Period.class, "period");
        if (period == null) {
            period = getValue(Period.class);
            writeWork.set("period", period);
        }

        return period;
    }
}
