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

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Represents an application user token.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class UserToken {

	private String userLoginId;

	private String userName;

	private String userEmail;

	private String roleCode;

	private String branchCode;

	private String tenantCode;

	private String tenantName;

	private String colorScheme;

	private String ipAddress;

	private String departmentCode;

	private String themePath;

	private String zoneCode;

	private String organizationCode;

	private Long userId;

	private Long tenantId;

	private Long organizationId;

	private boolean globalAccess;

	private boolean reservedUser;

	private boolean allowMultipleLogin;

	private boolean remote;

	private boolean authorized;

	public UserToken(Long tenantId) {
		this.tenantId = tenantId;
	}

	private UserToken(String userLoginId, String userName, String userEmail, String ipAddress, String branchCode,
			String zoneCode, String tenantCode, String tenantName, String colorScheme, Long userId, Long tenantId,
			boolean globalAccess, boolean reservedUser, boolean allowMultipleLogin, boolean remote) {
		this.userLoginId = userLoginId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.zoneCode = zoneCode;
		this.branchCode = branchCode;
		this.tenantCode = tenantCode;
		this.tenantName = tenantName;
		this.colorScheme = colorScheme;
		this.ipAddress = ipAddress;
		this.userId = userId;
		this.tenantId = tenantId;
		this.globalAccess = globalAccess;
		this.reservedUser = reservedUser;
		this.allowMultipleLogin = allowMultipleLogin;
		this.remote = remote;
		this.authorized = true;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	public String getUserLoginId() {
		return userLoginId;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public Long getUserId() {
		return userId;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public String getTenantName() {
		return tenantName;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public String getColorScheme() {
		return colorScheme;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public boolean isWithOrganization() {
		return organizationId != null && organizationId > 0L;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getThemePath() {
		return themePath;
	}

	public void setThemePath(String themePath) {
		this.themePath = themePath;
	}

	public boolean isGlobalAccess() {
		return globalAccess;
	}

	public boolean isReservedUser() {
		return reservedUser;
	}

	public boolean isAllowMultipleLogin() {
		return allowMultipleLogin;
	}

	public boolean isRemote() {
		return remote;
	}

	public boolean isWithUserLoginId() {
		return userLoginId != null;
	}

	public boolean isWithTenantId() {
		return tenantId != null;
	}

	public boolean isPrimaryTenant() {
		return Entity.PRIMARY_TENANT_ID.equals(tenantId);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private String userLoginId;

		private String userName;

		private String userEmail;

		private String branchCode;

		private String tenantCode;

		private String tenantName;

		private String colorScheme;

		private String ipAddress;

		private String zoneCode;

		private Long userId;

		private Long tenantId;

		private boolean globalAccess;

		private boolean reservedUser;

		private boolean allowMultipleLogin;

		private boolean remote;

		private Builder() {

		}

		public Builder userLoginId(String userLoginId) {
			this.userLoginId = userLoginId;
			return this;
		}

		public Builder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder userEmail(String userEmail) {
			this.userEmail = userEmail;
			return this;
		}

		public Builder branchCode(String branchCode) {
			this.branchCode = branchCode;
			return this;
		}

		public Builder tenantCode(String tenantCode) {
			this.tenantCode = tenantCode;
			return this;
		}

		public Builder tenantName(String tenantName) {
			this.tenantName = tenantName;
			return this;
		}

		public Builder colorScheme(String colorScheme) {
			this.colorScheme = colorScheme;
			return this;
		}

		public Builder ipAddress(String ipAddress) {
			this.ipAddress = ipAddress;
			return this;
		}

		public Builder zoneCode(String zoneCode) {
			this.zoneCode = zoneCode;
			return this;
		}

		public Builder globalAccess(boolean globalAccess) {
			this.globalAccess = globalAccess;
			return this;
		}

		public Builder reservedUser(boolean reservedUser) {
			this.reservedUser = reservedUser;
			return this;
		}

		public Builder allowMultipleLogin(boolean allowMultipleLogin) {
			this.allowMultipleLogin = allowMultipleLogin;
			return this;
		}

		public Builder remote(boolean remote) {
			this.remote = remote;
			return this;
		}

		public Builder tenantId(Long tenantId) {
			this.tenantId = tenantId;
			return this;
		}

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public UserToken build() {
			if (StringUtils.isBlank(userLoginId)) {
				throw new RuntimeException("Login ID is required!");
			}

			return new UserToken(userLoginId, userName, userEmail, ipAddress, branchCode, zoneCode, tenantCode,
					tenantName, colorScheme, userId, tenantId, globalAccess, reservedUser, allowMultipleLogin, remote);
		}
	}

}
