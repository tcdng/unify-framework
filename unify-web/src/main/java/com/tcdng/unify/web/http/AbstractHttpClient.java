/*
 * Copyright 2018-2025 The Code Department.
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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;

/**
 * Abstract HTTP client implementation.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractHttpClient extends AbstractUnifyComponent implements HttpClient {

	@Configurable("10000")
	private int connectTimeout;

	@Configurable("30000")
	private int readTimeout;

    @Override
    public HttpClientTextResponse getRemoteTextResource(String url, Map<String, String> parameters)
            throws UnifyException {
        HttpURLConnection connection = getHttpURLConnection(url);
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getParameters(parameters));
            out.flush();
            out.close();

            int responseCode = connection.getResponseCode();
            StringBuilder responseText = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                responseText.append(line);
            }
            in.close();
            return new HttpClientTextResponse(responseCode, responseText.toString());
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private String getParameters(Map<String, String> parameters) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean isAppendSymbol = false;
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            if (isAppendSymbol) {
                sb.append('&');
            } else {
                isAppendSymbol = true;
            }
            sb.append(URLEncoder.encode(param.getKey(), "UTF-8")).append("=")
                    .append(URLEncoder.encode(param.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    protected abstract HttpURLConnection getHttpURLConnection(String url) throws UnifyException;
}
