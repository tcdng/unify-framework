/*
 * Copyright 2018-2019 The Code Department.
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
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.panel.ContentPanel;

/**
 * Used for generating a close page content response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("unloadcontentresponse")
public class UnloadContentResponse extends AbstractJsonPageControllerResponse {

    public UnloadContentResponse() {
        super("loadContentHdl");// Same handler as load content
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing unload content response: path ID = [{0}]", page.getPathId());
        if (getRequestContextUtil().isRemoteViewer()) {
            writer.write(",\"closeRemoteTab\":true");
        } else {
            Document document = getRequestContextUtil().getRequestDocument();
            appendRefreshPageJSON(writer, document, page);
            writer.write(",");
            appendRefreshAttributesJson(writer, true);
            ContentPanel contentPanel = (ContentPanel) document.getContentPanel();
            writer.write(",\"busyIndicator\":\"").write(contentPanel.getBusyIndicatorId()).write("\"");
            writer.write(",\"scrollToTop\":true");
        }
    }

    private void appendRefreshPageJSON(ResponseWriter writer, Document document, Page page)
            throws UnifyException {
        writer.write(",\"refreshPanels\":[");
        ContentPanel contentPanel = (ContentPanel) document.getContentPanel();
        contentPanel.removeContent(page);
        writer.writeJsonPanel(contentPanel, true);
        writer.write("]");
    }
}
