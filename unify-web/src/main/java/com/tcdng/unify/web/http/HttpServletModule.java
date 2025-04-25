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
package com.tcdng.unify.web.http;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.TimeZone;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.RequestContextManager;
import com.tcdng.unify.core.UnifyContainer;
import com.tcdng.unify.core.UnifyContainerConfig;
import com.tcdng.unify.core.UnifyContainerEnvironment;
import com.tcdng.unify.core.UnifyCoreConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.system.UserSessionManager;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TypeRepository;
import com.tcdng.unify.core.util.UnifyConfigUtils;
import com.tcdng.unify.web.RequestPathParts;
import com.tcdng.unify.web.UnifyWebInterface;
import com.tcdng.unify.web.UnifyWebPropertyConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;

/**
 * Serves as an interface between the servlet container and the Unify container;
 * handling the exchange of http requests and responses between both containers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class HttpServletModule  {

	private UnifyContainer unifyContainer;

	private UnifyWebInterface webInterface;

	private RequestContextManager requestContextManager;

	private HttpRequestHandler httpRequestHandler;

	private UserSessionManager userSessionManager;

	private Locale applicationLocale;

	private TimeZone applicationTimeZone;

	private String contextPath;

	private boolean isTenantPathEnabled;

	private boolean embedded;

	public HttpServletModule() {
		this(false);
	}

	public HttpServletModule(boolean embedded) {
		this.embedded = embedded;
	}

	public void init(final String contextPath, final String workingFolder, String configFilename, final TypeRepository tr) throws Exception {
		if (!embedded) {
			if (StringUtils.isBlank(configFilename)) {
				configFilename = IOUtils.buildFilename(workingFolder, UnifyCoreConstants.CONFIGURATION_FILE);
			}

	        final String environment = System.getProperty("unify.environment");
	        if (!StringUtils.isBlank(environment)) {
	        	configFilename = UnifyConfigUtils.resolveConfigFileToEnvironment(configFilename, environment);
	        }

			InputStream configInputStream = null;
			try {
				this.contextPath = contextPath;
				UnifyContainerEnvironment uce = new UnifyContainerEnvironment(tr, workingFolder);
				UnifyContainerConfig.Builder uccb = UnifyContainerConfig.newBuilder();

				// Scan configuration
				UnifyConfigUtils.readConfigFromTypeRepository(uccb, tr);
				uccb.deploymentMode(true);
				configInputStream = new FileInputStream(IOUtils.fileInstance(configFilename, null));

				// Read xml configuration
				UnifyConfigUtils.readConfigFromXml(uccb, configInputStream, workingFolder);
				uccb.setPropertyIfBlank(UnifyWebPropertyConstants.APPLICATION_HOME,
						ReservedPageControllerConstants.DEFAULT_APPLICATION_HOME);

				// Create container
				UnifyContainerConfig ucc = uccb.build();
				this.unifyContainer = new UnifyContainer();
				this.unifyContainer.startup(uce, ucc);
				this.applicationLocale = unifyContainer.getApplicationLocale();
				this.applicationTimeZone = unifyContainer.getApplicationTimeZone();
				this.requestContextManager = (RequestContextManager) unifyContainer
						.getComponent(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER);
				this.httpRequestHandler = (HttpRequestHandler) unifyContainer
						.getComponent(WebApplicationComponents.APPLICATION_HTTPREQUESTHANDLER);
				this.userSessionManager = (UserSessionManager) unifyContainer
						.getComponent(ApplicationComponents.APPLICATION_USERSESSIONMANAGER);
				this.isTenantPathEnabled = DataUtils.convert(boolean.class,
						unifyContainer.getSetting(UnifyWebPropertyConstants.APPLICATION_TENANT_PATH_ENABLED));
			} catch (UnifyException e) {
				e.printStackTrace();
				if (unifyContainer != null && unifyContainer.isStarted()) {
					unifyContainer.shutdown();
				}
				throw e;
			} finally {
				IOUtils.close(configInputStream);
			}
		}
	}

    public void init(Locale applicationLocale, TimeZone applicationTimeZone, UnifyWebInterface webInterface,
            RequestContextManager requestContextManager, HttpRequestHandler applicationController,
            UserSessionManager userSessionManager, boolean isTenantPathEnabled) {
        this.applicationLocale = applicationLocale;
        this.applicationTimeZone = applicationTimeZone;
        this.webInterface = webInterface;
        this.requestContextManager = requestContextManager;
        this.httpRequestHandler = applicationController;
        this.userSessionManager = userSessionManager;
        this.isTenantPathEnabled = isTenantPathEnabled;
        this.contextPath = webInterface.getContextPath();
    }

	public void destroy() {
		if (unifyContainer != null && unifyContainer.isStarted()) {
			unifyContainer.shutdown();
		}
	}

    public UnifyWebInterface getWebInterface() {
        return webInterface;
    }

    public RequestContextManager getRequestContextManager() {
        return requestContextManager;
    }

    public HttpRequestHandler getHttpRequestHandler() {
        return httpRequestHandler;
    }

    public UserSessionManager getUserSessionManager() {
        return userSessionManager;
    }

    public Locale getApplicationLocale() {
        return applicationLocale;
    }

    public TimeZone getApplicationTimeZone() {
        return applicationTimeZone;
    }

    public String getContextPath() {
        return contextPath;
    }

    public boolean isTenantPathEnabled() {
        return isTenantPathEnabled;
    }

    public boolean isEmbedded() {
        return embedded;
    }
    
    public void handleRequest(HttpRequestMethodType type, HttpRequest httpRequest,
            HttpResponse httpResponse) throws UnifyException {
        if (!embedded || webInterface.isServicingRequests()) {
            try {
                HttpRequestHandler httpRequestHandler = getHttpRequestHandler();
                RequestPathParts reqPathParts = httpRequestHandler.resolveRequestPath(httpRequest);
                requestContextManager.loadRequestContext(
                        httpRequestHandler.getUserSession(this, httpRequest, reqPathParts),
                        httpRequest.getServletPath());
                httpRequestHandler.handleRequest(type, reqPathParts, httpRequest,
                        httpResponse);
            } finally {
                try {
                    userSessionManager.updateCurrentSessionLastAccessTime();
                } catch (Exception e) {
                }
                
                requestContextManager.unloadRequestContext();
            }
        }
    }
}
