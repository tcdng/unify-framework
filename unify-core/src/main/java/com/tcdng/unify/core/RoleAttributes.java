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
package com.tcdng.unify.core;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Encapsulates role attributes.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class RoleAttributes {

    private String code;

    private String description;

    private Map<String, ViewDirective> dynamicViewDirectives;

    private Map<String, Set<String>> nonViewDirectivePrivilegeCodes;

    private Set<String> staticViewDirectivePrivilegeCodes;

    private Set<String> stepCodes;

    public RoleAttributes(String code, String description, Map<String, ViewDirective> dynamicViewDirectives,
            Set<String> staticViewDirectivePrivilegeCodes, Map<String, Set<String>> nonViewDirectivePrivilegeCodes,
            Set<String> stepCodes) {
        this.code = code;
        this.description = description;
        this.dynamicViewDirectives = DataUtils.unmodifiableMap(dynamicViewDirectives);
        this.staticViewDirectivePrivilegeCodes = DataUtils.unmodifiableSet(staticViewDirectivePrivilegeCodes);
        this.nonViewDirectivePrivilegeCodes = nonViewDirectivePrivilegeCodes;
        if (this.nonViewDirectivePrivilegeCodes != null) {
            for (Map.Entry<String, Set<String>> entry : this.nonViewDirectivePrivilegeCodes.entrySet()) {
                this.nonViewDirectivePrivilegeCodes.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
            }
        }
        this.nonViewDirectivePrivilegeCodes = DataUtils.unmodifiableMap(this.nonViewDirectivePrivilegeCodes);
        this.stepCodes = DataUtils.unmodifiableSet(stepCodes);
    }

    public RoleAttributes(String code, String description, Set<String> staticViewDirectivePrivilegeCodes) {
        this.code = code;
        this.description = description;
        this.staticViewDirectivePrivilegeCodes = DataUtils.unmodifiableSet(staticViewDirectivePrivilegeCodes);
        this.dynamicViewDirectives = Collections.emptyMap();
        this.nonViewDirectivePrivilegeCodes = Collections.emptyMap();
        this.stepCodes = Collections.emptySet();
    }

    public RoleAttributes() {
        this.dynamicViewDirectives = Collections.emptyMap();
        this.staticViewDirectivePrivilegeCodes = Collections.emptySet();
        this.nonViewDirectivePrivilegeCodes = Collections.emptyMap();
        this.stepCodes = Collections.emptySet();
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public ViewDirective getDynamicViewDirective(String privilegeCode) {
        return dynamicViewDirectives.get(privilegeCode);
    }

    public boolean isStaticViewDirectivePrivilege(String privilegeCode) {
        return staticViewDirectivePrivilegeCodes.contains(privilegeCode);
    }

    public Set<String> getPrivilegeCodes(String privilegeCategoryCode) {
        return nonViewDirectivePrivilegeCodes.get(privilegeCategoryCode);
    }

    public Set<String> getStepCodes() {
        return stepCodes;
    }
}
