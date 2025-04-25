/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.FileAttachmentType;

/**
 * Upload control handler.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface UploadControlHandler extends UnifyComponent {

	/**
	 * Saves an upload.
	 * 
	 * @param dataIndex the item data index
	 * @param type      the attachment type
	 * @param filename  the file name
	 * @param fileData  the file data
	 * @throws UnifyException if an error occurs
	 */
	void saveUpload(int dataIndex, FileAttachmentType type, String filename, byte[] fileData) throws UnifyException;

	/**
	 * Checks is data item at index has file data
	 * 
	 * @param dataIndex the data index
	 * @return true if present otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean isFileDataPresent(int dataIndex) throws UnifyException;
}
