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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for a text-based unicast communication.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractUnicastTextCommunicator extends AbstractNetworkUnicastCommunicator {

	private BufferedReader reader;

	private BufferedWriter writer;

	@Override
	protected void onOpen(InputStream in, OutputStream out) throws UnifyException {
		writer = new BufferedWriter(new OutputStreamWriter(out));
		reader = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	protected void onClose() throws UnifyException {
		IOUtils.close(reader);
		IOUtils.close(writer);
		reader = null;
		writer = null;
	}

	@Override
	protected void flushWrite() throws UnifyException {
		try {
			writer.flush();
		} catch (IOException e) {
			throwTransmitException(e);
		}
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

	/**
	 * Writes text.
	 * 
	 * @param text
	 *            the text to write
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void write(String text) throws UnifyException {
		try {
			writer.write(text);
			if (isAutoFlush()) {
				writer.flush();
			}
		} catch (IOException e) {
			throwTransmitException(e);
		}
	}

	/**
	 * Writes text with a new line.
	 * 
	 * @param text
	 *            the text to write
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void writeLine(String text) throws UnifyException {
		try {
			writer.write(text);
			writer.newLine();
			if (isAutoFlush()) {
				writer.flush();
			}
		} catch (IOException e) {
			throwTransmitException(e);
		}
	}

	/**
	 * Writes a new line.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void writeNewLine() throws UnifyException {
		try {
			writer.newLine();
			if (isAutoFlush()) {
				writer.flush();
			}
		} catch (IOException e) {
			throwTransmitException(e);
		}
	}

	/**
	 * Write characters from buffer.
	 * 
	 * @param buffer
	 *            the buffer to write from
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void writeChar(char[] buffer) throws UnifyException {
		try {
			writer.write(buffer);
			if (isAutoFlush()) {
				writer.flush();
			}
		} catch (IOException e) {
			throwReceiveException(e);
		}
	}

	/**
	 * Writes characters from portion of buffer.
	 * 
	 * @param buffer
	 *            the buffer to write from
	 * @param offset
	 *            the offset
	 * @param length
	 *            the number of characters to write
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void writeChar(char[] buffer, int offset, int length) throws UnifyException {
		try {
			writer.write(buffer, offset, length);
			if (isAutoFlush()) {
				writer.flush();
			}
		} catch (IOException e) {
			throwReceiveException(e);
		}
	}

	/**
	 * Writes a character.
	 * 
	 * @param ch
	 *            the character to write
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void writeChar(char ch) throws UnifyException {
		try {
			writer.write(ch);
			if (isAutoFlush()) {
				writer.flush();
			}
		} catch (IOException e) {
			throwTransmitException(e);
		}
	}
}
