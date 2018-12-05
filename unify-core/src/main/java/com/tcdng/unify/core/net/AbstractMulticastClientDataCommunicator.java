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
package com.tcdng.unify.core.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for a primitive-data-based multicast client
 * communicator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractMulticastClientDataCommunicator extends AbstractMulticastClientCommunicator {

	private DataInputStream in;

	@Override
	protected void onOpen(InputStream in) throws UnifyException {
		this.in = new DataInputStream(in);
	}

	@Override
	protected void onClose() throws UnifyException {
		IOUtils.close(this.in);
		this.in = null;
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
			return this.in.read(buffer);
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.read(buffer, offset, length);
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readBoolean();
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readByte();
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readChar();
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readDouble();
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readFloat();
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readInt();
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readLong();
		} catch (IOException e) {
			this.throwReceiveException(e);
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
			return this.in.readShort();
		} catch (IOException e) {
			this.throwReceiveException(e);
		}
		return 0;
	}

}
