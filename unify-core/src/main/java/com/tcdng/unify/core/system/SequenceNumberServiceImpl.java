/*
 * Copyright (c) 2018-2025 The Code Department.
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.DbHelper;
import com.tcdng.unify.core.system.entities.ClusterDateSequenceNumber;
import com.tcdng.unify.core.system.entities.ClusterDateSequenceNumberQuery;
import com.tcdng.unify.core.system.entities.ClusterSequenceBlock;
import com.tcdng.unify.core.system.entities.ClusterSequenceBlockQuery;
import com.tcdng.unify.core.system.entities.ClusterSequenceNumber;
import com.tcdng.unify.core.system.entities.ClusterSequenceNumberQuery;
import com.tcdng.unify.core.system.entities.ClusterUniqueString;
import com.tcdng.unify.core.system.entities.ClusterUniqueStringQuery;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.QueryUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Default implementation of a sequence number service.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE)
public class SequenceNumberServiceImpl extends AbstractBusinessService implements SequenceNumberService, DbHelper {

    private Map<String, SequenceBlock> sequenceBlockMap;

    @Configurable("1000")
    private int sequenceBlockSize;

    /**
     * The number of attempts to get next sequence block.
     */
    @Configurable("20")
    private int maxNextSequenceBlockAttempts;

    public SequenceNumberServiceImpl() {
        sequenceBlockMap = new HashMap<String, SequenceBlock>();
    }

	@Override
	public boolean exists(Class<? extends Entity> entityClass, Object inst, String fieldName) throws UnifyException {
		List<Set<String>> uniqueConstraints = db(entityClass).getUniqueConstraints(entityClass);
		if (!DataUtils.isBlank(uniqueConstraints)) {
			for (Set<String> fieldNames : uniqueConstraints) {
				if (fieldNames.contains(fieldName)) {
					Query<? extends Entity> query = Query.of(entityClass);
					for (String _fieldName : fieldNames) {
						Object val = DataUtils.getBeanProperty(inst, _fieldName);
						if (val != null) {
							query.addEquals(_fieldName, val);
						}
					}

					final Long id = DataUtils.getBeanProperty(Long.class, inst, "id");
					if (QueryUtils.isValidLongCriteria(id)) {
						query.addNotEquals("id", id);
					}

					if (!query.isEmptyCriteria() && db(entityClass).countAll(query) > 0) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean exists(Class<? extends Entity> entityClass, Object inst) throws UnifyException {
		List<Set<String>> uniqueConstraints = db(entityClass).getUniqueConstraints(entityClass);
		if (!DataUtils.isBlank(uniqueConstraints)) {
			for (Set<String> fieldNames : uniqueConstraints) {
				Query<? extends Entity> query = Query.of(entityClass);
				for (String fieldName : fieldNames) {
					Object val = DataUtils.getBeanProperty(inst, fieldName);
					if (val != null) {
						query.addEquals(fieldName, val);
					}
				}

				final Long id = DataUtils.getBeanProperty(Long.class, inst, "id");
				if (QueryUtils.isValidLongCriteria(id)) {
					query.addNotEquals("id", id);
				}

				if (db(entityClass).countAll(query) > 0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean exists(Query<? extends Entity> query) throws UnifyException {
		return db(query.getEntityClass()).countAll(query) > 0;
	}

	@Override
	public <T extends Entity> boolean isOfThisSequence(Class<T> clazz) throws UnifyException {
    	if (!db().isOfThisDatabase(clazz)) {
        	Table ta = clazz.getAnnotation(Table.class);    	
    		return  ta != null && ta.allowAlternateIdSource();
    	}
    	
    	return true;
	}

	@Override
    @Synchronized("sys:nextdatesequencenumber-lock")
	public void ensureCachedBlockSequence(String sequencedName) throws UnifyException {
        SequenceBlock sequenceBlock = sequenceBlockMap.get(sequencedName);
        if (sequenceBlock == null) {
        	ensureNewCachedBlockSequence(sequencedName);
        }		
	}

	@Override
    @Synchronized("sys:nextdatesequencenumber-lock")
    public Long getCachedBlockNextSequenceNumber(String sequencedName) throws UnifyException {
        SequenceBlock sequenceBlock = sequenceBlockMap.get(sequencedName);
        if (sequenceBlock == null || sequenceBlock.willExpire()) {
            return getNewBlockCachedBlockNextSequenceNumber(sequencedName);
        }

        return sequenceBlock.getNextId();
    }

    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public void ensureNewCachedBlockSequence(String sequencedName) throws UnifyException {
    	getSequenceBlock(sequencedName);
    }

    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public Long getNewBlockCachedBlockNextSequenceNumber(String sequencedName) throws UnifyException {
        final SequenceBlock sequenceBlock = getSequenceBlock(sequencedName);
        Long id = sequenceBlock.getNextId();
        if (sequenceBlock.isExpired()) {
            nextSequenceBlock(sequencedName, sequenceBlock, maxNextSequenceBlockAttempts);
        }

        return id;
    }

    private SequenceBlock getSequenceBlock(String sequencedName) throws UnifyException {
        SequenceBlock sequenceBlock = sequenceBlockMap.get(sequencedName);
        if (sequenceBlock == null) {
            try {
                sequenceBlock = new SequenceBlock();
                nextSequenceBlock(sequencedName, sequenceBlock, 1);
            } catch (UnifyException e) {
                ClusterSequenceBlock clusterSequenceBlock = new ClusterSequenceBlock();
                clusterSequenceBlock.setSequenceName(sequencedName);
                clusterSequenceBlock.setNextBlock(sequenceBlockSize + 1);
                clusterSequenceBlock.setBlockSize(sequenceBlockSize);
                db().create(clusterSequenceBlock);

                sequenceBlock.next = 1;
                sequenceBlock.last = sequenceBlock.next + clusterSequenceBlock.getBlockSize() - 1;
            }
            sequenceBlockMap.put(sequencedName, sequenceBlock);
        }
        
        return sequenceBlock;
    }
    
    @Override
    @Synchronized("sys:nextsequencenumber-lock")
    public Long getNextSequenceNumber(String sequenceName) throws UnifyException {
        Long sequenceNumber = null;
        ClusterSequenceNumber clusterSequenceNumber = db()
                .find(new ClusterSequenceNumberQuery().sequenceName(sequenceName));
        if (clusterSequenceNumber != null) {
            sequenceNumber = clusterSequenceNumber.getSequenceCounter() + 1;
            clusterSequenceNumber.setSequenceCounter(sequenceNumber);
            db().updateById(clusterSequenceNumber);
        } else {
            sequenceNumber = Long.valueOf(1);
            clusterSequenceNumber = new ClusterSequenceNumber();
            clusterSequenceNumber.setSequenceName(sequenceName);
            clusterSequenceNumber.setSequenceCounter(sequenceNumber);
            db().create(clusterSequenceNumber);
        }
        return sequenceNumber;
    }

    @Override
    @Synchronized("sys:nextdatesequencenumber-lock")
    public Long getNextSequenceNumber(String sequenceName, Date date) throws UnifyException {
        Long sequenceNumber = null;
        Date midnightDate = CalendarUtils.getMidnightDate(date);
        ClusterDateSequenceNumber dateSequenceNumber = db()
                .find(new ClusterDateSequenceNumberQuery().sequenceDate(midnightDate).sequenceName(sequenceName));
        if (dateSequenceNumber != null) {
            sequenceNumber = dateSequenceNumber.getSequenceCounter() + 1;
            dateSequenceNumber.setSequenceCounter(sequenceNumber);
            db().updateById(dateSequenceNumber);
        } else {
            sequenceNumber = Long.valueOf(1);
            dateSequenceNumber = new ClusterDateSequenceNumber();
            dateSequenceNumber.setSequenceDate(midnightDate);
            dateSequenceNumber.setSequenceName(sequenceName);
            dateSequenceNumber.setSequenceCounter(sequenceNumber);
            db().create(dateSequenceNumber);
        }
        return sequenceNumber;
    }

    @Override
    @Synchronized("sys:uniquestring-lock")
    public Long getUniqueStringId(final String uniqueString) throws UnifyException {
        final String md5 = DigestUtils.md5Hex(uniqueString);
        ClusterUniqueString clusterUniqueString = db().find(new ClusterUniqueStringQuery().uniqueString(md5));
        if (clusterUniqueString == null) {
            clusterUniqueString = new ClusterUniqueString();
            clusterUniqueString.setUniqueString(md5);
            return (Long) db().create(clusterUniqueString);
        }

        return clusterUniqueString.getId();
    }

    @Override
    public void reset() throws UnifyException {
        if (!isProductionMode()) {
            db().updateAll(new ClusterSequenceBlockQuery().ignoreEmptyCriteria(true),
                    new Update().add("nextBlock", 1L).add("blockSize", sequenceBlockSize));
            sequenceBlockMap.clear();
        }
    }

    /**
     * Gets the next sequence block for a sequence. Makes multiple attempts in case
     * version number changes. This facilitates sequence number generation for
     * clustered environments that share the same database.
     * 
     * @param sequencedName
     *                        the sequence name
     * @param sequenceCounter
     *                        the sequence counter
     * @throws UnifyException
     *                        if an error occurs
     */
    private void nextSequenceBlock(String sequencedName, SequenceBlock sequenceCounter, int maxAttempts)
            throws UnifyException {
        for (int attempts = 1; attempts <= maxAttempts; attempts++) {
            try {
                ClusterSequenceBlock clusterSequenceBlock = db().find(ClusterSequenceBlock.class, sequencedName);
                sequenceCounter.next = clusterSequenceBlock.getNextBlock();
                sequenceCounter.last = sequenceCounter.next + clusterSequenceBlock.getBlockSize() - 1;
                clusterSequenceBlock.setNextBlock(sequenceCounter.next + clusterSequenceBlock.getBlockSize());
                db().updateByIdVersion(clusterSequenceBlock);
                break;
            } catch (UnifyException e) {
                if (attempts >= maxAttempts) {
                    throw e;
                }
            }
            ThreadUtils.yield();
        }
    }

    private class SequenceBlock {
        public long next;

        public long last;

        public Long getNextId() {
            return Long.valueOf(next++);
        }

        public boolean willExpire() {
            return (next + 1) > last;
        }

        public boolean isExpired() {
            return next > last;
        }
    }
}
