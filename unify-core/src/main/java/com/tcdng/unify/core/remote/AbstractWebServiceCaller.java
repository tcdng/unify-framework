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

package com.tcdng.unify.core.remote;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.stream.ObjectStreamer;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for web service caller.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractWebServiceCaller extends AbstractUnifyComponent implements WebServiceCaller {

    @Override
    public <T> T getToRemote(Class<T> resultType, String targetUrl) throws UnifyException {
        return callRemote(resultType, "GET", targetUrl, null);
    }

    @Override
    public <T> T postToRemote(Class<T> resultType, String targetUrl) throws UnifyException {
        return callRemote(resultType, "POST", targetUrl, null);
    }

    @Override
    public <T> T postToRemote(Class<T> resultType, String targetUrl, Object param) throws UnifyException {
        return callRemote(resultType, "POST", targetUrl, param);
    }

    private <T> T callRemote(Class<T> resultType, String command, String targetUrl, Object param)
            throws UnifyException {
        OutputStream out = null;
        InputStream in = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
            if (param != null) {
                conn.setDoOutput(true);
            }
            conn.setDoInput(true);
            conn.setRequestMethod(command);
            conn.setUseCaches(false);
            setHeaders(conn);

            conn.connect();

            // Stream remote call parameter out
            if (param != null) {
                out = conn.getOutputStream();
                getObjectStreamer().marshal(param, out);
                out.flush();
                out.close();
            }

            // Receive result
            in = conn.getInputStream();
            return (T) getObjectStreamer().unmarshal(resultType, in);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }
        return null;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected abstract void setHeaders(HttpURLConnection conn) throws UnifyException;

    protected abstract ObjectStreamer getObjectStreamer() throws UnifyException;
}
