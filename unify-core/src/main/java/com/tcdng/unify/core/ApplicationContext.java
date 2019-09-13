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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.tcdng.unify.core.constant.TriState;
import com.tcdng.unify.core.data.Context;

/**
 * Application context class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ApplicationContext extends Context {

    private static final ViewDirective ALLOW_VIEW_DIRECTIVE =
            new ViewDirective(true, true, false, TriState.CONFORMING);

    private static final ViewDirective DISALLOW_VIEW_DIRECTIVE = new ViewDirective(false, false, true, TriState.TRUE);

    private UnifyContainer container;

    private Locale applicationLocale;
    
    private TimeZone timeZone;

    private String lineSeparator;

    private Map<String, RoleAttributes> roleAttributes;

    public ApplicationContext(UnifyContainer container, Locale applicationLocale, TimeZone timeZone, String lineSeparator) {
        this.container = container;
        this.applicationLocale = applicationLocale;
        this.timeZone = timeZone;
        this.lineSeparator = lineSeparator;
        this.roleAttributes = new HashMap<String, RoleAttributes>();
    }

    /**
     * Tests if context has role attributes loaded for the supplied role code.
     * 
     * @param roleCode
     *            the role code
     * @return true if context has attributes for role
     */
    public boolean isRoleAttributes(String roleCode) {
        return this.roleAttributes.containsKey(roleCode);
    }

    /**
     * Sets attributes for specified role.
     * 
     * @param roleCode
     *            the role code
     * @param roleAttributes
     *            the attributes to load.
     */
    public void setRoleAttributes(String roleCode, RoleAttributes roleAttributes) {
        this.roleAttributes.put(roleCode, roleAttributes);
    }

    /**
     * Gets supplied role privilege code view directive.
     * 
     * @param roleCode
     *            the role code
     * @param privilege
     *            the privilege to test
     * @return the role's view directive for supplied privilege
     */
    public ViewDirective getRoleViewDirective(String roleCode, String privilege) {
        if (roleCode != null && privilege != null && !privilege.isEmpty()) {
            RoleAttributes roleAttributes = this.roleAttributes.get(roleCode);
            if (roleAttributes != null) {
                if (roleAttributes.isViewAllPrivilege(privilege)) {
                    return ALLOW_VIEW_DIRECTIVE;
                }

                ViewDirective directive = roleAttributes.getViewDirective(privilege);
                if (directive != null) {
                    return directive;
                }
            }
            return DISALLOW_VIEW_DIRECTIVE;
        }
        return ALLOW_VIEW_DIRECTIVE;
    }

    public Set<String> getPrivilegeCodes(String roleCode, String privilegeCategoryCode) {
        if (roleCode != null && privilegeCategoryCode != null && !privilegeCategoryCode.isEmpty()) {
            RoleAttributes roleAttributes = this.roleAttributes.get(roleCode);
            if (roleAttributes != null) {
                Set<String> privilegeCodes = roleAttributes.getPrivilegeCodes(privilegeCategoryCode);
                if (privilegeCodes != null) {
                    return privilegeCodes;
                }
            }
        }
        return Collections.emptySet();
    }

    public Set<String> getStepCodes(String roleCode) {
        if (roleCode != null) {
            return this.roleAttributes.get(roleCode).getStepCodes();
        }

        return Collections.emptySet();
    }

    public List<String> getApplicationBanner() throws UnifyException {
        return container.getApplicationBanner();
    }

    public Locale getApplicationLocale() {
        return applicationLocale;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public UnifyContainer getContainer() {
        return container;
    }
}
