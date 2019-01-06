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
package com.tcdng.unify.core;

/**
 * Abstract base for a unify container interface.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractUnifyContainerInterface extends AbstractUnifyComponent
        implements UnifyContainerInterface {

    private boolean servicingRequests;

    @Override
    public void startServicingRequests() throws UnifyException {
        if (!servicingRequests) {
            onStartServicingRequests();
            servicingRequests = true;
        }
    }

    @Override
    public void stopServicingRequests() throws UnifyException {
        if (servicingRequests) {
            onStopServicingRequests();
            servicingRequests = false;
        }
    }

    @Override
    public boolean isServicingRequests() {
        return servicingRequests;
    }

    /**
     * Performs an on-start servicing requests operation.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void onStartServicingRequests() throws UnifyException;

    /**
     * Performs an on-stop servicing requests operation.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void onStopServicingRequests() throws UnifyException;
}
