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

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.constant.ClientPlatform;
import com.tcdng.unify.core.data.Parameters;
import com.tcdng.unify.web.AbstractClientRequest;
import com.tcdng.unify.web.ClientCookie;
import com.tcdng.unify.web.RequestPathParts;
import com.tcdng.unify.web.constant.ClientRequestType;

/**
 * HTTP client request.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class HttpClientRequest extends AbstractClientRequest {

	private ClientPlatform clientPlatform;

	private HttpRequestMethodType methodType;

	private RequestPathParts requestPathParts;

	private Charset charset;

	private HttpRequestHeaders headers;

	private Parameters parameters;

	private Map<String, ClientCookie> cookies;

	private String text;
	
	private byte[] bytes;
	
	public HttpClientRequest(ClientPlatform clientPlatform, HttpRequestMethodType methodType,
			RequestPathParts requestPathParts, Charset charset, HttpRequestHeaders headers,
			Map<String, Object> parameters, Map<String, ClientCookie> cookies, String text, byte[] bytes) {
		this.clientPlatform = clientPlatform;
		this.methodType = methodType;
		this.requestPathParts = requestPathParts;
		this.headers = headers;
		this.charset = charset;
		this.parameters = new Parameters(parameters);
		this.cookies = cookies;
		this.text = text;
		this.bytes = bytes;
	}

	@Override
	public ClientPlatform getClientPlatform() {
		return clientPlatform;
	}

	@Override
	public ClientRequestType getType() {
		return methodType.clientRequestType();
	}

	@Override
	public RequestPathParts getRequestPathParts() {
		return requestPathParts;
	}

	@Override
	public Charset getCharset() {
		return charset;
	}

	@Override
	public Parameters getParameters() {
		return parameters;
	}

	@Override
	public HttpRequestHeaders getRequestHeaders() {
		return headers;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public Set<String> getCookieNames() {
		return cookies.keySet();
	}

	@Override
	public ClientCookie getCookie(String name) {
		return cookies.get(name);
	}
}
