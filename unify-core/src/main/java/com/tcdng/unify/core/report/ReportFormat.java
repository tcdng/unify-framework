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
package com.tcdng.unify.core.report;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.ContentTypeConstants;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Supported report format type.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("reportformatlist")
public enum ReportFormat implements EnumConst {
    CSV("CSV", ".csv", ContentTypeConstants.TEXT_CSV),
    DOC("DOC", ".doc", ContentTypeConstants.APPLICATION_DOC),
    DOCX("DOCX", ".docx", ContentTypeConstants.APPLICATION_DOCX),
    PDF("PDF", ".pdf", ContentTypeConstants.APPLICATION_PDF),
    XLS("XLS", ".xls", ContentTypeConstants.APPLICATION_XLS),
    XLSX("XLSX", ".xlsx", ContentTypeConstants.APPLICATION_XLSX),
    XML("XML", ".xml", ContentTypeConstants.APPLICATION_XML);

    private final String code;

    private final String fileExtension;

    private final String contentType;

    private ReportFormat(String code, String fileExtension, String contentType) {
        this.code = code;
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    @Override
    public String code() {
        return this.code;
    }

    public static ReportFormat fromCode(String code) {
        return EnumUtils.fromCode(ReportFormat.class, code);
    }

    public static ReportFormat fromName(String name) {
        return EnumUtils.fromName(ReportFormat.class, name);
    }

    public String fileExt() {
        return this.fileExtension;
    }

    public String contentType() {
        return this.contentType;
    }
}
