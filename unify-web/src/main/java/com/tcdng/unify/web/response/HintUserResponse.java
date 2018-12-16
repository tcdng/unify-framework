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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.PageController;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.data.Hint;

/**
 * Used for generating a hint user response. This response is implicitly added
 * to every result, so there's no need to explicitly add when defining an action
 * result with {@link ResultMapping}.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("hintuserresponse")
public class HintUserResponse extends AbstractJsonPageControllerResponse {

    public HintUserResponse() {
        super("hintUserHdl");
    }

    @Override
    protected void doGenerate(ResponseWriter writer, PageController pageController) throws UnifyException {
        logDebug("Preparing hint user response: controller = [{0}]", pageController.getName());
        List<Hint> hintList = getRequestContextUtil().getUserHints();
        if (hintList != null && !hintList.isEmpty()) {
            writer.write(",\"hintUserHtml\":");
            StringBuilder hsb = new StringBuilder();
            hsb.append("<div class=\"ui-user-hint\">");
            for (Hint hint : hintList) {
                hsb.append("<span class=\"");
                switch (hint.getMode()) {
                case ERROR:
                    hsb.append("ui-user-hint-error");
                    break;
                case INFO:
                    hsb.append("ui-user-hint-info");
                    break;
                case WARNING:
                default:
                    hsb.append("ui-user-hint-warning");
                    break;
                }
                hsb.append("\">");
                hsb.append(hint.getMessage());
                hsb.append("</span>");
            }
            hsb.append("</div>");
            writer.writeJsonQuote(hsb.toString());
        }
    }
}
