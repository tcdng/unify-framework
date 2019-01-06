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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for a primitive-data-based unicast communication.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractUnicastDataCommunicator extends AbstractNetworkUnicastCommunicator {

    private DataInputStream in;

    private DataOutputStream out;

    @Override
    protected void onOpen(InputStream in, OutputStream out) throws UnifyException {
        this.in = new DataInputStream(in);
        this.out = new DataOutputStream(out);
    }

    @Override
    protected void onClose() throws UnifyException {
        IOUtils.close(in);
        IOUtils.close(out);
        in = null;
        out = null;
    }

    @Override
    protected void flushWrite() throws UnifyException {
        try {
            out.flush();
        } catch (IOException e) {
            throwTransmitException(e);
        }
    }

    /**
     * Reads bytes into buffer.
     * 
     * @param buffer
     *            the buffer to read into
     * @return the number of bytes read
     * @throws UnifyException
     *             if an error occurs
     */
    protected int readBytes(byte[] buffer) throws UnifyException {
        try {
            return in.read(buffer);
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads bytes into portion of buffer.
     * 
     * @param buffer
     *            the buffer to read into
     * @param offset
     *            the offset
     * @param length
     *            the number of bytes to read
     * @return the number of bytes read
     * @throws UnifyException
     *             if an error occurs
     */
    protected int readBytes(byte[] buffer, int offset, int length) throws UnifyException {
        try {
            return in.read(buffer, offset, length);
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads a boolean.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected boolean readBoolean() throws UnifyException {
        try {
            return in.readBoolean();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return false;
    }

    /**
     * Reads a byte.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected byte readByte() throws UnifyException {
        try {
            return in.readByte();
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
    protected char readChar() throws UnifyException {
        try {
            return in.readChar();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads a double.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected double readDouble() throws UnifyException {
        try {
            return in.readDouble();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads a float.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected float readFloat() throws UnifyException {
        try {
            return in.readFloat();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads an integer.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected int readInt() throws UnifyException {
        try {
            return in.readInt();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads a long.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected long readLong() throws UnifyException {
        try {
            return in.readLong();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Reads a short.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected short readShort() throws UnifyException {
        try {
            return in.readShort();
        } catch (IOException e) {
            throwReceiveException(e);
        }
        return 0;
    }

    /**
     * Writes bytes into buffer.
     * 
     * @param buffer
     *            the buffer to write into
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeBytes(byte[] buffer) throws UnifyException {
        try {
            out.write(buffer);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }
    }

    /**
     * Writes bytes into portion of buffer.
     * 
     * @param buffer
     *            the buffer to write into
     * @param offset
     *            the offset
     * @param length
     *            the number of bytes to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeBytes(byte[] buffer, int offset, int length) throws UnifyException {
        try {
            out.write(buffer, offset, length);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }

    }

    /**
     * Writes a boolean.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeBoolean(boolean data) throws UnifyException {
        try {
            out.writeBoolean(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }
    }

    /**
     * Writes a byte.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeByte(byte data) throws UnifyException {
        try {
            out.writeByte(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }

    }

    /**
     * Writes a character.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeChar(char data) throws UnifyException {
        try {
            out.writeChar(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }

    }

    /**
     * Writes a double.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeDouble(double data) throws UnifyException {
        try {
            out.writeDouble(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }
    }

    /**
     * Writes a float.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeFloat(float data) throws UnifyException {
        try {
            out.writeFloat(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }

    }

    /**
     * Writes an integer.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeInt(int data) throws UnifyException {
        try {
            out.writeInt(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }

    }

    /**
     * Writes a long.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeLong(long data) throws UnifyException {
        try {
            out.writeLong(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }

    }

    /**
     * Writes a short.
     * 
     * @param data
     *            the data to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeShort(short data) throws UnifyException {
        try {
            out.writeShort(data);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }

    }

}
