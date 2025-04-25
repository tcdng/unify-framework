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

package com.tcdng.unify.core.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.system.entities.SingleVersionBlob;
import com.tcdng.unify.core.system.entities.SingleVersionClob;

/**
 * Single version large object service test case.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SingleVersionLargeObjectServiceTest extends AbstractUnifyComponentTest {

    @Test(expected = UnifyException.class)
    public void testStoreBlobNullBlob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", null, 1);
    }

    @Test(expected = UnifyException.class)
    public void testStoreBlobZeroVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 0);
    }

    @Test(expected = UnifyException.class)
    public void testStoreBlobLessThanZeroVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, -1);
    }

    @Test
    public void testStoreBlob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        boolean stored = svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 1);
        assertTrue(stored);
    }

    @Test
    public void testStoreBlobMultiple() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        boolean stored =
                svlos.storeBlob("testApp", "category1", "userImg1", new byte[] { (byte) 0xca, (byte) 0xfe }, 1);
        assertTrue(stored);
        stored = svlos.storeBlob("testApp", "category1", "userImg2", new byte[] { (byte) 0xba, (byte) 0xbe }, 1);
        assertTrue(stored);
    }

    @Test
    public void testStoreBlobNewVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 1);
        boolean stored = svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xba, (byte) 0xbe }, 2);
        assertTrue(stored);
    }

    @Test
    public void testStoreBlobOldVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 2);
        boolean stored = svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xba, (byte) 0xbe }, 1);
        assertFalse(stored);
        stored = svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xba, (byte) 0xbe }, 2);
        assertFalse(stored);
    }

    @Test
    public void testGetBlobVersionUnknownBob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        long version = svlos.getBlobVersion("testApp", "category1", "userImg");
        assertEquals(0, version);
    }

    @Test
    public void testGetBlobVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 2);
        long version = svlos.getBlobVersion("testApp", "category1", "userImg");
        assertEquals(2, version);
    }

    @Test
    public void testGetBlobNewVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 2);
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xba, (byte) 0xbe }, 4);
        long version = svlos.getBlobVersion("testApp", "category1", "userImg");
        assertEquals(4, version);
    }

    @Test
    public void testRetreiveBlobNotExist() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        byte[] blob = svlos.retrieveBlob("testApp", "category1", "userImg");
        assertNull(blob);
    }

    @Test
    public void testRetreiveBlob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 2);
        byte[] blob = svlos.retrieveBlob("testApp", "category1", "userImg");
        assertNotNull(blob);
        assertEquals(2, blob.length);
        assertEquals((byte) 0xca, blob[0]);
        assertEquals((byte) 0xfe, blob[1]);
    }

    @Test
    public void testRetreiveBlobNewVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xca, (byte) 0xfe }, 1);
        svlos.storeBlob("testApp", "category1", "userImg", new byte[] { (byte) 0xba, (byte) 0xbe }, 2);
        byte[] blob = svlos.retrieveBlob("testApp", "category1", "userImg");
        assertNotNull(blob);
        assertEquals(2, blob.length);
        assertEquals((byte) 0xba, blob[0]);
        assertEquals((byte) 0xbe, blob[1]);
    }

    @Test
    public void testRetreiveBlobMultiple() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg1", new byte[] { (byte) 0xca, (byte) 0xfe }, 1);
        svlos.storeBlob("testApp", "category1", "userImg2", new byte[] { (byte) 0xba, (byte) 0xbe }, 1);

        byte[] blob = svlos.retrieveBlob("testApp", "category1", "userImg1");
        assertNotNull(blob);
        assertEquals(2, blob.length);
        assertEquals((byte) 0xca, blob[0]);
        assertEquals((byte) 0xfe, blob[1]);

        blob = svlos.retrieveBlob("testApp", "category1", "userImg2");
        assertNotNull(blob);
        assertEquals(2, blob.length);
        assertEquals((byte) 0xba, blob[0]);
        assertEquals((byte) 0xbe, blob[1]);
    }

    @Test
    public void testRetreiveBlobObjectNames() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeBlob("testApp", "category1", "userImg1", new byte[] { (byte) 0xca, (byte) 0xfe }, 1);
        svlos.storeBlob("testApp", "category1", "userImg2", new byte[] { (byte) 0xba, (byte) 0xbe }, 1);

        List<String> objectNames = svlos.retrieveBlobObjectNames("testApp", "category1");
        assertNotNull(objectNames);
        assertEquals(2, objectNames.size());
        Collections.sort(objectNames);
        assertEquals("userImg1", objectNames.get(0));
        assertEquals("userImg2", objectNames.get(1));
    }

    @Test(expected = UnifyException.class)
    public void testStoreClobNullClob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", null, 1);
    }

    @Test(expected = UnifyException.class)
    public void testStoreClobZeroVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", 0);
    }

    @Test(expected = UnifyException.class)
    public void testStoreClobLessThanZeroVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", -1);
    }

    @Test
    public void testStoreClob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        boolean stored = svlos.storeClob("testApp", "category1", "userImg", "cafe", 1);
        assertTrue(stored);
    }

    @Test
    public void testStoreClobMultiple() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        boolean stored = svlos.storeClob("testApp", "category1", "userImg1", "cafe", 1);
        assertTrue(stored);
        stored = svlos.storeClob("testApp", "category1", "userImg2", "babe", 1);
        assertTrue(stored);
    }

    @Test
    public void testStoreClobNewVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", 1);
        boolean stored = svlos.storeClob("testApp", "category1", "userImg", "babe", 2);
        assertTrue(stored);
    }

    @Test
    public void testStoreClobOldVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", 2);
        boolean stored = svlos.storeClob("testApp", "category1", "userImg", "babe", 1);
        assertFalse(stored);
        stored = svlos.storeClob("testApp", "category1", "userImg", "babe", 2);
        assertFalse(stored);
    }

    @Test
    public void testRetreiveClobNotExist() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        String clob = svlos.retrieveClob("testApp", "category1", "userImg");
        assertNull(clob);
    }

    @Test
    public void testRetreiveClob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", 2);
        String clob = svlos.retrieveClob("testApp", "category1", "userImg");
        assertNotNull(clob);
        assertEquals("cafe", clob);
    }

    @Test
    public void testRetreiveClobNewVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", 1);
        svlos.storeClob("testApp", "category1", "userImg", "babe", 2);
        String clob = svlos.retrieveClob("testApp", "category1", "userImg");
        assertNotNull(clob);
        assertEquals("babe", clob);
    }

    @Test
    public void testRetreiveClobMultiple() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg1", "cafe", 1);
        svlos.storeClob("testApp", "category1", "userImg2", "babe", 1);

        String clob = svlos.retrieveClob("testApp", "category1", "userImg1");
        assertNotNull(clob);
        assertEquals("cafe", clob);

        clob = svlos.retrieveClob("testApp", "category1", "userImg2");
        assertNotNull(clob);
        assertEquals("babe", clob);
    }

    @Test
    public void testRetreiveClobObjectNames() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg1", "cafe", 1);
        svlos.storeClob("testApp", "category1", "userImg2", "babe", 1);

        List<String> objectNames = svlos.retrieveClobObjectNames("testApp", "category1");
        assertNotNull(objectNames);
        assertEquals(2, objectNames.size());
        Collections.sort(objectNames);
        assertEquals("userImg1", objectNames.get(0));
        assertEquals("userImg2", objectNames.get(1));
    }

    @Test
    public void testGetClobVersionUnknownBob() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        long version = svlos.getClobVersion("testApp", "category1", "userImg");
        assertEquals(0, version);
    }

    @Test
    public void testGetClobVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", 2);
        long version = svlos.getClobVersion("testApp", "category1", "userImg");
        assertEquals(2, version);
    }

    @Test
    public void testGetClobNewVersion() throws Exception {
        SingleVersionLargeObjectService svlos = getSingleVersionLargeObjectService();
        svlos.storeClob("testApp", "category1", "userImg", "cafe", 2);
        svlos.storeClob("testApp", "category1", "userImg", "babe", 4);
        long version = svlos.getClobVersion("testApp", "category1", "userImg");
        assertEquals(4, version);
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(SingleVersionBlob.class, SingleVersionClob.class);
    }

    private SingleVersionLargeObjectService getSingleVersionLargeObjectService() throws Exception {
        return (SingleVersionLargeObjectService) getComponent(
                ApplicationComponents.APPLICATION_SINGLEVERSIONLOBSERVICE);
    }
}
