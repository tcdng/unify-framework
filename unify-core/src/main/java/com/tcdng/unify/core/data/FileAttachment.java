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

/**
 * Notification attachment definition.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class FileAttachment {

    private FileAttachmentType type;

    private String fileName;

    private byte[] data;

    public FileAttachment(FileAttachmentType type, String fileName, byte[] data) {
        this.type = type;
        this.fileName = fileName;
        this.data = data;
    }

    public FileAttachmentType getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

}
