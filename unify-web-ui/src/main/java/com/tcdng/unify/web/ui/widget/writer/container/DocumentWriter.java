/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.container;

import java.util.HashSet;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.WebStringWriter;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.constant.ClientSyncNameConstants;
import com.tcdng.unify.web.ui.PagePathInfoRepository;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.DocumentLayout;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.container.BasicDocument;
import com.tcdng.unify.web.ui.widget.container.BasicDocumentResources;
import com.tcdng.unify.web.ui.widget.writer.AbstractPageWriter;

/**
 * Basic document writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(BasicDocument.class)
@Component("document-writer")
public class DocumentWriter extends AbstractPageWriter {

	@Configurable
	private PagePathInfoRepository pathInfoRepository;

	@Configurable
	private BasicDocumentResources resources;

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		BasicDocument document = (BasicDocument) widget;
		document.setClientId(getPageManager().getCurrentRequestClientId());
		
		writer.write("<!DOCTYPE html>");
		writer.write("<html ");
		writeTagAttributes(writer, document);
		writer.write(">");

		// Head
		writer.write("<head>");
		// Write title
		writer.write("<title>");
		String title = document.getCaption();
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
		writer.writeFileImageContextURL(document.getFavicon());
		writer.write("\">");

		// Write font symbols
		writeEmbeddedStyle(writer, document);

		// Write style sheet links
		writeStyleSheet(writer, "$t{css/unify-web.css}");
		Set<String> excludeStyleSheet = new HashSet<String>(document.getExcludeStyleSheet());

		String[] styleSheets = document.getStyleSheet();
		if (styleSheets != null) {
			for (String styleSheet : styleSheets) {
				if (!excludeStyleSheet.contains(styleSheet)) {
					writeStyleSheet(writer, styleSheet);
					excludeStyleSheet.add(styleSheet); // Avoid duplication
				}
			}
		}

		for (String styleSheet : getPageManager().getDocumentStyleSheets()) {
			if (!excludeStyleSheet.contains(styleSheet)) {
				writeStyleSheet(writer, styleSheet);
				excludeStyleSheet.add(styleSheet); // Avoid duplication
			}
		}

		writeResourcesStyleSheet(writer);

		// Write javascript sources
		writeJavascript(writer, "web/js/unify-web.js");
		Set<String> excludeScripts = new HashSet<String>(document.getExcludeScript());

		String[] scripts = document.getScript();
		if (scripts != null) {
			for (String script : scripts) {
				if (!excludeScripts.contains(script)) {
					writeJavascript(writer, script);
					excludeScripts.add(script); // Avoid duplication
				}
			}
		}

		for (String script : getPageManager().getDocumentsScripts()) {
			if (!excludeScripts.contains(script)) {
				writeJavascript(writer, script);
				excludeScripts.add(script); // Avoid duplication
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
			writer.write("') no-repeat;background-size:cover;");
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

		//Latency base
		writeLatencySection(writer, document);
		
		// Write document structure an content
		DocumentLayout documentLayout = document.getUplAttribute(DocumentLayout.class, "layout");
		writer.writeStructureAndContent(documentLayout, document);

		writer.write("</body></html>");
	}

	protected void writeLatencySection(ResponseWriter writer, Document document) throws UnifyException {
		writer.write("<div id=\"").write(document.getLatencyPanelId())
				.write("\" class=\"dclatency\" style=\"display:none;\">");
		writer.write("<div class=\"base\">");
		writer.write("<img src=\"");
		writer.writeContextResourceURL("/resource/file", MimeType.IMAGE.template(), "$t{images/latency.gif}");
		writer.write("\">");
		writer.write("</div>");
		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		BasicDocument document = (BasicDocument) widget;
		writer.write("<script");
		writeNonce(writer);
		writer.write(">");
		// Set document properties
		ControllerPathParts controllerPathParts = pathInfoRepository.getControllerPathParts(document);
		writer.write("ux.setupDocument(\"").write(document.getClientId()).write("\", \"")
				.write(controllerPathParts.getControllerPathId()).write("\", \"").write(document.getPopupBaseId())
				.write("\", \"").write(document.getPopupWinId()).write("\", \"").write(document.getPopupSysId())
				.write("\", \"").write(document.getLatencyPanelId()).write("\", \"").write(getSessionContext().getId())
				.write("\");");

		if (document.isPushUpdate()) {
			final String wsContextPath = getSessionContext().getContextPath() + ClientSyncNameConstants.SYNC_CONTEXT;
			writer.write("ux.wsPushUpdate(\"").write(wsContextPath).write("\");");
		}

		writer.useSecondary();
		// Write layout behavior
		DocumentLayout documentLayout = document.getUplAttribute(DocumentLayout.class, "layout");
		writer.writeBehavior(documentLayout, document);

		// Write inherited behavior
		super.doWriteBehavior(writer, document, handlers);

		// Write panel behaviors
		writeBehaviour(writer, document.getHeaderPanel());
		writeBehaviour(writer, document.getMenuPanel());
		writeBehaviour(writer, document.getContentPanel());
		writeBehaviour(writer, document.getFooterPanel());
		WebStringWriter scriptLsw = writer.discardSecondary();
		writer.write("var behaviorPrm = ").write(scriptLsw).write(";");
		writer.write("ux.perform(behaviorPrm);");

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

		// Write No-push
		if (getRequestContextUtil().isNoPushWidgets()) {
			writer.write("var noPushList = ").writeJsonArray(getRequestContextUtil().getNoPushWidgetIds()).write(";");
			writer.write("ux.markNoPushWidgets(noPushList);");
		}

		writer.write("ux.cascadeStretch();");

		// Set focus
		getRequestContextUtil().considerDefaultFocusOnWidget();
		if (getRequestContextUtil().isFocusOnWidgetOrDefault()) {
			writer.write("ux.setFocus({wdgid:\"").write(getRequestContextUtil().getFocusOnWidgetIdOrDefault())
					.write("\"});");
			getRequestContextUtil().clearFocusOnWidget();
		}
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
		if (isWithFontSymbolManager()) {
			StringBuilder fsb = new StringBuilder();
			int i = 0;
			fsb.append(".g_fsm {font-family: ").append(document.getFontFamily());
			for (String fontResource : getFontResources()) {
				writeFont(writer, "FontSymbolMngr" + i, fontResource);
				fsb.append(", 'FontSymbolMngr").append(i).append('\'');
				i++;
			}
			
			fsb.append(";}");
			writer.write(fsb);
		}
		
		// Additional
		Set<String> excludeFonts = new HashSet<String>();
		String[] fonts = document.getFont();
		if (fonts != null) {
			for (String font : fonts) {
				if (!excludeFonts.contains(font)) {
					final String[] parts =  font.split(":", 5);
					writeFont(writer, parts[0], parts[1], parts[2], parts[3], parts[4]);
					excludeFonts.add(font); // Avoid duplication
				}
			}
		}

		for (String font : getPageManager().getDocumentFonts()) {
			if (!excludeFonts.contains(font)) {
				final String[] parts =  font.split(":", 5);
				writeFont(writer, parts[0], parts[1], parts[2], parts[3], parts[4]);
				excludeFonts.add(font); // Avoid duplication
			}
		}
		
		writer.write("</style>");
	}

	private void writeImageBeforeCss(ResponseWriter writer, String className, String imgSrc) throws UnifyException {
		writer.write(className).write(" {vertical-align:middle;display: inline-block !important;} ").write(className)
				.write(":before {content: \"\";vertical-align:middle;display: inline-block;width: 100%;height: 100%;background: url(");
		writer.writeFileImageContextURL(imgSrc);
		writer.write(")no-repeat center/100% 100%; }");
	}

	private void writeFont(ResponseWriter writer, String family, String fontResource) throws UnifyException {
		writer.write("@font-face {font-family: '").write(family).write("'; src: url(");
		writer.writeContextResourceURL("/resource/file", MimeType.APPLICATION_OCTETSTREAM.template(), fontResource);
		writer.write(");} ");
	}

	private void writeFont(ResponseWriter writer, String family, String weight, String stretch, String style,
			String fontResource) throws UnifyException {
		writer.write("@font-face {font-family: '").write(family).write("'; font-weight:").write(weight)
				.write("; font-stretch:").write(stretch).write("; font-style:").write(style).write("; src: url(");
		writer.writeContextResourceURL("/resource/file", MimeType.APPLICATION_OCTETSTREAM.template(), fontResource);
		writer.write(");} ");
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
		writer.write("\"");
		writeNonce(writer);
		writer.write("></script>");
	}

	private void writeNonce(ResponseWriter writer) throws UnifyException {
		if (getRequestContextUtil().isWithNonce()) {
			writer.write(" nonce=\"");
			writer.write(getRequestContextUtil().getNonce());
			writer.write("\"");
		}
	}

	private void writeBehaviour(ResponseWriter writer, Panel panel) throws UnifyException {
		if (panel != null) {
			writer.writeBehavior(panel);
		}
	}
}
