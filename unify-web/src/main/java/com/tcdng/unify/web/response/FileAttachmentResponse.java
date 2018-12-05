/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.web.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.AbstractOpenWindowPageControllerResponse;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.data.FileAttachmentInfo;
import com.tcdng.unify.web.ui.data.FileAttachmentsInfo;

/**
 * File attachment response
 *
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("fileattachmentresponse")
public class FileAttachmentResponse extends AbstractOpenWindowPageControllerResponse {

	@Override
	protected WindowResourceInfo prepareWindowResource() throws UnifyException {
		FileAttachmentsInfo fileAttachmentsInfo = (FileAttachmentsInfo) this
				.getRequestAttribute(UnifyWebRequestAttributeConstants.FILEATTACHMENTS_INFO);
		FileAttachmentInfo fileAttachmentInfo = fileAttachmentsInfo.getSelectedAttachmentInfo();
		String resourceName = getTimestampedResourceName(fileAttachmentInfo.getFilename());
		return new WindowResourceInfo(fileAttachmentsInfo, "/resource/fileattachment", resourceName,
				fileAttachmentInfo.getType().contentType(), false);
	}

}
