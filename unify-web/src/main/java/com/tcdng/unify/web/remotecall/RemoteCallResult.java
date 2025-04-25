/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.web.remotecall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Remote call result.
 * 
 * @author Lateef
 */
@JsonInclude(Include.NON_NULL)
public abstract class RemoteCallResult {

	@JacksonXmlProperty(isAttribute = true)
    private String methodCode;

	@JacksonXmlProperty(isAttribute = true)
    private String errorCode;

	@JacksonXmlProperty
    private String errorMsg;

    public RemoteCallResult(String methodCode, String errorCode, String errorMsg) {
        this.methodCode = methodCode;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public RemoteCallResult() {

    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @JsonIgnore
    public boolean isError() {
        return errorCode != null || errorMsg != null;
    }
}
