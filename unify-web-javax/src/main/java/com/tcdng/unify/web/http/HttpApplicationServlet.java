/*
 * Copyright 2018-2024 The Code Department.
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

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.http.util.HttpWebTypeUtils;

/**
 * Serves as an interface between the servlet container and the Unify container;
 * handling the exchange of http requests and responses between both containers.
 * 
 * @author The Code Department
 * @since 1.0
 */
@MultipartConfig( //2MB, 10MB, 50MB
		fileSizeThreshold = 1024 * 1024 * 2,
		maxFileSize = 1024 * 1024 * 10,
		maxRequestSize = 1024 * 1024 * 50)
public class HttpApplicationServlet extends HttpServlet {

	/** The serial version ID */
	private static final long serialVersionUID = 3971544226497014269L;

	private HttpServletModule httpModule;

	public HttpApplicationServlet() {
		this(false);
	}

	public HttpApplicationServlet(boolean embedded) {
		this.httpModule = new HttpServletModule(embedded);
	}

	public HttpApplicationServlet(HttpServletModule httpModule) {
		this.httpModule = httpModule;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		if (!httpModule.isEmbedded()) {
			final ServletContext servletContext = config.getServletContext();
			final String workingFolder = servletContext.getRealPath("");
			final String configFilename = config.getInitParameter("application-config-file");
			try {
				httpModule.init(servletContext.getContextPath(), workingFolder, configFilename,
						HttpWebTypeUtils.getTypeRepositoryFromServletContext(servletContext));
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}

	@Override
	public void destroy() {
		httpModule.destroy();
		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequestMethod(HttpRequestMethodType.GET, request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequestMethod(HttpRequestMethodType.POST, request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequestMethod(HttpRequestMethodType.DELETE, request, response);
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequestMethod(HttpRequestMethodType.HEAD, request, response);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequestMethod(HttpRequestMethodType.OPTIONS, request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequestMethod(HttpRequestMethodType.PUT, request, response);
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequestMethod(HttpRequestMethodType.TRACE, request, response);
	}

	private void doRequestMethod(HttpRequestMethodType type, HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		HttpResponse resp = new HttpResponseImpl(response);
		try {
			httpModule.handleRequest(type, new HttpRequestImpl(request), resp);
		} catch (Exception e) {
			e.printStackTrace();
			Writer pw = null;
			try {
				resp.setContentType(MimeType.TEXT_HTML.template());
				pw = resp.getWriter();
				pw.write("<html>\n<head>\n");
				pw.write("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"/>\n");
				pw.write("<title>ErrorPart 500</title>\n");
				pw.write("</head>\n<body>");
				pw.write("<h2>HTTP ERROR 500 - ");
				pw.write(e.getMessage());
				pw.write("</h2>\n");
				pw.write("</body>\n</html>\n");
				resp.setStatusInternalServerError();
			} catch (IOException e1) {
				e.printStackTrace();
			} finally {
				IOUtils.close(pw);
			}
		}
	}
}
