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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * File attachment type.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("fileattachmenttypelist")
public enum FileAttachmentType implements EnumConst {

    AUDIO("AUD", "audio/*,audio/mp3", "audio/*,audio/mp3"),
    CSV("CSV", ".csv", ContentTypeConstants.TEXT_CSV),
    EXCEL("XLS", ".xls,.xlsx", ContentTypeConstants.APPLICATION_XLS + ";" + ContentTypeConstants.APPLICATION_XLSX),
    IMAGE("IMG", "image/*", ContentTypeConstants.IMAGE),
    PDF("PDF", ".pdf", ContentTypeConstants.APPLICATION_PDF),
    TEXT("TXT", "text/*", "text/*"),
    VIDEO("VID", "video/*,video/mp4", "video/*,video/mp4"),
    WILDCARD("WILD", "", ContentTypeConstants.APPLICATION_OCTETSTREAM),
    WORD("DOC", ".doc,.docx", ContentTypeConstants.APPLICATION_DOC + ";" + ContentTypeConstants.APPLICATION_DOCX);

    private final String code;

    private final String extensions;

    private final String contentType;

    private FileAttachmentType(String code, String extensions, String contentType) {
        this.code = code;
        this.extensions = extensions;
        this.contentType = contentType;
    }

    @Override
    public String code() {
        return code;
    }

    public String extensions() {
        return extensions;
    }

    public String contentType() {
        return contentType;
    }

    public static FileAttachmentType fromCode(String code) {
        return EnumUtils.fromCode(FileAttachmentType.class, code);
    }

    public static FileAttachmentType fromName(String name) {
        return EnumUtils.fromName(FileAttachmentType.class, name);
    }
}
