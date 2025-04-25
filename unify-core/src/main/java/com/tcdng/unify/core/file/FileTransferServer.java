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
package com.tcdng.unify.core.file;

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * File server used for transferring files.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface FileTransferServer extends UnifyComponent {

    /**
     * Gets a file list from remote server path.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @return the file list
     * @throws UnifyException
     *             if an error occurs
     */
    List<FileInfo> getRemoteFileList(FileTransferSetup fileTransferSetup) throws UnifyException;

    /**
     * Tests if a file directory exists on remote server.
     * 
     * @param fileTransferSetup
     *            The file server info
     * @return boolean a true value if file directory exists on server otherwise
     *         false
     * @throws UnifyException
     *             If an error occurs
     */
    boolean remoteDirectoryExists(FileTransferSetup fileTransferSetup) throws UnifyException;

    /**
     * Tests if a file exists on remote server.
     * 
     * @param fileTransferSetup
     *            The file server info
     * @param serverFile
     *            the server file name
     * @return boolean a true value if file exists on server otherwise false
     * @throws UnifyException
     *             If an error occurs
     */
    boolean remoteFileExists(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException;

    /**
     * Creates a directory, the transfer info remote path, on remote server if does
     * not exist.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @throws UnifyException
     *             if an error occurs
     */
    void createRemoteDirectory(FileTransferSetup fileTransferSetup) throws UnifyException;

    /**
     * Creates a file on remote server truncating if already exists.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @param serverFile
     *            the server file name
     * @throws UnifyException
     *             if an error occurs
     */
    void createRemoteFile(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException;

    /**
     * Deletes a file on remote server.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @param serverFile
     *            the server file name
     * @throws UnifyException
     *             if an error occurs
     */
    void deleteRemoteFile(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException;

    /**
     * Reads a block of bytes from file on remote file server.
     * 
     * @param fileTransferSetup
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
    byte[] readRemoteBlock(FileTransferSetup fileTransferSetup, String serverFile, long index, int size)
            throws UnifyException;

    /**
     * Gets a file list from local path.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @return the file list
     * @throws UnifyException
     *             if an error occurs
     */
    List<FileInfo> getLocalFileList(FileTransferSetup fileTransferSetup) throws UnifyException;

    /**
     * Creates a file on local truncating if already exists.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @param localFile
     *            the local file name
     * @throws UnifyException
     *             if an error occurs
     */
    void createLocalFile(FileTransferSetup fileTransferSetup, String localFile) throws UnifyException;

    /**
     * Creates a local directory.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @throws UnifyException
     *             if an error occurs
     */
    void createLocalDirectory(FileTransferSetup fileTransferSetup) throws UnifyException;

    /**
     * Deletes a local file.
     * 
     * @param fileTransferSetup
     *            the file server info
     * @param localFile
     *            the local file name
     * @throws UnifyException
     *             if an error occurs
     */
    void deleteLocalFile(FileTransferSetup fileTransferSetup, String localFile) throws UnifyException;

    /**
     * Reads a block of bytes from file on local.
     * 
     * @param fileTransferSetup
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
    byte[] readLocalBlock(FileTransferSetup fileTransferSetup, String localFile, long index, int size)
            throws UnifyException;

    /**
     * Upload a file to remote file server.
     * 
     * @param fileTransferSetup
     *            the file transfer information
     * @param serverFile
     *            the server file name
     * @param localFile
     *            the local file name
     * @throws UnifyException
     *             if an error occurs
     */
    void uploadFile(FileTransferSetup fileTransferSetup, String serverFile, String localFile) throws UnifyException;

    /**
     * Upload all files including subfolders to remote file server.
     * 
     * @param fileTransferSetup
     *            the file transfer information
     * @throws UnifyException
     *             if an error occurs
     */
    void uploadFiles(FileTransferSetup fileTransferSetup) throws UnifyException;

    /**
     * Download a file from remote file server.
     * 
     * @param fileTransferSetup
     *            the file transfer information
     * @param serverFile
     *            the server file name
     * @param localFile
     *            the local file name
     * @throws UnifyException
     *             if an error occurs
     */
    void downloadFile(FileTransferSetup fileTransferSetup, String serverFile, String localFile) throws UnifyException;

    /**
     * Download all files including subfolders from remote file server.
     * 
     * @param fileTransferSetup
     *            the file transfer information
     * @throws UnifyException
     *             if an error occurs
     */
    void downloadFiles(FileTransferSetup fileTransferSetup) throws UnifyException;
}
