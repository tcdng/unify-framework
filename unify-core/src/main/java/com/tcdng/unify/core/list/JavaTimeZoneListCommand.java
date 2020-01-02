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
package com.tcdng.unify.core.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Java time zone list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("javatimezonelist")
public class JavaTimeZoneListCommand extends AbstractZeroParamsListCommand {

    private List<Listable> javaTimeZoneList;

    @Override
    public List<? extends Listable> execute(Locale locale, ZeroParams params) throws UnifyException {
        if (javaTimeZoneList == null) {
            javaTimeZoneList = new ArrayList<Listable>();

            String[] availableZoneIds = TimeZone.getAvailableIDs();

            for (String zoneId : availableZoneIds) {
                if (zoneId.indexOf('/') > 0) {
                    if (!zoneId.startsWith("Etc") && !zoneId.startsWith("SystemV")) {
                        String utc = getUTCOffset(TimeZone.getTimeZone(zoneId).getRawOffset());
                        javaTimeZoneList.add(new ListData(zoneId, String.format("%s %s", zoneId, utc)));
                    }
                }
            }

            DataUtils.sort(javaTimeZoneList, ListData.class, "listDescription", true);
        }

        return javaTimeZoneList;
    }

    private String getUTCOffset(int rawOffset) {
        if (rawOffset != 0) {
            long hours = TimeUnit.MILLISECONDS.toHours(rawOffset);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(rawOffset);
            return String.format("(UTC%+03d:%02d)", hours, Math.abs(minutes - TimeUnit.HOURS.toMinutes(hours)));
        }

        return "(UTC+00:00)";
    }
}
