/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyContainerInterface;
import com.tcdng.unify.core.UnifyCoreRequestAttributeConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.data.ReentrantLockFactoryMap;
import com.tcdng.unify.core.system.entities.ClusterCommand;
import com.tcdng.unify.core.system.entities.ClusterCommandParam;
import com.tcdng.unify.core.system.entities.ClusterCommandParamQuery;
import com.tcdng.unify.core.system.entities.ClusterCommandQuery;
import com.tcdng.unify.core.system.entities.ClusterLock;
import com.tcdng.unify.core.system.entities.ClusterLockQuery;
import com.tcdng.unify.core.system.entities.ClusterNode;
import com.tcdng.unify.core.system.entities.ClusterNodeQuery;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.NetworkUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Default implementation of application cluster manager. Uses datasource
 * timestamp to coordinate synchronization of method calls across clusters.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_CLUSTERSERVICE)
public class ClusterServiceImpl extends AbstractBusinessService implements ClusterService {

	private static final String MASTER_LOCK = "master-lock";

	@Configurable("1") // Node expiration in minutes
	private int nodeExpirationPeriod;

	private ReentrantLockFactoryMap<String> lockList;

	private ConcurrentHashMap<String, LockInfo> runtimeDbLocks;

	private Set<String> pseudoLocks;

	public ClusterServiceImpl() {
		lockList = new ReentrantLockFactoryMap<String>();
		runtimeDbLocks = new ConcurrentHashMap<String, LockInfo>();
		pseudoLocks = new HashSet<String>();
	}

	public void setNodeExpirationPeriod(int nodeExpirationPeriod) {
		this.nodeExpirationPeriod = nodeExpirationPeriod;
	}

	@Override
	public String getLockThreadId() throws UnifyException {
		return getRuntimeId() + ':' + ThreadUtils.currentThreadId();
	}

	@Override
	public void beginSynchronization(String lockName) throws UnifyException {
		if (isClusterMode()) {
			while (!grabDbLock(lockName)) {
				ThreadUtils.sleep(50);
			}
		} else {
			lockList.get(lockName).lock();
		}
	}

	@Transactional(TransactionAttribute.REQUIRES_NEW)
	@Override
	public void endSynchronization(String lockName) throws UnifyException {
		if (isClusterMode()) {
			releaseDbLock(lockName);
		} else {
			lockList.get(lockName).unlock();
		}
	}

	@Override
	public boolean grabMasterSynchronizationLock() throws UnifyException {
		return grabSynchronizationLock(MASTER_LOCK);
	}

	@Override
	public boolean grabSynchronizationLock(String lockName) throws UnifyException {
		if (isClusterMode()) {
			return grabDbLock(lockName);
		}

		pseudoLocks.add(lockName);
		return true;
	}

	@Override
	public boolean isWithSynchronizationLock(String lockName) throws UnifyException {
		if (isClusterMode()) {
			final LockInfo lockInfo = runtimeDbLocks.get(lockName);
			return lockInfo != null && lockInfo.isOwnedBy(getLockThreadId());
		}

		return pseudoLocks.contains(lockName);
	}

	@Override
	public boolean releaseMasterSynchronizationLock() throws UnifyException {
		return releaseSynchronizationLock(MASTER_LOCK);
	}

	@Override
	public boolean releaseSynchronizationLock(String lockName) throws UnifyException {
		if (isClusterMode()) {
			return releaseDbLock(lockName);
		}

		return pseudoLocks.remove(lockName);
	}

	@Override
	public List<ClusterLock> findClusterLocks(ClusterLockQuery query) throws UnifyException {
		return db().findAll(query);
	}

	@Override
	public List<ClusterNode> findClusterNodes(ClusterNodeQuery query) throws UnifyException {
		return db().findAll(query);
	}

