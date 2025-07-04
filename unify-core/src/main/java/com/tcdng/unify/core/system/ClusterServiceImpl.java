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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyContainerInterface;
import com.tcdng.unify.core.UnifyCoreRequestAttributeConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.system.entities.ClusterCommand;
import com.tcdng.unify.core.system.entities.ClusterCommandParam;
import com.tcdng.unify.core.system.entities.ClusterCommandParamQuery;
import com.tcdng.unify.core.system.entities.ClusterCommandQuery;
import com.tcdng.unify.core.system.entities.ClusterNode;
import com.tcdng.unify.core.system.entities.ClusterNodeQuery;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.NetworkUtils;

/**
 * Default implementation of application cluster manager. Uses datasource
 * timestamp to coordidate synchronization of method calls across clusters.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_CLUSTERSERVICE)
public class ClusterServiceImpl extends AbstractBusinessService implements ClusterService {

	private final String CLUSTER_HOUSE_KEEPING_LOCK = "app:clusterhousekeeping-lock";
	
	@Configurable("1") // Node expiration in minutes
	private int nodeExpirationPeriod;

	@Override
	public List<ClusterNode> findClusterNodes(ClusterNodeQuery query) throws UnifyException {
		return db().findAll(query);
	}

	@Override
	public void broadcastToOtherNodes(String command, String... params) throws UnifyException {
		if (isClusterMode()
				&& !Boolean.TRUE.equals(getRequestAttribute(UnifyCoreRequestAttributeConstants.SUPPRESS_BROADCAST))) {
			List<String> nodeIdList = db().valueList(String.class, "nodeId",
					new ClusterNodeQuery().nodeNotEqual(getNodeId()));
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
				resultList.add(new Command(clusterCommand.getCommandCode(), db().valueList(String.class, "parameter",
						new ClusterCommandParamQuery().clusterCommandId(clusterCommandId))));
			}

			db().deleteAll(new ClusterCommandParamQuery().clusterCommandIdIn(clusterCommandIdList));
			db().deleteAll(new ClusterCommandQuery().idIn(clusterCommandIdList));
		}
		return resultList;
	}


	@Periodic(PeriodicType.FAST)
	public void performHeartBeat(TaskMonitor taskMonitor) throws UnifyException {
		final String nodeId = getNodeId();
		// Send a heart beat.
		Date lastHeartBeat = db().getNow();
		if (db().updateAll(new ClusterNodeQuery().nodeId(nodeId),
				new Update().add("lastHeartBeat", lastHeartBeat)) == 0) {
			// Register node
			ClusterNode clusterNode = new ClusterNode();
			clusterNode.setNodeId(nodeId);
			clusterNode.setLastHeartBeat(lastHeartBeat);
			clusterNode.setIpAddress(NetworkUtils.getLocalHostIpAddress());
			UnifyContainerInterface unifyContainerInterface = (UnifyContainerInterface) this
					.getComponent("unify-commandinterface");
			clusterNode.setCommandPort(Integer.valueOf(unifyContainerInterface.getPort()));
			db().create(clusterNode);
		}
	}

	@Periodic(PeriodicType.SLOWER)
	@Synchronized(lock = CLUSTER_HOUSE_KEEPING_LOCK, waitForLock = false)
	public void performClusterHouseKeeping(TaskMonitor taskMonitor) throws UnifyException {
		// Obliterate cluster nodes with stopped heart beats (Dead nodes).
		List<String> deadNodeIds = db().valueList(String.class, "nodeId", new ClusterNodeQuery()
				.lastHeartBeatOlderThan(getNewNodeExpiryDate()).nodeNotEqual(getNodeId()));
		if (!deadNodeIds.isEmpty()) {
			// Delete node commands
			db().deleteAll(new ClusterCommandParamQuery().nodeIdIn(deadNodeIds));
			db().deleteAll(new ClusterCommandQuery().nodeIdIn(deadNodeIds));

			// Delete nodes
			db().deleteAll(new ClusterNodeQuery().nodeIdIn(deadNodeIds));
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private Date getNewNodeExpiryDate() throws UnifyException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(db().getNow().getTime() - (nodeExpirationPeriod * 60000));
		return calendar.getTime();
	}
}
