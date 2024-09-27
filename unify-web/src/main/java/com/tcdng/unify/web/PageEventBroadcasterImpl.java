/*
 * Copyright 2018-2024 The Code Department.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Broadcast;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.constant.ClientSyncCommandConstants;
import com.tcdng.unify.core.constant.TopicEventType;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.EntityChangeEventBroadcaster;
import com.tcdng.unify.web.constant.ServerSyncCommandConstants;

/**
 * Page event broadcaster implementation. Sticky session will return client to
 * same server.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Transactional
@Component(WebApplicationComponents.APPLICATION_PAGEEVENTBROADCASTER)
public class PageEventBroadcasterImpl extends AbstractBusinessService
		implements PageEventBroadcaster, EntityChangeEventBroadcaster {

	private static final int MAX_PROCESSING_THREADS = 64;

	private final ExecutorService broadcastExecutor;

	private Map<String, ClientSyncSession> sessions;

	private Map<String, Set<String>> listenersByTopic;

	public PageEventBroadcasterImpl() {
		this.sessions = new ConcurrentHashMap<String, ClientSyncSession>();
		this.listenersByTopic = new ConcurrentHashMap<String, Set<String>>();
		this.broadcastExecutor = Executors.newFixedThreadPool(MAX_PROCESSING_THREADS);
	}

	@Override
	public void registerClient(ClientSyncSession session) {
		logDebug("Registering client session with client ID [{0}]...", session.getClientId());
		sessions.put(session.getClientId(), session);
	}

	@Override
	public void unregisterClient(String clientId) {
		logDebug("Unregistering client session with client ID [{0}]...", clientId);
		sessions.remove(clientId);
		removeClientFromAllTopics(clientId);
	}

	@Override
	public void processClientEvent(ClientEventMsg eventMsg) {
		logDebug("Processing client event with client ID [{0}], command [{1}] and parameter [{2}]...",
				eventMsg.getClientId(), eventMsg.getCmd(), eventMsg.getParam());
		switch (eventMsg.getCmd()) {
		case ClientSyncCommandConstants.OPEN:
		case ClientSyncCommandConstants.CLOSE:
		case ClientSyncCommandConstants.EXPIRE:
			break;
		case ClientSyncCommandConstants.LISTEN:
			listenToTopic(eventMsg.getClientId(), eventMsg.getParam());
			break;
		case ClientSyncCommandConstants.CREATE:
		case ClientSyncCommandConstants.UPDATE:
		case ClientSyncCommandConstants.DELETE:
			try {
				broadcastTopicEvent(eventMsg.getClientId(), eventMsg.getCmd(), eventMsg.getParam());
			} catch (Exception e) {
				logError(e);
			}
			break;
		default:
		}
	}

	@Override
	public void broadcastEntityChange(TopicEventType eventType, Class<? extends Entity> entityClass)
			throws UnifyException {
		broadcastEntityChange(eventType, entityClass, null);
	}

	@Override
	public void broadcastEntityChange(TopicEventType eventType, Class<? extends Entity> entityClass, Object id)
			throws UnifyException {
		final String topic = id != null ? entityClass.getName() + ":" + id : entityClass.getName();
		broadcastExecutor.execute(new BroadcastThread(new BroadcastReq(eventType.syncCmd(), topic)));
	}

	@Broadcast
	public void broadcastTopicEvent(String... params) throws UnifyException {
		final String srcClientId = params[0];
		final String type = params[1];
		final String topic = params[2];
		logDebug("Broadcasting client event [{0}] for topic [{1}] and originating from client [{2}]...", type, topic,
				srcClientId);

		broadcast(srcClientId, topic, type);
		int index = topic.indexOf(':');
		if (index > 0) {
			final String mainTopic = topic.substring(0, index);
			broadcast(srcClientId, mainTopic, type);
		}
	}

	private class BroadcastReq {
		
		private String cmd;
		
		private String topic;

		public BroadcastReq(String cmd, String topic) {
			this.cmd = cmd;
			this.topic = topic;
		}

		public String getCmd() {
			return cmd;
		}

		public String getTopic() {
			return topic;
		}		
	}

	public class BroadcastThread implements Runnable {

		private final BroadcastReq req;

		public BroadcastThread(BroadcastReq req) {
			this.req = req;
		}

		@Override
		public void run() {
			try {
				broadcastTopicEvent(null, req.getCmd(), req.getTopic());
			} catch (UnifyException e) {
				logError(e);
			}
		}
	}
	
	private void broadcast(String srcClientId, String topic, String type) {
		Set<String> listeners = listenersByTopic.get(topic);
		for (String listeningClientId : new ArrayList<String>(listeners)) {
			if (!srcClientId.equals(listeningClientId)) {
				ClientSyncSession session = sessions.get(listeningClientId);
				if (session != null) {
					session.sendEventToRemote(
							new ServerEventMsg(srcClientId, ServerSyncCommandConstants.REFRESH, type));
				}
			}
		}
	}

	private void listenToTopic(String clientId, String topic) {
		removeClientFromAllTopics(clientId);
		Set<String> listeners = listenersByTopic.get(topic);
		if (listeners == null) {
			synchronized (this) {
				if (listeners == null) {
					listeners = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
					listenersByTopic.put(topic, listeners);
				}
			}
		}

		listeners.add(clientId);
	}

	private void removeClientFromAllTopics(String clientId) {
		for (Set<String> listeners : listenersByTopic.values()) {
			listeners.remove(clientId);
		}
	}
}