	@Override
	public void broadcastToOtherNodes(String command, String... params) throws UnifyException {
		if (isClusterMode()
				&& !Boolean.TRUE.equals(getRequestAttribute(UnifyCoreRequestAttributeConstants.SUPPRESS_BROADCAST))) {
			final String runtimeId = getRuntimeId();
			Set<String> nodeIdList = db().valueSet(String.class, "nodeId",
					new ClusterNodeQuery().runtimeNotEqual(runtimeId));
			if (!nodeIdList.isEmpty()) {
				ClusterCommand clusterCommandData = new ClusterCommand();
				clusterCommandData.setCommandCode(command);

				ClusterCommandParam clusterCommandParamData = null;
				boolean isParams = params.length > 0;
				if (isParams) {
					clusterCommandParamData = new ClusterCommandParam();
				}

				for (String nodeId : nodeIdList) {
					clusterCommandData.setNodeId(nodeId);
					Long clusterCommandId = (Long) db().create(clusterCommandData);
					if (isParams) {
						clusterCommandParamData.setClusterCommandId(clusterCommandId);
						for (String param : params) {
							clusterCommandParamData.setParameter(param);
							db().create(clusterCommandParamData);
						}
					}
				}
			}
		}
	}

	@Override
	public List<Command> getClusterCommands() throws UnifyException {
		final String runtimeId = getRuntimeId();
		List<Command> resultList = Collections.emptyList();
		List<ClusterCommand> clusterCommandList = db().findAll(new ClusterCommandQuery().runtimeId(runtimeId));
		if (!clusterCommandList.isEmpty()) {
			List<Long> clusterCommandIdList = new ArrayList<Long>();
			resultList = new ArrayList<Command>();
			for (ClusterCommand clusterCommand : clusterCommandList) {
				Long clusterCommandId = clusterCommand.getId();
				clusterCommandIdList.add(clusterCommandId);
				resultList.add(new Command(clusterCommand.getCommandCode(), db().valueList(String.class, "parameter",
						new ClusterCommandParamQuery().clusterCommandId(clusterCommandId))));
			}

			db().deleteAll(new ClusterCommandParamQuery().clusterCommandIdIn(clusterCommandIdList));
			db().deleteAll(new ClusterCommandQuery().idIn(clusterCommandIdList));
		}
		return resultList;
	}

	@Transactional(TransactionAttribute.REQUIRES_NEW)
	public boolean grabDbLock(String lockName) throws UnifyException {
		final String lockThreadId = getLockThreadId();
		final String runtimeId = getRuntimeId();
		final Date now = db().getNow();
		LockInfo lockInfo = runtimeDbLocks.get(lockName);
		if (lockInfo == null || lockInfo.isExpired(now)) {
			synchronized (runtimeDbLocks) {
				lockInfo = runtimeDbLocks.get(lockName);
				if (lockInfo == null || lockInfo.isExpired(now)) {
					final String nodeId = getNodeId();
					final Date expiresOn = getNextLockExpirationDate(now);
					db().deleteAll(new ClusterLockQuery().expiryTimeBefore(now)); // Ensures table exists
					try {

						ClusterLock clusterLock = new ClusterLock();
						clusterLock.setLockName(lockName);
						clusterLock.setNodeId(nodeId);
						clusterLock.setRuntimeId(runtimeId);
						clusterLock.setCurrentOwner(lockThreadId);
						clusterLock.setExpiryTime(expiresOn);
						clusterLock.setLockCount(Integer.valueOf(1));
						db().create(clusterLock);

						lockInfo = new LockInfo(lockThreadId, expiresOn);
						runtimeDbLocks.put(lockName, lockInfo);
					} catch (Exception e) {
					}
				}
			}
		}

		if (lockInfo != null && lockInfo.isOwnedBy(lockThreadId)) {
			lockInfo.incUsages();
			return true;
		}

		return false;
	}

	@Transactional(TransactionAttribute.REQUIRES_NEW)
	public boolean releaseDbLock(final String lockName) throws UnifyException {
		final String lockThreadId = getLockThreadId();
		final LockInfo lockInfo = runtimeDbLocks.get(lockName);
		if (lockInfo != null && lockInfo.isOwnedBy(lockThreadId)) {
			lockInfo.decUsages();
			if (lockInfo.isDisposable()) {
				try {
					db().deleteAll(new ClusterLockQuery().lockName(lockName).currentOwner(lockThreadId));
				} finally {
					runtimeDbLocks.remove(lockName);
				}
			}

			return true;
		}

		return false;
	}

