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
package com.tcdng.unify.web;

import java.nio.charset.Charset;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.ClientPlatform;
import com.tcdng.unify.core.data.Parameters;
import com.tcdng.unify.web.constant.ClientRequestType;
import com.tcdng.unify.web.http.HttpRequestHeaders;

/**
 * Test controller request.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class TestClientRequest extends AbstractClientRequest {

    private RequestPathParts requestPathParts;

    private Parameters parameters;

    private String text;
    
    private byte[] bytes;
    
    public TestClientRequest(RequestPathParts requestPathParts, String text) {
        this.requestPathParts = requestPathParts;
        this.parameters = new Parameters();
        this.text = text;
    }
    
    public TestClientRequest(RequestPathParts requestPathParts, byte[] bytes) {
        this.requestPathParts = requestPathParts;
        this.parameters = new Parameters();
        this.bytes = bytes;
    }
    
    public TestClientRequest(RequestPathParts requestPathParts) {
        this.requestPathParts = requestPathParts;
        this.parameters = new Parameters();
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
	public String getQueryString() {
		return null;
	}

	@Override
	public Parameters getParameters() {
		return parameters;
	}

    public void setParameters(Parameters parameters) {
		this.parameters = parameters;
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
