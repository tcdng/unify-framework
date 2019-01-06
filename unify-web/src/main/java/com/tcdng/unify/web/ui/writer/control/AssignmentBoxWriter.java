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
package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.AssignmentBox;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Assignment box writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(AssignmentBox.class)
@Component("assignmentbox-writer")
public class AssignmentBoxWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        AssignmentBox assignmentBox = (AssignmentBox) widget;
        assignmentBox.updateState();
        writer.write("<div");
        writeTagAttributes(writer, assignmentBox);
        writer.write(">");

        writer.write("<div class=\"abftable\">");
        writer.write("<div class=\"abrow\">");
        writeFilter(writer, assignmentBox, assignmentBox.getFilterSel1(), assignmentBox.getFilterCaption1());
        writeFilter(writer, assignmentBox, assignmentBox.getFilterSel2(), assignmentBox.getFilterCaption2());
        writer.write("</div></div>");

        writer.write("<div class=\"abtable\">");
        writer.write("<div class=\"abrow\"><div class=\"abscell\"><fieldset class=\"abfieldset\"><legend>")
                .write(assignmentBox.getAssignCaption()).write(":</legend>");
        writer.writeStructureAndContent(assignmentBox.getAssignSel());
        writer.write("</fieldset></div><div class=\"abbcell\"><div>");
        writer.writeStructureAndContent(assignmentBox.getAssignBtn());
        writer.write("</div><div>");
        writer.writeStructureAndContent(assignmentBox.getAssignAllBtn());
        writer.write("</div><div>");
        writer.writeStructureAndContent(assignmentBox.getUnassignBtn());
        writer.write("</div><div>");
        writer.writeStructureAndContent(assignmentBox.getUnassignAllBtn());
        writer.write("</div></div><div class=\"abscell\"><fieldset class=\"abfieldset\"><legend>")
                .write(assignmentBox.getUnassignCaption()).write(":</legend>");
        writer.writeStructureAndContent(assignmentBox.getUnassignSel());
        writer.write("</fieldset></div></div>");
        writer.write("</div></div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);

        AssignmentBox assignmentBox = (AssignmentBox) widget;
        writer.writeBehaviour(assignmentBox.getAssignSel());
        writer.writeBehaviour(assignmentBox.getUnassignSel());

        Control filter1 = assignmentBox.getFilterSel1();
        if (filter1 != null) {
            writer.writeBehaviour(filter1);
        }

        Control filter2 = assignmentBox.getFilterSel2();
        if (filter2 != null) {
            writer.writeBehaviour(filter2);
        }

        // Append rigging
        writer.write("ux.rigAssignmentBox({");
        writer.write("\"pId\":\"").write(assignmentBox.getId()).write('"');
        writer.write(",\"pCmdURL\":\"");
        writer.writeCommandURL();
        writer.write('"');
        writer.write(",\"pContId\":\"").write(assignmentBox.getContainerId()).write('"');
        if (assignmentBox.getFilterSel1() != null) {
            writer.write(",\"pFilterSel1Id\":\"").write(assignmentBox.getFilterSel1().getId()).write('"');
        }

        if (assignmentBox.getFilterSel2() != null) {
            writer.write(",\"pFilterSel2Id\":\"").write(assignmentBox.getFilterSel2().getId()).write('"');
        }

        writer.write(",\"pAssnSelId\":\"").write(assignmentBox.getAssignSel().getId()).write('"');
        writer.write(",\"pUnassnSelId\":\"").write(assignmentBox.getUnassignSel().getId()).write('"');
        writer.write(",\"pAssnBtnId\":\"").write(assignmentBox.getAssignBtn().getId()).write('"');
        writer.write(",\"pAssnAllBtnId\":\"").write(assignmentBox.getAssignAllBtn().getId()).write('"');
        writer.write(",\"pUnassnBtnId\":\"").write(assignmentBox.getUnassignBtn().getId()).write('"');
        writer.write(",\"pUnassnAllBtnId\":\"").write(assignmentBox.getUnassignAllBtn().getId()).write('"');
        writer.write(",\"pEditable\":").write(assignmentBox.isContainerEditable());
        writer.write("});");
    }

    private void writeFilter(ResponseWriter writer, AssignmentBox assignmentBox, Control filter, String caption)
            throws UnifyException {
        if (filter != null) {
            writer.write("<div class=\"abscell\"> <span class=\"ablabel\">");
            writer.write(caption).write(":</span>");
            writer.writeStructureAndContent(filter);
            writer.write("</div>");
        }
    }
}
