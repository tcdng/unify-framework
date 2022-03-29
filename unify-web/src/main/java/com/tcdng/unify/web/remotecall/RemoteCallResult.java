/*
 * Copyright 2018-2022 The Code Department.
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Remote call result.
 * 
 * @author Lateef
 */
public abstract class RemoteCallResult {

    private String methodCode;

    private String errorCode;

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

    @XmlAttribute
    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @XmlAttribute
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @XmlElement
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isError() {
        return errorCode != null || errorMsg != null;
    }
}
