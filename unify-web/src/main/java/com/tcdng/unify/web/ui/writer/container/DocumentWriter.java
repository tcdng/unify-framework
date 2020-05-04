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
package com.tcdng.unify.web.ui.writer.container;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.PathInfoRepository;
import com.tcdng.unify.web.PathParts;
import com.tcdng.unify.web.font.FontSymbolManager;
import com.tcdng.unify.web.ui.DocumentLayout;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.container.BasicDocument;
import com.tcdng.unify.web.ui.container.BasicDocumentResources;
import com.tcdng.unify.web.ui.writer.AbstractPageWriter;

/**
 * Basic document writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(BasicDocument.class)
@Component("document-writer")
public class DocumentWriter extends AbstractPageWriter {

    @Configurable
    private PathInfoRepository pathInfoRepository;

    @Configurable
    private BasicDocumentResources resources;

    @Configurable
    private FontSymbolManager fontSymbolManager;

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        BasicDocument document = (BasicDocument) widget;
        writer.write("<!DOCTYPE html>");
        writer.write("<html ");
        writeTagAttributes(writer, document);
        writer.write(">");

        // Head
        writer.write("<head>");
        // Write title
        writer.write("<title>");
        String title = document.getUplAttribute(String.class, "caption");
        if (StringUtils.isNotBlank(title)) {
            writer.write(title);
        } else {
            writer.write(getUnifyComponentContext().getInstanceName());
        }
        writer.write("</title>");

        // Write META
        if (document.getMeta() != null) {
            for (String oneMeta : document.getMeta()) {
                writer.write("<meta ");
                writer.write(oneMeta);
                writer.write(" >");
            }
        }

        // Write favorite icon
        writer.write("<link rel=\"shortcut icon\" href=\"");
        writer.writeFileImageContextURL(document.getUplAttribute(String.class, "favicon"));
        writer.write("\">");

        // Write style sheet links
        writeStyleSheet(writer, "$t{css/unify-web.css}");

        for (String styleSheet : getPageManager().getDocumentStyleSheets()) {
            writeStyleSheet(writer, styleSheet);
        }

        String[] styleSheets = document.getUplAttribute(String[].class, "styleSheet");
        if (styleSheets != null) {
            for (String styleSheet : styleSheets) {
                writeStyleSheet(writer, styleSheet);
            }
        }

        writeResourcesStyleSheet(writer);

        // Write font symbols
        writeEmbeddedStyle(writer, document);

        // Write javascript sources
        writeJavascript(writer, "web/js/unify-web.js");

        for (String script : getPageManager().getDocumentsScripts()) {
            writeJavascript(writer, script);
        }

        String[] scripts = document.getUplAttribute(String[].class, "script");
        if (scripts != null) {
            for (String script : scripts) {
                writeJavascript(writer, script);
            }
        }

        writeResourcesScript(writer);

        writer.write("</head>");

        // Body
        writer.write("<body class=\"dBody\"");
        String style = document.getStyle();
        String backImageSrc = document.getBackImageSrc();
        if (StringUtils.isNotBlank(backImageSrc)) {
            writer.write(" style=\"background: url('");
            writer.writeFileImageContextURL(backImageSrc);
            writer.write("') no-repeat;background-size:100% 100%;");
            if (style != null) {
                writer.write(style);
            }
            writer.write("\"");
        } else {
            if (style != null) {
                writer.write(" style=\"").write(style).write("\"");
            }
        }

        writer.write(">");

        // Popup base
        writer.write("<div id=\"").write(document.getPopupBaseId()).write("\" class=\"dcpopbase\">");

        writer.write("<div id=\"").write(document.getPopupWinId()).write("\" class=\"dcpop\"></div>");
        writer.write("<div id=\"").write(document.getPopupSysId()).write("\" class=\"dcsysinfo\"></div>");

        writer.write("</div>");

        // Write document structure an content
        DocumentLayout documentLayout = document.getUplAttribute(DocumentLayout.class, "layout");
        writer.writeStructureAndContent(documentLayout, document);

        writer.write("</body></html>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        BasicDocument document = (BasicDocument) widget;
        writer.write("<script>");
        // Set document properties
        PathParts pathParts = pathInfoRepository.getPathParts(document);
        writer.write("ux.setupDocument(\"").write(pathParts.getControllerName()).write("\", \"")
                .write(document.getPopupBaseId()).write("\", \"").write(document.getPopupWinId()).write("\", \"")
                .write(document.getPopupSysId()).write("\", \"").write(getSessionContext().getId()).write("\");");

        // Write layout behavior
        DocumentLayout documentLayout = document.getUplAttribute(DocumentLayout.class, "layout");
        writer.writeBehaviour(documentLayout, document);

        // Write inherited behavior
        super.doWriteBehavior(writer, document);

        // Write panel behaviors
        writeBehaviour(writer, document.getHeaderPanel());
        writeBehaviour(writer, document.getMenuPanel());
        writeBehaviour(writer, document.getContentPanel());
        writeBehaviour(writer, document.getFooterPanel());

        // Write page aliases
        writer.write("var aliasPrms = {");
        writer.write("\"pageNameAliases\":");
        writer.writeJsonPageNameAliasesArray();
        writer.write("}; ux.setPageNameAliases(aliasPrms);");

        // Write debouncing
        if (getRequestContextUtil().isRegisteredDebounceWidgets()) {
            writer.write("var debounceList = ")
                    .writeJsonArray(getRequestContextUtil().getAndClearRegisteredDebounceWidgetIds()).write(";");
            writer.write("ux.registerDebounce(debounceList);");
        }

        writer.write("ux.cascadeStretch();");
        writer.write("</script>");
    }

    @Override
    protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {

    }

    private void writeEmbeddedStyle(ResponseWriter writer, BasicDocument document) throws UnifyException {
        writer.write("<style>");
        // Write custom check box images
        writeImageBeforeCss(writer, " .g_cba", "$t{images/checked.png}");
        writeImageBeforeCss(writer, " .g_cbb", "$t{images/unchecked.png}");
        writeImageBeforeCss(writer, " .g_cbc", "$t{images/checked_gray.png}");
        writeImageBeforeCss(writer, " .g_cbd", "$t{images/unchecked_gray.png}");

        // Write font symbols
        if (fontSymbolManager != null) {
            StringBuilder fsb = new StringBuilder();
            int i = 0;
            fsb.append(".g_fsm {font-family: ").append(document.getUplAttribute(String.class, "fontFamily"));
            for (String fontResource : fontSymbolManager.getFontResources()) {
                fsb.append(", 'FontSymbolMngr").append(i).append('\'');

                writer.write("@font-face {font-family: 'FontSymbolMngr").write(i).write("'; src: url(");
                writer.writeContextResourceURL("/resource/file", MimeType.APPLICATION_OCTETSTREAM.template(),
                        fontResource);
                writer.write(");} ");
                i++;
            }
            fsb.append(";}");

            writer.write(fsb);
        }
        writer.write("</style>");
    }

    private void writeImageBeforeCss(ResponseWriter writer, String className, String imgSrc) throws UnifyException {
        writer.write(className).write(" {vertical-align:middle;display: inline-block;} ").write(className).write(
                ":before {content: \"\";vertical-align:middle;display: inline-block;width: 100%;height: 100%;background: url(");
        writer.writeFileImageContextURL(imgSrc);
        writer.write(")no-repeat center/100% 100%; }");
    }

    private void writeResourcesStyleSheet(ResponseWriter writer) throws UnifyException {
        if (resources != null) {
            for (String sheetLink : resources.getStyleSheetResourceLinks()) {
                writer.write("<link href=\"");
                writer.write(sheetLink);
                writer.write("\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\">");
            }
        }
    }

    private void writeResourcesScript(ResponseWriter writer) throws UnifyException {
        if (resources != null) {
            for (String scriptLink : resources.getScriptResourceLinks()) {
                writer.write("<script src=\"");
                writer.write(scriptLink);
                writer.write("\"></script>");
            }
        }
    }

    private void writeStyleSheet(ResponseWriter writer, String styleSheet) throws UnifyException {
        writer.write("<link href=\"");
        writer.writeContextResourceURL("/resource/file", MimeType.TEXT_CSS.template(), styleSheet);
        writer.write("\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\">");
    }

    private void writeJavascript(ResponseWriter writer, String script) throws UnifyException {
        writer.write("<script src=\"");
        writer.writeContextResourceURL("/resource/file", MimeType.TEXT_JAVASCRIPT.template(), script);
        writer.write("\"></script>");
    }

    private void writeBehaviour(ResponseWriter writer, Panel panel) throws UnifyException {
        if (panel != null) {
            writer.writeBehaviour(panel);
        }
    }
}
