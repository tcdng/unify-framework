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
package com.tcdng.unify.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Zip utilities.
 * 
 * @author The Code Department
 * @version 1.0
 */
public final class ZipUtils {

	private ZipUtils() {

	}

	/**
	 * Extracts all zip file content to folder
	 * 
	 * @param targetFolder the target folder
	 * @param zipFile      the zip file
	 * @throws UnifyException if an error occurs
	 */
	public static void extractAll(String targetFolder, File zipFile) throws UnifyException {
		InputStream in = null;
		try {
			in = new FileInputStream(zipFile);
			ZipUtils.localExtractAll(targetFolder, in);
		} catch (FileNotFoundException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
		} finally {
			IOUtils.close(in);
		}
	}

	/**
	 * Extracts all zip file content to folder
	 * 
	 * @param targetFolder the target folder
	 * @param fileName     the zip file name
	 * @throws UnifyException if an error occurs
	 */
	public static void extractAll(String targetFolder, String fileName) throws UnifyException {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			ZipUtils.localExtractAll(targetFolder, in);
		} catch (FileNotFoundException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
		} finally {
			IOUtils.close(in);
		}
	}

	/**
	 * Extracts all zip in byte array content to folder
	 * 
	 * @param targetFolder the target folder
	 * @param zipArray     the zip file name
	 * @throws UnifyException if an error occurs
	 */
	public static void extractAll(String targetFolder, byte[] zipArray) throws UnifyException {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(zipArray);
			ZipUtils.localExtractAll(targetFolder, in);
		} finally {
			IOUtils.close(in);
		}
	}

	/**
	 * Extracts all zip file content to folder
	 * 
	 * @param targetFolder the target folder
	 * @param in           the input stream
	 * @throws UnifyException if an error occurs
	 */
	public static void extractAll(String targetFolder, InputStream in) throws UnifyException {
		try {
			ZipUtils.localExtractAll(targetFolder, in);
		} finally {
			IOUtils.close(in);
		}
	}

	private static void localExtractAll(String targetFolder, InputStream in) throws UnifyException {
		IOUtils.ensureDirectoryExists(targetFolder);
		
        try (ZipInputStream zin = new ZipInputStream(in)) {
            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                String targetFilePath = IOUtils.buildFilename(targetFolder, entry.getName());
                if (entry.isDirectory()) {
            		IOUtils.ensureDirectoryExists(targetFilePath);
                } else {
                    extractFile(targetFilePath, zin);
                }
                zin.closeEntry();
                entry = zin.getNextEntry();
            }
        } catch (IOException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
		}
	}

    private static void extractFile(String targetFilePath, ZipInputStream zin) throws UnifyException {
        File targetFile = new File(targetFilePath);
		IOUtils.ensureDirectoryExists(targetFile.getParent());
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
        	IOUtils.writeAllLeaveOpen(fos, zin);
        } catch (IOException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
		}
    }
}
