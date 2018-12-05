/*
 * Copyright 2014 The Code Department
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

import com.tcdng.unify.web.ClientRequest;

/**
 * HTTP client request.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class HttpClientRequest implements ClientRequest {

	private String path;

	private Charset charset;

	private Map<String, Object> parameters;

	public HttpClientRequest(String path, Charset charset, Map<String, Object> parameters) {
		this.path = path;
		this.charset = charset;
		this.parameters = parameters;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public Charset getCharset() {
		return charset;
	}

	@Override
	public Set<String> getParameterNames() {
		return parameters.keySet();
	}

	@Override
	public Object getParameter(String name) {
		return parameters.get(name);
	}
}
