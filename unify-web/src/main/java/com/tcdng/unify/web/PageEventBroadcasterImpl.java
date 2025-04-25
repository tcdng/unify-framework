/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Broadcast;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.business.AbstractQueuedExec;
import com.tcdng.unify.core.business.QueuedExec;
import com.tcdng.unify.core.constant.ClientSyncCommandConstants;
import com.tcdng.unify.core.database.EntityEvent;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.constant.ServerSyncCommandConstants;

/**
 * Page event broadcaster implementation. Sticky session will return client to
 * same server.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Transactional
@Component(WebApplicationComponents.APPLICATION_PAGEEVENTBROADCASTER)
public class PageEventBroadcasterImpl extends AbstractBusinessService implements PageEventBroadcaster {

	private static final int MAX_PROCESSING_THREADS = 64;

	private final QueuedExec<BroadcastReq> queuedExec;

	private Map<String, ClientSyncSession> sessions;

	private Map<String, Set<String>> listenersByTopic;

	public PageEventBroadcasterImpl() {
		this.sessions = new ConcurrentHashMap<String, ClientSyncSession>();
		this.listenersByTopic = new ConcurrentHashMap<String, Set<String>>();
		this.queuedExec = new AbstractQueuedExec<BroadcastReq>(MAX_PROCESSING_THREADS) {

			@Override
			protected void doExecute(BroadcastReq req) {
				if (req.isWithSrcClient()) {
					logDebug("Broadcasting client event [{0}] for topic [{1}] and originating from client [{2}]...",
							req.getCmd(), req.getTopic(), req.getSrcClientId());
					broadcast(req.getSrcClientId(), req.getTopic(), req.getCmd());
				} else {
					logDebug("Broadcasting client event [{0}] for topic [{1}]...", req.getCmd(), req.getTopic());
					broadcast(null, req.getTopic(), req.getCmd());
				}
			}
		};
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

	@Periodic(PeriodicType.FASTEST)
	public void broadcastEntityChange(TaskMonitor taskMonitor) throws UnifyException {
		if (isInterfacesOpen()) {
			for (EntityEvent entityEvent : tm().collectEntityEvents()) {
				final String topic = entityEvent.getId() != null
						? entityEvent.getEntityClass().getName() + ":" + entityEvent.getId()
						: entityEvent.getEntityClass().getName();
				broadcastTopicEvent(entityEvent.getSrcClientId(), entityEvent.getEventType().syncCmd(), topic);
			}
		}
	}

	@Broadcast
	public void broadcastTopicEvent(String... params) throws UnifyException {
		final String srcClientId = params[0];
		final String type = params[1];
		final String topic = params[2];

		queuedExec.execute(new BroadcastReq(srcClientId, type, topic));
		int index = topic.indexOf(':');
		if (index > 0) {
			final String mainTopic = topic.substring(0, index);
			queuedExec.execute(new BroadcastReq(srcClientId, type, mainTopic));
		}
	}

	private class BroadcastReq {

		private String srcClientId;

		private String cmd;

		private String topic;

		public BroadcastReq(String srcClientId, String cmd, String topic) {
			this.srcClientId = srcClientId;
			this.cmd = cmd;
			this.topic = topic;
		}

		public String getSrcClientId() {
			return srcClientId;
		}

		public String getCmd() {
			return cmd;
		}

		public String getTopic() {
			return topic;
		}

		public boolean isWithSrcClient() {
			return !StringUtils.isBlank(srcClientId);
		}
	}

	private void broadcast(String srcClientId, String topic, String type) {
		Set<String> listeners = listenersByTopic.get(topic);
		if (listeners != null) {
			for (String listeningClientId : new ArrayList<String>(listeners)) {
				if (srcClientId == null || !srcClientId.equals(listeningClientId)) {
					ClientSyncSession session = sessions.get(listeningClientId);
					if (session != null) {
						session.sendEventToRemote(
								new ServerEventMsg(srcClientId, ServerSyncCommandConstants.REFRESH, type));
					}
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
