/*
 * Copyright 2018 The Code Department
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

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * File server used for transferring files.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface FileTransferServer extends UnifyComponent {

	/**
	 * Gets a file list from remote server path.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @return the file list
	 * @throws UnifyException
	 *             if an error occurs
	 */
	List<FileInfo> getRemoteFileList(FileTransferInfo fileTransferInfo) throws UnifyException;

	/**
	 * Tests if a file directory exists on remote server.
	 * 
	 * @param fileTransferInfo
	 *            The file server info
	 * @return boolean a true value if file directory exists on server otherwise
	 *         false
	 * @throws UnifyException
	 *             If an error occurs
	 */
	boolean remoteDirectoryExists(FileTransferInfo fileTransferInfo) throws UnifyException;

	/**
	 * Tests if a file exists on remote server.
	 * 
	 * @param fileTransferInfo
	 *            The file server info
	 * @param serverFile
	 *            the server file name
	 * @return boolean a true value if file exists on server otherwise false
	 * @throws UnifyException
	 *             If an error occurs
	 */
	boolean remoteFileExists(FileTransferInfo fileTransferInfo, String serverFile) throws UnifyException;

	/**
	 * Creates a directory, the transfer info remote path, on remote server if does
	 * not exist.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void createRemoteDirectory(FileTransferInfo fileTransferInfo) throws UnifyException;

	/**
	 * Creates a file on remote server truncating if already exists.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @param serverFile
	 *            the server file name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void createRemoteFile(FileTransferInfo fileTransferInfo, String serverFile) throws UnifyException;

	/**
	 * Deletes a file on remote server.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @param serverFile
	 *            the server file name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void deleteRemoteFile(FileTransferInfo fileTransferInfo, String serverFile) throws UnifyException;

	/**
	 * Reads a block of bytes from file on remote file server.
	 * 
	 * @param fileTransferInfo
	 *            the file transfer information
	 * @param serverFile
	 *            the remote file name
	 * @param index
	 *            the index to start reading block from
	 * @param size
	 *            the size of block to read in bytes
	 * @return the block of bytes read
	 * @throws UnifyException
	 *             if an error occurs
	 */
	byte[] readRemoteBlock(FileTransferInfo fileTransferInfo, String serverFile, long index, int size)
			throws UnifyException;

	/**
	 * Gets a file list from local path.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @return the file list
	 * @throws UnifyException
	 *             if an error occurs
	 */
	List<FileInfo> getLocalFileList(FileTransferInfo fileTransferInfo) throws UnifyException;

	/**
	 * Creates a file on local truncating if already exists.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @param localFile
	 *            the local file name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void createLocalFile(FileTransferInfo fileTransferInfo, String localFile) throws UnifyException;

	/**
	 * Creates a local directory.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void createLocalDirectory(FileTransferInfo fileTransferInfo) throws UnifyException;

	/**
	 * Deletes a local file.
	 * 
	 * @param fileTransferInfo
	 *            the file server info
	 * @param localFile
	 *            the local file name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void deleteLocalFile(FileTransferInfo fileTransferInfo, String localFile) throws UnifyException;

	/**
	 * Reads a block of bytes from file on local.
	 * 
	 * @param fileTransferInfo
	 *            the file transfer information
	 * @param localFile
	 *            the local file name
	 * @param index
	 *            the index to start reading block from
	 * @param size
	 *            the size of block to read in bytes
	 * @return the block of bytes read
	 * @throws UnifyException
	 *             if an error occurs
	 */
	byte[] readLocalBlock(FileTransferInfo fileTransferInfo, String localFile, long index, int size)
			throws UnifyException;

	/**
	 * Upload a file to remote file server.
	 * 
	 * @param fileTransferInfo
	 *            the file transfer information
	 * @param serverFile
	 *            the server file name
	 * @param localFile
	 *            the local file name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void uploadFile(FileTransferInfo fileTransferInfo, String serverFile, String localFile) throws UnifyException;

	/**
	 * Upload all files including subfolders to remote file server.
	 * 
	 * @param fileTransferInfo
	 *            the file transfer information
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void uploadFiles(FileTransferInfo fileTransferInfo) throws UnifyException;

	/**
	 * Download a file from remote file server.
	 * 
	 * @param fileTransferInfo
	 *            the file transfer information
	 * @param serverFile
	 *            the server file name
	 * @param localFile
	 *            the local file name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void downloadFile(FileTransferInfo fileTransferInfo, String serverFile, String localFile) throws UnifyException;

	/**
	 * Download all files including subfolders from remote file server.
	 * 
	 * @param fileTransferInfo
	 *            the file transfer information
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void downloadFiles(FileTransferInfo fileTransferInfo) throws UnifyException;
}
