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

package com.tcdng.unify.core.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;

/**
 * File transfer server based on FTP.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = ApplicationComponents.APPLICATION_FTPTRANSFERSERVER, description = "FTP")
public class FtpFileTransferServer extends AbstractFileTransferServer {

	@Override
	public List<FileInfo> getRemoteFileList(FileTransferInfo fileTransferInfo) throws UnifyException {
		List<FileInfo> list = Collections.emptyList();
		FtpFileFilter ftpFileFilter = new FtpFileFilter(fileTransferInfo);
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			FTPFile[] files = ftpClient.listFiles(getNormalizedRemotePath(fileTransferInfo), ftpFileFilter);
			if (files != null && files.length > 0) {
				list = new ArrayList<FileInfo>();
				for (FTPFile file : files) {
					list.add(new FileInfo(file.getName(), null, file.getSize(), file.getTimestamp().getTimeInMillis(),
							file.getTimestamp().getTimeInMillis(), file.isFile(), false));
				}
			}
		} catch (IOException e) {
			throwOperationErrorException(e);
		} finally {
			restoreFTPClient(ftpClient);
		}
		return list;
	}

	@Override
	public boolean remoteDirectoryExists(FileTransferInfo fileTransferInfo) throws UnifyException {
		return remoteDirectoryExists(fileTransferInfo, getNormalizedRemotePath(fileTransferInfo));
	}

	@Override
	public boolean remoteFileExists(FileTransferInfo fileTransferInfo, String serverFile) throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		InputStream inputStream = null;
		try {
			inputStream = ftpClient.retrieveFileStream(getNormalizedRemotePath(fileTransferInfo) + serverFile);
			if (inputStream == null || ftpClient.getReplyCode() == 550) {
				return false;
			}
		} catch (IOException e) {
			throwOperationErrorException(e);
		} finally {
			IOUtils.close(inputStream);
			restoreFTPClient(ftpClient);
		}
		return true;
	}

	@Override
	public void createRemoteDirectory(FileTransferInfo fileTransferInfo) throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			makeRemoteDirectories(fileTransferInfo, ftpClient, getNormalizedRemotePath(fileTransferInfo));
		} finally {
			restoreFTPClient(ftpClient);
		}
	}

	@Override
	public void createRemoteFile(FileTransferInfo fileTransferInfo, String serverFile) throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			String pathName = getNormalizedRemotePath(fileTransferInfo) + serverFile;
			boolean created = ftpClient.storeFile(pathName, new ByteArrayInputStream(DataUtils.ZEROLEN_BYTE_ARRAY));
			if (!created) {
				throw new Exception("Failed to create file [" + ftpClient.getReplyString() + "].");
			}
		} catch (Exception e) {
			throwOperationErrorException(e);
		} finally {
			restoreFTPClient(ftpClient);
		}
	}

	@Override
	public void deleteRemoteFile(FileTransferInfo fileTransferInfo, String serverFile) throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			String pathName = getNormalizedRemotePath(fileTransferInfo) + serverFile;
			boolean deleted = ftpClient.deleteFile(pathName);
			if (!deleted) {
				throw new Exception("Failed to delete file [" + ftpClient.getReplyCode() + "].");
			}
		} catch (Exception e) {
			throwOperationErrorException(e);
		} finally {
			restoreFTPClient(ftpClient);
		}
	}

	@Override
	public byte[] readRemoteBlock(FileTransferInfo fileTransferInfo, String serverFile, long index, int size)
			throws UnifyException {
		throwUnsupportedOperationException();
		return null;
	}

	@Override
	public void uploadFile(FileTransferInfo fileTransferInfo, String serverFile, String localFile)
			throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			String pathName = getNormalizedRemotePath(fileTransferInfo) + serverFile;
			File actLocalFile = getLocalFile(fileTransferInfo, localFile);
			uploadFile(ftpClient, pathName, actLocalFile, fileTransferInfo.isDeleteSourceOnTransfer());
		} finally {
			restoreFTPClient(ftpClient);
		}
	}

	@Override
	public void uploadFiles(FileTransferInfo fileTransferInfo) throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			String remotePath = getNormalizedRemotePath(fileTransferInfo);
			File localDir = new File(getNormalizedLocalPath(fileTransferInfo));
			FileFilter fileFilter = new FileFilter(fileTransferInfo);
			uploadFiles(fileTransferInfo, ftpClient, remotePath, localDir, fileFilter);
		} finally {
			restoreFTPClient(ftpClient);
		}
	}

	@Override
	public void downloadFile(FileTransferInfo fileTransferInfo, String serverFile, String localFile)
			throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			String pathName = getNormalizedRemotePath(fileTransferInfo) + serverFile;
			File actLocalFile = getLocalFile(fileTransferInfo, localFile);
			downloadFile(ftpClient, pathName, actLocalFile, fileTransferInfo.isDeleteSourceOnTransfer());
		} finally {
			restoreFTPClient(ftpClient);
		}
	}

	@Override
	public void downloadFiles(FileTransferInfo fileTransferInfo) throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			String remotePath = getNormalizedRemotePath(fileTransferInfo);
			File localDir = new File(getNormalizedLocalPath(fileTransferInfo));
			FtpFileFilter ftpFileFilter = new FtpFileFilter(fileTransferInfo);
			downloadFiles(ftpClient, remotePath, localDir, ftpFileFilter, fileTransferInfo.isDeleteSourceOnTransfer());
		} finally {
			restoreFTPClient(ftpClient);
		}
	}

	private void uploadFiles(FileTransferInfo fileTransferInfo, FTPClient ftpClient, String remotePath, File localDir,
			FileFilter fileFilter) throws UnifyException {
		makeRemoteDirectories(fileTransferInfo, ftpClient, remotePath);
		File[] files = localDir.listFiles(fileFilter);
		for (File file : files) {
			if (file.isDirectory()) {
				String newRemotePath = remotePath + file.getName() + '/';
				uploadFiles(fileTransferInfo, ftpClient, newRemotePath, file, fileFilter);
			} else {
				String remoteFile = remotePath + file.getName();
				uploadFile(ftpClient, remoteFile, file, fileTransferInfo.isDeleteSourceOnTransfer());
			}
		}
	}

	private void uploadFile(FTPClient ftpClient, String remoteFile, File localFile, boolean deleteOnTransfer)
			throws UnifyException {
		InputStream inputStream = null;
		try {
			logDebug("Upload: [File: {0}]", localFile.getAbsolutePath());
			inputStream = new FileInputStream(localFile);
			boolean uploaded = ftpClient.storeFile(remoteFile, inputStream);
			if (uploaded) {
				logDebug("Upload: [Status: SENT ]");
				if (deleteOnTransfer) {
					localFile.delete();
					logDebug("Local file deleted.");
				}
			} else {
				throw new Exception("Failed to upload file [" + ftpClient.getReplyString() + "].");
			}
		} catch (Exception e) {
			throwOperationErrorException(e);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	private void downloadFiles(FTPClient ftpClient, String remotePath, File localDir, FtpFileFilter ftpFileFilter,
			boolean deleteOnTransfer) throws UnifyException {
		try {
			localDir.mkdirs();
			FTPFile[] files = ftpClient.listFiles(remotePath, ftpFileFilter);
			for (FTPFile file : files) {
				File localFile = new File(getNormalizedLocalPath(localDir.getAbsolutePath()) + file.getName());
				if (file.isDirectory()) {
					String newRemotePath = remotePath + file.getName() + '/';
					downloadFiles(ftpClient, newRemotePath, localFile, ftpFileFilter, deleteOnTransfer);
				} else {
					String remoteFile = remotePath + file.getName();
					downloadFile(ftpClient, remoteFile, localFile, deleteOnTransfer);
				}
			}
		} catch (IOException e) {
			throwOperationErrorException(e);
		}
	}

	private void downloadFile(FTPClient ftpClient, String remoteFile, File localFile, boolean deleteOnTransfer)
			throws UnifyException {
		OutputStream outputStream = null;
		try {
			logDebug("Download: [File: {0}]", remoteFile);
			outputStream = new FileOutputStream(localFile);
			boolean downloaded = ftpClient.retrieveFile(remoteFile, outputStream);
			if (downloaded) {
				logDebug("Download: [Status: RECEIVED ]");
				if (deleteOnTransfer) {
					ftpClient.deleteFile(remoteFile);
					logDebug("Remote file deleted.");
				}
			} else {
				throw new Exception("Failed to download file [" + ftpClient.getReplyString() + "].");
			}
		} catch (Exception e) {
			throwOperationErrorException(e);
		} finally {
			IOUtils.close(outputStream);
		}
	}

	private void makeRemoteDirectories(FileTransferInfo fileTransferInfo, FTPClient ftpClient, String remotePath)
			throws UnifyException {
		try {
			if (remotePath.startsWith("/")) {
				remotePath = remotePath.substring(1);
			}

			if (remotePath.endsWith("/")) {
				remotePath = remotePath.substring(0, remotePath.length() - 1);
			}

			String[] elems = remotePath.split("/");
			StringBuilder sb = new StringBuilder();
			for (String elem : elems) {
				sb.append('/');
				sb.append(elem);
				String actRemotePath = sb.toString();
				if (!remoteDirectoryExists(fileTransferInfo, actRemotePath)) {
					boolean created = ftpClient.makeDirectory(actRemotePath);
					if (!created) {
						throw new Exception("Failed to create directory [" + ftpClient.getReplyString() + "].");
					}
				}
			}

		} catch (Exception e) {
			throwOperationErrorException(e);
		}
	}

	private boolean remoteDirectoryExists(FileTransferInfo fileTransferInfo, String remotePath) throws UnifyException {
		FTPClient ftpClient = getFTPClient(fileTransferInfo);
		try {
			ftpClient.changeWorkingDirectory(remotePath);
			if (ftpClient.getReplyCode() == 550) {
				return false;
			}
		} catch (IOException e) {
			throwOperationErrorException(e);
		} finally {
			restoreFTPClient(ftpClient);
		}
		return true;
	}

	private FTPClient getFTPClient(FileTransferInfo fileTransferInfo) throws UnifyException {
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(fileTransferInfo.getRemoteHost(), fileTransferInfo.getRemotePort());
			if (!ftpClient.login(fileTransferInfo.getAuthenticationId(),
					fileTransferInfo.getAuthenticationPassword())) {
				throw new Exception("Failed to log into FTP server [" + fileTransferInfo.getRemoteHost() + "].");
			}

			ftpClient.enterLocalPassiveMode();
		} catch (Exception e) {
			throwOperationErrorException(e);
		}

		return ftpClient;
	}

	private void restoreFTPClient(FTPClient ftpClient) throws UnifyException {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException e) {
			throwOperationErrorException(e);
		}
	}

	private class FtpFileFilter extends FileFilter implements FTPFileFilter {

		public FtpFileFilter(FileTransferInfo fileTransferInfo) {
			super(fileTransferInfo.getFilePrefixes(), fileTransferInfo.getFileSuffixes());
		}

		@Override
		public boolean accept(FTPFile ftpFile) {
			return accept(ftpFile.getName(), ftpFile.isFile());
		}

	}
}
