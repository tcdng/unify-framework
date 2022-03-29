/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.stream.JsonObjectStreamer;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.remotecall.RemoteCallFormat;

/**
 * Abstract plain JSON controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractPlainJsonController extends AbstractPlainController {

    @Configurable
    private JsonObjectStreamer jsonObjectStreamer;

    public void setJsonObjectStreamer(JsonObjectStreamer jsonObjectStreamer) {
        this.jsonObjectStreamer = jsonObjectStreamer;
    }

    @Override
    public void doProcess(ClientRequest request, ClientResponse response) throws UnifyException {
        RemoteCallFormat remoteCallFormat = (RemoteCallFormat) request
                .getParameter(RequestParameterConstants.REMOTE_CALL_FORMAT);
        if (!RemoteCallFormat.JSON.equals(remoteCallFormat)) {
            throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_MESSAGE_FORMAT_NOT_MATCH_EXPECTED,
                    remoteCallFormat, RemoteCallFormat.JSON, getName());
        }

        response.setContentType(RemoteCallFormat.JSON.mimeType().template());
        String jsonRequest = (String) request.getParameter(RequestParameterConstants.REMOTE_CALL_BODY);
        jsonObjectStreamer.marshal(doExecute(jsonRequest), response.getWriter());
    }

    protected <T> T getObjectFromRequestJson(Class<T> jsonType, String json) throws UnifyException {
        return jsonObjectStreamer.unmarshal(jsonType, json);
    }

    protected abstract Object doExecute(String jsonRequest) throws UnifyException;
}
