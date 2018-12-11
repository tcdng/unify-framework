/*
 * Copyright 2018 The Code Department
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.data.Aggregate;
import com.tcdng.unify.core.data.AggregateType;
import com.tcdng.unify.core.operation.Update;

/**
 * Abstract implementation of a database. Also implements transaction management
 * methods of a database transaction manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDatabase extends AbstractUnifyComponent implements Database, DatabaseTransactionManager {

	private static final ThreadLocal<Stack<TransactionalCall>> transactionsThreadLocal;

	static {
		transactionsThreadLocal = new ThreadLocal<Stack<TransactionalCall>>() {
			@Override
			protected Stack<TransactionalCall> initialValue() {
				return new Stack<TransactionalCall>();
			}
		};
	}

	@Configurable(ApplicationComponents.APPLICATION_DATASOURCE)
	private DataSource dataSource;

	@Override
	public DatabaseTransactionManager getTransactionManager() throws UnifyException {
		return this;
	}

	@Override
	public String getDataSourceName() throws UnifyException {
		return dataSource.getName();
	}

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
				transaction = new TransactionalCall(true);
			}
			break;
		case REQUIRES_NEW:
			transaction = new TransactionalCall(true);
			break;
		case SUPPORTS:
			if (!transactions.isEmpty()) {
				transaction = transactions.peek();
			} else {
				transaction = new TransactionalCall(false);
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
				transaction = new TransactionalCall(false);
			}
			break;
		case NOT_SUPPORTED:
			transaction = new TransactionalCall(false);
			break;
		}

		transaction.join(this);
		transactions.push(transaction);
	}

	@Override
	public void endTransaction() throws UnifyException {
		try {
			transactionsThreadLocal.get().pop().leave();
			if (transactionsThreadLocal.get().isEmpty()) {
				transactionsThreadLocal.remove();
			}
		} catch (RuntimeException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.TRANSACTION_IS_ALREADY_COMPLETED);
		}
	}

	@Override
	public boolean isTransactionOpen() {
		if (!transactionsThreadLocal.get().isEmpty()) {
			TransactionalCall tc = transactionsThreadLocal.get().peek();
			return tc.isTransaction();
		}
		return false;
	}

	@Override
	public void setRollback() throws UnifyException {
		try {
			transactionsThreadLocal.get().peek().setRollback();
		} catch (RuntimeException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.TRANSACTION_IS_ALREADY_COMPLETED);
		}
	}

	@Override
	public void setSavePoint() throws UnifyException {
		getDatabaseSession().setSavepoint();
	}

	@Override
	public void clearSavePoint() throws UnifyException {
		getDatabaseSession().clearSavepoint();
	}

	@Override
	public void rollbackToSavepoint() throws UnifyException {
		getDatabaseSession().rollbackToSavepoint();
	}

	@Override
	public void commit() throws UnifyException {
		transactionsThreadLocal.get().peek().commit();
	}

	@Override
	public <T extends Entity> T find(Class<T> clazz, Object pk) throws UnifyException {
		return getDatabaseSession().find(clazz, pk);
	}

	@Override
	public <T extends Entity> T find(Class<T> clazz, Object pk, Object versionNo) throws UnifyException {
		return getDatabaseSession().find(clazz, pk, versionNo);
	}

	@Override
	public <T extends Entity> T find(Query<T> query) throws UnifyException {
		return getDatabaseSession().find(query);
	}

	@Override
	public <T extends Entity> T findConstraint(T record) throws UnifyException {
		return getDatabaseSession().findConstraint(record);
	}

	@Override
	public <T extends Entity> List<T> findAll(Query<T> query) throws UnifyException {
		return getDatabaseSession().findAll(query);
	}

	@Override
	public <T, U extends Entity> Map<T, U> findAllMap(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().findAllMap(keyClass, keyName, query);
	}

	@Override
	public <T, U extends Entity> Map<T, List<U>> findAllListMap(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().findAllListMap(keyClass, keyName, query);
	}

	@Override
	public <T extends Entity> T list(Class<T> clazz, Object pk) throws UnifyException {
		return getDatabaseSession().list(clazz, pk);
	}

	@Override
	public <T extends Entity> T list(Class<T> clazz, Object pk, Object versionNo) throws UnifyException {
		return getDatabaseSession().list(clazz, pk, versionNo);
	}

	@Override
	public <T extends Entity> T list(Query<T> query) throws UnifyException {
		return getDatabaseSession().list(query);
	}

	@Override
	public <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException {
		return getDatabaseSession().listAll(query);
	}

	@Override
	public <T, U extends Entity> Map<T, U> listAllMap(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().listAll(keyClass, keyName, query);
	}

	@Override
	public <T, U extends Entity> Map<T, List<U>> listAllListMap(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().listAllListMap(keyClass, keyName, query);
	}

	@Override
	public <T, U extends Entity> List<T> valueList(Class<T> fieldClass, String fieldName, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().valueList(fieldClass, fieldName, query);
	}

	@Override
	public <T, U extends Entity> T value(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
		return getDatabaseSession().value(fieldClass, fieldName, query);
	}

	@Override
	public <T, U extends Entity> Set<T> valueSet(Class<T> fieldClass, String fieldName, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().valueSet(fieldClass, fieldName, query);
	}

	@Override
	public <T, U, V extends Entity> Map<T, U> valueMap(Class<T> keyClass, String keyName, Class<U> valueClass,
			String valueName, Query<V> query) throws UnifyException {
		return getDatabaseSession().valueMap(keyClass, keyName, valueClass, valueName, query);
	}

	@Override
	public <T, U, V extends Entity> Map<T, List<U>> valueListMap(Class<T> keyClass, String keyName, Class<U> valueClass,
			String valueName, Query<V> query) throws UnifyException {
		return getDatabaseSession().valueListMap(keyClass, keyName, valueClass, valueName, query);
	}

	@Override
	public void populateListOnly(Entity record) throws UnifyException {
		getDatabaseSession().populateListOnly(record);
	}

	@Override
	public Object create(Entity record) throws UnifyException {
		return getDatabaseSession().create(record);
	}

	@Override
	public int updateById(Entity record) throws UnifyException {
		return getDatabaseSession().updateById(record);
	}

	@Override
	public int updateByIdVersion(Entity record) throws UnifyException {
		return getDatabaseSession().updateByIdVersion(record);
	}

	@Override
	public int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException {
		return getDatabaseSession().updateById(clazz, id, update);
	}

	@Override
	public int updateAll(Query<?> query, Update update) throws UnifyException {
		return getDatabaseSession().updateAll(query, update);
	}

	@Override
	public int deleteById(Entity record) throws UnifyException {
		return getDatabaseSession().deleteById(record);
	}

	@Override
	public int deleteByIdVersion(Entity record) throws UnifyException {
		return getDatabaseSession().deleteByIdVersion(record);
	}

	@Override
	public int delete(Class<? extends Entity> clazz, Object pk) throws UnifyException {
		return getDatabaseSession().delete(clazz, pk);
	}

	@Override
	public int deleteAll(Query<?> query) throws UnifyException {
		return getDatabaseSession().deleteAll(query);
	}

	@Override
	public int countAll(Query<?> query) throws UnifyException {
		return getDatabaseSession().count(query);
	}

	@Override
	public List<Aggregate<?>> aggregate(AggregateType aggregateType, Query<?> query) throws UnifyException {
		return getDatabaseSession().aggregate(aggregateType, query);
	}

	@Override
	public Date getNow() throws UnifyException {
		return getDatabaseSession().getNow();
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected DataSource getDataSource() {
		return dataSource;
	}

	protected abstract DatabaseSession createDatabaseSession() throws UnifyException;

	private DatabaseSession getDatabaseSession() throws UnifyException {
		if (!transactionsThreadLocal.get().isEmpty()) {
			TransactionalCall tc = transactionsThreadLocal.get().peek();
			if (tc.isTransaction()) {
				return (DatabaseSession) transactionsThreadLocal.get().peek().getDatabaseSession();
			}
		}
		throw new UnifyException(UnifyCoreErrorConstants.TRANSACTION_IS_REQUIRED);
	}

	/**
	 * The transactional call
	 */
	private static class TransactionalCall {
		private Map<String, DatabaseSession> databaseSessions;
		private Stack<DatabaseSession> sessionStack;
		private boolean transaction;
		private boolean rollback;

		public TransactionalCall(boolean transaction) {
			this.transaction = transaction;
			rollback = !transaction;
			databaseSessions = new HashMap<String, DatabaseSession>();
			sessionStack = new Stack<DatabaseSession>();
		}

		/**
		 * Creates a join
		 * 
		 * @param dataSourceName
		 *            the data source name
		 * @throws UnifyException
		 *             if an error occurs
		 */
		public void join(AbstractDatabase pm) throws UnifyException {
			DatabaseSession databaseSession = databaseSessions.get(pm.getDataSourceName());
			if (databaseSession == null) {
				databaseSession = pm.createDatabaseSession();
				databaseSessions.put(pm.getDataSourceName(), databaseSession);
			}
			sessionStack.push(databaseSession);
		}

		/**
		 * Leaves transaction
		 * 
		 * @throws UnifyException
		 *             - If an error occurs
		 */
		public void leave() throws UnifyException {
			try {
				sessionStack.pop();
			} catch (RuntimeException e) {
				throw new UnifyException(e, UnifyCoreErrorConstants.TRANSACTION_IS_ALREADY_COMPLETED);
			}

			// Assumes transaction has ended when session stack is empty
			if (sessionStack.isEmpty()) {
				commit(true);
				databaseSessions.clear();
			}
		}

		public void commit() throws UnifyException {
			commit(false);
		}

		/**
		 * Gets the current database session
		 * 
		 * @throws UnifyException
		 *             - If an error occurs
		 */
		public DatabaseSession getDatabaseSession() throws UnifyException {
			try {
				return sessionStack.peek();
			} catch (RuntimeException e) {
				throw new UnifyException(e, UnifyCoreErrorConstants.TRANSACTION_IS_ALREADY_COMPLETED);
			}
		}

		/**
		 * Sets transaction to roll back. All sessions are rolled back at the end of
		 * transaction
		 */
		public void setRollback() {
			rollback = true;
		}

		/**
		 * @return the transaction
		 */
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
		}
	}
}
