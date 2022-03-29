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
package com.tcdng.unify.web.discovery.gem.data;

import javax.xml.bind.annotation.XmlElement;

/**
 * API discovery remote call information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class APIDiscoveryRemoteCallInfo {

    private String code;

    private String description;

    private String url;

    private boolean restricted;

    // TODO Add parameter definition

    // TODO Add result definition

    public APIDiscoveryRemoteCallInfo(String code, String description, String url, boolean restricted) {
        this.code = code;
        this.description = description;
        this.url = url;
        this.restricted = restricted;
    }

    public APIDiscoveryRemoteCallInfo() {

    }

    public String getCode() {
        return code;
    }

    @XmlElement(required = true)
    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement(required = true)
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    @XmlElement(required = true)
    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRestricted() {
        return restricted;
    }

    @XmlElement(required = true)
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
}
