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

/**
 * Represents an application user token.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UserToken {

    private String roleCode;

    private String themePath;

    private String userLoginId;

    private String userName;

    private Object userId;

    private Object branchCode;

    private String ipAddress;

    private boolean globalAccess;

    private boolean reservedUser;

    private boolean allowMultipleLogin;

    private boolean remote;

    public UserToken(String userLoginId, String userName, String ipAddress, Object userId, Object branchCode,
            boolean globalAccess, boolean reservedUser, boolean allowMultipleLogin, boolean remote) {
        this.userLoginId = userLoginId;
        this.userName = userName;
        this.userId = userId;
        this.branchCode = branchCode;
        this.ipAddress = ipAddress;
        this.globalAccess = globalAccess;
        this.reservedUser = reservedUser;
        this.allowMultipleLogin = allowMultipleLogin;
        this.remote = remote;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getThemePath() {
        return themePath;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public void setThemePath(String themePath) {
        this.themePath = themePath;
    }

    public String getUserLoginId() {
        return userLoginId;
    }

    public String getUserName() {
        return userName;
    }

    public Object getUserId() {
        return userId;
    }

    public Object getBranchCode() {
        return branchCode;
    }

    public String getIpAddress() {
        return ipAddress;
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
}
