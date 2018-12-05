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
import com.tcdng.unify.core.net.NetworkSchemeType;
import com.tcdng.unify.jetty.JettyApplicationComponents;
import com.tcdng.unify.web.AbstractHttpInterface;

/**
 * Jetty HTTP web interface implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(JettyApplicationComponents.JETTY_HTTPINTERFACE)
public class JettyHttpInterface extends AbstractHttpInterface {

	private Server httpServer;

	private NetworkSchemeType networkSchemeType;

	public JettyHttpInterface() {
		this.networkSchemeType = NetworkSchemeType.HTTP;
	}

	@Override
	public String getScheme() {
		return this.networkSchemeType.code();
	}

	@Override
	public int getPort() {
		return this.getHttpPort();
	}

	@Override
	protected void onInitialize() throws UnifyException {
		try {
			this.logInfo("Initializing HTTP server on port {0}; using context path {1} and servlet path {2}...",
					Integer.toString(this.getHttpPort()), this.getContextPath(), this.getServletPath());
			this.httpServer = new Server(this.getHttpPort());
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath(this.getContextPath());
			context.getSessionHandler().getSessionManager().setMaxInactiveInterval(
					this.getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_SESSION_TIMEOUT,
							UnifyContainer.DEFAULT_APPLICATION_SESSION_TIMEOUT));
			this.httpServer.setHandler(context);

			ServletHolder mainHolder = new ServletHolder(this.createHttpServlet());
			mainHolder.getRegistration().setMultipartConfig(
					new MultipartConfigElement(this.getMultipartLocation(), this.getMultipartMaxFileSize(),
							this.getMultipartMaxRequestSize(), this.getMultipartFileSizeThreshold()));
			context.addServlet(mainHolder, this.getServletPath());
			this.httpServer.start();
			this.logInfo("HTTP server initialization completed.");
		} catch (Exception e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INITIALIZATION_ERROR, this.getName());
		}
	}

	@Override
	protected void onTerminate() throws UnifyException {
		try {
			this.httpServer.stop();
		} catch (Exception e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_TERMINATION_ERROR, this.getName());
		}
	}

	@Override
	protected void onStartServicingRequests() throws UnifyException {

	}

	@Override
	protected void onStopServicingRequests() throws UnifyException {

	}
}
