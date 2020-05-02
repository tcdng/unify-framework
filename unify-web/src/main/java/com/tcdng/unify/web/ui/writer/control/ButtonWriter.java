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
import com.tcdng.unify.web.ui.TargetControl;
import com.tcdng.unify.web.ui.control.Button;
import com.tcdng.unify.web.ui.writer.AbstractTargetControlWriter;

/**
 * Button writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(Button.class)
@Component("button-writer")
public class ButtonWriter extends AbstractTargetControlWriter {

    @Override
    protected void doWriteTargetControl(ResponseWriter writer, TargetControl targetControl) throws UnifyException {
        Button button = (Button) targetControl;
        String imageSrc = button.getUplAttribute(String.class, "imageSrc");
        writer.write("<button type=\"button\"");
        writeTagAttributes(writer, button);
        writer.write("</>");
        if (StringUtils.isNotBlank(imageSrc)) {
            writer.write("<img src=\"");
            writer.writeFileImageContextURL(imageSrc);
            writer.write("\">");
            writer.write("<span>");
            writeCaption(writer, button);
            writer.write("</span>");
        } else {
            writeCaption(writer, button);
        }

        writer.write("</button>");
    }
}
