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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserSession;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.ClientPlatform;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.ColorUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ClientResponse;
import com.tcdng.unify.web.Controller;
import com.tcdng.unify.web.ControllerFinder;
import com.tcdng.unify.web.PathInfoRepository;
import com.tcdng.unify.web.RequestPathParts;
import com.tcdng.unify.web.TenantPathManager;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.UnifyWebPropertyConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.constant.RequestHeaderConstants;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;
import com.tcdng.unify.web.remotecall.RemoteCallFormat;

/**
 * Default application HTTP request handler.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_HTTPREQUESTHANDLER)
public class HttpRequestHandlerImpl extends AbstractUnifyComponent implements HttpRequestHandler {

    private static final String CONTENT_DISPOSITION = "content-disposition";
    private static final String DISPOSITION_FILENAME = "filename";
    private static final String DISPOSITION_CREATIONDATE = "creation-date";
    private static final String DISPOSITION_MODIFICATIONDATE = "modification-date";

    private static final int BUFFER_SIZE = 4096;

    @Configurable
    private ControllerFinder controllerFinder;

    @Configurable
    private PathInfoRepository pathInfoRepository;

    @Configurable
    private TenantPathManager tenantPathManager;

    private FactoryMap<String, RequestPathParts> requestPathParts;

    private Set<String> remoteViewerList;

    private boolean isTenantPathEnabled;

    public HttpRequestHandlerImpl() {

        requestPathParts = new FactoryMap<String, RequestPathParts>()
            {

                @Override
                protected RequestPathParts create(String resolvedPath, Object... params) throws Exception {
                    int len = resolvedPath.length();
                    if (len > 0 && resolvedPath.charAt(len - 1) == '/') {
                        resolvedPath = resolvedPath.substring(0, len - 1);
                    }

                    String controllerPath = resolvedPath;
                    String tenantPath = null;
                    if (isTenantPathEnabled) {
                        if (StringUtils.isBlank(resolvedPath)) {
                            throw new UnifyException(UnifyWebErrorConstants.TENANT_PART_EXPECTED_IN_URL);
                        }

                        int cIndex = resolvedPath.indexOf('/', 1);
                        if (cIndex > 0) {
                            tenantPath = resolvedPath.substring(0, cIndex);
                            controllerPath = resolvedPath.substring(cIndex);
                        } else {
                            tenantPath = resolvedPath;
                            controllerPath = null;
                        }

                        tenantPathManager.verifyTenantPath(tenantPath);
                    }

                    if (StringUtils.isBlank(controllerPath)) {
                        controllerPath = getContainerSetting(String.class, UnifyWebPropertyConstants.APPLICATION_HOME,
                                ReservedPageControllerConstants.DEFAULT_APPLICATION_HOME);
                    }

                    return new RequestPathParts(pathInfoRepository.getControllerPathParts(controllerPath), tenantPath);
                }

            };
    }

    public void setControllerFinder(ControllerFinder controllerFinder) {
        this.controllerFinder = controllerFinder;
    }

    public void setPathInfoRepository(PathInfoRepository pathInfoRepository) {
        this.pathInfoRepository = pathInfoRepository;
    }

    public void setTenantPathManager(TenantPathManager tenantPathManager) {
        this.tenantPathManager = tenantPathManager;
    }

    @Override
    public RequestPathParts resolveRequestPath(HttpRequest httpRequest) throws UnifyException {
        String resolvedPath = httpRequest.getPathInfo();
        return requestPathParts.get(resolvedPath == null ? "" : resolvedPath);
    }

    @Override
    public RequestPathParts getRequestPathParts(String requestPath) throws UnifyException {
        return requestPathParts.get(requestPath);
    }

    @Override
    public void handleRequest(HttpRequestMethodType methodType, RequestPathParts requestPathParts,
            HttpRequest httpRequest, HttpResponse httpResponse) throws UnifyException {
        try {
            Charset charset = StandardCharsets.UTF_8;
            if (httpRequest.getCharacterEncoding() != null) {
                charset = Charset.forName(httpRequest.getCharacterEncoding());
            }

            ClientRequest clientRequest = new HttpClientRequest(detectClientPlatform(httpRequest), methodType,
                    requestPathParts, charset, extractRequestParameters(httpRequest, charset));
            ClientResponse clientResponse = new HttpClientResponse(httpResponse);

            String origin = httpRequest.getHeader("origin");
            if (!StringUtils.isBlank(origin) && (remoteViewerList.isEmpty() || remoteViewerList.contains(origin))) {
                httpResponse.setHeader("Access-Control-Allow-Origin", origin);
                httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS");
                httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type");
                httpResponse.setHeader("Access-Control-Max-Age", "600");
            }

            Controller controller = controllerFinder
                    .findController(clientRequest.getRequestPathParts().getControllerPathParts());
            controller.process(clientRequest, clientResponse);
        } catch (UnifyException ue) {
            logError(ue);
            throw ue;
        }
    }

    public UserSession getUserSession(HttpServletModule httpModule, HttpRequest httpRequest,
            RequestPathParts reqPathParts) throws UnifyException {
        HttpUserSession userSession = null;
        if (reqPathParts.isSessionless()) {
            // Non-UI controllers are session less. Handle sessionless remote call
            httpRequest.invalidateSession();

            // Create single use session object
            userSession = createHttpUserSession(httpModule, httpRequest, reqPathParts, null);
        } else {
            if (StringUtils.isNotBlank(httpRequest.getParameter(RequestParameterConstants.REMOTE_VIEWER))) {
                // Handle remote view
                httpRequest.invalidateSession();

                String sessionId = (String) httpRequest.getParameter(RequestParameterConstants.REMOTE_SESSION_ID);
                userSession = (AbstractHttpUserSession) httpModule.getUserSessionManager().getUserSession(sessionId);
                if (userSession == null) {
                    userSession = createHttpUserSession(httpModule, httpRequest, reqPathParts, sessionId);
                    httpModule.getUserSessionManager().addUserSession(userSession);

                    String userLoginId = httpRequest.getParameter(RequestParameterConstants.REMOTE_USERLOGINID);
                    String userName = httpRequest.getParameter(RequestParameterConstants.REMOTE_USERNAME);
                    String roleCode = httpRequest.getParameter(RequestParameterConstants.REMOTE_ROLECD);
                    String branchCode = httpRequest.getParameter(RequestParameterConstants.REMOTE_BRANCH_CODE);
                    String zoneCode = httpRequest.getParameter(RequestParameterConstants.REMOTE_ZONE_CODE);
                    String tenantCode = httpRequest.getParameter(RequestParameterConstants.REMOTE_TENANT_CODE);
                    String colorScheme = ColorUtils.getConformingColorSchemeCode(
                            httpRequest.getParameter(RequestParameterConstants.REMOTE_COLOR_SCHEME));
                    boolean globalAccess = Boolean
                            .valueOf(httpRequest.getParameter(RequestParameterConstants.REMOTE_GLOBAL_ACCESS));

                    UserToken userToken = UserToken.newBuilder().userLoginId(userLoginId).userName(userName)
                            .ipAddress(userSession.getRemoteAddress()).branchCode(branchCode).zoneCode(zoneCode)
                            .tenantCode(tenantCode).colorScheme(colorScheme).globalAccess(globalAccess)
                            .allowMultipleLogin(true).remote(true).build();
                    userToken.setRoleCode(roleCode);
                    userSession.getSessionContext().setUserToken(userToken);
                }
            } else {
                // Handle document request
                userSession = (HttpUserSession) httpRequest.getSessionAttribute(HttpConstants.USER_SESSION);
                if (httpModule.isTenantPathEnabled() && userSession != null
                        && !DataUtils.equals(reqPathParts.getTenantPath(), userSession.getTenantPath())) {
                    httpRequest.removeSessionAttribute(HttpConstants.USER_SESSION);
                    userSession.invalidate();
                    userSession = null;
                }

                if (userSession == null) {
                    synchronized (httpRequest.getSessionSychObject()) {
                        userSession = (HttpUserSession) httpRequest.getSessionAttribute(HttpConstants.USER_SESSION);
                        if (userSession == null) {
                            userSession = createHttpUserSession(httpModule, httpRequest, reqPathParts, null);
                            httpRequest.setSessionAttribute(HttpConstants.USER_SESSION, userSession);
                        }
                    }
                }
            }
        }

        userSession.setTransient(httpModule.getUserSessionManager());
        return userSession;
    }

    private HttpUserSession createHttpUserSession(HttpServletModule httpModule, HttpRequest httpRequest,
            RequestPathParts reqPathParts, String sessionId) throws UnifyException {
        String remoteIpAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (remoteIpAddress == null || remoteIpAddress.trim().isEmpty()
                || "unknown".equalsIgnoreCase(remoteIpAddress)) {
            remoteIpAddress = httpRequest.getHeader("Proxy-Client-IP");
        }

        if (remoteIpAddress == null || remoteIpAddress.trim().isEmpty()
                || "unknown".equalsIgnoreCase(remoteIpAddress)) {
            remoteIpAddress = httpRequest.getHeader("WL-Proxy-Client-IP");
        }

        if (remoteIpAddress == null || remoteIpAddress.trim().isEmpty()
                || "unknown".equalsIgnoreCase(remoteIpAddress)) {
            remoteIpAddress = httpRequest.getRemoteAddr();
        }

        StringBuilder uriBase = new StringBuilder();
        uriBase.append(httpRequest.getScheme()).append("://").append(httpRequest.getServerName());
        if (!(("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort() == 80)
                || ("https".equals(httpRequest.getScheme()) && httpRequest.getServerPort() == 443))) {
            uriBase.append(":").append(httpRequest.getServerPort());
        }

        HttpUserSession userSession = httpRequest.createHttpUserSession(httpModule.getApplicationLocale(),
                httpModule.getApplicationTimeZone(), sessionId, uriBase.toString(), httpModule.getContextPath(),
                reqPathParts.getTenantPath(), remoteIpAddress);
        userSession.setTransient(httpModule.getUserSessionManager());
        return userSession;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitialize() throws UnifyException {
        isTenantPathEnabled = getContainerSetting(boolean.class,
                UnifyWebPropertyConstants.APPLICATION_TENANT_PATH_ENABLED, false);
        List<String> viewersList = DataUtils.convert(ArrayList.class, String.class,
                getContainerSetting(Object.class, UnifyWebPropertyConstants.APPLICATION_REMOTE_VIEWERS));
        if (!DataUtils.isBlank(viewersList)) {
            remoteViewerList = new HashSet<String>(viewersList);
        } else {
            remoteViewerList = Collections.emptySet();
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private ClientPlatform detectClientPlatform(HttpRequest httpRequest) {
        ClientPlatform platform = ClientPlatform.DEFAULT;
        String userAgent = httpRequest.getHeader("User-Agent");
        if (userAgent != null && userAgent.indexOf("Mobile") >= 0) {
            platform = ClientPlatform.MOBILE;
        }
        return platform;
    }

    private Map<String, Object> extractRequestParameters(HttpRequest httpRequest, Charset charset)
            throws UnifyException {
        Map<String, Object> result = new HashMap<String, Object>();
        String contentType = httpRequest.getContentType() == null ? null : httpRequest.getContentType().toLowerCase();
        RemoteCallFormat remoteCallFormat = RemoteCallFormat
                .fromContentType(httpRequest.getHeader(RequestHeaderConstants.REMOTE_MESSAGE_TYPE_HEADER), contentType);
        if (remoteCallFormat != null) {
            result.put(RequestParameterConstants.REMOTE_CALL_FORMAT, remoteCallFormat);
            try {
                switch (remoteCallFormat) {
                    case OCTETSTREAM:
                        result.put(RequestParameterConstants.REMOTE_CALL_INPUTSTREAM, httpRequest.getInputStream());
                        break;
                    case TAGGED_BINARYMESSAGE:
                        result.put(RequestParameterConstants.REMOTE_CALL_BODY,
                                IOUtils.readAll(httpRequest.getInputStream()));
                        break;
                    case JSON:
                    case TAGGED_XMLMESSAGE:
                    case XML:
                        result.put(RequestParameterConstants.REMOTE_CALL_BODY,
                                IOUtils.readAll(httpRequest.getReader()));
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                throwOperationErrorException(e);
            }
        } else {
            boolean isFormData = contentType != null && contentType.indexOf("multipart/form-data") >= 0;
            if (isFormData) {
                processParts(result, httpRequest);
            } else {
                boolean chkMorsic = true;
                Map<String, String[]> httpRequestParamMap = httpRequest.getParameterMap();
                for (Map.Entry<String, String[]> entry : httpRequestParamMap.entrySet()) {
                    String key = entry.getKey();
                    if (chkMorsic && RequestParameterConstants.MORSIC.equals(key)) {
                        chkMorsic = false;
                        continue;
                    }

                    String[] values = entry.getValue();
                    if (values.length == 1) {
                        if (!values[0].isEmpty()) {
                            result.put(key, values[0]);
                        } else {
                            result.put(key, null);
                        }
                    } else {
                        result.put(key, values);
                    }
                }
            }
        }

        return result;
    }

    private void processParts(Map<String, Object> requestParameterMap, HttpRequest httpRequest) throws UnifyException {
        logDebug("Processing multi-part request parameters [{0}]", requestParameterMap.keySet());
        try {
            Map<String, List<String>> stringMap = new HashMap<String, List<String>>();
            Map<String, List<UploadedFile>> uploadedFileMap = new HashMap<String, List<UploadedFile>>();
            char[] buffer = new char[BUFFER_SIZE];
            boolean chkMorsic = true;
            for (HttpPart part : httpRequest.getParts()) {
                String name = part.getName();
                if (chkMorsic && RequestParameterConstants.MORSIC.equals(name)) {
                    chkMorsic = false;
                    continue;
                }

                ContentDisposition contentDisposition = getContentDisposition(part);
                if (contentDisposition.isFileName()) {
                    UploadedFile frmFile = new UploadedFile(contentDisposition.getFileName(),
                            contentDisposition.getCreationDate(), contentDisposition.getModificationDate(),
                            IOUtils.readAll(part.getInputStream()));
                    List<UploadedFile> list = uploadedFileMap.get(name);
                    if (list == null) {
                        list = new ArrayList<UploadedFile>();
                        uploadedFileMap.put(name, list);
                    }
                    list.add(frmFile);
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    for (int length = 0; (length = reader.read(buffer)) > 0;)
                        sb.append(buffer, 0, length);
                    List<String> list = stringMap.get(name);
                    if (list == null) {
                        list = new ArrayList<String>();
                        stringMap.put(name, list);
                    }
                    list.add(sb.toString());
                }
            }

            for (Map.Entry<String, List<String>> entry : stringMap.entrySet()) {
                List<String> list = entry.getValue();
                if (list.size() == 1) {
                    if (!list.get(0).isEmpty()) {
                        requestParameterMap.put(entry.getKey(), list.get(0));
                    } else {
                        requestParameterMap.put(entry.getKey(), null);
                    }
                } else {
                    requestParameterMap.put(entry.getKey(), DataUtils.toArray(String.class, list));
                }
            }

            for (Map.Entry<String, List<UploadedFile>> entry : uploadedFileMap.entrySet()) {
                List<UploadedFile> list = entry.getValue();
                requestParameterMap.put(entry.getKey(), DataUtils.toArray(UploadedFile.class, list));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        logDebug("Multi-part request processing completed");
    }

    private ContentDisposition getContentDisposition(HttpPart part) throws UnifyException {
        String fileName = null;
        Date creationDate = null;
        Date modificationDate = null;
        for (String disposition : part.getHeader(CONTENT_DISPOSITION).split(";")) {
            if (disposition.trim().startsWith(DISPOSITION_FILENAME)) {
                fileName = disposition.substring(disposition.indexOf('=') + 1).trim().replace("\"", "");
                continue;
            }

            if (disposition.trim().startsWith(DISPOSITION_CREATIONDATE)) {
                creationDate = CalendarUtils
                        .parseRfc822Date(disposition.substring(disposition.indexOf('=') + 1).trim());
                continue;
            }

            if (disposition.trim().startsWith(DISPOSITION_MODIFICATIONDATE)) {
                modificationDate = CalendarUtils
                        .parseRfc822Date(disposition.substring(disposition.indexOf('=') + 1).trim());
                continue;
            }
        }

        return new ContentDisposition(fileName, creationDate, modificationDate);
    }

    private class ContentDisposition {

        private String fileName;

        private Date creationDate;

        private Date modificationDate;

        public ContentDisposition(String fileName, Date creationDate, Date modificationDate) {
            this.fileName = fileName;
            this.creationDate = creationDate;
            this.modificationDate = modificationDate;
        }

        public String getFileName() {
            return fileName;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public Date getModificationDate() {
            return modificationDate;
        }

        public boolean isFileName() {
            return fileName != null;
        }
    }
}
