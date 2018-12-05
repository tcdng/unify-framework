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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;

/**
 * Network interface tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class NetworkInterfaceTest extends AbstractUnifyComponentTest {

	private static final String TESTUNICASTCLIENTCOMM = "test-textunicastclientcomm";

	private static final String TESTUNICASTSERVERCOMM = "test-textunicastservercomm";

	private static final String TESTMULTICASTCLIENTCOMM = "test-datamulticastclientcomm";

	private static final String TESTMULTICASTSERVERCOMM = "test-datamulticastservercomm";

	@Test
	public void testConfigureNetworkInterface() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.configure(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, "test-unicastcfg1",
				TESTUNICASTCLIENTCOMM, "localhost", 10001, 20);
	}

	@Test(expected = UnifyException.class)
	public void testConfigureNetworkInterfaceIncompatible() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, "test-unicastcfg2",
				TESTUNICASTCLIENTCOMM, "localhost", 10002, 20);
	}

	@Test(expected = UnifyException.class)
	public void testConfigureNetworkInterfaceExists() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.configure(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, "test-unicastcfg3",
				TESTUNICASTCLIENTCOMM, "localhost", 10003, 20);
		networkInterface.configure(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, "test-unicastcfg3",
				TESTUNICASTCLIENTCOMM, "localhost", 10004, 20);
	}

	@Test(expected = UnifyException.class)
	public void testStartLocalUnicastServerNoConfig() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.startLocalUnicastServer("none-config");
	}

	@Test
	public void testStartLocalUnicastServer() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, "test-unicastcfg5",
				TESTUNICASTSERVERCOMM, "localhost", 10005, 20);
		networkInterface.startLocalUnicastServer("test-unicastcfg5");
		networkInterface.stopLocalUnicastServer("test-unicastcfg5");
	}

	@Test(expected = UnifyException.class)
	public void testStopLocalUnicastServerNotExist() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, "test-unicastcfg6",
				TESTUNICASTSERVERCOMM, "localhost", 10006, 20);
		networkInterface.stopLocalUnicastServer("test-unicastcfg6");
	}

	@Test
	public void testEstablishUnicast() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		String id = null;
		try {
			networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, "test-unicastcfg7",
					TESTUNICASTSERVERCOMM, "localhost", 10007, 20);
			networkInterface.configure(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, "test-unicastcfg7-client",
					TESTUNICASTCLIENTCOMM, "localhost", 10007, 20);
			networkInterface.startLocalUnicastServer("test-unicastcfg7");
			id = networkInterface.establishUnicast("test-unicastcfg7-client");
		} finally {
			networkInterface.stopLocalUnicastServer("test-unicastcfg7");
		}
		assertNotNull(id);
	}

	@Test
	public void testDestroyUnicast() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		try {
			networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, "test-unicastcfg8",
					TESTUNICASTSERVERCOMM, "localhost", 10008, 20);
			networkInterface.configure(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, "test-unicastcfg8-client",
					TESTUNICASTCLIENTCOMM, "localhost", 10008, 20);
			networkInterface.startLocalUnicastServer("test-unicastcfg8");
			String id = networkInterface.establishUnicast("test-unicastcfg8-client");
			networkInterface.destroyUnicast(id);
		} finally {
			networkInterface.stopLocalUnicastServer("test-unicastcfg8");
		}
	}

	@Test
	public void testUnicast() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		try {
			networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, "test-unicastcfg9",
					TESTUNICASTSERVERCOMM, "localhost", 10009, 20);
			networkInterface.configure(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, "test-unicastcfg9-client",
					TESTUNICASTCLIENTCOMM, "localhost", 10009, 20);
			networkInterface.startLocalUnicastServer("test-unicastcfg9");
			String id = networkInterface.establishUnicast("test-unicastcfg9-client");
			TestTextRequest request = new TestTextRequest("Optimus", "Prime");
			TestTextResponse response = (TestTextResponse) networkInterface.unicast(id, request);
			assertNotNull(response);
			assertEquals("Optimus Prime", response.getFullname());
			networkInterface.destroyUnicast(id);
		} finally {
			networkInterface.stopLocalUnicastServer("test-unicastcfg9");
		}
	}

	@Test
	public void testUnicastMultiple() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		try {
			networkInterface.configure(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, "test-unicastcfg10",
					TESTUNICASTSERVERCOMM, "localhost", 10010, 20);
			networkInterface.configure(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, "test-unicastcfg10-client",
					TESTUNICASTCLIENTCOMM, "localhost", 10010, 20);
			networkInterface.startLocalUnicastServer("test-unicastcfg10");
			String id = networkInterface.establishUnicast("test-unicastcfg10-client");
			TestTextResponse response1 = (TestTextResponse) networkInterface.unicast(id,
					new TestTextRequest("Optimus", "Prime"));
			TestTextResponse response2 = (TestTextResponse) networkInterface.unicast(id,
					new TestTextRequest("Albert", "Einstien"));
			TestTextResponse response3 = (TestTextResponse) networkInterface.unicast(id,
					new TestTextRequest("Homer", "Simpson"));
			assertEquals("Optimus Prime", response1.getFullname());
			assertEquals("Albert Einstien", response2.getFullname());
			assertEquals("Homer Simpson", response3.getFullname());
			networkInterface.destroyUnicast(id);
		} finally {
			networkInterface.stopLocalUnicastServer("test-unicastcfg10");
		}
	}

	@Test(expected = UnifyException.class)
	public void testStartLocalMulticastNoConfig() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.startLocalMulticastClient("none-config");
	}

	@Test
	public void testStartLocalMulticastClient() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.configure(NetworkInterfaceConfigType.LOCAL_MULTICAST_CLIENT, "test-multicastcfg1",
				TESTMULTICASTCLIENTCOMM, "225.0.0.1", 20001, 20);
		networkInterface.startLocalMulticastClient("test-multicastcfg1");
		networkInterface.stopLocalMulticastClient("test-multicastcfg1");
	}

	@Test(expected = UnifyException.class)
	public void testStopLocalMulticastClientNotExist() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		networkInterface.configure(NetworkInterfaceConfigType.LOCAL_MULTICAST_CLIENT, "test-multicastcfg2",
				TESTMULTICASTCLIENTCOMM, "225.0.0.1", 20002, 20);
		networkInterface.stopLocalMulticastClient("test-multicastcfg2");
	}

	@Test
	public void testEstablishMuilticast() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		String id = null;
		try {
			networkInterface.configure(NetworkInterfaceConfigType.LOCAL_MULTICAST_CLIENT, "test-multicastcfg3-client",
					TESTMULTICASTCLIENTCOMM, "225.0.0.2", 20003, 20);
			networkInterface.configure(NetworkInterfaceConfigType.REMOTE_MULTICAST_CLIENT, "test-multicastcfg3-server",
					TESTMULTICASTSERVERCOMM, "225.0.0.2", 20003, 20);
			networkInterface.startLocalMulticastClient("test-multicastcfg3-client");
			id = networkInterface.establishMulticast("test-multicastcfg3-server");
		} finally {
			networkInterface.stopLocalMulticastClient("test-multicastcfg3-client");
		}
		assertNotNull(id);
	}

	@Test
	public void testDestroyMulticast() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		try {
			networkInterface.configure(NetworkInterfaceConfigType.LOCAL_MULTICAST_CLIENT, "test-multicastcfg4-client",
					TESTMULTICASTCLIENTCOMM, "225.0.0.3", 20004, 20);
			networkInterface.configure(NetworkInterfaceConfigType.REMOTE_MULTICAST_CLIENT, "test-multicastcfg4-server",
					TESTMULTICASTSERVERCOMM, "225.0.0.3", 20004, 20);
			networkInterface.startLocalMulticastClient("test-multicastcfg4-client");
			String id = networkInterface.establishMulticast("test-multicastcfg4-server");
			networkInterface.destroyMulticast(id);
		} finally {
			networkInterface.stopLocalMulticastClient("test-multicastcfg4-client");
		}
	}

	@Test(timeout = 4000)
	public void testMulticast() throws Exception {
		NetworkInterface networkInterface = getNetworkInterface();
		try {
			networkInterface.configure(NetworkInterfaceConfigType.LOCAL_MULTICAST_CLIENT, "test-multicastcfg5-client",
					TESTMULTICASTCLIENTCOMM, "225.0.0.4", 20005, 20);
			networkInterface.configure(NetworkInterfaceConfigType.REMOTE_MULTICAST_CLIENT, "test-multicastcfg5-server",
					TESTMULTICASTSERVERCOMM, "225.0.0.4", 20005, 20);
			networkInterface.startLocalMulticastClient("test-multicastcfg5-client");

			TestMulticastClientDataCommunicator tmcdc = (TestMulticastClientDataCommunicator) this
					.getComponent(TESTMULTICASTCLIENTCOMM);
			List<TestMulticastMessage> messages = tmcdc.getMessages();
			while (messages == null || messages.isEmpty()) {
				String id = networkInterface.establishMulticast("test-multicastcfg5-server");
				networkInterface.multicast(id, new TestMulticastMessage('M', 300, 80));
				networkInterface.destroyMulticast(id);
				messages = tmcdc.getMessages();
				Thread.yield();
			}

			TestMulticastMessage message = messages.get(0);
			assertEquals('M', message.getCode());
			assertEquals(300, message.getWidth());
			assertEquals(80, message.getHeight());
			messages.clear();

		} finally {
			networkInterface.stopLocalMulticastClient("test-multicastcfg5-client");
		}
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}

	private NetworkInterface getNetworkInterface() throws Exception {
		return (NetworkInterface) getComponent(ApplicationComponents.APPLICATION_NETWORKINTERFACE);
	}

}
