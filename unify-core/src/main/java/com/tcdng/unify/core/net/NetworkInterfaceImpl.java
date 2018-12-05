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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Default network interface implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_NETWORKINTERFACE)
public class NetworkInterfaceImpl extends AbstractUnifyComponent implements NetworkInterface {

	private static final Map<NetworkInterfaceConfigType, Class<? extends NetworkCommunicator>> map;

	private static final int MULTICAST_PACKET_SIZE = 512;

	static {
		map = new HashMap<NetworkInterfaceConfigType, Class<? extends NetworkCommunicator>>();
		map.put(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, UnicastServerCommunicator.class);
		map.put(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, UnicastClientCommunicator.class);
		map.put(NetworkInterfaceConfigType.LOCAL_MULTICAST_CLIENT, MulticastClientCommunicator.class);
		map.put(NetworkInterfaceConfigType.REMOTE_MULTICAST_CLIENT, MulticastServerCommunicator.class);
	}

	@Configurable("300") // 5 minutes (300 seconds)
	private int localUnicastSessionTimeout;

	@Configurable("8")
	private int minLocalUnicastServerThreads;

	private Map<String, NetworkInterfaceConfig> networkInterfaceConfigs;

	private Map<String, LocalUnicastServer> localUnicastServers;

	private Map<String, LocalUnicastClient> localUnicastClients;

	private Map<String, LocalMulticastServer> localMulticastServers;

	private Map<String, LocalMulticastClient> localMulticastClients;

	private long idCounter;

	public NetworkInterfaceImpl() {
		networkInterfaceConfigs = new HashMap<String, NetworkInterfaceConfig>();
		localUnicastServers = new ConcurrentHashMap<String, LocalUnicastServer>();
		localUnicastClients = new ConcurrentHashMap<String, LocalUnicastClient>();
		localMulticastServers = new ConcurrentHashMap<String, LocalMulticastServer>();
		localMulticastClients = new ConcurrentHashMap<String, LocalMulticastClient>();
	}

