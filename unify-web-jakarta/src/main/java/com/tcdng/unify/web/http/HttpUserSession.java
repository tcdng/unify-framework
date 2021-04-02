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
package com.tcdng.unify.web.http;

import java.util.Locale;
import java.util.TimeZone;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

import com.tcdng.unify.core.SessionContext;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserSession;
import com.tcdng.unify.core.system.UserSessionManager;
import com.tcdng.unify.core.util.ApplicationUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * HTTP user session.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class HttpUserSession implements UserSession, HttpSessionBindingListener {

    private transient UserSessionManager userSessionManager;

    private SessionContext sessionContext;

    public HttpUserSession(Locale locale, TimeZone timeZone, String sessionId, String uriBase, String contextPath,
            String tenantPath, String remoteHost, String remoteIpAddress, String remoteUser) {
        if (StringUtils.isBlank(sessionId)) {
            sessionId = ApplicationUtils.generateSessionContextId();
        }

        sessionContext = new SessionContext(sessionId, locale, timeZone, uriBase, contextPath, tenantPath, remoteHost,
                remoteIpAddress, remoteUser);
    }

    @Override
    public SessionContext getSessionContext() {
        return sessionContext;
    }

    @Override
    public String getRemoteAddress() {
        return sessionContext.getRemoteAddress();
    }

    @Override
    public String getRemoteHost() {
        return sessionContext.getRemoteHost();
    }

    @Override
    public String getRemoteUser() {
        return sessionContext.getRemoteUser();
    }

    @Override
    public String getTenantPath() {
        return sessionContext.getTenantPath();
    }

    public void invalidate() {
    	if (sessionContext != null) {
            try {
                userSessionManager.removeUserSession(this);
                sessionContext = null;
            } catch (UnifyException e) {
            }
    	}
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {

    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
    	invalidate();
    }

    public void setTransient(UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }
}
