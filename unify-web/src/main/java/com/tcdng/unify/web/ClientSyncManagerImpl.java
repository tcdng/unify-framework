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
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.UnifyCoreConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.constant.ClientSyncCommandConstants;
import com.tcdng.unify.web.util.ClientSyncUtils;

/**
 * Client synchronization manager implementation. Horizontally scaled instances
 * will use sticky sessions so there's no need to store sessions in application
 * datasource.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Transactional
@Component(WebApplicationComponents.APPLICATION_CLIENTSYNCMANAGER)
public class ClientSyncManagerImpl extends AbstractBusinessService implements ClientSyncManager {

	private static final String EXPIRATION_HOUSEKEEP_LOCK = "csm::expirationhousekeep-lock";

	@Configurable
	private ClientSyncProcessor clientSyncProcessor;

	private final Map<String, ClientSyncSession> sessions;

	public ClientSyncManagerImpl() {
		this.sessions = new ConcurrentHashMap<String, ClientSyncSession>();
	}

	@Override
	public void openClientSession(ClientSyncSession session) {
		sessions.put(session.getId(), session);
	}

	@Override
	public void processClientMessage(String clientSessionId, String msg) {
		logDebug("Processing client session [{0}] message [{1}]...", clientSessionId, msg);
		try {
			ClientSyncSession session = getSession(clientSessionId);
			ClientSyncMsg syncMsg = DataUtils.fromJsonString(ClientSyncMsg.class, msg);
			if (ClientSyncCommandConstants.OPEN.equals(syncMsg.getCmd())) {
				final String clientId = syncMsg.getParam();
				syncMsg.setClientId(clientId);
				session.setClientId(clientId);
			}

			if (clientSyncProcessor != null) {
				clientSyncProcessor.process(syncMsg);
			}
		} catch (Exception ex) {
			logError(ex);
		}
	}

	@Override
	public void closeClientSession(String clientSessionId, String reason) {
		logDebug("Closing client session [{0}] with reason [{1}]...", clientSessionId, reason);
		try {
			ClientSyncSession session = getSession(clientSessionId);
			ClientSyncMsg syncMsg = new ClientSyncMsg(session.getClientId(), ClientSyncCommandConstants.CLOSE, reason);
			if (clientSyncProcessor != null) {
				clientSyncProcessor.process(syncMsg);
			}
		} catch (Exception ex) {
			logError(ex);
		}
	}

	@Periodic(PeriodicType.NORMAL)
	@Synchronized(EXPIRATION_HOUSEKEEP_LOCK)
	public void performExpirationHouseKeeping(TaskMonitor taskMonitor) throws UnifyException {
		Date now = db().getNow();
		int expirationInSeconds = getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_SESSION_TIMEOUT,
				UnifyCoreConstants.DEFAULT_APPLICATION_SESSION_TIMEOUT_SECONDS);
		Date expiryTime = CalendarUtils.getDateWithOffset(now, -(expirationInSeconds * 1000));
		for (String clientSessionId : new ArrayList<String>(sessions.keySet())) {
			ClientSyncSession session = sessions.get(clientSessionId);
			if (expiryTime.after(session.lastClientCallOn())) {
				sessions.remove(clientSessionId);
				if (clientSyncProcessor != null) {
					clientSyncProcessor
							.process(new ClientSyncMsg(session.getClientId(), ClientSyncCommandConstants.EXPIRE, null));
				}
			}
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {
		ClientSyncUtils.registerClientSyncManager(this);
		super.onInitialize();
	}

	private ClientSyncSession getSession(String clientSessionId) {
		ClientSyncSession session = sessions.get(clientSessionId);
		if (session == null) {
			throw new IllegalArgumentException("Supplied client session ID is unknown.");
		}

		session.heartBeat();
		return session;
	}
}
