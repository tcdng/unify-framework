/*
 * Copyright 2018-2019 The Code Department.
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
import com.tcdng.unify.web.ui.AbstractControl;

/**
 * Represents a text clock.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-textclock")
@UplAttributes({ @UplAttribute(name = "dateTitle", type = String.class, defaultValue = "$m{textclock.date.title}"),
        @UplAttribute(name = "timeTitle", type = String.class, defaultValue = "$m{textclock.time.title}") })
public class TextClock extends AbstractControl {

    public String getDateTitle() throws UnifyException {
        return getUplAttribute(String.class, "dateTitle");
    }

    public String getTimeTitle() throws UnifyException {
        return getUplAttribute(String.class, "timeTitle");
    }

    public String getDateId() throws UnifyException {
        return getPrefixedId("date_");
    }

    public String getTimeId() throws UnifyException {
        return getPrefixedId("time_");
    }
}
