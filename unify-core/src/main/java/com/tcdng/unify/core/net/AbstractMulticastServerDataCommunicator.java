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
package com.tcdng.unify.core.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for a primitive-data-based multicast server
 * communication.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractMulticastServerDataCommunicator extends AbstractMulticastServerCommunicator {

	private DataOutputStream out;

	@Override
	protected void onOpen(OutputStream out) throws UnifyException {
		this.out = new DataOutputStream(out);
	}

	@Override
	protected void onClose() throws UnifyException {
		IOUtils.close(out);
		out = null;
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
		} catch (IOException e) {
			throwTransmitException(e);
		}

	}

}
