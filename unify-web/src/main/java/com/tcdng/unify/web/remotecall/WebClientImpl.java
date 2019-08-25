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
package com.tcdng.unify.web.remotecall;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMaps;
import com.tcdng.unify.core.stream.JSONObjectStreamer;
import com.tcdng.unify.core.stream.ObjectStreamer;
import com.tcdng.unify.core.stream.XMLObjectStreamer;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.NetworkUtils;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.constant.RequestHeaderConstants;
import com.tcdng.unify.web.discovery.gem.APIDiscoveryPathConstants;
import com.tcdng.unify.web.discovery.gem.APIDiscoveryRemoteCallCodeConstants;
import com.tcdng.unify.web.discovery.gem.data.DiscoverRemoteCallParams;
import com.tcdng.unify.web.discovery.gem.data.DiscoverRemoteCallResult;

/**
 * Default implementation of a web client.
 * 
 * @author Lateef
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_WEBCLIENT)
public class WebClientImpl extends AbstractUnifyComponent implements WebClient {

    @Configurable
    private XMLObjectStreamer xmlObjectStreamer;

    @Configurable
    private JSONObjectStreamer jsonObjectStreamer;

    @Configurable
    private RemoteCallBinaryMessageStreamer remoteCallBinaryMessageStreamer;

    @Configurable
    private RemoteCallXmlMessageStreamer remoteCallXmlMessageStreamer;

    private Map<RemoteCallFormat, ObjectStreamer> objectStreamers;

    private FactoryMaps<String, String, RemoteCallSetup> preferences;

    public WebClientImpl() {
        preferences = new FactoryMaps<String, String, RemoteCallSetup>() {

            @Override
            protected RemoteCallSetup createObject(String remoteAppURL, String methodCode, Object... params)
                    throws Exception {
                return new RemoteCallSetup((RemoteCallFormat) params[1], (Charset) params[2], (String) params[0]);
            }
        };
    }

    @Override
    public void setupRemoteCall(String remoteAppURL, String methodCode) throws UnifyException {
        setupRemoteCall(remoteAppURL, methodCode, RemoteCallFormat.JSON, StandardCharsets.UTF_8);
    }

    @Override
    public void setupBinaryMessagingRemoteCall(String remoteAppURL, String methodCode) throws UnifyException {
        setupRemoteCall(remoteAppURL, methodCode, RemoteCallFormat.TAGGED_BINARYMESSAGE, null);
    }

    @Override
    public void setupXmlMessagingRemoteCall(String remoteAppURL, String methodCode) throws UnifyException {
        setupRemoteCall(remoteAppURL, methodCode, RemoteCallFormat.TAGGED_XMLMESSAGE, StandardCharsets.UTF_8);
    }

    @Override
    public void setupRemoteCall(String remoteAppURL, String methodCode, RemoteCallFormat format, Charset charset)
            throws UnifyException {
        logDebug("Setting up remote call with code = [{0}], remoteAppURL = [{1}]...", methodCode, remoteAppURL);
        if (preferences.isKey(remoteAppURL, methodCode)) {
            throw new UnifyException(UnifyWebErrorConstants.REMOTECALL_CLIENT_SETUP_CODE_EXISTS, remoteAppURL,
                    methodCode);
        }

        logDebug("Discovering remote call method with code [{0}] for remote application [{1}]...", methodCode,
                remoteAppURL);
        String discoveryURL = getDiscoveryURL(remoteAppURL);
        if (!isRemoteCallSetup(discoveryURL, APIDiscoveryRemoteCallCodeConstants.DISCOVER_REMOTE_CALL)) {
            preferences.get(discoveryURL, APIDiscoveryRemoteCallCodeConstants.DISCOVER_REMOTE_CALL, discoveryURL,
                    RemoteCallFormat.JSON, StandardCharsets.UTF_8);
        }

        DiscoverRemoteCallResult result =
                remoteCall(DiscoverRemoteCallResult.class, discoveryURL, new DiscoverRemoteCallParams(methodCode));
        checkError(result);

        preferences.get(remoteAppURL, methodCode, result.getRemoteCallInfo().getUrl(), format, charset);
        logDebug("...remote call setup completed.");
    }

    @Override
    public void clearAllRemoteCallSetup(String remoteAppURL) throws UnifyException {
        preferences.remove(remoteAppURL);
    }

    @Override
    public void clearRemoteCallSetup(String remoteAppURL, String methodCode) throws UnifyException {
        preferences.remove(remoteAppURL, methodCode);
    }

    @Override
    public boolean isRemoteCallSetup(String remoteAppURL, String methodCode) throws UnifyException {
        return preferences.isKey(remoteAppURL, methodCode);
    }

    @Override
    public PushBinaryMessageResult sendBinaryMessage(String remoteAppURL, PushBinaryMessageParams params)
            throws UnifyException {
        return remoteCall(PushBinaryMessageResult.class, remoteAppURL, params);
    }

    @Override
    public PushXmlMessageResult sendXmlMessage(String remoteAppURL, PushXmlMessageParams params) throws UnifyException {
        return remoteCall(PushXmlMessageResult.class, remoteAppURL, params);
    }

    @Override
    public <T extends RemoteCallResult> T remoteCall(Class<T> resultType, String remoteAppURL, RemoteCallParams param)
            throws UnifyException {
        T result = null;
        if (!preferences.isKey(remoteAppURL, param.getMethodCode())) {
            throw new UnifyException(UnifyWebErrorConstants.REMOTECALL_CLIENT_SETUP_CODE_UNKNOWN, remoteAppURL,
                    param.getMethodCode());
        }

        RemoteCallSetup remoteCallSetup = preferences.get(remoteAppURL, param.getMethodCode());
        param.setClientAppCode(getApplicationCode());
        OutputStream out = null;
        InputStream in = null;
        try {
            Charset charset = remoteCallSetup.getCharset();

            // Choose streamer based on format
            ObjectStreamer streamer = objectStreamers.get(remoteCallSetup.getFormat());

            // Establish connection
            RemoteCallFormat format = remoteCallSetup.getFormat();
            HttpURLConnection conn = (HttpURLConnection) new URL(remoteCallSetup.getTargetURL()).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", format.mimeType().template());
            if (charset != null) {
                conn.setRequestProperty("Accept-Charset", charset.name());
            }
            
            if (format.isTagged()) {
                conn.setRequestProperty(RequestHeaderConstants.REMOTE_MESSAGE_TYPE_HEADER,
                        RequestHeaderConstants.REMOTE_TAGGED_MESSAGE_TYPE);
            }
            
            conn.connect();

            // Stream remote call parameter out
            out = conn.getOutputStream();
            streamer.marshal(param, out, charset);
            out.flush();
            out.close();

            // Receive result
            in = conn.getInputStream();
            result = (T) streamer.unmarshal(resultType, in, charset);
            checkError(result);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }

        return result;
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
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private String getDiscoveryURL(String remoteAppURL) throws UnifyException {
        return NetworkUtils.constructURL(remoteAppURL, APIDiscoveryPathConstants.DISCOVER_REMOTECALL_PATH);
    }

    private void checkError(RemoteCallResult result) throws UnifyException {
        if (result.isError()) {
            throw new UnifyException(UnifyWebErrorConstants.REMOTECALL_CLIENT_ERROR, result.getErrorCode(),
                    result.getErrorMsg());
        }
    }

    private class RemoteCallSetup {

        private RemoteCallFormat format;

        private Charset charset;

        private String targetURL;

        public RemoteCallSetup(RemoteCallFormat format, Charset charset, String targetURL) {
            this.format = format;
            this.charset = charset;
            this.targetURL = targetURL;
        }

        public RemoteCallFormat getFormat() {
            return format;
        }

        public Charset getCharset() {
            return charset;
        }

        public String getTargetURL() {
            return targetURL;
        }
    }

}
