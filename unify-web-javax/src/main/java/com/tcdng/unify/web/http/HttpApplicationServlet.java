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
package com.tcdng.unify.web.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.http.util.HttpWebTypeUtils;

/**
 * Serves as an interface between the servlet container and the Unify container;
 * handling the exchange of http requests and responses between both containers.
 * 
 * @author The Code Department
 * @since 1.0
 */
@MultipartConfig
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
        try {
            httpModule.handleRequest(type, new HttpRequestImpl(request), new HttpResponseImpl(response));
        } catch (UnifyException e) {
            throw new ServletException(e);
        }
    }
}
