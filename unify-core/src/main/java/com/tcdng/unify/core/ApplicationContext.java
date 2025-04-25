/*
 * Copyright (c) 2018-2025 The Code Department.
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

import com.tcdng.unify.core.data.AlternativePrivilege;
import com.tcdng.unify.core.data.Context;
import com.tcdng.unify.core.format.Formatter;

/**
 * Application context class.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ApplicationContext extends Context {

	private final UnifyContainer container;

	private final Locale applicationLocale;

	private final TimeZone timeZone;

	private final String lineSeparator;

	private final boolean ignoreViewDirective;

	private final Map<String, RoleAttributes> roleAttributes;

	private ApplicationAttributeProvider attributeProvider;

	private AlternativePrivilegeProvider altPrivilegeNameProvider;

	private RolePrivilegeManager rolePrivilegeManager;

	public ApplicationContext(UnifyContainer container, Locale applicationLocale, TimeZone timeZone,
			String lineSeparator, boolean ignoreViewDirective) {
		this.container = container;
		this.applicationLocale = applicationLocale;
		this.timeZone = timeZone;
		this.lineSeparator = lineSeparator;
		this.ignoreViewDirective = ignoreViewDirective;
		this.roleAttributes = new HashMap<String, RoleAttributes>();
	}

	/**
	 * Tests if context has role attributes loaded for the supplied role code.
	 * 
	 * @param roleCode the role code
	 * @return true if context has attributes for role
	 */
	public boolean isRoleAttributes(String roleCode) {
		return this.roleAttributes.containsKey(roleCode);
	}

	/**
	 * Sets attributes for specified role.
	 * 
	 * @param roleCode       the role code
	 * @param roleAttributes the attributes to load.
	 */
	public void setRoleAttributes(String roleCode, RoleAttributes roleAttributes) {
		this.roleAttributes.put(roleCode, roleAttributes);
	}

	/**
	 * Gets supplied role privilege code view directive.
	 * 
	 * @param roleCode  the role code
	 * @param privilege the privilege to test
	 * @return the role's view directive for supplied privilege
	 */
	public ViewDirective getRoleViewDirective(String roleCode, String privilege) {
		if (roleCode != null && privilege != null && !privilege.isEmpty()) {
			RoleAttributes roleAttributes = this.roleAttributes.get(roleCode);
			if (roleAttributes != null) {
				if (roleAttributes.isStaticViewDirectivePrivilege(privilege)) {
					return ViewDirective.ALLOW_VIEW_DIRECTIVE;
				}

				ViewDirective directive = roleAttributes.getDynamicViewDirective(privilege);
				if (directive != null) {
					return directive;
				}
			}
			return ViewDirective.DISALLOW_VIEW_DIRECTIVE;
		}

		return ViewDirective.ALLOW_VIEW_DIRECTIVE;
	}

	/**
	 * Creates a cached formatter component.
	 * 
	 * @param formatterUpl the formatter UPL
	 * @return the formatter object
	 * @throws UnifyException if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public Formatter<Object> getFormatter(String formatterUpl) throws UnifyException {
		return (Formatter<Object>) container.getUplComponent(applicationLocale, formatterUpl, true);
	}

	/**
	 * Creates an un-cached formatter component.
	 * 
	 * @param formatterUpl the formatter UPL
	 * @return the formatter object
	 * @throws UnifyException if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public Formatter<Object> createFormatter(String formatterUpl) throws UnifyException {
		return (Formatter<Object>) container.getUplComponent(applicationLocale, formatterUpl, false);
	}

	@Override
	public Object getAttribute(String name) throws UnifyException {
		Object val = super.getAttribute(name);
		if (val == null && attributeProvider != null) {
			return attributeProvider.getAttribute(name);
		}

		return val;
	}

	public void setAttributeProvider(ApplicationAttributeProvider attributeProvider) {
		this.attributeProvider = attributeProvider;
	}

	public void setAltPrivilegeNameProvider(AlternativePrivilegeProvider altPrivilegeNameProvider) {
		this.altPrivilegeNameProvider = altPrivilegeNameProvider;
	}

	public void setRolePrivilegeManager(RolePrivilegeManager rolePrivilegeManager) {
		this.rolePrivilegeManager = rolePrivilegeManager;
	}

	public AlternativePrivilege getAlternativePrivilege(String privilege) throws UnifyException {
		return altPrivilegeNameProvider != null ? altPrivilegeNameProvider.getAlternativePrivilege(privilege) : null;
	}

	public boolean isRoleWithPrivilege(String roleCode, String privilege) throws UnifyException {
		return rolePrivilegeManager != null ? rolePrivilegeManager.isRoleWithPrivilege(roleCode, privilege) : false;
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

	public boolean isIgnoreViewDirective() {
		return ignoreViewDirective;
	}

	public UnifyContainer getContainer() {
		return container;
	}
}
