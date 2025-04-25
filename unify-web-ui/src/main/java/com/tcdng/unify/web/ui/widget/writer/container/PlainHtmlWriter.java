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
package com.tcdng.unify.web.ui.widget.writer.container;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Writes;
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
 * @since 4.1
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

}
