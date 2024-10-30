/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.util;

import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.data.FileAttachmentInfo;
import com.tcdng.unify.core.util.ImageUtils.ImageType;

/**
 * File utilities.
 * 
 * @author The Code Department
 * @version 1.0
 */
public final class FileUtils {

	private FileUtils() {

	}

	/**
	 * Detects file attachment type from supplied file attachment information.
	 * 
	 * @param fileAttachmentInfo the file attachment information
	 * @return the file attachment type
	 */
	public static FileAttachmentType detectFileAttachmentType(FileAttachmentInfo fileAttachmentInfo) {
		FileAttachmentType type = FileUtils.detectFileAttachmentType(fileAttachmentInfo.getFilename());
		return type.isWildCard() ? FileUtils.detectFileAttachmentType(fileAttachmentInfo.getAttachment()) : type;
	}

	/**
	 * Detects file attachment type from supplied file name.
	 * 
	 * @param fileName the file name
	 * @return the file attachment type
	 */
	public static FileAttachmentType detectFileAttachmentType(String fileName) {
		return FileAttachmentType.detectFromFileName(fileName);
	}

	/**
	 * Detects file attachment type from supplied file
	 * 
	 * @param file the file to check
	 * @return the file attachment type
	 */
	public static FileAttachmentType detectFileAttachmentType(byte[] file) {
		if (file != null) {
			// Image
			final ImageType imageType = ImageUtils.detectImageType(file);
			if (imageType != null) {
				switch (imageType) {
				case BMP:
					return FileAttachmentType.IMAGE_BMP;
				case GIF:
					return FileAttachmentType.IMAGE_GIF;
				case JPEG:
					return FileAttachmentType.IMAGE_JPG;
				case PNG:
					return FileAttachmentType.IMAGE_PNG;
				default:
					break;
				}
			}

			// PDF
			if ((file.length > 4) && (file[0] == (byte) 0x25) && (file[1] == (byte) 0x50) && (file[2] == (byte) 0x44)
					&& (file[3] == (byte) 0x46)) {
				return FileAttachmentType.PDF;
			}
		}

		return null;
	}
	
	public static String extendFileName(String fileName, String extension) {
		if (fileName != null && extension != null) {
			int lastIndex = fileName.lastIndexOf('.');
			if (lastIndex >= 0) {
				return fileName.substring(0, lastIndex) + extension + fileName.substring(lastIndex);
			}
		}
		
		return fileName;
	}
}