	@Override
	public synchronized void configure(NetworkInterfaceConfigType type, String configName, String communicatorName,
			String host, int port, int maxThreads) throws UnifyException {
		if (networkInterfaceConfigs.containsKey(configName)) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_CONFIG_EXISTS, configName);
		}

		if (getComponentConfig(map.get(type), communicatorName) == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_TYPE_COMM_INCOMPATIBLE, communicatorName,
					type);
		}

		networkInterfaceConfigs.put(configName, new NetworkInterfaceConfig(type, configName, communicatorName,
				host, port,
				maxThreads < minLocalUnicastServerThreads ? minLocalUnicastServerThreads : maxThreads));
	}

	@Override
	public String establishUnicast(String configName) throws UnifyException {
		logDebug("Establishing a unicast connection. Configuration = {0}...", configName);
		NetworkInterfaceConfig nic = getConfig(NetworkInterfaceConfigType.REMOTE_UNICAST_SERVER, configName);
		String id = configName + String.valueOf(++idCounter);
		localUnicastClients.put(id, new LocalUnicastClient(id, nic));
		return id;
	}

	@Override
	public void destroyUnicast(String sessionID) throws UnifyException {
		logDebug("Destroying a unicast connection. ID = {0}...", sessionID);
		LocalUnicastClient localUnicastClient = localUnicastClients.remove(sessionID);
		if (localUnicastClient == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_UNICASTCLIENT_NOT_STARTED, sessionID);
		}
		localUnicastClient.close();
	}

	@Override
	public NetworkMessage unicast(String sessionID, NetworkMessage message) throws UnifyException {
		logDebug("Sending a unicast message. Session ID = {0}, message = [{1}]...", sessionID, message);
		LocalUnicastClient localUnicastClient = localUnicastClients.get(sessionID);
		if (localUnicastClient == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_UNICASTCLIENT_NOT_STARTED, sessionID);
		}

		return localUnicastClient.send(message);
	}

	@Override
	public synchronized void startLocalUnicastServer(String configName) throws UnifyException {
		NetworkInterfaceConfig nic = getConfig(NetworkInterfaceConfigType.LOCAL_UNICAST_SERVER, configName);
		if (localUnicastServers.containsKey(configName)) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_UNICASTSERVER_STARTED, configName);
		}
		LocalUnicastServer localUnicastServer = new LocalUnicastServer(nic);
		new Thread(localUnicastServer).start();
		localUnicastServers.put(configName, localUnicastServer);
	}

	@Override
	public void stopLocalUnicastServer(String configName) throws UnifyException {
		LocalUnicastServer localUnicastServer = localUnicastServers.remove(configName);
		if (localUnicastServer == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_UNICASTSERVER_NOT_STARTED, configName);
		}
		localUnicastServer.close();
	}

	@Override
	public boolean isLocalUnicastServerRunning(String configName) throws UnifyException {
		LocalUnicastServer localUnicastServer = localUnicastServers.get(configName);
		if (localUnicastServer == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_UNICASTSERVER_NOT_STARTED, configName);
		}
		return localUnicastServer.isRunning();
	}

	@Override
	public String establishMulticast(String configName) throws UnifyException {
		logDebug("Establishing a multicast connection. Configuration = {0}...", configName);
		NetworkInterfaceConfig nic = getConfig(NetworkInterfaceConfigType.REMOTE_MULTICAST_CLIENT, configName);
		String id = configName + String.valueOf(++idCounter);
		localMulticastServers.put(id, new LocalMulticastServer(id, nic));
		return id;
	}

	@Override
	public void destroyMulticast(String sessionID) throws UnifyException {
		logDebug("Destroying a multicast connection. ID = {0}...", sessionID);
		LocalMulticastServer localMulticastServer = localMulticastServers.remove(sessionID);
		if (localMulticastServer == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_MULTICASTSERVER_NOT_STARTED, sessionID);
		}
		localMulticastServer.close();
	}

	@Override
	public void multicast(String sessionID, NetworkMessage message) throws UnifyException {
		logDebug("Sending a multicast message. Session ID = {0}, message = [{1}]...", sessionID, message);
		LocalMulticastServer localMulticastServer = localMulticastServers.get(sessionID);
		if (localMulticastServer == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_MULTICASTSERVER_NOT_STARTED, sessionID);
		}

		localMulticastServer.send(message);
	}

	@Override
	public synchronized void startLocalMulticastClient(String configName) throws UnifyException {
		NetworkInterfaceConfig nic = getConfig(NetworkInterfaceConfigType.LOCAL_MULTICAST_CLIENT, configName);
		if (localUnicastServers.containsKey(configName)) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_MULTICLIENT_STARTED, configName);
		}

		LocalMulticastClient localMulticastClient = new LocalMulticastClient(nic);
		new Thread(localMulticastClient).start();
		localMulticastClients.put(configName, localMulticastClient);
	}

	@Override
	public void stopLocalMulticastClient(String configName) throws UnifyException {
		LocalMulticastClient localMulticastClient = localMulticastClients.remove(configName);
		if (localMulticastClient == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_MULTICASTCLIENT_NOT_STARTED, configName);
		}
		localMulticastClient.close();
	}

	@Override
	public boolean isLocalMulticastClientRunning(String configName) throws UnifyException {
		LocalMulticastClient localMulticastClient = localMulticastClients.get(configName);
		if (localMulticastClient == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_MULTICASTCLIENT_NOT_STARTED, configName);
		}
		return localMulticastClient.isRunning();
	}

	@Periodic(PeriodicType.SLOWER)
	public void performHouseKeeping(TaskMonitor taskMonitor) throws UnifyException {
		logDebug("Performing network interface housekeeping. Interface = [{0}]...", getName());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -localUnicastSessionTimeout);
		Date timeOut = calendar.getTime();

		// Destroy expired local unicast clients
		logDebug("Destroying expired local unicast clients...");
		for (String key : localUnicastClients.keySet()) {
			LocalUnicastClient localUnicastClient = localUnicastClients.get(key);
			if (timeOut.after(localUnicastClient.getLastAccessTime())) {
				destroyUnicast(localUnicastClient.getId());
			}
		}

		// Destroy expired local multicast servers
		logDebug("Destroying expired local multicast servers...");
		for (String key : localMulticastServers.keySet()) {
			LocalMulticastServer localMulticastServer = localMulticastServers.get(key);
			if (timeOut.after(localMulticastServer.getLastAccessTime())) {
				destroyUnicast(localMulticastServer.getId());
			}
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private synchronized NetworkInterfaceConfig getConfig(NetworkInterfaceConfigType type, String configName)
			throws UnifyException {
		NetworkInterfaceConfig nic = networkInterfaceConfigs.get(configName);
		if (nic == null) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_CONFIG_UNKNOWN, configName);
		}

		if (!nic.getType().equals(type)) {
			throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_CONFIG_INCOMPATIBLE, configName);
		}

		return nic;
	}

	private class LocalUnicastClient {

		private UnicastClientCommunicator unicastClientCommunicator;

		private Socket socket;

		private String id;

		private Date lastAccessTime;

		public LocalUnicastClient(String id, NetworkInterfaceConfig nic) throws UnifyException {
			boolean success = false;
			try {
				this.id = id;
				socket = new Socket(nic.getHost(), nic.getPort());
				unicastClientCommunicator = (UnicastClientCommunicator) getComponent(nic.getCommunicator());
				unicastClientCommunicator.open(socket.getInputStream(), socket.getOutputStream());
				success = true;
			} catch (UnifyException e) {
				throw e;
			} catch (Exception e) {
				throwOperationErrorException(e);
			} finally {
				if (!success && socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
					}
				}
			}
		}

		public NetworkMessage send(NetworkMessage message) throws UnifyException {
			lastAccessTime = new Date();
			return unicastClientCommunicator.communicate(message);
		}

		public String getId() {
			return id;
		}

		public Date getLastAccessTime() {
			return lastAccessTime;
		}

		public void close() {
			try {
				unicastClientCommunicator.close();
			} catch (UnifyException e) {
			}

			try {
				socket.close();
			} catch (IOException e) {
			}
		}

	}

	private class LocalUnicastServer implements Runnable {

		private List<UnicastServerCommunicationThread> workingThreadList;

		private ExecutorService executor;

		private ServerSocket serverSocket;

		private String communicator;

		private boolean running;

		public LocalUnicastServer(NetworkInterfaceConfig nic) throws UnifyException {
			boolean success = false;
			try {
				workingThreadList = Collections
						.synchronizedList(new ArrayList<UnicastServerCommunicationThread>());
				serverSocket = new ServerSocket(nic.getPort());
				communicator = nic.getCommunicator();
				executor = Executors.newFixedThreadPool(nic.getMaxThreads());
				success = true;
			} catch (IOException e) {
				throw new UnifyException(e, UnifyCoreErrorConstants.NETWORKINTERFACE_UNABLE_BIND_LOCALSERVER,
						nic.getConfigName(), nic.getPort());
			} finally {
				if (!success && serverSocket != null) {
					try {
						serverSocket.close();
					} catch (IOException e) {
					}
				}
			}
		}

		@Override
		public void run() {
			running = true;
			while (running) {
				try {
					Socket socket = serverSocket.accept();
					UnicastServerCommunicator serverCommunicator = (UnicastServerCommunicator) getComponent(
							communicator);
					serverCommunicator.open(socket.getInputStream(), socket.getOutputStream());
					executor.execute(new UnicastServerCommunicationThread(socket, serverCommunicator));
				} catch (Exception e) {
					if (running) {
						logError(e);
						running = false;
					}
				}
			}

			for (UnicastServerCommunicationThread usct : new ArrayList<UnicastServerCommunicationThread>(
					workingThreadList)) {
				usct.close();
			}
			executor.shutdownNow();
		}

		public boolean isRunning() {
			return running;
		}

		public void close() {
			try {
				running = false;
				serverSocket.close();
			} catch (IOException e) {
			}
		}

		private class UnicastServerCommunicationThread implements Runnable {

			private UnicastServerCommunicator serverCommunicator;

			private Socket socket;

			private boolean open;

			public UnicastServerCommunicationThread(Socket socket, UnicastServerCommunicator serverCommunicator) {
				this.socket = socket;
				this.serverCommunicator = serverCommunicator;
				open = true;
			}

			@Override
			public void run() {
				try {
					workingThreadList.add(this);
					while (open && serverCommunicator.communicate()) {
						ThreadUtils.yield();
					}
				} catch (UnifyException e) {
					logError(e);
				} finally {
					workingThreadList.remove(this);
					try {
						socket.close();
					} catch (IOException e) {
					}
				}
			}

			public void close() {
				open = false;
			}
		}
	}

	private class LocalMulticastServer {

		private MulticastServerCommunicator multicastServerCommunicator;

		private MulticastSocket socket;

		private ByteArrayOutputStream out;

		private String id;

		private Date lastAccessTime;

		private InetAddress address;

		private byte[] buffer;

		private int port;

		public LocalMulticastServer(String id, NetworkInterfaceConfig nic) throws UnifyException {
			boolean success = false;
			try {
				address = InetAddress.getByName(nic.getHost());
				port = nic.getPort();
				this.id = id;
				socket = new MulticastSocket();
				multicastServerCommunicator = (MulticastServerCommunicator) getComponent(nic.getCommunicator());
				out = new ByteArrayOutputStream();
				multicastServerCommunicator.open(out);
				buffer = new byte[MULTICAST_PACKET_SIZE];
				success = true;
			} catch (UnifyException e) {
				throw e;
			} catch (Exception e) {
				throwOperationErrorException(e);
			} finally {
				IOUtils.close(out);
				if (!success && socket != null) {
					socket.close();
				}
			}
		}

		public void send(NetworkMessage message) throws UnifyException {
			try {
				lastAccessTime = new Date();
				out.reset();
				multicastServerCommunicator.send(message);
				if (out.size() > MULTICAST_PACKET_SIZE) {
					throw new UnifyException(UnifyCoreErrorConstants.NETWORKINTERFACE_MESSAGE_LARGER_THAN_MAXPACKETSIZE,
							MULTICAST_PACKET_SIZE, out.size());
				}

				out.flush();
				byte[] data = out.toByteArray();
				System.arraycopy(data, 0, buffer, 0, data.length);
				DatagramPacket packet = new DatagramPacket(buffer, MULTICAST_PACKET_SIZE, address, port);
				socket.send(packet);
			} catch (IOException e) {
				throwOperationErrorException(e);
			}
		}

		public String getId() {
			return id;
		}

		public Date getLastAccessTime() {
			return lastAccessTime;
		}

		public void close() {
			try {
				multicastServerCommunicator.close();
			} catch (UnifyException e) {
			}

			IOUtils.close(out);
			socket.close();
		}

	}

	private class LocalMulticastClient implements Runnable {

		private MulticastClientCommunicator multicastClientCommunicator;

		private MulticastSocket socket;

		private ByteArrayInputStream in;

		private byte[] buffer;

		private boolean running;

		public LocalMulticastClient(NetworkInterfaceConfig nic) throws UnifyException {
			boolean success = false;
			try {
				socket = new MulticastSocket(nic.getPort());
				socket.joinGroup(InetAddress.getByName(nic.getHost()));
				buffer = new byte[MULTICAST_PACKET_SIZE];
				in = new ByteArrayInputStream(buffer);
				multicastClientCommunicator = (MulticastClientCommunicator) getComponent(nic.getCommunicator());
				multicastClientCommunicator.open(in);
				success = true;
			} catch (IOException e) {
				throw new UnifyException(e, UnifyCoreErrorConstants.NETWORKINTERFACE_UNABLE_BIND_LOCALCLIENT,
						nic.getConfigName(), nic.getPort());
			} finally {
				if (!success && socket != null) {
					socket.close();
				}
			}
		}

		@Override
		public void run() {
			running = true;
			while (running) {
				try {
					in.reset();
					DatagramPacket pack = new DatagramPacket(buffer, MULTICAST_PACKET_SIZE);
					socket.receive(pack);
					multicastClientCommunicator.receive();
				} catch (Exception e) {
					if (running) {
						logError(e);
						running = false;
					}
				}
			}
		}

		public boolean isRunning() {
			return running;
		}

		public void close() {
			try {
				multicastClientCommunicator.close();
			} catch (UnifyException e) {
			}

			IOUtils.close(in);
			running = false;
			socket.close();
		}
	}
}
