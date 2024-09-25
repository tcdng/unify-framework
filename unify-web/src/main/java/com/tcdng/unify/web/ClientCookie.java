/*
 * Copyright 2018-2024 The Code Department.
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

package com.tcdng.unify.web;

import com.tcdng.unify.core.util.StringUtils;

/**
 * Client cookie.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ClientCookie {

    private String domain;

    private String path;

    private String name;
    
    private String val;
    
    private int maxAge;

    public ClientCookie(String domain, String path, String name, String val, int maxAge) {
        this.domain = domain;
        this.path = path;
        this.name = name;
        this.val = val;
        this.maxAge = maxAge;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getVal() {
        return val;
    }

    public int getMaxAge() {
        return maxAge;
    }
    
    public boolean isValuePresent() {
    	return !StringUtils.isBlank(val);
    }
}
