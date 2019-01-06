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
package com.tcdng.unify.core.file;

/**
 * File information data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FileInfo {

    private String filename;

    private String absolutePath;

    private long length;

    private long created;

    private long modified;

    private boolean file;

    private boolean hidden;

    public FileInfo(String filename, String absolutePath, long length, long created, long modified, boolean file,
            boolean hidden) {
        this.filename = filename;
        this.absolutePath = absolutePath;
        this.length = length;
        this.created = created;
        this.modified = modified;
        this.file = file;
        this.hidden = hidden;
    }

    public String getFilename() {
        return filename;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public long getLength() {
        return length;
    }

    public long getCreated() {
        return created;
    }

    public long getModified() {
        return modified;
    }

    public boolean isFile() {
        return file;
    }

    public boolean isDirectory() {
        return !file;
    }

    public boolean isHidden() {
        return hidden;
    }
}
