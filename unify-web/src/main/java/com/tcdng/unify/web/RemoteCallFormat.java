/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.constant.ContentTypeConstants;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Supported remote call messaging formats.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public enum RemoteCallFormat implements EnumConst {

	JSON("JSON", ContentTypeConstants.APPLICATION_JSON), XML("XML", ContentTypeConstants.APPLICATION_XML);

	private String code;

	private String contentType;

	private RemoteCallFormat(String code, String contentType) {
		this.code = code;
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	@Override
	public String code() {
		return this.code;
	}

	public static RemoteCallFormat fromCode(String code) {
		return EnumUtils.fromCode(RemoteCallFormat.class, code);
	}

	public static RemoteCallFormat fromName(String name) {
		return EnumUtils.fromName(RemoteCallFormat.class, name);
	}

	public static RemoteCallFormat fromContentType(String contentType) {
		if (contentType != null) {
			if (contentType.startsWith(JSON.getContentType())) {
				return JSON;
			}

			if (contentType.startsWith(XML.getContentType())) {
				return XML;
			}
		}

		return null;
	}
}
