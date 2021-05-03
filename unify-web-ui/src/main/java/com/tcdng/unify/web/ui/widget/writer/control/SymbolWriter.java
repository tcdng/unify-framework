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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.font.FontSymbolManager;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.TargetControl;
import com.tcdng.unify.web.ui.widget.control.Symbol;
import com.tcdng.unify.web.ui.widget.writer.AbstractTargetControlWriter;

/**
 * Symbol writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(Symbol.class)
@Component("symbol-writer")
public class SymbolWriter extends AbstractTargetControlWriter {

    @Configurable
    private FontSymbolManager fontSymbolManager;

    public void setFontSymbolManager(FontSymbolManager fontSymbolManager) {
        this.fontSymbolManager = fontSymbolManager;
    }

    @Override
    protected void doWriteTargetControl(ResponseWriter writer, TargetControl targetControl) throws UnifyException {
        Symbol symbolWidget = (Symbol) targetControl;
        writer.write("<button type=\"button\"");
        writeTagAttributesWithTrailingExtraStyleClass(writer, symbolWidget, "g_fsm");
        writer.write("/>");
        if (fontSymbolManager != null) {
            String symbol = symbolWidget.getUplAttribute(String.class, "symbol");
            writer.write(fontSymbolManager.resolveSymbolHtmlHexCode(symbol));
        } else {
            writer.write("&#x25e6;");
        }
        writer.write("</button>");
    }
}
