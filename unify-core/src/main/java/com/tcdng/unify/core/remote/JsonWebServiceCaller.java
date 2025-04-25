/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.net.HttpURLConnection;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.stream.JsonObjectStreamer;
import com.tcdng.unify.core.stream.ObjectStreamer;

/**
 * JSON web service caller.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(ApplicationComponents.APPLICATION_JSONWEBSERVICECALLER)
public class JsonWebServiceCaller extends AbstractWebServiceCaller {

    @Configurable
    private JsonObjectStreamer streamer;

    @Override
    protected void setHeaders(HttpURLConnection conn) throws UnifyException {
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json-patch+json");
    }

    @Override
    protected ObjectStreamer getObjectStreamer() throws UnifyException {
        return streamer;
    }

}
