/*
 * Copyright 2018-2023 The Code Department.
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
import com.tcdng.unify.web.ui.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating a hide popup response.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("hidepopupresponse")
@UplAttributes({ @UplAttribute(name = "systemInfo", type = boolean.class, defaultVal = "false") })
public class HidePopupResponse extends AbstractJsonPageControllerResponse {

    public HidePopupResponse() {
        super("hidePopupHdl", false);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing hide popup response: path ID = [{0}]", page.getPathId());
        if (getUplAttribute(boolean.class, "systemInfo")) {
            writer.write(",\"hideSysInfoPopup\":true");
        } else {
            writer.write(",\"hidePopup\":true");
        }
    }
}
