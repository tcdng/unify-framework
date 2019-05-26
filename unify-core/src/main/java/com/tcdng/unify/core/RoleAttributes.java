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

    private Map<String, PrivilegeSettings> controlledAccessPrivilegeSettings;

    private Map<String, Set<String>> privilegeCodesByCategory;

    private Set<String> allAccessPrivileges;

    private Set<String> stepCodes;

    public RoleAttributes(String code, String description,
            Map<String, PrivilegeSettings> controlledAccessPrivilegeSettings, Set<String> allAccessPrivileges,
            Map<String, Set<String>> privilegeCodesByCategory, Set<String> stepCodes) {
        this.code = code;
        this.description = description;
        this.controlledAccessPrivilegeSettings = DataUtils.unmodifiableMap(controlledAccessPrivilegeSettings);
        this.allAccessPrivileges = DataUtils.unmodifiableSet(allAccessPrivileges);
        this.privilegeCodesByCategory = privilegeCodesByCategory;
        if (this.privilegeCodesByCategory != null) {
            for (Map.Entry<String, Set<String>> entry : this.privilegeCodesByCategory.entrySet()) {
                this.privilegeCodesByCategory.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
            }
        }
        this.privilegeCodesByCategory = DataUtils.unmodifiableMap(this.privilegeCodesByCategory);
        this.stepCodes = DataUtils.unmodifiableSet(stepCodes);
    }
    
    public RoleAttributes(String code, String description, Set<String> allAccessPrivileges) {
        this.code = code;
        this.description = description;
        this.allAccessPrivileges = DataUtils.unmodifiableSet(allAccessPrivileges);
        this.controlledAccessPrivilegeSettings = Collections.emptyMap();
        this.privilegeCodesByCategory = Collections.emptyMap();
        this.stepCodes = Collections.emptySet();
    }


    public RoleAttributes() {
        this.controlledAccessPrivilegeSettings = Collections.emptyMap();
        this.allAccessPrivileges = Collections.emptySet();
        this.privilegeCodesByCategory = Collections.emptyMap();
        this.stepCodes = Collections.emptySet();
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public PrivilegeSettings getControlledAccessPrivilegeSettings(String privilegeCode) {
        return controlledAccessPrivilegeSettings.get(privilegeCode);
    }

    public boolean isAllAccessPrivilege(String privilegeCode) {
        return allAccessPrivileges.contains(privilegeCode);
    }

    public Set<String> getPrivilegeCodes(String privilegeCategoryCode) {
        return privilegeCodesByCategory.get(privilegeCategoryCode);
    }

    public Set<String> getStepCodes() {
        return stepCodes;
    }
}
