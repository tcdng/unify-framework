/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.util.HashMap;
import java.util.Map;

/**
 * Supported MIME types.
 * 
 * @author The Code Department
 * @since 4.1
 */
public enum MimeType {
    APPLICATION_OCTETSTREAM("application/octet-stream", false),
    APPLICATION_PDF("application/pdf", false),
    APPLICATION_EXCEL("application/vnd.ms-excel;application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", false),
    APPLICATION_XLS("application/vnd.ms-excel", false),
    APPLICATION_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", false),
    APPLICATION_WORD("application/msword;application/vnd.openxmlformats-officedocument.wordprocessingml.document", false),
    APPLICATION_DOC("application/msword", false),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", false),
    APPLICATION_JSON("application/json", true),
    APPLICATION_XML("application/xml", true),
    TEXT_HTML("text/html", true),
    TEXT_CSV("text/csv", true),
    TEXT_CSS("text/css", true),
    TEXT_JAVASCRIPT("text/javascript", true),
    TEXT_XML("text/xml", true),
    TEXT_PLAIN("text/plain", true),
    TEXT_PLAIN_UTF8("text/plain;charset=UTF-8", true),
    TEXT("text/*", true),
    IMAGE_PNG("image/png", false),
    IMAGE_JPG("image/jpg", false),
    IMAGE_GIF("image/gif", false),
    IMAGE_BMP("image/bmp", false),
    IMAGE("image/*", false),
    AUDIO("audio/*,audio/mp3", false),
    VIDEO("video/*,video/mp4", false);

    private final String template;

    private final boolean text;

    private static final Map<String, MimeType> map = new HashMap<String, MimeType>();
    static {
    	map.put(APPLICATION_OCTETSTREAM.template, APPLICATION_OCTETSTREAM);
    	map.put(APPLICATION_PDF.template, APPLICATION_PDF);
    	map.put(APPLICATION_XLS.template, APPLICATION_XLS);
    	map.put(APPLICATION_XLSX.template, APPLICATION_XLSX);
    	map.put(APPLICATION_WORD.template, APPLICATION_WORD);
    	map.put(APPLICATION_DOC.template, APPLICATION_DOC);
    	map.put(APPLICATION_DOCX.template, APPLICATION_DOCX);
    	map.put(APPLICATION_JSON.template, APPLICATION_JSON);
    	map.put(APPLICATION_XML.template, APPLICATION_XML);
    	map.put(TEXT_HTML.template, TEXT_HTML);
    	map.put(TEXT_CSV.template, TEXT_CSV);
    	map.put(TEXT_CSS.template, TEXT_CSS);
    	map.put(TEXT_JAVASCRIPT.template, TEXT_JAVASCRIPT);
    	map.put(TEXT_XML.template, TEXT_XML);
    	map.put(TEXT_PLAIN.template, TEXT_PLAIN);
    	map.put(IMAGE_PNG.template, IMAGE_PNG);
    	map.put(IMAGE_JPG.template, IMAGE_JPG);
    	map.put(IMAGE_GIF.template, IMAGE_GIF);
    	map.put(IMAGE_BMP.template, IMAGE_BMP);
    }
    
    private MimeType(String template, boolean text) {
        this.template = template;
        this.text = text;
    }

    public static MimeType fromTemplate(String template) {
    	return map.get(template);
    }
    
    public String template() {
        return template;
    }
    
    public boolean isTextable() {
    	return text;
    }
    
    public boolean isApplicationOctetStream() {
    	return APPLICATION_OCTETSTREAM.equals(this);
    }
    
    public boolean isApplicationJson() {
    	return APPLICATION_JSON.equals(this);
    }
    
    public boolean isApplicationXml() {
    	return APPLICATION_XML.equals(this);
    }
    
    public boolean isImage() {
    	return IMAGE_PNG.equals(this) || IMAGE_JPG.equals(this) || IMAGE_GIF.equals(this) || IMAGE_BMP.equals(this) || IMAGE.equals(this);
    }
    
    public boolean isTextHtml() {
    	return TEXT_HTML.equals(this);
    }
    
    public boolean isTextCss() {
    	return TEXT_CSS.equals(this);
    }
    
    public boolean isTextJavascript() {
    	return TEXT_JAVASCRIPT.equals(this);
    }
    
    public boolean isTextCsv() {
    	return TEXT_CSV.equals(this);
    }
    
    public boolean isTextXml() {
    	return TEXT_XML.equals(this);
    }
    
    public boolean isPlainText() {
    	return TEXT_PLAIN.equals(this) || TEXT_PLAIN_UTF8.equals(this);
    }
}
