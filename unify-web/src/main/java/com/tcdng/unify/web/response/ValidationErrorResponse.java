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
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.data.ValidationInfo;

/**
 * Used for generating form validation error response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("validationerrorresponse")
public class ValidationErrorResponse extends AbstractJsonPageControllerResponse {

    public ValidationErrorResponse() {
        super("validationErrorHdl");
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing validation error response: path ID = [{0}]", page.getPathId());
        StringBuilder htmlSb = new StringBuilder();
        writer.write(",\"validationInfo\":[");
        boolean appendSymbol = false;
        for (ValidationInfo validationInfo : getRequestContextUtil().getRequestValidationInfoList()) {
            if (appendSymbol) {
                writer.write(',');
            } else {
                appendSymbol = true;
            }

            writer.write("{\"pId\":\"").write(validationInfo.getId()).write("\", \"borderStyle\":\"")
                    .write(validationInfo.getBorderStyle()).write("\"");
            writer.write(",\"pBrdId\":\"").write(validationInfo.getBorderId()).write("\"");
            writer.write(",\"pNotfId\":\"").write(validationInfo.getNotificationId()).write("\"");
            if (!validationInfo.isPass()) {
                String message = validationInfo.getMessage();
                logDebug("Preparing validation error response: error = [{0}]", message);
                if (htmlSb.length() == 0) {
                    writer.write(",\"setFocus\":true");
                    htmlSb.append(true);
                }
                writer.write(",\"msg\":\"");
                writer.write(message);
                writer.write("\"");
            }
            writer.write("}");
        }
        writer.write("]");
    }
}
