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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessService;
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
