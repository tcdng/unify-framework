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

package com.tcdng.unify.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Abstract plain output stream controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractPlainByteArrayOutputStreamController extends AbstractPlainOutputStreamController {

    private String disposition;

    public AbstractPlainByteArrayOutputStreamController(MimeType mimeType, String disposition) {
        super(mimeType);
        this.disposition = disposition;
    }

    public AbstractPlainByteArrayOutputStreamController() {

    }

    @Override
    public void doProcess(ClientRequest request, ClientResponse response) throws UnifyException {
        try {
            response.setContentType(getContentType());
            if (StringUtils.isNotBlank(disposition)) {
                response.setMetaData("Content-Disposition", disposition);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doExecute(baos, request);
            baos.flush();
            byte[] data = baos.toByteArray();

            response.setMetaData("Content-Length", String.valueOf(data.length));
            response.getOutputStream().write(data);
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
    }
}
