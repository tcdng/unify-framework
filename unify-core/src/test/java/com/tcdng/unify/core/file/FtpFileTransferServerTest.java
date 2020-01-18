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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;

/**
 * FTP file transfer server implementation tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Ignore // Comment Ignore to run
public class FtpFileTransferServerTest extends AbstractUnifyComponentTest {

    private static final String REMOTE_HOST = "localhost";
    private static final int REMOTE_PORT = 2121;
    private static final String REMOTE_AUTH_ID = "admin";
    private static final String REMOTE_AUTH_PASSWORD = "admin";
    private static final String LOCAL_UPLOAD_PATH = "c:\\unify_test\\upload";
    private static final String LOCAL_DOWNLOAD_PATH = "c:\\unify_test\\download";

    @Test
    public void testGetRemoteFileList() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .useAuthenticationId(REMOTE_AUTH_ID).remotePort(REMOTE_PORT)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/filelist").build();
        List<FileInfo> fileInfoList = fileTransferServer.getRemoteFileList(fileTransferSetup);
        assertNotNull(fileInfoList);
        assertEquals(2, fileInfoList.size());

        FileInfo fileInfo = fileInfoList.get(0);
        assertNotNull(fileInfo);
        assertEquals("books", fileInfo.getFilename());
        assertFalse(fileInfo.isFile());

        fileInfo = fileInfoList.get(1);
        assertNotNull(fileInfo);
        assertEquals("Hello.txt", fileInfo.getFilename());
        assertTrue(fileInfo.isFile());

        fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                .remotePath("unify_test/filelist/books").build();
        fileInfoList = fileTransferServer.getRemoteFileList(fileTransferSetup);
        assertNotNull(fileInfoList);
        assertEquals(4, fileInfoList.size());

        fileInfo = fileInfoList.get(0);
        assertNotNull(fileInfo);
        assertEquals("arc", fileInfo.getFilename());
        assertFalse(fileInfo.isFile());

        fileInfo = fileInfoList.get(1);
        assertNotNull(fileInfo);
        assertEquals("Blue Sky.txt", fileInfo.getFilename());
        assertTrue(fileInfo.isFile());

        fileInfo = fileInfoList.get(2);
        assertNotNull(fileInfo);
        assertEquals("Core-Servlets-and-JSP.pdf", fileInfo.getFilename());
        assertTrue(fileInfo.isFile());

        fileInfo = fileInfoList.get(3);
        assertNotNull(fileInfo);
        assertEquals("iText.pdf", fileInfo.getFilename());
        assertTrue(fileInfo.isFile());
    }

    @Test
    public void testGetRemoteFileListWithFilter() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup =
                FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                        .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                        .remotePath("unify_test/filelist").filterByExtension(".pdf").build();
        List<FileInfo> fileInfoList = fileTransferServer.getRemoteFileList(fileTransferSetup);
        assertNotNull(fileInfoList);
        assertEquals(1, fileInfoList.size());

        FileInfo fileInfo = fileInfoList.get(0);
        assertNotNull(fileInfo);
        assertEquals("books", fileInfo.getFilename());
        assertFalse(fileInfo.isFile());

        fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                .remotePath("unify_test/filelist/books").filterByExtension(".pdf").build();
        fileInfoList = fileTransferServer.getRemoteFileList(fileTransferSetup);
        assertNotNull(fileInfoList);
        assertEquals(3, fileInfoList.size());

        fileInfo = fileInfoList.get(0);
        assertNotNull(fileInfo);
        assertEquals("arc", fileInfo.getFilename());
        assertFalse(fileInfo.isFile());

        fileInfo = fileInfoList.get(1);
        assertNotNull(fileInfo);
        assertEquals("Core-Servlets-and-JSP.pdf", fileInfo.getFilename());
        assertTrue(fileInfo.isFile());

        fileInfo = fileInfoList.get(2);
        assertNotNull(fileInfo);
        assertEquals("iText.pdf", fileInfo.getFilename());
        assertTrue(fileInfo.isFile());
    }

    @Test
    public void testRemoteDirectoryExist() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .remotePort(REMOTE_PORT).useAuthenticationId(REMOTE_AUTH_ID)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/filelist").build();
        assertTrue(fileTransferServer.remoteDirectoryExists(fileTransferSetup));

        fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                .remotePath("unify_test/filelist/books").build();
        assertTrue(fileTransferServer.remoteDirectoryExists(fileTransferSetup));
    }

    @Test
    public void testRemoteDirectoryNotExist() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .remotePort(REMOTE_PORT).useAuthenticationId(REMOTE_AUTH_ID)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/filelistA").build();
        assertFalse(fileTransferServer.remoteDirectoryExists(fileTransferSetup));

        fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                .remotePath("unify_test/filelist/booksA").build();
        assertFalse(fileTransferServer.remoteDirectoryExists(fileTransferSetup));
    }

    @Test
    public void testRemoteFileExist() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .remotePort(REMOTE_PORT).useAuthenticationId(REMOTE_AUTH_ID)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/filelist").build();
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "Hello.txt"));

        fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                .remotePath("unify_test/filelist/books").build();
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "Blue Sky.txt"));
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "Core-Servlets-and-JSP.pdf"));
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "iText.pdf"));
    }

    @Test
    public void testCreateRemoteDirectory() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .remotePort(REMOTE_PORT).useAuthenticationId(REMOTE_AUTH_ID)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/newlist").build();
        fileTransferServer.createRemoteDirectory(fileTransferSetup);
        assertTrue(fileTransferServer.remoteDirectoryExists(fileTransferSetup));
    }

    @Test
    public void testCreateRemoteFile() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .remotePort(REMOTE_PORT).useAuthenticationId(REMOTE_AUTH_ID)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/newlist").build();
        fileTransferServer.createRemoteFile(fileTransferSetup, "Sample.txt");
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "Sample.txt"));
    }

    @Test(expected = UnifyException.class)
    public void testCreateRemoteFileInvalidFolder() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .remotePort(REMOTE_PORT).useAuthenticationId(REMOTE_AUTH_ID)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/newlist/samples").build();
        fileTransferServer.createRemoteFile(fileTransferSetup, "Sample2.txt");
    }

    @Test
    public void testDeleteRemoteFile() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST)
                .remotePort(REMOTE_PORT).useAuthenticationId(REMOTE_AUTH_ID)
                .useAuthenticationPassword(REMOTE_AUTH_PASSWORD).remotePath("unify_test/newlist").build();
        fileTransferServer.createRemoteFile(fileTransferSetup, "Sample.txt");
        fileTransferServer.deleteRemoteFile(fileTransferSetup, "Sample.txt");
        assertFalse(fileTransferServer.remoteFileExists(fileTransferSetup, "Sample.txt"));
    }

    @Test
    public void testUploadFile() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup =
                FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                        .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                        .remotePath("unify_test/uploadlist").localPath(LOCAL_UPLOAD_PATH).build();
        fileTransferServer.createRemoteDirectory(fileTransferSetup);
        fileTransferServer.uploadFile(fileTransferSetup, "MyTcdLogo.png", "TcdLogo.png");
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "MyTcdLogo.png"));
    }

    @Test
    public void testUploadFiles() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup =
                FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                        .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                        .remotePath("unify_test/uploadlist/myimages").localPath(LOCAL_UPLOAD_PATH + "\\images").build();
        fileTransferServer.uploadFiles(fileTransferSetup);
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "users.png"));

        fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                .remotePath("unify_test/uploadlist/myimages/set1").build();
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "app_server.png"));
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "branch_inner.png"));

        fileTransferSetup = FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                .remotePath("unify_test/uploadlist/myimages/set2").build();
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "flowcentral.jpg"));
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "merlin.jpg"));
        assertTrue(fileTransferServer.remoteFileExists(fileTransferSetup, "raelyn.jpg"));
    }

    @Test
    public void testDownloadFile() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup =
                FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                        .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                        .remotePath("unify_test/filelist").localPath(LOCAL_DOWNLOAD_PATH).build();
        fileTransferServer.downloadFile(fileTransferSetup, "Hello.txt", "MyHello.txt");
        assertTrue(new File(LOCAL_DOWNLOAD_PATH + "\\MyHello.txt").isFile());
    }

    @Test
    public void testDownloadFiles() throws Exception {
        FileTransferServer fileTransferServer = getFileTransferServer();
        FileTransferSetup fileTransferSetup =
                FileTransferSetup.newBuilder().remoteHost(REMOTE_HOST).remotePort(REMOTE_PORT)
                        .useAuthenticationId(REMOTE_AUTH_ID).useAuthenticationPassword(REMOTE_AUTH_PASSWORD)
                        .remotePath("unify_test/filelist/books").localPath(LOCAL_DOWNLOAD_PATH + "\\mybooks").build();
        fileTransferServer.downloadFiles(fileTransferSetup);
        assertTrue(new File(LOCAL_DOWNLOAD_PATH + "\\mybooks\\Blue Sky.txt").isFile());
        assertTrue(new File(LOCAL_DOWNLOAD_PATH + "\\mybooks\\Core-Servlets-and-JSP.pdf").isFile());
        assertTrue(new File(LOCAL_DOWNLOAD_PATH + "\\mybooks\\iText.pdf").isFile());
        assertTrue(new File(LOCAL_DOWNLOAD_PATH + "\\mybooks\\arc\\Red Sky.txt").isFile());
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

    protected FileTransferServer getFileTransferServer() throws Exception {
        return (FileTransferServer) getComponent(ApplicationComponents.APPLICATION_FTPTRANSFERSERVER);
    }

}
