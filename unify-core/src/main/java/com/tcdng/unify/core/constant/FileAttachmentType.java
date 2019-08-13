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

    AUDIO("AUD", "audio/*,audio/mp3", MimeType.AUDIO),
    CSV("CSV", ".csv", MimeType.TEXT_CSV),
    EXCEL("XLS", ".xls,.xlsx", MimeType.APPLICATION_EXCEL),
    IMAGE("IMG", "image/*", MimeType.IMAGE),
    PDF("PDF", ".pdf", MimeType.APPLICATION_PDF),
    TEXT("TXT", "text/*", MimeType.TEXT),
    VIDEO("VID", "video/*,video/mp4", MimeType.VIDEO),
    WILDCARD("WILD", "", MimeType.APPLICATION_OCTETSTREAM),
    WORD("DOC", ".doc,.docx", MimeType.APPLICATION_WORD);

    private final String code;

    private final String extensions;

    private final MimeType mimeType;

    private FileAttachmentType(String code, String extensions, MimeType mimeType) {
        this.code = code;
        this.extensions = extensions;
        this.mimeType = mimeType;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return TEXT.code;
    }

    public String extensions() {
        return extensions;
    }

    public MimeType mimeType() {
        return mimeType;
    }

    public static FileAttachmentType fromCode(String code) {
        return EnumUtils.fromCode(FileAttachmentType.class, code);
    }

    public static FileAttachmentType fromName(String name) {
        return EnumUtils.fromName(FileAttachmentType.class, name);
    }
}
