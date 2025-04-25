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
package com.tcdng.unify.jetty.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.tcdng.unify.core.UnifyCoreConstants;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.NetworkSchemeType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.jetty.JettyApplicationComponents;
import com.tcdng.unify.web.constant.ClientSyncNameConstants;
import com.tcdng.unify.web.http.AbstractEmbeddedHttpWebServer;
import com.tcdng.unify.web.http.HttpApplicationServlet;

/**
 * Jetty embedded web server.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(JettyApplicationComponents.JETTY_EMBEDDEDWEBSERVER)
public class JettyEmbeddedWebServer extends AbstractEmbeddedHttpWebServer {

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
    public int getPort() throws UnifyException {
        return getHttpPort();
    }

    @Override
    protected void onInitialize() throws UnifyException {
        try {
            httpServer = new Server();

            List<Integer> portList = new ArrayList<Integer>();
            String keyStorePath = getKeyStorePath();
            if (!StringUtils.isBlank(keyStorePath)) {
                final int httpsPort = getHttpsPort();
                logInfo("Configuring HTTPS on port [{0}]...", Integer.toString(httpsPort));
                HttpConfiguration https = new HttpConfiguration();
                https.addCustomizer(new SecureRequestCustomizer());

                SslContextFactory sslContextFactory = new SslContextFactory.Server();
                Path _keyStorePath = Paths.get(keyStorePath).toAbsolutePath();
                if (!Files.exists(_keyStorePath)) {
                    throwOperationErrorException(new FileNotFoundException(_keyStorePath.toString()));
                }
                sslContextFactory.setKeyStorePath(_keyStorePath.toString());
                String password = getKeyStorePass();
                sslContextFactory.setKeyStorePassword(password);
                sslContextFactory.setKeyManagerPassword(password);

                ServerConnector sslConnector = new ServerConnector(httpServer,
                        new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                        new HttpConnectionFactory(https));
                sslConnector.setPort(httpsPort);
                httpServer.addConnector(sslConnector);
                portList.add(httpsPort);
            }

            if (isHttpsOnly()) {
                if (portList.isEmpty()) {
                    throwOperationErrorException(new IllegalArgumentException(
                            "You must provide SSL keystore properties since you have specified HTTPS only."));
                }
            } else {
                // Setup HTTP
                final int httpPort = getHttpPort();
                logInfo("Configuring HTTP on port [{0}]...", Integer.toString(httpPort));
                ServerConnector connector = new ServerConnector(httpServer);
                connector.setPort(httpPort);
                httpServer.addConnector(connector);
                portList.add(httpPort);
            }

            logInfo("Initializing HTTP server on ports {0}; using context path {1} and servlet path {2}...",
                    portList, getContextPath(), getServletPath());
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath(getContextPath());
            context.getSessionHandler().setMaxInactiveInterval(
                    getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_SESSION_TIMEOUT,
                            UnifyCoreConstants.DEFAULT_APPLICATION_SESSION_TIMEOUT_SECONDS));
            final String sessionCookieName = generateSessionCookieName();
            context.getSessionHandler().getSessionCookieConfig().setName(sessionCookieName);
//            context.getSessionHandler().getSessionCookieConfig().setHttpOnly(true);            
            httpServer.setHandler(context);

            // Websocket
            ServletHolder jettyHolder = new ServletHolder(JettyClientSyncWebSocketServlet.class);
            context.addServlet(jettyHolder, ClientSyncNameConstants.SYNC_CONTEXT);
            
            // HTTP/HTTPS
            ServletHolder mainHolder = new ServletHolder(new HttpApplicationServlet(createHttpServletModule()));
            mainHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(getMultipartLocation(),
                    getMultipartMaxFileSize(), getMultipartMaxRequestSize(), getMultipartFileSizeThreshold()));
            context.addServlet(mainHolder, getServletPath());
            context.setErrorHandler(new CustomErrorHandler());
            httpServer.addBean(new CustomErrorHandler());
            
            
            httpServer.start();
            HttpGenerator.setJettyVersion("");
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

    public class CustomErrorHandler extends ErrorHandler {

        protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message,
                boolean showStacks) throws IOException {
            String uri = request.getRequestURI();

            writeErrorPageMessage(request, writer, code, message, uri);
            if (showStacks) {
                writeErrorPageStacks(request, writer);
            }
        }

    }
}
