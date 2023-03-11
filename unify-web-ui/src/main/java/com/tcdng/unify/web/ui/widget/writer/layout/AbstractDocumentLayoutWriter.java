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
package com.tcdng.unify.web.ui.widget.writer.layout;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.DocumentLayout;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.writer.AbstractDhtmlWriter;
import com.tcdng.unify.web.ui.widget.writer.DocumentLayoutWriter;

/**
 * Abstract base class for UI document layout writers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDocumentLayoutWriter extends AbstractDhtmlWriter implements DocumentLayoutWriter {

    @Override
    public void writeStructureAndContent(ResponseWriter writer, DocumentLayout layout, Document document)
            throws UnifyException {
        writer.write("<div");
        writeTagId(writer, "lay_" + document.getId());
        writeTagStyleClass(writer, layout.getStyleClass());
        writeTagStyle(writer, layout.getStyle());
        writer.write(">");
        writeInnerStructureAndContent(writer, layout, document);
        writer.write("</div>");
    }

    protected void writeSection(ResponseWriter writer, String styleClass, Panel panel) throws UnifyException {
        if (panel != null) {
            writer.write("<div class=\"");
            writer.write(styleClass);
            writer.write("\"><div class=\"cell\">");
            writer.writeStructureAndContent(panel);
            writer.write("</div></div>");
        }
    }

    protected void writeLatencySection(ResponseWriter writer, Document document) throws UnifyException {
		writer.write("<div id=\"").write(document.getLatencyPanelId())
				.write("\" class=\"dclatency\" style=\"display:none;\">");
		writer.write("<img src=\"");
		writer.writeContextResourceURL("/resource/file", MimeType.IMAGE.template(), "$t{images/latency.gif}");
		writer.write("\">");
		writer.write("</div>");
    }
    
    protected abstract void writeInnerStructureAndContent(ResponseWriter writer, DocumentLayout layout,
            Document document) throws UnifyException;

}
