/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.core.notification;

import java.io.File;

import com.tcdng.unify.core.constant.FileAttachmentType;

/**
 * An email attachment data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class EmailAttachment {

    private FileAttachmentType type;

    private String name;

    private File file;

    private byte[] blob;

    private boolean inline;

    public EmailAttachment(FileAttachmentType type, String name, File file) {
        this(type, name, file, false);
    }

    public EmailAttachment(FileAttachmentType type, String name, File file, boolean inline) {
        this.type = type;
        this.name = name;
        this.file = file;
        this.inline = inline;
    }

    public EmailAttachment(FileAttachmentType type, String name, byte[] blob) {
        this(type, name, blob, false);
    }

    public EmailAttachment(FileAttachmentType type, String name, byte[] blob, boolean inline) {
        this.type = type;
        this.name = name;
        this.blob = blob;
        this.inline = inline;
    }

    public FileAttachmentType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public byte[] getBlob() {
        return blob;
    }

    public boolean isInline() {
        return inline;
    }
}
