/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.layout;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.DocumentLayout;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.layout.DesktopType0Layout;

/**
 * Type-0 desktop layout writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(DesktopType0Layout.class)
@Component("desktoptype0-writer")
public class DesktopType0LayoutWriter extends AbstractDocumentLayoutWriter {

    @Override
    public void writeBehaviour(ResponseWriter writer, DocumentLayout layout, Document document) throws UnifyException {

    }

    @Override
    protected void writeInnerStructureAndContent(ResponseWriter writer, DocumentLayout layout, Document document)
            throws UnifyException {
        writer.write("<div class=\"dt0row\"><div class=\"dt0cell\">");
        for (String longName : document.getLayoutWidgetLongNames()) {
            Widget widget = document.getWidgetByLongName(longName);
            if (widget.isVisible()) {
                writer.writeStructureAndContent(widget);
            } else if (widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
        }
        writer.write("</div></div>");
    }

}
