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
package com.tcdng.unify.core.transform;

import java.util.Date;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Used to transform date values to dates with time only elements.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Tooling(name = "timeOfDayTrans", description = "Time-Of-Day Transformer")
@Component("timeofday-transformer")
public class TimeOfDayTransformer extends AbstractUnifyComponent implements Transformer<Date, Date> {

    @Override
    public Date forwardTransform(Date date) throws UnifyException {
        return CalendarUtils.getTimeOfDay(date);
    }

    @Override
    public Date reverseTransform(Date date) throws UnifyException {
        return CalendarUtils.getTimeOfDay(date);
    }

    @Override
    protected void onInitialize() throws UnifyException {
    }

    @Override
    protected void onTerminate() throws UnifyException {
    }
}
