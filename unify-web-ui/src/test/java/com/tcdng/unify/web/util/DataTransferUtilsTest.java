/*
 * Copyright 2018-2025 The Code Department.
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

package com.tcdng.unify.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.DataTransferHeader;
import com.tcdng.unify.web.ui.util.DataTransferUtils;

/**
 * Data transfer utilities tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DataTransferUtilsTest {

    DataTransferHeader header = new DataTransferHeader(null);

    @Test
    public void testCreateTransferBlock() throws Exception {
        DataTransferBlock dtb = DataTransferUtils.createTransferBlock("p7.c0d0.c1", new DataTransferHeader("SHIRTS"));
        assertNotNull(dtb);
        assertEquals("SHIRTS", dtb.getValue());
        assertEquals("p7", dtb.getId());
        assertEquals(-1, dtb.getItemIndex());
        
        dtb = dtb.getChildBlock();
        assertNotNull(dtb);
        assertEquals("SHIRTS", dtb.getValue());
        assertEquals("p7.c0", dtb.getId());
        assertEquals(0, dtb.getItemIndex());        
        
        dtb = dtb.getChildBlock();
        assertNotNull(dtb);
        assertEquals("SHIRTS", dtb.getValue());
        assertEquals("p7.c0d0.c1", dtb.getId());
        assertEquals(-1, dtb.getItemIndex());        
    }
    
    @Test
    public void testStripTransferDataIndexPart() throws Exception {
        String s = DataTransferUtils.stripTransferDataIndexPart("p10029345");
        assertEquals("p10029345", s);
        
        s = DataTransferUtils.stripTransferDataIndexPart("p100293d0");
        assertEquals("p100293", s);
        
        s = DataTransferUtils.stripTransferDataIndexPart("p1002955d023");
        assertEquals("p1002955", s);      
    }
}
