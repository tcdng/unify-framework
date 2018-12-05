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
package com.tcdng.unify.core.system;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessModule;
import com.tcdng.unify.core.operation.Update;
import com.tcdng.unify.core.system.entities.ClusterDateSequenceNumber;
import com.tcdng.unify.core.system.entities.ClusterDateSequenceNumberQuery;
import com.tcdng.unify.core.system.entities.ClusterSequenceNumber;
import com.tcdng.unify.core.system.entities.ClusterUniqueString;
import com.tcdng.unify.core.system.entities.ClusterUniqueStringQuery;
import com.tcdng.unify.core.system.entities.SequenceNumberQuery;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Default implementation of a sequence number module.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_SEQUENCENUMBERBUSINESSMODULE)
public class SequenceNumberBusinessModuleImpl extends AbstractBusinessModule implements SequenceNumberBusinessModule {

	private Map<String, SequenceCounter> sequenceCounterMap;

	@Configurable("100")
	private int sequenceBlockSize;

	/**
	 * The number of attempts to get next sequence for sequence ID. For distributed
	 * environment where consistent sequence has to be maintained
	 */
	@Configurable("20")
	private int maxNextSequenceAttempts;

	public SequenceNumberBusinessModuleImpl() {
		sequenceCounterMap = new HashMap<String, SequenceCounter>();
	}

	@Override
	@Transactional(TransactionAttribute.REQUIRES_NEW)
	public synchronized Long getNextSequenceNumber(String sequencedName) throws UnifyException {
		SequenceCounter sequenceCounter = sequenceCounterMap.get(sequencedName);
		if (sequenceCounter == null) {
			try {
				sequenceCounter = new SequenceCounter();
				nextSequence(sequencedName, sequenceCounter, 1);
			} catch (UnifyException e) {
				ClusterSequenceNumber sequenceNumberData = new ClusterSequenceNumber();
				sequenceNumberData.setSequenceName(sequencedName);
				sequenceNumberData.setNextBlock(sequenceBlockSize + 1);
				sequenceNumberData.setBlockSize(sequenceBlockSize);
				db().create(sequenceNumberData);

				sequenceCounter.next = 1;
				sequenceCounter.last = sequenceCounter.next + sequenceNumberData.getBlockSize() - 1;
			}
			sequenceCounterMap.put(sequencedName, sequenceCounter);
		}

		Long id = Long.valueOf(sequenceCounter.next++);
		if (sequenceCounter.next > sequenceCounter.last) {
			nextSequence(sequencedName, sequenceCounter, maxNextSequenceAttempts);
		}
		return id;
	}

	@Override
	@Transactional
	@Synchronized("nextdatesequencenumber-lock")
	public Long getNextSequenceNumber(String sequenceName, Date date) throws UnifyException {
		Long sequenceNumber = null;
		Date midnightDate = CalendarUtils.getMidnightDate(date);
		ClusterDateSequenceNumber dateSequenceNumberData = db()
				.find(new ClusterDateSequenceNumberQuery().sequenceDate(midnightDate).sequenceName(sequenceName));
		if (dateSequenceNumberData != null) {
			sequenceNumber = dateSequenceNumberData.getSequenceCounter() + 1;
			dateSequenceNumberData.setSequenceCounter(sequenceNumber);
			db().updateById(dateSequenceNumberData);
		} else {
			sequenceNumber = Long.valueOf(1);
			dateSequenceNumberData = new ClusterDateSequenceNumber();
			dateSequenceNumberData.setSequenceDate(midnightDate);
			dateSequenceNumberData.setSequenceName(sequenceName);
			dateSequenceNumberData.setSequenceCounter(sequenceNumber);
			db().create(dateSequenceNumberData);
		}
		return sequenceNumber;
	}

	@Override
	@Transactional
	@Synchronized("uniquestring-lock")
	public Long getUniqueStringId(String uniqueString) throws UnifyException {
		ClusterUniqueString uniqueStringData = db().find(new ClusterUniqueStringQuery().uniqueString(uniqueString));
		if (uniqueStringData == null) {
			uniqueStringData = new ClusterUniqueString();
			uniqueStringData.setUniqueString(uniqueString);
			return (Long) db().create(uniqueStringData);
		}
		return uniqueStringData.getId();
	}

	@Transactional
	@Override
	public void reset() throws UnifyException {
		if (!isProductionMode()) {
			db().updateAll(new SequenceNumberQuery().ignoreEmptyCriteria(true),
					new Update().add("nextBlock", 1L).add("blockSize", sequenceBlockSize));
			sequenceCounterMap.clear();
		}
	}

	/**
	 * Gets the next sequence for a sequence. Makes multiple attempts in case
	 * version number changes. This maintains a consistent sequence number
	 * generation for clustered environments that share the same database.
	 * 
	 * @param sequencedName
	 *            the sequence name
	 * @param sequenceCounter
	 *            the sequence counter
	 * @throws UnifyException
	 *             if an error occurs
	 */
	private void nextSequence(String sequencedName, SequenceCounter sequenceCounter, int maxAttempts)
			throws UnifyException {
		for (int attempts = 1; attempts <= maxAttempts; attempts++) {
			try {
				ClusterSequenceNumber sequenceNumberData = db().find(ClusterSequenceNumber.class, sequencedName);
				sequenceCounter.next = sequenceNumberData.getNextBlock();
				sequenceCounter.last = sequenceCounter.next + sequenceNumberData.getBlockSize() - 1;
				sequenceNumberData.setNextBlock(sequenceCounter.next + sequenceNumberData.getBlockSize());
				db().updateByIdVersion(sequenceNumberData);
				break;
			} catch (UnifyException e) {
				if (attempts >= maxAttempts) {
					throw e;
				}
			}
			ThreadUtils.yield();
		}
	}

	private class SequenceCounter {
		public long next;

		public long last;
	}
}
