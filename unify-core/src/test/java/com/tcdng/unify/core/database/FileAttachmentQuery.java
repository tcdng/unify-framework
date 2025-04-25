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
package com.tcdng.unify.core.database;

/**
 * Query object for test file attachment.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class FileAttachmentQuery extends AbstractTestTableEntityQuery<FileAttachment> {

    public FileAttachmentQuery() {
        super(FileAttachment.class);
    }
    
    public FileAttachmentQuery ownerId(Long ownerId) {
        return (FileAttachmentQuery) addEquals("ownerId", ownerId);
    }
    
    public FileAttachmentQuery ownerType(String ownerType) {
        return (FileAttachmentQuery) addEquals("ownerType", ownerType);
    }
    
    public FileAttachmentQuery code(String code) {
        return (FileAttachmentQuery) addEquals("code", code);
    }
}
