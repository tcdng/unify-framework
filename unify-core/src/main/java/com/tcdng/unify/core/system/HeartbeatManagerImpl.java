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
package com.tcdng.unify.core.system;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.business.AbstractQueuedExec;
import com.tcdng.unify.core.business.QueuedExec;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Heartbeat manager implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_HEARTBEATMANAGER)
public class HeartbeatManagerImpl extends AbstractBusinessService implements HeartbeatManager {

	private static final int MAX_HEARTBEAT_THREADS = 32;

	private final Map<String, HeartbeatConfig> configs;

	private final QueuedExec<HeartbeatConfig> queuedExec;

	public HeartbeatManagerImpl() {
		this.configs = new ConcurrentHashMap<String, HeartbeatConfig>();
		this.queuedExec = new AbstractQueuedExec<HeartbeatConfig>(MAX_HEARTBEAT_THREADS) {

			@Override
			protected void doExecute(HeartbeatConfig heartbeatConfig) {
				try {
					performHeartbeat(heartbeatConfig);
				} catch (UnifyException e) {
					logError(e);
				} finally {
					heartbeatConfig.setProcessing(false);
				}
			}

		};
	}

	@Override
	public String startHeartbeat(Query<? extends Entity> query, String expiryFieldName, long lifeExtensionInMinutes)
			throws UnifyException {
		final String id = UUID.randomUUID().toString();
		final long _lifeExtensionInMinutes = lifeExtensionInMinutes <= 0 ? 1 : lifeExtensionInMinutes;
		HeartbeatConfig heartbeatConfig = new HeartbeatConfig(id, query.copy(), expiryFieldName,
				_lifeExtensionInMinutes);
		performHeartbeat(heartbeatConfig);
		configs.put(id, heartbeatConfig);
		return id;
	}

	@Override
	public void stopHeartbeat(String heartbeatId) throws UnifyException {
		configs.remove(heartbeatId);
	}

	@Periodic(PeriodicType.SLOWEST)
	public void sustainHeartbeats(TaskMonitor taskMonitor) throws UnifyException {
		for (HeartbeatConfig heartbeatConfig : configs.values()) {
			if (!heartbeatConfig.isProcessing()) {
				heartbeatConfig.setProcessing(true);
				queuedExec.execute(heartbeatConfig);
			}
		}
	}

	private void performHeartbeat(HeartbeatConfig heartbeatConfig) throws UnifyException {
		final Date newExpiryDate = CalendarUtils.getDateWithFrequencyOffset(getNow(), FrequencyUnit.MINUTE,
				heartbeatConfig.getLifeExtensionInMinutes());
		if (db().updateAll(heartbeatConfig.getQuery(),
				new Update().add(heartbeatConfig.getExpiryFieldName(), newExpiryDate)) == 0) {
			stopHeartbeat(heartbeatConfig.getId());
		}
	}

	private class HeartbeatConfig {

		private final String id;

		private final Query<? extends Entity> query;

		private final String expiryFieldName;

		private final long lifeExtensionInMinutes;

		private boolean processing;

		public HeartbeatConfig(String id, Query<? extends Entity> query, String expiryFieldName,
				long lifeExtensionInMinutes) {
			this.id = id;
			this.query = query;
			this.expiryFieldName = expiryFieldName;
			this.lifeExtensionInMinutes = lifeExtensionInMinutes;
		}

		public String getId() {
			return id;
		}

		public Query<? extends Entity> getQuery() {
			return query;
		}

		public String getExpiryFieldName() {
			return expiryFieldName;
		}

		public long getLifeExtensionInMinutes() {
			return lifeExtensionInMinutes;
		}

		public boolean isProcessing() {
			return processing;
		}

		public void setProcessing(boolean processing) {
			this.processing = processing;
		}

	}
}
