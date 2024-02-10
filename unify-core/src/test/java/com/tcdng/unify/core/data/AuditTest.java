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
package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.data.Audit.TrailItem;

/**
 * Audit tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class AuditTest extends AbstractUnifyComponentTest {

    @Test
    public void testBuilder() throws Exception {
        Audit.Builder ab = Audit.newBuilder();
        assertNotNull(ab);
        Audit audit = ab.build();
        assertNotNull(audit);
    }

    @Test
    public void testIsWithItem() throws Exception {
        Audit.Builder ab = Audit.newBuilder();
        assertNotNull(ab);
        Audit audit = ab.build();
        assertNotNull(audit);
        assertFalse(audit.isWithItem("name"));
        assertFalse(audit.isWithItem("age"));
        assertFalse(audit.isWithItem("gender"));       
        
        ab.addItem("name", "Sani", "Bello");
        ab.addItem("age", null, Integer.valueOf(32));
        
        audit = ab.build();
        assertNotNull(audit);
        assertTrue(audit.isWithItem("name"));
        assertTrue(audit.isWithItem("age"));
        assertFalse(audit.isWithItem("gender"));       
    }

    @Test
    public void testGetItemNames() throws Exception {
        Audit.Builder ab = Audit.newBuilder();
        assertNotNull(ab);
        Audit audit = ab.build();
        assertNotNull(audit);
        Set<String> names = audit.getItemNames();
        assertTrue(names.isEmpty());
       
        ab.addItem("name", "Sani", "Bello");
        ab.addItem("age", null, Integer.valueOf(32));
        
        audit = ab.build();
        assertNotNull(audit);
        names = audit.getItemNames();
        assertNotNull(names);
        assertEquals(2, names.size());       
        assertTrue(names.contains("name"));
        assertTrue(names.contains("age"));
        assertFalse(names.contains("gender"));       
    }

    @Test
    public void testTrialItem() throws Exception {
        Audit.Builder ab = Audit.newBuilder();
        assertNotNull(ab);
        Audit audit = ab.build();
        assertNotNull(audit);
        TrailItem trial = audit.getTrailItem("sam");
        assertNull(trial);
       
        ab.addItem("name", "Sani", "Bello");
        ab.addItem("age", null, Integer.valueOf(32));
        
        audit = ab.build();
        assertNotNull(audit);

        trial = audit.getTrailItem("name");
        assertNotNull(trial);
        assertEquals("name", trial.getFieldName());
        assertEquals("Sani", trial.getOldValue());
        assertEquals("Bello", trial.getNewValue());
        
        trial = audit.getTrailItem("age");
        assertNotNull(trial);
        assertEquals("age", trial.getFieldName());
        assertNull(trial.getOldValue());
        assertEquals(Integer.valueOf(32), trial.getNewValue());        
    }


    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
