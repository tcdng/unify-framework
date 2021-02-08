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
package com.tcdng.unify.web.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.SystemUtils;
import com.tcdng.unify.web.AbstractController;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ClientResponse;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.PathInfoRepository;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.constant.ReadOnly;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.ResetOnWrite;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.constant.SystemInfoConstants;
import com.tcdng.unify.web.ui.constant.PageRequestParameterConstants;
import com.tcdng.unify.web.ui.util.DataTransferUtils;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.ResponseWriterPool;

/**
 * Convenient base class for user interface controllers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractUIController extends AbstractController implements UIController {

    private static final Set<String> skipOnPopulateSet = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            PageRequestParameterConstants.DOCUMENT,
            PageRequestParameterConstants.TARGET_VALUE,
            PageRequestParameterConstants.WINDOW_NAME,
            PageRequestParameterConstants.VALIDATION_ACTION,
            PageRequestParameterConstants.CONFIRM_MSG,
            PageRequestParameterConstants.CONFIRM_MSGICON,
            PageRequestParameterConstants.CONFIRM_PARAM,
            RequestParameterConstants.REMOTE_VIEWER,
            RequestParameterConstants.REMOTE_ROLECD,
            RequestParameterConstants.REMOTE_SESSION_ID,
            RequestParameterConstants.REMOTE_USERLOGINID,
            RequestParameterConstants.REMOTE_USERNAME,
            RequestParameterConstants.REMOTE_BRANCH_CODE,
            RequestParameterConstants.REMOTE_ZONE_CODE,
            RequestParameterConstants.REMOTE_GLOBAL_ACCESS,
            RequestParameterConstants.REMOTE_COLOR_SCHEME,
            RequestParameterConstants.REMOTE_TENANT_CODE)));

    @Configurable
    private UIControllerUtil uiControllerUtil;

    @Configurable
    private PageRequestContextUtil pageRequestContextUtil;

    @Configurable
    private PageManager pageManager;

    @Configurable
    private PathInfoRepository pathInfoRepository;

    @Configurable
    private ResponseWriterPool responseWriterPool;

    private boolean readOnly;

    private boolean resetOnWrite;

    public AbstractUIController(Secured secured, ReadOnly readOnly, ResetOnWrite resetOnWrite) {
        super(secured);
        this.readOnly = readOnly.isTrue();
        this.resetOnWrite = resetOnWrite.isTrue();
    }

    @Override
    public final void process(ClientRequest request, ClientResponse response) throws UnifyException {
        try {
            final ControllerPathParts reqPathParts = request.getRequestPathParts().getControllerPathParts();
            PageController<?> docPageController = null;
            ControllerPathParts docPathParts = null;
            String documentPath = (String) request.getParameter(PageRequestParameterConstants.DOCUMENT);
            if (documentPath != null) {
                docPathParts = pathInfoRepository.getControllerPathParts(documentPath);
                docPageController = (PageController<?>) getControllerFinder().findController(docPathParts);
                pageRequestContextUtil.setRequestDocument((Document) uiControllerUtil.loadRequestPage(docPathParts));
            }

            pageRequestContextUtil.extractRequestParameters(request);
            
            ensureSecureAccess(reqPathParts, pageRequestContextUtil.isRemoteViewer());
            doProcess(request, response, docPageController, docPathParts);
        } catch (Exception e) {
            e.printStackTrace();
            writeExceptionResponse(request, response, e);
        } finally {
            response.close();
        }
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public boolean isResetOnWrite() {
        return resetOnWrite;
    }

    protected PageRequestContextUtil getPageRequestContextUtil() throws UnifyException {
        return pageRequestContextUtil;
    }

    protected PageManager getPageManager() {
        return pageManager;
    }

    protected PathInfoRepository getPathInfoRepository() {
        return pathInfoRepository;
    }

    protected ResponseWriterPool getResponseWriterPool() {
        return responseWriterPool;
    }

    protected UIControllerUtil getUIControllerUtil() {
        return uiControllerUtil;
    }

    protected abstract DataTransferParam getDataTransferParam() throws UnifyException;

    protected abstract void doProcess(ClientRequest request, ClientResponse response,
            PageController<?> docPageController, ControllerPathParts docPathParts) throws UnifyException;
    
    protected class DataTransferParam {

        private UIControllerInfo uiControllerInfo;

        private Class<?> validationClass;

        private Class<?> validationIdClass;

        public DataTransferParam(UIControllerInfo uiControllerInfo, Class<?> validationClass,
                Class<?> validationIdClass) {
            this.uiControllerInfo = uiControllerInfo;
            this.validationClass = validationClass;
            this.validationIdClass = validationIdClass;
        }

        public DataTransferParam(UIControllerInfo uiControllerInfo) {
            this.uiControllerInfo = uiControllerInfo;
        }

        public UIControllerInfo getUIControllerInfo() {
            return uiControllerInfo;
        }

        public Class<?> getValidationClass() {
            return validationClass;
        }

        public Class<?> getValidationIdClass() {
            return validationIdClass;
        }
    }

    protected DataTransfer prepareDataTransfer(ClientRequest request) throws UnifyException {
        String actionId = (String) request.getParameter(PageRequestParameterConstants.VALIDATION_ACTION);
        Map<String, DataTransferBlock> transferBlocks = null;
        DataTransferParam dataTransferParam = getDataTransferParam();

        for (String transferId : request.getParameterNames()) {
            if (skipOnPopulateSet.contains(transferId)) {
                continue;
            }

            Object values = request.getParameter(transferId);
            if (PageRequestParameterConstants.REFRESH.equals(transferId)) {
                String[] strings = null;
                if (values instanceof String[]) {
                    strings = (String[]) values;
                } else {
                    strings = new String[] { (String) values };
                }

                for (int i = 0; i < strings.length; i++) {
                    strings[i] = pageManager.getLongName(strings[i]);
                }
                pageRequestContextUtil.setResponseRefreshPanels(strings);
                continue;
            }

            if (PageRequestParameterConstants.COMMAND.equals(transferId)) {
                if (!(values instanceof String)) {
                    throw new UnifyException(UnifyWebUIErrorConstants.MULTIPLE_COMMAND_PARAMETERS_IN_REQUEST);
                }

                String[] commandElements = ((String) values).split("->");
                DataTransferBlock transferBlock = DataTransferUtils.createTransferBlock(commandElements[0]);
                String parentLongName = pageManager.getLongName(transferBlock.getId());
                RequestCommand requestCommand = new RequestCommand(transferBlock, parentLongName, commandElements[1]);
                pageRequestContextUtil.setRequestCommand(requestCommand);
                continue;
            }

            if (PageRequestParameterConstants.COMMAND_TAG.equals(transferId)) {
                if (!(values instanceof String)) {
                    throw new UnifyException(UnifyWebUIErrorConstants.MULTIPLE_COMMAND_PARAMETERS_IN_REQUEST);
                }

                pageRequestContextUtil.setRequestCommandTag((String) values);
                continue;
            }

            if (transferBlocks == null) {
                transferBlocks = new HashMap<String, DataTransferBlock>();
            }

            DataTransferHeader header = new DataTransferHeader(values);
            DataTransferBlock transferBlock = DataTransferUtils.createTransferBlock(transferId, header);
            String id = transferBlock.getId();
            header.setLongName(pageManager.getLongName(id));
            header.setBindingInfo(dataTransferParam.getUIControllerInfo().getPropertyInfo(id));

            DataTransferBlock eldestBlock = transferBlocks.get(id);
            if (eldestBlock == null) {
                transferBlocks.put(id, transferBlock);
            } else {
                transferBlock.setSiblingBlock(eldestBlock.getSiblingBlock());
                eldestBlock.setSiblingBlock(transferBlock);
            }
        }

        return new DataTransfer(dataTransferParam.getValidationClass(), dataTransferParam.getValidationIdClass(),
                actionId, transferBlocks);
    }
    
    protected void populate(DataTransfer dataTransfer) throws UnifyException {
        if (!isReadOnly()) {
            logDebug("Populating controller [{0}]", getName());

            // Reset first
            if (isResetOnWrite()) {
                reset();
            }

            // Populate controller
            for (DataTransferBlock dataTransferBlock : dataTransfer.getDataTransferBlocks()) {
                do {
                    logDebug("Populating widget [{0}] with value [{1}] using transfer block [{2}]...",
                            dataTransferBlock.getLongName(), dataTransferBlock.getDebugValue(), dataTransferBlock);
                    populate(dataTransferBlock);
                    dataTransferBlock = dataTransferBlock.getSiblingBlock();
                } while (dataTransferBlock != null);
            }

            logDebug("Controller population completed [{0}]", getName());
        }
    }

    protected void writeResponse(ResponseWriter writer, Page page, Result result) throws UnifyException {
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
            if (pageRequestContextUtil.isRemoteViewer()) {
                writer.write(",\"remoteView\":{");
                writer.write("\"view\":\"").write(pageRequestContextUtil.getRemoteViewer()).write("\"}");
            }
            writer.write("}");
        } else {
            for (PageControllerResponse pageControllerResponse : result.getResponses()) {
                pageControllerResponse.generate(writer, page);
            }
        }
    }

    private void writeExceptionResponse(ClientRequest request, ClientResponse response, Exception e)
            throws UnifyException {
        logError(e);

        if (response.isOutUsed()) {
            if (e instanceof UnifyException) {
                throw (UnifyException) e;
            } else {
                throw new UnifyOperationException(e);
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
        ResponseWriter writer = responseWriterPool.getResponseWriter(request);
        try {
            PageController<?> pageController = null;
            ControllerPathParts respPathParts = null;
            Page page = null;
            Result result = null;
            if (StringUtils.isBlank((String) request.getParameter(PageRequestParameterConstants.DOCUMENT))
                    && !pageRequestContextUtil.isRemoteViewer()) {
                respPathParts =
                        pathInfoRepository.getControllerPathParts(SystemInfoConstants.UNAUTHORIZED_CONTROLLER_NAME);
                pageController = (PageController<?>) getControllerFinder().findController(respPathParts);
                page = uiControllerUtil.loadRequestPage(respPathParts);
                result = uiControllerUtil.getPageControllerInfo(pageController.getName()).getResult(ResultMappingConstants.INDEX);
            } else {
                respPathParts =
                        pathInfoRepository.getControllerPathParts(SystemInfoConstants.SYSTEMINFO_CONTROLLER_NAME);
                pageController = (PageController<?>) getControllerFinder().findController(respPathParts);
                page = uiControllerUtil.loadRequestPage(respPathParts);
                page.setWidgetVisible("stackTrace", !loginRequired);
                result = uiControllerUtil.getPageControllerInfo(pageController.getName())
                        .getResult(SystemInfoConstants.SHOW_SYSTEM_EXCEPTION_MAPPING);
            }

            pageRequestContextUtil.setResponsePathParts(respPathParts);
            writeResponse(writer, page, result);

            response.setContentType(result.getMimeType().template());
            writer.writeTo(response.getWriter());
        } catch (UnifyException e1) {
            throw e1;
        } catch (Exception e1) {e1.printStackTrace();
            throwOperationErrorException(e1);
        } finally {
            responseWriterPool.restore(writer);
        }
    }

}
