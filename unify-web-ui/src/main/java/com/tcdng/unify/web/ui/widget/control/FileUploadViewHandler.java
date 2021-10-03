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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.web.ui.widget.data.FileAttachmentInfo;

/**
 * File upload view handler.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface FileUploadViewHandler extends UnifyComponent {

    /**
     * Saves an upload.
     * 
     * @param id
     *                   the upload ID
     * @param category
     *                   the upload category
     * @param type
     *                   the attachment type
     * @param filename
     *                   the file name
     * @param attachment
     *                   the file data
     * @return the original upload ID if update otherwise new ID on create
     * @throws UnifyException
     *                        if an error occurs
     */
    Object save(Object id, String category, FileAttachmentType type, String filename, byte[] attachment) throws UnifyException;

    /**
     * Retrieves attachment data.
     * 
     * @param id
     *                 the upload ID
     * @param category
     *                 the upload category
     * @param type
     *                 the attachment type
     * @return the attachment info if found otherwise false
     * @throws UnifyException
     *                        if an error occurs
     */
    FileAttachmentInfo retrive(Object id, String category, FileAttachmentType type) throws UnifyException;

    /**
     * Deletes an upload record.
     * 
     * @param id
     *                 the upload ID
     * @param category
     *                 the upload category
     * @throws UnifyException
     *                        if an error occurs
     */
    void delete(Object id, String category) throws UnifyException;
}
