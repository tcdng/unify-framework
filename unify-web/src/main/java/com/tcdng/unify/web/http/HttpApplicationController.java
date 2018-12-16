/*
 * Copyright 2018 The Code Department
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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationController;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ClientResponse;
import com.tcdng.unify.web.ControllerManager;
import com.tcdng.unify.web.RemoteCallFormat;
import com.tcdng.unify.web.UnifyWebPropertyConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;

/**
 * Default HTTP application controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_HTTPCONTROLLER)
public class HttpApplicationController extends AbstractUnifyComponent implements ApplicationController {

    private static final String CONTENT_DISPOSITION = "content-disposition";
    private static final String DISPOSITION_FILENAME = "filename";
    private static final String DISPOSITION_CREATIONDATE = "creation-date";
    private static final String DISPOSITION_MODIFICATIONDATE = "modification-date";

    private static final int BUFFER_SIZE = 4096;

    @Configurable(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER)
    private ControllerManager controllerManager;

    private List<String> remoteViewerList;

    @Override
    public void execute(Object requestObject, Object responseObject) throws UnifyException {
        HttpServletRequest request = (HttpServletRequest) requestObject;
        String resolvedPath = request.getPathInfo();
        if (resolvedPath != null && resolvedPath.endsWith("/")) {
            resolvedPath = resolvedPath.substring(0, resolvedPath.length() - 1);
        }

        if (StringUtils.isBlank(resolvedPath)) {
            resolvedPath = getContainerSetting(String.class, UnifyWebPropertyConstants.APPLICATION_HOME,
                    ReservedPageControllerConstants.DEFAULT_APPLICATION_HOME);
        }

        Charset charset = StandardCharsets.UTF_8;
        if (request.getCharacterEncoding() != null) {
            charset = Charset.forName(request.getCharacterEncoding());
        }

        ClientRequest clientRequest = new HttpClientRequest(resolvedPath, charset,
                extractRequestParameters(request, charset));
        ClientResponse clientResponse = new HttpClientResponse((HttpServletResponse) responseObject);

        if (!remoteViewerList.isEmpty()) {
            String origin = request.getHeader("origin");
            if (remoteViewerList.contains(origin)) {
                HttpServletResponse response = (HttpServletResponse) responseObject;
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type");
                response.setHeader("Access-Control-Max-Age", "600");
            }
        }

        controllerManager.executeController(clientRequest, clientResponse);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitialize() throws UnifyException {
        remoteViewerList = DataUtils.convert(ArrayList.class, String.class,
                getContainerSetting(Object.class, UnifyWebPropertyConstants.APPLICATION_REMOTE_VIEWERS), null);
        if (remoteViewerList == null) {
            remoteViewerList = Collections.emptyList();
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private Map<String, Object> extractRequestParameters(HttpServletRequest request, Charset charset)
            throws UnifyException {
        Map<String, Object> result = new HashMap<String, Object>();
        String contentType = request.getContentType() == null ? null : request.getContentType().toLowerCase();
        RemoteCallFormat remoteCallFormat = RemoteCallFormat.fromContentType(contentType);
        if (remoteCallFormat != null) {
            try {
                String reqBody = new String(IOUtils.readAll(request.getInputStream()), charset);
                result.put(RequestParameterConstants.REMOTE_CALL_BODY, reqBody);
                result.put(RequestParameterConstants.REMOTE_CALL_FORMAT, remoteCallFormat);
            } catch (IOException e) {
                throwOperationErrorException(e);
            }
        } else {
            boolean isFormData = contentType != null && contentType.indexOf("multipart/form-data") >= 0;
            if (isFormData) {
                processParts(result, request);
            } else {
                boolean chkMorsic = true;
                Map<String, String[]> httpRequestParamMap = request.getParameterMap();
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

    private void processParts(Map<String, Object> requestParameterMap, HttpServletRequest request)
            throws UnifyException {
        logDebug("Processing multi-part request parameters [{0}]", requestParameterMap.keySet());
        try {
            Map<String, List<String>> stringMap = new HashMap<String, List<String>>();
            Map<String, List<UploadedFile>> uploadedFileMap = new HashMap<String, List<UploadedFile>>();
            char[] buffer = new char[BUFFER_SIZE];
            boolean chkMorsic = true;
            for (Part part : request.getParts()) {
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
                    requestParameterMap.put(entry.getKey(), list.toArray(new String[list.size()]));
                }
            }

            for (Map.Entry<String, List<UploadedFile>> entry : uploadedFileMap.entrySet()) {
                List<UploadedFile> list = entry.getValue();
                requestParameterMap.put(entry.getKey(), list.toArray(new UploadedFile[list.size()]));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        logDebug("Multi-part request processing completed");
    }

    private ContentDisposition getContentDisposition(Part part) throws UnifyException {
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
