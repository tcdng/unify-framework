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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.AbstractPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating a load entire document response.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("loaddocumentresponse")
@UplAttributes({
	@UplAttribute(name = "path", type = String.class)})
public class LoadDocumentResponse extends AbstractPageControllerResponse {

    @Override
    public void generate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing load document response: path ID = [{0}]", page.getPathId());
        Document document = (Document) page;
        writer.writeStructureAndContent(document);
        writer.writeBehavior(document);
    }

    @Override
	public boolean isDocumentPathResponse() throws UnifyException {
		return !StringUtils.isBlank(getDocumentPath());
	}

	@Override
	public String getDocumentPath() throws UnifyException {
		return getUplAttribute(String.class, "path");
	}
}
