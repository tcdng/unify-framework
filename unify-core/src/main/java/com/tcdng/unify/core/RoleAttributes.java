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

    private Map<String, ViewDirective> viewDirectives;

    private Map<String, Set<String>> privilegeCodesByCategory;

    private Set<String> viewAllPrivilegeCodes;

    private Set<String> stepCodes;

    public RoleAttributes(String code, String description,
            Map<String, ViewDirective> viewDirectives, Set<String> viewAllPrivilegeCodes,
            Map<String, Set<String>> privilegeCodesByCategory, Set<String> stepCodes) {
        this.code = code;
        this.description = description;
        this.viewDirectives = DataUtils.unmodifiableMap(viewDirectives);
        this.viewAllPrivilegeCodes = DataUtils.unmodifiableSet(viewAllPrivilegeCodes);
        this.privilegeCodesByCategory = privilegeCodesByCategory;
        if (this.privilegeCodesByCategory != null) {
            for (Map.Entry<String, Set<String>> entry : this.privilegeCodesByCategory.entrySet()) {
                this.privilegeCodesByCategory.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
            }
        }
        this.privilegeCodesByCategory = DataUtils.unmodifiableMap(this.privilegeCodesByCategory);
        this.stepCodes = DataUtils.unmodifiableSet(stepCodes);
    }
    
    public RoleAttributes(String code, String description, Set<String> viewAllPrivilegeCodes) {
        this.code = code;
        this.description = description;
        this.viewAllPrivilegeCodes = DataUtils.unmodifiableSet(viewAllPrivilegeCodes);
        this.viewDirectives = Collections.emptyMap();
        this.privilegeCodesByCategory = Collections.emptyMap();
        this.stepCodes = Collections.emptySet();
    }


    public RoleAttributes() {
        this.viewDirectives = Collections.emptyMap();
        this.viewAllPrivilegeCodes = Collections.emptySet();
        this.privilegeCodesByCategory = Collections.emptyMap();
        this.stepCodes = Collections.emptySet();
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public ViewDirective getViewDirective(String privilegeCode) {
        return viewDirectives.get(privilegeCode);
    }

    public boolean isViewAllPrivilege(String privilegeCode) {
        return viewAllPrivilegeCodes.contains(privilegeCode);
    }

    public Set<String> getPrivilegeCodes(String privilegeCategoryCode) {
        return privilegeCodesByCategory.get(privilegeCategoryCode);
    }

    public Set<String> getStepCodes() {
        return stepCodes;
    }
}
