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
package com.tcdng.unify.core.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Abstract file transfer server.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractFileTransferServer extends AbstractUnifyComponent implements FileTransferServer {

    @Override
    public List<FileInfo> getLocalFileList(FileTransferSetup fileTransferSetup) throws UnifyException {
        FileFilter fileFilter = new FileFilter(fileTransferSetup);
        File[] files = new File(fileTransferSetup.getLocalPath()).listFiles(fileFilter);
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        List<FileInfo> list = new ArrayList<FileInfo>();
        for (File file : files) {
            list.add(new FileInfo(file.getName(), file.getAbsolutePath(), file.length(), file.lastModified(),
                    file.lastModified(), file.isFile(), file.isHidden()));
        }

        return list;
    }

    @Override
    public void createLocalFile(FileTransferSetup fileTransferSetup, String localFile) throws UnifyException {
        FileInputStream fileInputStream = null;
        try {
            File file = getLocalFile(fileTransferSetup, localFile);
            fileInputStream = new FileInputStream(file);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(fileInputStream);
        }
    }

    @Override
    public void createLocalDirectory(FileTransferSetup fileTransferSetup) throws UnifyException {
        File dir = new File(fileTransferSetup.getLocalPath());
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
    }

    @Override
    public void deleteLocalFile(FileTransferSetup fileTransferSetup, String localFile) throws UnifyException {
        File file = getLocalFile(fileTransferSetup, localFile);
        file.delete();
    }

    @Override
    public byte[] readLocalBlock(FileTransferSetup fileTransferSetup, String localFile, long index, int size)
            throws UnifyException {
        byte[] block = null;
        InputStream inputStream = null;
        try {
            File file = getLocalFile(fileTransferSetup, localFile);
            inputStream = IOUtils.openFileInputStream(file.getAbsolutePath(), index);
            block = new byte[size];
            IOUtils.read(block, inputStream);
        } finally {
            IOUtils.close(inputStream);
        }
        return block;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected String getNormalizedRemotePath(FileTransferSetup fileTransferSetup) throws UnifyException {
        return getNormalizedRemotePath(fileTransferSetup.getRemotePath());
    }

    protected String getNormalizedRemotePath(String remotePath) throws UnifyException {
        remotePath = remotePath.replace("\\", "/");
        if (!remotePath.startsWith("/")) {
            remotePath = "/" + remotePath;
        }
        if (!remotePath.endsWith("/")) {
            remotePath = remotePath + "/";
        }

        return remotePath;
    }

    protected String getNormalizedLocalPath(FileTransferSetup fileTransferSetup) throws UnifyException {
        return getNormalizedLocalPath(fileTransferSetup.getLocalPath());
    }

    protected String getNormalizedLocalPath(String localPath) throws UnifyException {
        return IOUtils.conformFilePath(localPath);
    }

    protected File getLocalFile(FileTransferSetup fileTransferSetup, String localFile) throws UnifyException {
        String filename = localFile;
        if (fileTransferSetup.getLocalPath() != null) {
            filename = IOUtils.buildFilename(fileTransferSetup.getLocalPath(), filename);
        }
        return new File(filename);
    }

    protected File getLocalFile(String localPath, String localFile) throws UnifyException {
        String filename = localFile;
        if (localPath != null) {
            filename = IOUtils.buildFilename(localPath, filename);
        }
        return new File(filename);
    }
}
