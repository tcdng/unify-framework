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
package com.tcdng.unify.web.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.ContentPanel;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Used for generating a load/refresh entire page content response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("loadcontentresponse")
public class LoadContentResponse extends AbstractJsonPageControllerResponse {

    public LoadContentResponse() {
        super("loadContentHdl");
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing load content response: path ID = [{0}]", page.getPathId());
        ContentPanel contentPanel = getRequestContextUtil().getRequestDocument().getContentPanel();
        appendRefreshPageJSON(writer, contentPanel, page);
        writer.write(",");
        appendRefreshAttributesJson(writer, true);
        writer.write(",\"busyIndicator\":\"").write(contentPanel.getBusyIndicatorId()).write("\"");
        writer.write(",\"scrollToTop\":true");
    }

    private void appendRefreshPageJSON(ResponseWriter writer, ContentPanel contentPanel, Page page)
            throws UnifyException {
        writer.write(",\"refreshPanels\":[");
        writer.writeJsonPanel(contentPanel, true);
        writer.write("]");
    }
}
