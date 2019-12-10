/*
 * Copyright 2018-2019 The Code Department.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.remotecall.RemoteCallFormat;
import com.tcdng.unify.web.ui.Widget;

/**
 * Controller manager test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ControllerManagerTest extends AbstractUnifyWebTest {

    @Test
    public void testGetPageControllerInfo() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        PageControllerInfo pbbi = bbm.getPageControllerInfo("/testauthor");
        assertNotNull(pbbi);
    }

    @Test
    public void testPageControllerInfoActions() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        PageControllerInfo pbbi = bbm.getPageControllerInfo("/testauthor");

        Set<String> actionNames = pbbi.getActionNames();
        assertTrue(actionNames.contains("/createAuthor"));
        assertTrue(actionNames.contains("/viewAuthor"));
        assertTrue(actionNames.contains("/newAuthor"));

        Action action = pbbi.getAction("/createAuthor");
        assertNotNull(action);
        assertNotNull(action.getMethod());
        assertEquals(AuthorPageController.class, action.getMethod().getDeclaringClass());
        assertEquals("createAuthor", action.getMethod().getName());

        action = pbbi.getAction("/viewAuthor");
        assertNotNull(action);
        assertNotNull(action.getMethod());
        assertEquals(AuthorPageController.class, action.getMethod().getDeclaringClass());
        assertEquals("viewAuthor", action.getMethod().getName());

        action = pbbi.getAction("/newAuthor");
        assertNotNull(action);
        assertNotNull(action.getMethod());
        assertEquals(AuthorPageController.class, action.getMethod().getDeclaringClass());
        assertEquals("newAuthor", action.getMethod().getName());
    }

    @Test
    public void testPageControllerInfoActionsInheritance() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        PageControllerInfo pbbi = bbm.getPageControllerInfo("/testtechnicalauthor");

        Set<String> actionNames = pbbi.getActionNames();
        assertTrue(actionNames.contains("/createAuthor"));
        assertTrue(actionNames.contains("/viewAuthor"));
        assertTrue(actionNames.contains("/newAuthor"));
        assertTrue(actionNames.contains("/printTechnicalSpec"));
        assertFalse(actionNames.contains("/drawTechnicalSpec"));

        Action action = pbbi.getAction("/createAuthor");
        assertNotNull(action);
        assertNotNull(action.getMethod());
        assertEquals(AuthorPageController.class, action.getMethod().getDeclaringClass());
        assertEquals("createAuthor", action.getMethod().getName());

        action = pbbi.getAction("/viewAuthor");
        assertNotNull(action);
        assertNotNull(action.getMethod());
        assertEquals(TechnicalAuthorPageController.class, action.getMethod().getDeclaringClass());
        assertEquals("viewAuthor", action.getMethod().getName());

        action = pbbi.getAction("/newAuthor");
        assertNotNull(action);
        assertNotNull(action.getMethod());
        assertEquals(TechnicalAuthorPageController.class, action.getMethod().getDeclaringClass());
        assertEquals("newAuthor", action.getMethod().getName());

        action = pbbi.getAction("/printTechnicalSpec");
        assertNotNull(action);
        assertNotNull(action.getMethod());
        assertEquals(TechnicalAuthorPageController.class, action.getMethod().getDeclaringClass());
        assertEquals("printTechnicalSpec", action.getMethod().getName());
    }

    @Test(expected = UnifyException.class)
    public void testGetPageControllerInfoInvalid() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        bbm.getPageControllerInfo("/resource/mock");
    }

    @Test
    public void testGetResourceControllerInfo() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        ResourceControllerInfo rbbi = bbm.getResourceControllerInfo("/resource/mock");
        assertNotNull(rbbi);
    }

    @Test
    public void testResourceControllerInfoActions() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        ResourceControllerInfo rbbi = bbm.getResourceControllerInfo("/resource/mock");

        Set<String> pageNames = rbbi.getPropertyIds();
        assertNotNull(pageNames);
        assertEquals(4 + 2, pageNames.size());

        Set<String> properties = new HashSet<String>();
        for (String pageName : pageNames) {
            properties.add(rbbi.getPropertyInfo(pageName).getProperty());
        }
        assertEquals(4 + 2, properties.size());

        assertTrue(properties.contains("resourceName"));
        assertTrue(properties.contains("contentType"));
        assertTrue(properties.contains("attachment"));
        assertTrue(properties.contains("morsic"));
        assertTrue(properties.contains("accountNo"));
        assertTrue(properties.contains("balance"));
    }

    @Test(expected = UnifyException.class)
    public void testGetResourceControllerInfoInvalid() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        bbm.getResourceControllerInfo("/testauthor");
    }

    @Test
    public void testGetRemoteCallControllerInfo() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        RemoteCallControllerInfo rcbbi = bbm.getRemoteCallControllerInfo("/remotecall/mock");
        assertNotNull(rcbbi);
    }

    @Test
    public void testRemoteCallControllerInfoHandlers() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        RemoteCallControllerInfo rcbbiXml = bbm.getRemoteCallControllerInfo("/remotecall/mock");
        RemoteCallHandler handler = rcbbiXml.getRemoteCallHandler("/remotecall/mock/getAccountDetails");
        assertNotNull(handler);
        assertNotNull(handler.getMethod());
        assertEquals("mock-001", handler.getMethodCode());
        assertEquals(MockRemoteCallController.class, handler.getMethod().getDeclaringClass());
        assertEquals("getAccountDetails", handler.getMethod().getName());
    }

    @Test
    public void testGetController() throws Exception {
        Controller pbb = (Controller) getComponent("/testauthor");
        assertNotNull(pbb);

        Controller rbb = (Controller) getComponent("/resource/mock");
        assertNotNull(rbb);
    }

    @Test
    public void testGetPageControllerSameSession() throws Exception {
        Controller bb1 = (Controller) getComponent("/testauthor");
        Controller bb2 = (Controller) getComponent("/testauthor");
        assertSame(bb1, bb2);
        assertEquals(bb1, bb2);
    }

    @Test
    public void testPopulateControllerProperty() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        PathInfoRepository pathInfoRepository = (PathInfoRepository) getComponent(WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);
        
        // Create page controller and load page to request context
        bbm.getController(pathInfoRepository.getPathParts("/testauthor"), true);
        
        Date birthDt = new Date();
        bbm.populateControllerPageBean("/testauthor", "fullName", "Adrian Skim");
        bbm.populateControllerPageBean("/testauthor", "birthDt", birthDt);
        bbm.populateControllerPageBean("/testauthor", "height", Double.valueOf(25.34));

        AuthorPageController apbb = (AuthorPageController) getComponent("/testauthor");
        AuthorPageBean authorPageBean = (AuthorPageBean) apbb.getPage().getPageBean();
        assertEquals("Adrian Skim", authorPageBean.getFullName());
        assertEquals(birthDt, authorPageBean.getBirthDt());
        assertEquals(Double.valueOf(25.34), authorPageBean.getHeight());
    }

    @Test
    public void testExecuteXMLRemoteCallController() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);

        TestClientRequest request = new TestClientRequest("/remotecall/mock/getAccountDetails");
        String reqXml = "<accountDetailParams>" + "<accountNo>0123456785</accountNo>" + "</accountDetailParams>";
        request.setParameter(RequestParameterConstants.REMOTE_CALL_FORMAT, RemoteCallFormat.XML);
        request.setParameter(RequestParameterConstants.REMOTE_CALL_BODY, reqXml);

        TestClientResponse response = new TestClientResponse();
        bbm.executeController(request, response);

        String expRespXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<accountDetailResult>"
                + "<accountNo>0123456785</accountNo>" + "<accountName>Edward Banfa</accountName>"
                + "<balance>250000.0</balance>" + "</accountDetailResult>";
        assertEquals(expRespXml, response.toString());
    }

    @Test
    public void testExecuteJSONRemoteCallController() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);

        TestClientResponse response = new TestClientResponse();
        TestClientRequest request = new TestClientRequest("/remotecall/mock/getAccountDetails");
        String reqJson = "{" + "\"accountNo\":\"0123456785\"" + "}";
        request.setParameter(RequestParameterConstants.REMOTE_CALL_FORMAT, RemoteCallFormat.JSON);
        request.setParameter(RequestParameterConstants.REMOTE_CALL_BODY, reqJson);

        bbm.executeController(request, response);
    }

    @Test
    public void testExecutePageController() throws Exception {
        ControllerManager bbm =
                (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
        PathInfoRepository pathInfoRepository = (PathInfoRepository) getComponent(WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);
        
        TestClientResponse response = new TestClientResponse();
        TestClientRequest request = new TestClientRequest("/testauthor/createAuthor");
        Date birthDt = new Date();
        AuthorPageController apbb = (AuthorPageController) bbm.getController(pathInfoRepository.getPathParts("/testauthor"), true);
        Widget uic1 = apbb.getPageWidgetByLongName(Widget.class, "/testauthor.fullName");
        Widget uic2 = apbb.getPageWidgetByLongName(Widget.class, "/testauthor.birthDt");
        Widget uic3 = apbb.getPageWidgetByLongName(Widget.class, "/testauthor.height");
        request.setParameter(uic1.getId(), "Tom Jones");
        request.setParameter(uic2.getId(), birthDt);
        request.setParameter(uic3.getId(), "24.22");

        bbm.executeController(request, response);

        // Ensure controller is populated
        AuthorPageBean authorPageBean = (AuthorPageBean) apbb.getPage().getPageBean();
        assertEquals("Tom Jones", authorPageBean.getFullName());
        assertEquals(birthDt, authorPageBean.getBirthDt());
        assertEquals(Double.valueOf(24.22), authorPageBean.getHeight());

        // Test result
        assertEquals("{\"jsonResp\":[{\"handler\":\"hintUserHdl\"},{\"handler\":\"refreshMenuHdl\"}]}",
                response.toString());
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
