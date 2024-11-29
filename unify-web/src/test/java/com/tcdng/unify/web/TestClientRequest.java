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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.ClientPlatform;
import com.tcdng.unify.web.constant.ClientRequestType;
import com.tcdng.unify.web.http.HttpRequestHeaders;

/**
 * Test controller request.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TestClientRequest extends AbstractClientRequest {

    private RequestPathParts requestPathParts;

    private Map<String, Object> parameters;

    private String text;
    
    private byte[] bytes;
    
    public TestClientRequest(RequestPathParts requestPathParts, String text) {
        this.requestPathParts = requestPathParts;
        parameters = new HashMap<String, Object>();
        this.text = text;
    }
    
    public TestClientRequest(RequestPathParts requestPathParts, byte[] bytes) {
        this.requestPathParts = requestPathParts;
        parameters = new HashMap<String, Object>();
        this.bytes = bytes;
    }
    
    public TestClientRequest(RequestPathParts requestPathParts) {
        this.requestPathParts = requestPathParts;
        parameters = new HashMap<String, Object>();
    }

    @Override
    public ClientPlatform getClientPlatform() {
        return ClientPlatform.DEFAULT;
    }

    @Override
    public ClientRequestType getType() {
        return ClientRequestType.GET;
    }

    @Override
    public RequestPathParts getRequestPathParts() {
        return requestPathParts;
    }

    @Override
    public Charset getCharset() {
        return null;
    }

    @Override
	public HttpRequestHeaders getRequestHeaders() {
		return null;
	}

	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
    public Set<String> getParameterNames() {
        return parameters.keySet();
    }

    @Override
    public Object getParameter(String name) {
        return parameters.get(name);
    }

    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    @Override
	public String getText() throws UnifyException {
		return text;
	}

	@Override
	public byte[] getBytes() throws UnifyException {
		return bytes;
	}

    @Override
    public Set<String> getCookieNames() {
        return null;
    }

    @Override
    public ClientCookie getCookie(String name) {
        return null;
    }
}
