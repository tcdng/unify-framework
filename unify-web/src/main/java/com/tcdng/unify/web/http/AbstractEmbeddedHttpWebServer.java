/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.RequestContextManager;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.system.UserSessionManager;
import com.tcdng.unify.web.UnifyWebPropertyConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.http.AbstractHttpWebInterface;
import com.tcdng.unify.web.http.HttpRequestHandler;

/**
 * Abstract base class for embedded HTTP web servers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractEmbeddedHttpWebServer extends AbstractHttpWebInterface implements EmbeddedHttpWebServer {

    @Configurable("8080")
    private int httpPort;

    @Configurable("443")
    private int httpsPort;

    @Configurable("/unify")
    private String contextPath;

    @Configurable
    private String keyStorePath;

    @Configurable
    private String keyStorePass;

    @Configurable
    private boolean httpsOnly;

	@Configurable("/*")
	private String servletPath;

	@Configurable("data/tmp")
	private String multipartLocation;

	@Configurable("67108864") /* 64MB */
	private long multipartMaxFileSize;

	@Configurable("67108864") /* 64MB */
	private long multipartMaxRequestSize;

	@Configurable("4096")
	private int multipartFileSizeThreshold;

	public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    public void setHttpsOnly(boolean httpsOnly) {
        this.httpsOnly = httpsOnly;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setMultipartLocation(String multipartLocation) {
        this.multipartLocation = multipartLocation;
    }

    public void setMultipartMaxFileSize(long multipartMaxFileSize) {
        this.multipartMaxFileSize = multipartMaxFileSize;
    }

    public void setMultipartMaxRequestSize(long multipartMaxRequestSize) {
        this.multipartMaxRequestSize = multipartMaxRequestSize;
    }

    public void setMultipartFileSizeThreshold(int multipartFileSizeThreshold) {
        this.multipartFileSizeThreshold = multipartFileSizeThreshold;
    }

    @Override
	public String getContextPath() {
		return contextPath;
	}

	protected HttpServletModule createHttpServletModule() throws UnifyException {
		HttpServletModule httpApplicationServlet = new HttpServletModule(true); // Embedded
		httpApplicationServlet.init(getApplicationLocale(), getApplicationTimeZone(), this,
				(RequestContextManager) getComponent(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER),
				(HttpRequestHandler) getComponent(WebApplicationComponents.APPLICATION_HTTPREQUESTHANDLER),
				(UserSessionManager) getComponent(ApplicationComponents.APPLICATION_USERSESSIONMANAGER),
				getContainerSetting(boolean.class, UnifyWebPropertyConstants.APPLICATION_TENANT_PATH_ENABLED, false));
		return httpApplicationServlet;
	}

	protected int getHttpPort() throws UnifyException {
		return getPreferredPort() > 0 ? getPreferredPort(): httpPort;
	}

	protected int getHttpsPort() {
        return httpsPort;
    }

    protected String getKeyStorePath() {
        return keyStorePath;
    }

    protected String getKeyStorePass() {
        return keyStorePass;
    }

    protected boolean isHttpsOnly() {
        return httpsOnly;
    }

    protected String getServletPath() {
		return servletPath;
	}

	protected String getMultipartLocation() {
		return multipartLocation;
	}

	protected long getMultipartMaxFileSize() {
		return multipartMaxFileSize;
	}

	protected long getMultipartMaxRequestSize() {
		return multipartMaxRequestSize;
	}

	protected int getMultipartFileSizeThreshold() {
		return multipartFileSizeThreshold;
	}
}
