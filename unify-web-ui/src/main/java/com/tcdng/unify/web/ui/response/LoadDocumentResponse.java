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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.ui.AbstractPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating a load entire document response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("loaddocumentresponse")
public class LoadDocumentResponse extends AbstractPageControllerResponse {

    @Override
    public void generate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing load document response: path ID = [{0}]", page.getPathId());
        Document document = (Document) page;
        writer.writeStructureAndContent(document);
        writer.writeBehavior(document);
    }
}
