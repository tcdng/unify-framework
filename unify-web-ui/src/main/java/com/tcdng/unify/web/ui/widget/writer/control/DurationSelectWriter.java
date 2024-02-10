/*
 * Copyright 2018-2024 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.DurationSelect;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Duration select writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(DurationSelect.class)
@Component("durationselect-writer")
public class DurationSelectWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        DurationSelect durationSelect = (DurationSelect) widget;
        writer.write("<div");
        writeTagStyleClass(writer, durationSelect);
        writeTagStyle(writer, durationSelect);
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

        writer.write("<input type=\"hidden\"");
        writeTagId(writer, durationSelect);
        writeTagName(writer, durationSelect);
        writer.write("/>");
        writer.write("</div>");
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		DurationSelect durationSelect = (DurationSelect) widget;
		if (durationSelect.isShowDays()) {
			writer.writeBehavior(durationSelect.getDaySelCtrl());
		}

		if (durationSelect.isShowHours()) {
			writer.writeBehavior(durationSelect.getHourSelCtrl());
		}

		writer.writeBehavior(durationSelect.getMinuteSelCtrl());

		writer.beginFunction("ux.rigDurationSelect");
		writer.writeParam("pId", durationSelect.getId());
		if (durationSelect.isShowDays()) {
			writer.writeParam("pDaySelId", durationSelect.getDaySelCtrl().getId());
		}

		if (durationSelect.isShowHours()) {
			writer.writeParam("pHourSelId", durationSelect.getHourSelCtrl().getId());
		}

		int duration = durationSelect.getValue(int.class);
		duration = duration - (duration % durationSelect.getMinuteJump());

		writer.writeParam("pMinSelId", durationSelect.getMinuteSelCtrl().getId());
		writer.writeParam("pVal", duration);
		writer.endFunction();
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