	@Periodic(value = PeriodicType.FAST, clusterMode = true)
	@Transactional(TransactionAttribute.REQUIRES_NEW)
	public void performHeartBeat(TaskMonitor taskMonitor) throws UnifyException {
		final String runtimeId = getRuntimeId();
		// Send a heart beat.
		final Date now = db().getNow();
		if (db().updateAll(new ClusterNodeQuery().runtimeId(runtimeId), new Update().add("lastHeartBeat", now)) == 0) {
			// Register node
			final String nodeId = getNodeId();
			ClusterNode clusterNode = new ClusterNode();
			clusterNode.setNodeId(nodeId);
			clusterNode.setRuntimeId(runtimeId);
			clusterNode.setLastHeartBeat(now);
			clusterNode.setIpAddress(NetworkUtils.getLocalHostIpAddress());
			UnifyContainerInterface unifyContainerInterface = (UnifyContainerInterface) this
					.getComponent("unify-commandinterface");
			clusterNode.setCommandPort(Integer.valueOf(unifyContainerInterface.getPort()));
			db().create(clusterNode);
		}

		// Extend life of locks held by this node runtime
		if (!runtimeDbLocks.isEmpty()) {
			final Date expiresOn = getNextLockExpirationDate(now);
			db().updateAll(new ClusterLockQuery().runtimeId(runtimeId).expiryTimeAfter(now).expiryTimeBefore(expiresOn)
					.lockNameIn(runtimeDbLocks.keySet()), new Update().add("expiryTime", expiresOn));
			for (Map.Entry<String, LockInfo> entry : runtimeDbLocks.entrySet()) {
				final LockInfo lockInfo = entry.getValue();
				if (!lockInfo.isExpired(now)) {
					lockInfo.setExpiresOn(expiresOn);
				}
			}
		}
	}

	@Periodic(value = PeriodicType.SLOWER, clusterMode = true)
	@Transactional(TransactionAttribute.REQUIRES_NEW)
	public void performClusterHouseKeeping(TaskMonitor taskMonitor) throws UnifyException {
		// Obliterate cluster nodes with stopped heart beats (Dead nodes).
		if (grabMasterSynchronizationLock()) {
			try {
				final String runtimeId = getRuntimeId();
				final Date now = db().getNow();
				Set<String> deadRuntimeIds = db().valueSet(String.class, "runtimeId", new ClusterNodeQuery()
						.lastHeartBeatOlderThan(getNewNodeExpiryDate(now)).runtimeNotEqual(runtimeId));
				if (!deadRuntimeIds.isEmpty()) {
					// Delete node commands
					db().deleteAll(new ClusterCommandParamQuery().runtimeIdIn(deadRuntimeIds));
					db().deleteAll(new ClusterCommandQuery().runtimeIdIn(deadRuntimeIds));

					// Delete nodes
					db().deleteAll(new ClusterNodeQuery().runtimeIdIn(deadRuntimeIds));

					// Delete locks
					db().deleteAll(new ClusterLockQuery().runtimeIdIn(deadRuntimeIds));
				}
			} finally {
				releaseMasterSynchronizationLock();
			}
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private class LockInfo {

		private final String ownerLockThreadId;

		private Date expiresOn;

		private int lockUsages;

		public LockInfo(String ownerLockThreadId, Date expiresOn) {
			this.ownerLockThreadId = ownerLockThreadId;
			this.expiresOn = expiresOn;
			this.lockUsages = 0;
		}

		public void incUsages() {
			lockUsages++;
		}

		public void decUsages() {
			if (lockUsages > 0) {
				lockUsages--;
			}
		}

		public void setExpiresOn(Date expiresOn) {
			if (this.expiresOn == null || this.expiresOn.before(expiresOn)) {
				this.expiresOn = expiresOn;
			}
		}

		public boolean isExpired(Date now) {
			return expiresOn.before(now);
		}

		public boolean isDisposable() {
			return lockUsages == 0;
		}

		public boolean isOwnedBy(String id) {
			return ownerLockThreadId.equals(id);
		}
	}

	/**
	 * Calculates a new expiration date by adding thrice the house keeping rate to
	 * current time.
	 */
	private Date getNextLockExpirationDate(Date now) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now.getTime() + PeriodicType.FAST.getPeriodInMillSec() * 3);
		return calendar.getTime();
	}

	private Date getNewNodeExpiryDate(Date now) throws UnifyException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now.getTime() - (nodeExpirationPeriod * 60000));
		return calendar.getTime();
	}
}
