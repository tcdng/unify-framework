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
package com.tcdng.unify.core.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.data.Listable;

/**
 * Default list manager implementation tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ListManagerImplTest extends AbstractUnifyComponentTest {

    @Test
    public void testGetStaticList() throws Exception {
        ListManager listManager = (ListManager) getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);

        List<? extends Listable> list = listManager.getList(Locale.GERMAN, "colorlist");
        assertEquals(3, list.size());
        assertEquals("red", list.get(0).getListKey());
        assertEquals("blue", list.get(1).getListKey());
        assertEquals("purple", list.get(2).getListKey());
        assertEquals("Rot", list.get(0).getListDescription());
        assertEquals("Blau", list.get(1).getListDescription());
        assertEquals("Lila", list.get(2).getListDescription());

        list = listManager.getList(Locale.ENGLISH, "colorlist");

        assertEquals(3, list.size());
        assertEquals("red", list.get(0).getListKey());
        assertEquals("blue", list.get(1).getListKey());
        assertEquals("purple", list.get(2).getListKey());
        assertEquals("Red", list.get(0).getListDescription());
        assertEquals("Blue", list.get(1).getListDescription());
        assertEquals("Purple", list.get(2).getListDescription());
    }

    @Test
    public void testGetStaticListList() throws Exception {
        ListManager listManager = (ListManager) getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);

        List<? extends Listable> list = listManager.getList(Locale.ENGLISH, "staticlistlist");
        assertNotNull(list);
        assertTrue(!list.isEmpty());
    }

    @Test
    public void testGetStaticListSublistWithFilter() throws Exception {
        ListManager listManager = (ListManager) getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);

        List<? extends Listable> list = listManager.getSubList(Locale.ENGLISH, "staticlistlist", "F", -1);
        assertNotNull(list);
        assertTrue(!list.isEmpty());
    }

    @Test
    public void testGetStaticListSublistWithLimit() throws Exception {
        ListManager listManager = (ListManager) getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);

        List<? extends Listable> list = listManager.getSubList(Locale.ENGLISH, "staticlistlist", null, 4);
        assertNotNull(list);
        assertEquals(4, list.size());
    }

    @Test
    public void testGetStaticListSublistWithFilterAndLimit() throws Exception {
        ListManager listManager = (ListManager) getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);

        List<? extends Listable> list = listManager.getSubList(Locale.ENGLISH, "staticlistlist", "F", 3);
        assertNotNull(list);
        assertEquals(3, list.size());
    }

    @Test
    public void testGetListItemByKey() throws Exception {
        ListManager listManager = (ListManager) getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);
        Listable listable = listManager.getListItemByKey(Locale.ENGLISH, "colorlist", "red");
        assertNotNull(listable);
        assertEquals("red", listable.getListKey());
        assertEquals("Red", listable.getListDescription());

        listable = listManager.getListItemByKey(Locale.ENGLISH, "colorlist", "purple");
        assertNotNull(listable);
        assertEquals("purple", listable.getListKey());
        assertEquals("Purple", listable.getListDescription());
    }

    @Test
    public void testGetListItemByDescription() throws Exception {
        ListManager listManager = (ListManager) getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);
        Listable listable = listManager.getListItemByDescription(Locale.ENGLISH, "colorlist", "Red");
        assertNotNull(listable);
        assertEquals("red", listable.getListKey());
        assertEquals("Red", listable.getListDescription());

        listable = listManager.getListItemByDescription(Locale.ENGLISH, "colorlist", "Purple");
        assertNotNull(listable);
        assertEquals("purple", listable.getListKey());
        assertEquals("Purple", listable.getListDescription());

        listable = listManager.getListItemByDescription(Locale.ENGLISH, "colorlist", "Brown");
        assertNull(listable);
    }
    
    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
