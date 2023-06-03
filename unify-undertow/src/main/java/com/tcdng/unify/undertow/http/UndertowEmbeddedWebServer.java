/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.undertow.http;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

import com.tcdng.unify.core.UnifyCoreConstants;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.NetworkSchemeType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.undertow.UndertowApplicationComponents;
import com.tcdng.unify.web.http.AbstractEmbeddedHttpWebServer;
import com.tcdng.unify.web.http.HttpApplicationServlet;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 * Undertow embedded web server.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(UndertowApplicationComponents.UNDERTOW_EMBEDDEDWEBSERVER)
public class UndertowEmbeddedWebServer extends AbstractEmbeddedHttpWebServer {

	private Undertow undertow;

	private NetworkSchemeType networkSchemeType;

	public UndertowEmbeddedWebServer() {
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
			Undertow.Builder ub = Undertow.builder();
			final String contextPath = StringUtils.isBlank(getContextPath()) ? "/" : getContextPath();
			DeploymentInfo deploymentInfo = Servlets.deployment().setDeploymentName(getApplicationName())
					.setClassLoader(UndertowEmbeddedWebServer.class.getClassLoader()).setContextPath(contextPath);
			List<Integer> portList = new ArrayList<Integer>();
			String keyStorePath = getKeyStorePath();
			if (!StringUtils.isBlank(keyStorePath)) {
				final int httpsPort = getHttpsPort();
				logInfo("Configuring HTTPS on port [{0}]...", Integer.toString(httpsPort));
				Path _keyStorePath = Paths.get(keyStorePath).toAbsolutePath();
				if (!Files.exists(_keyStorePath)) {
					throwOperationErrorException(new FileNotFoundException(_keyStorePath.toString()));
				}
				final String keystoreFile = _keyStorePath.toString();
				final char[] _password = getKeyStorePass().toCharArray();
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				keyStore.load(new FileInputStream(keystoreFile), _password);
				KeyManagerFactory keyManagerFactory = KeyManagerFactory
						.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				keyManagerFactory.init(keyStore, _password);

				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
				ub.addHttpsListener(httpsPort, "localhost", sslContext);
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
				ub.addHttpListener(httpPort, "localhost");
				portList.add(httpPort);
			}

			logInfo("Initializing HTTP server on ports {0}; using context path {1} and servlet path {2}...", portList,
					contextPath, getServletPath());
			final String _servletName = UndertowApplicationComponents.UNDERTOW_EMBEDDEDWEBSERVER + "-servlet";
			ServletInfo servletInfo = Servlets
					.servlet(_servletName, HttpApplicationServlet.class,
							new ImmediateInstanceFactory<Servlet>(
									new HttpApplicationServlet(createHttpServletModule())))
					.addMapping(getServletPath());
			deploymentInfo.addServlet(servletInfo);
			deploymentInfo.setDefaultSessionTimeout(
					getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_SESSION_TIMEOUT,
							UnifyCoreConstants.DEFAULT_APPLICATION_SESSION_TIMEOUT_SECONDS));
			deploymentInfo.setDefaultMultipartConfig(new MultipartConfigElement(getMultipartLocation(),
					getMultipartMaxFileSize(), getMultipartMaxRequestSize(), getMultipartFileSizeThreshold()));
			DeploymentManager dm = Servlets.defaultContainer().addDeployment(deploymentInfo);
			dm.deploy();
			PathHandler path = Handlers.path(Handlers.path().addPrefixPath(contextPath, dm.start()));
			ub.setHandler(path);
			undertow = ub.build();
			undertow.start();
			logInfo("HTTP server initialization completed.");
		} catch (Exception e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INITIALIZATION_ERROR, getName());
		}
	}

	@Override
	protected void onTerminate() throws UnifyException {
		try {
			undertow.stop();
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
