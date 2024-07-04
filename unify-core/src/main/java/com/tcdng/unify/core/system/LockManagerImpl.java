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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.common.constants.ApplicationCommonConstants;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.LockInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.util.SqlUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Lock manager implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_LOCKMANAGER)
public class LockManagerImpl extends AbstractUnifyComponent implements LockManager {

	private final long KEEPALIVE_HEARTBEAT_MILLISECONDS = 2500;

	private final long EXPIRATION_MILLISECONDS = 20000;

	private final long GRAB_RETRY_MILLISECONDS = 500;

	private final Map<String, Object> synchObjects;

	private final Map<String, ThreadLockInfo> threadLockInfos;

	private boolean clusterMode;

	private boolean heartbeatRunning;

	public LockManagerImpl() {
		this.synchObjects = new ConcurrentHashMap<String, Object>();
		this.threadLockInfos = new ConcurrentHashMap<String, ThreadLockInfo>();
	}

	@Override
	public boolean isLocked(String lockName) throws UnifyException {
		boolean locked = threadLockInfos.containsKey(lockName);
		if (!locked && clusterMode) {
			final Date _now = getNow();
			final Timestamp now = new Timestamp(_now.getTime());
			SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
					ApplicationCommonConstants.APPLICATION_DATASOURCE);
			Connection connection = (Connection) sqlDataSource.getConnection();
			PreparedStatement pstmt = null;
			ResultSet rst = null;
			try {
				pstmt = connection.prepareStatement(
						"SELECT COUNT(*) FROM unclusterlock WHERE unclusterlock_id = ? AND current_owner IS NOT NULL AND expiry_time < ?");
				pstmt.setString(1, lockName);
				pstmt.setTimestamp(2, now);

				rst = pstmt.executeQuery();
				rst.next();
				locked = rst.getInt(1) > 0;
				connection.commit();
			} catch (Exception e) {
				logSevere(e);
			} finally {
				SqlUtils.close(rst);
				SqlUtils.close(pstmt);
				sqlDataSource.restoreConnection(connection);
			}
		}

