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

import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;

/**
 * Represents a multicast client communicator.
 *
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface MulticastServerCommunicator extends NetworkMulticastCommunicator {

	/**
	 * Opens communicator for communication.
	 * 
	 * @param out
	 *            the output stream
	 * @throws UnifyException
	 *             if communication is already open. If an error occurs
	 */
	void open(OutputStream out) throws UnifyException;

	/**
	 * Sends a multicast message.
	 * 
	 * @param message
	 *            the message to send
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void send(NetworkMessage message) throws UnifyException;
}
