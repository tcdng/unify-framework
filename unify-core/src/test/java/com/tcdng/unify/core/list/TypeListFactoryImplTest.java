/*
 * Copyright 2018-2024 The Code Department.
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

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.data.Listable;

/**
 * Default type list factory implementation tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TypeListFactoryImplTest extends AbstractUnifyComponentTest {

    @Test
    public void testGetTypeList() throws Exception {
        TypeListFactory typeListFactory = (TypeListFactory) getComponent(ApplicationComponents.APPLICATION_TYPELISTFACTORY);

        List<? extends Listable> list = typeListFactory.getTypeList(Locale.getDefault(), AComponent.class);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("a-component", list.get(0).getListKey());
        assertEquals("A Component", list.get(0).getListDescription());
        assertEquals("b-component", list.get(1).getListKey());
        assertEquals("B Component", list.get(1).getListDescription());

        list = typeListFactory.getTypeList(Locale.getDefault(), BComponent.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("b-component", list.get(0).getListKey());
        assertEquals("B Component", list.get(0).getListDescription());
    }
    
    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
