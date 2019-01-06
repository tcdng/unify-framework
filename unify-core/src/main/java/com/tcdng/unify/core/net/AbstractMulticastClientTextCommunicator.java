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
package com.tcdng.unify.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for a text-based multicast client communicator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractMulticastClientTextCommunicator extends AbstractMulticastClientCommunicator {

    private BufferedReader reader;

    @Override
    protected void onOpen(InputStream in) throws UnifyException {
        reader = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    protected void onClose() throws UnifyException {
        IOUtils.close(reader);
        reader = null;
    }

    /**
     * Reads a line of text.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected String readLine() throws UnifyException {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return null;
    }

    /**
     * Reads characters into buffer.
     * 
     * @param buffer
     *            the buffer to read into
     * @return the number of characters read
     * @throws UnifyException
     *             if an error occurs
     */
    protected int readChar(char[] buffer) throws UnifyException {
        try {
            return reader.read(buffer);
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads characters into portion of buffer.
     * 
     * @param buffer
     *            the buffer to read into
     * @param offset
     *            the offset
     * @param length
     *            the number of characters to read
     * @return the number of characters read
     * @throws UnifyException
     *             if an error occurs
     */
    protected int readChar(char[] buffer, int offset, int length) throws UnifyException {
        try {
            return reader.read(buffer, offset, length);
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads a character.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected int readChar() throws UnifyException {
        try {
            return reader.read();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

}
