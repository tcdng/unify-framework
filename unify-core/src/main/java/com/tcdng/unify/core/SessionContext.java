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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.tcdng.unify.core.constant.UserPlatform;
import com.tcdng.unify.core.data.Context;

/**
 * User session context.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SessionContext extends Context {

    public static final String TRUE_ATTRIBUTE = "trueAttribute";

    public static final String FALSE_ATTRIBUTE = "falseAttribute";

    private UserToken userToken;

    private Locale locale;

    private TimeZone timeZone;

    private String id;

    private String uriBase;

    private String contextPath;

    private String tenantPath;

    private String remoteHost;

    private String remoteAddress;

    private String remoteUser;

    private UserPlatform platform;

    private Date lastAccessTime;

    private boolean isNewLastAccessTime;
    
    private boolean useDaylightSavings;

    public SessionContext(String id, Locale locale, TimeZone timeZone, String uriBase, String contextPath,
            String tenantPath, String remoteHost, String remoteAddress, String remoteUser, UserPlatform platform) {
        this.id = id;
        this.locale = locale;
        this.timeZone = timeZone;
        this.uriBase = uriBase;
        this.contextPath = contextPath;
        this.tenantPath = tenantPath;
        this.remoteHost = remoteHost;
        this.remoteAddress = remoteAddress;
        this.remoteUser = remoteUser;
        this.platform = platform;
        this.isNewLastAccessTime = true;
        setAttribute(TRUE_ATTRIBUTE, Boolean.TRUE);
        setAttribute(FALSE_ATTRIBUTE, Boolean.FALSE);
    }

    public UserToken getUserToken() {
        return userToken;
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getId() {
        return id;
    }

    public String getUriBase() {
        return uriBase;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getTenantPath() {
        return tenantPath;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public UserPlatform getPlatform() {
        return platform;
    }

    public boolean isWithTenantPath() {
        return tenantPath != null;
    }

    public boolean isUserLoggedIn() {
        return userToken != null;
    }

    public boolean isNewLastAccessTime() {
        return isNewLastAccessTime;
    }

    public Date getLastAccessTime() {
        isNewLastAccessTime = false;
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        isNewLastAccessTime = true;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isUseDaylightSavings() {
        return useDaylightSavings;
    }

    public void setUseDaylightSavings(boolean useDaylightSavings) {
        this.useDaylightSavings = useDaylightSavings;
    }

    public long getTimeZoneOffset() {
        if (useDaylightSavings) {
            return timeZone.getRawOffset() + timeZone.getDSTSavings();
        }

        return timeZone.getRawOffset();
    }
}
