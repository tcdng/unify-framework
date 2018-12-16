/*
 * Copyright 2018 The Code Department
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

    private String id;

    private String uriBase;

    private String contextPath;

    private String remoteHost;

    private String remoteAddress;

    private String remoteUser;

    private String remoteViewer;

    private UserPlatform platform;

    private Date lastAccessTime;

    public SessionContext(String id, String uriBase, String contextPath, String remoteHost, String remoteAddress,
            String remoteUser, String remoteViewer, UserPlatform platform) {
        this.id = id;
        this.locale = Locale.getDefault();
        this.uriBase = uriBase;
        this.contextPath = contextPath;
        this.remoteHost = remoteHost;
        this.remoteAddress = remoteAddress;
        this.remoteUser = remoteUser;
        this.platform = platform;
        this.lastAccessTime = new Date();
        this.remoteViewer = remoteViewer;
        this.getAttributes().put(TRUE_ATTRIBUTE, Boolean.TRUE);
        this.getAttributes().put(FALSE_ATTRIBUTE, Boolean.FALSE);
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

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public String getRemoteViewer() {
        return remoteViewer;
    }

    public UserPlatform getPlatform() {
        return platform;
    }

    public boolean isRemoteViewer() {
        return this.remoteViewer != null;
    }

    public boolean isUserLoggedIn() {
        return this.userToken != null || this.isRemoteViewer();
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void accessed() {
        this.lastAccessTime = new Date();
    }
}