		return locked;
	}

	@Override
	public boolean tryGrabLock(String lockName) throws UnifyException {
		final String threadId = String.valueOf(ThreadUtils.currentThreadId());
		boolean grabbed = false;
		ThreadLockInfo threadLockInfo = threadLockInfos.get(lockName);
		if (threadLockInfo != null) {
			if (threadLockInfo.isOwnerThread(threadId)) {
				threadLockInfo.inc();
				grabbed = true;
			}
		} else {
			synchronized (getSynchObject(lockName)) {
				threadLockInfo = threadLockInfos.get(lockName);
				if (threadLockInfo == null) {
					if (clusterMode) {
						final String nodeId = getNodeId();
						final Date _now = getNow();
						final Timestamp now = new Timestamp(_now.getTime());
						final Timestamp nextExpiryTime = getNextExpiryTimestamp(_now);
						SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
								ApplicationCommonConstants.APPLICATION_DATASOURCE);
						Connection connection = (Connection) sqlDataSource.getConnection();
						PreparedStatement pstmt = null;
						ResultSet rst = null;
						try {
							pstmt = connection
									.prepareStatement("SELECT COUNT(*) FROM unclusterlock WHERE unclusterlock_id = ?");
							pstmt.setString(1, lockName);

							rst = pstmt.executeQuery();
							rst.next();
							if (rst.getInt(1) == 0) {
								SqlUtils.close(pstmt);
								// Create new lock record (This will fail - throw a PK exception - if another
								// node beat this node to it)
								pstmt = connection.prepareStatement(
										"INSERT INTO unclusterlock (unclusterlock_id, current_owner, thread_id, expiry_time, lock_count) VALUES (?,?,?,?,?)");
								pstmt.setString(1, lockName);
								pstmt.setString(2, nodeId);
								pstmt.setString(3, threadId);
								pstmt.setTimestamp(4, nextExpiryTime);
								pstmt.setInt(5, 1);
							} else {
								SqlUtils.close(pstmt);
								pstmt = connection.prepareStatement(
										"UPDATE unclusterlock SET current_owner = ?, thread_id = ? WHERE unclusterlock_id = ? AND (current_owner IS NULL OR expiry_time < ?)");
								pstmt.setString(1, nodeId);
								pstmt.setString(2, threadId);
								pstmt.setString(3, lockName);
								pstmt.setTimestamp(4, now);
							}

							grabbed = pstmt.executeUpdate() > 0;
							connection.commit();
						} catch (Exception e) {
							logSevere(e);
						} finally {
							SqlUtils.close(rst);
							SqlUtils.close(pstmt);
							sqlDataSource.restoreConnection(connection);
						}
					} else {
						grabbed = true;
					}
				}

				if (grabbed) {
					threadLockInfos.put(lockName, new ThreadLockInfo(lockName, threadId));
				}
			}
		}

		return grabbed;
	}

	@Override
	public boolean grabLock(String lockName) throws UnifyException {
		return grabLock(lockName, 0L);
	}

	@Override
	public boolean grabLock(String lockName, final long timeout) throws UnifyException {
		long retryMillisecs = 0;
		boolean grabbed = false;
		do {
			if (retryMillisecs > 0) {
				ThreadUtils.sleep(GRAB_RETRY_MILLISECONDS);
			}

			if (tryGrabLock(lockName)) {
				grabbed = true;
				break;
			}

			retryMillisecs += GRAB_RETRY_MILLISECONDS;
		} while (timeout <= 0 || retryMillisecs < timeout);

		return grabbed;
	}

	@Override
	public void releaseLock(String lockName) throws UnifyException {
		final String threadId = String.valueOf(ThreadUtils.currentThreadId());
		ThreadLockInfo threadLockInfo = threadLockInfos.get(lockName);
		if (threadLockInfo != null && threadLockInfo.isOwnerThread(threadId) && threadLockInfo.dec()) {
			if (clusterMode) {
				final String nodeId = getNodeId();
				SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
						ApplicationCommonConstants.APPLICATION_DATASOURCE);
				Connection connection = (Connection) sqlDataSource.getConnection();
				PreparedStatement pstmt = null;
				try {
					pstmt = connection.prepareStatement(
							"UPDATE unclusterlock SET current_owner = ?, thread_id = ? WHERE unclusterlock_id = ? AND current_owner = ? AND thread_id = ?");
					pstmt.setNull(1, Types.VARCHAR);
					pstmt.setNull(2, Types.VARCHAR);
					pstmt.setString(3, lockName);
					pstmt.setString(4, nodeId);
					pstmt.setString(5, threadId);
					pstmt.executeUpdate();
					connection.commit();
				} catch (Exception e) {
					logSevere(e);
				} finally {
					SqlUtils.close(pstmt);
					sqlDataSource.restoreConnection(connection);
				}
			}

			threadLockInfos.remove(lockName);
		}
	}

	@Override
	public LockInfo getLockInfo(String lockName) throws UnifyException {
		ThreadLockInfo threadLockInfo = threadLockInfos.get(lockName);
		if (threadLockInfo != null) {
			String ownerId = null;
			String threadId = null;
			Date expiryTime = null;
			SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
					ApplicationCommonConstants.APPLICATION_DATASOURCE);
			Connection connection = (Connection) sqlDataSource.getConnection();
			PreparedStatement pstmt = null;
			ResultSet rst = null;
			try {
				pstmt = connection.prepareStatement(
						"SELECT current_owner, thread_id,expiry_time FROM unclusterlock WHERE unclusterlock_id = ?");
				pstmt.setString(1, lockName);
				rst = pstmt.executeQuery();
				if (rst.next()) {
					ownerId = rst.getString(1);
					threadId = rst.getString(2);
					expiryTime = rst.getDate(3);
				}
			} catch (Exception e) {
				logSevere(e);
			} finally {
				SqlUtils.close(pstmt);
				sqlDataSource.restoreConnection(connection);
			}

			return new LockInfo(lockName, ownerId, threadId, expiryTime, (int) threadLockInfo.getExcursion());
		}

		return null;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		clusterMode = isClusterMode();
		if (clusterMode) {
			new Thread(new KeepAliveThread()).start();
		}
	}

	@Override
	protected void onTerminate() throws UnifyException {
		heartbeatRunning = false;
	}

	private class KeepAliveThread implements Runnable {

		@Override
		public void run() {
			heartbeatRunning = true;
			while (heartbeatRunning) {
				ThreadUtils.sleep(KEEPALIVE_HEARTBEAT_MILLISECONDS);
				try {
					keepLocksAlive();
				} catch (Exception e) {
					logSevere(e);
				}
			}
		}

	}

	private void keepLocksAlive() throws UnifyException {
		if (!threadLockInfos.isEmpty()) {
			final String nodeId = getNodeId();
			SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
					ApplicationCommonConstants.APPLICATION_DATASOURCE);
			Connection connection = (Connection) sqlDataSource.getConnection();
			try {
				for (ThreadLockInfo threadLockInfo : new ArrayList<ThreadLockInfo>(threadLockInfos.values())) {
					if (threadLockInfo.isActive()) {
						synchronized (getSynchObject(threadLockInfo.getLockName())) {
							PreparedStatement pstmt = null;
							try {
								final Timestamp nextExpiryTime = getNextExpiryTimestamp(getNow());
								pstmt = connection.prepareStatement(
										"UPDATE unclusterlock SET expiry_time = ? WHERE unclusterlock_id = ? AND current_owner = ?");
								pstmt.setTimestamp(1, nextExpiryTime);
								pstmt.setString(2, threadLockInfo.getLockName());
								pstmt.setString(3, nodeId);
								pstmt.executeUpdate();
								connection.commit();
							} catch (Exception e) {
								logSevere(e);
							} finally {
								SqlUtils.close(pstmt);
							}
						}
					}
				}
			} finally {
				sqlDataSource.restoreConnection(connection);
			}
		}
	}

	private Object getSynchObject(String lockName) {
		Object obj = synchObjects.get(lockName);
		if (obj == null) {
			synchronized (this) {
				obj = synchObjects.get(lockName);
				if (obj == null) {
					obj = new Object();
					synchObjects.put(lockName, obj);
				}
			}
		}

		return obj;
	}

	private Date getNow() {
		return new Date();
	}

	private Timestamp getNextExpiryTimestamp(Date _now) {
		return new Timestamp(_now.getTime() + EXPIRATION_MILLISECONDS);
	}

	private class ThreadLockInfo {

		private final String lockName;

		private final String ownerThreadId;

		private long excursion;

		public ThreadLockInfo(String lockName, String ownerThreadId) {
			this.lockName = lockName;
			this.ownerThreadId = ownerThreadId;
			this.excursion = 1;
		}

		public String getLockName() {
			return lockName;
		}

		public long getExcursion() {
			return excursion;
		}

		public boolean isOwnerThread(String threadId) {
			return ownerThreadId.equals(threadId);
		}

		public void inc() {
			excursion++;
		}

		public boolean dec() {
			excursion--;
			return excursion <= 0;
		}

		public boolean isActive() {
			return excursion > 0;
		}
	}
}
