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
package com.tcdng.unify.core.constant;

/**
 * Supported MIME types.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum MimeType {
    APPLICATION_OCTETSTREAM("application/octet-stream"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_EXCEL("application/vnd.ms-excel;application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    APPLICATION_XLS("application/vnd.ms-excel"),
    APPLICATION_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    APPLICATION_WORD("application/msword;application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    APPLICATION_DOC("application/msword"),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    TEXT_HTML("text/html"),
    TEXT_CSV("text/csv"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    TEXT_XML("text/xml"),
    TEXT_PLAIN("text/plain"),
    TEXT_PLAIN_UTF8("text/plain;charset=UTF-8"),
    TEXT("text/*"),
    IMAGE_PNG("image/png"),
    IMAGE_JPG("image/jpg"),
    IMAGE_GIF("image/gif"),
    IMAGE_BMP("image/bmp"),
    IMAGE("image/*"),
    AUDIO("audio/*,audio/mp3"),
    VIDEO("video/*,video/mp4");

    private String template;

    private MimeType(String template) {
        this.template = template;
    }

    public String template() {
        return template;
    }
}
