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
package com.tcdng.unify.web;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyError;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.stream.JsonObjectStreamer;
import com.tcdng.unify.core.stream.ObjectStreamer;
import com.tcdng.unify.core.stream.XmlObjectStreamer;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.remotecall.RemoteCallBinaryMessageStreamer;
import com.tcdng.unify.web.remotecall.RemoteCallError;
import com.tcdng.unify.web.remotecall.RemoteCallFormat;
import com.tcdng.unify.web.remotecall.RemoteCallParams;
import com.tcdng.unify.web.remotecall.RemoteCallResult;
import com.tcdng.unify.web.remotecall.RemoteCallXmlMessageStreamer;

/**
 * Convenient base class for remote call controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractRemoteCallController extends AbstractController implements RemoteCallController {

    @Configurable
    private ControllerUtil controllerUtil;
    
    @Configurable
    private XmlObjectStreamer xmlObjectStreamer;

    @Configurable
    private JsonObjectStreamer jsonObjectStreamer;

    @Configurable
    private RemoteCallBinaryMessageStreamer remoteCallBinaryMessageStreamer;

    @Configurable
    private RemoteCallXmlMessageStreamer remoteCallXmlMessageStreamer;

    private Map<RemoteCallFormat, ObjectStreamer> objectStreamers;

    public AbstractRemoteCallController(Secured secured) {
        super(secured);
    }

    public AbstractRemoteCallController() {
        super(Secured.FALSE);
    }

    public void setControllerUtil(ControllerUtil controllerUtil) {
        this.controllerUtil = controllerUtil;
    }

    public void setXmlObjectStreamer(XmlObjectStreamer xmlObjectStreamer) {
        this.xmlObjectStreamer = xmlObjectStreamer;
    }

    public void setJsonObjectStreamer(JsonObjectStreamer jsonObjectStreamer) {
        this.jsonObjectStreamer = jsonObjectStreamer;
    }

    public void setRemoteCallBinaryMessageStreamer(RemoteCallBinaryMessageStreamer remoteCallBinaryMessageStreamer) {
        this.remoteCallBinaryMessageStreamer = remoteCallBinaryMessageStreamer;
    }

    public void setRemoteCallXmlMessageStreamer(RemoteCallXmlMessageStreamer remoteCallXmlMessageStreamer) {
        this.remoteCallXmlMessageStreamer = remoteCallXmlMessageStreamer;
    }

    @Override
    public final void process(ClientRequest request, ClientResponse response) throws UnifyException {
        try {
            final ControllerPathParts reqPathParts = request.getRequestPathParts().getControllerPathParts();
            RemoteCallFormat remoteCallFormat =
                    (RemoteCallFormat) request.getParameter(RequestParameterConstants.REMOTE_CALL_FORMAT);
            Object reqBody = request.getParameter(RequestParameterConstants.REMOTE_CALL_BODY);
            Object respBody = executeRemoteCall(remoteCallFormat,
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
        } catch (Exception e) {
            // TODO
        } finally {
            response.close();
        }
    }

    @Override
    public void ensureContextResources(ControllerPathParts controllerPathParts) throws UnifyException {
        
    }

    @Override
    protected void onInitialize() throws UnifyException {
        objectStreamers = new HashMap<RemoteCallFormat, ObjectStreamer>();
        objectStreamers.put(RemoteCallFormat.JSON, jsonObjectStreamer);
        objectStreamers.put(RemoteCallFormat.XML, xmlObjectStreamer);
        objectStreamers.put(RemoteCallFormat.TAGGED_BINARYMESSAGE, remoteCallBinaryMessageStreamer);
        objectStreamers.put(RemoteCallFormat.TAGGED_XMLMESSAGE, remoteCallXmlMessageStreamer);
        objectStreamers = Collections.unmodifiableMap(objectStreamers);
    }

    protected Object executeRemoteCall(RemoteCallFormat remoteCallFormat,
            String remoteHandler, Object remoteParam) throws UnifyException {
        Object respObj = null;
        String methodCode = null;

        ObjectStreamer streamer = objectStreamers.get(remoteCallFormat);
        RemoteCallHandler handler = null;
        try {
            RemoteCallControllerInfo rbbInfo = controllerUtil.getRemoteCallControllerInfo(getName());
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

            RemoteCallResult result = (RemoteCallResult) handler.getMethod().invoke(this, param);
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

}
