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

package com.tcdng.unify.core.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.constant.TopicEventType;

/**
 * Default application database transaction manager implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER)
public class DatabaseTransactionManagerImpl extends AbstractUnifyComponent implements DatabaseTransactionManager {

	private static final ThreadLocal<Stack<TransactionalCall>> transactionsThreadLocal;

	@Configurable
	private EntityChangeEventBroadcaster entityChangeBroadcaster;

	static {
		transactionsThreadLocal = new ThreadLocal<Stack<TransactionalCall>>() {
			@Override
			protected Stack<TransactionalCall> initialValue() {
				return new Stack<TransactionalCall>();
			}
		};
	}

	@Configurable("true")
	private boolean autoJoin;

	@Override
	public void beginTransaction() throws UnifyException {
		beginTransaction(TransactionAttribute.REQUIRES_NEW);
	}

	@Override
	public void beginTransaction(TransactionAttribute txnType) throws UnifyException {
		Stack<TransactionalCall> transactions = transactionsThreadLocal.get();
		TransactionalCall transaction = null;
		switch (txnType) {
		case REQUIRED:
			if (!transactions.isEmpty() && transactions.peek().isTransaction()) {
				transaction = transactions.peek();
			} else {
				transaction = new TransactionalCall(entityChangeBroadcaster, autoJoin, true);
			}
			break;
		case REQUIRES_NEW:
			transaction = new TransactionalCall(entityChangeBroadcaster, autoJoin, true);
			break;
		case SUPPORTS:
			if (!transactions.isEmpty()) {
				transaction = transactions.peek();
			} else {
				transaction = new TransactionalCall(entityChangeBroadcaster, autoJoin, false);
			}
			break;
		case MANDATORY:
			if (!transactions.isEmpty()) {
				transaction = transactions.peek();
				if (!transaction.isTransaction()) {
					throw new UnifyException(UnifyCoreErrorConstants.TRANSACTION_IS_REQUIRED);
				}
			} else {
				throw new UnifyException(UnifyCoreErrorConstants.TRANSACTION_IS_REQUIRED);
			}
			break;
		case NEVER:
			if (!transactions.isEmpty()) {
				transaction = transactions.peek();
				if (transaction.isTransaction()) {
					throw new UnifyException(UnifyCoreErrorConstants.TRANSACTION_IS_NEVER_REQUIRED);
				}
			} else {
				transaction = new TransactionalCall(entityChangeBroadcaster, autoJoin, false);
			}
			break;
		case NOT_SUPPORTED:
			transaction = new TransactionalCall(entityChangeBroadcaster, autoJoin, false);
			break;
		}

		transaction.start();
		transactions.push(transaction);
	}

	@Override
	public void endTransaction() throws UnifyException {
		try {
			transactionsThreadLocal.get().pop().end();
			if (transactionsThreadLocal.get().isEmpty()) {
				transactionsThreadLocal.remove();
			}
		} catch (RuntimeException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.TRANSACTION_IS_ALREADY_COMPLETED);
		}
	}

	@Override
	public boolean isTransactionOpen() throws UnifyException {
		if (!transactionsThreadLocal.get().isEmpty()) {
			return getCurrentTransaction().isTransaction();
		}
		return false;
	}

	@Override
	public void joinTransaction(Database db) throws UnifyException {
		getCurrentTransaction().join(db);
	}

	@Override
	public DatabaseSession getDatabaseSession(Database db) throws UnifyException {
		TransactionalCall tc = getCurrentTransaction();
		if (tc.isTransaction()) {
			return tc.getDatabaseSession(db);
		}

		throw new UnifyException(UnifyCoreErrorConstants.TRANSACTION_IS_REQUIRED);
	}

	@Override
	public void setRollback() throws UnifyException {
		getCurrentTransaction().setRollback();
	}

	@Override
	public void clearRollback() throws UnifyException {
		getCurrentTransaction().clearRollback();
	}

	@Override
	public void setSavePoint() throws UnifyException {
		getCurrentTransaction().setSavePoint();
	}

	@Override
	public void clearSavePoint() throws UnifyException {
		getCurrentTransaction().clearSavePoint();
	}

	@Override
	public void rollbackToSavePoint() throws UnifyException {
		getCurrentTransaction().rollbackToSavePoint();
		getCurrentTransaction().clearRollback();
	}

	@Override
	public void commit() throws UnifyException {
		getCurrentTransaction().commit();
	}

	@Override
	public void setOffEntityEvent(TopicEventType eventType, String srcClientId, Class<? extends Entity> entityClass,
			Object id) throws UnifyException {
		getCurrentTransaction().addEntityEvent(eventType, srcClientId, entityClass, id);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private static class EntityEvent {

		private TopicEventType eventType;

		private String srcClientId;

		private Class<? extends Entity> entityClass;

		private Object id;

		public EntityEvent(TopicEventType eventType, String srcClientId, Class<? extends Entity> entityClass,
				Object id) {
			this.eventType = eventType;
			this.srcClientId = srcClientId;
			this.entityClass = entityClass;
			this.id = id;
		}

		public TopicEventType getEventType() {
			return eventType;
		}

		public String getSrcClientId() {
			return srcClientId;
		}

		public Class<? extends Entity> getEntityClass() {
			return entityClass;
		}

		public Object getId() {
			return id;
		}

	}

	private TransactionalCall getCurrentTransaction() throws UnifyException {
		try {
			return transactionsThreadLocal.get().peek();
		} catch (RuntimeException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.TRANSACTION_IS_ALREADY_COMPLETED);
		}
	}

	private static class TransactionalCall {
		private EntityChangeEventBroadcaster entityChangeBroadcaster;
		private Map<Database, DatabaseSession> databaseSessions;
		private boolean autoJoin;
		private boolean transaction;
		private boolean rollback;
		private int depth;
		private List<EntityEvent> events;

		public TransactionalCall(EntityChangeEventBroadcaster entityChangeBroadcaster, boolean autoJoin,
				boolean transaction) {
			this.entityChangeBroadcaster = entityChangeBroadcaster;
			this.autoJoin = autoJoin;
			this.transaction = transaction;
			this.events = new ArrayList<EntityEvent>();
			rollback = !transaction;
			databaseSessions = new HashMap<Database, DatabaseSession>();
		}

		public DatabaseSession join(Database db) throws UnifyException {
			DatabaseSession databaseSession = databaseSessions.get(db);
			if (databaseSession == null) {
				databaseSession = db.createDatabaseSession();
				databaseSessions.put(db, databaseSession);
			}

			return databaseSession;
		}

		public DatabaseSession getDatabaseSession(Database db) throws UnifyException {
			DatabaseSession databaseSession = databaseSessions.get(db);
			if (databaseSession == null) {
				if (!autoJoin) {
					throw new UnifyException(UnifyCoreErrorConstants.DATABASE_NOT_PART_OF_TRANSACTION);
				}

				return join(db);
			}

			return databaseSession;
		}

		public void addEntityEvent(TopicEventType eventType, String srcClientId, Class<? extends Entity> entityClass,
				Object id) throws UnifyException {
			events.add(new EntityEvent(eventType, srcClientId, entityClass, id));
		}

		public void start() throws UnifyException {
			depth++;
		}

		public void end() throws UnifyException {
			if (--depth == 0) {
				commit(true);
				databaseSessions.clear();
			}
		}

		public void setSavePoint() throws UnifyException {
			for (DatabaseSession dataSourceSession : databaseSessions.values()) {
				dataSourceSession.setSavepoint();
			}
		}

		public void clearSavePoint() throws UnifyException {
			for (DatabaseSession dataSourceSession : databaseSessions.values()) {
				dataSourceSession.clearSavepoint();
			}
		}

		public void rollbackToSavePoint() throws UnifyException {
			for (DatabaseSession dataSourceSession : databaseSessions.values()) {
				dataSourceSession.rollbackToSavepoint();
			}
		}

		public void setRollback() {
			rollback = true;
		}

		public void clearRollback() {
			rollback = false;
		}

		public void commit() throws UnifyException {
			commit(false);
		}

		public boolean isTransaction() {
			return transaction;
		}

		private void commit(boolean isClose) throws UnifyException {
			for (DatabaseSession dataSourceSession : databaseSessions.values()) {
				try {
					if (rollback) {
						dataSourceSession.rollback();
					} else {
						dataSourceSession.commit();
					}
				} catch (Exception e) {
				} finally {
					if (isClose) {
						try {
							dataSourceSession.close();
						} catch (Exception e) {
						}
					}
				}
			}

			for (EntityEvent event : events) {
				entityChangeBroadcaster.broadcastEntityChange(event.getEventType(), event.getSrcClientId(),
						event.getEntityClass(), event.getId());
			}
			events = new ArrayList<EntityEvent>();

			rollback = false;
		}
	}

}
