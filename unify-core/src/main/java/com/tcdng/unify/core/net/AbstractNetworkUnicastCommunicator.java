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

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;

/**
 * Abstract network unicast communicator.
 *
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton(false)
public abstract class AbstractNetworkUnicastCommunicator extends AbstractUnifyComponent
        implements NetworkUnicastCommunicator {

    @Configurable("true")
    private boolean autoFlush;

    private boolean open;

    @Override
    public void open(InputStream in, OutputStream out) throws UnifyException {
        if (open) {
            throw new UnifyException(UnifyCoreErrorConstants.NETWORKCOMMUNICATOR_OPEN, getName());
        }
        onOpen(in, out);
        open = true;
    }

    @Override
    public void close() throws UnifyException {
        open = false;
        onClose();
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {
        close();
    }

    /**
     * Returns true if auto flush after every write.
     */
    protected boolean isAutoFlush() {
        return autoFlush;
    }

    /**
     * Throws a receive exception.
     * 
     * @param cause
     *            the cause
     * @throws UnifyException
     *             the receive exception
     */
    protected void throwReceiveException(Exception cause) throws UnifyException {
        throw new UnifyException(cause, UnifyCoreErrorConstants.NETWORKCOMMUNICATOR_RECEIVE_ERROR, getName());
    }

    /**
     * Throws a transmit exception.
     * 
     * @param cause
     *            the cause
     * @throws UnifyException
     *             the transmit exception
     */
    protected void throwTransmitException(Exception cause) throws UnifyException {
        throw new UnifyException(cause, UnifyCoreErrorConstants.NETWORKCOMMUNICATOR_TRANSMIT_ERROR, getName());
    }

    /**
     * Executed on open of communicator.
     * 
     * @param in
     *            the input stream to communicate with
     * @param out
     *            the output stream to communicate with
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void onOpen(InputStream in, OutputStream out) throws UnifyException;

    /**
     * Executed on close of communicator.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void onClose() throws UnifyException;

    /**
     * Flushes transmission stream.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void flushWrite() throws UnifyException;
}
