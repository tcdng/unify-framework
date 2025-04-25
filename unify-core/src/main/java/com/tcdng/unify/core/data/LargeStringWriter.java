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

package com.tcdng.unify.core.data;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * A large string writer..
 * 
 * @author The Code Department
 * @since 1.0
 */
public class LargeStringWriter extends Writer {

    private static final int DEFAULT_INITIAL_CAPACITY = 1024;

    private char[] data;

    private int len;

    public LargeStringWriter() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public LargeStringWriter(int initialCapacity) {
        data = new char[initialCapacity];
    }

    @Override
    public void write(int c) {
        ensureFit(1);
        data[len++] = (char) c;
    }

    @Override
    public void write(char[] cbuf) {
        ensureFit(cbuf.length);
        System.arraycopy(cbuf, 0, data, len, cbuf.length);
        len += cbuf.length;
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        ensureFit(len);
        System.arraycopy(cbuf, off, data, this.len, len);
        this.len += len;
    }

    @Override
    public void write(String str) {
        if (str == null) {
            write("null");
            return;
        }

        int len = str.length();
        ensureFit(len);
        str.getChars(0, len, data, this.len);
        this.len += len;
    }

    @Override
    public void write(String str, int off, int len) {
        ensureFit(len);
        str.getChars(off, off + len, data, this.len);
        this.len += len;
    }

    @Override
    public LargeStringWriter append(CharSequence csq) {
        if (csq == null) {
            write("null");
        } else {
            write(csq.toString());
        }

        return this;
    }

    @Override
    public LargeStringWriter append(CharSequence csq, int start, int end) {
        if (csq == null) {
            write("null");
        } else {
            write(csq.subSequence(start, end).toString());
        }

        return this;
    }

    @Override
    public LargeStringWriter append(char c) {
        ensureFit(1);
        data[len++] = c;
        return this;
    }

    public LargeStringWriter append(boolean bool) {
        if (bool) {
            write("true");
        } else {
            write("false");
        }

        return this;
    }

    public LargeStringWriter append(String str) {
        write(str);
        return this;
    }

    public LargeStringWriter append(Object obj) {
        if (obj == null) {
            write("null");
        } else {
            write(String.valueOf(obj));
        }

        return this;
    }

    public LargeStringWriter append(LargeStringWriter lsw) {
        write(lsw.data, 0, lsw.len);
        return this;
    }

    public void writeTo(Writer writer) throws IOException {
        writer.write(data, 0, len);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String toString() {
        return new String(data, 0, len);
    }

    public int capacity() {
        return data.length;
    }

    public int length() {
        return len;
    }

    public boolean isEmpty() {
        return len == 0;
    }

    protected char[] getData() {
        return data;
    }

    private void ensureFit(int numNewChars) {
        int newMinimumRequired = len + numNewChars;
        if (newMinimumRequired > data.length) {
            int newCapacity = data.length << 1; // Double capacity.
            if (newCapacity < newMinimumRequired) {
                newCapacity = newMinimumRequired;
            }

            data = Arrays.copyOf(data, newCapacity);
        }
    }
}
