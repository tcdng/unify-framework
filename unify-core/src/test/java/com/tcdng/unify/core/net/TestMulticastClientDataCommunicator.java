/*
 * Copyright 2014 The Code Department
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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Singleton;

/**
 * Test multicast client data communicator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("test-datamulticastclientcomm")
@Singleton(true)
public class TestMulticastClientDataCommunicator extends AbstractMulticastClientDataCommunicator {

	private List<TestMulticastMessage> messages;

	public TestMulticastClientDataCommunicator() {
		messages = new ArrayList<TestMulticastMessage>();
	}

	@Override
	public void receive() throws UnifyException {
		messages.add(new TestMulticastMessage(readChar(), readInt(), readInt()));
	}

	public List<TestMulticastMessage> getMessages() {
		return messages;
	}

}
