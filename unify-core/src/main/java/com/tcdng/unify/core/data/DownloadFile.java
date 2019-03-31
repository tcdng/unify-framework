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
package com.tcdng.unify.core.data;

/**
 * Data object that represents a download file.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DownloadFile {

    private String contentType;

    private String filename;

    byte[] data;

    public DownloadFile(String contentType, String filename, byte[] data) {
        this.contentType = contentType;
        this.filename = filename;
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

}
