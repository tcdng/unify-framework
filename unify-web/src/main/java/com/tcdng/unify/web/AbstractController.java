/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.web;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.SessionContext;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.logging.EventLogger;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.SystemUtils;
import com.tcdng.unify.core.util.ValueStoreUtils;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.http.HttpRequestHeaders;
import com.tcdng.unify.web.http.HttpRequestParameters;

/**
 * Abstract base controller component.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractController extends AbstractUnifyComponent implements Controller {

	@Configurable
	private EventLogger eventLogger;

	@Configurable
	private ControllerFinder controllerFinder;

	private Secured secured;

	public AbstractController(Secured secured) {
		this.secured = secured;
	}

	@Override
	public boolean isSecured() {
		return secured.isTrue();
	}

    @Override
	public boolean isRefererRequired() {
		return false;
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected Secured getSecured() {
		return secured;
	}
	
	protected HttpRequestHeaders getHttpRequestHeaders() throws UnifyException {
		return (HttpRequestHeaders) getRequestAttribute(UnifyWebRequestAttributeConstants.HEADERS);
	}

	protected final <T> T getRequestParameter(Class<T> dataType, String paramName) throws UnifyException {
		return DataUtils.convert(dataType, getHttpRequestParameter(paramName));
	}

	protected HttpRequestParameters getHttpRequestParameters() throws UnifyException {
		return (HttpRequestParameters) getRequestAttribute(UnifyWebRequestAttributeConstants.PARAMETERS);
	}

	protected String getHttpRequestHeader(String headerName) throws UnifyException {
		HttpRequestHeaders headers = getHttpRequestHeaders();
		return headers != null? headers.getHeader(headerName) : null;
	}

	protected String getHttpRequestParameter(String paramName) throws UnifyException {
		HttpRequestParameters parameters = getHttpRequestParameters();
		return parameters != null? parameters.getParameter(paramName) : null;
	}

	protected ValueStore createValueStore(Object sourceObject) throws UnifyException {
		return ValueStoreUtils.getValueStore(sourceObject, null, 0);
	}

	protected EventLogger getEventLogger() throws UnifyException {
		return eventLogger;
	}

	protected ControllerFinder getControllerFinder() {
		return controllerFinder;
	}

	protected void ensureSecureAccess(ControllerPathParts reqPathParts, boolean remoteView) throws UnifyException {
		SessionContext sessionContext = getSessionContext();
		final boolean isUserLoggedInAndAuthorized = sessionContext.isAuthorized() || remoteView;
		if (isSecured() && !isUserLoggedInAndAuthorized) {
			String forceLogout = (String) sessionContext
					.removeAttribute(UnifyWebSessionAttributeConstants.FORCE_LOGOUT);
			if (forceLogout != null) {
				throw new UnifyException(SystemUtils.getSessionAttributeErrorCode(forceLogout),
						reqPathParts.getControllerPath());
			}

			throw new UnifyException(UnifyWebErrorConstants.LOGIN_REQUIRED, reqPathParts.getControllerPath());
		}
	}
}
