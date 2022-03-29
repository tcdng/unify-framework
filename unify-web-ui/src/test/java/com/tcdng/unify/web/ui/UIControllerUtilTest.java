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
package com.tcdng.unify.web.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.AbstractUnifyWebTest;
import com.tcdng.unify.web.Action;

/**
 * UI Controller utilities tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UIControllerUtilTest extends AbstractUnifyWebTest {

	@Test
	public void testGetPageControllerInfo() throws Exception {
		UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		PageControllerInfo pbbi = uicu.getPageControllerInfo("/testauthor");
		assertNotNull(pbbi);
	}

	@Test
	public void testPageControllerInfoActions() throws Exception {
	    UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		PageControllerInfo pbbi = uicu.getPageControllerInfo("/testauthor");

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
	    UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		PageControllerInfo pbbi = uicu.getPageControllerInfo("/testtechnicalauthor");

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
	    UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		uicu.getPageControllerInfo("/resource/mock");
	}

	@Test
	public void testGetResourceControllerInfo() throws Exception {
	    UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		ResourceControllerInfo rbbi = uicu.getResourceControllerInfo("/resource/mock");
		assertNotNull(rbbi);
	}

	@Test
	public void testResourceControllerInfoActions() throws Exception {
	    UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		ResourceControllerInfo rbbi = uicu.getResourceControllerInfo("/resource/mock");

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
	    UIControllerUtil uicu = (UIControllerUtil) getComponent(
				WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL);
		uicu.getResourceControllerInfo("/testauthor");
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}

}
