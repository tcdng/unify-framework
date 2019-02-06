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
package com.tcdng.unify.web;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
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
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.ContentTypeConstants;
import com.tcdng.unify.core.constant.LocaleType;
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
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.SystemInfoConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.BindingInfo;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.PageAction;
import com.tcdng.unify.web.ui.PageManager;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.ResponseWriterPool;

/**
 * Abstract controller manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractControllerManager extends AbstractUnifyComponent implements ControllerManager {

    @Configurable
    private PageManager pageManager;

    @Configurable
    private RequestContextUtil requestContextUtil;

    @Configurable
    private ResponseWriterPool responseWriterPool;

    @Configurable
    private XMLObjectStreamer xmlObjectStreamer;

    @Configurable
    private JSONObjectStreamer jsonObjectStreamer;

    private Map<String, String> actionToControllerNameMap;

    private Map<String, Result> defaultResultMap;

    private FactoryMap<Class<? extends PageController>, PageControllerActionInfo> pageControllerActionInfoMap;

    private FactoryMap<String, PageControllerInfo> pageControllerInfoMap;

    private FactoryMap<String, RemoteCallControllerInfo> remoteCallControllerInfoMap;

    private FactoryMap<String, ResourceControllerInfo> resourceControllerInfoMap;

    private PageControllerResponse hintUserResponse;

    private PageControllerResponse refreshMenuResponse;

    private Set<String> skipOnPopulateSet;

    private String commonUtilitiesControllerName;

    public AbstractControllerManager() {
        actionToControllerNameMap = new ConcurrentHashMap<String, String>();
        defaultResultMap = new HashMap<String, Result>();
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
                PageControllerInfo pageControllerInfo = createPageControllerInfo(controllerName);
                for (String actionName : pageControllerInfo.getActionNames()) {
                    actionToControllerNameMap.put(actionName, controllerName);
                }
                return pageControllerInfo;
            }
        };

        remoteCallControllerInfoMap = new FactoryMap<String, RemoteCallControllerInfo>() {
            @Override
            protected RemoteCallControllerInfo create(String controllerName, Object... params) throws Exception {
                RemoteCallControllerInfo remoteCallControllerInfo = createRemoteCallControllerInfo(controllerName);
                for (String handlerName : remoteCallControllerInfo.getRemoteHandlerNames()) {
                    actionToControllerNameMap.put(handlerName, controllerName);
                }
                return remoteCallControllerInfo;
            }
        };

        resourceControllerInfoMap = new FactoryMap<String, ResourceControllerInfo>() {
            @Override
            protected ResourceControllerInfo create(String controllerName, Object... params) throws Exception {
                ResourceControllerInfo resourceControllerInfo = createResourceControllerInfo(controllerName);
                actionToControllerNameMap.put(controllerName, controllerName);
                return resourceControllerInfo;
            }
        };

        skipOnPopulateSet.add(RequestParameterConstants.DOCUMENT);
        skipOnPopulateSet.add(RequestParameterConstants.TARGET_VALUE);
        skipOnPopulateSet.add(RequestParameterConstants.VALIDATION_ACTION);
        skipOnPopulateSet.add(RequestParameterConstants.CONFIRM_MSG);
        skipOnPopulateSet.add(RequestParameterConstants.CONFIRM_PARAM);

        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_ROLECD);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_SESSION_ID);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_USERLOGINID);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_USERNAME);
        skipOnPopulateSet.add(RequestParameterConstants.REMOTE_VIEWER);
    }

    @Override
    public PageControllerInfo getPageControllerInfo(String controllerName) throws UnifyException {
        return pageControllerInfoMap.get(controllerName);
    }

    @Override
    public void updatePageControllerInfo(String controllerName, String standalonePanelName) throws UnifyException {
        PageControllerInfo pbbi = getPageControllerInfo(controllerName);
        Map<String, BindingInfo> bindings = pageManager.getStandalonePanelPropertyBindings(standalonePanelName);
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
    public Controller getController(String path) throws UnifyException {
        logDebug("Obtaining controller with name [{0}]", path);
        PathParts pathParts = analyzePath(path);

        SessionContext sessionContext = getSessionContext();
        Controller controller = (Controller) sessionContext.getAttribute(pathParts.getBeanId());
        if (controller == null) {
            synchronized (sessionContext) {
                controller = (Controller) sessionContext.getAttribute(pathParts.getBeanId());
                if (controller == null) {
                    UnifyComponentConfig unifyComponentConfig =
                            getComponentConfig(Controller.class, pathParts.getActBeanName());

                    if (unifyComponentConfig == null) {
                        // May be a real path request
                        File file = new File(IOUtils.buildFilename(getUnifyComponentContext().getWorkingPath(), path));
                        if (file.exists()) {
                            ResourceController realPathResourceController =
                                    (ResourceController) this.getComponent("/resource/realpath");
                            realPathResourceController.setResourceName(path);
                            return realPathResourceController;
                        }
                    }

                    if (unifyComponentConfig == null) {
                        throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_UNKNOWN_COMP, path);
                    }

                    controller = (Controller) getComponent(pathParts.getActBeanName());

                    if (ControllerType.PAGE_CONTROLLER.equals(controller.getType())) {
                        PageController pageController = (PageController) controller;
                        Page page = pageManager.createPage(sessionContext.getLocale(), pathParts.getActBeanName());
                        page.setSessionId(pathParts.getBeanId());
                        pageController.setPage(page);
                        sessionContext.setAttribute(pathParts.getBeanId(), controller);
                    } else if (ControllerType.REMOTECALL_CONTROLLER.equals(controller.getType())) {
                        sessionContext.setAttribute(pathParts.getActBeanName(), controller);
                    }
                }
            }
        }
        logDebug("Controller with name [{0}] retrieved", pathParts.getActBeanName());
        return controller;
    }

    @Override
    public void executeController(ClientRequest request, ClientResponse response) throws UnifyException {
        Controller controller = null;
        try {
            controller = getController(request.getPath());
            if (controller.isUserInterface()) {
                String documentPath = (String) request.getParameter(RequestParameterConstants.DOCUMENT);
                if (documentPath != null) {
                    PageController docPageController = (PageController) getController(documentPath);
                    requestContextUtil.setRequestDocumentController(docPageController);
                }

                requestContextUtil.extractRequestParameters(request);
            }

            SessionContext sessionContext = getSessionContext();
            if (controller.isSecured() && !sessionContext.isUserLoggedIn()) {
                String forceLogout = (String) sessionContext.removeAttribute(UnifyWebSessionAttributeConstants.FORCE_LOGOUT);
                if (forceLogout != null) {
                    throw new UnifyException(SystemUtils.getSessionAttributeErrorCode(forceLogout), request.getPath());
                }

                throw new UnifyException(UnifyWebErrorConstants.LOGIN_REQUIRED, request.getPath());
            }

            if (ControllerType.REMOTECALL_CONTROLLER.equals(controller.getType())) {
                RemoteCallController remoteCallController = (RemoteCallController) controller;
                String reqBody = (String) request.getParameter(RequestParameterConstants.REMOTE_CALL_BODY);
                RemoteCallFormat remoteCallFormat =
                        (RemoteCallFormat) request.getParameter(RequestParameterConstants.REMOTE_CALL_FORMAT);
                String responseString =
                        executeRemoteCall(remoteCallController, remoteCallFormat, request.getPath(), reqBody);
                response.setContentType(remoteCallFormat.getContentType());
                if (request.getCharset() != null) {
                    response.setCharacterEncoding(request.getCharset().name());
                }

                response.getWriter().write(responseString);
                response.getWriter().flush();
            } else if (ControllerType.RESOURCE_CONTROLLER.equals(controller.getType())) {
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
                response.getOutputStream().flush();
            } else {
                ResponseWriter writer = responseWriterPool.getResponseWriter();
                try {
                    PageController pageController = (PageController) controller;
                    PageControllerInfo pbbInfo = pageControllerInfoMap.get(pageController.getName());
                    requestContextUtil.setRequestPage(pageController.getPage());
                    String resultName = ResultMappingConstants.VALIDATION_ERROR;

                    DataTransfer dataTransfer = prepareDataTransfer(pageController, request);
                    if (validate(pageController, dataTransfer)) {
                        synchronized (pageController) {
                            populate(pageController, dataTransfer);
                            resultName = executePageCall(pageController, request.getPath());
                        }

                        // Check if action result needs to be routed to containing
                        // document controller
                        if (!pbbInfo.hasResultWithName(resultName) && !pageController.getPage().isDocument()) {
                            PageController docPageController = requestContextUtil.getRequestDocumentController();
                            if (docPageController != null) {
                                pageController = docPageController;
                                pbbInfo = pageControllerInfoMap.get(pageController.getName());
                            }
                        }

                        // Route to common utilities if necessary
                        if (!pbbInfo.hasResultWithName(resultName)) {
                            pageController = (PageController) getController(commonUtilitiesControllerName);
                            pbbInfo = pageControllerInfoMap.get(commonUtilitiesControllerName);
                        }
                    }

                    requestContextUtil.setResponsePageControllerInfo(
                            new ControllerResponseInfo(pageController.getName(), pageController.getSessionId()));
                    Result result = pbbInfo.getResult(resultName);
                    writeResponse(writer, pageController, result);

                    // logDebug("Page controller response: response = [{0}]", responseString);
                    response.setContentType(result.getContentType());
                    if (request.getCharset() != null) {
                        response.setCharacterEncoding(request.getCharset().name());
                    }

                    writer.writeTo(response.getWriter());
                    response.getWriter().flush();
                } finally {
                    responseWriterPool.restore(writer);
                }
            }
        } catch (Exception e) {
            boolean isUserInterface = false;
            if (controller != null) {
                isUserInterface = controller.isUserInterface();
            } else {
                isUserInterface = request.getParameter(RequestParameterConstants.REMOTE_CALL_BODY) == null;
            }

            if (isUserInterface) {
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
    public void populateController(String controllerName, String property, Object value) throws UnifyException {
        Controller controller = getController(controllerName);
        DataUtils.setNestedBeanProperty(controller, property, value);
    }

    @Override
    protected void onInitialize() throws UnifyException {
        Locale defaultLocale = Locale.getDefault();
        commonUtilitiesControllerName =
                getContainerSetting(String.class, UnifyWebPropertyConstants.APPLICATION_COMMON_UTILITIES,
                        ReservedPageControllerConstants.COMMONUTILITIES);

        hintUserResponse = (PageControllerResponse) getUplComponent(defaultLocale, "!hintuserresponse", false);

        refreshMenuResponse = (PageControllerResponse) getUplComponent(defaultLocale, "!refreshmenuresponse", false);

        defaultResultMap.put(ResultMappingConstants.NONE,
                new Result(new PageControllerResponse[] { hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.INDEX,
                new Result(ContentTypeConstants.TEXT_HTML, new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!loaddocumentresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.OPEN,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!loadcontentresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.SAVE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!postresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.CLOSE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!unloadcontentresponse", false),
                        hintUserResponse, refreshMenuResponse }));

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
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected PageManager getPageManager() {
        return pageManager;
    }

    protected String getControllerName(String actionPath) throws UnifyException {
        return actionToControllerNameMap.get(actionPath);
    }

    protected void populate(UserInterfaceController uiController, DataTransfer dataTransfer) throws UnifyException {
        if (!uiController.isReadOnly()) {
            logDebug("Populating controller [{0}]", uiController.getName());

            // Reset first
            uiController.reset();

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

    protected boolean validate(PageController pageController, DataTransfer dataTransfer) throws UnifyException {
        Page page = pageController.getPage();
        boolean successful = true;
        if (page.isValidationEnabled()) {
            String actionId = dataTransfer.getActionId();
            logDebug("Page validation is enabled. actionId = [{0}]", actionId);

            if (!StringUtils.isBlank(actionId)) {
                logDebug("Performing request parameter validation. Controller [{0}]", pageController.getName());

                // Do validations
                PageAction pageAction = page.getPageAction(pageManager.getLongName(actionId));
                UplElementReferences uer = pageAction.getUplAttribute(UplElementReferences.class, "validations");
                for (String validationLongName : uer.getLongNames()) {
                    logDebug("Applying validation [{0}]...", validationLongName);
                    successful &= page.getPageWidgetValidator(pageManager, validationLongName).validate(dataTransfer);
                }

                logDebug("Request parameter validation completed. Controller [{0}]", pageController.getName());
            }
        }

        return successful;
    }

    protected String executePageCall(PageController pageController, String action) throws UnifyException {
        try {
            return (String) pageControllerInfoMap.get(pageController.getName()).getAction(action).getMethod()
                    .invoke(pageController);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return null;
    }

    protected String executeRemoteCall(RemoteCallController remoteCallController, RemoteCallFormat remoteCallFormat,
            String remoteHandler, String remoteParam) throws UnifyException {
        String respObj = null;
        String methodCode = null;
        ObjectStreamer streamer = jsonObjectStreamer;
        if (RemoteCallFormat.XML.equals(remoteCallFormat)) {
            streamer = xmlObjectStreamer;
        }

        RemoteCallHandler handler = null;
        try {
            RemoteCallControllerInfo rbbInfo = remoteCallControllerInfoMap.get(remoteCallController.getName());
            handler = rbbInfo.getRemoteCallHandler(remoteHandler);
            RemoteCallParams param = streamer.unmarshal(handler.getParamType(), remoteParam);
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
            PageController pageController = null;
            Result result = null;
            if (StringUtils.isBlank((String) request.getParameter(RequestParameterConstants.DOCUMENT))
                    && !requestContextUtil.isRemoteViewer()) {
                pageController = (PageController) getController(SystemInfoConstants.UNAUTHORISED_CONTROLLER_NAME);
                result = pageControllerInfoMap.get(pageController.getName()).getResult(ResultMappingConstants.INDEX);
            } else {
                pageController = (PageController) getController(SystemInfoConstants.SYSTEMINFO_CONTROLLER_NAME);
                pageController.getPage().getWidgetByShortName("stackTrace").setVisible(!loginRequired);
                result = pageControllerInfoMap.get(pageController.getName())
                        .getResult(SystemInfoConstants.SHOW_SYSTEM_EXCEPTION_MAPPING);
            }

            requestContextUtil.setResponsePageControllerInfo(
                    new ControllerResponseInfo(pageController.getName(), pageController.getSessionId()));
            writeResponse(writer, pageController, result);

            response.setContentType(result.getContentType());
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

    private boolean isActionHandlerSignature(Method method) throws UnifyException {
        return String.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0
                && method.getExceptionTypes().length == 1
                && UnifyException.class.isAssignableFrom(method.getExceptionTypes()[0]);
    }

    @SuppressWarnings("unchecked")
    private boolean isSuperCommandMethod(Class<? extends PageController> pageControllerClass, String methodName)
            throws UnifyException {
        Class<?> clazz = pageControllerClass;
        while ((clazz = clazz.getSuperclass()) != null && PageController.class.isAssignableFrom(clazz)) {
            if (pageControllerActionInfoMap.get((Class<? extends PageController>) clazz).isActionMethod(methodName)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private PageControllerInfo createPageControllerInfo(String controllerName) throws UnifyException {
        // Process action handlers
        Map<String, Action> actionMap = new HashMap<String, Action>();
        Class<? extends PageController> typeClass = getComponentType(PageController.class, controllerName);
        for (Method method : pageControllerActionInfoMap.get(typeClass).getActionMethods()) {
            actionMap.put(controllerName + '/' + method.getName(), new Action(method));
        }

        // Set auto routing to index action
        Action action = actionMap.get(controllerName + "/index");
        actionMap.put(controllerName, action);

        // Process result mappings
        Map<String, Result> resultMap = new ConcurrentHashMap<String, Result>();
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
                    resultMap.put(ra.name(),
                            new Result(responses.toArray(new PageControllerResponse[responses.size()])));
                }
            }
        }

        // Resolve category
        List<String> categoryList = DataUtils.convert(ArrayList.class, String.class,
                getContainerSetting(Object.class, UnifyCorePropertyConstants.APPLICATION_LAYOUT), null);
        UnifyConfigUtils.resolveConfigurationOverrides(resultMap, categoryList);

        resultMap.putAll(defaultResultMap); // Set result mappings that can
                                            // not be overriden

        // Set page name bindings
        Map<String, BindingInfo> pageNamePropertyBindingMap = new HashMap<String, BindingInfo>();
        pageNamePropertyBindingMap.putAll(pageManager.getStandalonePanelPropertyBindings(controllerName));
        setIdRequestParameterBindings(typeClass, pageNamePropertyBindingMap);

        return new PageControllerInfo(controllerName, actionMap, resultMap, pageNamePropertyBindingMap);
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
            com.tcdng.unify.web.annotation.GatewayAction goa =
                    method.getAnnotation(com.tcdng.unify.web.annotation.GatewayAction.class);
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
        Map<String, BindingInfo> propertyBindingMap = new HashMap<String, BindingInfo>();
        setIdRequestParameterBindings(resourceControllerClass, propertyBindingMap);
        return new ResourceControllerInfo(controllerName, propertyBindingMap);
    }

    private void writeResponse(ResponseWriter writer, PageController pageController, Result result)
            throws UnifyException {
        if (ContentTypeConstants.APPLICATION_JSON.equals(result.getContentType())) {
            writer.write("{\"jsonResp\":[");
            boolean appendSym = false;
            for (PageControllerResponse pageControllerResponse : result.getResponses()) {
                if (appendSym) {
                    writer.write(',');
                } else {
                    appendSym = true;
                }
                pageControllerResponse.generate(writer, pageController);
            }
            writer.write("]");
            if (requestContextUtil.isRemoteViewer()) {
                writer.write(",\"remoteView\":{");
                writer.write("\"view\":\"").write(requestContextUtil.getRemoteViewer()).write("\",\"sessionID\":\"")
                        .write(getSessionContext().getId()).write("\"}");
            }
            writer.write("}");
        } else {
            for (PageControllerResponse pageControllerResponse : result.getResponses()) {
                pageControllerResponse.generate(writer, pageController);
            }
        }
    }

    private DataTransfer prepareDataTransfer(UserInterfaceController uiController, ClientRequest request)
            throws UnifyException {
        Class<?> validationClass = null;
        Class<?> validationIdClass = null;
        String actionId = (String) request.getParameter(RequestParameterConstants.VALIDATION_ACTION);
        Map<String, DataTransferBlock> transferBlocks = null;
        UserInterfaceControllerInfo uiControllerInfo = null;
        if (ControllerType.RESOURCE_CONTROLLER.equals(uiController.getType())) {
            uiControllerInfo = resourceControllerInfoMap.get(uiController.getName());
        } else {
            uiControllerInfo = pageControllerInfoMap.get(uiController.getName());
            Page page = ((PageController) uiController).getPage();
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
                    strings[i] = getPageManager().getLongName(strings[i]);
                }
                requestContextUtil.setResponseRefreshPanels(strings);
                continue;
            }

            if (RequestParameterConstants.COMMAND.equals(transferId)) {
                if (!(values instanceof String)) {
                    throw new UnifyException(UnifyWebErrorConstants.MULTIPLE_COMMAND_PARAMETERS_IN_REQUEST);
                }

                String[] commandElements = ((String) values).split("->");
                RequestCommand requestCommand =
                        new RequestCommand(getPageManager().getLongName(commandElements[0]), commandElements[1]);
                requestContextUtil.setRequestCommand(requestCommand);
                continue;
            }

            if (transferBlocks == null) {
                transferBlocks = new HashMap<String, DataTransferBlock>();
            }

            DataTransferHeader header = new DataTransferHeader(values);
            DataTransferBlock transferBlock = createTransferBlock(transferId, header);
            String id = transferBlock.getId();
            header.setLongName(pageManager.getLongName(id));
            header.setBindingInfo(uiControllerInfo.getBindingInfo(id));

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

    private DataTransferBlock createTransferBlock(String transferId, DataTransferHeader header) {
        DataTransferBlock transferBlock = null;

        String id = transferId;
        int index = 0;
        do {
            int itemIndex = -1;
            index = id.lastIndexOf('d');
            if (index > 0) {
                itemIndex = Integer.parseInt(id.substring(index + 1));
                id = id.substring(0, index);
            }

            transferBlock = new DataTransferBlock(header, id, itemIndex, transferBlock);
            index = id.lastIndexOf('.');
            if (index > 0) {
                id = id.substring(0, index);
            }
        } while (index > 0);
        return transferBlock;
    }

    private void setIdRequestParameterBindings(Class<? extends Controller> controllerClass,
            Map<String, BindingInfo> propertyBindingMap) throws UnifyException {
        // Request parameter bindings
        List<Class<?>> classHeirachyList = ReflectUtils.getClassHierachyList(controllerClass);
        for (Class<?> clazz : classHeirachyList) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(RequestParameter.class)) {
                    String fieldName = field.getName();
                    propertyBindingMap.put(pageManager.getPageName(field.getName()),
                            new BindingInfo(fieldName, fieldName, fieldName, false));
                }
            }
        }
    }

    private PathParts analyzePath(String path) throws UnifyException {
        String actBeanName = actionToControllerNameMap.get(path);
        if (actBeanName != null) {
            return new PathParts(actBeanName, actBeanName);
        }

        int colIndex = path.lastIndexOf(':');
        if (colIndex >= 0) {
            String beanId = null;
            actBeanName = path.substring(0, colIndex);

            int slashIndex = path.lastIndexOf('/');
            if (slashIndex > colIndex) {
                beanId = path.substring(0, slashIndex);
            } else {
                beanId = path;
            }

            return new PathParts(actBeanName, beanId);
        } else {
            if (getComponentConfig(Controller.class, path) == null) {
                int slashIndex = path.lastIndexOf('/');
                if (slashIndex > 0) {
                    actBeanName = path.substring(0, slashIndex);
                    return new PathParts(actBeanName, actBeanName);
                }
            }
        }
        return new PathParts(path, path);
    }

    private class PathParts {

        private String actBeanName;

        private String beanId;

        public PathParts(String actBeanName, String beanId) {
            this.actBeanName = actBeanName;
            this.beanId = beanId;
        }

        public String getActBeanName() {
            return actBeanName;
        }

        public String getBeanId() {
            return beanId;
        }
    }
}
