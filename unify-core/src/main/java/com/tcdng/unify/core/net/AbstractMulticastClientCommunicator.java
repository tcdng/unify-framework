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

import java.io.InputStream;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Abstract network multicast client communicator.
 *
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractMulticastClientCommunicator extends AbstractNetworkMulticastCommunicator
		implements MulticastClientCommunicator {

	@Override
	public void open(InputStream in) throws UnifyException {
		if (open) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKCOMMUNICATOR_OPEN, getName());
		}
		onOpen(in);
		open = true;
	}

	/**
	 * Executed on open of communicator.
	 * 
	 * @param in
	 *            the input stream to communicate with
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected abstract void onOpen(InputStream in) throws UnifyException;
}
