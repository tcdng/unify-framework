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
package com.tcdng.unify.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TargetPath information repository test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PathInfoRepositoryTest extends AbstractUnifyWebTest {

    private PathInfoRepository pir;

    @Test
    public void testGetPagePathInfo() throws Exception {
        PagePathInfo pagePathInfo = pir.getPagePathInfo("/testauthor");
        assertNotNull(pagePathInfo);
        assertEquals("/testauthor", pagePathInfo.getPathId());
        assertNull(pagePathInfo.getColorScheme());
        assertEquals("/testauthor/openPage", pagePathInfo.getOpenPagePath());
        assertEquals("/testauthor/savePage", pagePathInfo.getSavePagePath());
        assertEquals("/testauthor/closePage", pagePathInfo.getClosePagePath());
        assertFalse(pagePathInfo.isRemoteSave());
    }

    @Test
    public void testGetPathPartsNoAction() throws Exception {
        PathParts pathParts = pir.getPathParts("/testauthor");
        assertNotNull(pathParts);
        assertEquals("/testauthor", pathParts.getFullPath());
        assertEquals("/testauthor", pathParts.getPathId());
        assertEquals("/testauthor", pathParts.getControllerName());
        assertNull(pathParts.getPathVariable());
        assertNull(pathParts.getActionName());
        assertTrue(pathParts.isUiController());
        assertFalse(pathParts.isActionPath());
        assertFalse(pathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsWithAction() throws Exception {
        PathParts pathParts = pir.getPathParts("/testauthor/createAuthor");
        assertNotNull(pathParts);
        assertEquals("/testauthor/createAuthor", pathParts.getFullPath());
        assertEquals("/testauthor", pathParts.getPathId());
        assertEquals("/testauthor", pathParts.getControllerName());
        assertNull(pathParts.getPathVariable());
        assertEquals("/createAuthor", pathParts.getActionName());
        assertTrue(pathParts.isUiController());
        assertTrue(pathParts.isActionPath());
        assertFalse(pathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsVariablePathNoAction() throws Exception {
        PathParts pathParts = pir.getPathParts("/testauthor:20");
        assertNotNull(pathParts);
        assertEquals("/testauthor:20", pathParts.getFullPath());
        assertEquals("/testauthor:20", pathParts.getPathId());
        assertEquals("/testauthor", pathParts.getControllerName());
        assertEquals("20", pathParts.getPathVariable());
        assertNull(pathParts.getActionName());
        assertTrue(pathParts.isUiController());
        assertFalse(pathParts.isActionPath());
        assertTrue(pathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsVariablePathWithAction() throws Exception {
        PathParts pathParts = pir.getPathParts("/testauthor:35/createAuthor");
        assertNotNull(pathParts);
        assertEquals("/testauthor:35/createAuthor", pathParts.getFullPath());
        assertEquals("/testauthor:35", pathParts.getPathId());
        assertEquals("/testauthor", pathParts.getControllerName());
        assertEquals("35", pathParts.getPathVariable());
        assertEquals("/createAuthor", pathParts.getActionName());
        assertTrue(pathParts.isUiController());
        assertTrue(pathParts.isActionPath());
        assertTrue(pathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsReuse() throws Exception {
        PathParts pathParts1 = pir.getPathParts("/testauthor:35/createAuthor");
        PathParts pathParts2 = pir.getPathParts("/testauthor:35/createAuthor");
        assertNotNull(pathParts1);
        assertNotNull(pathParts2);
        assertFalse(pathParts1 == pathParts2);

        PathParts pathParts3 = pir.getPathParts("/testauthor/createAuthor");
        PathParts pathParts4 = pir.getPathParts("/testauthor/createAuthor");
        assertNotNull(pathParts3);
        assertNotNull(pathParts4);
        assertTrue(pathParts3 == pathParts4);
    }

    @Override
    protected void onSetup() throws Exception {
        pir = (PathInfoRepository) getComponent(WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY);
    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
