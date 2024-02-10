/*
 * Copyright 2018-2024 The Code Department.
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

package com.tcdng.unify.core.util.json;

import java.io.IOException;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Json writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class JsonWriter {

    private static final int DEFAULT_CAPACITY = 4;

    private StringBuilder sb;

    private char[] body;

    private boolean[] appendSym;

    private final int capacityBlock;
    
    private int depth;

    public JsonWriter(StringBuilder sb, int capacityBlock) {
        this.sb = sb;
        this.body = new char[capacityBlock];
        this.appendSym = new boolean[capacityBlock];
        this.capacityBlock = capacityBlock;
        this.depth = -1;
    }

    public JsonWriter(StringBuilder sb) {
        this(sb, DEFAULT_CAPACITY);
    }

    public JsonWriter(int capacityBlock) {
        this(new StringBuilder(), capacityBlock);
    }

    public JsonWriter() {
        this(DEFAULT_CAPACITY);
    }

    public int getCapacity() {
        return body.length;
    }
    
    public JsonWriter beginArray() {
        if (depth >= 0 && body[depth] == '[') {
            throw new RuntimeException("Can not open array in array.");
        }
        
        openBody('[');
        return this;
    }

    public JsonWriter beginArray(String fieldName) {
        preWrite();
        sb.append('"').append(fieldName).append("\":[");
        descend('[');
        return this;
    }

    public JsonWriter endArray() {
        closeBody('[', ']');
        return this;
    }

    public JsonWriter beginObject() {
        openBody('{');
        return this;
    }

    public JsonWriter beginObject(String fieldName) {
        preWrite();
        sb.append('"').append(fieldName).append("\":{");
        descend('{');
        return this;
    }

    public JsonWriter endObject() {
        closeBody('{', '}');
        return this;
    }

    public JsonWriter write(String fieldName, String[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, String val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, Number[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, Number val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, Boolean[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, Boolean val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, char[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, char val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, int[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, int val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, long[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, long val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, short[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, short val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, float[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, float val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, double[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, double val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, boolean[] val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter write(String fieldName, boolean val) {
        preWrite();
        JsonUtils.writeField(sb, fieldName, val);
        return this;
    }

    public JsonWriter writeObject(Object object)
            throws UnifyException {
        preWriteForObject();
        DataUtils.writeJsonObject(object, new JsonWriterOutputStream());
        return this;
    }

    public JsonWriter writeObject(Object[] object)
            throws UnifyException {
        preWriteForObject();
        DataUtils.writeJsonObject(object, new JsonWriterOutputStream());
        return this;
    }

    public JsonWriter writeObject(String fieldName, Object object)
            throws UnifyException {
        preWrite();
        sb.append('"').append(fieldName).append("\":");
        DataUtils.writeJsonObject(object, new JsonWriterOutputStream());
        return this;
    }

    public JsonWriter writeScript(String fieldName, String script)
            throws UnifyException {
        preWrite();
        sb.append('"').append(fieldName).append("\":");
        sb.append(script);
        return this;
    }
    
    @Override
    public String toString() {
        if (depth >= 0) {
            throw new RuntimeException("JSON body is still open.");
        }
        
        return sb.toString();
    }
    
    private class JsonWriterOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            sb.append((char) b);
        }
        
    }
    
    private void preWrite() {
        if (depth < 0) {
            throw new RuntimeException("No body is open.");
        }

        if(body[depth] == '[') {
            throw new RuntimeException("Can not write field to array.");
        }
        
        if (appendSym[depth]) {
            sb.append(',');
        } else {
            appendSym[depth] = true;
        }
    }
    
    private void preWriteForObject() {
        if (depth >= 0) {
            if (appendSym[depth]) {
                sb.append(',');
            } else {
                appendSym[depth] = true;
            }
        }
    }

    private void openBody(char startChar) {
        if(depth >= 0) {
            if (appendSym[depth]) {
                sb.append(',');
            } else {
                appendSym[depth] = true;
            }
        }
        sb.append(startChar);
        descend(startChar);
    }

    private void descend(char startChar) {
        depth++;
        if (depth == body.length) {
            char[] newBody = new char[body.length + capacityBlock];
            System.arraycopy(body, 0, newBody, 0, body.length);
            body = newBody;

            boolean[] newAppendSym = new boolean[appendSym.length + capacityBlock];
            System.arraycopy(appendSym, 0, newAppendSym, 0, appendSym.length);
            appendSym = newAppendSym;
        }

        body[depth] = startChar;
        appendSym[depth] = false;
    }
    
    private void closeBody(char startChar, char endChar) {
        if (depth < 0) {
            throw new RuntimeException("No open body to close.");
        }

        if (startChar != body[depth]) {
            throw new RuntimeException("Body starting with \'" + body[depth] + "\' is still open.");
        }

        sb.append(endChar);
        // ascend
        depth--;
    }
}
