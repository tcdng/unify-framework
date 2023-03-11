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
package com.tcdng.unify.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Path information repository test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PathInfoRepositoryTest extends AbstractUnifyWebTest {

    private PathInfoRepository pir;

    @Test
    public void testGetPathPartsNoAction() throws Exception {
        ControllerPathParts controllerPathParts = pir.getControllerPathParts("/testauthor");
        assertNotNull(controllerPathParts);

        assertEquals("/testauthor", controllerPathParts.getControllerPath());
        assertEquals("/testauthor", controllerPathParts.getControllerPathId());
        assertEquals("/testauthor", controllerPathParts.getControllerName());
        assertNull(controllerPathParts.getPathVariable());
        assertNull(controllerPathParts.getActionName());
        assertFalse(controllerPathParts.isSessionless());
        assertFalse(controllerPathParts.isActionPath());
        assertFalse(controllerPathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsWithAction() throws Exception {
        ControllerPathParts controllerPathParts = pir.getControllerPathParts("/testauthor/createAuthor");
        assertNotNull(controllerPathParts);

        assertEquals("/testauthor/createAuthor", controllerPathParts.getControllerPath());
        assertEquals("/testauthor", controllerPathParts.getControllerPathId());
        assertEquals("/testauthor", controllerPathParts.getControllerName());
        assertNull(controllerPathParts.getPathVariable());
        assertEquals("/createAuthor", controllerPathParts.getActionName());
        assertFalse(controllerPathParts.isSessionless());
        assertTrue(controllerPathParts.isActionPath());
        assertFalse(controllerPathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsVariablePathNoAction() throws Exception {
        ControllerPathParts controllerPathParts = pir.getControllerPathParts("/testauthor:20");
        assertNotNull(controllerPathParts);

        assertEquals("/testauthor:20", controllerPathParts.getControllerPath());
        assertEquals("/testauthor:20", controllerPathParts.getControllerPathId());
        assertEquals("/testauthor", controllerPathParts.getControllerName());
        assertEquals("20", controllerPathParts.getPathVariable());
        assertNull(controllerPathParts.getActionName());
        assertFalse(controllerPathParts.isSessionless());
        assertFalse(controllerPathParts.isActionPath());
        assertTrue(controllerPathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsVariablePathWithAction() throws Exception {
        ControllerPathParts controllerPathParts = pir.getControllerPathParts("/testauthor:35/createAuthor");
        assertNotNull(controllerPathParts);

        assertEquals("/testauthor:35/createAuthor", controllerPathParts.getControllerPath());
        assertEquals("/testauthor:35", controllerPathParts.getControllerPathId());
        assertEquals("/testauthor", controllerPathParts.getControllerName());
        assertEquals("35", controllerPathParts.getPathVariable());
        assertEquals("/createAuthor", controllerPathParts.getActionName());
        assertFalse(controllerPathParts.isSessionless());
        assertTrue(controllerPathParts.isActionPath());
        assertTrue(controllerPathParts.isVariablePath());
    }

    @Test
    public void testGetPathPartsReuse() throws Exception {
        ControllerPathParts pathParts1 = pir.getControllerPathParts("/testauthor:35/createAuthor");
        ControllerPathParts pathParts2 = pir.getControllerPathParts("/testauthor:35/createAuthor");
        assertNotNull(pathParts1);
        assertNotNull(pathParts2);
        assertTrue(pathParts1 == pathParts2);

        ControllerPathParts pathParts3 = pir.getControllerPathParts("/testauthor/createAuthor");
        ControllerPathParts pathParts4 = pir.getControllerPathParts("/testauthor/createAuthor");
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
