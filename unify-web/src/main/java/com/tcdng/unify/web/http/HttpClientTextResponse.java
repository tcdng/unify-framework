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

/**
 * HTTP client text response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class HttpClientTextResponse {

	private int responseCode;

	private String responseText;

	public HttpClientTextResponse(int responseCode, String responseText) {
		this.responseCode = responseCode;
		this.responseText = responseText;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseText() {
		return responseText;
	}
}
