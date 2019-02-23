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

package com.tcdng.unify.web.embedded;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.RequestContextManager;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.system.UserSessionManager;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.http.AbstractHttpWebInterface;
import com.tcdng.unify.web.http.HttpApplicationServlet;
import com.tcdng.unify.web.http.HttpRequestHandler;

/**
 * Abstract base class for embedded web servers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractEmbeddedWebServer extends AbstractHttpWebInterface implements EmbeddedWebServer {

    @Configurable("8080")
    private int httpPort;

    @Configurable("/unify")
    private String contextPath;

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

    @Override
    public String getContextPath() {
        return contextPath;
    }

    protected HttpApplicationServlet createHttpServlet() throws UnifyException {
        HttpApplicationServlet httpApplicationServlet = new HttpApplicationServlet(true); // Embedded
        httpApplicationServlet.setup(this,
                (RequestContextManager) getComponent(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER),
                (HttpRequestHandler) getComponent(WebApplicationComponents.APPLICATION_HTTPREQUESTHANDLER),
                (UserSessionManager) getComponent(ApplicationComponents.APPLICATION_USERSESSIONMANAGER));
        return httpApplicationServlet;
    }

    protected int getHttpPort() {
        return httpPort;
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
