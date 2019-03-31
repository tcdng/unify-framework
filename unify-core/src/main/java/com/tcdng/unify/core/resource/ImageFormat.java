/*
 * Copyright 2018-2019 The Code Department.
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
import com.tcdng.unify.core.constant.MimeType;
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

    BITMAP("BMP", ".bmp", MimeType.IMAGE_BMP),
    JPEG("JPG", ".jpg", MimeType.IMAGE_JPG),
    GIF("GIF", ".gif", MimeType.IMAGE_GIF),
    PNG("PNG", ".png", MimeType.IMAGE_PNG),
    WILDCARD("WLD", ".*", MimeType.IMAGE);

    private final String code;

    private final String fileExtension;

    private final MimeType mimeType;

    private ImageFormat(String code, String fileExtension, MimeType mimeType) {
        this.code = code;
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
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

    public MimeType mimeType() {
        return this.mimeType;
    }
}
