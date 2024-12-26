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
package com.tcdng.unify.web.ui;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.web.AbstractController;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ClientResponse;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.DocPathParts;
import com.tcdng.unify.web.DocumentController;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.font.FontSymbolManager;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.ResponseWriterPool;

/**
 * Convenient abstract base class for document controllers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDocumentController extends AbstractController implements DocumentController {

	@Configurable
	private ResponseWriterPool responseWriterPool;

    @Configurable
    private FontSymbolManager fontSymbolManager;

	public AbstractDocumentController(Secured secured) {
		super(secured);
	}

	@Override
	public void process(ClientRequest request, ClientResponse response) throws UnifyException {
		response.setContentType(MimeType.TEXT_HTML.template());
		final DocPathParts docPathParts = request.getRequestPathParts().getControllerPathParts().getDocPathParts();
		ResponseWriter writer = responseWriterPool.getResponseWriter(request);
		try {
			writeDocument(writer, docPathParts.getDocPath(), docPathParts.getSection());
			writer.writeTo(response.getWriter());
		} finally {
			responseWriterPool.restore(writer);
		}
	}

	@Override
	public void ensureContextResources(ControllerPathParts controllerPathParts) throws UnifyException {

	}

	protected abstract void writeDocument(ResponseWriter writer, String docPath, String section) throws UnifyException;

    protected final boolean isWithFontSymbolManager() {
        return fontSymbolManager != null;
    }
    
    protected List<String> getFontResources() throws UnifyException {
        return fontSymbolManager.getFontResources();
    }

	protected void writeEmbeddedStyle(ResponseWriter writer, String fontFamily) throws UnifyException {
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
			fsb.append(".g_fsm {font-family: ").append(fontFamily);
			for (String fontResource : getFontResources()) {
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
		writer.write(className).write(" {vertical-align:middle;display: inline-block !important;} ").write(className)
				.write(":before {content: \"\";vertical-align:middle;display: inline-block;width: 100%;height: 100%;background: url(");
		writer.writeFileImageContextURL(imgSrc);
		writer.write(")no-repeat center/100% 100%; }");
	}

	protected void writeStyleSheet(ResponseWriter writer, String styleSheet) throws UnifyException {
		writer.write("<link href=\"");
		writer.writeContextResourceURL("/resource/file", MimeType.TEXT_CSS.template(), styleSheet);
		writer.write("\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\">");
	}
}
