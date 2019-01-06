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

import com.tcdng.unify.core.UnifyException;

/**
 * Represents a unicast server communicator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UnicastClientCommunicator extends NetworkUnicastCommunicator {

    /**
     * Performs a unicase client communication.
     * 
     * @param message
     *            the message to send
     * @return a response message if any
     * @throws UnifyException
     *             if an error occurs
     */
    NetworkMessage communicate(NetworkMessage message) throws UnifyException;
}
