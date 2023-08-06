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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.AssignmentBox;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Assignment box writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(AssignmentBox.class)
@Component("assignmentbox-writer")
public class AssignmentBoxWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        AssignmentBox assignmentBox = (AssignmentBox) widget;
        writer.write("<div");
        writeTagAttributes(writer, assignmentBox);
        writer.write(">");

        writer.write("<div class=\"abftable\">");
        writer.write("<div class=\"abrow\">");
        writeFilter(writer, assignmentBox.getFilterSel1(), assignmentBox.getFilterCaption1());
        writeFilter(writer, assignmentBox.getFilterSel2(), assignmentBox.getFilterCaption2());
        writer.write("</div></div>");

        writer.write("<div class=\"abtable\">");
        writer.write("<div class=\"abrow\">");
        // Assigned List
        writer.write("<div class=\"abscell\"><fieldset class=\"abfieldset\"><legend>")
        .write(assignmentBox.getAssignCaption()).write(":</legend>");
        writer.writeStructureAndContent(assignmentBox.getAssignSel());
        writer.write("</fieldset></div>");
        if (!assignmentBox.isShowAssignedOnly()) {
            // Action Buttons
            writer.write("<div class=\"abbcell\"><div>");
            writer.writeStructureAndContent(assignmentBox.getAssignBtn());
            writer.write("</div><div>");
            writer.writeStructureAndContent(assignmentBox.getUnassignBtn());
            writer.write("</div>");
            if(assignmentBox.isAllowAssignAll()) {
                writer.write("<div>");
                writer.writeStructureAndContent(assignmentBox.getAssignAllBtn());
                writer.write("</div><div>");
                writer.writeStructureAndContent(assignmentBox.getUnassignAllBtn());
                writer.write("</div>");
            }
            writer.write("</div>");

            // Unassigned List
            writer.write("<div class=\"abscell\"><fieldset class=\"abfieldset\"><legend>")
            .write(assignmentBox.getUnassignCaption()).write(":</legend>");
            writer.writeStructureAndContent(assignmentBox.getUnassignSel());
            writer.write("</fieldset></div>");
        }
        writer.write("</div></div>");
        
        writer.write("</div>");
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		AssignmentBox assignmentBox = (AssignmentBox) widget;
		writer.writeBehavior(assignmentBox.getAssignSel());
		writer.writeBehavior(assignmentBox.getUnassignSel());

		Control filter1 = assignmentBox.getFilterSel1();
		if (filter1 != null) {
			filter1.setEditable(true);
			writer.writeBehavior(filter1);
		}

		Control filter2 = assignmentBox.getFilterSel2();
		if (filter2 != null) {
			filter2.setEditable(true);
			writer.writeBehavior(filter2);
		}

		// Append rigging
		writer.beginFunction("ux.rigAssignmentBox");
		writer.writeParam("pId", assignmentBox.getId());
		writer.writeCommandURLParam("pCmdURL");
		writer.writeParam("pContId", assignmentBox.getContainerId());
		if (assignmentBox.getFilterSel1() != null) {
			writer.writeParam("pFilterSel1Id", assignmentBox.getFilterSel1().getId());
		}

		if (assignmentBox.getFilterSel2() != null) {
			writer.writeParam("pFilterSel2Id", assignmentBox.getFilterSel2().getId());
		}

		writer.writeParam("pAssnSelId", assignmentBox.getAssignSel().getId());
		writer.writeParam("pAssnOnly", assignmentBox.isShowAssignedOnly());
		if (!assignmentBox.isShowAssignedOnly()) {
			writer.writeParam("pAssnAll", assignmentBox.isAllowAssignAll());
			writer.writeParam("pUnassnSelId", assignmentBox.getUnassignSel().getId());
			writer.writeParam("pAssnBtnId", assignmentBox.getAssignBtn().getId());
			writer.writeParam("pAssnAllBtnId", assignmentBox.getAssignAllBtn().getId());
			writer.writeParam("pUnassnBtnId", assignmentBox.getUnassignBtn().getId());
			writer.writeParam("pUnassnAllBtnId", assignmentBox.getUnassignAllBtn().getId());
		}
		writer.writeParam("pEditable", assignmentBox.isContainerEditable());
		writer.writeParam("pRef", DataUtils.toArray(String.class, writer.getPostCommandRefs()));
		writer.endFunction();
	}

    private void writeFilter(ResponseWriter writer, Control filter, String caption)
            throws UnifyException {
        if (filter != null) {
        	filter.setEditable(true);
            writer.write("<div class=\"abscell\"> <span class=\"ablabel\">");
            writer.write(caption).write(":</span>");
            writer.writeStructureAndContent(filter);
            writer.write("</div>");
        }
    }
}
