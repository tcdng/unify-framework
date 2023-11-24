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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.system.entities.ClusterLock;
import com.tcdng.unify.core.system.entities.ClusterLockQuery;
import com.tcdng.unify.core.system.entities.ClusterNode;
import com.tcdng.unify.core.system.entities.ClusterNodeQuery;

/**
 * Used to manage operations across clustered instances of unify container.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ClusterService extends BusinessService {

    /**
     * Returns the node lock owner ID.
     * 
     * @param nodeOnly
     *            indicates that only node instance ID should be lock owner ID
     *            without thread information
     * @throws UnifyException
     *             if an error occurs
     */
    String getLockOwnerId(boolean nodeOnly) throws UnifyException;

    /**
     * Begins a synchronization block with specified lock. Blocks until
     * synchronization handle is obtained or an error occurs. Lock should be release
     * by calling {@link #endSynchronization(String)}.
     * 
     * @param lockName
     *            the lock name
     * @throws UnifyException
     *             if an error occurs
     */
    void beginSynchronization(String lockName) throws UnifyException;

    /**
     * Ends a synchronization block for specified lock.
     * 
     * @param lockName
     *            the lock name
     * @throws UnifyException
     *             if an error occurs
     */
    void endSynchronization(String lockName) throws UnifyException;

    /**
     * Tries to grab cluster master synchronization lock. Does not block.
     * 
     * @return a true value if cluster lock is gotten, otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    boolean grabMasterSynchronizationLock() throws UnifyException;

    /**
     * Tries to grab a cluster synchronization lock with specified name. Does not
     * block.Lock should be release by calling
     * {@link #releaseSynchronizationLock(String)}.
     * 
     * @param lockName
     *            the lock name
     * @return a true value if synchronization lock is gotten, otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    boolean grabSynchronizationLock(String lockName) throws UnifyException;
    
    /**
     * Checks if current node has a hold on a cluster synchronization lock.
     * 
     * @param lockName
     *            the lock name
     * @return a true value is lock is held otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isWithSynchronizationLock(String lockName) throws UnifyException;

    /**
     * Releases a cluster synchronization lock.
     * 
     * @param lockName
     *            the lock name
     * @return a true value if lock held by member was released. A false value if
     *         lock is not held by this node or lock with name does not exist.
     * @throws UnifyException
     *             if an error occurs
     */
    boolean releaseSynchronizationLock(String lockName) throws UnifyException;

    /**
     * Gets a list of cluster synchronization lock data that matches supplied
     * criteria.
     * 
     * @param query
     *            the query to match
     * @return A list of cluster syncgronization data
     * @throws UnifyException
     *             if an error occurs
     */
    List<ClusterLock> findClusterLocks(ClusterLockQuery query) throws UnifyException;

    /**
     * Finds a list of cluster node information based on supplied criteria.
     * 
     * @param query
     *            the search query
     * @return list of node data that match criteria
     * @throws UnifyException
     *             if an error occurs
     */
    List<ClusterNode> findClusterNodes(ClusterNodeQuery query) throws UnifyException;

    /**
     * Broadcasts a cluster command to other nodes.
     * 
     * @param command
     *            the command to send
     * @param params
     *            the command parameters
     * @throws UnifyException
     *             if an error occurs
     */
    void broadcastToOtherNodes(String command, String... params) throws UnifyException;

    /**
     * Retrieves all cluster commands for this node.
     * 
     * @return list of cluster commands for this node
     * @throws UnifyException
     *             if an error occurs
     */
    List<Command> getClusterCommands() throws UnifyException;
}
