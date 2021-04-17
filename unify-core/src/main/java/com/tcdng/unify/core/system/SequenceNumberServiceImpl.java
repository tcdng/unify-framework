/*
 * Copyright 2018-2020 The Code Department.
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

import org.apache.commons.codec.digest.DigestUtils;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.system.entities.ClusterDateSequenceNumber;
import com.tcdng.unify.core.system.entities.ClusterDateSequenceNumberQuery;
import com.tcdng.unify.core.system.entities.ClusterSequenceBlock;
import com.tcdng.unify.core.system.entities.ClusterSequenceBlockQuery;
import com.tcdng.unify.core.system.entities.ClusterSequenceNumber;
import com.tcdng.unify.core.system.entities.ClusterSequenceNumberQuery;
import com.tcdng.unify.core.system.entities.ClusterUniqueString;
import com.tcdng.unify.core.system.entities.ClusterUniqueStringQuery;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Default implementation of a sequence number service.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE)
public class SequenceNumberServiceImpl extends AbstractBusinessService implements SequenceNumberService {

    private Map<String, SequenceBlock> sequenceBlockMap;

    @Configurable("100")
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
    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public synchronized Long getCachedBlockNextSequenceNumber(String sequencedName) throws UnifyException {
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

        Long id = Long.valueOf(sequenceBlock.next++);
        if (sequenceBlock.next > sequenceBlock.last) {
            nextSequenceBlock(sequencedName, sequenceBlock, maxNextSequenceBlockAttempts);
        }
        return id;
    }

    @Override
    @Transactional
    @Synchronized("nextsequencenumber-lock")
    public Long getNextSequenceNumber(String sequenceName) throws UnifyException {
        Long sequenceNumber = null;
        ClusterSequenceNumber clusterSequenceNumber =
                db().find(new ClusterSequenceNumberQuery().sequenceName(sequenceName));
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
    @Transactional
    @Synchronized("nextdatesequencenumber-lock")
    public Long getNextSequenceNumber(String sequenceName, Date date) throws UnifyException {
        Long sequenceNumber = null;
        Date midnightDate = CalendarUtils.getMidnightDate(date);
        ClusterDateSequenceNumber dateSequenceNumber =
                db().find(new ClusterDateSequenceNumberQuery().sequenceDate(midnightDate).sequenceName(sequenceName));
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
    @Transactional
    @Synchronized("uniquestring-lock")
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

    @Transactional
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
     *            the sequence name
     * @param sequenceCounter
     *            the sequence counter
     * @throws UnifyException
     *             if an error occurs
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
    }
}
