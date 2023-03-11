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
package com.tcdng.unify.web.remotecall;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Remote call parameter.
 * 
 * @author Lateef
 * @since 1.0
 */
public abstract class RemoteCallParams {

    private String methodCode;

    private String clientAppCode;

    public RemoteCallParams(String methodCode, String clientAppCode) {
        this.methodCode = methodCode;
        this.clientAppCode = clientAppCode;
    }

    public RemoteCallParams(String methodCode) {
        this.methodCode = methodCode;
    }

    public RemoteCallParams() {
        
    }

    public String getMethodCode() {
        return methodCode;
    }

    @XmlAttribute(required = true)
    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getClientAppCode() {
        return clientAppCode;
    }

    @XmlAttribute(required = true)
    public void setClientAppCode(String clientAppCode) {
        this.clientAppCode = clientAppCode;
    }
}
