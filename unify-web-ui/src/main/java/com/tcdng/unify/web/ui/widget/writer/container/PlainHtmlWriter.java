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

import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.PagePathInfoRepository;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.container.BasicDocumentResources;
import com.tcdng.unify.web.ui.widget.container.BasicPlainHtml;
import com.tcdng.unify.web.ui.widget.writer.AbstractPageWriter;

/**
 * Basic plain HTML writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(BasicPlainHtml.class)
@Component("plainhtml-writer")
public class PlainHtmlWriter extends AbstractPageWriter {

	@Configurable
	private PagePathInfoRepository pathInfoRepository;

	@Configurable
	private BasicDocumentResources resources;

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		BasicPlainHtml plainHtml = (BasicPlainHtml) widget;
		writer.write("<!DOCTYPE html>");
		writer.write("<html ");
		writeTagAttributes(writer, plainHtml);
		writer.write(">");

		// Head
		writer.write("<head>");
		// Write title
		writer.write("<title>");
		String title = plainHtml.getCaption();
		if (StringUtils.isNotBlank(title)) {
			writer.write(title);
		} else {
			writer.write(getUnifyComponentContext().getInstanceName());
		}
		writer.write("</title>");

		// Write favorite icon
		writer.write("<link rel=\"shortcut icon\" href=\"");
		writer.writeFileImageContextURL(plainHtml.getFavicon());
		writer.write("\">");

		// Write style sheet links
		Set<String> excludeStyleSheet = plainHtml.getExcludeStyleSheet();

		for (String styleSheet : getPageManager().getDocumentStyleSheets()) {
			if (!excludeStyleSheet.contains(styleSheet)) {
				writeStyleSheet(writer, styleSheet);
			}
		}

		String[] styleSheets = plainHtml.getStyleSheet();
		if (styleSheets != null) {
			for (String styleSheet : styleSheets) {
				if (!excludeStyleSheet.contains(styleSheet)) {
					writeStyleSheet(writer, styleSheet);
				}
			}
		}

		Set<String> excludeScripts = plainHtml.getExcludeScript();
		for (String script : getPageManager().getDocumentsScripts()) {
			if (!excludeScripts.contains(script)) {
				writeJavascript(writer, script);
			}
		}

		String[] scripts = plainHtml.getScript();
		if (scripts != null) {
			for (String script : scripts) {
				if (!excludeScripts.contains(script)) {
					writeJavascript(writer, script);
				}
			}
		}

		writer.write("</head>");

		// Body
		writer.write("<body class=\"dBody\"");
		String style = plainHtml.getStyle();
		String backImageSrc = plainHtml.getBackImageSrc();
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

		if (!StringUtils.isBlank(plainHtml.getBodyContent())) {
			writer.write(plainHtml.getBodyContent());
		}

		writer.write("</body></html>");

		if (!StringUtils.isBlank(plainHtml.getScripts())) {
			writer.write("<script>");
			writer.write(plainHtml.getScripts());
			writer.write("</script>");
		}
	}

	@Override
	protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {

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

}
