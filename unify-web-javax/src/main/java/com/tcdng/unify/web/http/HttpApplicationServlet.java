/*
 * Copyright 2018-2020 The Code Department.
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
import javax.servlet.http.HttpSession;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserSession;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.util.ColorUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.RequestPathParts;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.http.util.HttpWebTypeUtils;

/**
 * Serves as an interface between the servlet container and the Unify container;
 * handling the exchange of http requests and responses between both containers.
 * 
 * @author Lateef Ojulari
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
            throws ServletException, IOException {
        if (!httpModule.isEmbedded() || httpModule.getWebInterface().isServicingRequests()) {
            try {
                RequestPathParts reqPathParts = httpModule.getHttpRequestHandler().resolveRequestPath(request);
                httpModule.getRequestContextManager().loadRequestContext(getUserSession(request, reqPathParts),
                        request.getServletPath());
                httpModule.getHttpRequestHandler().handleRequest(type, reqPathParts, request, response);
            } catch (Exception e) {
                throw new ServletException(e);
            } finally {
                try {
                    httpModule.getUserSessionManager().updateCurrentSessionLastAccessTime();
                } catch (Exception e) {
                }
                httpModule.getRequestContextManager().unloadRequestContext();
            }
        }
    }

    private UserSession getUserSession(HttpServletRequest request, RequestPathParts reqPathParts)
            throws UnifyException {
        HttpUserSession userSession = null;
        if (reqPathParts.isSessionless()) {
            // Non-UI controllers are session less. Handle sessionless remote call
            HttpSession httpSession = request.getSession(false);
            if (httpSession != null) {
                httpSession.invalidate();
            }
            // Create single use session object
            userSession = createHttpUserSession(request, reqPathParts, null);
        } else {
            if (StringUtils.isNotBlank(request.getParameter(RequestParameterConstants.REMOTE_VIEWER))) {
                // Handle remote view
                HttpSession httpSession = request.getSession(false);
                if (httpSession != null) {
                    httpSession.invalidate();
                }

                String sessionId = (String) request.getParameter(RequestParameterConstants.REMOTE_SESSION_ID);
                userSession = (HttpUserSession) httpModule.getUserSessionManager().getUserSession(sessionId);
                if (userSession == null) {
                    userSession = createHttpUserSession(request, reqPathParts, sessionId);
                    httpModule.getUserSessionManager().addUserSession(userSession);

                    String userLoginId = request.getParameter(RequestParameterConstants.REMOTE_USERLOGINID);
                    String userName = request.getParameter(RequestParameterConstants.REMOTE_USERNAME);
                    String roleCode = request.getParameter(RequestParameterConstants.REMOTE_ROLECD);
                    String branchCode = request.getParameter(RequestParameterConstants.REMOTE_BRANCH_CODE);
                    String zoneCode = request.getParameter(RequestParameterConstants.REMOTE_ZONE_CODE);
                    String tenantCode = request.getParameter(RequestParameterConstants.REMOTE_TENANT_CODE);
                    String colorScheme = ColorUtils.getConformingColorSchemeCode(
                            request.getParameter(RequestParameterConstants.REMOTE_COLOR_SCHEME));
                    boolean globalAccess = Boolean
                            .valueOf(request.getParameter(RequestParameterConstants.REMOTE_GLOBAL_ACCESS));

                    UserToken userToken = UserToken.newBuilder().userLoginId(userLoginId).userName(userName)
                            .ipAddress(userSession.getRemoteAddress()).branchCode(branchCode).zoneCode(zoneCode)
                            .tenantCode(tenantCode).colorScheme(colorScheme).globalAccess(globalAccess)
                            .allowMultipleLogin(true).remote(true).build();
                    userToken.setRoleCode(roleCode);
                    userSession.getSessionContext().setUserToken(userToken);
                }
            } else {
                // Handle document request
                HttpSession httpSession = request.getSession();
                userSession = (HttpUserSession) httpSession.getAttribute(HttpConstants.USER_SESSION);
                if (httpModule.isTenantPathEnabled() && userSession != null
                        && !DataUtils.equals(reqPathParts.getTenantPath(), userSession.getTenantPath())) {
                    httpSession.removeAttribute(HttpConstants.USER_SESSION);
                    userSession.invalidate();
                    userSession = null;
                }

                if (userSession == null) {
                    synchronized (httpSession) {
                        userSession = (HttpUserSession) httpSession.getAttribute(HttpConstants.USER_SESSION);
                        if (userSession == null) {
                            userSession = createHttpUserSession(request, reqPathParts, null);
                            httpSession.setAttribute(HttpConstants.USER_SESSION, userSession);
                        }
                    }
                }
            }
        }

        userSession.setTransient(httpModule.getUserSessionManager());
        return userSession;
    }

    private HttpUserSession createHttpUserSession(HttpServletRequest request, RequestPathParts reqPathParts,
            String sessionId) throws UnifyException {
        String remoteIpAddress = request.getHeader("X-FORWARDED-FOR");
        if (remoteIpAddress == null || remoteIpAddress.trim().isEmpty()
                || "unknown".equalsIgnoreCase(remoteIpAddress)) {
            remoteIpAddress = request.getHeader("Proxy-Client-IP");
        }

        if (remoteIpAddress == null || remoteIpAddress.trim().isEmpty()
                || "unknown".equalsIgnoreCase(remoteIpAddress)) {
            remoteIpAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (remoteIpAddress == null || remoteIpAddress.trim().isEmpty()
                || "unknown".equalsIgnoreCase(remoteIpAddress)) {
            remoteIpAddress = request.getRemoteAddr();
        }

        StringBuilder uriBase = new StringBuilder();
        uriBase.append(request.getScheme()).append("://").append(request.getServerName());
        if (!(("http".equals(request.getScheme()) && request.getServerPort() == 80)
                || ("https".equals(request.getScheme()) && request.getServerPort() == 443))) {
            uriBase.append(":").append(request.getServerPort());
        }

        HttpUserSession userSession = new HttpUserSession(httpModule.getApplicationLocale(),
                httpModule.getApplicationTimeZone(), sessionId, uriBase.toString(), httpModule.getContextPath(),
                reqPathParts.getTenantPath(), request.getRemoteHost(), remoteIpAddress, request.getRemoteUser());
        userSession.setTransient(httpModule.getUserSessionManager());
        return userSession;
    }
}
