/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.data;

import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.util.StringUtils;

/**
 * File attachment information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class FileAttachmentInfo {

    private String name;

    private String description;

    private String filename;

    private FileAttachmentType type;

    private byte[] attachment;

    private boolean present;
    
    public FileAttachmentInfo(FileAttachmentType type, String name, String description, String filename) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.filename = filename;
    }

    public FileAttachmentInfo(FileAttachmentType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public FileAttachmentInfo(FileAttachmentType type, String name) {
        this.type = type;
        this.name = name;
    }

    public FileAttachmentInfo(FileAttachmentType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
		this.description = description;
	}

	public FileAttachmentType getType() {
        return type;
    }

    public void setType(FileAttachmentType type) {
		this.type = type;
	}

	public String getTypeName() {
        return type.code();
    }

    public String getAccept() {
        return type.mimeType().template();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public boolean isPresent() {
		return present;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	public boolean isEmpty() {
        return this.filename == null;
    }

	public boolean isUndefined() {
        return StringUtils.isBlank(this.description);
    }

    public int size() {
        if (this.attachment != null) {
            return this.attachment.length;
        }

        return 0;
    }
}
