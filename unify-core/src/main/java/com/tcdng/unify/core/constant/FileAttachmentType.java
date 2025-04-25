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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * File attachment type.
 * 
 * @author The Code Department
 * @since 4.1
 */
@StaticList(name = "fileattachmenttypelist", description = "$m{staticlist.fileattachmenttypelist}")
public enum FileAttachmentType implements EnumConst {

    AUDIO("AUD", "audio/*,audio/mp3", MimeType.AUDIO),
    CSV("CSV", ".csv", MimeType.TEXT_CSV),
    EXCEL("XLS", ".xls,.xlsx", MimeType.APPLICATION_EXCEL),
    IMAGE("IMG", "image/*", MimeType.IMAGE),
    IMAGE_PNG("PNG", ".png", MimeType.IMAGE_PNG),
    IMAGE_JPG("JPG", ".jpg,.jpeg", MimeType.IMAGE_JPG),
    IMAGE_GIF("GIF", ".gif", MimeType.IMAGE_GIF),
    IMAGE_BMP("BMP", ".bmp", MimeType.IMAGE_BMP),
    PDF("PDF", ".pdf", MimeType.APPLICATION_PDF),
    XML("XML", ".xml", MimeType.APPLICATION_XML),
    TEXT("TXT", ".txt", MimeType.TEXT),
    VIDEO("VID", "video/*,video/mp4", MimeType.VIDEO),
    WILDCARD("WILD", "", MimeType.APPLICATION_OCTETSTREAM),
    WORD("DOC", ".doc,.docx", MimeType.APPLICATION_WORD);

	private static final Map<String, FileAttachmentType> byExtension;
	
	static {
		Map<String, FileAttachmentType> map = new HashMap<String, FileAttachmentType>();
		map.put(".mp3", AUDIO);
		map.put(".csv", CSV);
		map.put(".xls", EXCEL);
		map.put(".xlsx", EXCEL);
		map.put(".png", IMAGE_PNG);
		map.put(".jpg", IMAGE_JPG);
		map.put(".gif", IMAGE_GIF);
		map.put(".bmp", IMAGE_BMP);
		map.put(".pdf", PDF);
		map.put(".xml", XML);
		map.put(".txt", TEXT);
		map.put(".mp4", VIDEO);
		map.put(".doc", WORD);
		map.put(".docx", WORD);
		byExtension = Collections.unmodifiableMap(map);
	}
	
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

    public boolean isWildCard() {
    	return WILDCARD.equals(this);
    }
    
    public String appendDefaultExtension(String filename) {
    	if (filename != null && filename.indexOf('.') < 0) {
    		switch(this) {
			case AUDIO:
				break;
			case CSV:
				return filename + ".csv";
			case EXCEL:
				break;
			case IMAGE:
				return filename + ".jpg";
			case IMAGE_BMP:
				return filename + ".bmp";
			case IMAGE_GIF:
				return filename + ".gif";
			case IMAGE_JPG:
				return filename + ".jpg";
			case IMAGE_PNG:
				return filename + ".png";
			case PDF:
				return filename + ".pdf";
			case TEXT:
				return filename + ".txt";
			case VIDEO:
				break;
			case WILDCARD:
				break;
			case WORD:
				return filename + ".doc";
			case XML:
				return filename + ".xml";
			default:
				break;    		
    		}
    	}
    	
    	return filename;
    }
    
    public static FileAttachmentType detectFromFileName(String fileName) {
    	FileAttachmentType type = WILDCARD;
        if (!StringUtils.isBlank(fileName)) {
            int index = fileName.lastIndexOf('.');
            if (index > 0) {
                String ext = fileName.substring(index).toLowerCase();
                type = byExtension.get(ext);
            }
        }

        return type;
    }

    public static FileAttachmentType fromCode(String code) {
        return EnumUtils.fromCode(FileAttachmentType.class, code);
    }

    public static FileAttachmentType fromName(String name) {
        return EnumUtils.fromName(FileAttachmentType.class, name);
    }

}
