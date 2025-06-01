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
package com.tcdng.unify.web.ui;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.web.AbstractUnifyWebTest;
import com.tcdng.unify.web.ControllerFinder;
import com.tcdng.unify.web.PathInfoRepository;
import com.tcdng.unify.web.RequestPathParts;
import com.tcdng.unify.web.TestClientRequest;
import com.tcdng.unify.web.TestClientResponse;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.controller.DocumentLoaderController;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Page controller tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class PageControllerTest extends AbstractUnifyWebTest {

	@Test
	public void testPopulateControllerProperty() throws Exception {
		PageRequestContextUtil prcu = (PageRequestContextUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_PAGEREQUESTCONTEXTUTIL);
		ControllerFinder controllerFinder = (ControllerFinder) getComponent(
				WebApplicationComponents.APPLICATION_CONTROLLERFINDER);
		UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		PathInfoRepository pathInfoRepository = (PathInfoRepository) getComponent(
				WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);

		prcu.setRequestAttribute(UnifyWebRequestAttributeConstants.CLIENT_ID, "cid1");
		// Create page controller and load page to request context
		AuthorPageController controller = (AuthorPageController) controllerFinder
				.findController(pathInfoRepository.getControllerPathParts("/testauthor"));

		Date birthDt = new Date();
		uicu.populatePageBean("/testauthor", "fullName", "Adrian Skim");
		uicu.populatePageBean("/testauthor", "birthDt", birthDt);
		uicu.populatePageBean("/testauthor", "height", Double.valueOf(25.34));

		AuthorPageBean authorPageBean = (AuthorPageBean) controller.getPage().getPageBean();
		assertEquals("Adrian Skim", authorPageBean.getFullName());
		assertEquals(birthDt, authorPageBean.getBirthDt());
		assertEquals(Double.valueOf(25.34), authorPageBean.getHeight());
	}

	@Test
	public void testExecutePageController() throws Exception {
		PageRequestContextUtil prcu = (PageRequestContextUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_PAGEREQUESTCONTEXTUTIL);
		ControllerFinder controllerFinder = (ControllerFinder) getComponent(
				WebApplicationComponents.APPLICATION_CONTROLLERFINDER);
		PathInfoRepository pir = (PathInfoRepository) getComponent(
				WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);

		TestClientResponse response = new TestClientResponse();
		TestClientRequest request = new TestClientRequest(
				new RequestPathParts(pir.getControllerPathParts("/testauthor/createAuthor")));
		Date birthDt = new Date();

		prcu.setRequestAttribute(UnifyWebRequestAttributeConstants.CLIENT_ID, "cid1");
		AuthorPageController controller = (AuthorPageController) controllerFinder
				.findController(pir.getControllerPathParts("/testauthor"));
		Widget uic1 = controller.getPageWidgetByLongName(Widget.class, "/testauthor.fullName");
		Widget uic2 = controller.getPageWidgetByLongName(Widget.class, "/testauthor.birthDt");
		Widget uic3 = controller.getPageWidgetByLongName(Widget.class, "/testauthor.height");
		request.getParameters().setParam(uic1.getId(), "Tom Jones");
		request.getParameters().setParam(uic2.getId(), birthDt);
		request.getParameters().setParam(uic3.getId(), "24.22");

		controller.process(request, response);

		// Ensure controller is populated
		AuthorPageBean authorPageBean = (AuthorPageBean) controller.getPage().getPageBean();
		assertEquals("Tom Jones", authorPageBean.getFullName());
		assertEquals(birthDt, authorPageBean.getBirthDt());
		assertEquals(Double.valueOf(24.22), authorPageBean.getHeight());

		// Test result
		assertEquals(
				"{\"jsonResp\":[{\"handler\":\"hintUserHdl\"},{\"handler\":\"refreshMenuHdl\"}],\"scrollReset\":true}",
				response.toString());
	}

	@SuppressWarnings("unused")
	@Test
	public void testDocumentLoading() throws Exception {
		ControllerFinder controllerFinder = (ControllerFinder) getComponent(
				WebApplicationComponents.APPLICATION_CONTROLLERFINDER);
		PathInfoRepository pathInfoRepository = (PathInfoRepository) getComponent(
				WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);

		// Create page controller and load page to request context
		DocumentLoaderController controller = (DocumentLoaderController) controllerFinder
				.findController(pathInfoRepository.getControllerPathParts("/testauthor"));
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}

}
