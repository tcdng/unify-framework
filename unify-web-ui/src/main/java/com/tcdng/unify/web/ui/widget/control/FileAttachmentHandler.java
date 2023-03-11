/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.widget.data.FileAttachmentInfo;

/**
 * File attachment handler.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface FileAttachmentHandler extends UnifyComponent {

    /**
     * Fills file attachment file names.
     * 
     * @param parentId
     *            the parent ID
     * @param attachmentInfoList
     *            the attachment information list
     * @throws UnifyException
     *             if an error occurs
     */
    void fillAttachFileNames(Object parentId, List<FileAttachmentInfo> attachmentInfoList) throws UnifyException;

    /**
     * Handles an attachment request.
     * 
     * @param parentId
     *            the parent ID
     * @param fileAttachmentInfo
     *            the attachment information
     * @throws UnifyException
     *             if an error occurs
     */
    void handleAttach(Object parentId, FileAttachmentInfo fileAttachmentInfo) throws UnifyException;

    /**
     * Handles a file attachment view request.
     * 
     * @param parentId
     *            the parent ID
     * @param fileAttachmentInfo
     *            the attachment information
     * @return the attachment result
     * @throws UnifyException
     *             if an error occurs
     */
    FileAttachmentInfo handleView(Object parentId, FileAttachmentInfo fileAttachmentInfo) throws UnifyException;

    /**
     * Handles a file detachment.
     * 
     * @param parentId
     *            the parent ID
     * @param fileAttachmentInfo
     *            the attachment information
     * @throws UnifyException
     *             if an error occurs
     */
    void handleDetach(Object parentId, FileAttachmentInfo fileAttachmentInfo) throws UnifyException;
}
