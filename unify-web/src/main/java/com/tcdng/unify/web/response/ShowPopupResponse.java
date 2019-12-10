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
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Used for generating a show popup response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("showpopupresponse")
@UplAttributes({ @UplAttribute(name = "popup", type = String.class),
        @UplAttribute(name = "systemInfo", type = boolean.class, defaultVal = "false") })
public class ShowPopupResponse extends AbstractJsonPageControllerResponse {

    public ShowPopupResponse() {
        super("showPopupHdl");
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing show popup response: path ID = [{0}]", page.getPathId());
        appendPopupPanelsJSON(writer, page);
        writer.write(",");
        appendRefreshAttributesJson(writer, false);
    }

    protected void appendPopupPanelsJSON(ResponseWriter writer, Page page) throws UnifyException {
        if (getUplAttribute(boolean.class, "systemInfo")) {
            writer.write(",\"showSysInfoPopup\":");
        } else {
            writer.write(",\"showPopup\":");
        }
        Panel panel = null;
        String popupShortName = getUplAttribute(String.class, "popup");
        if (popupShortName != null) {
            panel = page.getPanelByShortName(popupShortName);
        } else {
            panel = page.getPanelByLongName(getRequestContextUtil().getRequestPopupName());
        }
        writer.writeJsonPanel(panel, false);
    }
}
