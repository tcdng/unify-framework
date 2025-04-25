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

import java.net.HttpURLConnection;
import java.net.URL;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.WebApplicationComponents;

/**
 * HTTP client implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_HTTPCLIENT)
public class HttpClientImpl extends AbstractHttpClient {

    @Override
    protected HttpURLConnection getHttpURLConnection(String url) throws UnifyException {
        try {
            return (HttpURLConnection) new URL(url).openConnection();
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return null;
    }
}
