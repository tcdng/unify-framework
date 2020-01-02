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
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Used for generating a refresh panel response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("refreshpanelresponse")
@UplAttributes({ @UplAttribute(name = "panels", type = String[].class) })
public class RefreshPanelResponse extends AbstractJsonPageControllerResponse {

    public RefreshPanelResponse() {
        super("refreshPanelHdl");
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        boolean useLongNames = false;
        String[] refreshList = getPanels();
        if (refreshList == null || refreshList.length == 0) {
            refreshList = getRequestContextUtil().getResponseRefreshPanels();
            useLongNames = true;
        }
        appendRefreshPanelsJson(writer, page, refreshList, useLongNames);
        writer.write(",");
        appendRefreshAttributesJson(writer, false);
    }

    protected String[] getPanels() throws UnifyException {
        return getUplAttribute(String[].class, "panels");
    }

    private void appendRefreshPanelsJson(ResponseWriter writer, Page page, String[] panelIds,
            boolean useLongNames) throws UnifyException {
        logDebug("Preparing refresh panel response: path ID = [{0}], useLongNames = [{1}]", page.getPathId(),
                useLongNames);
        writer.write(",\"refreshPanels\":[");
        if (panelIds != null) {
            boolean appendSym = false;
            for (int i = 0; i < panelIds.length; i++) {
                if (appendSym) {
                    writer.write(',');
                } else {
                    appendSym = true;
                }

                if (useLongNames) {
                    writer.writeJsonPanel(page.getPanelByLongName(panelIds[i]), true);
                } else {
                    writer.writeJsonPanel(page.getPanelByShortName(panelIds[i]), true);
                }
            }
        }
        writer.write("]");
    }
}
