/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.web.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.AbstractPageControllerResponse;
import com.tcdng.unify.web.PageController;
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Used for generating a load entire document response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("loaddocumentresponse")
public class LoadDocumentResponse extends AbstractPageControllerResponse {

	@Override
	public void generate(ResponseWriter writer, PageController pageController) throws UnifyException {
		logDebug("Preparing load document response: controller = [{0}]", pageController.getName());
		Document document = (Document) pageController.getPage();
		writer.writeStructureAndContent(document);
		writer.writeBehaviour(document);
	}
}
