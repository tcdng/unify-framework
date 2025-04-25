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

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * File system IO component.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface FileSystemIO extends UnifyComponent {

    /**
     * Builds a canonical file name.
     * 
     * @param path
     *            the file path
     * @param filename
     *            the file name
     * @return the proper file name
     */
    String buildFilename(String path, String filename);

    /**
     * Returns true if supplied file name is in file system.
     * 
     * @param absoluteFilename
     *            the file name to test
     */
    boolean isFile(String absoluteFilename);

    /**
     * Opens a file input stream.
     * 
     * @param filename
     *            the file name
     * @return the file input stream
     * @throws UnifyException
     *             if an error occurs
     */
    InputStream openFileInputStream(String filename) throws UnifyException;

    /**
     * Opens a file input stream and skip by specified number of bytes..
     * 
     * @param filename
     *            the file name
     * @param skip
     *            the number of bytes to skip by
     * @return the file input stream
     * @throws UnifyException
     *             if an error occurs
     */
    InputStream openFileInputStream(String filename, long skip) throws UnifyException;

    /**
     * Reads data from input stream into supplied buffer. Reads at most the length
     * of the buffer.
     * 
     * @param buffer
     *            the buffer to read into
     * @param inputStream
     *            the input stream to read from
     * @return the number of bytes read
     * @throws UnifyException
     *             if an error occurs
     */
    int read(byte[] buffer, InputStream inputStream) throws UnifyException;

    /**
     * Reads all data from input stream into a byte array.
     * 
     * @param inputStream
     *            the input stream to read from
     * @return byte[] the resulting byte array
     * @throws UnifyException
     *             if an error occurs
     */
    byte[] readAll(InputStream inputStream) throws UnifyException;

    /**
     * Closes an input stream.
     * 
     * @param inputStream
     *            the input stream to close
     */
    void close(InputStream inputStream);

    /**
     * Opens a file output stream. Truncates file if file already exists.
     * 
     * @param filename
     *            the file name
     * @return the file output stream
     * @throws UnifyException
     *             if an error occurs
     */
    OutputStream openFileOutputStream(String filename) throws UnifyException;

    /**
     * Opens a file output stream.
     * 
     * @param filename
     *            the file name
     * @param append
     *            flag that indicates an append
     * @return the file output stream
     * @throws UnifyException
     *             if an error occurs
     */
    OutputStream openFileOutputStream(String filename, boolean append) throws UnifyException;

    /**
     * Writes all data from input stream to output stream. Closes input stream at
     * end of write.
     * 
     * @param outputStream
     *            the output stream to write to
     * @param inputStream
     *            the input stream to read from
     * @return the number of bytes written
     * @throws UnifyException
     *             if an error occurs
     */
    long writeAll(OutputStream outputStream, InputStream inputStream) throws UnifyException;

    /**
     * Writes all supplied data to specified output stream.
     * 
     * @param outputStream
     *            the output stream to write to
     * @param data
     *            the data to write
     * @return the number of bytes written
     * @throws UnifyException
     *             if an error occurs
     */
    long writeAll(OutputStream outputStream, byte[] data) throws UnifyException;

    /**
     * Closes an output stream.
     * 
     * @param outputStream
     *            the output stream to close
     */
    void close(OutputStream outputStream);
}
