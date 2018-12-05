/*
 * Copyright 2014 The Code Department
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

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.tcdng.unify.core.SessionContext;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserSession;
import com.tcdng.unify.core.constant.UserPlatform;
import com.tcdng.unify.core.system.UserSessionManager;
import com.tcdng.unify.core.util.ApplicationUtils;

/**
 * HTTP user session.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class HttpUserSession implements UserSession, HttpSessionBindingListener {

	private transient UserSessionManager userSessionManager;

	private SessionContext sessionContext;

	public HttpUserSession(String uriBase, String contextPath, String remoteHost, String remoteIpAddress,
			String remoteUser, String remoteViewer, UserPlatform platform) {
		sessionContext = new SessionContext(ApplicationUtils.generateSessionContextId(), uriBase, contextPath,
				remoteHost, remoteIpAddress, remoteUser, remoteViewer, platform);
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
	public String getRemoteViewer() {
		return sessionContext.getRemoteViewer();
	}

	@Override
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		try {
			userSessionManager.addUserSession(this);
		} catch (UnifyException e) {
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		try {
			userSessionManager.removeUserSession(this);
		} catch (UnifyException e) {
		}
	}

	public void setTransient(UserSessionManager userSessionManager) {
		this.userSessionManager = userSessionManager;
	}
}
