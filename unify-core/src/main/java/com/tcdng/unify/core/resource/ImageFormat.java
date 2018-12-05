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

package com.tcdng.unify.core.resource;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.ContentTypeConstants;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Supported image formats.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("imageformatlist")
public enum ImageFormat implements EnumConst {

	BITMAP("BMP", ".bmp", "image/bmp"), JPEG("JPG", ".jpg", "image/jpg"), GIF("GIF", ".gif", "image/gif"), PNG("PNG",
			".png", "image/png"), WILDCARD("WLD", ".*", ContentTypeConstants.IMAGE);

	private final String code;

	private final String fileExtension;

	private final String contentType;

	private ImageFormat(String code, String fileExtension, String contentType) {
		this.code = code;
		this.fileExtension = fileExtension;
		this.contentType = contentType;
	}

	@Override
	public String code() {
		return this.code;
	}

	public static ImageFormat fromCode(String code) {
		return EnumUtils.fromCode(ImageFormat.class, code);
	}

	public static ImageFormat fromName(String name) {
		return EnumUtils.fromName(ImageFormat.class, name);
	}

	public String fileExt() {
		return this.fileExtension;
	}

	public String contentType() {
		return this.contentType;
	}
}
