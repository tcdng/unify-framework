/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Supported report format type.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "reportformatlist", description="$m{staticlist.reportformatlist}")
public enum ReportFormat implements EnumConst {
    CSV("CSV", ".csv", MimeType.TEXT_CSV),
    DOC("DOC", ".doc", MimeType.APPLICATION_DOC),
    DOCX("DOCX", ".docx", MimeType.APPLICATION_DOCX),
    PDF("PDF", ".pdf", MimeType.APPLICATION_PDF),
    XLS("XLS", ".xls", MimeType.APPLICATION_XLS),
    XLSX("XLSX", ".xlsx", MimeType.APPLICATION_XLSX),
    XML("XML", ".xml", MimeType.APPLICATION_XML);

    private final String code;

    private final String fileExtension;

    private final MimeType mimeType;

    private ReportFormat(String code, String fileExtension, MimeType mimeType) {
        this.code = code;
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return PDF.code;
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

    public MimeType mimeType() {
        return this.mimeType;
    }
}
