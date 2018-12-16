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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for an object-based unicast communication.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractUnicastObjectCommunicator extends AbstractNetworkUnicastCommunicator {

    private ObjectInputStream in;

    private ObjectOutputStream out;

    @Override
    protected void onOpen(InputStream in, OutputStream out) throws UnifyException {
        try {
            this.out = new ObjectOutputStream(out); // Sends header
            this.out.flush();
            this.in = new ObjectInputStream(in); // Blocks waiting for stream
                                                 // header (Don't change this
                                                 // order)
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
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
     * Reads an object.
     * 
     * @return the object read
     * @throws UnifyException
     *             if an error occurs
     */
    protected Object readObject() throws UnifyException {
        try {
            return in.readObject();
        } catch (Exception e) {
            throwReceiveException(e);
        }
        return null;
    }

    /**
     * Writes a serializable object.
     * 
     * @param object
     *            the object to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeObject(Serializable object) throws UnifyException {
        try {
            out.writeObject(object);
            if (isAutoFlush()) {
                out.flush();
            }
        } catch (IOException e) {
            throwTransmitException(e);
        }
    }
}
