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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.DurationSelect;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Duration select writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(DurationSelect.class)
@Component("durationselect-writer")
public class DurationSelectWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        DurationSelect durationSelect = (DurationSelect) widget;
        writer.write("<div");
        writeTagAttributes(writer, durationSelect);
        writer.write(">");

        writer.write("<div class=\"dstable\">");
        writer.write("<div class=\"dsrow\">");
        if (durationSelect.isShowDays()) {
            writeFilter(writer, durationSelect.getDaySelCtrl(), resolveSessionMessage("$m{durationselect.days}"));
        }

        if (durationSelect.isShowHours()) {
            writeFilter(writer, durationSelect.getHourSelCtrl(), resolveSessionMessage("$m{durationselect.hours}"));
        }

        writeFilter(writer, durationSelect.getMinuteSelCtrl(), resolveSessionMessage("$m{durationselect.minutes}"));
        writer.write("</div></div>");

        writer.writeStructureAndContent(durationSelect.getDurationCtrl());
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);

        DurationSelect durationSelect = (DurationSelect) widget;
        if (durationSelect.isShowDays()) {
            writer.writeBehavior(durationSelect.getDaySelCtrl());
        }

        if (durationSelect.isShowHours()) {
            writer.writeBehavior(durationSelect.getHourSelCtrl());
        }

        writer.writeBehavior(durationSelect.getMinuteSelCtrl());

        writer.write("ux.rigDurationSelect({");
        writer.write("\"pId\":\"").write(durationSelect.getId()).write('"');
        if (durationSelect.isShowDays()) {
            writer.write(",\"pDaySelId\":\"").write(durationSelect.getDaySelCtrl().getId()).write('"');
        }

        if (durationSelect.isShowHours()) {
            writer.write(",\"pHourSelId\":\"").write(durationSelect.getHourSelCtrl().getId()).write('"');
        }

        writer.write(",\"pMinSelId\":\"").write(durationSelect.getMinuteSelCtrl().getId()).write('"');
        writer.write(",\"pDurationId\":\"").write(durationSelect.getDurationCtrl().getId()).write('"');
        writer.write("});");
    }

    private void writeFilter(ResponseWriter writer, Control select, String caption) throws UnifyException {
        if (select != null) {
            writer.write("<div class=\"dscell\">");
            writer.writeStructureAndContent(select);
            writer.write("<span class=\"dslabel\">");
            writer.write(caption).write("</span>");
            writer.write("</div>");
        }
    }
}
