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

import com.tcdng.unify.core.constant.TriState;
import com.tcdng.unify.core.data.Context;

/**
 * Application context class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ApplicationContext extends Context {

    private static final PrivilegeSettings ALL_PRIVILEGES =
            new PrivilegeSettings(true, true, false, TriState.CONFORMING);

    private static final PrivilegeSettings NO_PRIVILEGES = new PrivilegeSettings(false, false, true, TriState.TRUE);

    private UnifyContainer container;

    private Locale applicationLocale;

    private String lineSeparator;

    private Map<String, RoleAttributes> roleAttributes;

    public ApplicationContext(UnifyContainer container, Locale applicationLocale, String lineSeparator) {
        this.container = container;
        this.applicationLocale = applicationLocale;
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
     * Tests if supplied role has privilege code attribute.
     * 
     * @param roleCode
     *            the role code
     * @param privilege
     *            the privilege to test
     * @return true if role has privilege
     */
    public PrivilegeSettings getPrivilegeSettings(String roleCode, String privilege) {
        if (roleCode != null && privilege != null && !privilege.isEmpty()) {
            RoleAttributes roleAttributes = this.roleAttributes.get(roleCode);
            if (roleAttributes != null) {
                if (roleAttributes.isAllAccessPrivilege(privilege)) {
                    return ALL_PRIVILEGES;
                }

                PrivilegeSettings pSettings = roleAttributes.getControlledAccessPrivilegeSettings(privilege);
                if (pSettings != null) {
                    return pSettings;
                }
            }
            return NO_PRIVILEGES;
        }
        return ALL_PRIVILEGES;
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

    /**
     * Returns the application banner ASCII text
     * 
     * @return if an error occurs
     */
    public List<String> getApplicationBanner() throws UnifyException {
        return container.getApplicationBanner();
    }

    /**
     * Gets the application locale.
     * 
     * @return the application locale
     */
    public Locale getApplicationLocale() {
        return applicationLocale;
    }

    /**
     * Sets the application locale.
     * 
     * @param applicationLocale
     *            the locale to set
     */
    public void setApplicationLocale(Locale applicationLocale) {
        this.applicationLocale = applicationLocale;
    }

    /**
     * Gets the application line separator.
     * 
     * @return the application line separator
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    public UnifyContainer getContainer() {
        return container;
    }
}
