/*
 * Copyright 2018-2022 The Code Department.
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
import java.util.Set;

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
 * timestamp to coordidate synchronization of method calls across clusters.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_CLUSTERSERVICE)
public class ClusterServiceImpl extends AbstractBusinessService implements ClusterService {

    @Configurable("1") // Node expiration in minutes
    private int nodeExpirationPeriod;

    private ReentrantLockFactoryMap<String> lockList;

    private List<String> dbLockList;

    private Set<String> pseudoLocks;
    
    public ClusterServiceImpl() {
        lockList = new ReentrantLockFactoryMap<String>();
        dbLockList = new ArrayList<String>();
        pseudoLocks = new HashSet<String>();
    }

    public void setNodeExpirationPeriod(int nodeExpirationPeriod) {
        this.nodeExpirationPeriod = nodeExpirationPeriod;
    }

    @Override
    public String getLockOwnerId(boolean nodeOnly) throws UnifyException {
        if (nodeOnly) {
            return getNodeId();
        }
        return getNodeId() + ':' + ThreadUtils.currentThreadId();
    }

    @Override
    public void beginSynchronization(String lockName) throws UnifyException {
        if (isClusterMode()) {
            while (!grabDbLock(lockName, false)) {
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
            releaseDbLock(lockName, false);
        } else {
            lockList.get(lockName).unlock();
        }
    }

    @Override
    public boolean grabMasterSynchronizationLock() throws UnifyException {
        return grabSynchronizationLock("master-lock");
    }

    @Override
    public boolean grabSynchronizationLock(String lockName) throws UnifyException {
        if (isClusterMode()) {
            return grabDbLock(lockName, true);
        }
        
        pseudoLocks.add(lockName);
        return true;
    }

    @Override
    public boolean isWithSynchronizationLock(String lockName) throws UnifyException {
        if (isClusterMode()) {
            String lockOwnerId = getLockOwnerId(true);
            return db().countAll(new ClusterLockQuery().lockName(lockName).currentOwner(lockOwnerId)) > 0;
        }

        return pseudoLocks.contains(lockName);
    }

    @Override
    public boolean releaseSynchronizationLock(String lockName) throws UnifyException {
        if (isClusterMode()) {
            return releaseDbLock(lockName, true);
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
            List<String> nodeIdList =
                    db().valueList(String.class, "nodeId", new ClusterNodeQuery().nodeNotEqual(getNodeId()));
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
        List<Command> resultList = Collections.emptyList();
        List<ClusterCommand> clusterCommandList = db().findAll(new ClusterCommandQuery().nodeId(getNodeId()));

        if (!clusterCommandList.isEmpty()) {
            List<Long> clusterCommandIdList = new ArrayList<Long>();
            resultList = new ArrayList<Command>();
            for (ClusterCommand clusterCommand : clusterCommandList) {
                Long clusterCommandId = clusterCommand.getId();
                clusterCommandIdList.add(clusterCommandId);
                resultList.add(new Command(clusterCommand.getCommandCode(), db().valueList(String.class,
                        "parameter", new ClusterCommandParamQuery().clusterCommandId(clusterCommandId))));
            }

            db().deleteAll(new ClusterCommandParamQuery().clusterCommandIdIn(clusterCommandIdList));
            db().deleteAll(new ClusterCommandQuery().idIn(clusterCommandIdList));
        }
        return resultList;
    }

    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public boolean grabDbLock(String lockName, boolean nodeOnly) throws UnifyException {
        boolean successfulLock = false;
        String lockOwnerId = getLockOwnerId(nodeOnly);
        logDebug("Attempt by [{0}] to hold lock [{1}]...", lockOwnerId, lockName);
        ClusterLockQuery query = new ClusterLockQuery().lockName(lockName);
        ClusterLock clusterLock = db().find(query);
        if (clusterLock == null) {
            try {
                clusterLock = new ClusterLock();
                clusterLock.setLockName(lockName);
                clusterLock.setCurrentOwner(lockOwnerId);
                clusterLock.setExpiryTime(getNewLockExpirationDate());
                clusterLock.setLockCount(Integer.valueOf(1));
                db().create(clusterLock);
                successfulLock = true;
            } catch (Exception e) {
            }
        } else {
            if (lockOwnerId.equals(clusterLock.getCurrentOwner())) {
                successfulLock = db().updateAll(query.currentOwner(lockOwnerId),
                        new Update().add("expiryTime", getNewLockExpirationDate()).add("lockCount",
                                clusterLock.getLockCount() + 1)) > 0;
            } else {
                successfulLock = db().updateAll(query.expiredOrFree(db().getNow()),
                        new Update().add("currentOwner", lockOwnerId).add("expiryTime", getNewLockExpirationDate())
                                .add("lockCount", Integer.valueOf(1))) > 0;
            }
        }

        if (successfulLock) {
            if (!dbLockList.contains(lockName)) {
                dbLockList.add(lockName);
            }
            logDebug("Lock [{0}] sucessfully held by [{1}]...", lockName, lockOwnerId);
        } else {
            logDebug("[{0}] failed to hold lock [{1}]...", lockOwnerId, lockName);
        }
        return successfulLock;
    }

    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public boolean releaseDbLock(String lockName, boolean nodeOnly) throws UnifyException {
        boolean successfulRelease = false;
        String lockOwnerId = getLockOwnerId(nodeOnly);
        logDebug("Attempt by [{0}] to release lock [{1}]...", lockOwnerId, lockName);
        ClusterLockQuery query = new ClusterLockQuery().lockName(lockName).currentOwner(lockOwnerId);
        ClusterLock clusterLock = db().find(query);
        if (clusterLock != null) {
            Integer lockCount = clusterLock.getLockCount() - 1;
            if (lockCount > 0) {
                successfulRelease = db().updateAll(query, new Update().add("lockCount", lockCount)) > 0;
                if (successfulRelease) {
                    logDebug("Lock [{0}] partially released by [{1}]...", lockName, lockOwnerId);
                }
            } else {
                successfulRelease = db().updateAll(query,
                        new Update().add("currentOwner", null).add("lockCount", Integer.valueOf(0))) > 0;
                if (successfulRelease) {
                    dbLockList.remove(lockName);
                    logDebug("Lock [{0}] fully released by [{1}]...", lockName, lockOwnerId);
                }
            }
        }

        if (!successfulRelease) {
            logDebug("[{0}] failed to release lock [{1}]...", lockOwnerId, lockName);
        }
        return successfulRelease;
    }

    @Periodic(value = PeriodicType.FAST, clusterMode = true)
    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public void performHeartBeat(TaskMonitor taskMonitor) throws UnifyException {
        String nodeId = getNodeId();
        // Send a heart beat.
        Date lastHeartBeat = db().getNow();
        if (db().updateAll(new ClusterNodeQuery().nodeId(nodeId),
                new Update().add("lastHeartBeat", lastHeartBeat)) == 0) {
            // Register node
            ClusterNode clusterNode = new ClusterNode();
            clusterNode.setNodeId(nodeId);
            clusterNode.setLastHeartBeat(lastHeartBeat);
            clusterNode.setIpAddress(NetworkUtils.getLocalHostIpAddress());
            UnifyContainerInterface unifyContainerInterface =
                    (UnifyContainerInterface) this.getComponent("unify-commandinterface");
            clusterNode.setCommandPort(Integer.valueOf(unifyContainerInterface.getPort()));
            db().create(clusterNode);
        }

        // Extend life of locks held by this node
        if (!dbLockList.isEmpty()) {
            db().updateAll(new ClusterLockQuery().lockNameIn(new ArrayList<String>(dbLockList)),
                    new Update().add("expiryTime", getNewLockExpirationDate()));
        }
    }

    @Periodic(value = PeriodicType.SLOWER, clusterMode = true)
    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public void performClusterHouseKeeping(TaskMonitor taskMonitor) throws UnifyException {
        // Obliterate cluster nodes with stopped heart beats (Dead nodes).
        if (grabMasterSynchronizationLock()) {
            List<String> deadNodeIds = db().valueList(String.class, "nodeId",
                    new ClusterNodeQuery().lastHeartBeatOlderThan(getNewNodeExpiryDate()).nodeNotEqual(getNodeId()));
            if (!deadNodeIds.isEmpty()) {
                // Delete node commands
                db().deleteAll(new ClusterCommandParamQuery().nodeIdIn(deadNodeIds));
                db().deleteAll(new ClusterCommandQuery().nodeIdIn(deadNodeIds));

                // Delete nodes
                db().deleteAll(new ClusterNodeQuery().nodeIdIn(deadNodeIds));
            }
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    /**
     * Calculates a new expiration date by adding twice the house keeping rate to
     * current time.
     * 
     * @see {@link #performClusterHouseKeeping(TaskMonitor)}
     * @return the calculated date
     * @throws UnifyException
     *             if an error occurs
     */
    private Date getNewLockExpirationDate() throws UnifyException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(db().getNow().getTime() + PeriodicType.FAST.getPeriodInMillSec() * 2);
        return calendar.getTime();
    }

    private Date getNewNodeExpiryDate() throws UnifyException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(db().getNow().getTime() - (nodeExpirationPeriod * 60000));
        return calendar.getTime();
    }
}
