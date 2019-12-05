/*
 * Copyright 2018-2019 The Code Department.
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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;

/**
 * A file transfer server for test purposes.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TestFileTransferServer extends AbstractFileTransferServer {

    @Configurable
    private String[] localFilenames;

    @Configurable
    private String[] remoteFilenames;

    private Map<String, FileInfo> localFiles;

    private Map<String, FileInfo> remoteFiles;

    public TestFileTransferServer() {
        localFiles = new HashMap<String, FileInfo>();
        remoteFiles = new HashMap<String, FileInfo>();
    }

    @Override
    public List<FileInfo> getLocalFileList(FileTransferSetup fileTransferSetup) throws UnifyException {
        return new ArrayList<FileInfo>(localFiles.values());
    }

    @Override
    public void createLocalFile(FileTransferSetup fileTransferSetup, String localFile) throws UnifyException {
        if (!localFiles.containsKey(localFile)) {
            localFiles.put(localFile, new FileInfo(localFile, localFile, 0, 0, 0, true, false));
        }
    }

    @Override
    public void createLocalDirectory(FileTransferSetup fileTransferSetup) throws UnifyException {
    }

    @Override
    public void deleteLocalFile(FileTransferSetup fileTransferSetup, String localFile) throws UnifyException {
        remoteFiles.remove(localFile);
    }

    @Override
    public List<FileInfo> getRemoteFileList(FileTransferSetup fileTransferSetup) throws UnifyException {
        return new ArrayList<FileInfo>(remoteFiles.values());
    }

    @Override
    public boolean remoteDirectoryExists(FileTransferSetup fileTransferSetup) throws UnifyException {
        return true;
    }

    @Override
    public boolean remoteFileExists(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException {
        return remoteFiles.containsKey(serverFile);
    }

    @Override
    public void createRemoteDirectory(FileTransferSetup fileTransferSetup) throws UnifyException {
        throwUnsupportedOperationException();
    }

    @Override
    public void createRemoteFile(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException {
        if (!remoteFiles.containsKey(serverFile)) {
            remoteFiles.put(serverFile, new FileInfo(serverFile, serverFile, 0, 0, 0, true, false));
        }
    }

    @Override
    public void deleteRemoteFile(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException {
        remoteFiles.remove(serverFile);
    }

    @Override
    public byte[] readRemoteBlock(FileTransferSetup fileTransferSetup, String serverFile, long index, int size)
            throws UnifyException {
        return null;
    }

    @Override
    public void uploadFile(FileTransferSetup fileTransferSetup, String serverFile, String localFile)
            throws UnifyException {
        if (!localFiles.containsKey(localFile)) {
            throwOperationErrorException(new FileNotFoundException(localFile));
        }
    }

    @Override
    public void downloadFile(FileTransferSetup fileTransferSetup, String serverFile, String localFile)
            throws UnifyException {
        if (!remoteFiles.containsKey(serverFile)) {
            throwOperationErrorException(new FileNotFoundException(serverFile));
        }
    }

    @Override
    public void uploadFiles(FileTransferSetup fileTransferSetup) throws UnifyException {

    }

    @Override
    public void downloadFiles(FileTransferSetup fileTransferSetup) throws UnifyException {

    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();

        if (localFilenames != null) {
            for (String localFilename : localFilenames) {
                localFiles.put(localFilename, new FileInfo(localFilename, localFilename, 0, 0, 0, true, false));
            }
        }

        if (remoteFilenames != null) {
            for (String remoteFilename : remoteFilenames) {
                remoteFiles.put(remoteFilename, new FileInfo(remoteFilename, remoteFilename, 0, 0, 0, true, false));
            }
        }
    }

}
