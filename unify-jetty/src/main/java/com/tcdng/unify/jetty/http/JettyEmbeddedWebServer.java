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
package com.tcdng.unify.jetty.http;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.tcdng.unify.core.UnifyContainer;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.NetworkSchemeType;
import com.tcdng.unify.jetty.JettyApplicationComponents;
import com.tcdng.unify.web.embedded.AbstractEmbeddedWebServer;

/**
 * Jetty embedded web server.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(JettyApplicationComponents.JETTY_EMBEDDEDWEBSERVER)
public class JettyEmbeddedWebServer extends AbstractEmbeddedWebServer {

    private Server httpServer;

    private NetworkSchemeType networkSchemeType;

    public JettyEmbeddedWebServer() {
        networkSchemeType = NetworkSchemeType.HTTP;
    }

    @Override
    public String getScheme() {
        return networkSchemeType.code();
    }

    @Override
    public int getPort() {
        return getHttpPort();
    }

    @Override
    protected void onInitialize() throws UnifyException {
        try {
            logInfo("Initializing HTTP server on port {0}; using context path {1} and servlet path {2}...",
                    Integer.toString(getHttpPort()), getContextPath(), getServletPath());
            httpServer = new Server(getHttpPort());
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath(getContextPath());
            context.getSessionHandler().getSessionManager().setMaxInactiveInterval(
                    getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_SESSION_TIMEOUT,
                            UnifyContainer.DEFAULT_APPLICATION_SESSION_TIMEOUT));
            httpServer.setHandler(context);

            ServletHolder mainHolder = new ServletHolder(createHttpServlet());
            mainHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(getMultipartLocation(),
                    getMultipartMaxFileSize(), getMultipartMaxRequestSize(), getMultipartFileSizeThreshold()));
            context.addServlet(mainHolder, getServletPath());
            httpServer.start();
            logInfo("HTTP server initialization completed.");
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INITIALIZATION_ERROR, getName());
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {
        try {
            httpServer.stop();
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_TERMINATION_ERROR, getName());
        }
    }

    @Override
    protected void onStartServicingRequests() throws UnifyException {

    }

    @Override
    protected void onStopServicingRequests() throws UnifyException {

    }
}
