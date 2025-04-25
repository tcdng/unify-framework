/*
 * Copyright (c) 2018-2025 The Code Department.
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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.TargetControl;
import com.tcdng.unify.web.ui.widget.control.Button;
import com.tcdng.unify.web.ui.widget.writer.AbstractTargetControlWriter;

/**
 * Button writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(Button.class)
@Component("button-writer")
public class ButtonWriter extends AbstractTargetControlWriter {

    @Override
    protected void doWriteTargetControl(ResponseWriter writer, TargetControl targetControl) throws UnifyException {
        Button button = (Button) targetControl;
        writer.write("<button type=\"button\"");
        writeTagAttributesWithTrailingExtraStyleClass(writer, button, "g_fsm");
        writer.write("/>");
        String imageSrc = button.getUplAttribute(String.class, "imageSrc");
        String caption = button.isResolve()? resolveSessionMessage(button.getCaption()) : button.getCaption();
        if (StringUtils.isNotBlank(imageSrc)) {
            writer.write("<img src=\"");
            writer.writeFileImageContextURL(imageSrc);
            writer.write("\">");
            if (caption != null) {
                writer.write("<span>");
                if (button.isHtmlEscape()) {
                	writer.writeWithHtmlEscape(caption);
                } else {
                	writer.write(caption);
                }
                
                writer.write("</span>");
            }
        } else {
            boolean isSymbol = false;
            if (isWithFontSymbolManager()) {
                String symbol = button.getSymbol();
                if (isSymbol = !StringUtils.isBlank(symbol)) {
                    writer.write(resolveSymbolHtmlHexCode(symbol));
                }
            }

            caption = caption == null? resolveValue(button) : caption;
            if (caption != null) {
                if (isSymbol) {
                    writer.write("&nbsp;&nbsp;");
                }
                
                if (button.isHtmlEscape()) {
                	writer.writeWithHtmlEscape(caption);
                } else {
                	writer.write(caption);
                }
            }
        }

        writer.write("</button>");
    }
}
