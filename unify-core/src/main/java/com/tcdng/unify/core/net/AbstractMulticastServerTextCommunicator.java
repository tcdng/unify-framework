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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for a text-based muticast server communicator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractMulticastServerTextCommunicator extends AbstractMulticastServerCommunicator {

	private BufferedWriter writer;

	@Override
	protected void onOpen(OutputStream out) throws UnifyException {
		writer = new BufferedWriter(new OutputStreamWriter(out));
	}

	@Override
	protected void onClose() throws UnifyException {
		IOUtils.close(writer);
		writer = null;
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
		} catch (IOException e) {
			throwTransmitException(e);
		}
	}

}
