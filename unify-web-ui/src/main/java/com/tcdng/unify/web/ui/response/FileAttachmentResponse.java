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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.data.FileAttachmentInfo;
import com.tcdng.unify.core.data.FileAttachmentsInfo;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.AbstractOpenWindowPageControllerResponse;

/**
 * File attachment response
 *
 * @author The Code Department
 * @since 1.0
 */
@Component("fileattachmentresponse")
public class FileAttachmentResponse extends AbstractOpenWindowPageControllerResponse {

    @Override
	protected WindowResourceInfo prepareWindowResource() throws UnifyException {
		FileAttachmentInfo fileAttachmentInfo = null;
		Object resource = getRequestAttribute(UnifyWebRequestAttributeConstants.FILEATTACHMENTS_INFO);
		if (resource instanceof FileAttachmentsInfo) {
			FileAttachmentsInfo fileAttachmentsInfo = (FileAttachmentsInfo) resource;
			fileAttachmentInfo = fileAttachmentsInfo.getSelectedAttachmentInfo();
			String fileName = fileAttachmentInfo.getFilename();
			if (fileAttachmentsInfo.isViewTimestamped()) {
				fileName = getTimestampedResourceName(fileAttachmentInfo.getFilename());
			}

			final FileAttachmentType type = FileAttachmentType.detectFromFileName(fileName);
			return new WindowResourceInfo(fileAttachmentsInfo, "/resource/fileattachment", fileName,
					type.mimeType().template(), false);
		}

		fileAttachmentInfo = (FileAttachmentInfo) resource;
		String fileName = fileAttachmentInfo.getFilename();
		final FileAttachmentType type = FileAttachmentType.detectFromFileName(fileName);
		return new WindowResourceInfo(fileAttachmentInfo, "/resource/fileattachment", fileName,
				type == null ? FileAttachmentType.WILDCARD.mimeType().template() : type.mimeType().template(), false);
	}

}
