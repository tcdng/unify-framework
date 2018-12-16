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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Represents a network interface.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface NetworkInterface extends UnifyComponent {

    /**
     * Configures this interface.
     * 
     * @param type
     *            the configuration type
     * @param configName
     *            the configuration name
     * @param communicatorName
     *            the communicator name
     * @param host
     *            the host name
     * @param port
     *            the session port
     * @param maxThreads
     *            the maximum active service threads
     * @throws UnifyException
     *             if configuration already exists. If an error occurs
     */
    void configure(NetworkInterfaceConfigType type, String configName, String communicatorName, String host, int port,
            int maxThreads) throws UnifyException;

    /**
     * Establishes a unicast communication session.
     * 
     * @param configName
     *            the remote unicast server configuration name
     * @return the session ID
     * @throws UnifyException
     *             if configuration is unknown. If an error occurs
     */
    String establishUnicast(String configName) throws UnifyException;

    /**
     * Destroys a unicast communication session.
     * 
     * @param sessionID
     *            the session ID
     * @throws UnifyException
     *             if an error occurs
     */
    void destroyUnicast(String sessionID) throws UnifyException;

    /**
     * Sends a unicast message over session.
     * 
     * @param sessionID
     *            the unicast session ID
     * @param message
     *            the message to send
     * @return Response message if any
     * @throws UnifyException
     *             if session with ID is unknown. If an error occurs
     */
    NetworkMessage unicast(String sessionID, NetworkMessage message) throws UnifyException;

    /**
     * Starts a local unicast server.
     * 
     * @param configName
     *            the server config name
     * @throws UnifyException
     *             if port is in use. If local server with config is unknown or
     *             started. If an error occurs
     */
    void startLocalUnicastServer(String configName) throws UnifyException;

    /**
     * Stops local unicast server.
     * 
     * @param configName
     *            the server config name
     * @throws UnifyException
     *             If no local server with config name is running. If an error
     *             occurs
     */
    void stopLocalUnicastServer(String configName) throws UnifyException;

    /**
     * Returns true if local unicast server is running otherwise false.
     * 
     * @param configName
     *            the server config name
     * @throws UnifyException
     *             If local server with config is unknown. If an error occurs
     */
    boolean isLocalUnicastServerRunning(String configName) throws UnifyException;

    /**
     * Establishes a multicast communication session.
     * 
     * @param configName
     *            the remote unicast server configuration name
     * @return the session ID
     * @throws UnifyException
     *             if configuration is unknown. If an error occurs
     */
    String establishMulticast(String configName) throws UnifyException;

    /**
     * Destroys a multicast communication session.
     * 
     * @param sessionID
     *            the session ID
     * @throws UnifyException
     *             if an error occurs
     */
    void destroyMulticast(String sessionID) throws UnifyException;

    /**
     * Sends a multicast message over session.
     * 
     * @param sessionID
     *            the multicast session ID
     * @param message
     *            the message to send
     * @throws UnifyException
     *             if session with ID is unknown. If an error occurs
     */
    void multicast(String sessionID, NetworkMessage message) throws UnifyException;

    /**
     * Starts a local multicast client.
     * 
     * @param configName
     *            the client config name
     * @throws UnifyException
     *             If local client with config is unknown or started. If an error
     *             occurs
     */
    void startLocalMulticastClient(String configName) throws UnifyException;

    /**
     * Stops local multicast client.
     * 
     * @param configName
     *            the client config name
     * @throws UnifyException
     *             If no local client with config name is running. If an error
     *             occurs
     */
    void stopLocalMulticastClient(String configName) throws UnifyException;

    /**
     * Returns true if local multicast client is running otherwise false.
     * 
     * @param configName
     *            the client config name
     * @throws UnifyException
     *             If local client with config is unknown. If an error occurs
     */
    boolean isLocalMulticastClientRunning(String configName) throws UnifyException;
}
