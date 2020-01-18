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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.LabeledText;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Labeled text writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(LabeledText.class)
@Component("labeledtext-writer")
public class LabeledTextWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        LabeledText labeledText = (LabeledText) widget;
        writer.write("<div ");
        writeTagAttributes(writer, labeledText);
        writer.write(">");
        writer.write("<span class=\"lttitle\">");
        writeCaption(writer, labeledText);
        writer.write(":</span>&nbsp;");
        String valueString = labeledText.getStringValue();
        if (labeledText.getUplAttribute(int.class, "maxLen") > 0) {
            valueString = StringUtils.ellipsize(valueString, labeledText.getUplAttribute(int.class, "maxLen"));
        }
        writer.write("<span class=\"ltcontent\">").writeWithHtmlEscape(valueString);
        writer.write("</span>&nbsp;");
        writer.write("</div>");
    }

}
