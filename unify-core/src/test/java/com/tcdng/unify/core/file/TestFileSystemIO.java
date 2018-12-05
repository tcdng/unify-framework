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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * In-memory test file system IO.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("test-filesystemio")
public class TestFileSystemIO extends AbstractFileSystemIO {

	private Map<String, byte[]> files;

	private Map<ByteArrayOutputStream, String> streamReferences;

	public TestFileSystemIO() {
		files = new HashMap<String, byte[]>();
		streamReferences = new HashMap<ByteArrayOutputStream, String>();
	}

	@Override
	public boolean isFile(String absoluteFilename) {
		return files.get(absoluteFilename) != null;
	}

	@Override
	public InputStream openFileInputStream(String filename) throws UnifyException {
		return openFileInputStream(filename, 0);
	}

	@Override
	public InputStream openFileInputStream(String filename, long skip) throws UnifyException {
		try {
			byte[] file = files.get(filename);
			if (file == null) {
				throw new UnifyException(UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, filename);
			}

			InputStream inputStream = new ByteArrayInputStream(file);
			if (skip > 0) {
				inputStream.skip(skip);
			}
			return inputStream;
		} catch (IOException e) {
			throwOperationErrorException(e);
			;
		}
		return null;
	}

	@Override
	public OutputStream openFileOutputStream(String filename) throws UnifyException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		streamReferences.put(outputStream, filename);
		files.put(filename, new byte[0]);
		return outputStream;
	}

	@Override
	public OutputStream openFileOutputStream(String filename, boolean append) throws UnifyException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] file = files.get(filename);
			if (append && file != null) {
				outputStream.write(file);
			} else {
				files.put(filename, new byte[0]);
			}
			streamReferences.put(outputStream, filename);
			return outputStream;
		} catch (IOException e) {
			throwOperationErrorException(e);
			;
		}
		return null;
	}

	@Override
	public void close(OutputStream outputStream) {
		try {
			String filename = streamReferences.get(outputStream);
			if (filename != null) {
				outputStream.flush();
				files.put(filename, ((ByteArrayOutputStream) outputStream).toByteArray());
				streamReferences.remove(outputStream);
			}
		} catch (IOException e) {
		}
	}

}
