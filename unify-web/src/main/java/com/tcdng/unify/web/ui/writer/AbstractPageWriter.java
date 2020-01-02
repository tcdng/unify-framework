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
package com.tcdng.unify.web.ui.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.AbstractPage;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;

/**
 * Abstract base class for UI page writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractPageWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        AbstractPage abstractPage = (AbstractPage) widget;
        writer.write("<div");
        writePanelTagAttributes(writer, abstractPage);
        writer.write(">");
        abstractPage.cascadeValueStore();
        writeInnerStructureAndContent(writer, abstractPage);
        writer.write("</div>");
    }

    @Override
    protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
        AbstractPage abstractPage = (AbstractPage) panel;
        writeLayoutContent(writer, abstractPage);
    }

}
