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
package com.tcdng.unify.core.upl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.constant.UserPlatform;
import com.tcdng.unify.core.upl.artifacts.TestDocumentA;
import com.tcdng.unify.core.upl.artifacts.TestDocumentAWriter;
import com.tcdng.unify.core.upl.artifacts.TestDocumentB;
import com.tcdng.unify.core.upl.artifacts.TestDocumentC;
import com.tcdng.unify.core.upl.artifacts.TestDocumentCWriter;
import com.tcdng.unify.core.upl.artifacts.TestDocumentD;
import com.tcdng.unify.core.upl.artifacts.TestDocumentE;
import com.tcdng.unify.core.upl.artifacts.TestDocumentWriter1;
import com.tcdng.unify.core.upl.artifacts.TestDocumentWriter2;

/**
 * UPL component writer manager tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UplComponentWriterManagerTest extends AbstractUnifyComponentTest {

    private UplComponentWriterManager ucwManager;

    @Test
    public void testGetDefaultPlatformWriters() throws Exception {
        Map<Class<? extends UplComponent>, UplComponentWriter> writers = ucwManager.getWriters(UserPlatform.DEFAULT);
        assertNotNull(writers);
        assertTrue(writers.containsKey(TestDocumentA.class));
        assertTrue(writers.containsKey(TestDocumentB.class));
        assertTrue(writers.containsKey(TestDocumentC.class));
        assertTrue(writers.containsKey(TestDocumentD.class));
        assertTrue(writers.containsKey(TestDocumentE.class));

        UplComponentWriter writer = writers.get(TestDocumentA.class);
        assertNotNull(writer);
        assertEquals(TestDocumentAWriter.class, writer.getClass());

        writer = writers.get(TestDocumentB.class);
        assertNotNull(writer);
        assertEquals(TestDocumentAWriter.class, writer.getClass());

        writer = writers.get(TestDocumentC.class);
        assertNotNull(writer);
        assertEquals(TestDocumentAWriter.class, writer.getClass());

        writer = writers.get(TestDocumentD.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter1.class, writer.getClass());

        writer = writers.get(TestDocumentE.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter1.class, writer.getClass());
    }

    @Test
    public void testGetKnownPlatformWritersFull() throws Exception {
        Map<Class<? extends UplComponent>, UplComponentWriter> writers = ucwManager.getWriters(UserPlatform.MOBILE);
        assertNotNull(writers);
        assertTrue(writers.containsKey(TestDocumentA.class));
        assertTrue(writers.containsKey(TestDocumentB.class));
        assertTrue(writers.containsKey(TestDocumentC.class));
        assertTrue(writers.containsKey(TestDocumentD.class));
        assertTrue(writers.containsKey(TestDocumentE.class));

        UplComponentWriter writer = writers.get(TestDocumentA.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter2.class, writer.getClass());

        writer = writers.get(TestDocumentB.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter2.class, writer.getClass());

        writer = writers.get(TestDocumentC.class);
        assertNotNull(writer);
        assertEquals(TestDocumentCWriter.class, writer.getClass());

        writer = writers.get(TestDocumentD.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter2.class, writer.getClass());

        writer = writers.get(TestDocumentE.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter2.class, writer.getClass());
    }

    @Test
    public void testGetKnownPlatformWritersPartial() throws Exception {
        Map<Class<? extends UplComponent>, UplComponentWriter> writers = ucwManager.getWriters(UserPlatform.DEFAULT);
        assertNotNull(writers);
        assertTrue(writers.containsKey(TestDocumentA.class));
        assertTrue(writers.containsKey(TestDocumentB.class));
        assertTrue(writers.containsKey(TestDocumentC.class));
        assertTrue(writers.containsKey(TestDocumentD.class));
        assertTrue(writers.containsKey(TestDocumentE.class));

        UplComponentWriter writer = writers.get(TestDocumentA.class);
        assertNotNull(writer);
        assertEquals(TestDocumentAWriter.class, writer.getClass());

        writer = writers.get(TestDocumentB.class);
        assertNotNull(writer);
        assertEquals(TestDocumentAWriter.class, writer.getClass());

        writer = writers.get(TestDocumentC.class);
        assertNotNull(writer);
        assertEquals(TestDocumentAWriter.class, writer.getClass());

        writer = writers.get(TestDocumentD.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter1.class, writer.getClass());

        writer = writers.get(TestDocumentE.class);
        assertNotNull(writer);
        assertEquals(TestDocumentWriter1.class, writer.getClass());
    }

    @Test
    public void testGetUnknownPlatformWriters() throws Exception {
        Map<Class<? extends UplComponent>, UplComponentWriter> writers = ucwManager.getWriters(UserPlatform.DEFAULT);
        Map<Class<? extends UplComponent>, UplComponentWriter> defaultWriters =
                ucwManager.getWriters(UserPlatform.DEFAULT);
        assertSame(defaultWriters, writers);
    }

    @Override
    protected void onSetup() throws Exception {
        ucwManager =
                (UplComponentWriterManager) getComponent(ApplicationComponents.APPLICATION_UPLCOMPONENTWRITERMANAGER);
    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
