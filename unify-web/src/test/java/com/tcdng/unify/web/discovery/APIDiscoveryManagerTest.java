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
package com.tcdng.unify.web.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.tcdng.unify.web.AbstractUnifyWebTest;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.discovery.gem.APIDiscoveryRemoteCallCodeConstants;
import com.tcdng.unify.web.discovery.gem.data.APIDiscoveryRemoteCallInfo;

/**
 * API discovery manager tests.
 * 
 * @author Lateef
 * @since 1.0
 */
public class APIDiscoveryManagerTest extends AbstractUnifyWebTest {

	@Test
	public void testGetRemoteCallInfo() throws Exception {
		APIDiscoveryManager aPIDiscoveryManager = (APIDiscoveryManager) this
				.getComponent(WebApplicationComponents.APPLICATION_APIDISCOVERYMANAGER);
		APIDiscoveryRemoteCallInfo adrci = aPIDiscoveryManager
				.getRemoteCallInfo(APIDiscoveryRemoteCallCodeConstants.DISCOVER_REMOTE_CALL);
		assertNotNull(adrci);
		assertEquals(APIDiscoveryRemoteCallCodeConstants.DISCOVER_REMOTE_CALL, adrci.getCode());
		assertEquals("Discover Remote Call", adrci.getDescription());
		assertEquals("http://localhost/default/apidiscovery/discoverRemoteCall", adrci.getUrl());
		assertFalse(adrci.isRestricted());
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}

}
