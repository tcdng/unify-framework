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
package com.tcdng.unify.web;

import java.nio.charset.Charset;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Web client
 * 
 * @author Lateef
 * @since 1.0
 */
public interface WebClient extends UnifyComponent {

    /**
     * Setup remote call using default JSON format and UTF-8 character set.
     * 
     * @param remoteAppURL
     *            the remote application URL
     * @param methodCode
     *            the method code
     * @throws UnifyException
     *             if setup already exists. If an error occurs
     */
    void setupRemoteCall(String remoteAppURL, String methodCode) throws UnifyException;

    /**
     * Setup remote call for messaging.
     * 
     * @param remoteAppURL
     *            the remote application URL
     * @param methodCode
     *            the method code
     * @throws UnifyException
     *             if setup already exists. If an error occurs
     */
    void setupMessagingRemoteCall(String remoteAppURL, String methodCode) throws UnifyException;

    /**
     * Setup remote call.
     * 
     * @param remoteAppURL
     *            the remote application URL
     * @param methodCode
     *            the method code
     * @param format
     *            the message format
     * @param charset
     *            the character set
     * @throws UnifyException
     *             if setup already exists. If an error occurs
     */
    void setupRemoteCall(String remoteAppURL, String methodCode, RemoteCallFormat format, Charset charset)
            throws UnifyException;

    /**
     * Tests if any remote call is setup for supplied remote URL.
     * 
     * @param remoteAppURL
     *            the remote application URL
     * @param methodCode
     *            the method code
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isRemoteCallSetup(String remoteAppURL, String methodCode) throws UnifyException;

    /**
     * Clears all remote call setup for specified application.
     * 
     * @param remoteAppURL
     *            the remote application URL
     * @throws UnifyException
     *             if an error occurs
     */
    void clearAllRemoteCallSetup(String remoteAppURL) throws UnifyException;

    /**
     * Clears remote call setup if found.
     * 
     * @param remoteAppURL
     *            the remote application URL
     * @param methodCode
     *            the method code
     * @throws UnifyException
     *             if an error occurs
     */
    void clearRemoteCallSetup(String remoteAppURL, String methodCode) throws UnifyException;

    /**
     * Sends a message to remote server.
     * 
     * @param remoteAppURL
     *            the remote application URL
     * @param remoteMessage
     *            the remote message to send
     * @return the message acknowledgment
     * @throws UnifyException
     *             if setup with application and code is unknown is unknown. If an
     *             error occurs
     */
    TaggedMessageResult sendMessage(String remoteAppURL, TaggedMessageParams remoteMessage) throws UnifyException;

    /**
     * Executes a remote call.
     * 
     * @param resultType
     *            the result type
     * @param remoteAppURL
     *            the remote application URL
     * @param param
     *            the remote call parameter
     * @return the remote call result
     * @throws UnifyException
     *             if setup with application and code is unknown is unknown. If an
     *             error occurs
     */
    <T extends RemoteCallResult> T remoteCall(Class<T> resultType, String remoteAppURL, RemoteCallParams param)
            throws UnifyException;
}
