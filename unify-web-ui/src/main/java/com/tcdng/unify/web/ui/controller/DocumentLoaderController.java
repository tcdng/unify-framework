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
package com.tcdng.unify.web.ui.controller;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.RandomUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.UnifyWebSessionAttributeConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.AbstractDocumentController;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Document loader controller.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(WebApplicationComponents.APPLICATION_DOCUMENTLOADERCONTROLLER)
public class DocumentLoaderController extends AbstractDocumentController {

	public DocumentLoaderController() {
		super(Secured.FALSE);
	}

	@Override
	protected void writeDocument(ResponseWriter writer, String docPath, String section, String queryString) throws UnifyException {
		final String contextPath = getRequestAttribute(String.class,
				UnifyWebRequestAttributeConstants.LOADER_FORWARD_PATH);
		final String tempParam = RandomUtils.generateRandomLetters(8);
		setSessionAttribute(UnifyWebSessionAttributeConstants.TEMP_CLIENT_ID_PARAM, tempParam);
		
		writer.write("<html>\n<head>\n");
		writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"/>\n");
		writer.write("<title>Loading...</title>\n");
		writer.write("</head>\n<body></body>\n</html>\n");
		writer.write("<script>\n");
		writer.write("let cid = sessionStorage.getItem(\"page_cid\");\n");
		writer.write("if (cid === null) {\n");
		writer.write("let uxstore = localStorage.getItem(\"ux_store\");\n");
		writer.write("let _uxstore = uxstore !== null ? JSON.parse(uxstore): {cid:0};\n");
		writer.write("_uxstore.cid++;\n");
	    writer.write("cid = \"cid\" + _uxstore.cid.toString(16);\n");
	    writer.write("sessionStorage.setItem(\"page_cid\", cid);\n");
	    writer.write("localStorage.setItem(\"ux_store\", JSON.stringify(_uxstore));\n");
	    writer.write("}\n");
	    writer.write("let path=\"");
		writer.writeContextURL(contextPath);
		writer.write("?").write(tempParam).write("=");
		writer.write("\" + cid");
		if (!StringUtils.isBlank(queryString)) {
			writer.write(" + \"&").write(queryString).write("\"");
		}
		
		writer.write(";\n");	    
		writer.write("window.location.assign(path);\n");	
		writer.write("</script>\n");
	}

}
