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
package com.tcdng.unify.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.SessionContext;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyError;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.stream.JSONObjectStreamer;
import com.tcdng.unify.core.stream.ObjectStreamer;
import com.tcdng.unify.core.stream.XMLObjectStreamer;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.SystemUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;
import com.tcdng.unify.web.annotation.Gateway;
import com.tcdng.unify.web.annotation.RequestParameter;
import com.tcdng.unify.web.annotation.ResultMappings;
import com.tcdng.unify.web.constant.ClosePageMode;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.SystemInfoConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.remotecall.RemoteCallBinaryMessageStreamer;
import com.tcdng.unify.web.remotecall.RemoteCallError;
import com.tcdng.unify.web.remotecall.RemoteCallFormat;
import com.tcdng.unify.web.remotecall.RemoteCallParams;
import com.tcdng.unify.web.remotecall.RemoteCallResult;
import com.tcdng.unify.web.remotecall.RemoteCallXmlMessageStreamer;
import com.tcdng.unify.web.ui.ContentPanel;
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.PageAction;
import com.tcdng.unify.web.ui.PageManager;
import com.tcdng.unify.web.ui.PropertyInfo;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.ResponseWriterPool;
import com.tcdng.unify.web.util.DataTransferUtils;

/**
 * Default implementation of application controller manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER)
public class ControllerManagerImpl extends AbstractUnifyComponent implements ControllerManager {

    @Configurable
    private PageManager pageManager;

    @Configurable
    private PathInfoRepository pathInfoRepository;

    @Configurable
    private RequestContextUtil requestContextUtil;

    @Configurable
    private ResponseWriterPool responseWriterPool;

    @Configurable
    private XMLObjectStreamer xmlObjectStreamer;

    @Configurable
    private JSONObjectStreamer jsonObjectStreamer;

    @Configurable
    private RemoteCallBinaryMessageStreamer remoteCallBinaryMessageStreamer;

    @Configurable
    private RemoteCallXmlMessageStreamer remoteCallXmlMessageStreamer;

    private Map<RemoteCallFormat, ObjectStreamer> objectStreamers;

    private Map<String, Result> defaultResultMap;

    @SuppressWarnings("rawtypes")
    private FactoryMap<Class<? extends PageController>, PageControllerActionInfo> pageControllerActionInfoMap;

    private FactoryMap<String, PageControllerInfo> pageControllerInfoMap;

    private FactoryMap<String, RemoteCallControllerInfo> remoteCallControllerInfoMap;

    private FactoryMap<String, ResourceControllerInfo> resourceControllerInfoMap;

    private PageControllerResponse hintUserResponse;

    private PageControllerResponse refreshMenuResponse;

    private Set<String> skipOnPopulateSet;

    private String commonUtilitiesControllerName;

    @SuppressWarnings("rawtypes")
    public ControllerManagerImpl() {
        skipOnPopulateSet = new HashSet<String>();

        pageControllerActionInfoMap = new FactoryMap<Class<? extends PageController>, PageControllerActionInfo>() {

            @Override
            protected PageControllerActionInfo create(Class<? extends PageController> clazz, Object... params)
                    throws Exception {
                PageControllerActionInfo pageControllerActionInfo = new PageControllerActionInfo();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    com.tcdng.unify.web.annotation.Action aa =
                            method.getAnnotation(com.tcdng.unify.web.annotation.Action.class);
                    if (aa != null) {
                        if (isActionHandlerSignature(method)) {
                            pageControllerActionInfo.addActionMethod(method);
                        } else {
                            throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_INVALID_ACTION_HANDLER_SIGNATURE,
                                    clazz.getName(), method.getName());
                        }
                    } else {
                        // Check if method has an action handler method signature and super class has
                        // similar.
                        // In other words, check inheritance of @Action from super class.
                        // This implies that if a action handler method overrides a super action handler
                        // method, we
                        // don't have to apply the @Action annotation
                        if (isActionHandlerSignature(method) && isSuperCommandMethod(clazz, method.getName())) {
                            pageControllerActionInfo.addActionMethod(method);
                        }
                    }
                }

                return pageControllerActionInfo;
            }

        };

        pageControllerInfoMap = new FactoryMap<String, PageControllerInfo>() {
            @Override
            protected PageControllerInfo create(String controllerName, Object... params) throws Exception {
                return createPageControllerInfo(controllerName);
            }
        };

        remoteCallControllerInfoMap = new FactoryMap<String, RemoteCallControllerInfo>() {
            @Override
            protected RemoteCallControllerInfo create(String controllerName, Object... params) throws Exception {
                return createRemoteCallControllerInfo(controllerName);
            }
        };

        resourceControllerInfoMap = new FactoryMap<String, ResourceControllerInfo>() {
            @Override
            protected ResourceControllerInfo create(String controllerName, Object... params) throws Exception {
                return createResourceControllerInfo(controllerName);
            }
        };

        skipOnPopulateSet.add(RequestParameterConstants.DOCUMENT);
        skipOnPopulateSet.add(RequestParameterConstants.TARGET_VALUE);
        skipOnPopulateSet.add(RequestParameterConstants.VALIDATION_ACTION);
        skipOnPopulateSet.add(RequestParameterConstants.CONFIRM_MSG);
        skipOnPopulateSet.add(RequestParameterConstants.CONFIRM_MSGICON);
        skipOnPopulateSet.add(RequestParameterConstants.CONFIRM_PARAM);

        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_VIEWER);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_ROLECD);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_SESSION_ID);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_USERLOGINID);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_USERNAME);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_BRANCH_CODE);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_ZONE_CODE);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_GLOBAL_ACCESS);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_COLOR_SCHEME);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_TENANT_CODE);
    }

    @Override
    public PageControllerInfo getPageControllerInfo(String controllerName) throws UnifyException {
        return pageControllerInfoMap.get(controllerName);
    }

    @Override
    public void updatePageControllerInfo(String controllerName, String standalonePanelName) throws UnifyException {
        PageControllerInfo pbbi = getPageControllerInfo(controllerName);
        Map<String, PropertyInfo> bindings = pageManager.getStandalonePanelPropertyBindings(standalonePanelName);
        pbbi.addBindings(standalonePanelName, bindings);
    }

    @Override
    public RemoteCallControllerInfo getRemoteCallControllerInfo(String controllerName) throws UnifyException {
        return remoteCallControllerInfoMap.get(controllerName);
    }

    @Override
    public ResourceControllerInfo getResourceControllerInfo(String controllerName) throws UnifyException {
        return resourceControllerInfoMap.get(controllerName);
    }

    @Override
    public Controller getController(ControllerPathParts controllerPathParts, boolean isLoadPage) throws UnifyException {
        logDebug("Retrieving controller for path [{0}]...", controllerPathParts.getControllerPath());

        final String controllerName = controllerPathParts.getControllerName();
        UnifyComponentConfig unifyComponentConfig = getComponentConfig(Controller.class, controllerName);

        final String path = controllerPathParts.getControllerPath();
        if (unifyComponentConfig == null) {
            // May be a real path request
            File file = new File(IOUtils.buildFilename(getUnifyComponentContext().getWorkingPath(), path));
            if (file.exists()) {
                ResourceController realPathResourceController = (ResourceController) getComponent("/resource/realpath");
                realPathResourceController.setResourceName(path);
                return realPathResourceController;
            }
        }

        if (unifyComponentConfig == null) {
            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_UNKNOWN_COMP, path);
        }

        Controller controller = (Controller) getComponent(controllerName);
        if (ControllerType.PAGE_CONTROLLER.equals(controller.getType())) {
            SessionContext sessionContext = getSessionContext();
            Page page = (Page) sessionContext.getAttribute(controllerPathParts.getControllerPathId());
            if (page == null) {
                Page currentPage = requestContextUtil.getRequestPage();
                try {
                    PageController<?> pageController = (PageController<?>) controller;
                    page = pageManager.createPage(sessionContext.getLocale(), controllerName);
                    page.setPathParts(controllerPathParts);
                    Class<? extends PageBean> pageBeanClass = pageController.getPageBeanClass();
                    if (VoidPageBean.class.equals(pageBeanClass)) {
                        page.setPageBean(VoidPageBean.INSTANCE);
                    } else {
                        page.setPageBean(ReflectUtils.newInstance(pageBeanClass));
                    }

                    requestContextUtil.setRequestPage(page);
                    pageController.initPage();
                    sessionContext.setAttribute(controllerPathParts.getControllerPathId(), page);
                } finally {
                    if (!isLoadPage) {
                        requestContextUtil.setRequestPage(currentPage);
                    }
                }
            }
        }

        logDebug("Controller with name [{0}] retrieved", controllerName);
        return controller;
    }

    @Override
    public void executeController(ClientRequest request, ClientResponse response) throws UnifyException {
        Controller controller = null;
        PageController<?> docPageController = null;
        try {
            ControllerPathParts docPathParts = null;
            final ControllerPathParts reqPathParts = request.getRequestPathParts().getControllerPathParts();
            controller = getController(reqPathParts, false);

            ControllerType controllerType = controller.getType();
            if (controllerType.isUIController()) {
                String documentPath = (String) request.getParameter(RequestParameterConstants.DOCUMENT);
                if (documentPath != null) {
                    docPathParts = pathInfoRepository.getControllerPathParts(documentPath);
                    docPageController = (PageController<?>) getController(docPathParts, false);
                    requestContextUtil.setRequestDocument((Document) loadRequestPage(docPathParts));
                }

                requestContextUtil.extractRequestParameters(request);
            }

            SessionContext sessionContext = getSessionContext();
            boolean isUserLoggedIn = sessionContext.isUserLoggedIn() || requestContextUtil.isRemoteViewer();
            if (controller.isSecured() && !isUserLoggedIn) {
                String forceLogout =
                        (String) sessionContext.removeAttribute(UnifyWebSessionAttributeConstants.FORCE_LOGOUT);
                if (forceLogout != null) {
                    throw new UnifyException(SystemUtils.getSessionAttributeErrorCode(forceLogout),
                            reqPathParts.getControllerPath());
                }

                throw new UnifyException(UnifyWebErrorConstants.LOGIN_REQUIRED, reqPathParts.getControllerPath());
            }

            if (ControllerType.PLAIN_CONTROLLER.equals(controllerType)) {
                ((PlainController) controller).execute(request, response);
            } else if (ControllerType.REMOTECALL_CONTROLLER.equals(controllerType)) {
                RemoteCallController remoteCallController = (RemoteCallController) controller;
                RemoteCallFormat remoteCallFormat =
                        (RemoteCallFormat) request.getParameter(RequestParameterConstants.REMOTE_CALL_FORMAT);
                Object reqBody = request.getParameter(RequestParameterConstants.REMOTE_CALL_BODY);
                Object respBody = executeRemoteCall(remoteCallController, remoteCallFormat,
                        reqPathParts.getControllerPath(), reqBody);
                response.setContentType(remoteCallFormat.mimeType().template());
                if (request.getCharset() != null) {
                    response.setCharacterEncoding(request.getCharset().name());
                }

                if (remoteCallFormat.isStringFormat()) {
                    response.getWriter().write((String) respBody);
                } else {
                    response.getOutputStream().write((byte[]) respBody);
                }
            } else if (ControllerType.RESOURCE_CONTROLLER.equals(controllerType)) {
                ResourceController resourceController = (ResourceController) controller;
                if (!resourceController.isReadOnly()) {
                    DataTransfer dataTransfer = prepareDataTransfer(resourceController, request);
                    populate(resourceController, dataTransfer);
                }

                resourceController.prepareExecution();

                for (String key : resourceController.getMetaDataKeys()) {
                    response.setMetaData(key, resourceController.getMetaData(key));
                }

                if (resourceController.getContentType() != null) {
                    response.setContentType(resourceController.getContentType());
                }

                resourceController.execute(response.getOutputStream());
            } else {
                ResponseWriter writer = responseWriterPool.getResponseWriter();
                try {
                    ControllerPathParts respPathParts = reqPathParts;
                    PageController<?> pageController = (PageController<?>) controller;
                    PageControllerInfo pbbInfo = pageControllerInfoMap.get(pageController.getName());
                    Page page = loadRequestPage(reqPathParts);
                    String resultName = ResultMappingConstants.VALIDATION_ERROR;

                    DataTransfer dataTransfer = prepareDataTransfer(pageController, request);
                    if (validate(page, dataTransfer)) {
                        synchronized (page) {
                            populate(pageController, dataTransfer);
                            if (reqPathParts.isActionPath()) {
                                resultName = executePageCall(pageController, reqPathParts.getActionName());
                            } else {
                                resultName = executePageCall(pageController, "/indexPage");
                            }
                        }

                        logDebug("Processing result with name [{0}]...", resultName);
                        // Check if action result needs to be routed to containing
                        // document controller
                        if (!pbbInfo.hasResultWithName(resultName) && !page.isDocument()) {
                            if (docPageController != null) {
                                logDebug("AggregateItem with name [{0}] not found for controller [{1}]...", resultName,
                                        pageController.getName());
                                respPathParts = docPathParts;
                                pageController = docPageController;
                                page = loadRequestPage(respPathParts);
                                pbbInfo = pageControllerInfoMap.get(pageController.getName());
                                logDebug("AggregateItem with name [{0}] routed to controller [{1}]...", resultName,
                                        pageController.getName());
                            }
                        }

                        // Route to common utilities if necessary
                        if (!pbbInfo.hasResultWithName(resultName)) {
                            logDebug("AggregateItem with name [{0}] not found for controller [{1}]...", resultName,
                                    pageController.getName());
                            respPathParts = pathInfoRepository.getControllerPathParts(commonUtilitiesControllerName);
                            pageController = (PageController<?>) getController(respPathParts, false);
                            page = loadRequestPage(respPathParts);
                            pbbInfo = pageControllerInfoMap.get(pageController.getName());
                            logDebug("AggregateItem with name [{0}] routed to controller [{1}]...", resultName,
                                    pageController.getName());
                        }
                    }

                    // Write response using information from response path parts
                    requestContextUtil.setResponsePathParts(respPathParts);
                    Result result = pbbInfo.getResult(resultName);
                    writeResponse(writer, page, result);

                    response.setContentType(result.getMimeType().template());
                    if (request.getCharset() != null) {
                        response.setCharacterEncoding(request.getCharset().name());
                    }

                    writer.writeTo(response.getWriter());
                } finally {
                    // Remove closed pages from session
                    getSessionContext().removeAttributes(requestContextUtil.getClosedPagePaths());

                    // Restore writer
                    responseWriterPool.restore(writer);
                }
            }
        } catch (Exception e) {
            boolean isUIRequest = false;
            if (controller != null) {
                isUIRequest = controller.getType().isUIController();
            } else {
                isUIRequest = request.getParameter(RequestParameterConstants.REMOTE_CALL_BODY) == null;
            }

            if (isUIRequest) {
                writeExceptionResponseForUserInterface(request, response, e);
            } else {
                // TODO
                e.printStackTrace();
            }
        } finally {
            response.close();
        }
    }

    @Override
    public String executePageController(String fullActionPath) throws UnifyException {
        Page currentPage = requestContextUtil.getRequestPage();
        try {
            ControllerPathParts targetPathParts = pathInfoRepository.getControllerPathParts(fullActionPath);
            PageController<?> targetPageController = (PageController<?>) getController(targetPathParts, false);
            loadRequestPage(targetPathParts);
            return executePageCall(targetPageController, targetPathParts.getActionName());
        } finally {
            requestContextUtil.setRequestPage(currentPage); // Restore original page
        }
    }

    @Override
    public void populatePageBean(String controllerName, String property, Object value) throws UnifyException {
        Page currentPage = requestContextUtil.getRequestPage();
        try {
            ControllerPathParts targetPathParts = pathInfoRepository.getControllerPathParts(controllerName);
            getController(targetPathParts, false); // Force target page to be created in session if necessary
            Page targetPage = loadRequestPage(targetPathParts);
            DataUtils.setNestedBeanProperty(targetPage.getPageBean(), property, value);
        } finally {
            requestContextUtil.setRequestPage(currentPage); // Restore original page
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {
        // Object streamer mappings
        objectStreamers = new HashMap<RemoteCallFormat, ObjectStreamer>();
        objectStreamers.put(RemoteCallFormat.JSON, jsonObjectStreamer);
        objectStreamers.put(RemoteCallFormat.XML, xmlObjectStreamer);
        objectStreamers.put(RemoteCallFormat.TAGGED_BINARYMESSAGE, remoteCallBinaryMessageStreamer);
        objectStreamers.put(RemoteCallFormat.TAGGED_XMLMESSAGE, remoteCallXmlMessageStreamer);
        objectStreamers = Collections.unmodifiableMap(objectStreamers);

        // Default result mappings
        defaultResultMap = new HashMap<String, Result>();
        Locale defaultLocale = Locale.getDefault();
        commonUtilitiesControllerName =
                getContainerSetting(String.class, UnifyWebPropertyConstants.APPLICATION_COMMON_UTILITIES,
                        ReservedPageControllerConstants.COMMONUTILITIES);

        hintUserResponse = (PageControllerResponse) getUplComponent(defaultLocale, "!hintuserresponse", false);

        refreshMenuResponse = (PageControllerResponse) getUplComponent(defaultLocale, "!refreshmenuresponse", false);

        defaultResultMap.put(ResultMappingConstants.NONE,
                new Result(new PageControllerResponse[] { hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.INDEX, new Result(MimeType.TEXT_HTML, new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!loaddocumentresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.OPEN,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!loadcontentresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.SAVE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!postresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.CLOSE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!unloadcontentresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.REMOTE_VIEW, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!docviewresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.POST_RESPONSE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale,
                                "!postresponse pathRequestAttribute:$s{"
                                        + UnifyWebRequestAttributeConstants.COMMAND_POSTRESPONSE_PATH + "}",
                                false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.HINT_USER,
                new Result(new PageControllerResponse[] { hintUserResponse }));

        defaultResultMap.put(ResultMappingConstants.SHOW_POPUP, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!showpopupresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.HIDE_POPUP,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.HIDE_POPUP_FIRE_CONFIRM,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        hintUserResponse, refreshMenuResponse,
                        (PageControllerResponse) getUplComponent(defaultLocale, "!firepreconfirmresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.COMMAND,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!commandpostresponse", false),
                        hintUserResponse }));

        defaultResultMap.put(ResultMappingConstants.SHOW_ATTACHMENT, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!fileattachmentresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.DOWNLOAD_FILE, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!filedownloadresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.REFRESH_SHOW_POPUP,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!showpopupresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.REFRESH_SECTION, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!refreshsectionresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.VALIDATION_ERROR, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!validationerrorresponse", false) }));
        defaultResultMap = Collections.unmodifiableMap(defaultResultMap);
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected PageManager getPageManager() {
        return pageManager;
    }

    protected void populate(UIController uiController, DataTransfer dataTransfer) throws UnifyException {
        if (!uiController.isReadOnly()) {
            logDebug("Populating controller [{0}]", uiController.getName());

            // Reset first
            if (uiController.isResetOnWrite()) {
                uiController.reset();
            }

            // Populate controller
            for (DataTransferBlock dataTransferBlock : dataTransfer.getDataTransferBlocks()) {
                do {
                    logDebug("Populating widget [{0}] with value [{1}] using transfer block [{2}]...",
                            dataTransferBlock.getLongName(), dataTransferBlock.getDebugValue(), dataTransferBlock);
                    uiController.populate(dataTransferBlock);
                    dataTransferBlock = dataTransferBlock.getSiblingBlock();
                } while (dataTransferBlock != null);
            }

            logDebug("Controller population completed [{0}]", uiController.getName());
        }
    }

    protected boolean validate(Page page, DataTransfer dataTransfer) throws UnifyException {
        boolean successful = true;
        if (page.isValidationEnabled()) {
            String actionId = dataTransfer.getActionId();
            logDebug("Page validation is enabled. actionId = [{0}]", actionId);

            if (StringUtils.isNotBlank(actionId)) {
                logDebug("Performing request parameter validation. path ID [{0}]", page.getPathId());

                // Do validations
                PageAction pageAction = page.getPageAction(pageManager.getLongName(actionId));
                UplElementReferences uer = pageAction.getUplAttribute(UplElementReferences.class, "validations");
                for (String validationLongName : uer.getLongNames()) {
                    logDebug("Applying validation [{0}]...", validationLongName);
                    successful &= page.getPageWidgetValidator(pageManager, validationLongName).validate(dataTransfer);
                }

                logDebug("Request parameter validation completed. path ID [{0}]", page.getPathId());
            }
        }

        return successful;
    }

    protected String executePageCall(PageController<?> pageController, String actionName) throws UnifyException {
        try {
            if ("/openPage".equals(actionName)) {
                pageController.openPage();
                if (!requestContextUtil.isRemoteViewer()) {
                    ContentPanel contentPanel = requestContextUtil.getRequestDocument().getContentPanel();
                    contentPanel.addContent(requestContextUtil.getRequestPage());
                }
            } else if ("/closePage".equals(actionName)) {
                ClosePageMode closePageMode = requestContextUtil.getRequestTargetValue(ClosePageMode.class);
                performClosePage(closePageMode, true);
                return ResultMappingConstants.CLOSE;
            }

            String resultName = (String) pageControllerInfoMap.get(pageController.getName()).getAction(actionName)
                    .getMethod().invoke(pageController);
            if (ResultMappingConstants.CLOSE.equals(resultName)) {
                // Handle other actions that also return CLOSE result
                performClosePage(ClosePageMode.CLOSE, false);
            }

            return resultName;
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return null;
    }

    protected Object executeRemoteCall(RemoteCallController remoteCallController, RemoteCallFormat remoteCallFormat,
            String remoteHandler, Object remoteParam) throws UnifyException {
        Object respObj = null;
        String methodCode = null;

        ObjectStreamer streamer = objectStreamers.get(remoteCallFormat);
        RemoteCallHandler handler = null;
        try {
            RemoteCallControllerInfo rbbInfo = remoteCallControllerInfoMap.get(remoteCallController.getName());
            handler = rbbInfo.getRemoteCallHandler(remoteHandler);
            RemoteCallParams param = null;
            if (remoteCallFormat.isStringFormat()) {
                param = streamer.unmarshal(handler.getParamType(), (String) remoteParam);
            } else {
                param = streamer.unmarshal(handler.getParamType(), new ByteArrayInputStream((byte[]) remoteParam));
            }

            methodCode = handler.getMethodCode();
            if (handler.isRestricted() && rbbInfo.isRemoteCallGate()) {
                RemoteCallGate gate = (RemoteCallGate) getComponent(rbbInfo.getRemoteCallGateName());
                gate.grantPass(param.getClientAppCode(), methodCode);
            }

            RemoteCallResult result = (RemoteCallResult) handler.getMethod().invoke(remoteCallController, param);
            respObj = streamer.marshal(result);
        } catch (Exception e) {
            logError(e);
            RemoteCallResult error = null;
            if (handler != null) {
                error = ReflectUtils.newInstance(handler.getReturnType());
            } else {
                error = new RemoteCallError();
            }

            error.setMethodCode(methodCode);
            if (e instanceof UnifyException) {
                UnifyError ue = ((UnifyException) e).getUnifyError();
                error.setErrorCode(ue.getErrorCode());
                error.setErrorMsg(getSessionMessage(ue.getErrorCode(), ue.getErrorParams()));
            } else {
                if (e.getCause() instanceof UnifyException) {
                    UnifyError ue = ((UnifyException) e.getCause()).getUnifyError();
                    error.setErrorCode(ue.getErrorCode());
                    error.setErrorMsg(getSessionMessage(ue.getErrorCode(), ue.getErrorParams()));
                } else {
                    error.setErrorCode(UnifyWebErrorConstants.REMOTECALL_ERROR);
                    error.setErrorMsg(e.getMessage());
                }
            }
            respObj = streamer.marshal(error);
        }
        return respObj;
    }

    protected void writeExceptionResponseForUserInterface(ClientRequest request, ClientResponse response, Exception e)
            throws UnifyException {
        logError(e);

        if (response.isOutUsed()) {
            if (e instanceof UnifyException) {
                throw (UnifyException) e;
            } else {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, e);
            }
        }

        // Set exception attributes in session context.
        Boolean loginRequired = Boolean.FALSE;
        if (e instanceof UnifyException) {
            String errorCode = ((UnifyException) e).getUnifyError().getErrorCode();
            loginRequired = UnifyWebErrorConstants.LOGIN_REQUIRED.equals(errorCode)
                    || SystemUtils.isForceLogoutErrorCode(errorCode);
        }
        String message = getExceptionMessage(LocaleType.SESSION, e);
        setSessionAttribute(SystemInfoConstants.LOGIN_REQUIRED_FLAG, loginRequired);
        setSessionAttribute(SystemInfoConstants.EXCEPTION_MESSAGE_KEY, message);

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        sw.flush();
        setSessionAttribute(SystemInfoConstants.EXCEPTION_STACKTRACE_KEY, sw.toString());

        // Generate exception response
        ResponseWriter writer = responseWriterPool.getResponseWriter();
        try {
            PageController<?> pageController = null;
            ControllerPathParts respPathParts = null;
            Page page = null;
            Result result = null;
            if (StringUtils.isBlank((String) request.getParameter(RequestParameterConstants.DOCUMENT))
                    && !requestContextUtil.isRemoteViewer()) {
                respPathParts =
                        pathInfoRepository.getControllerPathParts(SystemInfoConstants.UNAUTHORIZED_CONTROLLER_NAME);
                pageController = (PageController<?>) getController(respPathParts, false);
                page = loadRequestPage(respPathParts);
                result = pageControllerInfoMap.get(pageController.getName()).getResult(ResultMappingConstants.INDEX);
            } else {
                respPathParts =
                        pathInfoRepository.getControllerPathParts(SystemInfoConstants.SYSTEMINFO_CONTROLLER_NAME);
                pageController = (PageController<?>) getController(respPathParts, false);
                page = loadRequestPage(respPathParts);
                page.setWidgetVisible("stackTrace", !loginRequired);
                result = pageControllerInfoMap.get(pageController.getName())
                        .getResult(SystemInfoConstants.SHOW_SYSTEM_EXCEPTION_MAPPING);
            }

            requestContextUtil.setResponsePathParts(respPathParts);
            writeResponse(writer, page, result);

            response.setContentType(result.getMimeType().template());
            writer.writeTo(response.getWriter());
        } catch (UnifyException e1) {
            throw e1;
        } catch (Exception e1) {
            throwOperationErrorException(e1);
        } finally {
            responseWriterPool.restore(writer);
        }
    }

    private class PageControllerActionInfo {

        private Map<String, Method> actionMethods;

        public PageControllerActionInfo() {
            actionMethods = new HashMap<String, Method>();
        }

        public void addActionMethod(Method method) {
            actionMethods.put(method.getName(), method);
        }

        public boolean isActionMethod(String name) {
            return actionMethods.containsKey(name);
        }

        public Collection<Method> getActionMethods() {
            return actionMethods.values();
        }
    }

    private void performClosePage(ClosePageMode closePageMode, boolean isFireClose) throws UnifyException {
        Page currentPage = requestContextUtil.getRequestPage();
        if (requestContextUtil.isRemoteViewer()) {
            // Fire closePage()
            ControllerPathParts controllerPathParts =
                    pathInfoRepository.getControllerPathParts(currentPage.getPathId());
            loadRequestPage(controllerPathParts);
            ((PageController<?>) getComponent(controllerPathParts.getControllerName())).closePage();
            requestContextUtil.setClosedPagePaths(Arrays.asList(currentPage.getPathId()));
            return;
        }

        ContentPanel contentPanel = requestContextUtil.getRequestDocument().getContentPanel();
        List<String> toClosePathIdList = contentPanel.evaluateRemoveContent(currentPage, closePageMode);
        if (!toClosePathIdList.isEmpty()) {
            try {
                if (isFireClose) {
                    // Fire closePage() for all targets
                    for (String closePathId : toClosePathIdList) {
                        ControllerPathParts controllerPathParts =
                                pathInfoRepository.getControllerPathParts(closePathId);
                        loadRequestPage(controllerPathParts);
                        ((PageController<?>) getComponent(controllerPathParts.getControllerName())).closePage();
                    }
                }

                // Do actual content removal
                contentPanel.removeContent(toClosePathIdList);

                // Set pages for removal
                requestContextUtil.setClosedPagePaths(toClosePathIdList);
            } finally {
                // Restore request page
                requestContextUtil.setRequestPage(currentPage);
            }
        }
    }

    private Page loadRequestPage(ControllerPathParts controllerPathParts) throws UnifyException {
        Page page = (Page) getSessionContext().getAttribute(controllerPathParts.getControllerPathId());
        requestContextUtil.setRequestPage(page);
        return page;
    }

    private boolean isActionHandlerSignature(Method method) throws UnifyException {
        return String.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0
                && method.getExceptionTypes().length == 1
                && UnifyException.class.isAssignableFrom(method.getExceptionTypes()[0]);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean isSuperCommandMethod(Class<? extends PageController> pageControllerClass, String methodName)
            throws UnifyException {
        Class<?> clazz = pageControllerClass;
        while ((clazz = clazz.getSuperclass()) != null && PageController.class.isAssignableFrom(clazz)) {
            if (pageControllerActionInfoMap.get((Class<? extends PageController<?>>) clazz)
                    .isActionMethod(methodName)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private PageControllerInfo createPageControllerInfo(String controllerName) throws UnifyException {
        // Process action handlers
        Map<String, Action> actionByNameMap = new HashMap<String, Action>();
        Class<? extends PageController> typeClass = getComponentType(PageController.class, controllerName);
        for (Method method : pageControllerActionInfoMap.get(typeClass).getActionMethods()) {
            actionByNameMap.put("/" + method.getName(), new Action(method));
        }

        // Process result mappings
        Map<String, Result> resultByNameMap = new ConcurrentHashMap<String, Result>();
        List<Class<?>> classList = ReflectUtils.getClassHierachyList(typeClass);
        for (Class<?> clazz : classList) {
            // Grab results definition by super class hierarchy with subclass
            // definitions overriding those in superclass
            ResultMappings rsa = clazz.getAnnotation(ResultMappings.class);
            if (rsa != null) {
                for (com.tcdng.unify.web.annotation.ResultMapping ra : rsa.value()) {
                    String[] descriptors = ra.response();

                    List<PageControllerResponse> responses = new ArrayList<PageControllerResponse>();
                    Locale locale = Locale.getDefault();
                    for (String descriptor : descriptors) {
                        if (!"!hintuserresponse".equals(descriptor) && !"!refreshmenuresponse".equals(descriptor)) {
                            responses.add((PageControllerResponse) getUplComponent(locale, descriptor, false));
                        }
                    }

                    // Add implicit responses
                    responses.add(hintUserResponse);
                    responses.add(refreshMenuResponse);

                    // Set result object
                    resultByNameMap.put(ra.name(),
                            new Result(DataUtils.toArray(PageControllerResponse.class, responses)));
                }
            }
        }

        // Resolve category
        List<String> categoryList = DataUtils.convert(ArrayList.class, String.class,
                getContainerSetting(Object.class, UnifyCorePropertyConstants.APPLICATION_LAYOUT), null);
        UnifyConfigUtils.resolveConfigurationOverrides(resultByNameMap, categoryList);

        resultByNameMap.putAll(defaultResultMap); // Set result mappings that can
        // not be overridden

        // Set page name bindings
        Map<String, PropertyInfo> pageNamePropertyBindingMap = new HashMap<String, PropertyInfo>();
        pageNamePropertyBindingMap.putAll(pageManager.getStandalonePanelPropertyBindings(controllerName));
        setIdRequestParameterBindings(typeClass, pageNamePropertyBindingMap);

        return new PageControllerInfo(controllerName, actionByNameMap, resultByNameMap, pageNamePropertyBindingMap);
    }

    private RemoteCallControllerInfo createRemoteCallControllerInfo(String controllerName) throws UnifyException {
        Class<? extends RemoteCallController> typeClass = getComponentType(RemoteCallController.class, controllerName);

        // Get gate if present
        String gateName = null;
        Gateway ga = typeClass.getAnnotation(Gateway.class);
        if (ga != null) {
            gateName = ga.value();
        }

        // Process remote call handlers
        Map<String, RemoteCallHandler> remoteCallHandlerMap = new HashMap<String, RemoteCallHandler>();
        Method[] methods = typeClass.getMethods();
        for (Method method : methods) {
            com.tcdng.unify.web.annotation.RemoteAction goa =
                    method.getAnnotation(com.tcdng.unify.web.annotation.RemoteAction.class);
            if (goa != null) {
                if (RemoteCallResult.class.isAssignableFrom(method.getReturnType())
                        && method.getParameterTypes().length == 1
                        && RemoteCallParams.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    remoteCallHandlerMap.put(controllerName + '/' + method.getName(),
                            new RemoteCallHandler(goa.name(), method, goa.restricted()));
                } else {
                    throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_INVALID_REMOTECALL_HANDLER_SIGNATURE,
                            controllerName, method.getName());
                }
            }
        }
        return new RemoteCallControllerInfo(controllerName, gateName, remoteCallHandlerMap);
    }

    private ResourceControllerInfo createResourceControllerInfo(String controllerName) throws UnifyException {
        Class<? extends ResourceController> resourceControllerClass =
                getComponentType(ResourceController.class, controllerName);
        Map<String, PropertyInfo> propertyBindingMap = new HashMap<String, PropertyInfo>();
        setIdRequestParameterBindings(resourceControllerClass, propertyBindingMap);
        return new ResourceControllerInfo(controllerName, propertyBindingMap);
    }

    private void writeResponse(ResponseWriter writer, Page page, Result result) throws UnifyException {
        if (MimeType.APPLICATION_JSON.equals(result.getMimeType())) {
            writer.write("{\"jsonResp\":[");
            boolean appendSym = false;
            for (PageControllerResponse pageControllerResponse : result.getResponses()) {
                if (appendSym) {
                    writer.write(',');
                } else {
                    appendSym = true;
                }
                pageControllerResponse.generate(writer, page);
            }
            writer.write("]");
            if (requestContextUtil.isRemoteViewer()) {
                writer.write(",\"remoteView\":{");
                writer.write("\"view\":\"").write(requestContextUtil.getRemoteViewer()).write("\"}");
            }
            writer.write("}");
        } else {
            for (PageControllerResponse pageControllerResponse : result.getResponses()) {
                pageControllerResponse.generate(writer, page);
            }
        }
    }

    private DataTransfer prepareDataTransfer(UIController uiController, ClientRequest request) throws UnifyException {
        Class<?> validationClass = null;
        Class<?> validationIdClass = null;
        String actionId = (String) request.getParameter(RequestParameterConstants.VALIDATION_ACTION);
        Map<String, DataTransferBlock> transferBlocks = null;
        UIControllerInfo uiControllerInfo = null;
        if (ControllerType.RESOURCE_CONTROLLER.equals(uiController.getType())) {
            uiControllerInfo = resourceControllerInfoMap.get(uiController.getName());
        } else {
            uiControllerInfo = pageControllerInfoMap.get(uiController.getName());
            Page page = requestContextUtil.getRequestPage();
            validationClass = (Class<?>) page.getAttribute("validationClass");
            validationIdClass = (Class<?>) page.getAttribute("validationIdClass");
        }

        for (String transferId : request.getParameterNames()) {
            if (skipOnPopulateSet.contains(transferId)) {
                continue;
            }

            Object values = request.getParameter(transferId);
            if (RequestParameterConstants.REFRESH.equals(transferId)) {
                String[] strings = null;
                if (values instanceof String[]) {
                    strings = (String[]) values;
                } else {
                    strings = new String[] { (String) values };
                }

                for (int i = 0; i < strings.length; i++) {
                    strings[i] = pageManager.getLongName(strings[i]);
                }
                requestContextUtil.setResponseRefreshPanels(strings);
                continue;
            }

            if (RequestParameterConstants.COMMAND.equals(transferId)) {
                if (!(values instanceof String)) {
                    throw new UnifyException(UnifyWebErrorConstants.MULTIPLE_COMMAND_PARAMETERS_IN_REQUEST);
                }

                String[] commandElements = ((String) values).split("->");
                DataTransferBlock transferBlock = DataTransferUtils.createTransferBlock(commandElements[0]);
                String parentLongName = pageManager.getLongName(transferBlock.getId());
                RequestCommand requestCommand = new RequestCommand(transferBlock, parentLongName, commandElements[1]);
                requestContextUtil.setRequestCommand(requestCommand);
                continue;
            }

            if (transferBlocks == null) {
                transferBlocks = new HashMap<String, DataTransferBlock>();
            }

            DataTransferHeader header = new DataTransferHeader(values);
            DataTransferBlock transferBlock = DataTransferUtils.createTransferBlock(transferId, header);
            String id = transferBlock.getId();
            header.setLongName(pageManager.getLongName(id));
            header.setBindingInfo(uiControllerInfo.getPropertyInfo(id));

            DataTransferBlock eldestBlock = transferBlocks.get(id);
            if (eldestBlock == null) {
                transferBlocks.put(id, transferBlock);
            } else {
                transferBlock.setSiblingBlock(eldestBlock.getSiblingBlock());
                eldestBlock.setSiblingBlock(transferBlock);
            }
        }

        return new DataTransfer(validationClass, validationIdClass, actionId, transferBlocks);
    }

    private void setIdRequestParameterBindings(Class<? extends Controller> controllerClass,
            Map<String, PropertyInfo> propertyBindingMap) throws UnifyException {
        // Request parameter bindings
        List<Class<?>> classHeirachyList = ReflectUtils.getClassHierachyList(controllerClass);
        for (Class<?> clazz : classHeirachyList) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(RequestParameter.class)) {
                    String fieldName = field.getName();
                    propertyBindingMap.put(pageManager.getPageName(field.getName()),
                            new PropertyInfo(fieldName, fieldName, fieldName, false));
                }
            }
        }
    }
}
