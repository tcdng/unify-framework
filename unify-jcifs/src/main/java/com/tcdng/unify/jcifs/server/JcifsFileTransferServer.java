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
package com.tcdng.unify.jcifs.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.file.AbstractFileTransferServer;
import com.tcdng.unify.core.file.FileInfo;
import com.tcdng.unify.core.file.FileFilter;
import com.tcdng.unify.core.file.FileTransferSetup;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.jcifs.JCIFSApplicationComponents;

/**
 * File transfer server based on JCIFS.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(name = JCIFSApplicationComponents.JCIFS_TRANSFERSERVER, description = "JCIFS (Windows SMB)")
public class JcifsFileTransferServer extends AbstractFileTransferServer {

    @Configurable("8192")
    private int bufferSize;

    @Override
    public List<FileInfo> getRemoteFileList(FileTransferSetup fileTransferSetup) throws UnifyException {
        List<FileInfo> list = Collections.emptyList();
        try {
            SmbFile smbFile = getSmbFile(fileTransferSetup, null);
            SmbFile[] files = smbFile.listFiles(new SMBFileFilter(fileTransferSetup));
            if (files != null && files.length > 0) {
                list = new ArrayList<FileInfo>();
                for (SmbFile file : files) {
                    String name = file.getName();
                    if (name.endsWith("/") || name.endsWith("\\")) {
                        name = name.substring(0, name.length() - 1);
                    }
                    list.add(new FileInfo(name, file.getCanonicalPath(), file.length(), file.createTime(),
                            file.lastModified(), file.isFile(), file.isHidden()));
                }
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return list;
    }

    @Override
    public boolean remoteDirectoryExists(FileTransferSetup fileTransferSetup) throws UnifyException {
        try {
            SmbFile smbFile = getSmbFile(fileTransferSetup, null);
            return smbFile.exists() && smbFile.isDirectory();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return false;
    }

    @Override
    public boolean remoteFileExists(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException {
        try {
            SmbFile smbFile = getSmbFile(fileTransferSetup, serverFile);
            return smbFile.exists() && smbFile.isFile();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return false;
    }

    @Override
    public void createRemoteDirectory(FileTransferSetup fileTransferSetup) throws UnifyException {
        createRemoteDirectories(getSmbFile(fileTransferSetup, null));
    }

    @Override
    public void createRemoteFile(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException {
        SmbFileOutputStream smbFileOutputStream = null;
        try {
            SmbFile smbFile = getSmbFile(fileTransferSetup, serverFile);
            smbFileOutputStream = new SmbFileOutputStream(smbFile);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(smbFileOutputStream);
        }
    }

    @Override
    public void deleteRemoteFile(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException {
        try {
            SmbFile smbFile = getSmbFile(fileTransferSetup, serverFile);
            smbFile.delete();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
    }

    @Override
    public byte[] readRemoteBlock(FileTransferSetup fileTransferSetup, String serverFile, long index, int size)
            throws UnifyException {
        byte[] block = null;
        SmbFileInputStream smbFileInputStream = null;
        try {
            // Prepare remote file stream
            SmbFile smbFile = getSmbFile(fileTransferSetup, serverFile);
            smbFileInputStream = new SmbFileInputStream(smbFile);
            smbFileInputStream.skip(index);

            // Read block
            block = new byte[size];
            IOUtils.read(block, smbFileInputStream);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(smbFileInputStream);
        }
        return block;
    }

    @Override
    public void uploadFile(FileTransferSetup fileTransferSetup, String serverFile, String localFile)
            throws UnifyException {
        SmbFile remoteSmbFile = getSmbFile(fileTransferSetup, serverFile);
        File actLocalFile = getLocalFile(fileTransferSetup, localFile);
        uploadFile(remoteSmbFile, actLocalFile, fileTransferSetup.isDeleteSourceOnTransfer());
    }

    @Override
    public void uploadFiles(FileTransferSetup fileTransferSetup) throws UnifyException {
        String remotePath = getNormalizedRemotePath(fileTransferSetup);
        NtlmPasswordAuthentication auth = getAuthentication(fileTransferSetup);
        File localDir = new File(getNormalizedLocalPath(fileTransferSetup));
        FileFilter fileFilter = new FileFilter(fileTransferSetup);
        uploadFiles(fileTransferSetup, auth, remotePath, localDir, fileFilter);
    }

    @Override
    public void downloadFile(FileTransferSetup fileTransferSetup, String serverFile, String localFile)
            throws UnifyException {
        SmbFile remoteSmbFile = getSmbFile(fileTransferSetup, serverFile);
        File actLocalFile = getLocalFile(fileTransferSetup, localFile);
        downloadFile(remoteSmbFile, actLocalFile, fileTransferSetup.isDeleteSourceOnTransfer());
    }

    @Override
    public void downloadFiles(FileTransferSetup fileTransferSetup) throws UnifyException {
        String remotePath = getNormalizedRemotePath(fileTransferSetup);
        NtlmPasswordAuthentication auth = getAuthentication(fileTransferSetup);
        File localDir = new File(getNormalizedLocalPath(fileTransferSetup));
        SMBFileFilter smbFileFilter = new SMBFileFilter(fileTransferSetup);
        downloadFiles(fileTransferSetup, auth, remotePath, localDir, smbFileFilter);
    }

    private void uploadFiles(FileTransferSetup fileTransferSetup, NtlmPasswordAuthentication auth, String remotePath,
            File localDir, FileFilter fileFilter) throws UnifyException {
        SmbFile remoteFile = getSmbFile(fileTransferSetup, auth, remotePath, null);
        createRemoteDirectories(remoteFile);
        File[] files = localDir.listFiles(fileFilter);
        for (File file : files) {
            if (file.isDirectory()) {
                String newRemotePath = remotePath + file.getName() + '/';
                uploadFiles(fileTransferSetup, auth, newRemotePath, file, fileFilter);
            } else {
                SmbFile remoteSmbFile = getSmbFile(fileTransferSetup, auth, remotePath, file.getName());
                uploadFile(remoteSmbFile, file, fileTransferSetup.isDeleteSourceOnTransfer());
            }
        }
    }

    private void uploadFile(SmbFile remoteSmbFile, File localFile, boolean deleteSourceOnTransfer)
            throws UnifyException {
        SmbFileOutputStream smbFileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            logDebug("Upload: [File: {0}]", localFile.getName());
            smbFileOutputStream = new SmbFileOutputStream(remoteSmbFile);
            fileInputStream = new FileInputStream(localFile);

            // Upload
            byte[] buffer = new byte[bufferSize];
            int read = 0;
            while ((read = fileInputStream.read(buffer)) >= 0) {
                smbFileOutputStream.write(buffer, 0, read);
                logDebug("Upload: [Data: {0}]", read);
            }
            logDebug("Upload: [Status: SENT ]");

            if (deleteSourceOnTransfer) {
                IOUtils.close(fileInputStream);
                localFile.delete();
                logDebug("Local file deleted.");
            }
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(smbFileOutputStream);
            IOUtils.close(fileInputStream);
        }
    }

    private void downloadFiles(FileTransferSetup fileTransferSetup, NtlmPasswordAuthentication auth, String remotePath,
            File localDir, SMBFileFilter smbFileFilter) throws UnifyException {
        try {
            localDir.mkdirs();
            SmbFile remoteFile = getSmbFile(fileTransferSetup, auth, remotePath, null);
            SmbFile[] files = remoteFile.listFiles(smbFileFilter);
            for (SmbFile file : files) {
                File localFile = new File(getNormalizedLocalPath(localDir.getAbsolutePath()) + file.getName());
                if (file.isDirectory()) {
                    String newRemotePath = remotePath + file.getName() + '/';
                    downloadFiles(fileTransferSetup, auth, newRemotePath, localFile, smbFileFilter);
                } else {
                    SmbFile actRemoteFile = getSmbFile(fileTransferSetup, auth, remotePath, file.getName());
                    downloadFile(actRemoteFile, localFile, fileTransferSetup.isDeleteSourceOnTransfer());
                }
            }
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
    }

    private void downloadFile(SmbFile remoteSmbFile, File localFile, boolean deleteSourceOnTransfer)
            throws UnifyException {
        SmbFileInputStream smbFileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            logDebug("Download: [File: {0}]", localFile);
            fileOutputStream = new FileOutputStream(localFile);
            smbFileInputStream = new SmbFileInputStream(remoteSmbFile);

            // Download
            byte[] buffer = new byte[bufferSize];
            int read = 0;
            while ((read = smbFileInputStream.read(buffer)) >= 0) {
                fileOutputStream.write(buffer, 0, read);
                logDebug("Download: [Data: {0}]", read);
            }
            logDebug("Download: [Status: RECEIVED ]");

            if (deleteSourceOnTransfer) {
                IOUtils.close(smbFileInputStream);
                remoteSmbFile.delete();
                logDebug("Remote file deleted.");
            }
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(fileOutputStream);
            IOUtils.close(smbFileInputStream);
        }
    }

    private void createRemoteDirectories(SmbFile smbFile) throws UnifyException {
        try {
            if (!smbFile.exists()) {
                smbFile.mkdirs();
            }
        } catch (SmbException e) {
            throwOperationErrorException(e);
        }
    }

    private SmbFile getSmbFile(FileTransferSetup fileTransferSetup, String serverFile) throws UnifyException {
        String remotePath = getNormalizedRemotePath(fileTransferSetup);
        NtlmPasswordAuthentication auth = getAuthentication(fileTransferSetup);
        return getSmbFile(fileTransferSetup, auth, remotePath, serverFile);
    }

    private SmbFile getSmbFile(FileTransferSetup fileTransferSetup, NtlmPasswordAuthentication auth, String remotePath,
            String serverFile) throws UnifyException {
        SmbFile smbFile = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("smb://").append(fileTransferSetup.getRemoteHost());
            if (fileTransferSetup.getRemotePort() > 0) {
                sb.append(':').append(fileTransferSetup.getRemotePort());
            }

            sb.append(remotePath);
            if (serverFile != null) {
                sb.append(serverFile);
            }

            String smbUrl = sb.toString();
            logDebug("Obtaining SMB file: File: [{0}]", smbUrl);
            if (auth != null) {
                smbFile = new SmbFile(smbUrl, auth);
            } else {
                smbFile = new SmbFile(smbUrl);
            }
        } catch (MalformedURLException e) {
            throwOperationErrorException(e);
        }
        return smbFile;
    }

    private NtlmPasswordAuthentication getAuthentication(FileTransferSetup fileTransferSetup) {
        NtlmPasswordAuthentication auth = null;
        if (fileTransferSetup.getAuthenticationId() != null) {
            String domain = null;
            String loginId = fileTransferSetup.getAuthenticationId();
            int domainIndex = loginId.indexOf('\\');
            if (domainIndex < 0) {
                domainIndex = loginId.indexOf('/');
            }

            if (domainIndex >= 0) {
                domain = loginId.substring(0, domainIndex);
                loginId = loginId.substring(domainIndex + 1);
            }

            auth = new NtlmPasswordAuthentication(domain, loginId, fileTransferSetup.getAuthenticationPassword());
        }

        return auth;
    }

    private class SMBFileFilter extends FileFilter implements SmbFileFilter {

        public SMBFileFilter(FileTransferSetup fileTransferSetup) {
            super(fileTransferSetup.getFilePrefixes(), fileTransferSetup.getFileSuffixes());
        }

        @Override
        public boolean accept(SmbFile smbFile) throws SmbException {
            return accept(smbFile.getName(), smbFile.isFile());
        }
    }
}
