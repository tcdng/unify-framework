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
package com.tcdng.unify.core.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Interface for object stream read/write device.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ObjectStreamer extends UnifyComponent {

    /**
     * Reads an object from supplied input stream.
     * 
     * @param type
     *            object type
     * @param inputStream
     *            input stream to read from
     * @param charset
     *            character set
     * @return read object
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException;

    /**
     * Reads an object from supplied input stream.
     * 
     * @param type
     *            object type
     * @param inputStream
     *            input stream to read from
     * @return read object
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T unmarshal(Class<T> type, InputStream inputStream) throws UnifyException;

    /**
     * Reads an object from supplied reader.
     * 
     * @param type
     *            object type
     * @param reader
     *            reader to read from
     * @return read object
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T unmarshal(Class<T> type, Reader reader) throws UnifyException;

    /**
     * Reads an object from supplied string.
     * 
     * @param type
     *            object type
     * @param string
     *            string to read
     * @return read object
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T unmarshal(Class<T> type, String string) throws UnifyException;

    /**
     * Writes an object to supplied output stream.
     * 
     * @param object
     *            the object to write
     * @param outputStream
     *            the stream to write to
     * @param charset
     *            character set
     * @throws UnifyException
     *             if an error occurs
     */
    void marshal(Object object, OutputStream outputStream, Charset charset) throws UnifyException;

    /**
     * Writes an object to supplied output stream.
     * 
     * @param object
     *            the object to write
     * @param outputStream
     *            the stream to write to
     * @throws UnifyException
     *             if an error occurs
     */
    void marshal(Object object, OutputStream outputStream) throws UnifyException;

    /**
     * Writes an object to supplied writer.
     * 
     * @param object
     *            the object to write
     * @param writer
     *            the writer to write to
     * @throws UnifyException
     *             if an error occurs
     */
    void marshal(Object object, Writer writer) throws UnifyException;

    /**
     * Writes an object.
     * 
     * @param object
     *            the object to write
     * @param writer
     *            the writer to write to
     * @throws UnifyException
     *             if an error occurs
     * @return String the string result
     */
    String marshal(Object object) throws UnifyException;
}
