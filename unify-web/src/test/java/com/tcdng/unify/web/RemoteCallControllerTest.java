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
package com.tcdng.unify.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.remotecall.RemoteCallFormat;

/**
 * Remote call controller tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class RemoteCallControllerTest extends AbstractUnifyWebTest {

	@Test
	public void testExecuteXMLRemoteCallController() throws Exception {
		ControllerFinder controllerFinder = (ControllerFinder) getComponent(
				WebApplicationComponents.APPLICATION_CONTROLLERFINDER);
		PathInfoRepository pir = (PathInfoRepository) getComponent(
				WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);

		TestClientRequest request = new TestClientRequest(
				new RequestPathParts(pir.getControllerPathParts("/remotecall/mock/getAccountDetails")),
				"<accountDetailParams>" + "<accountNo>0123456785</accountNo>" + "</accountDetailParams>");
		request.getParameters().setParam(RequestParameterConstants.REMOTE_CALL_FORMAT, RemoteCallFormat.XML);

		TestClientResponse response = new TestClientResponse();
		controllerFinder.findController(request.getRequestPathParts().getControllerPathParts()).process(request, response);

		String expRespXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<accountDetailResult>"
				+ "<accountNo>0123456785</accountNo>" + "<accountName>Edward Banfa</accountName>"
				+ "<balance>250000.0</balance>" + "</accountDetailResult>";
		assertEquals(expRespXml, response.toString());
	}

	@Test
	public void testExecuteJSONRemoteCallController() throws Exception {
        ControllerFinder controllerFinder = (ControllerFinder) getComponent(
                WebApplicationComponents.APPLICATION_CONTROLLERFINDER);
		PathInfoRepository pir = (PathInfoRepository) getComponent(
				WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);

		TestClientRequest request = new TestClientRequest(
				new RequestPathParts(pir.getControllerPathParts("/remotecall/mock/getAccountDetails")),
				"{" + "\"accountNo\":\"0123456785\"" + "}");
		request.getParameters().setParam(RequestParameterConstants.REMOTE_CALL_FORMAT, RemoteCallFormat.JSON);

        TestClientResponse response = new TestClientResponse();
        controllerFinder.findController(request.getRequestPathParts().getControllerPathParts()).process(request, response);
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}

}
